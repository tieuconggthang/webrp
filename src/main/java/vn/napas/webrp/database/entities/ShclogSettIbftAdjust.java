package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the SHCLOG_SETT_IBFT_ADJUST database table.
 * 
 */
@Entity
@Table(name="SHCLOG_SETT_IBFT_ADJUST")
@NamedQuery(name="ShclogSettIbftAdjust.findAll", query="SELECT s FROM ShclogSettIbftAdjust s")
public class ShclogSettIbftAdjust implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="tidb_id")
	private long tidbId;

	private String acceptorname;

	private String acctnum;

	@Column(name="ACQ_COUNTRY")
	private BigDecimal acqCountry;

	@Column(name="ACQ_CURRENCY_CODE")
	private BigDecimal acqCurrencyCode;

	@Column(name="ACQ_SEND")
	private String acqSend;

	private String acquirer;

	@Column(name="ACQUIRER_FE")
	private BigDecimal acquirerFe;

	@Column(name="ACQUIRER_RP")
	private BigDecimal acquirerRp;

	@Column(name="ADD_INFO")
	private String addInfo;

	private String addresponse;

	private BigDecimal amount;

	private String authnum;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Column(name="BB_BIN_ORIG")
	private BigDecimal bbBinOrig;

	@Column(name="BNB_SEND")
	private String bnbSend;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CAP_DATE")
	private Date capDate;

	@Column(name="CARDHOLDER_AMOUNT")
	private BigDecimal cardholderAmount;

	@Column(name="CARDHOLDER_CONV_RATE")
	private BigDecimal cardholderConvRate;

	private String cardproduct;

	@Column(name="CH_CURRENCY_CODE")
	private BigDecimal chCurrencyCode;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Column(name="DATA_ID")
	private BigDecimal dataId;

	private String des;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	@Column(name="EDIT_USER")
	private String editUser;

	@Column(name="F100_UPI")
	private String f100Upi;

	private BigDecimal f4;

	private BigDecimal f49;

	private BigDecimal f5;

	private BigDecimal f6;

	@Column(name="F60_UPI")
	private String f60Upi;

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
	private BigDecimal forwardInst;

	@Column(name="FROM_SYS")
	private String fromSys;

	private BigDecimal irfacqbnb;

	private BigDecimal irfacqiss;

	private BigDecimal irfbnbacq;

	private BigDecimal irfbnbiss;

	private BigDecimal irfissacq;

	private BigDecimal irfissbnb;

	@Column(name="IS_PART_REV")
	private BigDecimal isPartRev;

	private BigDecimal isrev;

	@Column(name="ISS_CONV_RATE")
	private BigDecimal issConvRate;

	@Column(name="ISS_CURRENCY_CODE")
	private BigDecimal issCurrencyCode;

	@Column(name="ISS_SEND")
	private String issSend;

	private String issuer;

	@Column(name="ISSUER_FE")
	private BigDecimal issuerFe;

	@Column(name="ISSUER_RP")
	private BigDecimal issuerRp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	@Column(name="MERCHANT_TYPE_ORIG")
	private BigDecimal merchantTypeOrig;

	private BigDecimal msgtype;

	@Column(name="MSGTYPE_DETAIL")
	private String msgtypeDetail;

	private String mvv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_DATE")
	private Date napasDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_EDIT_DATE")
	private Date napasEditDate;

	@Column(name="ONLY_SML")
	private String onlySml;

	private String origiss;

	private BigDecimal origrespcode;

	private BigDecimal origtrace;

	private String pan;

	private String pcode;

	@Column(name="PCODE_ORIG")
	private BigDecimal pcodeOrig;

	private BigDecimal pcode2;

	@Column(name="POS_CONDITION_CODE")
	private BigDecimal posConditionCode;

	@Column(name="POS_ENTRY_CODE")
	private BigDecimal posEntryCode;

	private BigDecimal ppcode;

	@Column(name="PRE_CARDHOLDER_AMOUNT")
	private BigDecimal preCardholderAmount;

	private BigDecimal preamount;

	@Column(name="PREAMOUNT_ACQ")
	private BigDecimal preamountAcq;

	@Column(name="PREAMOUNT_USD")
	private BigDecimal preamountUsd;

	private BigDecimal rc;

	@Column(name="RC_ACQ_72")
	private BigDecimal rcAcq72;

	@Column(name="RC_BEN")
	private String rcBen;

	@Column(name="RC_ISS")
	private String rcIss;

	@Column(name="RC_ISS_72")
	private BigDecimal rcIss72;

	@Column(name="RE_FEE")
	private BigDecimal reFee;

	private String refnum;

	private BigDecimal respcode;

	private BigDecimal revcode;

	@Column(name="SETT_CURRENCY_CODE")
	private BigDecimal settCurrencyCode;

	@Column(name="SETTLEMENT_AMOUNT")
	private BigDecimal settlementAmount;

	@Column(name="SETTLEMENT_CODE")
	private BigDecimal settlementCode;

	@Column(name="SETTLEMENT_CONV_RATE")
	private BigDecimal settlementConvRate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name="SETTLEMENT_RATE")
	private BigDecimal settlementRate;

	@Column(name="SML_VERIFY")
	private String smlVerify;

	private String src;

	private BigDecimal stt;

	private BigDecimal svfacqnp;

	private BigDecimal svfbnbnp;

	private BigDecimal svfissnp;

	private String tcc;

	private String termid;

	private String termid1;

	private String termloc;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgguinv;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgguiqt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgguiqtp;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgxlnv;

	private BigDecimal thu;

	@Column(name="THU_REFUN")
	private BigDecimal thuRefun;

	private String token;

	private BigDecimal trace;

	@Column(name="TRAN_CASE")
	private String tranCase;

	@Temporal(TemporalType.TIMESTAMP)
	private Date trandate;

	private BigDecimal transferee;

	@Column(name="TRANSIT_CSRR")
	private String transitCsrr;

	private BigDecimal trantime;

	private BigDecimal tt1;

	private BigDecimal tt2;

	private BigDecimal tt3;

	private BigDecimal tt4;

	private String txndest;

	private String txnsrc;

	public ShclogSettIbftAdjust() {
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

	public BigDecimal getAcqCountry() {
		return this.acqCountry;
	}

	public void setAcqCountry(BigDecimal acqCountry) {
		this.acqCountry = acqCountry;
	}

	public BigDecimal getAcqCurrencyCode() {
		return this.acqCurrencyCode;
	}

	public void setAcqCurrencyCode(BigDecimal acqCurrencyCode) {
		this.acqCurrencyCode = acqCurrencyCode;
	}

	public String getAcqSend() {
		return this.acqSend;
	}

	public void setAcqSend(String acqSend) {
		this.acqSend = acqSend;
	}

	public String getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public BigDecimal getAcquirerFe() {
		return this.acquirerFe;
	}

	public void setAcquirerFe(BigDecimal acquirerFe) {
		this.acquirerFe = acquirerFe;
	}

	public BigDecimal getAcquirerRp() {
		return this.acquirerRp;
	}

	public void setAcquirerRp(BigDecimal acquirerRp) {
		this.acquirerRp = acquirerRp;
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

	public BigDecimal getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigDecimal bbBin) {
		this.bbBin = bbBin;
	}

	public BigDecimal getBbBinOrig() {
		return this.bbBinOrig;
	}

	public void setBbBinOrig(BigDecimal bbBinOrig) {
		this.bbBinOrig = bbBinOrig;
	}

	public String getBnbSend() {
		return this.bnbSend;
	}

	public void setBnbSend(String bnbSend) {
		this.bnbSend = bnbSend;
	}

	public Date getCapDate() {
		return this.capDate;
	}

	public void setCapDate(Date capDate) {
		this.capDate = capDate;
	}

	public BigDecimal getCardholderAmount() {
		return this.cardholderAmount;
	}

	public void setCardholderAmount(BigDecimal cardholderAmount) {
		this.cardholderAmount = cardholderAmount;
	}

	public BigDecimal getCardholderConvRate() {
		return this.cardholderConvRate;
	}

	public void setCardholderConvRate(BigDecimal cardholderConvRate) {
		this.cardholderConvRate = cardholderConvRate;
	}

	public String getCardproduct() {
		return this.cardproduct;
	}

	public void setCardproduct(String cardproduct) {
		this.cardproduct = cardproduct;
	}

	public BigDecimal getChCurrencyCode() {
		return this.chCurrencyCode;
	}

	public void setChCurrencyCode(BigDecimal chCurrencyCode) {
		this.chCurrencyCode = chCurrencyCode;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public BigDecimal getDataId() {
		return this.dataId;
	}

	public void setDataId(BigDecimal dataId) {
		this.dataId = dataId;
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

	public String getEditUser() {
		return this.editUser;
	}

	public void setEditUser(String editUser) {
		this.editUser = editUser;
	}

	public String getF100Upi() {
		return this.f100Upi;
	}

	public void setF100Upi(String f100Upi) {
		this.f100Upi = f100Upi;
	}

	public BigDecimal getF4() {
		return this.f4;
	}

	public void setF4(BigDecimal f4) {
		this.f4 = f4;
	}

	public BigDecimal getF49() {
		return this.f49;
	}

	public void setF49(BigDecimal f49) {
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

	public String getF60Upi() {
		return this.f60Upi;
	}

	public void setF60Upi(String f60Upi) {
		this.f60Upi = f60Upi;
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

	public BigDecimal getForwardInst() {
		return this.forwardInst;
	}

	public void setForwardInst(BigDecimal forwardInst) {
		this.forwardInst = forwardInst;
	}

	public String getFromSys() {
		return this.fromSys;
	}

	public void setFromSys(String fromSys) {
		this.fromSys = fromSys;
	}

	public BigDecimal getIrfacqbnb() {
		return this.irfacqbnb;
	}

	public void setIrfacqbnb(BigDecimal irfacqbnb) {
		this.irfacqbnb = irfacqbnb;
	}

	public BigDecimal getIrfacqiss() {
		return this.irfacqiss;
	}

	public void setIrfacqiss(BigDecimal irfacqiss) {
		this.irfacqiss = irfacqiss;
	}

	public BigDecimal getIrfbnbacq() {
		return this.irfbnbacq;
	}

	public void setIrfbnbacq(BigDecimal irfbnbacq) {
		this.irfbnbacq = irfbnbacq;
	}

	public BigDecimal getIrfbnbiss() {
		return this.irfbnbiss;
	}

	public void setIrfbnbiss(BigDecimal irfbnbiss) {
		this.irfbnbiss = irfbnbiss;
	}

	public BigDecimal getIrfissacq() {
		return this.irfissacq;
	}

	public void setIrfissacq(BigDecimal irfissacq) {
		this.irfissacq = irfissacq;
	}

	public BigDecimal getIrfissbnb() {
		return this.irfissbnb;
	}

	public void setIrfissbnb(BigDecimal irfissbnb) {
		this.irfissbnb = irfissbnb;
	}

	public BigDecimal getIsPartRev() {
		return this.isPartRev;
	}

	public void setIsPartRev(BigDecimal isPartRev) {
		this.isPartRev = isPartRev;
	}

	public BigDecimal getIsrev() {
		return this.isrev;
	}

	public void setIsrev(BigDecimal isrev) {
		this.isrev = isrev;
	}

	public BigDecimal getIssConvRate() {
		return this.issConvRate;
	}

	public void setIssConvRate(BigDecimal issConvRate) {
		this.issConvRate = issConvRate;
	}

	public BigDecimal getIssCurrencyCode() {
		return this.issCurrencyCode;
	}

	public void setIssCurrencyCode(BigDecimal issCurrencyCode) {
		this.issCurrencyCode = issCurrencyCode;
	}

	public String getIssSend() {
		return this.issSend;
	}

	public void setIssSend(String issSend) {
		this.issSend = issSend;
	}

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public BigDecimal getIssuerFe() {
		return this.issuerFe;
	}

	public void setIssuerFe(BigDecimal issuerFe) {
		this.issuerFe = issuerFe;
	}

	public BigDecimal getIssuerRp() {
		return this.issuerRp;
	}

	public void setIssuerRp(BigDecimal issuerRp) {
		this.issuerRp = issuerRp;
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

	public BigDecimal getMerchantTypeOrig() {
		return this.merchantTypeOrig;
	}

	public void setMerchantTypeOrig(BigDecimal merchantTypeOrig) {
		this.merchantTypeOrig = merchantTypeOrig;
	}

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public String getMsgtypeDetail() {
		return this.msgtypeDetail;
	}

	public void setMsgtypeDetail(String msgtypeDetail) {
		this.msgtypeDetail = msgtypeDetail;
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

	public String getOnlySml() {
		return this.onlySml;
	}

	public void setOnlySml(String onlySml) {
		this.onlySml = onlySml;
	}

	public String getOrigiss() {
		return this.origiss;
	}

	public void setOrigiss(String origiss) {
		this.origiss = origiss;
	}

	public BigDecimal getOrigrespcode() {
		return this.origrespcode;
	}

	public void setOrigrespcode(BigDecimal origrespcode) {
		this.origrespcode = origrespcode;
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

	public String getPcode() {
		return this.pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public BigDecimal getPcodeOrig() {
		return this.pcodeOrig;
	}

	public void setPcodeOrig(BigDecimal pcodeOrig) {
		this.pcodeOrig = pcodeOrig;
	}

	public BigDecimal getPcode2() {
		return this.pcode2;
	}

	public void setPcode2(BigDecimal pcode2) {
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

	public BigDecimal getPpcode() {
		return this.ppcode;
	}

	public void setPpcode(BigDecimal ppcode) {
		this.ppcode = ppcode;
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

	public BigDecimal getPreamountAcq() {
		return this.preamountAcq;
	}

	public void setPreamountAcq(BigDecimal preamountAcq) {
		this.preamountAcq = preamountAcq;
	}

	public BigDecimal getPreamountUsd() {
		return this.preamountUsd;
	}

	public void setPreamountUsd(BigDecimal preamountUsd) {
		this.preamountUsd = preamountUsd;
	}

	public BigDecimal getRc() {
		return this.rc;
	}

	public void setRc(BigDecimal rc) {
		this.rc = rc;
	}

	public BigDecimal getRcAcq72() {
		return this.rcAcq72;
	}

	public void setRcAcq72(BigDecimal rcAcq72) {
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

	public BigDecimal getRcIss72() {
		return this.rcIss72;
	}

	public void setRcIss72(BigDecimal rcIss72) {
		this.rcIss72 = rcIss72;
	}

	public BigDecimal getReFee() {
		return this.reFee;
	}

	public void setReFee(BigDecimal reFee) {
		this.reFee = reFee;
	}

	public String getRefnum() {
		return this.refnum;
	}

	public void setRefnum(String refnum) {
		this.refnum = refnum;
	}

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public BigDecimal getRevcode() {
		return this.revcode;
	}

	public void setRevcode(BigDecimal revcode) {
		this.revcode = revcode;
	}

	public BigDecimal getSettCurrencyCode() {
		return this.settCurrencyCode;
	}

	public void setSettCurrencyCode(BigDecimal settCurrencyCode) {
		this.settCurrencyCode = settCurrencyCode;
	}

	public BigDecimal getSettlementAmount() {
		return this.settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public BigDecimal getSettlementCode() {
		return this.settlementCode;
	}

	public void setSettlementCode(BigDecimal settlementCode) {
		this.settlementCode = settlementCode;
	}

	public BigDecimal getSettlementConvRate() {
		return this.settlementConvRate;
	}

	public void setSettlementConvRate(BigDecimal settlementConvRate) {
		this.settlementConvRate = settlementConvRate;
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

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public BigDecimal getSvfacqnp() {
		return this.svfacqnp;
	}

	public void setSvfacqnp(BigDecimal svfacqnp) {
		this.svfacqnp = svfacqnp;
	}

	public BigDecimal getSvfbnbnp() {
		return this.svfbnbnp;
	}

	public void setSvfbnbnp(BigDecimal svfbnbnp) {
		this.svfbnbnp = svfbnbnp;
	}

	public BigDecimal getSvfissnp() {
		return this.svfissnp;
	}

	public void setSvfissnp(BigDecimal svfissnp) {
		this.svfissnp = svfissnp;
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

	public Date getTgxlnv() {
		return this.tgxlnv;
	}

	public void setTgxlnv(Date tgxlnv) {
		this.tgxlnv = tgxlnv;
	}

	public BigDecimal getThu() {
		return this.thu;
	}

	public void setThu(BigDecimal thu) {
		this.thu = thu;
	}

	public BigDecimal getThuRefun() {
		return this.thuRefun;
	}

	public void setThuRefun(BigDecimal thuRefun) {
		this.thuRefun = thuRefun;
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

	public BigDecimal getTransferee() {
		return this.transferee;
	}

	public void setTransferee(BigDecimal transferee) {
		this.transferee = transferee;
	}

	public String getTransitCsrr() {
		return this.transitCsrr;
	}

	public void setTransitCsrr(String transitCsrr) {
		this.transitCsrr = transitCsrr;
	}

	public BigDecimal getTrantime() {
		return this.trantime;
	}

	public void setTrantime(BigDecimal trantime) {
		this.trantime = trantime;
	}

	public BigDecimal getTt1() {
		return this.tt1;
	}

	public void setTt1(BigDecimal tt1) {
		this.tt1 = tt1;
	}

	public BigDecimal getTt2() {
		return this.tt2;
	}

	public void setTt2(BigDecimal tt2) {
		this.tt2 = tt2;
	}

	public BigDecimal getTt3() {
		return this.tt3;
	}

	public void setTt3(BigDecimal tt3) {
		this.tt3 = tt3;
	}

	public BigDecimal getTt4() {
		return this.tt4;
	}

	public void setTt4(BigDecimal tt4) {
		this.tt4 = tt4;
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