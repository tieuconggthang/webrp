package vn.napas.webrp.database.converter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.napas.webrp.database.converter.IbftConverters.IbtBinService;
import vn.napas.webrp.database.converter.IbftConverters.InstantTs;
import vn.napas.webrp.database.converter.IbftConverters.MapIbftAcqIdService;
import vn.napas.webrp.database.converter.IbftConverters.Tgtt20Checker;
import vn.napas.webrp.database.converter.IbftConverters.ToNumberBnvService;
import vn.napas.webrp.database.entities.IsomessageTmpTurn;
import vn.napas.webrp.database.entities.ShclogSettIbft;

@Component
public class IbftMapper {

	@Autowired
	IbftConverters ibftConverters;

	private IbftMapper() {
	}

	/**
	 * Tạo bản ghi SHCLOG_SETT_IBFT từ ISOMESSAGE_TMP_TURN theo rule Oracle gốc.
	 * 
	 * @param b      nguồn ISOMESSAGE_TMP_TURN
	 * @param today  base date (thường =
	 *               LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")))
	 * @param zone   múi giờ để tách HHmmss từ TNX_STAMP
	 * @param mapSvc dịch vụ tương đương MAP_IBFT_ACQ_ID
	 * @param ibtSvc dịch vụ tương đương GET_IBT_BIN
	 * @param bnvSvc dịch vụ tương đương TO_NUMBER_BNV
	 * @param tgtt20 checker TGTT_20
	 */
	private ShclogSettIbft privCreateShcLogSettIfgtFromIsomessageTmpTurn(IsomessageTmpTurn b
//            LocalDate today,
//            ZoneId zone,
//            MapIbftAcqIdService mapSvc,
//            IbtBinService ibtSvc,
//            ToNumberBnvService bnvSvc,
//            Tgtt20Checker tgtt20
	) {
		LocalDate today = LocalDate.now();
		ShclogSettIbft a = new ShclogSettIbft();

		// --- Header / fixed fields ---
		a.setDataId(ibftConverters.toDataId()); // 1
		a.setPpcode(ibftConverters.toPpcode(b.getProcCode())); // PPCODE
		a.setMsgtype(ibftConverters.toMsgType()); // '210'

		// --- Core mapping ---
		a.setPan(ibftConverters.toPan(b.getCardNo()));
		a.setPcode(ibftConverters.toPcode(b.getProcCode()));
		a.setAmount(new BigDecimal(b.getAmount())); // BigDecimal (đã cắt 2 số cuối)
		a.setAcqCurrencyCode(new BigDecimal(704));
		a.setTrace(ibftConverters.toTrace(b.getTraceNo()));
		a.setLocalTime(ibftConverters.toLocalTime(b.getLocalTime()));
		a.setLocalDate(ibftConverters.toLocalDate(b.getLocalDate(), today));
		a.setSettlementDate(ibftConverters.toSettlementDate(b.getSettleDate(), today));

		a.setAcquirer(ibftConverters.toAcquirer(b.getAcqId()));
		a.setIssuer(ibftConverters.toIssuer(b.getAcqId()));

		a.setRespcode(new BigDecimal(ibftConverters.toRespCode(b.getBenId(), b.getDestAccount(), b.getServiceCode(),
				b.getIssId(), b.getTcc())));

		a.setMerchantType(ibftConverters.toMerchantType()); // 6011
		a.setMerchantTypeOrig(ibftConverters.toMerchantTypeOrig(b.getMcc()));
		a.setAuthnum(ibftConverters.toAuthNum(b.getApprovalCode()));
		a.setSettCurrencyCode(ibftConverters.toSettleCurrency()); // 704

		a.setTermid(ibftConverters.toTermId(b.getTermId()));
		a.setAddInfo(ibftConverters.toAddInfo(b.getAddInfo()));
		a.setAcctnum(ibftConverters.toAcctNum(b.getAccountNo(), b.getDestAccount()));

		a.setIssCurrencyCode(ibftConverters.toIssCurrencyCode()); // 704
		a.setOrigtrace(ibftConverters.toOrigTrace(b.getTraceNo()));
		a.setOrigiss(b.getAcqId());
		a.setOrigrespcode(ibftConverters.toOrigRespCode()); // '97'
		a.setChCurrencyCode(ibftConverters.toChCurrencyCode()); // 704

		a.setAcquirerFe(ibftConverters.toAcquirerFe(b.getAcqId()));
		a.setAcquirerRp(ibftConverters.toAcquirerRp(b.getIssId(), b.getAcqId()));
		a.setIssuerFe(ibftConverters.toIssuerFe(b.getAcqId()));
		a.setIssuerRp(ibftConverters.toIssuerRp(b.getIssId(), b.getAcqId()));

		a.setPcode2(new BigDecimal(ibftConverters.toPcode2(b.getTcc(), b.getServiceCode())));
		a.setFromSys(ibftConverters.toFromSys()); // "IBT"

		a.setBbBin(new BigDecimal(
				ibftConverters.toBbBin(b.getIssId(), b.getBenId(), b.getProcCode(), b.getDestAccount())));
		a.setBbBinOrig(ibftConverters.toBbBinOrig(b.getBenId(), b.getIssId(), b.getProcCode(), b.getDestAccount()));

		a.setContentFund(ibftConverters.toContentFund(b.getIbftInfo()));
//        a.setTxnSrc(IbftConverters.toTxnSrc()); 
		a.setTxnsrc(ibftConverters.toTxnSrc());// "MTI=200"
		a.setAcqCountry(b.getAcqCountry());
		a.setPosEntryCode(b.getPosEntryCode());
		a.setPosConditionCode(b.getPosConditionCode());
		a.setAddresponse(ibftConverters.toAddResponse(b.getAddresponse()));
		a.setMvv(ibftConverters.toMvv(b.getMvv()));

		a.setF4(ibftConverters.toF4(b.getF4() + ""));
		a.setF5(ibftConverters.toF5(b.getF5() + ""));
		a.setF6(ibftConverters.toF6(b.getF6() + ""));
		a.setF49(b.getF49());

		a.setSettlementCode(b.getSettlementCode());
		a.setSettlementRate(b.getSettlementRate());
		a.setIssConvRate(b.getIssConvRate());
		a.setTcc(ibftConverters.toTcc(b.getTcc()));

		a.setRefnum(ibftConverters.toRefnum(b.getRefNo()));

		// TNX_STAMP -> trandate, trantime
//        var ts = (b.getTnxStamp() == null)
//                ? null
//                : InstantTs.of(b.getTnxStamp().toInstant(), zone != null ? zone : ZoneId.systemDefault());
		a.setTrandate(ibftConverters.toTranDate(b.getTnxStamp()));
		a.setTrantime(ibftConverters.toTranTime(b.getTnxStamp()));

		a.setAcceptorname(ibftConverters.toAcceptorName(b.getCardAcceptNameLocation()));
		a.setTermloc(ibftConverters.toTermLoc(b.getCardAcceptIdCode()));

		// F15 = NP_CONVERT_LOCAL_DATE(SETTLE_DATE, today)
		a.setF15(ibftConverters.toF15(b.getSettleDate(), today));
		a.setPcodeOrig(ibftConverters.toPcodeOrig(b.getProcCode()));

		a.setAccountNo(ibftConverters.toAccountNo(b.getAccountNo()));
		a.setDestAccount(ibftConverters.toDestAccount(b.getDestAccount()));

		return a;
	}

	/** Overload tiện dụng: mặc định zone VN. */
	public ShclogSettIbft createShcLogSettIfgtFromIsomessageTmpTurn(IsomessageTmpTurn b) {
		try {
			return privCreateShcLogSettIfgtFromIsomessageTmpTurn(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
