package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.report.util.SqlLogUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Chuyển MERGE Oracle -> 3 bước cho TiDB/MySQL:
 *   step0: chuẩn bị (UNIQUE KEY + VIEW nguồn)
 *   step1: UPDATE các bản ghi đã tồn tại (match)
 *   step2: INSERT các bản ghi chưa tồn tại (NOT EXISTS) + dedup
 *   step3: UPDATE STT ổn định
 *
 * Ghi log vào ERR_EX trước/sau mỗi bước.
 */
/**
 * MERGE_SHC_SETT_IBFT_200
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class Proc_MERGE_SHC_SETT_IBFT_200 {

	private static final String MODULE = "MERGE_SHC_SETT_IBFT_200";
	private final NamedParameterJdbcTemplate jdbc;
	private final JdbcTemplate jdbcTemplate;

	/*
	 * ========================= 1) CÁC CÂU LỆNH SQL DẠNG HẰNG
	 * =========================
	 */

	// step0.1 – UNIQUE KEY (bắt lỗi trùng tên thì bỏ qua)
	private static final String SQL_STEP0_UNIQUE_KEY = """
			ALTER TABLE SHCLOG_SETT_IBFT
			ADD UNIQUE KEY uk_ibft_merge (PAN, ORIGTRACE, TERMID, LOCAL_DATE, LOCAL_TIME, ACQUIRER)
			""";

	// step0.2 – VIEW nguồn chung (lọc & chuẩn hoá)
	private static final String SQL_STEP0_SOURCE_VIEW = """
			//        CREATE OR REPLACE VIEW V_IBFT_SRC_0200 AS
			//        SELECT
			//          TRIM(B.CARD_NO)                  AS card_no_trim,
			//          CAST(B.TRACE_NO AS UNSIGNED)     AS trace_no_num,
			//          B.TERM_ID                        AS term_id,
			//          LPAD(B.LOCAL_DATE,4,'0')         AS local_mmdd,
			//          CAST(B.LOCAL_TIME AS UNSIGNED)   AS local_time_num,
			//          CAST(B.ACQ_ID   AS UNSIGNED)     AS acq_id_num,
			//
			//          B.MTI, B.BEN_ID, B.SERVICE_CODE, B.ISS_ID, B.PROC_CODE, B.IBFT_INFO,
			//          B.ACQ_COUNTRY, B.POS_ENTRY_CODE, B.POS_CONDITION_CODE, B.ADDRESPONSE, B.MVV,
			//          B.F4, B.F5, B.F6, B.F49,
			//          B.SETTLEMENT_CODE, B.SETTLEMENT_RATE, B.ISS_CONV_RATE, B.TCC,
			//          B.REF_NO, B.TNX_STAMP, B.CARD_ACCEPT_NAME_LOCATION, B.CARD_ACCEPT_ID_CODE,
			//          B.SETTLE_DATE, B.ACCOUNT_NO, B.DEST_ACCOUNT, B.MCC, B.APPROVAL_CODE, B.ADD_INFO,
			//
			//          CAST(B.AMOUNT AS DECIMAL(20,0))                                   AS amount_raw_num,
			//          CAST(SUBSTRING(B.AMOUNT,1,GREATEST(CHAR_LENGTH(B.AMOUNT)-2,0)) AS UNSIGNED) AS amount_ins_num,
			//          CAST(SUBSTRING(B.F4,1,GREATEST(CHAR_LENGTH(B.F4)-2,0)) AS UNSIGNED) AS f4_num,
			//          CAST(SUBSTRING(B.F5,1,GREATEST(CHAR_LENGTH(B.F5)-2,0)) AS UNSIGNED) AS f5_num,
			//          CAST(SUBSTRING(B.F6,1,GREATEST(CHAR_LENGTH(B.F6)-2,0)) AS UNSIGNED) AS f6_num,
			//          CAST(CONCAT('2', B.TRACE_NO) AS UNSIGNED)                         AS trace2_num
			//        FROM ISOMESSAGE_TMP_TURN B
			//        WHERE B.CARD_NO IS NOT NULL
			//          AND B.MTI = '0200'
			//          AND B.BEN_ID   REGEXP '^[0-9]+$'
			//          AND B.TRACE_NO REGEXP '^[0-9]+$'
			//        """;

	// step 3.1 – UPDATE (match theo đúng điều kiện MERGE gốc)
	private static final String SQL_STEP3_1_UPDATE = """
			UPDATE SHCLOG_SETT_IBFT A
			JOIN V_ISOMESSAGE_TMP_TURN_0200 B
			  ON  A.PAN        = B.card_no_trim
			  AND A.ORIGTRACE  = B.trace_no_num
			  AND A.TERMID     = B.term_id
			  AND DATE_FORMAT(A.LOCAL_DATE, '%m%d') = B.local_mmdd
			  AND A.LOCAL_TIME = B.local_time_num
			  AND A.ACQUIRER   = B.acq_id_num
			SET
			  A.RESPCODE = CASE
			                 WHEN A.AMOUNT <> ROUND(B.amount_raw_num/100, 0) AND A.RESPCODE = 0
			                 THEN 116 ELSE A.RESPCODE
			               END,
			  A.TXNSRC   = CASE
			                 WHEN A.AMOUNT <> ROUND(B.amount_raw_num/100, 0)
			                 THEN 'RC=99' ELSE A.TXNSRC
			               END,
			  A.CONTENT_FUND = B.IBFT_INFO
			""";

	// step 3.2 – INSERT (NOT EXISTS) + dedup nguồn (1 dòng/khóa, ưu tiên TNX_STAMP
	// mới nhất)
	private static final String SQL_STEP3_2INSERT_NOT_EXISTS = """
			INSERT INTO SHCLOG_SETT_IBFT (
			  PAN, ORIGTRACE, TERMID, LOCAL_DATE, LOCAL_TIME, ACQUIRER,
			  DATA_ID, PPCODE, MSGTYPE, PCODE, AMOUNT, ACQ_CURRENCY_CODE, TRACE,
			  SETTLEMENT_DATE, ISSUER, RESPCODE, MERCHANT_TYPE, MERCHANT_TYPE_ORIG, AUTHNUM,
			  SETT_CURRENCY_CODE, ADD_INFO, ACCTNUM, ISS_CURRENCY_CODE, ORIGISS,
			  ORIGRESPCODE, CH_CURRENCY_CODE, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP,
			  PCODE2, FROM_SYS, BB_BIN, BB_BIN_ORIG, CONTENT_FUND, TXNSRC, ACQ_COUNTRY,
			  POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, F4, F5, F6, F49,
			  SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC,
			  refnum, trandate, trantime, ACCEPTORNAME, TERMLOC, F15, pcode_orig,
			  ACCOUNT_NO, DEST_ACCOUNT
			)
			SELECT
			  B.card_no_trim,
			  B.trace_no_num,
			  B.term_id,
			  STR_TO_DATE(CONCAT(YEAR(CURDATE()), B.local_mmdd), '%%Y%%m%%d'),
			  B.local_time_num,
			  B.acq_id_num,

			  1,
			  CAST(B.PROC_CODE AS UNSIGNED),
			  '210',
			  CAST(B.PROC_CODE AS UNSIGNED),
			  B.amount_ins_num,
			  704,
			  B.trace2_num,

			  STR_TO_DATE(CONCAT(YEAR(CURDATE()), LPAD(B.SETTLE_DATE,4,'0')), '%%Y%%m%%d'),
			  B.acq_id_num,
			  CASE
			    WHEN B.BEN_ID = '971133' AND B.DEST_ACCOUNT LIKE 'NPDC%%' THEN 68
			    WHEN B.SERVICE_CODE = 'QR_PUSH' THEN 68
			    WHEN B.BEN_ID = '971100' AND B.TCC = '99' THEN 68
			    WHEN TRIM(B.ISS_ID) IN ('980471','980472') THEN 68
			    ELSE 0
			  END,
			  6011,
			  CAST(B.MCC AS UNSIGNED),
			  B.APPROVAL_CODE,
			  704,
			  B.ADD_INFO,
			  CONCAT(IFNULL(B.ACCOUNT_NO,' '), '|', IFNULL(B.DEST_ACCOUNT,'')),
			  704,
			  CASE
			    WHEN TRIM(B.ISS_ID) = '980471' THEN 980471
			    WHEN TRIM(B.ISS_ID) = '980475' THEN 980478
			    WHEN B.acq_id_num = 191919 THEN 970459
			    WHEN B.acq_id_num = 970415 THEN 970489
			    ELSE MAP_IBFT_ACQ_ID(B.acq_id_num)
			  END,
			  '97',
			  704,
			  NULL, NULL, NULL, NULL,
			  CASE
			    WHEN B.acq_id_num = 191919 THEN 970459
			    WHEN B.acq_id_num = 970415 THEN 970489
			    ELSE MAP_IBFT_ACQ_ID(B.acq_id_num)
			  END,
			  'IBT',
			  CASE
			    WHEN TRIM(B.ISS_ID) = '980472' THEN 980471
			    WHEN TRIM(B.ISS_ID) = '980474' THEN 980478
			    WHEN B.BEN_ID IS NOT NULL AND B.PROC_CODE IN ('912020','910020')
			      THEN GET_IBT_BIN(B.BEN_ID)
			    ELSE GET_IBT_BIN(SUBSTR(B.DEST_ACCOUNT, 1, 6))
			  END,
			  CASE
			    WHEN B.BEN_ID IN (SELECT TGTT_ID FROM TGTT_20) THEN TO_NUMBER_BNV(B.BEN_ID)
			    WHEN TRIM(B.ISS_ID) IN ('980472','980474','980475')
			      THEN CASE WHEN B.BEN_ID IS NOT NULL AND B.PROC_CODE IN ('912020','910020')
			                THEN GET_IBT_BIN(B.BEN_ID)
			                ELSE GET_IBT_BIN(SUBSTR(B.DEST_ACCOUNT,1,6)) END
			    ELSE TO_NUMBER_BNV(B.BEN_ID)
			  END,
			  B.IBFT_INFO,
			  'MTI=200',
			  B.ACQ_COUNTRY, B.POS_ENTRY_CODE, B.POS_CONDITION_CODE, B.ADDRESPONSE, B.MVV,
			  B.f4_num, B.f5_num, B.f6_num, B.F49,
			  B.SETTLEMENT_CODE, B.SETTLEMENT_RATE, B.ISS_CONV_RATE, B.TCC,
			  B.REF_NO, DATE(B.TNX_STAMP), DATE_FORMAT(B.TNX_STAMP, '%%H%%i%%s'),
			  B.CARD_ACCEPT_NAME_LOCATION, B.CARD_ACCEPT_ID_CODE,
			  NULL, CAST(B.PROC_CODE AS UNSIGNED),
			  B.ACCOUNT_NO, B.DEST_ACCOUNT
			FROM (
			  /* dedup nguồn: chọn 1 dòng/khóa, ưu tiên TNX_STAMP mới nhất */
			  SELECT *
			  FROM (
			    SELECT
			      card_no_trim, trace_no_num, term_id, local_mmdd, local_time_num, acq_id_num,
			      MTI, BEN_ID, SERVICE_CODE, ISS_ID, PROC_CODE, IBFT_INFO,
			      ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV,
			      F4, F5, F6, F49,
			      SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC,
			      REF_NO, TNX_STAMP, CARD_ACCEPT_NAME_LOCATION, CARD_ACCEPT_ID_CODE,
			      SETTLE_DATE, ACCOUNT_NO, DEST_ACCOUNT, MCC, APPROVAL_CODE, ADD_INFO,
			      amount_ins_num, f4_num, f5_num, f6_num, trace2_num,
			      ROW_NUMBER() OVER (
			        PARTITION BY card_no_trim, trace_no_num, term_id, local_mmdd, local_time_num, acq_id_num
			        ORDER BY TNX_STAMP DESC
			      ) rn
			    FROM V_ISOMESSAGE_TMP_TURN_0200
			  ) t
			  WHERE t.rn = 1
			) B
			WHERE NOT EXISTS (
			  SELECT 1
			  FROM SHCLOG_SETT_IBFT A
			  WHERE A.PAN        = B.card_no_trim
			    AND A.ORIGTRACE  = B.trace_no_num
			    AND A.TERMID     = B.term_id
			    AND DATE_FORMAT(A.LOCAL_DATE,'%%m%%d') = B.local_mmdd
			    AND A.LOCAL_TIME = B.local_time_num
			    AND A.ACQUIRER   = B.acq_id_num
			)
			""";

	private static final String SQL_STEP_4_FINÍH_MERGE = """
			INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
						VALUES (NOW(), 0, 'Finish Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN', :module);
						""";
	private static final String SQL_STEP_5_UPDATE_SHCLOG_SETT_IBFT_STT = """
			UPDATE SHCLOG_SETT_IBFT AS a
			JOIN (
			    SELECT
			        tidb_id,
			        ROW_NUMBER() OVER (ORDER BY TRANDATE, TRANTIME, TRACE, id) + :iSTT AS new_stt
			    FROM SHCLOG_SETT_IBFT
			    WHERE STT IS NULL
			      AND ORIGRESPCODE = 97
			) AS b
			ON a.tidb_id = b.tidb_id
			SET a.STT = b.new_stt;
									""";

	private static final String SQL_STEP_6_FINISH_UPDATE = """
			INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
			VALUES (
			    NOW(),
			    0,
			    CONCAT('Finish Update STT for ', :rowupdate, ' transactions ORIGRESPCODE = 97 in SHCLOG_SETT_IBFT'),
			    :module
			);
						""";

	private static final String SQL_STEP_7_END_MERGE = """
			INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
			VALUES (NOW(), 0, 'End Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN', :module);
						""";

	/*
	 * ========================= 2) HÀM THỰC THI CHUNG =========================
	 */

	private int executeUpdate(String sql) {
		return jdbcTemplate.update(sql);
	}

	private int executeUpdate(String sql, Object... args) {
		return jdbcTemplate.update(sql, args);
	}

	private void safeAlterUniqueKey() {
		try {
			executeUpdate(SQL_STEP0_UNIQUE_KEY);
			log("Created unique key uk_ibft_merge");
		} catch (Exception ignore) {
			log("Unique key existed (skip)");
		}
	}

	private void log(String msg) {
		executeUpdate("""
				    INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
				    VALUES (NOW(), 0, ?, ?)
				""", msg, MODULE);
	}

	/*
	 * ========================= 3) CÁC STEP CỤ THỂ =========================
	 */

