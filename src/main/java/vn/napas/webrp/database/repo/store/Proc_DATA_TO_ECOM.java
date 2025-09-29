package vn.napas.webrp.database.repo.store;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.report.util.SqlLogUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Port of Oracle SP: WEBBC.DATA_TO_ECOM(pQRY_FROM_DATE, pQRY_TO_DATE)
 * to a pure-Java repository for TiDB/MySQL.
 *
 * Behavior parity:
 *  - DELETE from QR_ECOM_SUCC where SWITCH_SETTLE_DATE = (TO_DATE(pQRY_TO_DATE) + 1)
 *  - INSERT rows selected from SHCLOG_SETT_IBFT filtered by dates and response rules
 *  - SWITCH_SETTLE_DATE on inserted rows = CURDATE (server date)
 *  - SWITCH_STATUS formatting same as PL/SQL (2 digits for 0,1; otherwise 3 digits)
 *  - Error path writes to ERR_EX (no DB SMS call here; integrate with external notifier if needed)
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class Proc_DATA_TO_ECOM {

    private final NamedParameterJdbcTemplate jdbc;

    private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Run export using string input dates in dd/MM/yyyy.
     */
    @Transactional
    public int run(String pQRY_FROM_DATE, String pQRY_TO_DATE) {
        LocalDate d1 = LocalDate.parse(pQRY_FROM_DATE, DDMMYYYY);
        LocalDate d2 = LocalDate.parse(pQRY_TO_DATE, DDMMYYYY);
        return run(d1, d2);
    }

    /**
     * Run export using LocalDate params.
     */
    @Transactional
    public int run(LocalDate fromDate, LocalDate toDate) {
        // 1) Delete previous switch-day slice (d2 + 1)
        LocalDate switchDateForDelete = toDate.plusDays(1);
        int del = deleteBySwitchSettleDate(switchDateForDelete);

        // 2) Insert current data slice
        int ins = insertFromShclogIntoEcom(fromDate, toDate, LocalDate.now(ZoneId.systemDefault()));

        log.info("[DATA_TO_ECOM] Deleted {} rows (switch_date={}), Inserted {} rows (from {} to {}, switch_date={})",
                del, switchDateForDelete, ins, fromDate, toDate, LocalDate.now());
        return ins;
    }

    private int deleteBySwitchSettleDate(LocalDate switchDate) {
    	try {
			log.info("deleteBySwitchSettleDate");
		    String sql = "DELETE FROM QR_ECOM_SUCC WHERE SWITCH_SETTLE_DATE = :d";
	        return jdbc.update(sql, new MapSqlParameterSource("d", java.sql.Date.valueOf(switchDate)));
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
			return 0;
		} finally {

		}

    
    }

    private int insertFromShclogIntoEcom(LocalDate d1, LocalDate d2, LocalDate switchDateForInsert) {
        String sql = """
            INSERT INTO QR_ECOM_SUCC (
                AMOUNT,
                CARD_NUMBER_DETAIL,
                AUTHORISATION_CODE,
                MERCHANT_ID,
                F11_TRACE,
                F12_LOCAL_TIME,
                F13_LOCAL_DATE,
                F15_SETTLE_DATE,
                F32_ACQUIER,
                F41_CARD_ACCEPTOR_ID,
                F63_TRANS_SWITCH,
                F100_BEN,
                SWITCH_SETTLE_DATE,
                SWITCH_STATUS
            )
            SELECT
                t.AMOUNT,
                TRIM(t.PAN) AS CARD_NUMBER_DETAIL,
                t.AUTHNUM   AS AUTHORISATION_CODE,
                TRIM(
                    CASE
                        WHEN LOCATE('|', t.ACCTNUM) > 0 THEN SUBSTRING(t.ACCTNUM, LOCATE('|', t.ACCTNUM) + 1)
                        ELSE t.ACCTNUM
                    END
                )      AS MERCHANT_ID,
                t.ORIGTRACE  AS F11_TRACE,
                t.LOCAL_TIME AS F12_LOCAL_TIME,
                DATE_FORMAT(t.LOCAL_DATE, '%m%d')                         AS F13_LOCAL_DATE,
                IFNULL(DATE_FORMAT(t.SETTLEMENT_DATE, '%m%d'), '0101')    AS F15_SETTLE_DATE,
                t.ACQUIRER    AS F32_ACQUIER,
                TRIM(t.TERMID) AS F41_CARD_ACCEPTOR_ID,
                TRIM(t.ADDRESPONSE) AS F63_TRANS_SWITCH,
                t.BB_BIN       AS F100_BEN,
                :switchDate    AS SWITCH_SETTLE_DATE,
                CASE
                    WHEN t.RESPCODE IN (0,1) THEN LPAD(CAST(t.RESPCODE AS CHAR), 2, '0')
                    ELSE LPAD(CAST(t.RESPCODE AS CHAR), 3, '0')
                END AS SWITCH_STATUS
            FROM SHCLOG_SETT_IBFT t
            WHERE t.MSGTYPE = 210
              AND t.SETTLEMENT_DATE >= :d1
              AND t.SETTLEMENT_DATE <  DATE_ADD(:d2, INTERVAL 1 DAY)
              AND (
                    (t.RESPCODE = 0 AND t.FEE_NOTE IS NOT NULL)
                 OR (t.RESPCODE = 1 AND t.ORIGRESPCODE = 68)
                 OR (t.RESPCODE = 1 AND t.ORIGRESPCODE = 0)
                 OR (t.RESPCODE = 1 AND t.reason_edit LIKE '%(QRIBFTECOM)%')
              )
              AND t.BB_BIN IN (971100, 971111)
        """;
        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("d1", java.sql.Timestamp.valueOf(d1.atStartOfDay()))
                .addValue("d2", java.sql.Timestamp.valueOf(d2.atStartOfDay()))
                .addValue("switchDate", java.sql.Date.valueOf(switchDateForInsert));
        try {
        	log.info("insertFromShclogIntoEcom: {}", SqlLogUtils.renderSql(sql, ps.getValues()));
            return jdbc.update(sql, ps);
        } catch (DataAccessException ex) {
            writeError("Tao du lieu cho View ECOM failed: " + ex.getMessage());
            throw ex;
        }
    }

    private void writeError(String detail) {
        String err = """
            INSERT INTO ERR_EX (ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
            VALUES (NOW(), -1, :d, 'SHC_TMP')
        """;
        jdbc.update(err, new MapSqlParameterSource("d", detail));
        log.error("[DATA_TO_ECOM] {}", detail);
    }
}
