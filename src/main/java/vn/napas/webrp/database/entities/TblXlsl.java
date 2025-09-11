package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TBL_XLSL database table.
 * 
 */
@Entity
@Table(name="TBL_XLSL")
@NamedQuery(name="TblXlsl.findAll", query="SELECT t FROM TblXlsl t")
public class TblXlsl implements Serializable {
	private static final long serialVersionUID = 1L;

	private String acceptorname;

	@Column(name="ACH_BEN_ID")
	private BigDecimal achBenId;

	@Column(name="ACH_ISS_ID")
	private BigDecimal achIssId;

	@Column(name="ACQ_CURRENCY_CODE")
	private BigDecimal acqCurrencyCode;

	private String acquirer;

	private BigDecimal amount;

	@Column(name="AMOUNT_ISS")
	private BigDecimal amountIss;

	@Column(name="AMOUNT_SET")
	private BigDecimal amountSet;

	@Column(name="BB_ACCOUNT")
	private String bbAccount;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Column(name="CARDHOLDER_CONV_RATE")
	private BigDecimal cardholderConvRate;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_HOACHTOAN")
	private Date dateHoachtoan;

	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	@Column(name="FROM_ACC")
	private String fromAcc;

	private String issuer;

	private String ksv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	private BigDecimal msgtype;

	private String mvv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_EDIT_DATE")
	private Date napasEditDate;

	private BigDecimal origtrace;

	private String pan;

	@Column(name="PARTIAL_AMOUNT")
	private BigDecimal partialAmount;

	@Column(name="PARTIAL_AMOUNT_ISS")
	private BigDecimal partialAmountIss;

	@Column(name="PARTIAL_AMOUNT_SET")
	private BigDecimal partialAmountSet;

	private BigDecimal pcode;

	private BigDecimal pcode2;

	@Column(name="RC_HOACHTOAN")
	private BigDecimal rcHoachtoan;

