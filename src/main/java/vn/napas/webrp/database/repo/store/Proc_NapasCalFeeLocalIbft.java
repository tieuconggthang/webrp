package vn.napas.webrp.database.repo.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;
import vn.napas.webrp.noti.SmsNotifier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Repository
@RequiredArgsConstructor
public class Proc_NapasCalFeeLocalIbft {

	private final NamedParameterJdbcTemplate jdbc;
	private final DbLoggerRepository dbLog;
	private final SmsNotifier smsNotifier;
	private final String module = "NAPAS_CAL_FEE_LOCAL_IBFT";

	private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static final String SQL_UPDATE_FEE = """
			/* == Ported from RPT.NAPAS_CAL_FEE_LOCAL_IBFT == */
			UPDATE SHCLOG_SETT_IBFT A
			JOIN GR_FEE_CONFIG_NEW B
			  ON A.FEE_KEY = B.FEE_KEY
			 AND STR_TO_DATE(:TuTG, '%d/%m/%Y') BETWEEN B.VALID_FROM AND B.VALID_TO
			SET
			  A.FEE_NOTE = B.FEE_NOTE,
			  A.FEE_PAY_AT =
			    CASE
			      WHEN (A.Issuer_Rp = 605609 OR A.Acquirer_Rp = 605609)
			           AND SUBSTR(LPAD(CAST(A.PCODE AS CHAR),6,'0'),1,2) <> '43'
			        THEN CASE
			               WHEN B.FEE_PAY_TYPE = 'PHANG'      THEN B.FEE_PAY_AT
			               WHEN B.FEE_PAY_TYPE = 'PHAN_TRAM'  THEN B.FEE_PAY_AT * A.AMOUNT
			               ELSE A.FEE_PAY_AT
			             END
			      ELSE A.FEE_PAY_AT
			    END,
			  A.FEE_REC_AT =
			    CASE
			      WHEN (A.Issuer_Rp = 605609 OR A.Acquirer_Rp = 605609)
			           AND SUBSTR(LPAD(CAST(A.PCODE AS CHAR),6,'0'),1,2) <> '43'
			        THEN CASE
			               WHEN B.FEE_REC_TYPE = 'PHANG'      THEN B.FEE_REC_AT
			               WHEN B.FEE_PAY_TYPE = 'PHAN_TRAM'  THEN B.FEE_REC_AT * A.AMOUNT
			               ELSE A.FEE_REC_AT
			             END
			      ELSE A.FEE_REC_AT
			    END,
			  A.FEE_IRF_ISS_NO_VAT =
			    CASE
			      WHEN A.MSGTYPE = '430' AND B.FEE_ISS_TYPE = 'PHAN_TRAM'
			        THEN B.FEE_IRF_ISS * A.PREAMOUNT
			      WHEN A.MSGTYPE = '210' AND B.FEE_ISS_TYPE = 'PHANG'
			        THEN B.FEE_IRF_ISS
			      WHEN A.MSGTYPE = '210' AND B.FEE_ISS_TYPE = 'PHAN_TRAM'
			        THEN B.FEE_IRF_ISS * A.AMOUNT
			      WHEN A.MSGTYPE = '210' AND B.FEE_ISS_TYPE = 'CBFT_V2'
			        THEN B.FEE_IRF_ISS * A.AMOUNT + B.FEE_ISS_EXT
			      WHEN A.MSGTYPE = '210' AND B.FEE_ISS_TYPE = 'PHANG_PHAN_TRAM'
			        THEN B.FEE_IRF_ISS * A.AMOUNT + B.FEE_ISS_EXT
			      ELSE A.FEE_IRF_ISS_NO_VAT
			    END,
			  A.FEE_SVF_ISS_NO_VAT =
			    CASE
			      WHEN A.MSGTYPE = '430' THEN 0
			      WHEN A.MSGTYPE = '210' AND B.FEE_ISS_TYPE = 'PHANG'
			        THEN B.FEE_SVF_ISS
			      WHEN A.MSGTYPE = '210' AND B.FEE_ISS_TYPE = 'PHAN_TRAM'
			        THEN B.FEE_SVF_ISS * A.AMOUNT
			      WHEN A.MSGTYPE = '210' AND B.FEE_ISS_TYPE = 'PHANG_PHAN_TRAM'
			        THEN B.FEE_SVF_ISS * A.AMOUNT + B.FEE_ISS_EXT
			      ELSE A.FEE_SVF_ISS_NO_VAT
			    END,
			  A.FEE_IRF_ACQ_NO_VAT =
			    CASE
			      WHEN A.MSGTYPE = '430' AND B.FEE_ACQ_TYPE = 'PHAN_TRAM'
			        THEN B.FEE_IRF_ACQ * A.PREAMOUNT
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHANG'
			        THEN B.FEE_IRF_ACQ
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHAN_TRAM'
			        THEN B.FEE_IRF_ACQ * (
			               CASE WHEN A.ACQUIRER_RP = 605608
			                    THEN A.SETTLEMENT_AMOUNT
			                    ELSE CASE WHEN A.MSGTYPE='430' THEN A.PREAMOUNT ELSE A.AMOUNT END
			               END )
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM' AND A.Issuer_Rp = 602907
			        THEN B.FEE_IRF_ACQ
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM' AND A.Issuer_Rp <> 602907
			        THEN B.FEE_IRF_ACQ * (
			               CASE WHEN A.ACQUIRER_RP = 605608
			                    THEN A.SETTLEMENT_AMOUNT
			                    ELSE CASE WHEN A.MSGTYPE='430' THEN A.PREAMOUNT ELSE A.AMOUNT END
			               END ) + B.FEE_ACQ_EXT
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM_USD'
			        THEN B.FEE_IRF_ACQ * (
			               CASE WHEN A.ACQUIRER_RP = 605608
			                    THEN A.SETTLEMENT_AMOUNT
			                    ELSE CASE WHEN A.MSGTYPE='430' THEN A.PREAMOUNT ELSE A.AMOUNT END
			               END ) + B.FEE_ACQ_EXT / A.CARDHOLDER_CONV_RATE
			      ELSE A.FEE_IRF_ACQ_NO_VAT
			    END,
			  A.FEE_SVF_ACQ_NO_VAT =
			    CASE
			      WHEN A.MSGTYPE = '430' THEN 0
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHANG'
			        THEN B.FEE_SVF_ACQ
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHAN_TRAM'
			        THEN B.FEE_SVF_ACQ * (
			               CASE WHEN A.ACQUIRER_RP = 605608
			                    THEN A.SETTLEMENT_AMOUNT
			                    ELSE CASE WHEN A.MSGTYPE='430' THEN A.PREAMOUNT ELSE A.AMOUNT END
			               END )
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM'
			        THEN B.FEE_SVF_ACQ * (
			               CASE WHEN A.ACQUIRER_RP = 605608
			                    THEN A.SETTLEMENT_AMOUNT
			                    ELSE CASE WHEN A.MSGTYPE='430' THEN A.PREAMOUNT ELSE A.AMOUNT END
			               END ) + B.FEE_ACQ_EXT
			      WHEN A.MSGTYPE = '210' AND B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM_USD'
			        THEN B.FEE_SVF_ACQ * (
			               CASE WHEN A.ACQUIRER_RP = 605608
			                    THEN A.SETTLEMENT_AMOUNT
			                    ELSE CASE WHEN A.MSGTYPE='430' THEN A.PREAMOUNT ELSE A.AMOUNT END
			               END ) + B.FEE_ACQ_EXT / A.CARDHOLDER_CONV_RATE
			      ELSE A.FEE_IRF_ACQ_NO_VAT
			    END,
			  A.FEE_IRF_BEN_NO_VAT =
			    CASE
			      WHEN B.FEE_BEN_TYPE = 'PHANG'     THEN B.FEE_IRF_BEN
			      WHEN B.FEE_BEN_TYPE = 'PHAN_TRAM' THEN B.FEE_IRF_BEN * A.AMOUNT
			      WHEN B.FEE_BEN_TYPE = 'CBFT_V2'   THEN B.FEE_IRF_BEN * A.AMOUNT + B.FEE_BEN_EXT
			      ELSE A.FEE_IRF_BEN_NO_VAT
			    END,
			  A.FEE_SVF_BEN_NO_VAT =
			    CASE
			      WHEN B.FEE_BEN_TYPE = 'PHANG'     THEN B.FEE_SVF_BEN
			      WHEN B.FEE_BEN_TYPE = 'PHAN_TRAM' THEN B.FEE_SVF_BEN * A.AMOUNT
			      ELSE A.FEE_SVF_BEN_NO_VAT
			    END
			WHERE
			  A.SETTLEMENT_DATE = STR_TO_DATE(:TuTG, '%d/%m/%Y')
			  AND (
			       (A.MSGTYPE = '210' AND A.RESPCODE = 0 AND A.ISREV IS NULL)
			       OR (A.MSGTYPE = '430' AND A.RESPCODE = 114)
			      )
			  AND A.FEE_KEY IS NOT NULL
			  AND (A.FEE_NOTE IS NULL OR A.FEE_NOTE NOT LIKE 'HACH TOAN DIEU CHINH%%')
			  AND (
			       (
			         ( SUBSTR(LPAD(CAST(A.PCODE AS CHAR),6,'0'),1,2) IN ('00','01','30','35','40','41','42','43','48','20')
			           OR (SUBSTR(LPAD(CAST(A.PCODE AS CHAR),6,'0'),1,2) = '94' AND A.MERCHANT_TYPE = 6011)
			         )
			         AND IFNULL(A.FROM_SYS,'IST') LIKE '%%IST%%'
			       )
			       OR (
			         A.FROM_SYS IS NOT NULL
			         AND SUBSTR(LPAD(CAST(A.PCODE AS CHAR),6,'0'),1,2) IN ('01','42','91')
			       )
			     )
			  AND SUBSTR(A.FEE_KEY,1,3) <> 'ERR';
			""";
	
	
	private static final String SQL_RP_LOG_FEE = """
	        INSERT INTO RP_LOG_FEE (LOG_FEE_ID, TUTG, DENTG, TGTINHPHI, USERTP, numrow, PROCESSTIME)
	        VALUES ( /* thay thế Getkhoacuabang bằng AUTO_INCREMENT hoặc seq app-side */
	                 :logId, :TuTG, :TuTG, NOW(), 'KT3', :numrow, :processtimeSec )
	        """;