//	@Transactional
//	public void step0Prepare() {
//		safeAlterUniqueKey();
//		executeUpdate(SQL_STEP0_SOURCE_VIEW);
//		log("Created/Refreshed view V_IBFT_SRC_0200");
//	}

	@Transactional
	public void step31Update() {
		MapSqlParameterSource p = new MapSqlParameterSource();
//        int rows = executeUpdate(SQL_STEP3_1_UPDATE);
		exec(MODULE, SQL_STEP3_1_UPDATE, p);
//        log("Step1 UPDATE affected: " + rows);
//        return rows;
	}

	@Transactional
	public void step32Insert() {
		MapSqlParameterSource p = new MapSqlParameterSource();
		exec(MODULE, SQL_STEP3_2INSERT_NOT_EXISTS, p);
//        int rows = executeUpdate(SQL_STEP3_2INSERT_NOT_EXISTS);
//        log("Step2 INSERT affected: " + rows);
//        return rows;
	}

	@Transactional
	public int step2getMaxSTT() {
		Integer iSTT = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(STT), 0) FROM SHCLOG_SETT_IBFT", Integer.class);
//        jdbc.update("DROP TEMPORARY TABLE IF EXISTS _stt_tmp");
//        executeUpdate(SQL_STEP3_BUILD_TMP, iSTT);
//        int rows = executeUpdate(SQL_STEP3_APPLY);
//        jdbc.update("DROP TEMPORARY TABLE IF EXISTS _stt_tmp");
//        log("Step3 STT updated: " + rows);
		return iSTT;
	}

	/** Chạy toàn bộ pipeline theo thứ tự các bước */
	@Transactional
	public void runAll() {
		log("Begin pipeline");
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("module", MODULE);
		//step 2 get max stt from SHCLOG_SETT_IBFT
		int iSTT = step2getMaxSTT();
//        step0Prepare();
		p.addValue("iSTT", iSTT);
		step31Update();
		step32Insert();
//        step3UpdateStt();
		// step 4
		exec(MODULE, SQL_STEP_4_FINÍH_MERGE, p);
		// step 5 Update SHCLOG_SETT_IBFT
		int rows = exec(MODULE, SQL_STEP_5_UPDATE_SHCLOG_SETT_IBFT_STT, p);
		exec(MODULE, SQL_STEP_6_FINISH_UPDATE, p);
		exec(MODULE, SQL_STEP_7_END_MERGE, p);
		log("End pipeline");
	}

	private int exec(String tag, String sql, MapSqlParameterSource p) {
		String trimmed = sql == null ? "" : sql.trim();
		if (trimmed.isEmpty()) {
			log.info("{}: SKIPPED (chưa thay SQL TiDB)", tag);
			return -1;
		}
		log.info("sql tag: {}", SqlLogUtils.renderSql(sql, p.getValues()));
		int rows = jdbc.update(sql, p);
		log.info("{}: {} row(s)", tag, rows);
		return rows;
	}
}
