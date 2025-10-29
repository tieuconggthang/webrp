package vn.napas.webrp.database.converter;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.napas.webrp.database.repo.store.StoreUlts;
import vn.napas.webrp.report.service.ibft.IbftBankBinService;
import vn.napas.webrp.report.service.ibft.Tgtt20Service;

//java.text.SimpleDateFormat
/**
 * Bộ hàm chuyển đổi từng trường từ ISOMESSAGE_TMP_TURN sang giá trị để insert
 * SHCLOG_SETT_IBFT. Mỗi trường một hàm, không phụ thuộc framework (thuần Java).
 */
@Component
public class IbftConverters {
//	@Autowired
//	StoreUlts storeUlts;
	@Autowired
	IbftBankBinService ibftBankBinService;
	@Autowired
	Tgtt20Service tgtt20Service;

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

	public boolean isNumeric(String s) {
		return s != null && NUMERIC.matcher(s.trim()).matches();
	}

	public Long toLong(String s) {
		if (!isNumeric(s))
			return null;
		try {
			return Long.parseLong(s.trim());
		} catch (Exception e) {
			return null;
		}
	}

	public BigDecimal toBigDecimal(String s) {
		if (!isNumeric(s))
			return null;
		try {
			return new BigDecimal(Long.parseLong(s.trim()));
		} catch (Exception e) {
			return null;
		}
	}