	private BigDecimal respcode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name="SETTLEMENT_RATE")
	private BigDecimal settlementRate;

	private String statuscode;

	private BigDecimal stt;

	private String tcc;

	private String termid;

	@Column(name="TO_ACC")
	private String toAcc;

	private String token;

	private BigDecimal trace;

	@Temporal(TemporalType.TIMESTAMP)
	private Date trandate;

	private BigDecimal trantime;

	private String tsv;

	private BigDecimal type;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE")
	private Date updateDate;

	@Column(name="USER_HOACHTOAN")
	private String userHoachtoan;

	public TblXlsl() {
	}

	public String getAcceptorname() {
		return this.acceptorname;
	}

	public void setAcceptorname(String acceptorname) {
		this.acceptorname = acceptorname;
	}

	public BigDecimal getAchBenId() {
		return this.achBenId;
	}

	public void setAchBenId(BigDecimal achBenId) {
		this.achBenId = achBenId;
	}

	public BigDecimal getAchIssId() {
		return this.achIssId;
	}

	public void setAchIssId(BigDecimal achIssId) {
		this.achIssId = achIssId;
	}

	public BigDecimal getAcqCurrencyCode() {
		return this.acqCurrencyCode;
	}

	public void setAcqCurrencyCode(BigDecimal acqCurrencyCode) {
		this.acqCurrencyCode = acqCurrencyCode;
	}

	public String getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountIss() {
		return this.amountIss;
	}

	public void setAmountIss(BigDecimal amountIss) {
		this.amountIss = amountIss;
	}

	public BigDecimal getAmountSet() {
		return this.amountSet;
	}

	public void setAmountSet(BigDecimal amountSet) {
		this.amountSet = amountSet;
	}

	public String getBbAccount() {
		return this.bbAccount;
	}

	public void setBbAccount(String bbAccount) {
		this.bbAccount = bbAccount;
	}

	public BigDecimal getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigDecimal bbBin) {
		this.bbBin = bbBin;
	}

	public BigDecimal getCardholderConvRate() {
		return this.cardholderConvRate;
	}

	public void setCardholderConvRate(BigDecimal cardholderConvRate) {
		this.cardholderConvRate = cardholderConvRate;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public Date getDateHoachtoan() {
		return this.dateHoachtoan;
	}

	public void setDateHoachtoan(Date dateHoachtoan) {
		this.dateHoachtoan = dateHoachtoan;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public String getFromAcc() {
		return this.fromAcc;
	}

	public void setFromAcc(String fromAcc) {
		this.fromAcc = fromAcc;
	}

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getKsv() {
		return this.ksv;
	}

	public void setKsv(String ksv) {
		this.ksv = ksv;
	}

	public Date getLocalDate() {
		return this.localDate;
	}

	public void setLocalDate(Date localDate) {
		this.localDate = localDate;
	}

	public BigDecimal getLocalTime() {
		return this.localTime;
	}

	public void setLocalTime(BigDecimal localTime) {
		this.localTime = localTime;
	}

	public BigDecimal getMerchantType() {
		return this.merchantType;
	}

	public void setMerchantType(BigDecimal merchantType) {
		this.merchantType = merchantType;
	}

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public String getMvv() {
		return this.mvv;
	}

	public void setMvv(String mvv) {
		this.mvv = mvv;
	}

	public Date getNapasEditDate() {
		return this.napasEditDate;
	}

	public void setNapasEditDate(Date napasEditDate) {
		this.napasEditDate = napasEditDate;
	}

	public BigDecimal getOrigtrace() {
		return this.origtrace;
	}

	public void setOrigtrace(BigDecimal origtrace) {
		this.origtrace = origtrace;
	}

	public String getPan() {
		return this.pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public BigDecimal getPartialAmount() {
		return this.partialAmount;
	}

	public void setPartialAmount(BigDecimal partialAmount) {
		this.partialAmount = partialAmount;
	}

	public BigDecimal getPartialAmountIss() {
		return this.partialAmountIss;
	}

	public void setPartialAmountIss(BigDecimal partialAmountIss) {
		this.partialAmountIss = partialAmountIss;
	}

	public BigDecimal getPartialAmountSet() {
		return this.partialAmountSet;
	}

	public void setPartialAmountSet(BigDecimal partialAmountSet) {
		this.partialAmountSet = partialAmountSet;
	}

	public BigDecimal getPcode() {
		return this.pcode;
	}

	public void setPcode(BigDecimal pcode) {
		this.pcode = pcode;
	}

	public BigDecimal getPcode2() {
		return this.pcode2;
	}

	public void setPcode2(BigDecimal pcode2) {
		this.pcode2 = pcode2;
	}

	public BigDecimal getRcHoachtoan() {
		return this.rcHoachtoan;
	}

	public void setRcHoachtoan(BigDecimal rcHoachtoan) {
		this.rcHoachtoan = rcHoachtoan;
	}

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public BigDecimal getSettlementRate() {
		return this.settlementRate;
	}

	public void setSettlementRate(BigDecimal settlementRate) {
		this.settlementRate = settlementRate;
	}

	public String getStatuscode() {
		return this.statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public String getTcc() {
		return this.tcc;
	}

	public void setTcc(String tcc) {
		this.tcc = tcc;
	}

	public String getTermid() {
		return this.termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

	public String getToAcc() {
		return this.toAcc;
	}

	public void setToAcc(String toAcc) {
		this.toAcc = toAcc;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public BigDecimal getTrace() {
		return this.trace;
	}

	public void setTrace(BigDecimal trace) {
		this.trace = trace;
	}

	public Date getTrandate() {
		return this.trandate;
	}

	public void setTrandate(Date trandate) {
		this.trandate = trandate;
	}

	public BigDecimal getTrantime() {
		return this.trantime;
	}

	public void setTrantime(BigDecimal trantime) {
		this.trantime = trantime;
	}

	public String getTsv() {
		return this.tsv;
	}

	public void setTsv(String tsv) {
		this.tsv = tsv;
	}

	public BigDecimal getType() {
		return this.type;
	}

	public void setType(BigDecimal type) {
		this.type = type;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUserHoachtoan() {
		return this.userHoachtoan;
	}

	public void setUserHoachtoan(String userHoachtoan) {
		this.userHoachtoan = userHoachtoan;
	}

}