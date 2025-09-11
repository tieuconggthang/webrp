package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the ECOM_GDTS database table.
 * 
 */
@Entity
@Table(name="ECOM_GDTS")
@NamedQuery(name="EcomGdt.findAll", query="SELECT e FROM EcomGdt e")
public class EcomGdt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACQ_BANK_CODE")
	private String acqBankCode;

	@Column(name="ADJUST_AMOUNT")
	private BigDecimal adjustAmount;

	@Column(name="ADJUST_TRANX_CODE")
	private String adjustTranxCode;

	private BigDecimal amount;

	@Column(name="AMOUNT_GHINO")
	private BigDecimal amountGhino;

	@Column(name="AMOUNT_RETURN")
	private BigDecimal amountReturn;

	@Column(name="AMOUNT_TS")
	private BigDecimal amountTs;

	@Column(name="BANK_ID")
	private String bankId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CANCEL_DATE")
	private Date cancelDate;

	@Column(name="CANCEL_USER")
	private String cancelUser;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CARD_EXPIRED_DATE")
	private Date cardExpiredDate;

	@Column(name="CARD_HOLDER_NAME")
	private String cardHolderName;

	@Column(name="CARD_NUMBER")
	private String cardNumber;

	@Column(name="CARD_NUMBER_DETAIL")
	private String cardNumberDetail;

	private String currency;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_GHINO")
	private Date dateGhino;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_RECEIVE")
	private Date dateReceive;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TL")
	private Date dateTl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_YC")
	private Date dateYc;

	@Column(name="F103_TO_ACC")
	private String f103ToAcc;

	@Column(name="F11_TRACE")
	private BigDecimal f11Trace;

	@Column(name="F12_LOCAL_TIME")
	private BigDecimal f12LocalTime;

	@Column(name="F13_LOCAL_DATE")
	private String f13LocalDate;

	@Column(name="F15_SETTLE_DATE")
	private String f15SettleDate;

	@Column(name="F32_ACQUIRER")
	private String f32Acquirer;

	@Column(name="F32_ACQUIRER_ORIGIN")
	private BigDecimal f32AcquirerOrigin;

	@Column(name="F41_CARD_ACCEPTOR_ID")
	private String f41CardAcceptorId;

	@Column(name="FILE_TL")
	private String fileTl;

	@Column(name="FILE_YC")
	private String fileYc;

	@Column(name="ID_TS")
	private BigInteger idTs;

	@Column(name="IS_GHINO")
	private BigDecimal isGhino;

	@Column(name="IS_QRECOM")
	private BigDecimal isQrecom;

	@Column(name="IS_RECEIVE")
	private BigDecimal isReceive;

	@Column(name="IS_WS")
	private BigDecimal isWs;

	@Column(name="ISSUER_BANK_CODE")
	private String issuerBankCode;

	@Column(name="ISSUER_BANK_NAME")
	private String issuerBankName;

	@Column(name="KS_GHINO")
	private String ksGhino;

	@Column(name="KSV_DUYET_TL")
	private BigDecimal ksvDuyetTl;

	@Column(name="KSV_DUYET_YC")
	private BigDecimal ksvDuyetYc;

	@Column(name="KSV_TL")
	private String ksvTl;

	@Column(name="KSV_YC")
	private String ksvYc;

	@Column(name="KSVD_GHINO")
	private BigDecimal ksvdGhino;

	@Column(name="MA_GHINO")
	private BigDecimal maGhino;

	@Column(name="MA_TL")
	private BigDecimal maTl;

	@Column(name="MA_TS")
	private BigDecimal maTs;

	@Column(name="MERCHANT_CODE")
	private String merchantCode;

	@Column(name="MERCHANT_NAME")
	private String merchantName;

	@Column(name="NOTE_TL")
	private String noteTl;

	@Column(name="NOTE_YC")
	private String noteYc;

	@Column(name="PAYMENT_GATEWAY")
	private String paymentGateway;

	@Column(name="PAYMENT_METHOD")
	private String paymentMethod;

	@Column(name="RESPONSE_CODE")
	private String responseCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SYNC_SWI")
	private Date syncSwi;

	@Column(name="TRANSACTION_CODE")
	private String transactionCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TRANSACTION_DATE")
	private Date transactionDate;

	@Column(name="TRANSACTION_INFO")
	private String transactionInfo;

	@Column(name="TRANSACTION_REF")
	private String transactionRef;

	@Column(name="TRANSACTION_TYPE")
	private String transactionType;

	@Column(name="TSV_TL")
	private String tsvTl;

	@Column(name="TSV_YC")
	private String tsvYc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE")
	private Date updateDate;

	@Column(name="USER_GHINO")
	private String userGhino;

	public EcomGdt() {
	}

	public String getAcqBankCode() {
		return this.acqBankCode;
	}

	public void setAcqBankCode(String acqBankCode) {
		this.acqBankCode = acqBankCode;
	}

	public BigDecimal getAdjustAmount() {
		return this.adjustAmount;
	}

	public void setAdjustAmount(BigDecimal adjustAmount) {
		this.adjustAmount = adjustAmount;
	}

	public String getAdjustTranxCode() {
		return this.adjustTranxCode;
	}

	public void setAdjustTranxCode(String adjustTranxCode) {
		this.adjustTranxCode = adjustTranxCode;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountGhino() {
		return this.amountGhino;
	}

	public void setAmountGhino(BigDecimal amountGhino) {
		this.amountGhino = amountGhino;
	}

	public BigDecimal getAmountReturn() {
		return this.amountReturn;
	}

	public void setAmountReturn(BigDecimal amountReturn) {
		this.amountReturn = amountReturn;
	}

	public BigDecimal getAmountTs() {
		return this.amountTs;
	}

	public void setAmountTs(BigDecimal amountTs) {
		this.amountTs = amountTs;
	}

	public String getBankId() {
		return this.bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public Date getCancelDate() {
		return this.cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getCancelUser() {
		return this.cancelUser;
	}

	public void setCancelUser(String cancelUser) {
		this.cancelUser = cancelUser;
	}

	public Date getCardExpiredDate() {
		return this.cardExpiredDate;
	}

	public void setCardExpiredDate(Date cardExpiredDate) {
		this.cardExpiredDate = cardExpiredDate;
	}

	public String getCardHolderName() {
		return this.cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardNumberDetail() {
		return this.cardNumberDetail;
	}

	public void setCardNumberDetail(String cardNumberDetail) {
		this.cardNumberDetail = cardNumberDetail;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getDateGhino() {
		return this.dateGhino;
	}

	public void setDateGhino(Date dateGhino) {
		this.dateGhino = dateGhino;
	}

	public Date getDateReceive() {
		return this.dateReceive;
	}

	public void setDateReceive(Date dateReceive) {
		this.dateReceive = dateReceive;
	}

	public Date getDateTl() {
		return this.dateTl;
	}

	public void setDateTl(Date dateTl) {
		this.dateTl = dateTl;
	}

	public Date getDateYc() {
		return this.dateYc;
	}

	public void setDateYc(Date dateYc) {
		this.dateYc = dateYc;
	}

	public String getF103ToAcc() {
		return this.f103ToAcc;
	}

	public void setF103ToAcc(String f103ToAcc) {
		this.f103ToAcc = f103ToAcc;
	}

	public BigDecimal getF11Trace() {
		return this.f11Trace;
	}

	public void setF11Trace(BigDecimal f11Trace) {
		this.f11Trace = f11Trace;
	}

	public BigDecimal getF12LocalTime() {
		return this.f12LocalTime;
	}

	public void setF12LocalTime(BigDecimal f12LocalTime) {
		this.f12LocalTime = f12LocalTime;
	}

	public String getF13LocalDate() {
		return this.f13LocalDate;
	}

	public void setF13LocalDate(String f13LocalDate) {
		this.f13LocalDate = f13LocalDate;
	}

	public String getF15SettleDate() {
		return this.f15SettleDate;
	}

	public void setF15SettleDate(String f15SettleDate) {
		this.f15SettleDate = f15SettleDate;
	}

	public String getF32Acquirer() {
		return this.f32Acquirer;
	}

	public void setF32Acquirer(String f32Acquirer) {
		this.f32Acquirer = f32Acquirer;
	}

	public BigDecimal getF32AcquirerOrigin() {
		return this.f32AcquirerOrigin;
	}

	public void setF32AcquirerOrigin(BigDecimal f32AcquirerOrigin) {
		this.f32AcquirerOrigin = f32AcquirerOrigin;
	}

	public String getF41CardAcceptorId() {
		return this.f41CardAcceptorId;
	}

	public void setF41CardAcceptorId(String f41CardAcceptorId) {
		this.f41CardAcceptorId = f41CardAcceptorId;
	}

	public String getFileTl() {
		return this.fileTl;
	}

	public void setFileTl(String fileTl) {
		this.fileTl = fileTl;
	}

	public String getFileYc() {
		return this.fileYc;
	}

	public void setFileYc(String fileYc) {
		this.fileYc = fileYc;
	}

	public BigInteger getIdTs() {
		return this.idTs;
	}

	public void setIdTs(BigInteger idTs) {
		this.idTs = idTs;
	}

	public BigDecimal getIsGhino() {
		return this.isGhino;
	}

	public void setIsGhino(BigDecimal isGhino) {
		this.isGhino = isGhino;
	}

	public BigDecimal getIsQrecom() {
		return this.isQrecom;
	}

	public void setIsQrecom(BigDecimal isQrecom) {
		this.isQrecom = isQrecom;
	}

	public BigDecimal getIsReceive() {
		return this.isReceive;
	}

	public void setIsReceive(BigDecimal isReceive) {
		this.isReceive = isReceive;
	}

	public BigDecimal getIsWs() {
		return this.isWs;
	}

	public void setIsWs(BigDecimal isWs) {
		this.isWs = isWs;
	}

	public String getIssuerBankCode() {
		return this.issuerBankCode;
	}

	public void setIssuerBankCode(String issuerBankCode) {
		this.issuerBankCode = issuerBankCode;
	}

	public String getIssuerBankName() {
		return this.issuerBankName;
	}

	public void setIssuerBankName(String issuerBankName) {
		this.issuerBankName = issuerBankName;
	}

	public String getKsGhino() {
		return this.ksGhino;
	}

	public void setKsGhino(String ksGhino) {
		this.ksGhino = ksGhino;
	}

	public BigDecimal getKsvDuyetTl() {
		return this.ksvDuyetTl;
	}

	public void setKsvDuyetTl(BigDecimal ksvDuyetTl) {
		this.ksvDuyetTl = ksvDuyetTl;
	}

	public BigDecimal getKsvDuyetYc() {
		return this.ksvDuyetYc;
	}

	public void setKsvDuyetYc(BigDecimal ksvDuyetYc) {
		this.ksvDuyetYc = ksvDuyetYc;
	}

	public String getKsvTl() {
		return this.ksvTl;
	}

	public void setKsvTl(String ksvTl) {
		this.ksvTl = ksvTl;
	}

	public String getKsvYc() {
		return this.ksvYc;
	}

	public void setKsvYc(String ksvYc) {
		this.ksvYc = ksvYc;
	}

	public BigDecimal getKsvdGhino() {
		return this.ksvdGhino;
	}

	public void setKsvdGhino(BigDecimal ksvdGhino) {
		this.ksvdGhino = ksvdGhino;
	}

	public BigDecimal getMaGhino() {
		return this.maGhino;
	}

	public void setMaGhino(BigDecimal maGhino) {
		this.maGhino = maGhino;
	}

	public BigDecimal getMaTl() {
		return this.maTl;
	}

	public void setMaTl(BigDecimal maTl) {
		this.maTl = maTl;
	}

	public BigDecimal getMaTs() {
		return this.maTs;
	}

	public void setMaTs(BigDecimal maTs) {
		this.maTs = maTs;
	}

	public String getMerchantCode() {
		return this.merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getMerchantName() {
		return this.merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getNoteTl() {
		return this.noteTl;
	}

	public void setNoteTl(String noteTl) {
		this.noteTl = noteTl;
	}

	public String getNoteYc() {
		return this.noteYc;
	}

	public void setNoteYc(String noteYc) {
		this.noteYc = noteYc;
	}

	public String getPaymentGateway() {
		return this.paymentGateway;
	}

	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	public String getPaymentMethod() {
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public Date getSyncSwi() {
		return this.syncSwi;
	}

	public void setSyncSwi(Date syncSwi) {
		this.syncSwi = syncSwi;
	}

	public String getTransactionCode() {
		return this.transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public Date getTransactionDate() {
		return this.transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionInfo() {
		return this.transactionInfo;
	}

	public void setTransactionInfo(String transactionInfo) {
		this.transactionInfo = transactionInfo;
	}

	public String getTransactionRef() {
		return this.transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public String getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getTsvTl() {
		return this.tsvTl;
	}

	public void setTsvTl(String tsvTl) {
		this.tsvTl = tsvTl;
	}

	public String getTsvYc() {
		return this.tsvYc;
	}

	public void setTsvYc(String tsvYc) {
		this.tsvYc = tsvYc;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUserGhino() {
		return this.userGhino;
	}

	public void setUserGhino(String userGhino) {
		this.userGhino = userGhino;
	}

}