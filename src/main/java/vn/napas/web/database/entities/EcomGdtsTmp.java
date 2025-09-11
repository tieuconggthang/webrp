package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the ECOM_GDTS_TMP database table.
 * 
 */
@Entity
@Table(name="ECOM_GDTS_TMP")
@NamedQuery(name="EcomGdtsTmp.findAll", query="SELECT e FROM EcomGdtsTmp e")
public class EcomGdtsTmp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACQ_BANK_CODE")
	private String acqBankCode;

	private BigDecimal amount;

	@Column(name="AMOUNT_RETURN")
	private BigDecimal amountReturn;

	@Column(name="AMOUNT_TS")
	private BigDecimal amountTs;

	@Column(name="BANK_ID")
	private String bankId;

	@Column(name="CARD_NUMBER")
	private String cardNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_INSERT")
	private Date dateInsert;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TL")
	private Date dateTl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_YC")
	private Date dateYc;

	@Column(name="FILE_TL")
	private String fileTl;

	@Column(name="FILE_YC")
	private String fileYc;

	@Column(name="ID_TS")
	private BigDecimal idTs;

	@Column(name="ISSUER_BANK_CODE")
	private String issuerBankCode;

	@Column(name="KSV_TL")
	private String ksvTl;

	@Column(name="KSV_YC")
	private String ksvYc;

	@Column(name="MA_TL")
	private BigDecimal maTl;

	@Column(name="MA_TS")
	private BigDecimal maTs;

	@Column(name="MERCHANT_CODE")
	private String merchantCode;

	private BigDecimal msgtype;

	@Column(name="NOTE_TL")
	private String noteTl;

	@Column(name="NOTE_YC")
	private String noteYc;

	@Column(name="RESPONSE_CODE")
	private String responseCode;

	@Column(name="RETURN_CODE")
	private BigDecimal returnCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SEND_TIME")
	private Date sendTime;

	private BigDecimal status;

	@Column(name="TRANSACTION_CODE")
	private String transactionCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TRANSACTION_DATE")
	private Date transactionDate;

	@Column(name="TRANSACTION_TYPE")
	private String transactionType;

	@Column(name="TSV_TL")
	private String tsvTl;

	@Column(name="TSV_YC")
	private String tsvYc;

	public EcomGdtsTmp() {
	}

	public String getAcqBankCode() {
		return this.acqBankCode;
	}

	public void setAcqBankCode(String acqBankCode) {
		this.acqBankCode = acqBankCode;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public String getCardNumber() {
		return this.cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Date getDateInsert() {
		return this.dateInsert;
	}

	public void setDateInsert(Date dateInsert) {
		this.dateInsert = dateInsert;
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

	public BigDecimal getIdTs() {
		return this.idTs;
	}

	public void setIdTs(BigDecimal idTs) {
		this.idTs = idTs;
	}

	public String getIssuerBankCode() {
		return this.issuerBankCode;
	}

	public void setIssuerBankCode(String issuerBankCode) {
		this.issuerBankCode = issuerBankCode;
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

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
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

	public String getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public BigDecimal getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(BigDecimal returnCode) {
		this.returnCode = returnCode;
	}

	public Date getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public BigDecimal getStatus() {
		return this.status;
	}

	public void setStatus(BigDecimal status) {
		this.status = status;
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

}