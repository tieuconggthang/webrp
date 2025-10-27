package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the SHCLOG_SETT_IBFT database table.
 * 
 */
@Entity
@Table(name = "SHCLOG_SETT_IBFT")
@NamedQuery(name = "ShclogSettIbft.findAll", query = "SELECT s FROM ShclogSettIbft s")
public class ShclogSettIbft implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tidb_id")
	private Long tidbId;

	private String acceptorname;

	@Column(name = "ACCOUNT_NO")
	private String accountNo;

	private String acctnum;

	private String acctnum1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACH_EDIT_DATE")
	private Date achEditDate;

	@Column(name = "ACH_RECONCIL_STATUS")
	private String achReconcilStatus;

	@Column(name = "ACQ_COUNTRY")
	private BigDecimal acqCountry;

	@Column(name = "ACQ_CURRENCY_CODE")
	private BigDecimal acqCurrencyCode;

	@Column(name = "ACQ_RQ")
	private BigDecimal acqRq;

	private BigDecimal acquirer;

	@Column(name = "ACQUIRER_FE")
	private BigDecimal acquirerFe;

	@Column(name = "ACQUIRER_REF")
	private String acquirerRef;

	@Column(name = "ACQUIRER_RP")
	private BigDecimal acquirerRp;

	@Column(name = "ADD_INFO")
	private String addInfo;

	private String addresponse;

	private BigDecimal amount;

	private String authnum;

	@Column(name = "BB_ACCOUNT")
	private String bbAccount;

	@Column(name = "BB_BIN")
	private BigDecimal bbBin;

	@Column(name = "BB_BIN_ORIG")
	private BigDecimal bbBinOrig;

	@Column(name = "BNB_ACC")
	private String bnbAcc;

	@Column(name = "BNB_SWC")
	private String bnbSwc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CAP_DATE")
	private Date capDate;

	@Column(name = "CARD_SEQNO")
	private BigDecimal cardSeqno;

	@Column(name = "CARDHOLDER_AMOUNT")
	private BigDecimal cardholderAmount;

	@Column(name = "CARDHOLDER_CONV_RATE")
	private BigDecimal cardholderConvRate;

	private String cardproduct;

	@Column(name = "CH_CURRENCY_CODE")
	private BigDecimal chCurrencyCode;

	@Column(name = "CHIP_INDEX")
	private String chipIndex;

	@Column(name = "CODE_REF")
	private BigDecimal codeRef;

	@Column(name = "CONFIG_FEE_ID")
	private BigDecimal configFeeId;

	@Column(name = "CONTENT_FUND")
	private String contentFund;

	@Column(name = "CONV_RATE")
	private BigDecimal convRate;

	@Column(name = "CONV_RATE_ACQ")
	private BigDecimal convRateAcq;

	@Column(name = "DATA_ID")
	private BigDecimal dataId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_XL_TLTO")
	private Date dateXlTlto;

	private String des;

	@Column(name = "DEST_ACCOUNT")
	private String destAccount;

	@Column(name = "DEVICE_FEE")
	private BigDecimal deviceFee;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EDIT_DATE")
	private Date editDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EDIT_DATE_INS")
	private Date editDateIns;

	@Column(name = "EDIT_USER")
	private String editUser;

	private String endtoendid;

	private BigDecimal entityid;

	@Column(name = "F100_UPI")
	private String f100Upi;

	@Temporal(TemporalType.TIMESTAMP)
	private Date f15;

	private BigDecimal f4;

	private BigDecimal f49;

	private BigDecimal f5;

	private BigDecimal f6;

	@Column(name = "F60_UPI")
	private String f60Upi;

	private String f90;

	private BigDecimal famount;

	private BigDecimal fee;

	@Column(name = "FEE_ACQ")
	private BigDecimal feeAcq;

	@Column(name = "FEE_IRF_ACQ")
	private BigDecimal feeIrfAcq;

	@Column(name = "FEE_IRF_ACQ_NO_VAT")
	private BigDecimal feeIrfAcqNoVat;

	@Column(name = "FEE_IRF_BEN")
	private BigDecimal feeIrfBen;

	@Column(name = "FEE_IRF_BEN_NO_VAT")
	private BigDecimal feeIrfBenNoVat;

	@Column(name = "FEE_IRF_ISS")
	private BigDecimal feeIrfIss;

	@Column(name = "FEE_IRF_ISS_NO_VAT")
	private BigDecimal feeIrfIssNoVat;

	@Column(name = "FEE_IRF_PAY_AT")
	private BigDecimal feeIrfPayAt;

	@Column(name = "FEE_IRF_QT")
	private BigDecimal feeIrfQt;

	@Column(name = "FEE_IRF_REC_AT")
	private BigDecimal feeIrfRecAt;

	@Column(name = "FEE_ISS")
	private BigDecimal feeIss;

	@Column(name = "FEE_KEY")
	private String feeKey;

	@Column(name = "FEE_NOTE")
	private String feeNote;

	@Column(name = "FEE_PACKAGE_TYPE")
	private String feePackageType;

	@Column(name = "FEE_PAY_AT")
	private BigDecimal feePayAt;

	@Column(name = "FEE_PAY_DF")
	private BigDecimal feePayDf;

	@Column(name = "FEE_REC_AT")
	private BigDecimal feeRecAt;

	@Column(name = "FEE_REC_DF")
	private BigDecimal feeRecDf;

	@Column(name = "FEE_SERVICE")
	private BigDecimal feeService;

	@Column(name = "FEE_SVF_ACQ")
	private BigDecimal feeSvfAcq;

	@Column(name = "FEE_SVF_ACQ_NO_VAT")
	private BigDecimal feeSvfAcqNoVat;

	@Column(name = "FEE_SVF_BEN")
	private BigDecimal feeSvfBen;

	@Column(name = "FEE_SVF_BEN_NO_VAT")
	private BigDecimal feeSvfBenNoVat;

	@Column(name = "FEE_SVF_ISS")
	private BigDecimal feeSvfIss;

	@Column(name = "FEE_SVF_ISS_NO_VAT")
	private BigDecimal feeSvfIssNoVat;

	@Column(name = "FEE_SVF_PAY_AT")
	private BigDecimal feeSvfPayAt;

	@Column(name = "FEE_SVF_QT")
	private BigDecimal feeSvfQt;

	@Column(name = "FEE_SVF_REC_AT")
	private BigDecimal feeSvfRecAt;

	@Column(name = "FORWARD_INST")
	private BigDecimal forwardInst;

	@Column(name = "FROM_SML")
	private String fromSml;

	@Column(name = "FROM_SYS")
	private String fromSys;

	@Column(name = "INS_PCODE")
	private BigDecimal insPcode;

	@Column(name = "INS_TYPE_FEE")
	private String insTypeFee;

	private String instrid;

	private BigDecimal irfacqbnb;

	private BigDecimal irfacqiss;

	private BigDecimal irfbnbacq;

	private BigDecimal irfbnbiss;

	private BigDecimal irfissacq;

	private BigDecimal irfissbnb;

	@Column(name = "IS_PART_REV")
	private BigDecimal isPartRev;

	@Column(name = "IS_PARTIAL_SYNC")
	private BigDecimal isPartialSync;

	private BigDecimal isrev;

	@Column(name = "ISS_CONV_RATE")
	private BigDecimal issConvRate;

	@Column(name = "ISS_CURRENCY_CODE")
	private BigDecimal issCurrencyCode;

	@Column(name = "ISS_RQ")
	private BigDecimal issRq;

	private BigDecimal issuer;

	@Column(name = "ISSUER_DATA")
	private String issuerData;

	@Column(name = "ISSUER_FE")
	private BigDecimal issuerFe;

	@Column(name = "ISSUER_RP")
	private BigDecimal issuerRp;

	private String lddnv;

	private String loaigdreveso;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LOCAL_DATE")
	private Date localDate;

	@Column(name = "LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name = "MA_TLTO")
	private String maTlto;

	@Column(name = "MERCHANT_CODE")
	private String merchantCode;

	@Column(name = "MERCHANT_TYPE")
	private BigDecimal merchantType;

	@Column(name = "MERCHANT_TYPE_ORIG")
	private BigDecimal merchantTypeOrig;

	private String msgid;

	private BigDecimal msgtype;

	@Column(name = "MSGTYPE_DETAIL")
	private String msgtypeDetail;

	private String mvv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NAPAS_DATE")
	private Date napasDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NAPAS_EDIT_DATE")
	private Date napasEditDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NAPAS_EDIT_DATE_INS")
	private Date napasEditDateIns;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NAPAS_ND_DATE")
	private Date napasNdDate;

	@Column(name = "NEW_FEE")
	private BigDecimal newFee;

	@Column(name = "NSPK_F5")
	private BigDecimal nspkF5;

	@Column(name = "ONLY_SML")
	private String onlySml;

	@Column(name = "ORIG_ACQ")
	private BigDecimal origAcq;

	private String originator;

	private String origiss;

	private BigDecimal origrespcode;

	private BigDecimal origtrace;

	private String pan;

	private BigDecimal pcode;

	@Column(name = "PCODE_ORIG")
	private BigDecimal pcodeOrig;

	private BigDecimal pcode2;

	@Column(name = "POS_CONDITION_CODE")
	private BigDecimal posConditionCode;

	@Column(name = "POS_ENTRY_CODE")
	private BigDecimal posEntryCode;

	private BigDecimal ppcode;

	@Column(name = "PRE_CARDHOLDER_AMOUNT")
	private BigDecimal preCardholderAmount;

	private BigDecimal preamount;

	@Column(name = "PREAMOUNT_ACQ")
	private BigDecimal preamountAcq;

	@Column(name = "PREAMOUNT_TRANX")
	private BigDecimal preamountTranx;

	@Column(name = "PREAMOUNT_USD")
	private BigDecimal preamountUsd;

	private BigDecimal qamount;

	private BigDecimal ramount;

	private BigDecimal rc;

	@Column(name = "RC_ACQ")
	private String rcAcq;

	@Column(name = "RC_ACQ_72")
	private BigDecimal rcAcq72;

	@Column(name = "RC_BEN")
	private String rcBen;

	@Column(name = "RC_ISS")
	private String rcIss;

	@Column(name = "RC_ISS_72")
	private BigDecimal rcIss72;

	private BigDecimal reamount;

	@Column(name = "REASON_EDIT")
	private String reasonEdit;

	private String refnum;

	@Column(name = "REPAY_USD")
	private BigDecimal repayUsd;

	private BigDecimal respcode;

	@Column(name = "RESPCODE_GW")
	private String respcodeGw;

	private BigDecimal revcode;

	@Column(name = "SENDER_ACC")
	private String senderAcc;

	@Column(name = "SENDER_SWC")
	private String senderSwc;

	@Column(name = "SEQUENCE_IN_MONTH")
	private BigDecimal sequenceInMonth;

	@Column(name = "SETT_CURRENCY_CODE")
	private BigDecimal settCurrencyCode;

	@Column(name = "SETTLEMENT_AMOUNT")
	private BigDecimal settlementAmount;

	@Column(name = "SETTLEMENT_CODE")
	private BigDecimal settlementCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name = "SETTLEMENT_RATE")
	private BigDecimal settlementRate;

	private BigDecimal shcerror;

	@Column(name = "SML_VERIFY")
	private String smlVerify;

	private String src;

	private BigDecimal stt;

	@Column(name = "STT_ORIG")
	private BigDecimal sttOrig;

	private BigDecimal svfacqnp;

	private BigDecimal svfbnbnp;

	private BigDecimal svfissnp;

	private String tcc;

	private String termid;

	@Column(name = "TERMID_ACQ")
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

	@Column(name = "TOKEN_REQUESTOR")
	private String tokenRequestor;

	private BigDecimal trace;

	@Column(name = "TRACK2_SERVICE_CODE")
	private String track2ServiceCode;

	@Column(name = "TRAN_CASE")
	private String tranCase;

	@Temporal(TemporalType.TIMESTAMP)
	private Date trandate;

	@Column(name = "TRANSACTION_AMOUNT")
	private BigDecimal transactionAmount;

	private BigDecimal transferee;

	@Column(name = "TRANSIT_CSRR")
	private String transitCsrr;

	private BigDecimal trantime;

	@Column(name = "TXN_END_TIME")
	private BigDecimal txnEndTime;

	@Column(name = "TXN_START_TIME")
	private BigDecimal txnStartTime;

	private String txndest;

	private String txnsrc;

	public ShclogSettIbft() {
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

	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
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

	public Date getAchEditDate() {
		return this.achEditDate;
	}

	public void setAchEditDate(Date achEditDate) {
		this.achEditDate = achEditDate;
	}

	public String getAchReconcilStatus() {
		return this.achReconcilStatus;
	}

	public void setAchReconcilStatus(String achReconcilStatus) {
		this.achReconcilStatus = achReconcilStatus;
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

	public BigDecimal getAcqRq() {
		return this.acqRq;
	}

	public void setAcqRq(BigDecimal acqRq) {
		this.acqRq = acqRq;
	}

	public BigDecimal getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(BigDecimal acquirer) {
		this.acquirer = acquirer;
	}

	public BigDecimal getAcquirerFe() {
		return this.acquirerFe;
	}

	public void setAcquirerFe(BigDecimal acquirerFe) {
		this.acquirerFe = acquirerFe;
	}

	public String getAcquirerRef() {
		return this.acquirerRef;
	}

	public void setAcquirerRef(String acquirerRef) {
		this.acquirerRef = acquirerRef;
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

	public BigDecimal getBbBinOrig() {
		return this.bbBinOrig;
	}

	public void setBbBinOrig(BigDecimal bbBinOrig) {
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

	public BigDecimal getCardSeqno() {
		return this.cardSeqno;
	}

	public void setCardSeqno(BigDecimal cardSeqno) {
		this.cardSeqno = cardSeqno;
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

	public String getChipIndex() {
		return this.chipIndex;
	}

	public void setChipIndex(String chipIndex) {
		this.chipIndex = chipIndex;
	}

	public BigDecimal getCodeRef() {
		return this.codeRef;
	}

	public void setCodeRef(BigDecimal codeRef) {
		this.codeRef = codeRef;
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

	public BigDecimal getDataId() {
		return this.dataId;
	}

	public void setDataId(BigDecimal dataId) {
		this.dataId = dataId;
	}

	public Date getDateXlTlto() {
		return this.dateXlTlto;
	}

	public void setDateXlTlto(Date dateXlTlto) {
		this.dateXlTlto = dateXlTlto;
	}

	public String getDes() {
		return this.des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getDestAccount() {
		return this.destAccount;
	}

	public void setDestAccount(String destAccount) {
		this.destAccount = destAccount;
	}

	public BigDecimal getDeviceFee() {
		return this.deviceFee;
	}

	public void setDeviceFee(BigDecimal deviceFee) {
		this.deviceFee = deviceFee;
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

	public String getEndtoendid() {
		return this.endtoendid;
	}

	public void setEndtoendid(String endtoendid) {
		this.endtoendid = endtoendid;
	}

	public BigDecimal getEntityid() {
		return this.entityid;
	}

	public void setEntityid(BigDecimal entityid) {
		this.entityid = entityid;
	}

	public String getF100Upi() {
		return this.f100Upi;
	}

	public void setF100Upi(String f100Upi) {
		this.f100Upi = f100Upi;
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

	public String getF90() {
		return this.f90;
	}

	public void setF90(String f90) {
		this.f90 = f90;
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

	public BigDecimal getFeeIrfAcqNoVat() {
		return this.feeIrfAcqNoVat;
	}

	public void setFeeIrfAcqNoVat(BigDecimal feeIrfAcqNoVat) {
		this.feeIrfAcqNoVat = feeIrfAcqNoVat;
	}

	public BigDecimal getFeeIrfBen() {
		return this.feeIrfBen;
	}

	public void setFeeIrfBen(BigDecimal feeIrfBen) {
		this.feeIrfBen = feeIrfBen;
	}

	public BigDecimal getFeeIrfBenNoVat() {
		return this.feeIrfBenNoVat;
	}

	public void setFeeIrfBenNoVat(BigDecimal feeIrfBenNoVat) {
		this.feeIrfBenNoVat = feeIrfBenNoVat;
	}

	public BigDecimal getFeeIrfIss() {
		return this.feeIrfIss;
	}

	public void setFeeIrfIss(BigDecimal feeIrfIss) {
		this.feeIrfIss = feeIrfIss;
	}

	public BigDecimal getFeeIrfIssNoVat() {
		return this.feeIrfIssNoVat;
	}

	public void setFeeIrfIssNoVat(BigDecimal feeIrfIssNoVat) {
		this.feeIrfIssNoVat = feeIrfIssNoVat;
	}

	public BigDecimal getFeeIrfPayAt() {
		return this.feeIrfPayAt;
	}

	public void setFeeIrfPayAt(BigDecimal feeIrfPayAt) {
		this.feeIrfPayAt = feeIrfPayAt;
	}

	public BigDecimal getFeeIrfQt() {
		return this.feeIrfQt;
	}

	public void setFeeIrfQt(BigDecimal feeIrfQt) {
		this.feeIrfQt = feeIrfQt;
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

	public String getFeePackageType() {
		return this.feePackageType;
	}

	public void setFeePackageType(String feePackageType) {
		this.feePackageType = feePackageType;
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

	public BigDecimal getFeeSvfAcqNoVat() {
		return this.feeSvfAcqNoVat;
	}

	public void setFeeSvfAcqNoVat(BigDecimal feeSvfAcqNoVat) {
		this.feeSvfAcqNoVat = feeSvfAcqNoVat;
	}

	public BigDecimal getFeeSvfBen() {
		return this.feeSvfBen;
	}

	public void setFeeSvfBen(BigDecimal feeSvfBen) {
		this.feeSvfBen = feeSvfBen;
	}

	public BigDecimal getFeeSvfBenNoVat() {
		return this.feeSvfBenNoVat;
	}

	public void setFeeSvfBenNoVat(BigDecimal feeSvfBenNoVat) {
		this.feeSvfBenNoVat = feeSvfBenNoVat;
	}

	public BigDecimal getFeeSvfIss() {
		return this.feeSvfIss;
	}

	public void setFeeSvfIss(BigDecimal feeSvfIss) {
		this.feeSvfIss = feeSvfIss;
	}

	public BigDecimal getFeeSvfIssNoVat() {
		return this.feeSvfIssNoVat;
	}

	public void setFeeSvfIssNoVat(BigDecimal feeSvfIssNoVat) {
		this.feeSvfIssNoVat = feeSvfIssNoVat;
	}

	public BigDecimal getFeeSvfPayAt() {
		return this.feeSvfPayAt;
	}

	public void setFeeSvfPayAt(BigDecimal feeSvfPayAt) {
		this.feeSvfPayAt = feeSvfPayAt;
	}

	public BigDecimal getFeeSvfQt() {
		return this.feeSvfQt;
	}

	public void setFeeSvfQt(BigDecimal feeSvfQt) {
		this.feeSvfQt = feeSvfQt;
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

	public BigDecimal getInsPcode() {
		return this.insPcode;
	}

	public void setInsPcode(BigDecimal insPcode) {
		this.insPcode = insPcode;
	}

	public String getInsTypeFee() {
		return this.insTypeFee;
	}

	public void setInsTypeFee(String insTypeFee) {
		this.insTypeFee = insTypeFee;
	}

	public String getInstrid() {
		return this.instrid;
	}

	public void setInstrid(String instrid) {
		this.instrid = instrid;
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

	public BigDecimal getIsPartialSync() {
		return this.isPartialSync;
	}

	public void setIsPartialSync(BigDecimal isPartialSync) {
		this.isPartialSync = isPartialSync;
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

	public BigDecimal getIssRq() {
		return this.issRq;
	}

	public void setIssRq(BigDecimal issRq) {
		this.issRq = issRq;
	}

	public BigDecimal getIssuer() {
		return this.issuer;
	}

	public void setIssuer(BigDecimal issuer) {
		this.issuer = issuer;
	}

	public String getIssuerData() {
		return this.issuerData;
	}

	public void setIssuerData(String issuerData) {
		this.issuerData = issuerData;
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

	public BigDecimal getLocalTime() {
		return this.localTime;
	}

	public void setLocalTime(BigDecimal localTime) {
		this.localTime = localTime;
	}

	public String getMaTlto() {
		return this.maTlto;
	}

	public void setMaTlto(String maTlto) {
		this.maTlto = maTlto;
	}

	public String getMerchantCode() {
		return this.merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
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

	public String getMsgid() {
		return this.msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
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

	public BigDecimal getNspkF5() {
		return this.nspkF5;
	}

	public void setNspkF5(BigDecimal nspkF5) {
		this.nspkF5 = nspkF5;
	}

	public String getOnlySml() {
		return this.onlySml;
	}

	public void setOnlySml(String onlySml) {
		this.onlySml = onlySml;
	}

	public BigDecimal getOrigAcq() {
		return this.origAcq;
	}

	public void setOrigAcq(BigDecimal origAcq) {
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

	public BigDecimal getPcode() {
		return this.pcode;
	}

	public void setPcode(BigDecimal pcode) {
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

	public BigDecimal getPreamountTranx() {
		return this.preamountTranx;
	}

	public void setPreamountTranx(BigDecimal preamountTranx) {
		this.preamountTranx = preamountTranx;
	}

	public BigDecimal getPreamountUsd() {
		return this.preamountUsd;
	}

	public void setPreamountUsd(BigDecimal preamountUsd) {
		this.preamountUsd = preamountUsd;
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

	public BigDecimal getRc() {
		return this.rc;
	}

	public void setRc(BigDecimal rc) {
		this.rc = rc;
	}

	public String getRcAcq() {
		return this.rcAcq;
	}

	public void setRcAcq(String rcAcq) {
		this.rcAcq = rcAcq;
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

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public String getRespcodeGw() {
		return this.respcodeGw;
	}

	public void setRespcodeGw(String respcodeGw) {
		this.respcodeGw = respcodeGw;
	}

	public BigDecimal getRevcode() {
		return this.revcode;
	}

	public void setRevcode(BigDecimal revcode) {
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

	public BigDecimal getSequenceInMonth() {
		return this.sequenceInMonth;
	}

	public void setSequenceInMonth(BigDecimal sequenceInMonth) {
		this.sequenceInMonth = sequenceInMonth;
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

	public BigDecimal getShcerror() {
		return this.shcerror;
	}

	public void setShcerror(BigDecimal shcerror) {
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

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public BigDecimal getSttOrig() {
		return this.sttOrig;
	}

	public void setSttOrig(BigDecimal sttOrig) {
		this.sttOrig = sttOrig;
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

	public String getTokenRequestor() {
		return this.tokenRequestor;
	}

	public void setTokenRequestor(String tokenRequestor) {
		this.tokenRequestor = tokenRequestor;
	}

	public BigDecimal getTrace() {
		return this.trace;
	}

	public void setTrace(BigDecimal trace) {
		this.trace = trace;
	}

	public String getTrack2ServiceCode() {
		return this.track2ServiceCode;
	}

	public void setTrack2ServiceCode(String track2ServiceCode) {
		this.track2ServiceCode = track2ServiceCode;
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

	public BigDecimal getTxnEndTime() {
		return this.txnEndTime;
	}

	public void setTxnEndTime(BigDecimal txnEndTime) {
		this.txnEndTime = txnEndTime;
	}

	public BigDecimal getTxnStartTime() {
		return this.txnStartTime;
	}

	public void setTxnStartTime(BigDecimal txnStartTime) {
		this.txnStartTime = txnStartTime;
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