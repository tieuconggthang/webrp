package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the ECOM_XLSL database table.
 * 
 */
@Entity
@Table(name="ECOM_XLSL")
@NamedQuery(name="EcomXlsl.findAll", query="SELECT e FROM EcomXlsl e")
public class EcomXlsl implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACQ_BANK_CODE")
	private String acqBankCode;

	@Column(name="ADJUST_AMOUNT")
	private BigDecimal adjustAmount;

	@Column(name="ADJUST_TRANX_CODE")
	private String adjustTranxCode;

	private BigDecimal amount;

	@Column(name="BANK_ID")
	private String bankId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CARD_EXPIRED_DATE")
	private Date cardExpiredDate;

	@Column(name="CARD_HOLDER_NAME")
	private String cardHolderName;

	@Column(name="CARD_NUMBER")
	private String cardNumber;

	private String currency;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_XL")
	private Date dateXl;

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

	@Column(name="F41_CARD_ACCEPTOR_ID")
	private String f41CardAcceptorId;

	@Column(name="ISSUER_BANK_CODE")
	private String issuerBankCode;

	@Column(name="ISSUER_BANK_NAME")
	private String issuerBankName;

	@Column(name="MA_XL")
	private String maXl;

	@Column(name="MERCHANT_CODE")
	private String merchantCode;

	@Column(name="MERCHANT_NAME")
	private String merchantName;

	@Column(name="PAYMENT_GATEWAY")
	private String paymentGateway;

	@Column(name="PAYMENT_METHOD")
	private String paymentMethod;

	@Column(name="RESPONSE_CODE")
	private String responseCode;

	private BigDecimal stt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TIME_SYN")
	private Date timeSyn;

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

	private BigDecimal type;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_DATE")
	private Date updateDate;

	@Column(name="USER_XL")
	private String userXl;

	public EcomXlsl() {
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

	public String getBankId() {
		return this.bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
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

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getDateXl() {
		return this.dateXl;
	}

	public void setDateXl(Date dateXl) {
		this.dateXl = dateXl;
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

	public String getF41CardAcceptorId() {
		return this.f41CardAcceptorId;
	}

	public void setF41CardAcceptorId(String f41CardAcceptorId) {
		this.f41CardAcceptorId = f41CardAcceptorId;
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

	public String getMaXl() {
		return this.maXl;
	}

	public void setMaXl(String maXl) {
		this.maXl = maXl;
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

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public Date getTimeSyn() {
		return this.timeSyn;
	}

	public void setTimeSyn(Date timeSyn) {
		this.timeSyn = timeSyn;
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

	public String getUserXl() {
		return this.userXl;
	}

	public void setUserXl(String userXl) {
		this.userXl = userXl;
	}

}