package vn.napas.webrp.report.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import vn.napas.webrp.report.dto.EcomSearchForDisputeRequest;
import vn.napas.webrp.report.util.ParamUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DisputeRepo {
	private final NamedParameterJdbcTemplate np;

	// TS/CRT
	public List<Map<String, Object>> tsCrt(EcomSearchForDisputeRequest r) {
		String sql = """
				SELECT
				          A.transaction_code,
				          DATE_FORMAT(A.transaction_date,'%d/%m/%Y %H:%i:%s') AS transaction_date,
				          A.adjust_tranx_code,
				          A.update_date,
				          A.amount, A.adjust_amount, A.currency, A.merchant_code, A.merchant_name,
				          A.transaction_ref, A.transaction_info,
				          A.pan_mask AS card_number,
				          A.card_holder_name, A.card_expired_date, A.bank_id,
				          A.issuer_bank_code, A.issuer_bank_name, A.acq_bank_code,
				          A.payment_gateway, A.response_code, A.transaction_type, A.payment_method,
				          A.f11_trace, A.f12_local_time, A.f13_local_date, A.f15_settle_date,
				          A.f32_acquirer, A.f41_card_acceptor_id
				        FROM v_tsol_ecom A
				        WHERE A.transaction_date BETWEEN :fromTs AND :toTs
				          AND (:vtranxID  IS NULL OR A.transaction_code     = :vtranxID)
				          AND (:viss      IS NULL OR A.issuer_bank_code     = :viss)
				          AND (:vtranxREF IS NULL OR A.transaction_ref      = :vtranxREF)
				          AND (:vPanMask  IS NULL OR A.pan_mask             = :vPanMask)
				          AND (:vBankID   IS NULL OR A.bank_id              = :vBankID)
				          AND (:vacq      IS NULL OR A.merchant_code        = :vacq)
				        ORDER BY A.transaction_date, A.transaction_code,
				                 CASE A.transaction_type
				                   WHEN 'purchase'   THEN 0
				                   WHEN 'adjust'     THEN 1
				                   WHEN 'reconcile'  THEN 2
				                   WHEN 'reverse'    THEN 3
				                   WHEN 'chargeback' THEN 4
				                   ELSE 9 END
				""";

		Map<String, Object> p = new HashMap<>();
		p.put("fromTs", ParamUtils.toTs(r.getVfrom_date(), r.getVfrom_time()));
		p.put("toTs", ParamUtils.toTs(r.getVto_date(), r.getVto_time()));
		p.put("vtranxID", ParamUtils.nz(r.getVtranxID()));
		p.put("viss", ParamUtils.nz(r.getViss()));
		p.put("vtranxREF", ParamUtils.nz(r.getVtranxREF()));
		p.put("vPanMask", ParamUtils.nz(ParamUtils.maskPan(r.getVPan())));
		p.put("vBankID", ParamUtils.nz(r.getVBankID()));
		p.put("vacq", ParamUtils.nz(r.getVacq()));
		return np.queryForList(sql, p);
	}

	// TS/VRF
	public List<Map<String, Object>> tsVrf(EcomSearchForDisputeRequest r) {
		String sql = """
				SELECT TRANSACTION_CODE, TRANSACTION_DATE,
				               ADJUST_TRANX_CODE, UPDATE_DATE, AMOUNT, ADJUST_AMOUNT, CURRENCY,
				               MERCHANT_CODE, MERCHANT_NAME, TRANSACTION_REF, TRANSACTION_INFO,
				               CARD_NUMBER AS CARD_NUMBER, CARD_HOLDER_NAME, CARD_EXPIRED_DATE, BANK_ID,
				               ISSUER_BANK_CODE, ISSUER_BANK_NAME, ACQ_BANK_CODE, PAYMENT_GATEWAY,
				               RESPONSE_CODE, TRANSACTION_TYPE, PAYMENT_METHOD,
				               F11_TRACE, F12_Local_time, F13_Local_date, F15_Settle_DATE,
				               F32_Acquirer, F41_card_acceptor_id,
				               ID_TS, MA_TS, AMOUNT_TS, NOTE_YC, DATE_YC, TSV_YC, KSV_YC,
				               CANCEL_DATE, CANCEL_USER, MA_GHINO, DATE_GHINO, USER_GHINO, KS_GHINO,
				               MA_TL, DATE_TL, TSV_TL, KSV_TL
				        FROM ecom_gdts
				        WHERE  KSV_DUYET_YC IS NULL AND (:viss IS NULL OR ISSUER_BANK_CODE = :viss)
				       AND (:vTSV IS NULL OR TSV_YC = :vTSV)
				        ORDER BY ID_TS
				""";

		Map<String, Object> p = Map.of("viss", ParamUtils.nz(r.getViss()), "vTSV", ParamUtils.nz(r.getVTSV()));
		return np.queryForList(sql, p);
	}

	// TS/EDT
	public List<Map<String, Object>> tsEdt(EcomSearchForDisputeRequest r) {
		String sql = """
				SELECT TRANSACTION_CODE, TRANSACTION_DATE, ADJUST_TRANX_CODE, UPDATE_DATE, AMOUNT, ADJUST_AMOUNT, CURRENCY,
				               MERCHANT_CODE, MERCHANT_NAME, TRANSACTION_REF, TRANSACTION_INFO, pan_mask AS CARD_NUMBER,
				               CARD_HOLDER_NAME, CARD_EXPIRED_DATE, BANK_ID, ISSUER_BANK_CODE, ISSUER_BANK_NAME, ACQ_BANK_CODE,
				               PAYMENT_GATEWAY, RESPONSE_CODE, TRANSACTION_TYPE, PAYMENT_METHOD,
				               F11_TRACE, F12_Local_time, F13_Local_date, F15_Settle_DATE, F32_Acquirer, F41_card_acceptor_id,
				               ID_TS, MA_TS, AMOUNT_TS, NOTE_YC, DATE_YC, TSV_YC, KSV_YC, CANCEL_DATE, CANCEL_USER,
				               MA_GHINO, DATE_GHINO, USER_GHINO, KS_GHINO, MA_TL, DATE_TL, TSV_TL, KSV_TL
				        FROM ecom_gdts
				        WHERE KSV_DUYET_YC = 0
				          AND (:viss IS NULL OR ISSUER_BANK_CODE = :viss)
				          AND (:vTSV IS NULL OR TSV_YC = :vTSV)
				        ORDER BY ID_TS
				""";
		Map<String, Object> p = Map.of("viss", ParamUtils.nz(r.getViss()), "vTSV", ParamUtils.nz(r.getVTSV()));
		return np.queryForList(sql, p);
	}

	// TS/CCL
	public List<Map<String, Object>> tsCcl(EcomSearchForDisputeRequest r) {
		String sql = """
				SELECT TRANSACTION_CODE, TRANSACTION_DATE, ADJUST_TRANX_CODE, UPDATE_DATE, AMOUNT, ADJUST_AMOUNT, CURRENCY,
				               MERCHANT_CODE, MERCHANT_NAME, TRANSACTION_REF, TRANSACTION_INFO, pan_mask AS CARD_NUMBER,
				               CARD_HOLDER_NAME, CARD_EXPIRED_DATE, BANK_ID, ISSUER_BANK_CODE, ISSUER_BANK_NAME, ACQ_BANK_CODE,
				               PAYMENT_GATEWAY, RESPONSE_CODE, TRANSACTION_TYPE, PAYMENT_METHOD,
				               F11_TRACE, F12_Local_time, F13_Local_date, F15_Settle_DATE, F32_Acquirer, F41_card_acceptor_id,
				               ID_TS, MA_TS, AMOUNT_TS, NOTE_YC, FILE_YC, DATE_YC, TSV_YC, KSV_YC,
				               CANCEL_DATE, CANCEL_USER, MA_GHINO, DATE_GHINO, USER_GHINO, KS_GHINO, MA_TL, DATE_TL, TSV_TL, KSV_TL
				        FROM ecom_gdts
				        WHERE KSV_DUYET_YC = 1
				          AND MA_TL IS NULL AND KSV_DUYET_TL IS NULL
				          AND CANCEL_DATE IS NULL AND CANCEL_USER IS NULL
				          AND (:vTSV IS NULL OR KSV_YC = :vTSV)
				          AND DATE_YC BETWEEN :fromDay AND :toDay
				          AND ( :vtranxID IS NULL OR TRANSACTION_CODE = :vtranxID OR ADJUST_TRANX_CODE = :vtranxID )
				          AND ( :vtranxREF IS NULL OR TRANSACTION_REF = :vtranxREF )
				          AND ( :vPanMask IS NULL OR pan_mask = :vPanMask OR CARD_NUMBER IS NULL )
				          AND ( :vBankID IS NULL OR TRIM(BANK_ID) = :vBankID OR BANK_ID IS NULL )
				          AND ( :viss   IS NULL OR TRIM(ISSUER_BANK_CODE) = :viss )
				          AND ( :vacq   IS NULL OR TRIM(MERCHANT_CODE)    = :vacq )
				        ORDER BY ID_TS
				""";
		Map<String, Object> p = new HashMap<>();
		p.put("fromDay", ParamUtils.dayStart(r.getVfrom_date()));
		p.put("toDay", ParamUtils.dayEnd(r.getVto_date()));
		p.put("vTSV", ParamUtils.nz(r.getVTSV()));
		p.put("vtranxID", ParamUtils.nz(r.getVtranxID()));
		p.put("vtranxREF", ParamUtils.nz(r.getVtranxREF()));
		p.put("vPanMask", ParamUtils.nz(ParamUtils.maskPan(r.getVPan())));
		p.put("vBankID", ParamUtils.nz(r.getVBankID()));
		p.put("viss", ParamUtils.nz(r.getViss()));
		p.put("vacq", ParamUtils.nz(r.getVacq()));
		return np.queryForList(sql, p);
	}

	// TS/RCV
	public List<Map<String, Object>> tsRcv(EcomSearchForDisputeRequest r) {
		String sql = """
				SELECT TRANSACTION_CODE, TRANSACTION_DATE, ADJUST_TRANX_CODE, UPDATE_DATE, AMOUNT, ADJUST_AMOUNT, CURRENCY,
				               MERCHANT_CODE, MERCHANT_NAME, TRANSACTION_REF, TRANSACTION_INFO, pan_mask AS CARD_NUMBER,
				               CARD_HOLDER_NAME, CARD_EXPIRED_DATE, BANK_ID, ISSUER_BANK_CODE, ISSUER_BANK_NAME, ACQ_BANK_CODE,
				               PAYMENT_GATEWAY, RESPONSE_CODE, TRANSACTION_TYPE, PAYMENT_METHOD,
				               F11_TRACE, F12_Local_time, F13_Local_date, F15_Settle_DATE, F32_Acquirer, F41_card_acceptor_id,
				               ID_TS, MA_TS, AMOUNT_TS, NOTE_YC, FILE_YC, DATE_YC, TSV_YC, KSV_YC,
				               CANCEL_DATE, CANCEL_USER, MA_GHINO, DATE_GHINO, USER_GHINO, KS_GHINO,
				               MA_TL, NOTE_TL, FILE_TL, AMOUNT_RETURN, DATE_TL, TSV_TL, KSV_TL
				        FROM ecom_gdts
				        WHERE KSV_DUYET_YC = 1
				          AND MA_TL IS NOT NULL AND KSV_DUYET_TL = 1
				          AND IS_RECEIVE IS NULL
				          AND (:viss IS NULL OR ISSUER_BANK_CODE = :viss)
				          AND DATE_TL BETWEEN :fromDay AND :toDay
				          AND (
				               (:vma_ts = '000' AND (MA_TS IN (101,102,103,104,105,106,109) OR MA_TS IS NULL)) OR
				               ((:vma_ts <> '000' OR :vma_ts IS NULL) AND MA_TS = :vma_ts)
				          )
				          AND (
				               (:vma_tl = '000' AND (MA_TL IN (301,302,303,304,309,401,402,403,404,409) OR MA_TL IS NULL)) OR
				               ((:vma_tl <> '000' OR :vma_tl IS NULL) AND MA_TL = :vma_tl)
				          )
				        ORDER BY ID_TS
				""";
		Map<String, Object> p = new HashMap<>();
		p.put("fromDay", ParamUtils.dayStart(r.getVfrom_date()));
		p.put("toDay", ParamUtils.dayEnd(r.getVto_date()));
		p.put("viss", ParamUtils.nz(r.getViss()));
		p.put("vma_ts", ParamUtils.nz(r.getVma_ts()));
		p.put("vma_tl", ParamUtils.nz(r.getVma_tl()));
		return np.queryForList(sql, p);
	}

	// SRCH/ALL
	public List<Map<String, Object>> srchAll(EcomSearchForDisputeRequest r) {
		String sql = """
				SELECT ID_TS,
				               DATE_FORMAT(DATE_YC, '%d/%m/%Y %H:%i:%s') AS DATE_YC,
				               TSV_YC, MA_TS, AMOUNT_TS, NOTE_YC, FILE_YC,
				               DATE_FORMAT(DATE_TL, '%d/%m/%Y %H:%i:%s') AS DATE_TL,
				               MA_TL, AMOUNT_RETURN, NOTE_TL, FILE_TL,
				               CASE WHEN CANCEL_DATE IS NULL THEN '' ELSE 'GD da huy' END AS CANCEL_STATUS,
				               MA_GHINO, AMOUNT_GHINO, DATE_GHINO,
				               DATE_FORMAT(TRANSACTION_DATE, '%d/%m/%Y %H:%i:%s') AS TRANSACTION_DATE,
				               TRANSACTION_CODE, TRANSACTION_REF, TRANSACTION_INFO,
				               pan_mask AS CARD_NUMBER, BANK_ID,
				               TRANSACTION_TYPE, AMOUNT, ADJUST_AMOUNT, ACQ_BANK_CODE, MERCHANT_CODE, PAYMENT_GATEWAY
				        FROM ecom_gdts
				        WHERE DATE_YC BETWEEN :fromDay AND :toDay
				          AND (:vPanMask IS NULL OR pan_mask = :vPanMask OR CARD_NUMBER IS NULL)
				          AND (:viss IS NULL OR TRIM(ISSUER_BANK_CODE) = :viss OR ISSUER_BANK_CODE IS NULL)
				          AND (:vtranxID IS NULL OR TRIM(TRANSACTION_CODE) = :vtranxID)
				          AND (
				               (:vma_ts = '000' AND (MA_TS IN (101,102,103,104,105,106,109) OR MA_TS IS NULL)) OR
				               ((:vma_ts <> '000' OR :vma_ts IS NULL) AND MA_TS = :vma_ts)
				          )
				          AND (
				               (:vma_tl = '000' AND (MA_TL IN (301,302,303,304,309,401,402,403,404,409) OR MA_TL IS NULL)) OR
				               ((:vma_tl <> '000' OR :vma_tl IS NULL) AND MA_TL = :vma_tl)
				          )
				        ORDER BY ID_TS
				""";
		Map<String, Object> p = new HashMap<>();
		p.put("fromDay", ParamUtils.dayStart(r.getVfrom_date()));
		p.put("toDay", ParamUtils.dayEnd(r.getVto_date()));
		p.put("vPanMask", ParamUtils.nz(ParamUtils.maskPan(r.getVPan())));
		p.put("viss", ParamUtils.nz(r.getViss()));
		p.put("vtranxID", ParamUtils.nz(r.getVtranxID()));
		p.put("vma_ts", ParamUtils.nz(r.getVma_ts()));
		p.put("vma_tl", ParamUtils.nz(r.getVma_tl()));
		return np.queryForList(sql, p);
	}
}
