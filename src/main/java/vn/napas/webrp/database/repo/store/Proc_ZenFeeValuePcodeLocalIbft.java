package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;
import vn.napas.webrp.noti.SmsNotifier;
import vn.napas.webrp.report.util.SqlLogUtils;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mysql.cj.log.Log;

import java.sql.SQLException;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class Proc_ZenFeeValuePcodeLocalIbft {

	private final NamedParameterJdbcTemplate jdbc;
	private final DbLoggerRepository dbLog;
	private final SmsNotifier smsNoti;

	private final String module = "ZEN_FEE_VALUE_PCODE_LOCAL_IBFT";
	/* ====== SQL: ghi log vào ERR_EX ====== */
	private static final String SQL_LOG = "INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE, CRITICAL) "
			+ "VALUES (NOW(), :ecode, :detail, 'ZEN_FEE_VALUE_PCODE_LOCAL_IBFT', :critical)";

	/*
	 * ====== SQL: chèn dữ liệu ZEN_FEE_VALUE_IBFT ====== LƯU Ý: - Khuyến nghị thêm
	 * unique key (ZEN_TYPE, ZEN_VALUE) và dùng INSERT IGNORE để tránh trùng. - Tham
	 * số :settlementDate định dạng dd/MM/yyyy
	 */
	private static final String SQL_INSERT_VALUES = """
			INSERT IGNORE INTO ZEN_FEE_VALUE_IBFT (ZEN_TYPE, ZEN_VALUE)
			SELECT DISTINCT
			  'PCODE' AS ZEN_TYPE,
			  (
			    CASE
			      WHEN IFNULL(PCODE2, 0) NOT IN (
			        960000,970000,980000,990000,760000,
			        967500,977500,987500,997500,967600,977600,987600,997600,
			        967700,977700,987700,997700,
			        967800,977800,987800,997800,967900,977900,987900,997900,
			        968500,978500,988500,998500
			      ) THEN
			        CASE
			          WHEN ISSUER_RP = 602907
			            THEN IFNULL(PCODE_ORIG, SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2))
			          WHEN PCODE2 IS NULL OR PCODE2 IN (950000,850000,750000,760000,780000,790000)
			            THEN SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2)
			          WHEN PCODE2 IN (810000,820000,830000,860000,870000,880000)
			            THEN SUBSTR(LPAD(CAST(PCODE2 AS CHAR), 6, '0'), 1, 2)
			          WHEN PCODE2 = 910000 AND (FROM_SYS = 'IST' OR TRAN_CASE = 'C3|72')
			            THEN SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2)
			          WHEN PCODE2 = 930000
			            THEN SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2)
			          WHEN IFNULL(PCODE2,0) IN (968400,978400,988400,998400)
			            THEN '84'
			          ELSE SUBSTR(LPAD(CAST(PCODE2 AS CHAR), 6, '0'), 1, 2)
			        END

			      WHEN SUBSTR(LPAD(CAST(PCODE2 AS CHAR), 6, '0'), 1, 2) IN ('96','97','98','99')
			           AND ACQUIRER_RP IN (605609,220699,605608,600005)
			        THEN SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2)

			      WHEN SUBSTR(LPAD(CAST(PCODE2 AS CHAR), 6, '0'), 1, 2) IN ('98','99')
			           AND (
			             (SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2) = '30' AND MERCHANT_TYPE = 6012)
			             OR
			             (SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2) IN ('01','30','35','40','94') AND MERCHANT_TYPE = 6011)
			             OR
			             (SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2) IN ('00','20')
			              AND IFNULL(MERCHANT_TYPE_ORIG, 0) NOT IN (4111,4131,5172,9211,9222,9223,9311,9399,8398,7523,7524,5541,5542))
			           )
			        THEN SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2)

			      ELSE CONCAT(
			        SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2),
			        CASE LPAD(CAST(PCODE2 AS CHAR), 6, '0')
			          WHEN '967500' THEN '960000' WHEN '977500' THEN '970000'
			          WHEN '968500' THEN '960000' WHEN '978500' THEN '970000'
			          WHEN '987500' THEN '980000' WHEN '997500' THEN '990000'
			          WHEN '967700' THEN '960000' WHEN '977700' THEN '970000'
			          WHEN '987700' THEN '980000' WHEN '997700' THEN '990000'
			          WHEN '967800' THEN '960000' WHEN '977800' THEN '970000'
			          WHEN '987800' THEN '980000' WHEN '997800' THEN '990000'
			          WHEN '967900' THEN '960000' WHEN '977900' THEN '970000'
			          WHEN '987900' THEN '980000' WHEN '997900' THEN '990000'
			          WHEN '967600' THEN '960000' WHEN '977600' THEN '970000'
			          WHEN '987600' THEN '980000' WHEN '997600' THEN '990000'
			          ELSE LPAD(CAST(PCODE2 AS CHAR), 6, '0')
			        END
			      )
			    END
			  ) AS ZEN_VALUE
			FROM SHCLOG_SETT_IBFT
			WHERE SETTLEMENT_DATE = STR_TO_DATE(:settlementDate, '%d/%m/%Y')
			  AND MSGTYPE = 210
			  AND RESPCODE = 0
			  AND ISREV IS NULL
			  AND (
			        (
			          (
			            SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2) IN ('00','01','30','35','40','41','42','43','48','20')
			            AND IFNULL(MSGTYPE_DETAIL,'NA') NOT IN ('VPREC')
			          )
			          OR (
			            SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2) = '94' AND MERCHANT_TYPE = 6011
			          )
			        )
			        AND IFNULL(FROM_SYS,'IST') LIKE '%IST%'
			      OR (
			        FROM_SYS IS NOT NULL
			        AND SUBSTR(LPAD(CAST(PCODE AS CHAR), 6, '0'), 1, 2) IN ('01','42','91')
			      )
			  )
			""";

	/* ========== API ========== */

//	public void logBegin() {
//		jdbc.update(SQL_LOG, Map.of("ecode", 0, "detail", "Begin Zen Pcode value to SHCLOG_SETT_IBFT", "critical", 0));
//	}
//
//	public void logEnd() {
//		jdbc.update(SQL_LOG, Map.of("ecode", 0, "detail", "End Zen Pcode value to SHCLOG_SETT_IBFT", "critical", 0));
//	}
//
//	public void logError(String message) {
//		jdbc.update(SQL_LOG, Map.of("ecode", -1, "detail", "Error: " + message, "critical", 2));
//	}

	/**
	 * Thực thi insert chính. Trả về số dòng chèn (bỏ qua dòng trùng nhờ INSERT
	 * IGNORE).
	 * 
	 */
	/** 24.ZEN_FEE_VALUE_PCODE_LOCAL_IBFT.prc
	 * @param settlementDate_ddMMyyyy: sette date format ddMMyyyy
	 * @return
	 */
	public int insertZenValues(String settlementDate_ddMMyyyy) {
		try {
			dbLog.begin(module, "Begin Zen Pcode value to SHCLOG_SETT_IBFT");
			MapSqlParameterSource params = new MapSqlParameterSource().addValue("settlementDate",
					settlementDate_ddMMyyyy);
			log.info("SQL: {}",SqlLogUtils.renderSql(SQL_INSERT_VALUES, params.getValues()));
			int result = jdbc.update(SQL_INSERT_VALUES, params);
			dbLog.begin(module, "End Zen Pcode value to SHCLOG_SETT_IBFT");
			return result;
		} catch (Exception e) {
			int ecode = -1;
			String sqlState = null;
			String msg = e.getMessage();
			if (e instanceof java.sql.BatchUpdateException) {
				ecode = ((java.sql.BatchUpdateException) e).getErrorCode();
			}
			else if (e instanceof SQLException) {
				ecode = ((SQLException) e).getErrorCode();
			}
			msg = "ZEN_FEE_VALUE_PCODE_LOCAL_IBFT#0366155501;0983411005;0988766330#" + msg;
			dbLog.log(ecode + "", msg, module, 2);
//			ZEN_FEE_VALUE_PCODE_LOCAL_IBFT#0366155501;0983411005;0988766330#'||vDetail
			
			smsNoti.notifyError(module, msg);
			return -1;
		}
	}
}
