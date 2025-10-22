package vn.napas.webrp.database.converter;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.napas.webrp.database.repo.store.StoreUlts;

/**
 * Bộ hàm chuyển đổi từng trường từ ISOMESSAGE_TMP_TURN sang giá trị để insert
 * SHCLOG_SETT_IBFT. Mỗi trường một hàm, không phụ thuộc framework (thuần Java).
 */
@Component
public  class IbftConverters {
	@Autowired StoreUlts storeUlts;
	private IbftConverters() {
	}

	// ============== Dependencies (inject thực tế vào nơi gọi) ==============
	public interface MapIbftAcqIdService {
		/** Java tương đương DB function MAP_IBFT_ACQ_ID(acqIdText) */
		Long mapAcquirer(String acqIdText);
	}

	public interface IbtBinService {
		/** Java tương đương DB function GET_IBT_BIN(binOrBenId) */
		Long getIbtBin(String bin6OrBenId);
	}

	public interface ToNumberBnvService {
		/** Java tương đương DB function TO_NUMBER_BNV(benId) */
		Long toNumberBnv(String benId);
	}

	// =========================== Common utils ==============================
	private static final Pattern NUMERIC = Pattern.compile("^[0-9]+$");

	public static boolean isNumeric(String s) {
		return s != null && NUMERIC.matcher(s.trim()).matches();
	}

	public static Long toLong(String s) {
		if (!isNumeric(s))
			return null;
		try {
			return Long.parseLong(s.trim());
		} catch (Exception e) {
			return null;
		}
	}

