package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;
import vn.napas.webrp.noti.SmsNotifier;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class Proc_CHECK_BACKEND_DOUBLE {
	private final NamedParameterJdbcTemplate jdbc;
	private final DbLoggerRepository dblog;
	private final SmsNotifier smsNoti;
	private final String module = "BACKEND_DOUBLE";

	/**
	 * Chạy toàn bộ CHECK_BACKEND_DOUBLE (TiDB) và gọi KTC ở bước cuối.
	 * 
	 * @param bcCardId thay cho GET_BCCARD_ID() (605608)
	 * @return thống kê đơn giản
	 */
	public Summary runCheckBackendDouble(int bcCardId) {
		// 1) BEGIN log
//        insertErr("0", "BEGIN", "BACKEND_DOUBLE", null);
		dblog.log("0", "Begin", module, 0);
		// 2) Bước MERGE -> INSERT IGNORE nghi vấn IBFT double (MODULE=BACKEND_DOUBLE)
		int insertedMerge = insertSuspectsIbft(bcCardId);
//		insertErr("0", "MERGE INTO ibft_double Success. Count: " + insertedMerge + " rows", "BACKEND_DOUBLE", null);
		dblog.log("0", "MERGE INTO ibft_double Success. Count: " + insertedMerge + " rows", module, 0);
		if (insertedMerge > 0) {
			// 3) Duyệt và xử lý từng nghi vấn trong ngày
			List<Suspect> suspects = findTodaySuspects(module);
			for (Suspect s : suspects) {
				int totalCnt = countTxn2Tables(s);
				if (totalCnt >= 2) {
					int uniqPanBinToAcc = countDistinctPanBinToAcc(s);
					if (uniqPanBinToAcc == 1) {
						Long stt = findMaxSttInSett(s);
						if (stt != null && 1 == insertBleAutoFromSett(stt) && 1 == deleteSettByStt(stt)) {

							updateIbftDoubleModule(s, "BACKEND_DOUBLE", "BACKEND_DOUBLE-D");

							dblog.info("STEP 5 - ORIGTRACE =" + s.trace + "; Local_time=" + s.localTime,
									"BACKEND_DOUBLE");
						}
					} else {
						// >= 2: không phải double -> ignore
						updateIbftDoubleModule(s, "BACKEND_DOUBLE", "BACKEND_DOUBLE-I");
					}
				}
			}

			// 4) Tổng kết trong ngày
			int pending = countByModuleToday(module);
			String detail = " Co " + insertedMerge + " gd trung TT, da loai bo tu dong: " + (insertedMerge - pending)
					+ " giao dich, vui long kiem tra";
//			insertErr("0", detail, "BACKEND_DOUBLE-C", null);
			dblog.logInfo(detail, "BACKEND_DOUBLE-C");
			if (pending == 0) {
//				insertErr("0", "OK", "BACKEND_DOUBLE", null);
				dblog.info("OK", module);
			} else {
//				insertErr("-1", "Co GD chua duoc loai tu dong. De nghi check", "BACKEND_DOUBLE_ALERT", 2);
				dblog.error("-1", "BACKEND_DOUBLE_ALERT", "Co GD chua duoc loai tu dong. De nghi check", 2);
			}

			// 5) SMS thông báo (giống store cũ)
			safeSms("BACKEND_DOUBLE_IBFT#0366155501;0988766330#" + detail);
		} else {
			insertErr("0", "OK", "BACKEND_DOUBLE", null);
		}

		// 6) Gọi KTC (store phụ) ở bước cuối như bản gốc
		KtcResult ktc = runKtc(bcCardId, true);

		return new Summary(insertedMerge, ktc.inserted, ktc.detail);
	}

	// ========================= Bước MERGE -> INSERT IGNORE (IBFT)
	// =========================

	/**
	 * Thay cho MERGE block của CHECK_BACKEND_DOUBLE (MODULE=BACKEND_DOUBLE). Lấy dữ
	 * liệu từ: - SHCLOG: (SETTLEMENT_DATE=T-2 or EDIT_DATE>=T-2), msgtype=210,
	 * pcode!=390000, resp=0, pcode in (...). - SHCLOG_SETT_IBFT:
	 * (SETTLEMENT_DATE=T-1), msgtype=210, pcode!=390000, resp=0. Gom nhóm theo
	 * (ACQUIRER, ORIGTRACE->TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, AMOUNT), HAVING
	 * COUNT(*)>1.
	 */
	private int insertSuspectsIbft(int bcCardId) {
		final String sql = """
				INSERT IGNORE INTO IBFT_DOUBLE
				  (TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, RUNTIME, MODULE, AMOUNT, ACQUIRER)
				SELECT B.TRACE, B.LOCAL_TIME, B.LOCAL_DATE, TRIM(B.TERMID), NOW(),
				       'BACKEND_DOUBLE', B.AMOUNT, B.ACQUIRER
				FROM (
				 SELECT ACQUIRER, ORIGTRACE AS TRACE, LOCAL_TIME, LOCAL_DATE, TRIM(TERMID) TERMID, AMOUNT
				 FROM (
				   /* SHCLOG: (T-2) or EDIT_DATE>=T-2, msg=210, resp=0, pcode!=390000, pcode in (910020,910000,912000,912020) */
				   SELECT ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, TRIM(TERMID) TERMID, SETTLEMENT_DATE, EDIT_DATE, PCODE, RESPCODE, AMOUNT
				   FROM SHCLOG
				   WHERE (SETTLEMENT_DATE = DATE_SUB(CURDATE(), INTERVAL 2 DAY)
				          OR EDIT_DATE >= DATE_SUB(CURDATE(), INTERVAL 2 DAY))
				     AND MSGTYPE = 210
				     AND PCODE <> 390000
				     AND RESPCODE = 0
				     AND PCODE IN (910020,910000,912000,912020)
				   UNION ALL
				   /* SHCLOG_SETT_IBFT: T-1, msg=210, resp=0, pcode!=390000 */
				   SELECT ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, TRIM(TERMID) TERMID, SETTLEMENT_DATE, EDIT_DATE, PCODE, RESPCODE, AMOUNT
				   FROM SHCLOG_SETT_IBFT
				   WHERE SETTLEMENT_DATE = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
				     AND MSGTYPE = 210
				     AND PCODE <> 390000
				     AND RESPCODE = 0
				 ) t
				 GROUP BY ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, TRIM(TERMID), AMOUNT
				 HAVING COUNT(*) > 1
				) B
				""";
		int rows = jdbc.update(sql, new MapSqlParameterSource().addValue("bc", bcCardId));
		return rows;
	}

	// ========================= Vòng lặp xử lý nghi vấn IBFT
	// =========================

	private List<Suspect> findTodaySuspects(String module) {
		final String sql = """
				SELECT LOCAL_DATE, TRACE, LOCAL_TIME, TRIM(TERMID) AS TERMID,
				       ACQUIRER, AMOUNT
				FROM IBFT_DOUBLE
				WHERE RUNTIME > CURDATE()
				  AND MODULE = :m
				""";
		return jdbc.query(sql, Map.of("m", module), new SuspectMapper());
	}

	/**
	 * Đếm tổng số bản ghi trong 2 bảng nguồn (PCODE prefix 42/91) khớp khóa nghi
	 * vấn
	 */
	private int countTxn2Tables(Suspect s) {
		final String cond = """
				LOCAL_DATE = :ld AND ORIGTRACE = :tr AND LOCAL_TIME = :lt
				AND TRIM(TERMID) = :tm AND ACQUIRER = :aq AND AMOUNT = :am
				AND SUBSTR(LPAD(CAST(PCODE AS CHAR),6,'0'),1,2) IN ('42','91')
				""";
		final String q1 = "SELECT COUNT(*) FROM SHCLOG WHERE " + cond;
		final String q2 = "SELECT COUNT(*) FROM SHCLOG_SETT_IBFT WHERE " + cond;

		MapSqlParameterSource p = new MapSqlParameterSource().addValue("ld", s.localDate).addValue("tr", s.trace)
				.addValue("lt", s.localTime).addValue("tm", s.termId).addValue("aq", s.acquirer)
				.addValue("am", s.amount);

		Integer c1 = jdbc.queryForObject(q1, p, Integer.class);
		Integer c2 = jdbc.queryForObject(q2, p, Integer.class);
		return (c1 == null ? 0 : c1) + (c2 == null ? 0 : c2);
	}

	/** Đếm số nhóm DISTINCT (PAN, BB_BIN, TOACC) của nghi vấn */
	private int countDistinctPanBinToAcc(Suspect s) {
		final String base = """
				SELECT TRIM(PAN) AS PAN,
				       BB_BIN,
				       TRIM(SUBSTR(ACCTNUM, INSTR(CONCAT(ACCTNUM,'|'),'|')+1)) AS TOACC
				FROM %s
				WHERE LOCAL_DATE=:ld AND ORIGTRACE=:tr AND LOCAL_TIME=:lt
				  AND TRIM(TERMID)=:tm AND ACQUIRER=:aq AND AMOUNT=:am
				  AND SUBSTR(LPAD(CAST(PCODE AS CHAR),6,'0'),1,2) IN ('42','91')
				GROUP BY TRIM(PAN), BB_BIN,
				         TRIM(SUBSTR(ACCTNUM, INSTR(CONCAT(ACCTNUM,'|'),'|')+1))
				""";

		final String q = "SELECT COUNT(*) FROM ( " + " SELECT CONCAT_WS('#',PAN,BB_BIN,TOACC) AS k " + " FROM ( "
				+ String.format(base, "SHCLOG") + " UNION ALL " + String.format(base, "SHCLOG_SETT_IBFT")
				+ " ) x GROUP BY PAN, BB_BIN, TOACC " + ") t";

		MapSqlParameterSource p = new MapSqlParameterSource().addValue("ld", s.localDate).addValue("tr", s.trace)
				.addValue("lt", s.localTime).addValue("tm", s.termId).addValue("aq", s.acquirer)
				.addValue("am", s.amount);

		Integer cnt = jdbc.queryForObject(q, p, Integer.class);
		return cnt == null ? 0 : cnt;
	}

	/** Lấy MAX(STT) trong SHCLOG_SETT_IBFT cho nghi vấn */
	private Long findMaxSttInSett(Suspect s) {
		final String sql = """
				SELECT MAX(STT) FROM SHCLOG_SETT_IBFT
				WHERE LOCAL_DATE=:ld AND ORIGTRACE=:tr AND LOCAL_TIME=:lt
				  AND TRIM(TERMID)=:tm AND ACQUIRER=:aq AND AMOUNT=:am
				  AND SUBSTR(LPAD(CAST(PCODE AS CHAR),6,'0'),1,2) IN ('42','91')
				""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("ld", s.localDate).addValue("tr", s.trace)
				.addValue("lt", s.localTime).addValue("tm", s.termId).addValue("aq", s.acquirer)
				.addValue("am", s.amount);
		return jdbc.queryForObject(sql, p, Long.class);
	}

	/**
	 * Insert bản ghi từ SHCLOG_SETT_IBFT sang SHCLOG_BLE_AUTO (mapping 1-1 các cột
	 * theo store gốc)
	 */
	private int insertBleAutoFromSett(Long stt) {
		final String sql = """
				INSERT INTO SHCLOG_BLE_AUTO(
				  SETTLEMENT_RATE, ISS_CONV_RATE, F49, F5, F4, F6, F15, TCC, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ,
				  FEE_SVF_ACQ, FEE_IRF_PAY_AT, FEE_SVF_PAY_AT, FEE_IRF_REC_AT, FEE_SVF_REC_AT, FEE_IRF_BEN, FEE_SVF_BEN,
				  TOKEN, RC, SETTLEMENT_CODE, ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV,
				  TERMID1, MSGTYPE, PAN, PCODE, AMOUNT, ACQ_CURRENCY_CODE, FEE, NEW_FEE, SETTLEMENT_AMOUNT, TRACE,
				  LOCAL_TIME, LOCAL_DATE, SETTLEMENT_DATE, ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE, AUTHNUM,
				  CH_CURRENCY_CODE, TERMID, REFNUM, ACCTNUM1, CARD_SEQNO, ISS_CURRENCY_CODE, CHIP_INDEX, TRANDATE, TRANTIME,
				  CARDPRODUCT, REVCODE, ORIGTRACE, ACCEPTORNAME, TERMLOC, LOAIGDREVESO, THAYDOI, CONFIG_FEE_ID, TGTP,
				  TGGUIGD, TGGUIQT, TGDDNV, TGXLNV, REAMOUNT, RAMOUNT, QAMOUNT, LDDNV, FAMOUNT, TGGUINV, TGGUIQTP, EDIT_DATE,
				  EDIT_USER, SML_VERIFY, ORIGISS, ORIGRESPCODE, STT, ISREV, PREAMOUNT, CAP_DATE, FEE_ISS, FEE_ACQ, INS_PCODE,
				  CONV_RATE, FEE_REC_AT, FEE_PAY_AT, FEE_REC_DF, FEE_PAY_DF, EDIT_DATE_INS, ENTITYID, TRANSACTION_AMOUNT,
				  CARDHOLDER_AMOUNT, CARDHOLDER_CONV_RATE, BB_BIN, FORWARD_INST, TRANSFEREE, SETT_CURRENCY_CODE, PRE_CARDHOLDER_AMOUNT,
				  REPAY_USD, CONV_RATE_ACQ, TERMID_ACQ, SHCERROR, MERCHANT_TYPE_ORIG, BB_ACCOUNT, FEE_SERVICE, SENDER_ACC,
				  BNB_ACC, SENDER_SWC, BNB_SWC, CONTENT_FUND, RESPCODE_GW, ACCTNUM, FROM_SML, ORIGINATOR, ORIG_ACQ, FEE_NOTE,
				  ONLY_SML, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP, FEE_KEY, ACQ_RQ, ISS_RQ, FROM_SYS, PCODE2,
				  BB_BIN_ORIG, TXNSRC, TXNDEST, SRC, DES, TRAN_CASE, PCODE_ORIG, RC_ISS_72, RC_ACQ_72, RC_ISS, RC_BEN,
				  RC_ACQ, NAPAS_DATE, NAPAS_EDIT_DATE, NAPAS_EDIT_DATE_INS, NAPAS_ND_DATE, REASON_EDIT
				)
				SELECT
				  SETTLEMENT_RATE, ISS_CONV_RATE, F49, F5, F4, F6, F15, TCC, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ,
				  FEE_SVF_ACQ, FEE_IRF_PAY_AT, FEE_SVF_PAY_AT, FEE_IRF_REC_AT, FEE_SVF_REC_AT, FEE_IRF_BEN, FEE_SVF_BEN,
				  TOKEN, RC, SETTLEMENT_CODE, ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV,
				  TERMID1, MSGTYPE, PAN, PCODE, AMOUNT, ACQ_CURRENCY_CODE, FEE, NEW_FEE, SETTLEMENT_AMOUNT, TRACE,
				  LOCAL_TIME, LOCAL_DATE, SETTLEMENT_DATE, ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE, AUTHNUM,
				  CH_CURRENCY_CODE, TERMID, REFNUM, ACCTNUM1, CARD_SEQNO, ISS_CURRENCY_CODE, CHIP_INDEX, TRANDATE, TRANTIME,
				  CARDPRODUCT, REVCODE, ORIGTRACE, ACCEPTORNAME, TERMLOC, LOAIGDREVESO, THAYDOI, CONFIG_FEE_ID, TGTP,
				  TGGUIGD, TGGUIQT, TGDDNV, TGXLNV, REAMOUNT, RAMOUNT, QAMOUNT, LDDNV, FAMOUNT, TGGUINV, TGGUIQTP, EDIT_DATE,
				  EDIT_USER, SML_VERIFY, ORIGISS, ORIGRESPCODE, STT, ISREV, PREAMOUNT, CAP_DATE, FEE_ISS, FEE_ACQ, INS_PCODE,
				  CONV_RATE, FEE_REC_AT, FEE_PAY_AT, FEE_REC_DF, FEE_PAY_DF, EDIT_DATE_INS, ENTITYID, TRANSACTION_AMOUNT,
				  CARDHOLDER_AMOUNT, CARDHOLDER_CONV_RATE, BB_BIN, FORWARD_INST, TRANSFEREE, SETT_CURRENCY_CODE, PRE_CARDHOLDER_AMOUNT,
				  REPAY_USD, CONV_RATE_ACQ, TERMID_ACQ, SHCERROR, MERCHANT_TYPE_ORIG, BB_ACCOUNT, FEE_SERVICE, SENDER_ACC,
				  BNB_ACC, SENDER_SWC, BNB_SWC, CONTENT_FUND, RESPCODE_GW, ACCTNUM, FROM_SML, ORIGINATOR, ORIG_ACQ, FEE_NOTE,
				  ONLY_SML, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP, FEE_KEY, ACQ_RQ, ISS_RQ, FROM_SYS, PCODE2,
				  BB_BIN_ORIG, TXNSRC, TXNDEST, SRC, DES, TRAN_CASE, PCODE_ORIG, RC_ISS_72, RC_ACQ_72, RC_ISS, RC_BEN,
				  RC_ACQ, NAPAS_DATE, NAPAS_EDIT_DATE, NAPAS_EDIT_DATE_INS, NAPAS_ND_DATE,
				  DATE_FORMAT(NOW(), '%Y%m%d%H%i%s')
				FROM SHCLOG_SETT_IBFT
				WHERE STT = :stt
				""";
		return jdbc.update(sql, Map.of("stt", stt));
	}

	private int deleteSettByStt(Long stt) {
		final String sql = "DELETE FROM SHCLOG_SETT_IBFT WHERE STT=:stt";
		return jdbc.update(sql, Map.of("stt", stt));
	}

	private void updateIbftDoubleModule(Suspect s, String fromModule, String toModule) {
		final String sql = """
				UPDATE IBFT_DOUBLE
				SET MODULE = :toM
				WHERE RUNTIME > CURDATE()
				  AND MODULE = :fromM
				  AND ACQUIRER = :aq
				  AND TRACE = :tr
				  AND LOCAL_TIME = :lt
				  AND LOCAL_DATE = :ld
				  AND TRIM(TERMID) = :tm
				  AND AMOUNT = :am
				""";
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("toM", toModule).addValue("fromM", fromModule)
				.addValue("aq", s.acquirer).addValue("tr", s.trace).addValue("lt", s.localTime)
				.addValue("ld", s.localDate).addValue("tm", s.termId).addValue("am", s.amount);
		jdbc.update(sql, p);
	}

	private int countByModuleToday(String module) {
		final String sql = """
				SELECT COUNT(*) FROM IBFT_DOUBLE
				WHERE RUNTIME > CURDATE()
				  AND MODULE = :m
				""";
		Integer cnt = jdbc.queryForObject(sql, Map.of("m", module), Integer.class);
		return cnt == null ? 0 : cnt;
	}

	// ========================= KTC (store phụ) =========================

	/** Kết quả KTC */
	@Value
	static class KtcResult {
		int inserted;
		String detail;
	}

	/**
	 * Port của WEBBC.CHECK_BACKEND_DOUBLE_KTC – chỉ phát hiện nghi vấn theo rule
	 * KTC và cảnh báo.
	 * 
	 * @param bcCardId GET_BCCARD_ID() tương đương
	 * @param sendSms  có gửi SMS hay không
	 */
	public KtcResult runKtc(int bcCardId, boolean sendSms) {
		insertErr("0", "BEGIN", "BACKEND_DOUBLE_KTC", null);

		int inserted = insertSuspectsKtc(bcCardId);
		final String detail;
		if (inserted > 0) {
			detail = " Co " + inserted + " gd trung khi check KTC, vui long kiem tra";
			insertErr("-1", detail, "BACKEND_DOUBLE_KTC", 2);
			if (sendSms) {
				safeSms("BACKEND_DOUBLE_KTC#0366155501;0988766330#" + detail);
			}
		} else {
			detail = "OK";
			insertErr("0", "OK", "BACKEND_DOUBLE_KTC", null);
		}
		return new KtcResult(inserted, detail);
	}

	/**
	 * INSERT IGNORE nghi vấn KTC (MODULE=BACKEND_DOUBLE_KTC) theo đúng rule store
	 * KTC
	 */
	private int insertSuspectsKtc(int bcCardId) {
		final String sql = """
				INSERT IGNORE INTO IBFT_DOUBLE
				  (TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, RUNTIME, MODULE, AMOUNT, ACQUIRER)
				SELECT B.TRACE, B.LOCAL_TIME, B.LOCAL_DATE, TRIM(B.TERMID), NOW(),
				       'BACKEND_DOUBLE_KTC', B.AMOUNT, B.ACQUIRER
				FROM (
				  SELECT ACQUIRER, ORIGTRACE AS TRACE, LOCAL_TIME, LOCAL_DATE, TRIM(TERMID) TERMID, AMOUNT
				  FROM (
				    /* SHCLOG: T-2 */
				    SELECT ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, TRIM(TERMID) TERMID, AMOUNT
				    FROM SHCLOG
				    WHERE SETTLEMENT_DATE = DATE_SUB(CURDATE(), INTERVAL 2 DAY)
				      AND MSGTYPE = 210
				      AND (
				        (
				          SUBSTR(LPAD(CAST(PCODE AS CHAR),6,'0'),1,2) IN ('42','91')
				          AND RESPCODE BETWEEN 1 AND 98 AND RESPCODE <> 68
				        )
				        OR
				        (
				          SUBSTR(LPAD(CAST(PCODE AS CHAR),6,'0'),1,2) IN ('00','01','30','35','40','41','48','94')
				          AND (RESPCODE BETWEEN 0 AND 98 OR (RESPCODE BETWEEN 0 AND 98 AND ISREV IS NOT NULL))
				        )
				      )
				      AND ISSUER_RP   NOT IN (220699,602907,605609,600005,600006,600007, :bc)
				      AND ACQUIRER_RP NOT IN (220699,605609, :bc)
				      AND COALESCE(BB_BIN, 0) NOT IN (764000,600011)
				      AND FEE_NOTE IS NULL
				      AND PAN NOT LIKE '970411%%'
				    UNION ALL
				    /* SHCLOG_SETT_IBFT: T-1 */
				    SELECT ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, TRIM(TERMID) TERMID, AMOUNT
				    FROM SHCLOG_SETT_IBFT
				    WHERE SETTLEMENT_DATE = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
				      AND MSGTYPE = 210
				      AND (
				        (
				          SUBSTR(LPAD(CAST(PCODE AS CHAR),6,'0'),1,2) IN ('42','91')
				          AND RESPCODE BETWEEN 1 AND 98 AND RESPCODE <> 68
				        )
				        OR
				        (
				          SUBSTR(LPAD(CAST(PCODE AS CHAR),6,'0'),1,2) IN ('00','01','30','35','40','41','48','94')
				          AND (RESPCODE BETWEEN 0 AND 98 OR (RESPCODE BETWEEN 0 AND 98 AND ISREV IS NOT NULL))
				        )
				      )
				      AND ISSUER_RP   NOT IN (220699,602907,605609,600005,600006,600007, :bc)
				      AND ACQUIRER_RP NOT IN (220699,605609, :bc)
				      AND FEE_NOTE IS NULL
				      AND PAN NOT LIKE '970411%%'
				  ) t
				  GROUP BY ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, TRIM(TERMID), AMOUNT
				  HAVING COUNT(*) > 1
				) B
				""";
		return jdbc.update(sql, Map.of("bc", bcCardId));
	}

	// ========================= Tiện ích & model =========================

	private void insertErr(String code, String detail, String module, Integer critical) {
		if (critical == null) {
			jdbc.update("INSERT INTO ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE) VALUES (NOW(), :c,:d,:m)",
					Map.of("c", code, "d", detail, "m", module));
		} else {
			jdbc.update(
					"INSERT INTO ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE,CRITICAL) VALUES (NOW(), :c,:d,:m,:cr)",
					Map.of("c", code, "d", detail, "m", module, "cr", critical));
		}
	}

	private void safeSms(String text) {
		try {
			smsNoti.notifyError("BACKEND_DOUBLE_IBFT", text);

		} catch (Exception ex) {
			log.warn("SMS send failed: {}", ex.getMessage());
//			insertErr("-99", "SMS send failed: " + ex.getMessage(), "BACKEND_DOUBLE_SMS", 1);
		}
	}

	@Value
	static class Suspect {
		LocalDate localDate;
		String trace;
		String localTime;
		String termId;
		String acquirer;
		BigDecimal amount;
	}

	static class SuspectMapper implements RowMapper<Suspect> {
		@Override
		public Suspect mapRow(ResultSet rs, int rowNum) throws SQLException {
			LocalDate ld = rs.getDate("LOCAL_DATE").toLocalDate();
			return new Suspect(ld, rs.getString("TRACE"), rs.getString("LOCAL_TIME"), rs.getString("TERMID"),
					rs.getString("ACQUIRER"), rs.getBigDecimal("AMOUNT"));
		}
	}

	@Value
	public static class Summary {
		int insertedIbft; // số bản ghi nghi vấn IBFT mới
		int insertedKtc; // số bản ghi nghi vấn KTC mới
		String ktcDetail; // thông điệp KTC
	}
}
