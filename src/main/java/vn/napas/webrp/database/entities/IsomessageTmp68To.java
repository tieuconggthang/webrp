package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the ISOMESSAGE_TMP_68_TO database table.
 * 
 */
@Entity
@Table(name="ISOMESSAGE_TMP_68_TO")
@NamedQuery(name="IsomessageTmp68To.findAll", query="SELECT i FROM IsomessageTmp68To i")
public class IsomessageTmp68To implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="tidb_id")
	private long tidbId;

	@Column(name="ACCOUNT_NO")
	private String accountNo;

	@Column(name="ACQ_COUNTRY")
	private BigDecimal acqCountry;

	@Column(name="ACQ_ID")
	private String acqId;

	@Column(name="ADD_INFO")
	private String addInfo;

	private String addresponse;

	private BigInteger amount;

	@Column(name="AMOUNT_CARDHOLDER")
	private BigDecimal amountCardholder;

	@Column(name="AMOUNT_SETTLEMENT")
	private BigDecimal amountSettlement;

	@Column(name="APPROVAL_CODE")
	private String approvalCode;

	@Column(name="BEN_ID")
	private String benId;

	@Column(name="CARD_ACCEPT_ID_CODE")
	private String cardAcceptIdCode;

	@Column(name="CARD_ACCEPT_NAME_LOCATION")
	private String cardAcceptNameLocation;

	@Column(name="CARD_NO")
	private String cardNo;

	@Column(name="CONVERSION_RATE_CARDHOLDER")
	private String conversionRateCardholder;

	@Column(name="CONVERSION_RATE_SETTLEMENT")
	private String conversionRateSettlement;

	@Column(name="CURRENCY_CODE")
	private String currencyCode;

	@Column(name="CURRENCY_CODE_CARDHOLDER")
	private String currencyCodeCardholder;

	@Column(name="CURRENCY_CODE_SETTLEMENT")
	private String currencyCodeSettlement;

	@Column(name="DEST_ACCOUNT")
	private String destAccount;

	private BigDecimal f4;

	private int f49;

	private BigDecimal f5;

	private BigDecimal f6;

	@Column(name="IBFT_INFO")
	private String ibftInfo;

	private int isrev;

	@Column(name="ISS_CONV_RATE")
	private BigDecimal issConvRate;

	@Column(name="ISS_ID")
	private String issId;

	@Column(name="LOCAL_DATE")
	private String localDate;

	@Column(name="LOCAL_TIME")
	private String localTime;

	private String mcc;

	private String mti;

	private String mvv;

	@Column(name="OF_YEAR")
	private String ofYear;

	@Column(name="ORIGINAL_DATA")
	private String originalData;

	@Column(name="ORIGINAL_DATE")
	private String originalDate;

	private String origrespcode;

	private String packager;

	@Column(name="POS_CONDITION_CODE")
	private BigDecimal posConditionCode;

	@Column(name="POS_ENTRY_CODE")
	private BigDecimal posEntryCode;

	@Column(name="PROC_CODE")
	private String procCode;

	@Column(name="RC_210")
	private String rc210;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="RECONCILE_TIME")
	private Date reconcileTime;

	@Column(name="REF_NO")
	private String refNo;

	@Column(name="RESPONSE_CODE")
	private String responseCode;

	private String reversed;

	@Column(name="SEQ_NO")
	private BigInteger seqNo;

	@Column(name="SERVICE_CODE")
	private String serviceCode;

	@Column(name="SETTLE_DATE")
	private String settleDate;

	@Column(name="SETTLEMENT_CODE")
	private int settlementCode;

	@Column(name="SETTLEMENT_RATE")
	private BigDecimal settlementRate;

	private String tcc;

	@Column(name="TERM_ID")
	private String termId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TNX_STAMP")
	private Date tnxStamp;

	@Column(name="TRACE_NO")
	private String traceNo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TRANX_DATE")
	private Date tranxDate;

	@Column(name="TRANX_REF")
	private String tranxRef;

	@Column(name="USER_DEFINE")
	private String userDefine;

	@Column(name="VAS_INFO")
	private String vasInfo;

	public IsomessageTmp68To() {
	}

	public long getTidbId() {
		return this.tidbId;
	}

	public void setTidbId(long tidbId) {
		this.tidbId = tidbId;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public BigDecimal getAcqCountry() {
		return this.acqCountry;
	}

	public void setAcqCountry(BigDecimal acqCountry) {
		this.acqCountry = acqCountry;
	}

	public String getAcqId() {
		return this.acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}

	public String getAddInfo() {
		return this.addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	public String getAddresponse() {
		return this.addresponse;
	}

	public void setAddresponse(String addresponse) {
		this.addresponse = addresponse;
	}

	public BigInteger getAmount() {
		return this.amount;
	}

	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountCardholder() {
		return this.amountCardholder;
	}

	public void setAmountCardholder(BigDecimal amountCardholder) {
		this.amountCardholder = amountCardholder;
	}

	public BigDecimal getAmountSettlement() {
		return this.amountSettlement;
	}

	public void setAmountSettlement(BigDecimal amountSettlement) {
		this.amountSettlement = amountSettlement;
	}

	public String getApprovalCode() {
		return this.approvalCode;
	}

	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	public String getBenId() {
		return this.benId;
	}

	public void setBenId(String benId) {
		this.benId = benId;
	}

	public String getCardAcceptIdCode() {
		return this.cardAcceptIdCode;
	}

	public void setCardAcceptIdCode(String cardAcceptIdCode) {
		this.cardAcceptIdCode = cardAcceptIdCode;
	}

	public String getCardAcceptNameLocation() {
		return this.cardAcceptNameLocation;
	}

	public void setCardAcceptNameLocation(String cardAcceptNameLocation) {
		this.cardAcceptNameLocation = cardAcceptNameLocation;
	}

	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getConversionRateCardholder() {
		return this.conversionRateCardholder;
	}

	public void setConversionRateCardholder(String conversionRateCardholder) {
		this.conversionRateCardholder = conversionRateCardholder;
	}

	public String getConversionRateSettlement() {
		return this.conversionRateSettlement;
	}

	public void setConversionRateSettlement(String conversionRateSettlement) {
		this.conversionRateSettlement = conversionRateSettlement;
	}

	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCurrencyCodeCardholder() {
		return this.currencyCodeCardholder;
	}

	public void setCurrencyCodeCardholder(String currencyCodeCardholder) {
		this.currencyCodeCardholder = currencyCodeCardholder;
	}

	public String getCurrencyCodeSettlement() {
		return this.currencyCodeSettlement;
	}

	public void setCurrencyCodeSettlement(String currencyCodeSettlement) {
		this.currencyCodeSettlement = currencyCodeSettlement;
	}

	public String getDestAccount() {
		return this.destAccount;
	}

	public void setDestAccount(String destAccount) {
		this.destAccount = destAccount;
	}

	public BigDecimal getF4() {
		return this.f4;
	}

	public void setF4(BigDecimal f4) {
		this.f4 = f4;
	}

	public int getF49() {
		return this.f49;
	}

	public void setF49(int f49) {
		this.f49 = f49;
	}

	public BigDecimal getF5() {
		return this.f5;
	}

	public void setF5(BigDecimal f5) {
		this.f5 = f5;
	}

	public BigDecimal getF6() {
		return this.f6;
	}

	public void setF6(BigDecimal f6) {
		this.f6 = f6;
	}

	public String getIbftInfo() {
		return this.ibftInfo;
	}

	public void setIbftInfo(String ibftInfo) {
		this.ibftInfo = ibftInfo;
	}

	public int getIsrev() {
		return this.isrev;
	}

	public void setIsrev(int isrev) {
		this.isrev = isrev;
	}

	public BigDecimal getIssConvRate() {
		return this.issConvRate;
	}

	public void setIssConvRate(BigDecimal issConvRate) {
		this.issConvRate = issConvRate;
	}

	public String getIssId() {
		return this.issId;
	}

	public void setIssId(String issId) {
		this.issId = issId;
	}

	public String getLocalDate() {
		return this.localDate;
	}

	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}

	public String getLocalTime() {
		return this.localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public String getMcc() {
		return this.mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getMti() {
		return this.mti;
	}

	public void setMti(String mti) {
		this.mti = mti;
	}

	public String getMvv() {
		return this.mvv;
	}

	public void setMvv(String mvv) {
		this.mvv = mvv;
	}

	public String getOfYear() {
		return this.ofYear;
	}

	public void setOfYear(String ofYear) {
		this.ofYear = ofYear;
	}

	public String getOriginalData() {
		return this.originalData;
	}

	public void setOriginalData(String originalData) {
		this.originalData = originalData;
	}

	public String getOriginalDate() {
		return this.originalDate;
	}

	public void setOriginalDate(String originalDate) {
		this.originalDate = originalDate;
	}

	public String getOrigrespcode() {
		return this.origrespcode;
	}

	public void setOrigrespcode(String origrespcode) {
		this.origrespcode = origrespcode;
	}

	public String getPackager() {
		return this.packager;
	}

	public void setPackager(String packager) {
		this.packager = packager;
	}

	public BigDecimal getPosConditionCode() {
		return this.posConditionCode;
	}

	public void setPosConditionCode(BigDecimal posConditionCode) {
		this.posConditionCode = posConditionCode;
	}

	public BigDecimal getPosEntryCode() {
		return this.posEntryCode;
	}

	public void setPosEntryCode(BigDecimal posEntryCode) {
		this.posEntryCode = posEntryCode;
	}

	public String getProcCode() {
		return this.procCode;
	}

	public void setProcCode(String procCode) {
		this.procCode = procCode;
	}

	public String getRc210() {
		return this.rc210;
	}

	public void setRc210(String rc210) {
		this.rc210 = rc210;
	}

	public Date getReconcileTime() {
		return this.reconcileTime;
	}

	public void setReconcileTime(Date reconcileTime) {
		this.reconcileTime = reconcileTime;
	}

	public String getRefNo() {
		return this.refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getReversed() {
		return this.reversed;
	}

	public void setReversed(String reversed) {
		this.reversed = reversed;
	}

	public BigInteger getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(BigInteger seqNo) {
		this.seqNo = seqNo;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getSettleDate() {
		return this.settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public int getSettlementCode() {
		return this.settlementCode;
	}

	public void setSettlementCode(int settlementCode) {
		this.settlementCode = settlementCode;
	}

	public BigDecimal getSettlementRate() {
		return this.settlementRate;
	}

	public void setSettlementRate(BigDecimal settlementRate) {
		this.settlementRate = settlementRate;
	}

	public String getTcc() {
		return this.tcc;
	}

	public void setTcc(String tcc) {
		this.tcc = tcc;
	}

	public String getTermId() {
		return this.termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public Date getTnxStamp() {
		return this.tnxStamp;
	}

	public void setTnxStamp(Date tnxStamp) {
		this.tnxStamp = tnxStamp;
	}

	public String getTraceNo() {
		return this.traceNo;
	}

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
	}

	public Date getTranxDate() {
		return this.tranxDate;
	}

	public void setTranxDate(Date tranxDate) {
		this.tranxDate = tranxDate;
	}

	public String getTranxRef() {
		return this.tranxRef;
	}

	public void setTranxRef(String tranxRef) {
		this.tranxRef = tranxRef;
	}

	public String getUserDefine() {
		return this.userDefine;
	}

	public void setUserDefine(String userDefine) {
		this.userDefine = userDefine;
	}

	public String getVasInfo() {
		return this.vasInfo;
	}

	public void setVasInfo(String vasInfo) {
		this.vasInfo = vasInfo;
	}

}