	public static Integer toInt(String s) {
		if (!isNumeric(s))
			return null;
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return null;
		}
	}

	public static String nz(String s, String def) {
		return (s == null || s.isEmpty()) ? def : s;
	}

	public static String trim(String s) {
		return s == null ? null : s.trim();
	}

	/**
	 * Amount kiểu text có 2 chữ số thập phân ghép liền (vd "12345" -> 123.45).
	 * Oracle dùng SUBSTR(..., len-2).
	 */
	public static BigDecimal amountFromTail2(String amountRaw) {
		if (amountRaw == null || amountRaw.isEmpty())
			return BigDecimal.ZERO;
		String t = amountRaw.trim();
		if (!isNumeric(t))
			return BigDecimal.ZERO;
		if (t.length() <= 2) {
			// "5" => 0.05
			String withPad = String.format("%02d", Integer.parseInt(t));
			return new BigDecimal(withPad).movePointLeft(2);
		}
		String head = t.substring(0, t.length() - 2);
		return new BigDecimal(head);
	}

	/** F4/F5/F6 theo cùng công thức với Amount (loại 2 chữ số cuối). */
	public static Long numericFromTail2(String raw) {
		if (raw == null || raw.isEmpty())
			return 0L;
		String t = raw.trim();
		if (!isNumeric(t))
			return 0L;
		if (t.length() <= 2)
			return 0L;
		String head = t.substring(0, t.length() - 2);
		try {
			return Long.parseLong(head);
		} catch (Exception e) {
			return 0L;
		}
	}

	/**
	 * Tương đương NP_CONVERT_LOCAL_DATE(localStr, baseDate). Hỗ trợ: MMDD,
	 * YYYYMMDD, ISO yyyy-MM-dd, yyyy-MM-ddTHH:mm:ss
	 */
	public static LocalDate npConvertLocalDate(String localStr, LocalDate baseDate) {
		if (localStr == null || localStr.isEmpty())
			return null;
		String t = localStr.trim();
		try {
			if (t.length() == 4 && isNumeric(t)) { // MMDD
				int mm = Integer.parseInt(t.substring(0, 2));
				int dd = Integer.parseInt(t.substring(2, 4));
				return LocalDate.of(baseDate.getYear(), mm, dd);
			}
			if (t.length() == 8 && isNumeric(t)) { // YYYYMMDD
				int yyyy = Integer.parseInt(t.substring(0, 4));
				int mm = Integer.parseInt(t.substring(4, 6));
				int dd = Integer.parseInt(t.substring(6, 8));
				return LocalDate.of(yyyy, mm, dd);
			}
			// ISO date
			if (t.length() == 10 && t.charAt(4) == '-' && t.charAt(7) == '-') {
				return LocalDate.parse(t);
			}
			// ISO datetime -> lấy date
			if (t.contains("T")) {
				return LocalDateTime.parse(t).toLocalDate();
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	/** Lấy HHmmss từ timestamp giao dịch. */
	public static String hhmmssFromTimestamp(InstantTs ts) {
		if (ts == null || ts.instant == null)
			return null;
		return DateTimeFormatter.ofPattern("HHmmss").format(ZonedDateTime.ofInstant(ts.instant, ts.zone));
	}

	// Đại diện timestamp + zone (để định dạng giờ local chuẩn).
	public static final class InstantTs {
		public final Instant instant;
		public final ZoneId zone;

		public InstantTs(Instant instant, ZoneId zone) {
			this.instant = instant;
			this.zone = zone;
		}

		public static InstantTs of(Instant i, ZoneId z) {
			return new InstantTs(i, z);
		}
	}

	// ====================== Các HÀM CONVERT TỪNG TRƯỜNG =====================

	// --- Nhóm header/định danh ---
	public static Integer toDataId() {
		return 1;
	} // DATA_ID = 1

	public static Long toPpcode(String procCode) {
		return toLong(procCode);
	} // PPCODE

	public static String toMsgType() {
		return "210";
	} // MSGTYPE

	// --- PAN/PCODE/AMOUNT/TRACE/LOCAL_TIME/LOCAL_DATE/SETTLEMENT_DATE ---
	public static String toPan(String cardNo) {
		return cardNo;
	} // PAN = CARD_NO

	public static Long toPcode(String procCode) {
		return toLong(procCode);
	}

	public static BigDecimal toAmount(String amountRaw) {
		return amountFromTail2(amountRaw);
	}

	public static Long toTrace(String traceNo) {
		return toLong("2" + nz(traceNo, ""));
	} // TRACE = To_Number('2'||TRACE_NO)

	public static Integer toLocalTime(String localTime) {
		return toInt(localTime);
	}

	public static LocalDate toLocalDate(String localDate, LocalDate baseToday) {
		return npConvertLocalDate(localDate, baseToday);
	}

	public static LocalDate toSettlementDate(String settleDate, LocalDate baseToday) {
		return npConvertLocalDate(settleDate, baseToday);
	}

	// --- ACQUIRER/ISSUER ---
	public static Long toAcquirer(String acqId) {
		return toLong(acqId);
	}

	public static Long toIssuer(String acqId) {
		return toLong(acqId);
	}

	// --- RESPCODE (rule đặc biệt) ---
	public static Integer toRespCode(String benId, String destAccount, String serviceCode, String issId, String tcc) {
		if (Objects.equals(benId, "971133") && nz(destAccount, "").startsWith("NPDC"))
			return 68;
		if (Objects.equals(serviceCode, "QR_PUSH"))
			return 68;
		if (Objects.equals(benId, "971100") && Objects.equals(tcc, "99"))
			return 68;
		String iss = trim(issId);
		if (Objects.equals(iss, "980471") || Objects.equals(iss, "980472"))
			return 68;
		return 0;
	}

	public static Integer toMerchantType() {
		return 6011;
	}

	public static Long toMerchantTypeOrig(String mcc) {
		return toLong(mcc);
	}

	public static String toAuthNum(String approvalCode) {
		return approvalCode;
	}

	public static Integer toSettleCurrency() {
		return 704;
	}

	// --- TERMID/ADD_INFO/ACCTNUM/ISS_CURRENCY_CODE/ORIG* ---
	public static String toTermId(String termId) {
		return termId;
	}

	public static String toAddInfo(String addInfo) {
		return addInfo;
	}

	public static String toAcctNum(String accountNo, String destAccount) {
		return nz(accountNo, " ") + "|" + nz(destAccount, "");
	}

	public static Integer toIssCurrencyCode() {
		return 704;
	}

	public static Long toOrigTrace(String traceNo) {
		return toLong(traceNo);
	}

	public static Long toOrigIss(String acqId) {
		return toLong(acqId);
	}

	public static String toOrigRespCode() {
		return "97";
	}

	public static Integer toChCurrencyCode() {
		return 704;
	}

	// --- ACQUIRER_FE / ACQUIRER_RP / ISSUER_FE / ISSUER_RP (bao gồm rule đặc biệt)
	// ---
	public static Long toAcquirerLikeMapId(String acqId, MapIbftAcqIdService mapSvc) {
		Long acq = toLong(acqId);
		if (acq == null)
			return null;
		if (acq == 191919L)
			return 970459L;
		if (acq == 970415L)
			return 970489L;
		return Optional.ofNullable(mapSvc).map(s -> s.mapAcquirer(acqId)).orElse(null);
	}

	public static Long toAcquirerFe(String acqId, MapIbftAcqIdService mapSvc) {
		return toAcquirerLikeMapId(acqId, mapSvc);
	}

	public static Long toAcquirerRp(String issId, String acqId, MapIbftAcqIdService mapSvc) {
		String iss = trim(issId);
		if (Objects.equals(iss, "980471"))
			return 980471L;
		if (Objects.equals(iss, "980475"))
			return 980478L;
		return toAcquirerLikeMapId(acqId, mapSvc);
	}

	public static Long toIssuerFe(String acqId, MapIbftAcqIdService mapSvc) {
		return toAcquirerLikeMapId(acqId, mapSvc);
	}

	public static Long toIssuerRp(String issId, String acqId, MapIbftAcqIdService mapSvc) {
		String iss = trim(issId);
		if (Objects.equals(iss, "980471"))
			return 980471L;
		if (Objects.equals(iss, "980475"))
			return 980478L;
		return toAcquirerLikeMapId(acqId, mapSvc);
	}

	// --- PCODE2 theo TCC/SERVICE_CODE ---
	public static Integer toPcode2(String tcc, String serviceCode) {
		if (Objects.equals(tcc, "99"))
			return 930000;
		if (Objects.equals(tcc, "95"))
			return 950000;
		if (Objects.equals(serviceCode, "QR_PUSH"))
			return 890000;
		if (Objects.equals(tcc, "97"))
			return 720000;
		if (Objects.equals(tcc, "98"))
			return 730000;
		return 910000;
	}

	public static String toFromSys() {
		return "IBT";
	}

	// --- BB_BIN ---
	public static Long toBbBin(String issId, String benId, String procCode, String destAccount, IbtBinService ibtSvc) {
		String iss = trim(issId);
		if (Objects.equals(iss, "980472"))
			return 980471L;
		if (Objects.equals(iss, "980474"))
			return 980478L;

		boolean isBenMode = benId != null && ("912020".equals(procCode) || "910020".equals(procCode));
		if (isBenMode)
			return Optional.ofNullable(ibtSvc).map(s -> s.getIbtBin(benId)).orElse(null);
		String left6 = Optional.ofNullable(destAccount).filter(s -> s.length() >= 6).map(s -> s.substring(0, 6))
				.orElse(null);
		return Optional.ofNullable(ibtSvc).map(s -> s.getIbtBin(left6)).orElse(null);
	}

	// --- BB_BIN_ORIG ---
	public static Long toBbBinOrig(String benId, String issId, String procCode, String destAccount,
			IbtBinService ibtSvc, ToNumberBnvService bnvSvc, Tgtt20Checker tgtt20) {
		// TGTT 2.0
		if (tgtt20 != null && tgtt20.isTgtt20(benId)) {
			return Optional.ofNullable(bnvSvc).map(s -> s.toNumberBnv(benId)).orElse(null);
		}
		String iss = trim(issId);
		if (Objects.equals(iss, "980472") || Objects.equals(iss, "980474") || Objects.equals(iss, "980475")) {
			boolean isBenMode = benId != null && ("912020".equals(procCode) || "910020".equals(procCode));
			if (isBenMode)
				return Optional.ofNullable(ibtSvc).map(s -> s.getIbtBin(benId)).orElse(null);
			String left6 = Optional.ofNullable(destAccount).filter(s -> s.length() >= 6).map(s -> s.substring(0, 6))
					.orElse(null);
			return Optional.ofNullable(ibtSvc).map(s -> s.getIbtBin(left6)).orElse(null);
		}
		return Optional.ofNullable(bnvSvc).map(s -> s.toNumberBnv(benId)).orElse(null);
	}

	/**
	 * Kiểm tra BEN_ID có thuộc bảng TGTT_20 hay không. Bạn tự implement thực tế
	 * (DAO/cache).
	 */
	public interface Tgtt20Checker {
		boolean isTgtt20(String benId);
	}

	// --- CONTENT_FUND / TXNSRC / COUNTRY / POS / ADDRESPONSE / MVV ---
	public static String toContentFund(String ibftInfo) {
		return ibftInfo;
	}

	public static String toTxnSrc() {
		return "MTI=200";
	}

	public static String toAcqCountry(String acqCountry) {
		return acqCountry;
	}

	public static String toPosEntryCode(String code) {
		return code;
	}

	public static String toPosConditionCode(String code) {
		return code;
	}

	public static String toAddResponse(String addResponse) {
		return addResponse;
	}

	public static String toMvv(String mvv) {
		return mvv;
	}

	// --- F4/F5/F6/F49 & Settlement fields ---
	public static Long toF4(String f4) {
		return numericFromTail2(f4);
	}

	public static Long toF5(String f5) {
		return numericFromTail2(f5);
	}

	public static Long toF6(String f6) {
		return numericFromTail2(f6);
	}

	public static String toF49(String f49) {
		return f49;
	}

	public static String toSettlementCode(String v) {
		return v;
	}

	public static String toSettlementRate(String v) {
		return v;
	}

	public static String toIssConvRate(String v) {
		return v;
	}

	public static String toTcc(String tcc) {
		return tcc;
	}

	// --- REFNUM / TRANDATE / TRANTIME / ACCEPTOR / TERMLOC / F15 / PCODE_ORIG ---
	public static String toRefnum(String refNo) {
		return refNo;
	}

	public static LocalDate toTranDate(InstantTs ts) {
		if (ts == null || ts.instant == null)
			return null;
		return ZonedDateTime.ofInstant(ts.instant, ts.zone).toLocalDate();
	}

	public static String toTranTime(InstantTs ts) {
		return hhmmssFromTimestamp(ts);
	}

	public static String toAcceptorName(String s) {
		return s;
	}

	public static String toTermLoc(String s) {
		return s;
	}

	public static LocalDate toF15(String settleDate, LocalDate baseToday) {
		return npConvertLocalDate(settleDate, baseToday);
	}

	public static Long toPcodeOrig(String procCode) {
		return toLong(procCode);
	}

	// --- ACCOUNT_NO / DEST_ACCOUNT ---
	public static String toAccountNo(String s) {
		return s;
	}

	public static String toDestAccount(String s) {
		return s;
	}
}
