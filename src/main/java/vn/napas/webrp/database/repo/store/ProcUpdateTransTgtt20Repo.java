package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProcUpdateTransTgtt20Repo {
	private final NamedParameterJdbcTemplate jdbc;
	private final DbLoggerRepository dblog;
	private final String module = "UPDATE_TRANS_TGTT_20";

//    @Override
	@Transactional
	public void execute(int iDays) {
		LocalDate processDate = LocalDate.now().minusDays(iDays);
		Map<String, Object> params = Map.of("processDate", Date.valueOf(processDate));

//		log("0", "BEGIN", 0);
		dblog.begin(module, "Begin");
		upsertTgtt20();
//		log("0", "Merge Nonbank", 0);
		dblog.log("0", "Merge Nonbank", module, 0);

		updateFromPending(params);

		log("0", "END", 0);
	}

	/* ========== Steps ========== */

	private void upsertTgtt20() {
		// Đồng bộ từ bảng nguồn đã có sẵn bên TiDB: PARTICIPANT_IBFT20 (NONBANK)
		final String sql = """
				    INSERT INTO TGTT_20 (TGTT_ID, TGTT_SHORT)
				    SELECT p.PARTICIPANT_CODE, p.PARTICIPANT_CODE
				    FROM PARTICIPANT_IBFT20 p
				    WHERE p.PARTICIPANT_TYPE = 'NONBANK'
				    ON DUPLICATE KEY UPDATE TGTT_SHORT = VALUES(TGTT_SHORT)
				""";
		jdbc.getJdbcTemplate().update(sql);
	}

	private void updateFromPending(Map<String, Object> params) {
		// OPTION A: F13 là VARCHAR 'YYYYMMDD' (đổi thành B.F13 nếu F13 là DATE)
		final String sql = """
				    UPDATE SHCLOG_SETT_IBFT A
				    JOIN RR_QRPAY_NONBANK_PENDING B
				      ON A.ORIGTRACE    = CAST(B.F11 AS UNSIGNED)
				     AND A.LOCAL_TIME   = CAST(B.F12 AS UNSIGNED)
				     AND A.LOCAL_DATE   = STR_TO_DATE(B.F13, '%Y%m%d')  -- Nếu F13 là DATE: thay = B.F13
				     AND A.ACQUIRER_RP  = CAST(B.ACQ_ID AS UNSIGNED)
				     AND TRIM(A.TERMID) = TRIM(B.F41)
				    SET
				      A.INS_PCODE           = TRIM(SUBSTR(B.F3, 1, 2)),
				      A.MERCHANT_TYPE_ORIG  = B.F18,
				      A.ACCEPTORNAME        = B.F43
				    WHERE
				      A.SETTLEMENT_DATE = :processDate
				      AND A.BB_BIN = 980478
				      AND A.BB_BIN_ORIG IN (SELECT TGTT_ID FROM TGTT_20)
				      AND A.RESPCODE = 0
				      AND A.ORIGRESPCODE IN (68,97)
				      AND A.INS_PCODE IS NULL
				      AND B.ORIGINAL_SETTLE_DATETIME = :processDate
				""";
		jdbc.update(sql, new MapSqlParameterSource(params));
	}

	private void log(String code, String detail, int critical) {
		jdbc.getJdbcTemplate().update("INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE, CRITICAL) "
				+ "VALUES (NOW(), ?, ?, 'UPDATE_TRANS_TGTT_20', ?)", code, detail, critical);
	}
}