	/**
	 * Chạy tính phí cho ngày d1. - d1: LocalDate (ngày đối soát), format dd/MM/yyyy
	 * để bind vào SQL.
	 */
	@Transactional
	public int calFeeLocalIbft(LocalDate d1) {
		final String tuTg = d1.format(DDMMYYYY);
		final Instant begin = Instant.now();

		// 1) Log START
		dbLog.begin(module, "START");

		int affectedRows = 0;
		try {
			// 2) UPDATE phí (port từ MERGE WHEN MATCHED THEN UPDATE)
			MapSqlParameterSource params = new MapSqlParameterSource().addValue("TuTG", tuTg);
			affectedRows = jdbc.update(SQL_UPDATE_FEE, params);

			// 3) Log SUCCESS
			Instant end = Instant.now();
			long seconds = Duration.between(begin, end).toSeconds();
			String detail = String.format("Tinh phi thanh cong cho %d giao dich thanh cong, ngay : %s.%n"
					+ "Tong thoi gian tinh phi: %d giay.", affectedRows, tuTg, seconds);
//            jdbc.update(SQL_LOG_SUCCESS, new MapSqlParameterSource().addValue("detail", detail));
			dbLog.end(module, detail);
			// 4) RP_LOG_FEE (LOG_FEE_ID: bạn dùng AUTO_INCREMENT hoặc sinh ID từ app)
			long logId = System.currentTimeMillis(); // ví dụ tạm
			MapSqlParameterSource p2 = new MapSqlParameterSource().addValue("logId", logId).addValue("TuTG", tuTg)
					.addValue("numrow", affectedRows).addValue("processtimeSec", seconds);
			jdbc.update(SQL_RP_LOG_FEE, p2);

			return affectedRows;
		} catch (Exception ex) {
			// 5) Log ERROR
			String detail = "NAPAS_CAL_FEE_LOCAL_IBFT - POS:MERGE/UPDATE, Err detail: " + ex.getMessage();
//			jdbc.update(SQL_LOG_ERROR, new MapSqlParameterSource().addValue("ecode", -1).addValue("detail", detail));
//			// (Tùy chọn) gọi SMS/Notifier
//			throw ex; // để transaction rollback (nếu muốn giữ SUCCESS/LOG_FEE ngoài tx thì tách
//						// @Transactional)
			log.error("Excetpion " + ex.getMessage(), ex);
			dbLog.error(module, detail);
			smsNotifier.notifyError(tuTg, detail);
			return -1;
		}
	}
}
