package vn.napas.webrp.database.repo.sql;
//package com.example.tidb.etl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.report.util.SqlLogUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
@Slf4j
public class IsoMessageTmpTurnLoader {

	private final NamedParameterJdbcTemplate jdbc;
	private final JdbcTemplate jdbcTemplate;

	public IsoMessageTmpTurnLoader(NamedParameterJdbcTemplate jdbc, JdbcTemplate jdbcTemplate) {
		this.jdbc = jdbc;
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Thực thi INSERT...SELECT từ ipsibt20.isomessage sang ISOMESSAGE_TMP_TURN
	 * (TiDB).
	 *
	 * @param settleDate   Ngày đối soát (LocalDate), sẽ được format thành "MMdd"
	 *                     (vd 04/09/2025 -> "0904").
	 * @param acqExclude   Giá trị ACQ_ID cần loại trừ (vd "605609").
	 * @param mtiList      Danh sách MTI cần lấy (vd ["0200","0210"]).
	 * @param issList      Danh sách ISS_ID (vd [980472,980471,980474,980475]); có
	 *                     thể rỗng/null => chỉ áp dụng điều kiện IS NULL.
	 * @param procPrefixes Danh sách tiền tố PROC_CODE (vd ["42","91"]) → được map
	 *                     thành LIKE '42%' OR LIKE '91%'.
	 * @return Số dòng được insert.
	 */
	public int insertFromSourceIsomessage(LocalDate settleDate, String acqExclude, List<String> mtiList,
			List<String> issList, List<String> procPrefixes) {
		try {

			String settleMmdd = DateTimeFormatter.ofPattern("MMdd").format(settleDate); // "0904" cho 04/09/2025

			// Xây mệnh đề OR động cho proc_code LIKE :pc0 OR :pc1 ...
			if (procPrefixes == null || procPrefixes.isEmpty()) {
				procPrefixes = List.of("42", "91");
			}

//			StringBuilder procLikeClause = new StringBuilder();
			Map<String, Object> params = new HashMap<>();
//
//			for (int i = 0; i < procPrefixes.size(); i++) {
//				if (i > 0)
//					procLikeClause.append(" OR ");
//				String key = "pc" + i;
//				procLikeClause.append("proc_code LIKE :").append(key);
//				params.put(key, procPrefixes.get(i) + "%");
//			}
			params.put("procPrefixes", procPrefixes);

			// MTI list
			if (mtiList == null || mtiList.isEmpty()) {
				mtiList = List.of("0200", "0210");
			}
			params.put("mtis", mtiList);

			if (issList == null || issList.isEmpty()) {
				issList = List.of("980472", "980471", "980474", "980475");
			}
			params.put("issList", issList);

			params.put("acqExclude", acqExclude == null ? "605609" : acqExclude);
			params.put("settleMmdd", settleMmdd);

			// LƯU Ý: Nếu cột SETTLE_DATE nguồn là DATE, thay điều kiện bằng:
			// DATE(SETTLE_DATE) = STR_TO_DATE(:settleDate,'%Y-%m-%d')
			// và truyền params.put("settleDate", settleDate.toString()).


			String sql = """
					INSERT INTO ISOMESSAGE_TMP_TURN(
					  SEQ_NO, MTI, CARD_NO, PROC_CODE, ORIGINAL_DATE, TRACE_NO, REF_NO, ACQ_ID, ISS_ID,
					  APPROVAL_CODE, RESPONSE_CODE, TERM_ID, ORIGINAL_DATA, REVERSED, TNX_STAMP, PACKAGER, AMOUNT,
					  ACCOUNT_NO, LOCAL_TIME, LOCAL_DATE, SETTLE_DATE, MCC, CURRENCY_CODE, DEST_ACCOUNT,
					  RECONCILE_TIME, TRANX_DATE, ADD_INFO, SERVICE_CODE, VAS_INFO, BEN_ID, ORIGRESPCODE, IBFT_INFO,
					  ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, F4, F5, F6, F49,
					  SETTLEMENT_CODE, SETTLEMENT_RATE, ISS_CONV_RATE, TCC, CARD_ACCEPT_NAME_LOCATION,
					  CARD_ACCEPT_ID_CODE, OF_YEAR
					)
					SELECT
					  SEQ_NO,
					  MTI,
					  IFNULL(TRIM(CARD_NO), '') AS CARD_NO,
					  PROC_CODE,
					  ORIGINAL_DATE,
					  TRACE_NO,
					  REF_NO,
					  ACQ_ID,
					  ISS_ID,
					  APPROVAL_CODE,
					  RESPONSE_CODE,
					  TERM_ID,
					  ORIGINAL_DATA,
					  REVERSED,
					  TNX_STAMP,
					  PACKAGER,

					  /* AMOUNT (đích BIGINT) – chỉ nhận khi là số, else NULL */
					  CASE
					    WHEN NULLIF(TRIM(AMOUNT_SETTLEMENT), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(AMOUNT_SETTLEMENT) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS AMOUNT,

					  ACCOUNT_NO,
					  LOCAL_TIME,
					  LOCAL_DATE,
					  SETTLE_DATE,

					  /* MCC ở đích là VARCHAR(12) -> giữ nguyên chuỗi */
					  TRIM(MCC) AS MCC,

					  /* CURRENCY_CODE ở đích là VARCHAR(9) – giữ logic đổi 840/704; cast sang CHAR để rõ ràng */
					  CAST(
					    CASE UPPER(TRIM(CURRENCY_CODE))
					      WHEN 'USD' THEN 840
					      WHEN 'VND' THEN 704
					      WHEN 'VDD' THEN 704
					      ELSE CASE
					             WHEN NULLIF(TRIM(CURRENCY_CODE), '') REGEXP '^[0-9]+$'
					               THEN CAST(TRIM(CURRENCY_CODE) AS UNSIGNED)
					             ELSE NULL
					           END
					    END
					  AS CHAR(9)) AS CURRENCY_CODE,

					  DEST_ACCOUNT,
					  RECONCILE_TIME,
					  TRANX_DATE,
					  ADD_INFO,
					  SERVICE_CODE,
					  VAS_INFO,
					  BEN_ID,
					  RESPONSE_CODE AS ORIGRESPCODE,

					  CASE
					    WHEN LENGTH(TRIM(IBFT_INFO)) > 210 THEN SUBSTRING(TRIM(IBFT_INFO), 1, 210)
					    ELSE TRIM(IBFT_INFO)
					  END AS IBFT_INFO,

					  /* ACQ_COUNTRY (DECIMAL(28,2)) */
					  CASE
					    WHEN NULLIF(TRIM(ACQ_INST_COUNTRY_CODE), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(ACQ_INST_COUNTRY_CODE) AS DECIMAL(28,2))
					    ELSE NULL
					  END AS ACQ_COUNTRY,

					  /* POS_ENTRY_CODE (DECIMAL(28,2)) */
					  CASE
					    WHEN NULLIF(TRIM(POS_ENTRY_MODE), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(POS_ENTRY_MODE) AS DECIMAL(28,2))
					    ELSE NULL
					  END AS POS_ENTRY_CODE,

					  /* POS_CONDITION_CODE (DECIMAL(28,2)) */
					  CASE
					    WHEN NULLIF(TRIM(POS_CONDITION_CODE), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(POS_CONDITION_CODE) AS DECIMAL(28,2))
					    ELSE NULL
					  END AS POS_CONDITION_CODE,

					  TRANX_REF AS ADDRESPONSE,
					  SERVICE_CODE AS MVV,

					  /* F4/F5/F6 (DECIMAL(26,8)) */
					  CASE
					    WHEN NULLIF(TRIM(AMOUNT), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(AMOUNT) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS F4,

					  CASE
					    WHEN NULLIF(TRIM(AMOUNT_SETTLEMENT), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(AMOUNT_SETTLEMENT) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS F5,

					  CASE
					    WHEN NULLIF(TRIM(AMOUNT_CARDHOLDER), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(AMOUNT_CARDHOLDER) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS F6,

					  /* F49 (DECIMAL(18,6)) */
					  CASE UPPER(TRIM(CURRENCY_CODE))
					    WHEN 'USD' THEN 840
					    WHEN 'VND' THEN 704
					    WHEN 'VDD' THEN 704
					    ELSE CASE
					           WHEN NULLIF(TRIM(CURRENCY_CODE), '') REGEXP '^[0-9]+$'
					             THEN CAST(TRIM(CURRENCY_CODE) AS DECIMAL(18,6))
					           ELSE NULL
					         END
					  END AS F49,

					  /* SETTLEMENT_CODE (DECIMAL(18,6)) */
					  CASE CURRENCY_CODE_SETTLEMENT
					    WHEN '   ' THEN 704
					    WHEN '.  ' THEN 704
					    ELSE CASE
					           WHEN NULLIF(TRIM(CURRENCY_CODE_SETTLEMENT), '') REGEXP '^[0-9]+$'
					             THEN CAST(TRIM(CURRENCY_CODE_SETTLEMENT) AS DECIMAL(18,6))
					           ELSE NULL
					         END
					  END AS SETTLEMENT_CODE,

					  /* SETTLEMENT_RATE (DECIMAL(26,8)) */
					  CASE
					    WHEN NULLIF(TRIM(CONVERSION_RATE_SETTLEMENT), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(CONVERSION_RATE_SETTLEMENT) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS SETTLEMENT_RATE,

					  /* ISS_CONV_RATE (DECIMAL(26,8)) */
					  CASE
					    WHEN NULLIF(TRIM(CONVERSION_RATE_CARDHOLDER), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(CONVERSION_RATE_CARDHOLDER) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS ISS_CONV_RATE,

					  /* TCC đích VARCHAR(64) -> giữ nguyên chuỗi */
					  USER_DEFINE AS TCC,

					  CARD_ACCEPT_NAME_LOCATION,
					  CARD_ACCEPT_ID_CODE,

					  /* OF_YEAR đích VARCHAR(4) – nếu lo quá dài, có thể dùng SUBSTRING(...,1,4) */
					  PAYMENT_CODE AS OF_YEAR
					FROM isomessage
					WHERE SETTLE_DATE = :settleMmdd
					  AND SUBSTRING(proc_code,1,2) IN (:procPrefixes)
					  AND MTI IN (:mtis)
					  AND ACQ_ID <> :acqExclude
					  AND ((ISS_ID IS NULL) OR (ISS_ID IN (:issList) ))
					""";

			// chèn đoạn proc_code LIKE động
//			sql = sql.replace("%PROC_LIKE%", procLikeClause.toString());

			// xử lý điều kiện ISS_ID IN (:issList) nếu có danh sách

			String pretty = SqlLogUtils.renderSql(sql, params);
			log.info(pretty);
			// Thực thi
			return jdbc.update(sql, params);
		} catch (Exception e) {
			log.error("Execption " + e.getMessage(), e);
			return -1;
		}
	}

	public int insertFromSourceV_APG10_TRANS(LocalDate settleDate) {
		try {

			String settleMmdd = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(settleDate); // "0904" cho 04/09/2025

//			String sql = """
//					 INSERT INTO ISOMESSAGE_TMP_TURN(
//					  SEQ_NO, MTI, CARD_NO, PROC_CODE, ORIGINAL_DATE, TRACE_NO, REF_NO, ACQ_ID, ISS_ID,
//					  APPROVAL_CODE, RESPONSE_CODE, TERM_ID, ORIGINAL_DATA, REVERSED, TNX_STAMP, PACKAGER, AMOUNT,
//					  ACCOUNT_NO, LOCAL_TIME, LOCAL_DATE, SETTLE_DATE, MCC, CURRENCY_CODE, DEST_ACCOUNT,
//					  RECONCILE_TIME, TRANX_DATE, ADD_INFO, SERVICE_CODE, VAS_INFO, BEN_ID, ORIGRESPCODE, IBFT_INFO,
//					  ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, F4,
//					  F5, F6, F49, SETTLEMENT_CODE, SETTLEMENT_RATE,
//					  ISS_CONV_RATE, TCC, CARD_ACCEPT_NAME_LOCATION, CARD_ACCEPT_ID_CODE
//					)
//					SELECT
//					  SEQ_NO, MTI, IFNULL(TRIM(CARD_NO), ''), PROC_CODE, ORIGINAL_DATE, TRACE_NO, REF_NO, ACQ_ID, ISS_ID,
//					  APPROVAL_CODE, RESPONSE_CODE, TERM_ID, ORIGINAL_DATA, REVERSED, TNX_STAMP, PACKAGER, AMOUNT_SETTLEMENT,
//					  ACCOUNT_NO, LOCAL_TIME, LOCAL_DATE, SETTLE_DATE, MCC,
//					  CASE WHEN TRIM(CURRENCY_CODE)='USD' THEN 840
//					       WHEN TRIM(CURRENCY_CODE) IN ('VDD','VND') THEN 704
//					       ELSE CURRENCY_CODE END,
//					  DEST_ACCOUNT, RECONCILE_TIME, TRANX_DATE, ADD_INFO, SERVICE_CODE, VAS_INFO, BEN_ID, RESPONSE_CODE,
//					  CASE WHEN CHAR_LENGTH(TRIM(IBFT_INFO))>210 THEN SUBSTRING(TRIM(IBFT_INFO),1,210) ELSE TRIM(IBFT_INFO) END,
//					  ACQ_INST_COUNTRY_CODE, POS_ENTRY_MODE, POS_CONDITION_CODE, TRANX_REF, SERVICE_CODE,
//					  AMOUNT, AMOUNT_SETTLEMENT, AMOUNT_CARDHOLDER,
//					  CASE WHEN TRIM(CURRENCY_CODE)='USD' THEN 840
//					       WHEN TRIM(CURRENCY_CODE) IN ('VDD','VND') THEN 704
//					       ELSE CURRENCY_CODE END,
//					  CASE WHEN CURRENCY_CODE_SETTLEMENT IN ('   ', '.  ') THEN 704 ELSE CURRENCY_CODE_SETTLEMENT END,
//					  CONVERSION_RATE_SETTLEMENT, CONVERSION_RATE_CARDHOLDER, USER_DEFINE,
//					  CARD_ACCEPT_NAME_LOCATION, CARD_ACCEPT_ID_CODE
//					FROM V_APG10_TRANS
//					WHERE SETTLE_DATE_TIME = :settleMmdd
//					""";
			
			
			String sql = """
					INSERT INTO ISOMESSAGE_TMP_TURN(
					  SEQ_NO, MTI, CARD_NO, PROC_CODE, ORIGINAL_DATE, TRACE_NO, REF_NO, ACQ_ID, ISS_ID,
					  APPROVAL_CODE, RESPONSE_CODE, TERM_ID, ORIGINAL_DATA, REVERSED, TNX_STAMP, PACKAGER, AMOUNT,
					  ACCOUNT_NO, LOCAL_TIME, LOCAL_DATE, SETTLE_DATE, MCC, CURRENCY_CODE, DEST_ACCOUNT,
					  RECONCILE_TIME, TRANX_DATE, ADD_INFO, SERVICE_CODE, VAS_INFO, BEN_ID, ORIGRESPCODE, IBFT_INFO,
					  ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, F4,
					  F5, F6, F49, SETTLEMENT_CODE, SETTLEMENT_RATE,
					  ISS_CONV_RATE, TCC, CARD_ACCEPT_NAME_LOCATION, CARD_ACCEPT_ID_CODE
					)
					SELECT
					  SEQ_NO,
					  MTI,
					  IFNULL(TRIM(CARD_NO), '') AS CARD_NO,
					  PROC_CODE,
					  ORIGINAL_DATE,
					  TRACE_NO,
					  REF_NO,
					  ACQ_ID,
					  ISS_ID,
					  APPROVAL_CODE,
					  RESPONSE_CODE,
					  TERM_ID,
					  ORIGINAL_DATA,
					  REVERSED,
					  TNX_STAMP,
					  PACKAGER,

					  /* AMOUNT (đích BIGINT) – chỉ nhận khi là số, else NULL */
					  CASE
					    WHEN NULLIF(TRIM(AMOUNT_SETTLEMENT), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(AMOUNT_SETTLEMENT) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS AMOUNT,

					  ACCOUNT_NO,
					  LOCAL_TIME,
					  LOCAL_DATE,
					  SETTLE_DATE_TIME AS SETTLE_DATE,     -- bạn đang dùng SETTLE_DATE_TIME ở nguồn

					  /* MCC đích VARCHAR -> giữ chuỗi */
					  TRIM(MCC) AS MCC,

					  /* CURRENCY_CODE đích VARCHAR(9) */
					  CASE
					    WHEN UPPER(TRIM(CURRENCY_CODE)) = 'USD' THEN '840'
					    WHEN UPPER(TRIM(CURRENCY_CODE)) IN ('VDD','VND') THEN '704'
					    ELSE TRIM(CURRENCY_CODE)
					  END AS CURRENCY_CODE,

					  DEST_ACCOUNT,
					  RECONCILE_TIME,
					  TRANX_DATE,
					  ADD_INFO,
					  SERVICE_CODE,
					  VAS_INFO,
					  BEN_ID,
					  RESPONSE_CODE AS ORIGRESPCODE,

					  CASE
					    WHEN CHAR_LENGTH(TRIM(IBFT_INFO)) > 210 THEN SUBSTRING(TRIM(IBFT_INFO),1,210)
					    ELSE TRIM(IBFT_INFO)
					  END AS IBFT_INFO,

					  /* ACQ_COUNTRY (DECIMAL(28,2)) */
					  CASE
					    WHEN NULLIF(TRIM(ACQ_INST_COUNTRY_CODE), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(ACQ_INST_COUNTRY_CODE) AS DECIMAL(28,2))
					    ELSE NULL
					  END AS ACQ_COUNTRY,

					  /* POS_ENTRY_CODE (DECIMAL(28,2)) */
					  CASE
					    WHEN NULLIF(TRIM(POS_ENTRY_MODE), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(POS_ENTRY_MODE) AS DECIMAL(28,2))
					    ELSE NULL
					  END AS POS_ENTRY_CODE,

					  /* POS_CONDITION_CODE (DECIMAL(28,2)) */
					  CASE
					    WHEN NULLIF(TRIM(POS_CONDITION_CODE), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(POS_CONDITION_CODE) AS DECIMAL(28,2))
					    ELSE NULL
					  END AS POS_CONDITION_CODE,

					  TRANX_REF AS ADDRESPONSE,
					  SERVICE_CODE AS MVV,

					  /* F4/F5/F6 (DECIMAL(26,8)) */
					  CASE
					    WHEN NULLIF(TRIM(AMOUNT), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(AMOUNT) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS F4,

					  CASE
					    WHEN NULLIF(TRIM(AMOUNT_SETTLEMENT), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(AMOUNT_SETTLEMENT) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS F5,

					  CASE
					    WHEN NULLIF(TRIM(AMOUNT_CARDHOLDER), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(AMOUNT_CARDHOLDER) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS F6,

					  /* F49 (DECIMAL(18,6)) */
					  CASE
					    WHEN UPPER(TRIM(CURRENCY_CODE)) = 'USD' THEN 840
					    WHEN UPPER(TRIM(CURRENCY_CODE)) IN ('VDD','VND') THEN 704
					    WHEN NULLIF(TRIM(CURRENCY_CODE), '') REGEXP '^[0-9]+$'
					      THEN CAST(TRIM(CURRENCY_CODE) AS DECIMAL(18,6))
					    ELSE NULL
					  END AS F49,

					  /* SETTLEMENT_CODE (DECIMAL(18,6)) */
					  CASE
					    WHEN CURRENCY_CODE_SETTLEMENT IN ('   ', '.  ') THEN 704
					    WHEN NULLIF(TRIM(CURRENCY_CODE_SETTLEMENT), '') REGEXP '^[0-9]+$'
					      THEN CAST(TRIM(CURRENCY_CODE_SETTLEMENT) AS DECIMAL(18,6))
					    ELSE NULL
					  END AS SETTLEMENT_CODE,

					  /* SETTLEMENT_RATE (DECIMAL(26,8)) */
					  CASE
					    WHEN NULLIF(TRIM(CONVERSION_RATE_SETTLEMENT), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(CONVERSION_RATE_SETTLEMENT) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS SETTLEMENT_RATE,

					  /* ISS_CONV_RATE (DECIMAL(26,8)) */
					  CASE
					    WHEN NULLIF(TRIM(CONVERSION_RATE_CARDHOLDER), '') REGEXP '^-?[0-9]+(\\.[0-9]+)?$'
					      THEN CAST(TRIM(CONVERSION_RATE_CARDHOLDER) AS DECIMAL(26,8))
					    ELSE NULL
					  END AS ISS_CONV_RATE,

					  /* TCC đích VARCHAR(64) -> giữ nguyên chuỗi */
					  USER_DEFINE AS TCC,

					  CARD_ACCEPT_NAME_LOCATION,
					  CARD_ACCEPT_ID_CODE
					FROM V_APG10_TRANS
					WHERE SETTLE_DATE_TIME = :settleMmdd
					""";


			// chèn đoạn proc_code LIKE động
//			sql = sql.replace("%PROC_LIKE%", procLikeClause.toString());

			// xử lý điều kiện ISS_ID IN (:issList) nếu có danh sách
			Map<String, Object> params = new HashMap<>();
			params.put("settleMmdd", settleMmdd);
			String pretty = SqlLogUtils.renderSql(sql, params);
			log.info(pretty);
			// Thực thi
			return jdbc.update(sql, params);
		} catch (Exception e) {
			log.error("Execption " + e.getMessage(), e);
			return -1;
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void insertFromSourceISOMESSAGE_TMP_68_TO() {
		try {
			final String MODULE = "PROCESS_68_TIMEOUT";
			final LocalDate settlementDateMinus1 = LocalDate.now().minusDays(1);

			safeLog("BEGIN", "Start", MODULE, 0);

			// 1) Move sang ISOMESSAGE_TMP_68_TO
			String insertTo68To = """
					INSERT INTO ISOMESSAGE_TMP_68_TO
					SELECT t.*
					FROM ISOMESSAGE_TMP_TURN t
					WHERE t.RESPONSE_CODE = 68
					  AND EXISTS (
					    SELECT 1
					    FROM ISOMESSAGE_TMP_TURN u
					    WHERE u.MTI        = t.MTI
					      AND u.CARD_NO    = t.CARD_NO
					      AND u.TRACE_NO   = t.TRACE_NO
					      AND u.LOCAL_TIME = t.LOCAL_TIME
					      AND u.LOCAL_DATE = t.LOCAL_DATE
					      AND u.ACQ_ID     = t.ACQ_ID
					      AND u.TERM_ID    = t.TERM_ID
					      AND u.RESPONSE_CODE NOT IN (68, 0)
					  )
					""";
			int moved = jdbc.update(insertTo68To, java.util.Collections.emptyMap());
			safeLog("0", "Move to ISOMESSAGE_TMP_68_TO " + moved + " rows", MODULE, 0);

			if (moved > 0) {
				// 2) Xóa khỏi ISOMESSAGE_TMP_TURN
				String deleteFromTurn = """
						DELETE FROM ISOMESSAGE_TMP_TURN t
						WHERE t.RESPONSE_CODE = '68'
						  AND EXISTS (
						    SELECT 1
						    FROM ISOMESSAGE_TMP_TURN u
						    WHERE u.MTI        = t.MTI
						      AND u.CARD_NO    = t.CARD_NO
						      AND u.TRACE_NO   = t.TRACE_NO
						      AND u.LOCAL_TIME = t.LOCAL_TIME
						      AND u.LOCAL_DATE = t.LOCAL_DATE
						      AND u.ACQ_ID     = t.ACQ_ID
						      AND u.TERM_ID    = t.TERM_ID
						      AND u.RESPONSE_CODE NOT IN ('68', '00')
						  )
						""";
				
				int deleted = jdbc.update(deleteFromTurn, java.util.Collections.emptyMap());

				safeLog("0", "Delete From ISOMESSAGE_TMP_TURN " + deleted + " rows", MODULE, 0);

				// 3) Đổ sang ISOMESSAGE_TMP_68_TO_FULL (tính
				// F15_YEAR/LOCAL_DATE_YEAR/INSERT_DATE)
				String insertToFull = """
						INSERT INTO ISOMESSAGE_TMP_68_TO_FULL
						SELECT
						  t.*,
						  CASE
						    WHEN LENGTH(TRIM(t.SETTLE_DATE)) = 4 THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y'), TRIM(t.SETTLE_DATE))
						    WHEN LENGTH(TRIM(t.SETTLE_DATE)) = 8 THEN TRIM(t.SETTLE_DATE)
						    ELSE NULL
						  END AS F15_YEAR,
						  CASE
						    WHEN LENGTH(TRIM(t.LOCAL_DATE)) = 4 THEN CONCAT(DATE_FORMAT(CURDATE(), '%Y'), TRIM(t.LOCAL_DATE))
						    WHEN LENGTH(TRIM(t.LOCAL_DATE)) = 8 THEN TRIM(t.LOCAL_DATE)
						    ELSE NULL
						  END AS LOCAL_DATE_YEAR,
						  NOW() AS INSERT_DATE
						FROM ISOMESSAGE_TMP_68_TO t
						""";

				jdbc.update(insertToFull, java.util.Collections.emptyMap());

				safeLog("ALERT", "Co " + deleted + " Giao dich RC =68 co ma tuong minh duoc xu ly ||SETTLEMENT_DATE = "
						+ settlementDateMinus1, MODULE, 0);
			} else {
				safeLog("ALERT", "Khong Giao dich RC =68 co ma tuong minh||SETTLEMENT_DATE = " + settlementDateMinus1,
						MODULE, 0);
			}

			safeLog("END", "Finish", MODULE, 0);
		} catch (Exception e) {
			log.error("Execption " + e.getMessage(), e);
//			return -1;
		}
	}

	private void safeLog(String code, String detail, String module, Integer critical) {
		try {
			log(code, detail, module, critical); // REQUIRES_NEW bên ErrLogService để log không rollback
		} catch (Exception e) {
			// không để lỗi log ảnh hưởng tiến trình
			log.error("Exception : " + e.getMessage(), e);
		}
	}

//	  @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void log(String code, String detail, String module, Integer critical) {
		if (critical == null) {
			String sql = "INSERT INTO ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE) "
					+ "VALUES (NOW(), :code, :detail, :module)";
			var params = new MapSqlParameterSource().addValue("code", code).addValue("detail", detail)
					.addValue("module", module);
			jdbc.update(sql, params);
//	      jdbc.update("INSERT INTO ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE) VALUES (NOW(),?,?,?)",
//	          code, detail, module);
		} else {
			String sql = "INSERT INTO ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE,CRITICAL) "
					+ "VALUES (NOW(), :code, :detail, :module, :critical)";
			var params = new MapSqlParameterSource().addValue("code", code).addValue("detail", detail)
					.addValue("module", module).addValue("critical", critical); // có thể null
			jdbc.update(sql, params);
		}
	}
}
