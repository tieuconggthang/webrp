package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the SHCLOG_BLE_AUTO database table.
 * 
 */
@Entity
@Table(name="SHCLOG_BLE_AUTO")
@NamedQuery(name="ShclogBleAuto.findAll", query="SELECT s FROM ShclogBleAuto s")
public class ShclogBleAuto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="tidb_id")
	private long tidbId;

	private String acceptorname;

	private String acctnum;

	private String acctnum1;

	@Column(name="ACQ_COUNTRY")
	private BigDecimal acqCountry;

	@Column(name="ACQ_CURRENCY_CODE")
	private BigInteger acqCurrencyCode;

	@Column(name="ACQ_RQ")
	private BigInteger acqRq;

	private BigInteger acquirer;

	@Column(name="ACQUIRER_FE")
	private BigInteger acquirerFe;

	@Column(name="ACQUIRER_RP")
	private BigInteger acquirerRp;

	private String addresponse;

	private BigDecimal amount;

	private String authnum;

	@Column(name="BB_ACCOUNT")
	private String bbAccount;

	@Column(name="BB_BIN")
	private BigInteger bbBin;

	@Column(name="BB_BIN_ORIG")
	private BigInteger bbBinOrig;

	@Column(name="BNB_ACC")
	private String bnbAcc;

	@Column(name="BNB_SWC")
	private String bnbSwc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CAP_DATE")
	private Date capDate;

	@Column(name="CARD_SEQNO")
	private BigInteger cardSeqno;

	@Column(name="CARDHOLDER_AMOUNT")
	private BigDecimal cardholderAmount;

	@Column(name="CARDHOLDER_CONV_RATE")
	private BigInteger cardholderConvRate;

	private String cardproduct;

	@Column(name="CH_CURRENCY_CODE")
	private BigInteger chCurrencyCode;

	@Column(name="CHIP_INDEX")
	private String chipIndex;

	@Column(name="CONFIG_FEE_ID")
	private BigDecimal configFeeId;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Column(name="CONV_RATE")
	private BigDecimal convRate;

	@Column(name="CONV_RATE_ACQ")
	private BigDecimal convRateAcq;

	private String des;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE_INS")
	private Date editDateIns;

	@Column(name="EDIT_USER")
	private String editUser;

	private BigInteger entityid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date f15;

	private BigDecimal f4;

	private BigInteger f49;

	private BigDecimal f5;

	private BigDecimal f6;

	private BigDecimal famount;

	private BigDecimal fee;

	@Column(name="FEE_ACQ")
	private BigDecimal feeAcq;

	@Column(name="FEE_IRF_ACQ")
	private BigDecimal feeIrfAcq;

	@Column(name="FEE_IRF_BEN")
	private BigDecimal feeIrfBen;

	@Column(name="FEE_IRF_ISS")
	private BigDecimal feeIrfIss;

	@Column(name="FEE_IRF_PAY_AT")
	private BigDecimal feeIrfPayAt;

	@Column(name="FEE_IRF_REC_AT")
	private BigDecimal feeIrfRecAt;

	@Column(name="FEE_ISS")
	private BigDecimal feeIss;

	@Column(name="FEE_KEY")
	private String feeKey;

	@Column(name="FEE_NOTE")
	private String feeNote;

	@Column(name="FEE_PAY_AT")
	private BigDecimal feePayAt;

	@Column(name="FEE_PAY_DF")
	private BigDecimal feePayDf;

	@Column(name="FEE_REC_AT")
	private BigDecimal feeRecAt;

	@Column(name="FEE_REC_DF")
	private BigDecimal feeRecDf;

	@Column(name="FEE_SERVICE")
	private BigDecimal feeService;

	@Column(name="FEE_SVF_ACQ")
	private BigDecimal feeSvfAcq;

	@Column(name="FEE_SVF_BEN")
	private BigDecimal feeSvfBen;

	@Column(name="FEE_SVF_ISS")
	private BigDecimal feeSvfIss;

	@Column(name="FEE_SVF_PAY_AT")
	private BigDecimal feeSvfPayAt;

	@Column(name="FEE_SVF_REC_AT")
	private BigDecimal feeSvfRecAt;

	@Column(name="FORWARD_INST")
	private BigInteger forwardInst;

	@Column(name="FROM_SML")
	private String fromSml;

	@Column(name="FROM_SYS")
	private String fromSys;

	@Column(name="INS_PCODE")
	private BigInteger insPcode;

	private BigInteger isrev;

	@Column(name="ISS_CONV_RATE")
	private BigDecimal issConvRate;

	@Column(name="ISS_CURRENCY_CODE")
	private BigInteger issCurrencyCode;

	@Column(name="ISS_RQ")
	private BigInteger issRq;

	private BigInteger issuer;

	@Column(name="ISSUER_FE")
	private BigInteger issuerFe;

	@Column(name="ISSUER_RP")
	private BigInteger issuerRp;

	private String lddnv;

	private String loaigdreveso;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigInteger localTime;

	@Column(name="MERCHANT_TYPE")
	private BigInteger merchantType;

	@Column(name="MERCHANT_TYPE_ORIG")
	private BigInteger merchantTypeOrig;

	private BigInteger msgtype;

	private String mvv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_DATE")
	private Date napasDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_EDIT_DATE")
	private Date napasEditDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_EDIT_DATE_INS")
	private Date napasEditDateIns;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_ND_DATE")
	private Date napasNdDate;

	@Column(name="NEW_FEE")
	private BigDecimal newFee;

	@Column(name="ONLY_SML")
	private String onlySml;

	@Column(name="ORIG_ACQ")
	private BigInteger origAcq;

	private String originator;

	private String origiss;

	private BigInteger origrespcode;

	private BigInteger origtrace;

	private String pan;

	private BigInteger pcode;

	@Column(name="PCODE_ORIG")
	private BigInteger pcodeOrig;

	private BigInteger pcode2;

	@Column(name="POS_CONDITION_CODE")
	private BigDecimal posConditionCode;

	@Column(name="POS_ENTRY_CODE")
	private BigDecimal posEntryCode;

	@Column(name="PRE_CARDHOLDER_AMOUNT")
	private BigDecimal preCardholderAmount;

	private BigDecimal preamount;

	private BigDecimal qamount;

	private BigDecimal ramount;

	private BigInteger rc;

	@Column(name="RC_ACQ")
	private String rcAcq;

	@Column(name="RC_ACQ_72")
	private BigInteger rcAcq72;

	@Column(name="RC_BEN")
	private String rcBen;

	@Column(name="RC_ISS")
	private String rcIss;

	@Column(name="RC_ISS_72")
	private BigInteger rcIss72;

	private BigDecimal reamount;

	@Column(name="REASON_EDIT")
	private String reasonEdit;

	private String refnum;

	@Column(name="REPAY_USD")
	private BigDecimal repayUsd;

	private BigInteger respcode;

	@Column(name="RESPCODE_GW")
	private String respcodeGw;

	private BigInteger revcode;

	@Column(name="SENDER_ACC")
	private String senderAcc;

	@Column(name="SENDER_SWC")
	private String senderSwc;

	@Column(name="SETT_CURRENCY_CODE")
	private BigInteger settCurrencyCode;

	@Column(name="SETTLEMENT_AMOUNT")
	private BigDecimal settlementAmount;

	@Column(name="SETTLEMENT_CODE")
	private BigInteger settlementCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name="SETTLEMENT_RATE")
	private BigDecimal settlementRate;

	private BigInteger shcerror;

	@Column(name="SML_VERIFY")
	private String smlVerify;

	private String src;

	private BigInteger stt;

	private String tcc;

	private String termid;

	@Column(name="TERMID_ACQ")
	private String termidAcq;

	private String termid1;

	private String termloc;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgddnv;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgguigd;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgguinv;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgguiqt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgguiqtp;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgtp;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgxlnv;

	private String thaydoi;

	private String token;

	private BigInteger trace;

	@Column(name="TRAN_CASE")
	private String tranCase;

	@Temporal(TemporalType.TIMESTAMP)
	private Date trandate;

	@Column(name="TRANSACTION_AMOUNT")
	private BigDecimal transactionAmount;

	private BigDecimal transferee;

	private BigInteger trantime;

	private String txndest;

	private String txnsrc;

	public ShclogBleAuto() {
	}

	public long getTidbId() {
		return this.tidbId;
	}

	public void setTidbId(long tidbId) {
		this.tidbId = tidbId;
	}

	public String getAcceptorname() {
		return this.acceptorname;
	}

	public void setAcceptorname(String acceptorname) {
		this.acceptorname = acceptorname;
	}

	public String getAcctnum() {
		return this.acctnum;
	}

	public void setAcctnum(String acctnum) {
		this.acctnum = acctnum;
	}

	public String getAcctnum1() {
		return this.acctnum1;
	}

	public void setAcctnum1(String acctnum1) {
		this.acctnum1 = acctnum1;
	}

	public BigDecimal getAcqCountry() {
		return this.acqCountry;
	}

	public void setAcqCountry(BigDecimal acqCountry) {
		this.acqCountry = acqCountry;
	}

	public BigInteger getAcqCurrencyCode() {
		return this.acqCurrencyCode;
	}

	public void setAcqCurrencyCode(BigInteger acqCurrencyCode) {
		this.acqCurrencyCode = acqCurrencyCode;
	}

	public BigInteger getAcqRq() {
		return this.acqRq;
	}

	public void setAcqRq(BigInteger acqRq) {
		this.acqRq = acqRq;
	}

	public BigInteger getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(BigInteger acquirer) {
		this.acquirer = acquirer;
	}

	public BigInteger getAcquirerFe() {
		return this.acquirerFe;
	}

	public void setAcquirerFe(BigInteger acquirerFe) {
		this.acquirerFe = acquirerFe;
	}

	public BigInteger getAcquirerRp() {
		return this.acquirerRp;
	}

	public void setAcquirerRp(BigInteger acquirerRp) {
		this.acquirerRp = acquirerRp;
	}

	public String getAddresponse() {
		return this.addresponse;
	}

	public void setAddresponse(String addresponse) {
		this.addresponse = addresponse;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getAuthnum() {
		return this.authnum;
	}

	public void setAuthnum(String authnum) {
		this.authnum = authnum;
	}

	public String getBbAccount() {
		return this.bbAccount;
	}

	public void setBbAccount(String bbAccount) {
		this.bbAccount = bbAccount;
	}

	public BigInteger getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigInteger bbBin) {
		this.bbBin = bbBin;
	}

	public BigInteger getBbBinOrig() {
		return this.bbBinOrig;
	}

	public void setBbBinOrig(BigInteger bbBinOrig) {
		this.bbBinOrig = bbBinOrig;
	}

	public String getBnbAcc() {
		return this.bnbAcc;
	}

	public void setBnbAcc(String bnbAcc) {
		this.bnbAcc = bnbAcc;
	}

	public String getBnbSwc() {
		return this.bnbSwc;
	}

	public void setBnbSwc(String bnbSwc) {
		this.bnbSwc = bnbSwc;
	}

	public Date getCapDate() {
		return this.capDate;
	}

	public void setCapDate(Date capDate) {
		this.capDate = capDate;
	}

	public BigInteger getCardSeqno() {
		return this.cardSeqno;
	}

	public void setCardSeqno(BigInteger cardSeqno) {
		this.cardSeqno = cardSeqno;
	}

	public BigDecimal getCardholderAmount() {
		return this.cardholderAmount;
	}

	public void setCardholderAmount(BigDecimal cardholderAmount) {
		this.cardholderAmount = cardholderAmount;
	}

	public BigInteger getCardholderConvRate() {
		return this.cardholderConvRate;
	}

	public void setCardholderConvRate(BigInteger cardholderConvRate) {
		this.cardholderConvRate = cardholderConvRate;
	}

	public String getCardproduct() {
		return this.cardproduct;
	}

	public void setCardproduct(String cardproduct) {
		this.cardproduct = cardproduct;
	}

	public BigInteger getChCurrencyCode() {
		return this.chCurrencyCode;
	}

	public void setChCurrencyCode(BigInteger chCurrencyCode) {
		this.chCurrencyCode = chCurrencyCode;
	}

	public String getChipIndex() {
		return this.chipIndex;
	}

	public void setChipIndex(String chipIndex) {
		this.chipIndex = chipIndex;
	}

	public BigDecimal getConfigFeeId() {
		return this.configFeeId;
	}

	public void setConfigFeeId(BigDecimal configFeeId) {
		this.configFeeId = configFeeId;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public BigDecimal getConvRate() {
		return this.convRate;
	}

	public void setConvRate(BigDecimal convRate) {
		this.convRate = convRate;
	}

	public BigDecimal getConvRateAcq() {
		return this.convRateAcq;
	}

	public void setConvRateAcq(BigDecimal convRateAcq) {
		this.convRateAcq = convRateAcq;
	}

	public String getDes() {
		return this.des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public Date getEditDateIns() {
		return this.editDateIns;
	}

	public void setEditDateIns(Date editDateIns) {
		this.editDateIns = editDateIns;
	}

	public String getEditUser() {
		return this.editUser;
	}

	public void setEditUser(String editUser) {
		this.editUser = editUser;
	}

	public BigInteger getEntityid() {
		return this.entityid;
	}

	public void setEntityid(BigInteger entityid) {
		this.entityid = entityid;
	}

	public Date getF15() {
		return this.f15;
	}

	public void setF15(Date f15) {
		this.f15 = f15;
	}

	public BigDecimal getF4() {
		return this.f4;
	}

	public void setF4(BigDecimal f4) {
		this.f4 = f4;
	}

	public BigInteger getF49() {
		return this.f49;
	}

	public void setF49(BigInteger f49) {
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

	public BigDecimal getFamount() {
		return this.famount;
	}

	public void setFamount(BigDecimal famount) {
		this.famount = famount;
	}

	public BigDecimal getFee() {
		return this.fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getFeeAcq() {
		return this.feeAcq;
	}

	public void setFeeAcq(BigDecimal feeAcq) {
		this.feeAcq = feeAcq;
	}

	public BigDecimal getFeeIrfAcq() {
		return this.feeIrfAcq;
	}

	public void setFeeIrfAcq(BigDecimal feeIrfAcq) {
		this.feeIrfAcq = feeIrfAcq;
	}

	public BigDecimal getFeeIrfBen() {
		return this.feeIrfBen;
	}

	public void setFeeIrfBen(BigDecimal feeIrfBen) {
		this.feeIrfBen = feeIrfBen;
	}

	public BigDecimal getFeeIrfIss() {
		return this.feeIrfIss;
	}

	public void setFeeIrfIss(BigDecimal feeIrfIss) {
		this.feeIrfIss = feeIrfIss;
	}

	public BigDecimal getFeeIrfPayAt() {
		return this.feeIrfPayAt;
	}

	public void setFeeIrfPayAt(BigDecimal feeIrfPayAt) {
		this.feeIrfPayAt = feeIrfPayAt;
	}

	public BigDecimal getFeeIrfRecAt() {
		return this.feeIrfRecAt;
	}

	public void setFeeIrfRecAt(BigDecimal feeIrfRecAt) {
		this.feeIrfRecAt = feeIrfRecAt;
	}

	public BigDecimal getFeeIss() {
		return this.feeIss;
	}

	public void setFeeIss(BigDecimal feeIss) {
		this.feeIss = feeIss;
	}

	public String getFeeKey() {
		return this.feeKey;
	}

	public void setFeeKey(String feeKey) {
		this.feeKey = feeKey;
	}

	public String getFeeNote() {
		return this.feeNote;
	}

	public void setFeeNote(String feeNote) {
		this.feeNote = feeNote;
	}

	public BigDecimal getFeePayAt() {
		return this.feePayAt;
	}

	public void setFeePayAt(BigDecimal feePayAt) {
		this.feePayAt = feePayAt;
	}

	public BigDecimal getFeePayDf() {
		return this.feePayDf;
	}

	public void setFeePayDf(BigDecimal feePayDf) {
		this.feePayDf = feePayDf;
	}

	public BigDecimal getFeeRecAt() {
		return this.feeRecAt;
	}

	public void setFeeRecAt(BigDecimal feeRecAt) {
		this.feeRecAt = feeRecAt;
	}

	public BigDecimal getFeeRecDf() {
		return this.feeRecDf;
	}

	public void setFeeRecDf(BigDecimal feeRecDf) {
		this.feeRecDf = feeRecDf;
	}

	public BigDecimal getFeeService() {
		return this.feeService;
	}

	public void setFeeService(BigDecimal feeService) {
		this.feeService = feeService;
	}

	public BigDecimal getFeeSvfAcq() {
		return this.feeSvfAcq;
	}

	public void setFeeSvfAcq(BigDecimal feeSvfAcq) {
		this.feeSvfAcq = feeSvfAcq;
	}

	public BigDecimal getFeeSvfBen() {
		return this.feeSvfBen;
	}

	public void setFeeSvfBen(BigDecimal feeSvfBen) {
		this.feeSvfBen = feeSvfBen;
	}

	public BigDecimal getFeeSvfIss() {
		return this.feeSvfIss;
	}

	public void setFeeSvfIss(BigDecimal feeSvfIss) {
		this.feeSvfIss = feeSvfIss;
	}

	public BigDecimal getFeeSvfPayAt() {
		return this.feeSvfPayAt;
	}

	public void setFeeSvfPayAt(BigDecimal feeSvfPayAt) {
		this.feeSvfPayAt = feeSvfPayAt;
	}

	public BigDecimal getFeeSvfRecAt() {
		return this.feeSvfRecAt;
	}

	public void setFeeSvfRecAt(BigDecimal feeSvfRecAt) {
		this.feeSvfRecAt = feeSvfRecAt;
	}

	public BigInteger getForwardInst() {
		return this.forwardInst;
	}

	public void setForwardInst(BigInteger forwardInst) {
		this.forwardInst = forwardInst;
	}

	public String getFromSml() {
		return this.fromSml;
	}

	public void setFromSml(String fromSml) {
		this.fromSml = fromSml;
	}

	public String getFromSys() {
		return this.fromSys;
	}

	public void setFromSys(String fromSys) {
		this.fromSys = fromSys;
	}

	public BigInteger getInsPcode() {
		return this.insPcode;
	}

	public void setInsPcode(BigInteger insPcode) {
		this.insPcode = insPcode;
	}

	public BigInteger getIsrev() {
		return this.isrev;
	}

	public void setIsrev(BigInteger isrev) {
		this.isrev = isrev;
	}

	public BigDecimal getIssConvRate() {
		return this.issConvRate;
	}

	public void setIssConvRate(BigDecimal issConvRate) {
		this.issConvRate = issConvRate;
	}

	public BigInteger getIssCurrencyCode() {
		return this.issCurrencyCode;
	}

	public void setIssCurrencyCode(BigInteger issCurrencyCode) {
		this.issCurrencyCode = issCurrencyCode;
	}

	public BigInteger getIssRq() {
		return this.issRq;
	}

	public void setIssRq(BigInteger issRq) {
		this.issRq = issRq;
	}

	public BigInteger getIssuer() {
		return this.issuer;
	}

	public void setIssuer(BigInteger issuer) {
		this.issuer = issuer;
	}

	public BigInteger getIssuerFe() {
		return this.issuerFe;
	}

	public void setIssuerFe(BigInteger issuerFe) {
		this.issuerFe = issuerFe;
	}

	public BigInteger getIssuerRp() {
		return this.issuerRp;
	}

	public void setIssuerRp(BigInteger issuerRp) {
		this.issuerRp = issuerRp;
	}

	public String getLddnv() {
		return this.lddnv;
	}

	public void setLddnv(String lddnv) {
		this.lddnv = lddnv;
	}

	public String getLoaigdreveso() {
		return this.loaigdreveso;
	}

	public void setLoaigdreveso(String loaigdreveso) {
		this.loaigdreveso = loaigdreveso;
	}

	public Date getLocalDate() {
		return this.localDate;
	}

	public void setLocalDate(Date localDate) {
		this.localDate = localDate;
	}

	public BigInteger getLocalTime() {
		return this.localTime;
	}

	public void setLocalTime(BigInteger localTime) {
		this.localTime = localTime;
	}

	public BigInteger getMerchantType() {
		return this.merchantType;
	}

	public void setMerchantType(BigInteger merchantType) {
		this.merchantType = merchantType;
	}

	public BigInteger getMerchantTypeOrig() {
		return this.merchantTypeOrig;
	}

	public void setMerchantTypeOrig(BigInteger merchantTypeOrig) {
		this.merchantTypeOrig = merchantTypeOrig;
	}

	public BigInteger getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigInteger msgtype) {
		this.msgtype = msgtype;
	}

	public String getMvv() {
		return this.mvv;
	}

	public void setMvv(String mvv) {
		this.mvv = mvv;
	}

	public Date getNapasDate() {
		return this.napasDate;
	}

	public void setNapasDate(Date napasDate) {
		this.napasDate = napasDate;
	}

	public Date getNapasEditDate() {
		return this.napasEditDate;
	}

	public void setNapasEditDate(Date napasEditDate) {
		this.napasEditDate = napasEditDate;
	}

	public Date getNapasEditDateIns() {
		return this.napasEditDateIns;
	}

	public void setNapasEditDateIns(Date napasEditDateIns) {
		this.napasEditDateIns = napasEditDateIns;
	}

	public Date getNapasNdDate() {
		return this.napasNdDate;
	}

	public void setNapasNdDate(Date napasNdDate) {
		this.napasNdDate = napasNdDate;
	}

	public BigDecimal getNewFee() {
		return this.newFee;
	}

	public void setNewFee(BigDecimal newFee) {
		this.newFee = newFee;
	}

	public String getOnlySml() {
		return this.onlySml;
	}

	public void setOnlySml(String onlySml) {
		this.onlySml = onlySml;
	}

	public BigInteger getOrigAcq() {
		return this.origAcq;
	}

	public void setOrigAcq(BigInteger origAcq) {
		this.origAcq = origAcq;
	}

	public String getOriginator() {
		return this.originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public String getOrigiss() {
		return this.origiss;
	}

	public void setOrigiss(String origiss) {
		this.origiss = origiss;
	}

	public BigInteger getOrigrespcode() {
		return this.origrespcode;
	}

	public void setOrigrespcode(BigInteger origrespcode) {
		this.origrespcode = origrespcode;
	}

	public BigInteger getOrigtrace() {
		return this.origtrace;
	}

	public void setOrigtrace(BigInteger origtrace) {
		this.origtrace = origtrace;
	}

	public String getPan() {
		return this.pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public BigInteger getPcode() {
		return this.pcode;
	}

	public void setPcode(BigInteger pcode) {
		this.pcode = pcode;
	}

	public BigInteger getPcodeOrig() {
		return this.pcodeOrig;
	}

	public void setPcodeOrig(BigInteger pcodeOrig) {
		this.pcodeOrig = pcodeOrig;
	}

	public BigInteger getPcode2() {
		return this.pcode2;
	}

	public void setPcode2(BigInteger pcode2) {
		this.pcode2 = pcode2;
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

	public BigDecimal getPreCardholderAmount() {
		return this.preCardholderAmount;
	}

	public void setPreCardholderAmount(BigDecimal preCardholderAmount) {
		this.preCardholderAmount = preCardholderAmount;
	}

	public BigDecimal getPreamount() {
		return this.preamount;
	}

	public void setPreamount(BigDecimal preamount) {
		this.preamount = preamount;
	}

	public BigDecimal getQamount() {
		return this.qamount;
	}

	public void setQamount(BigDecimal qamount) {
		this.qamount = qamount;
	}

	public BigDecimal getRamount() {
		return this.ramount;
	}

	public void setRamount(BigDecimal ramount) {
		this.ramount = ramount;
	}

	public BigInteger getRc() {
		return this.rc;
	}

	public void setRc(BigInteger rc) {
		this.rc = rc;
	}

	public String getRcAcq() {
		return this.rcAcq;
	}

	public void setRcAcq(String rcAcq) {
		this.rcAcq = rcAcq;
	}

	public BigInteger getRcAcq72() {
		return this.rcAcq72;
	}

	public void setRcAcq72(BigInteger rcAcq72) {
		this.rcAcq72 = rcAcq72;
	}

	public String getRcBen() {
		return this.rcBen;
	}

	public void setRcBen(String rcBen) {
		this.rcBen = rcBen;
	}

	public String getRcIss() {
		return this.rcIss;
	}

	public void setRcIss(String rcIss) {
		this.rcIss = rcIss;
	}

	public BigInteger getRcIss72() {
		return this.rcIss72;
	}

	public void setRcIss72(BigInteger rcIss72) {
		this.rcIss72 = rcIss72;
	}

	public BigDecimal getReamount() {
		return this.reamount;
	}

	public void setReamount(BigDecimal reamount) {
		this.reamount = reamount;
	}

	public String getReasonEdit() {
		return this.reasonEdit;
	}

	public void setReasonEdit(String reasonEdit) {
		this.reasonEdit = reasonEdit;
	}

	public String getRefnum() {
		return this.refnum;
	}

	public void setRefnum(String refnum) {
		this.refnum = refnum;
	}

	public BigDecimal getRepayUsd() {
		return this.repayUsd;
	}

	public void setRepayUsd(BigDecimal repayUsd) {
		this.repayUsd = repayUsd;
	}

	public BigInteger getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigInteger respcode) {
		this.respcode = respcode;
	}

	public String getRespcodeGw() {
		return this.respcodeGw;
	}

	public void setRespcodeGw(String respcodeGw) {
		this.respcodeGw = respcodeGw;
	}

	public BigInteger getRevcode() {
		return this.revcode;
	}

	public void setRevcode(BigInteger revcode) {
		this.revcode = revcode;
	}

	public String getSenderAcc() {
		return this.senderAcc;
	}

	public void setSenderAcc(String senderAcc) {
		this.senderAcc = senderAcc;
	}

	public String getSenderSwc() {
		return this.senderSwc;
	}

	public void setSenderSwc(String senderSwc) {
		this.senderSwc = senderSwc;
	}

	public BigInteger getSettCurrencyCode() {
		return this.settCurrencyCode;
	}

	public void setSettCurrencyCode(BigInteger settCurrencyCode) {
		this.settCurrencyCode = settCurrencyCode;
	}

	public BigDecimal getSettlementAmount() {
		return this.settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public BigInteger getSettlementCode() {
		return this.settlementCode;
	}

	public void setSettlementCode(BigInteger settlementCode) {
		this.settlementCode = settlementCode;
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

	public BigInteger getShcerror() {
		return this.shcerror;
	}

	public void setShcerror(BigInteger shcerror) {
		this.shcerror = shcerror;
	}

	public String getSmlVerify() {
		return this.smlVerify;
	}

	public void setSmlVerify(String smlVerify) {
		this.smlVerify = smlVerify;
	}

	public String getSrc() {
		return this.src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public BigInteger getStt() {
		return this.stt;
	}

	public void setStt(BigInteger stt) {
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

	public String getTermidAcq() {
		return this.termidAcq;
	}

	public void setTermidAcq(String termidAcq) {
		this.termidAcq = termidAcq;
	}

	public String getTermid1() {
		return this.termid1;
	}

	public void setTermid1(String termid1) {
		this.termid1 = termid1;
	}

	public String getTermloc() {
		return this.termloc;
	}

	public void setTermloc(String termloc) {
		this.termloc = termloc;
	}

	public Date getTgddnv() {
		return this.tgddnv;
	}

	public void setTgddnv(Date tgddnv) {
		this.tgddnv = tgddnv;
	}

	public Date getTgguigd() {
		return this.tgguigd;
	}

	public void setTgguigd(Date tgguigd) {
		this.tgguigd = tgguigd;
	}

	public Date getTgguinv() {
		return this.tgguinv;
	}

	public void setTgguinv(Date tgguinv) {
		this.tgguinv = tgguinv;
	}

	public Date getTgguiqt() {
		return this.tgguiqt;
	}

	public void setTgguiqt(Date tgguiqt) {
		this.tgguiqt = tgguiqt;
	}

	public Date getTgguiqtp() {
		return this.tgguiqtp;
	}

	public void setTgguiqtp(Date tgguiqtp) {
		this.tgguiqtp = tgguiqtp;
	}

	public Date getTgtp() {
		return this.tgtp;
	}

	public void setTgtp(Date tgtp) {
		this.tgtp = tgtp;
	}

	public Date getTgxlnv() {
		return this.tgxlnv;
	}

	public void setTgxlnv(Date tgxlnv) {
		this.tgxlnv = tgxlnv;
	}

	public String getThaydoi() {
		return this.thaydoi;
	}

	public void setThaydoi(String thaydoi) {
		this.thaydoi = thaydoi;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public BigInteger getTrace() {
		return this.trace;
	}

	public void setTrace(BigInteger trace) {
		this.trace = trace;
	}

	public String getTranCase() {
		return this.tranCase;
	}

	public void setTranCase(String tranCase) {
		this.tranCase = tranCase;
	}

	public Date getTrandate() {
		return this.trandate;
	}

	public void setTrandate(Date trandate) {
		this.trandate = trandate;
	}

	public BigDecimal getTransactionAmount() {
		return this.transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public BigDecimal getTransferee() {
		return this.transferee;
	}

	public void setTransferee(BigDecimal transferee) {
		this.transferee = transferee;
	}

	public BigInteger getTrantime() {
		return this.trantime;
	}

	public void setTrantime(BigInteger trantime) {
		this.trantime = trantime;
	}

	public String getTxndest() {
		return this.txndest;
	}

	public void setTxndest(String txndest) {
		this.txndest = txndest;
	}

	public String getTxnsrc() {
		return this.txnsrc;
	}

	public void setTxnsrc(String txnsrc) {
		this.txnsrc = txnsrc;
	}

}