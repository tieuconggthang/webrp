package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;
import vn.napas.webrp.noti.SmsNotifier;
import vn.napas.webrp.report.util.SqlLogUtils;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 28.MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT.prc -> TiDB/Java
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class Proc_MergeFeeKeyToShclogSettIbft {

	private static final String MODULE = "MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT";

	private final NamedParameterJdbcTemplate jdbc;
	private final DbLoggerRepository dbLog;
	private final SmsNotifier smsNotifier;

	/**
	 * SQL UPDATE đã bỏ subquery trong ON bằng cách LEFT JOIN bảng tạm G
	 * (GR_FEE_CONFIG_NEW).
	 */
	private static final String SQL_UPDATE = """
			/* language=SQL */
			UPDATE SHCLOG_SETT_IBFT S
			JOIN (
			  SELECT
			      S0.PAN,
			      S0.ORIGTRACE,
			      S0.TERMID,
			      S0.LOCAL_DATE,
			      S0.LOCAL_TIME,
			      S0.ACQUIRER,

			      /* ===== TÍNH FEE_VALUE: ghép 4 thành phần ===== */
			      CONCAT(
			        /* (1) ACQUIRER_FE||ISSUER_FE (nếu FE null/0 thì lấy gốc) */
			        IFNULL(NULLIF(S0.ACQUIRER_FE, 0), S0.ACQUIRER),
			        IFNULL(NULLIF(S0.ISSUER_FE,   0), S0.ISSUER),

			        /* (2) Mã tiền tệ: 840 thì 840, ngược lại 704 (kể cả NULL) */
			        CASE WHEN S0.ACQ_CURRENCY_CODE = 840 THEN 840 ELSE 704 END,

			        /* (3) MERCHANT_TYPE theo các nhánh nghiệp vụ */
			        (
			          CASE
			            WHEN S0.TRAN_CASE = '72|C3' THEN 6011
			            WHEN S0.PCODE2 IN (890000,880000,720000,730000) THEN 0
			            WHEN S0.PCODE2 IN (840000,968400,978400,988400,998400) THEN S0.MERCHANT_TYPE
			            WHEN LEFT(LPAD(CAST(S0.PCODE2 AS CHAR), 6, '0'), 2) IN ('98','99')
			                 AND LEFT(LPAD(CAST(S0.PCODE  AS CHAR), 6, '0'), 2) = '30'
			                 AND S0.MERCHANT_TYPE = 6012
			              THEN S0.MERCHANT_TYPE
			            WHEN S0.PCODE2 IN (
			                 960000,970000,980000,990000,968500,978500,988500,998500,
			                 967500,977500,987500,997500,967600,977600,987600,997600,
			                 967700,977700,987700,997700,967800,977800,987800,997800,
			                 967900,977900,987900,997900,966100,976100,986100,996100,
			                 966200,976200,986200,996200
			               )
			               AND LEFT(LPAD(CAST(S0.PCODE AS CHAR), 6, '0'), 2) IN ('00','20')
			               AND S0.MERCHANT_TYPE_ORIG IN (
			                 SELECT G.MERCHANT_TYPE
			                 FROM GR_FEE_CONFIG_NEW G
			                 WHERE STR_TO_DATE(:settlementDate,'%d/%m/%Y') BETWEEN G.VALID_FROM AND G.VALID_TO
			                   AND SUBSTR(LPAD(CAST(S0.PCODE2 AS CHAR),6,'0'),3,2) = SUBSTR(G.PRO_CODE,3,2)
			               )
			              THEN S0.MERCHANT_TYPE_ORIG
			            WHEN LEFT(IFNULL(CAST(S0.PCODE2 AS CHAR),'0'),2) NOT IN ('96','97','98','99')
			               AND LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2) IN ('00','20')
			               AND S0.MERCHANT_TYPE_ORIG IN (4111,4131,5172,9211,9222,9223,9311,9399,8398,7523,7524,5541,5542)
			              THEN S0.MERCHANT_TYPE_ORIG
			            ELSE S0.MERCHANT_TYPE
			          END
			        ),

			        /* (4) Đuôi cặp mã từ PCODE/PCODE2 theo rule đặc biệt */
			        (
			          CASE
			            WHEN S0.PCODE2 IN (760000,967600,977600,987600,997600) THEN '76'
			            WHEN IFNULL(S0.PCODE2,0) NOT IN (
			                 960000,970000,980000,990000,968500,978500,988500,998500,
			                 967700,977700,987700,997700,967500,977500,987500,997500,
			                 967800,977800,987800,997800,967900,977900,987900,997900,
			                 966100,976100,986100,996100,966200,976200,986200,996200
			               )
			              THEN CASE
			                     WHEN S0.ISSUER_RP = 602907
			                          THEN IFNULL(S0.PCODE_ORIG, LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2))
			                     WHEN S0.PCODE2 IS NULL
			                       OR S0.PCODE2 IN (950000,850000,750000,760000,780000,790000)
			                          THEN LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2)
			                     WHEN S0.PCODE2 IN (810000,820000,830000,860000,870000,880000)
			                          THEN LEFT(LPAD(CAST(S0.PCODE2 AS CHAR),6,'0'),2)
			                     WHEN S0.PCODE2 = 910000 AND (S0.FROM_SYS = 'IST' OR S0.TRAN_CASE = 'C3|72')
			                          THEN LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2)
			                     WHEN S0.PCODE2 = 930000
			                          THEN LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2)
			                     WHEN IFNULL(S0.PCODE2,0) IN (968400,978400,988400,998400) THEN '84'
			                     ELSE LEFT(LPAD(CAST(S0.PCODE2 AS CHAR),6,'0'),2)
			                   END
			            WHEN LEFT(LPAD(CAST(S0.PCODE2 AS CHAR),6,'0'),2) IN ('98','99')
			             AND (
			                   (LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2) = '30' AND S0.MERCHANT_TYPE = 6012)
			                OR (LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2) IN ('01','30','35','40','94') AND S0.MERCHANT_TYPE = 6011)
			                OR (LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2) IN ('00','20')
			                    AND S0.MERCHANT_TYPE_ORIG NOT IN (4111,4131,5172,9211,9222,9223,9311,9399,8398,7523,7524,5541,5542))
			                 )
			              THEN LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2)
			            WHEN S0.ACQUIRER_RP IN (605609,220699,605608,600005)
			             AND LEFT(LPAD(CAST(S0.PCODE2 AS CHAR),6,'0'),2) IN ('96','97','98','99')
			              THEN LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2)
			            ELSE
			              CASE LPAD(CAST(S0.PCODE2 AS CHAR),6,'0')
			                WHEN '968500' THEN '960000' WHEN '978500' THEN '970000'
			                WHEN '967500' THEN '960000' WHEN '977500' THEN '970000'
			                WHEN '987500' THEN '980000' WHEN '997500' THEN '990000'
			                WHEN '967700' THEN '960000' WHEN '977700' THEN '970000'
			                WHEN '987700' THEN '980000' WHEN '997700' THEN '990000'
			                WHEN '967800' THEN '960000' WHEN '977800' THEN '970000'
			                WHEN '987800' THEN '980000' WHEN '997800' THEN '990000'
			                WHEN '967900' THEN '960000' WHEN '977900' THEN '970000'
			                WHEN '987900' THEN '980000' WHEN '997900' THEN '990000'
			                WHEN '966100' THEN '960000' WHEN '976100' THEN '970000'
			                WHEN '986100' THEN '980000' WHEN '996100' THEN '990000'
			                WHEN '966200' THEN '960000' WHEN '976200' THEN '970000'
			                WHEN '986200' THEN '980000' WHEN '996200' THEN '990000'
			                ELSE LPAD(CAST(S0.PCODE2 AS CHAR),6,'0')
			              END
			          END
			        )
			      ) AS FEE_VALUE

			  FROM SHCLOG_SETT_IBFT S0
			  WHERE S0.SETTLEMENT_DATE = STR_TO_DATE(:settlementDate,'%d/%m/%Y')
			    AND (
			         (S0.MSGTYPE = 210 AND S0.RESPCODE = 0 AND S0.ISREV IS NULL)
			         OR (S0.MSGTYPE = 430 AND S0.RESPCODE = 114)
			        )
			    AND S0.ISSUER_RP <> '602907'
			    AND (S0.FEE_NOTE IS NULL OR S0.FEE_NOTE NOT LIKE 'HACH TOAN DIEU CHINH%')
			    AND (
			          (
			            (
			               LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2) IN ('00','01','30','35','40','41','42','43','48','20')
			               AND IFNULL(S0.MSGTYPE_DETAIL,'NA') <> 'VPREC'
			            )
			            OR (LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2) = '94' AND S0.MERCHANT_TYPE = 6011)
			          )
			          AND IFNULL(S0.FROM_SYS,'IST') LIKE '%IST%'
			        OR (
			            S0.FROM_SYS IS NOT NULL
			            AND LEFT(LPAD(CAST(S0.PCODE AS CHAR),6,'0'),2) IN ('01','42','91')
			          )
			        )
			) A
			  ON  A.PAN        = S.PAN
			  AND A.ORIGTRACE  = S.ORIGTRACE
			  AND A.TERMID     = S.TERMID
			  AND A.LOCAL_DATE = S.LOCAL_DATE
			  AND A.LOCAL_TIME = S.LOCAL_TIME
			  AND A.ACQUIRER   = S.ACQUIRER
			JOIN ZEN_CONFIG_FEE_IBFT Z
			  ON Z.FEE_VALUE = A.FEE_VALUE
			SET S.FEE_KEY = Z.FEE_KEY;
						        """;

	/**
	 * Thực thi job merge FEE_KEY tương đương store Oracle.
	 * 
	 * @param settlementDateDdMMyyyy dd/MM/yyyy (vd: "07/09/2025")
	 * @return số bản ghi update (rows affected), hoặc -1 nếu lỗi
	 */
	public int mergeFeeKeyToShclogSettIbft(String settlementDateDdMMyyyy) {
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("settlementDate", settlementDateDdMMyyyy);

		try {
			dbLog.begin(MODULE, "Begin Merge Fee key to SHCLOG_SETT_IBFT");
			log.info(SqlLogUtils.renderSql(SQL_UPDATE, p.getValues()));

			int updated = jdbc.update(SQL_UPDATE, p);

			dbLog.end(MODULE, "End Merge Fee key to SHCLOG_SETT_IBFT");
			log.info("[{}] Updated rows: {}", MODULE, updated);
			return updated;

		} catch (DataAccessException ex) {
			int ecode = -1;
			String emsg = ex.getMessage();
			Throwable root = ex.getRootCause();
			if (root instanceof SQLException sqlEx) {
				ecode = sqlEx.getErrorCode();
				if (sqlEx.getMessage() != null)
					emsg = sqlEx.getMessage();
			} else if (root != null && root.getMessage() != null) {
				emsg = root.getMessage();
			}

			String detail = "MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT error: " + emsg;
			dbLog.error(String.valueOf(ecode), MODULE, detail, 2);
			smsNotifier.notifyError(MODULE, detail);

			log.error("[{}] {}", MODULE, detail, ex);
			return -1;

		} catch (Exception ex) {
			int ecode = (ex instanceof SQLException sqlEx) ? sqlEx.getErrorCode() : -1;
			String detail = "MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT error: " + ex.getMessage();

			dbLog.error(String.valueOf(ecode), MODULE, detail, 2);
			smsNotifier.notifyError(MODULE, detail);

			log.error("[{}] {}", MODULE, detail, ex);
			return -1;
		}
	}
}