	public Integer toInt(String s) {
		if (!isNumeric(s))
			return null;
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return null;
		}
	}

	public String nz(String s, String def) {
		return (s == null || s.isEmpty()) ? def : s;
	}

	public String trim(String s) {
		return s == null ? null : s.trim();
	}

	/**
	 * Amount kiểu text có 2 chữ số thập phân ghép liền (vd "12345" -> 123.45).
	 * Oracle dùng SUBSTR(..., len-2).
	 */
	public BigDecimal amountFromTail2(String amountRaw) {
		if (amountRaw == null || amountRaw.isEmpty())
			return BigDecimal.ZERO;
		String t = amountRaw.trim();
		if (!isNumeric(t))
			return BigDecimal.ZERO;
		if (t.length() <= 2) {
			// "5" => 0.05
			return BigDecimal.ZERO;
		}
		String head = t.substring(0, t.length() - 2);
		return new BigDecimal(head);
	}

	/** F4/F5/F6 theo cùng công thức với Amount (loại 2 chữ số cuối). */
	public Long numericFromTail2(String raw) {
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
	public LocalDate npConvertLocalDate(String localStr, LocalDate baseDate) {
		if (localStr == null || localStr.isEmpty())
			return null;
		return StoreUlts.NP_CONVERT_LOCAL_DATE(localStr, baseDate);

	}

	/** Lấy HHmmss từ timestamp giao dịch. */
	public String hhmmssFromTimestamp(Date ts) {
		if (ts == null)
			return null;
		DateFormat df = new SimpleDateFormat("HHmmss");

		return df.format(ts);
	}

	// Đại diện timestamp + zone (để định dạng giờ local chuẩn).
	public final class InstantTs {
		public final Instant instant;
		public final ZoneId zone;

		public InstantTs(Instant instant, ZoneId zone) {
			this.instant = instant;
			this.zone = zone;
		}

		public InstantTs of(Instant i, ZoneId z) {
			return new InstantTs(i, z);
		}
	}

	// ====================== Các HÀM CONVERT TỪNG TRƯỜNG =====================

	// --- Nhóm header/định danh ---
	public BigDecimal toDataId() {
		return new BigDecimal(1);
	} // DATA_ID = 1

	public BigDecimal toPpcode(String procCode) {
		return toBigDecimal(procCode);
	} // PPCODE

	public BigDecimal toMsgType() {
		return new BigDecimal(210);
	} // MSGTYPE

	// --- PAN/PCODE/AMOUNT/TRACE/LOCAL_TIME/LOCAL_DATE/SETTLEMENT_DATE ---
	public String toPan(String cardNo) {
		return cardNo;
	} // PAN = CARD_NO

	public BigDecimal toPcode(String procCode) {
		return toBigDecimal(procCode);
	}

	public BigDecimal toAmount(String amountRaw) {
		return amountFromTail2(amountRaw);
	}

	public BigDecimal toTrace(String traceNo) {
		return toBigDecimal("2" + nz(traceNo, ""));
	} // TRACE = To_Number('2'||TRACE_NO)

	public BigDecimal toLocalTime(String localTime) {
		return toBigDecimal(localTime);
	}

	public Date toLocalDate(String localDate, LocalDate baseToday) {
		LocalDate localDateTime = npConvertLocalDate(localDate, baseToday);
		ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
		Date returnDate = Date.from(localDateTime.atStartOfDay(zone).toInstant());
		return returnDate;
	}

	public Date toSettlementDate(String settleDate, LocalDate baseToday) {
		LocalDate localDateTime = npConvertLocalDate(settleDate, baseToday);
		ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
		Date returnDate = Date.from(localDateTime.atStartOfDay(zone).toInstant());
		return returnDate;
	}

	// --- ACQUIRER/ISSUER ---
	public BigDecimal toAcquirer(String acqId) {
		return toBigDecimal(acqId);
	}

	public BigDecimal toIssuer(String acqId) {
		return toBigDecimal(acqId);
	}

	// --- RESPCODE (rule đặc biệt) ---
	public Integer toRespCode(String benId, String destAccount, String serviceCode, String issId, String tcc) {
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

	public BigDecimal toMerchantType() {
		return new BigDecimal(6011);
	}

	public BigDecimal toMerchantTypeOrig(String mcc) {
		return toBigDecimal(mcc);
	}

	public String toAuthNum(String approvalCode) {
		return approvalCode;
	}

	public BigDecimal toSettleCurrency() {
		return new BigDecimal(704);
	}

	// --- TERMID/ADD_INFO/ACCTNUM/ISS_CURRENCY_CODE/ORIG* ---
	public String toTermId(String termId) {
		return termId;
	}

	public String toAddInfo(String addInfo) {
		return addInfo;
	}

	public String toAcctNum(String accountNo, String destAccount) {
		return nz(accountNo, "") + "|" + nz(destAccount, "");
	}

	public BigDecimal toIssCurrencyCode() {
		return new BigDecimal(704);
	}

	public BigDecimal toOrigTrace(String traceNo) {
		return toBigDecimal(traceNo);
	}

	public Long toOrigIss(String acqId) {
		return toLong(acqId);
	}

	public BigDecimal toOrigRespCode() {
		return new BigDecimal(97);
	}

	public BigDecimal toChCurrencyCode() {
		return new BigDecimal(704);
	}

	// --- ACQUIRER_FE / ACQUIRER_RP / ISSUER_FE / ISSUER_RP (bao gồm rule đặc biệt)
	// ---
	public Long toAcquirerLikeMapId(String acqId) {
		Long acq = toLong(acqId);
		if (acq == null)
			return null;
		if (acq == 191919L)
			return 970459L;
		if (acq == 970415L)
			return 970489L;
		return Long.parseLong(StoreUlts.MAP_IBFT_ACQ_ID(acqId) + "");
//		return Optional.ofNullable(mapSvc).map(s -> s.mapAcquirer(acqId)).orElse(null);
	}

	public BigDecimal toAcquirerFe(String acqId) {
		return new BigDecimal(toAcquirerLikeMapId(acqId));
	}

	public BigDecimal toAcquirerRp(String issId, String acqId) {
		String iss = trim(issId);
		if (Objects.equals(iss, "980471"))
			return new BigDecimal(980471);
		if (Objects.equals(iss, "980475"))
			return new BigDecimal(980478);
		return new BigDecimal(toAcquirerLikeMapId(acqId));
	}

	public BigDecimal toIssuerFe(String acqId) {
		return new BigDecimal(toAcquirerLikeMapId(acqId));
	}

	public BigDecimal toIssuerRp(String issId, String acqId) {
//		String iss = trim(issId);
//		if (Objects.equals(iss, "980471"))
//			return 980471L;
//		if (Objects.equals(iss, "980475"))
//			return 980478L;
//		return toAcquirerLikeMapId(acqId);
		return toAcquirerRp(issId, acqId);
	}

	// --- PCODE2 theo TCC/SERVICE_CODE ---
	public Integer toPcode2(String tcc, String serviceCode) {
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

	public String toFromSys() {
		return "IBT";
	}

	// --- BB_BIN ---
	public Long toBbBin(String issId, String benId, String procCode, String destAccount) {
		String iss = trim(issId);
		if (Objects.equals(iss, "980472"))
			return 980471L;
		if (Objects.equals(iss, "980474"))
			return 980478L;

		boolean isBenMode = benId != null && ("912020".equals(procCode) || "910020".equals(procCode));
		if (isBenMode)
			return Long.parseLong(ibftBankBinService.getIbtBin(benId) + "");
		String left6 = Optional.ofNullable(destAccount).filter(s -> s.length() >= 6).map(s -> s.substring(0, 6))
				.orElse(null);
		return Long.parseLong(ibftBankBinService.getIbtBin(left6) + "");
	}

	// --- BB_BIN_ORIG ---
	public BigDecimal toBbBinOrig(String benId, String issId, String procCode, String destAccount) {
		try {
			// TGTT 2.0
//		if (tgtt20 != null && tgtt20.isTgtt20(benId)) {
//			return Optional.ofNullable(bnvSvc).map(s -> s.toNumberBnv(benId)).orElse(null);
//		}
			if (tgtt20Service.checkTGTTExist(benId))
				return new BigDecimal(StoreUlts.to_number_bnv(benId));
			String iss = trim(issId);
			if (Objects.equals(iss, "980472") || Objects.equals(iss, "980474") || Objects.equals(iss, "980475")) {
				boolean isBenMode = benId != null && ("912020".equals(procCode) || "910020".equals(procCode));
				if (isBenMode)
					return new BigDecimal(ibftBankBinService.getIbtBin(benId));
				String left6 = Optional.ofNullable(destAccount).filter(s -> s.length() >= 6).map(s -> s.substring(0, 6))
						.orElse(null);
				return new BigDecimal(ibftBankBinService.getIbtBin(left6));
			}

			return new BigDecimal(StoreUlts.to_number_bnv(benId));
		} catch (Exception e) {
			
			return null;
		}
	}

	/**
	 * Kiểm tra BEN_ID có thuộc bảng TGTT_20 hay không. Bạn tự implement thực tế
	 * (DAO/cache).
	 */
	public interface Tgtt20Checker {
		boolean isTgtt20(String benId);
	}

	// --- CONTENT_FUND / TXNSRC / COUNTRY / POS / ADDRESPONSE / MVV ---
	public String toContentFund(String ibftInfo) {
		return ibftInfo;
	}

	public String toTxnSrc() {
		return "MTI=200";
	}

	public String toAcqCountry(String acqCountry) {
		return acqCountry;
	}

	public String toPosEntryCode(String code) {
		return code;
	}

	public String toPosConditionCode(String code) {
		return code;
	}

	public String toAddResponse(String addResponse) {
		return addResponse;
	}

	public String toMvv(String mvv) {
		return mvv;
	}

	// --- F4/F5/F6/F49 & Settlement fields ---
	public BigDecimal toF4(String f4) {
		return new BigDecimal(numericFromTail2(f4));
	}

	public BigDecimal toF5(String f5) {
		return new BigDecimal(numericFromTail2(f5));
	}

	public BigDecimal toF6(String f6) {
		return new BigDecimal(numericFromTail2(f6));
	}

	public String toF49(String f49) {
		return f49;
	}

	public String toSettlementCode(String v) {
		return v;
	}

	public String toSettlementRate(String v) {
		return v;
	}

	public String toIssConvRate(String v) {
		return v;
	}

	public String toTcc(String tcc) {
		return tcc;
	}

	// --- REFNUM / TRANDATE / TRANTIME / ACCEPTOR / TERMLOC / F15 / PCODE_ORIG ---
	public String toRefnum(String refNo) {
		return refNo;
	}

	public Date toTranDate(Date ts) {
		return ts;
	}

	public BigDecimal toTranTime(Date transTime) {
		return new BigDecimal(hhmmssFromTimestamp(transTime));
	}

	public String toAcceptorName(String s) {
		return s;
	}

	public String toTermLoc(String s) {
		return s;
	}

	public Date toF15(String settleDate, LocalDate baseToday) {
		LocalDate localDateTime = StoreUlts.NP_CONVERT_LOCAL_DATE(settleDate, baseToday);
		ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
		Date returnDate = Date.from(localDateTime.atStartOfDay(zone).toInstant());
		return returnDate;
//		return npConvertLocalDate(settleDate, baseToday);
	}

	public BigDecimal toPcodeOrig(String procCode) {
		return toBigDecimal(procCode);
	}

	// --- ACCOUNT_NO / DEST_ACCOUNT ---
	public String toAccountNo(String s) {
		return s;
	}

	public String toDestAccount(String s) {
		return s;
	}
}
