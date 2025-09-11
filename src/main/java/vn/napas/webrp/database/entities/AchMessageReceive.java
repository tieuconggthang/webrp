package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the ACH_MESSAGE_RECEIVE database table.
 * 
 */
@Entity
@Table(name="ACH_MESSAGE_RECEIVE")
@NamedQuery(name="AchMessageReceive.findAll", query="SELECT a FROM AchMessageReceive a")
public class AchMessageReceive implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal amount;

	@Column(name="CARD_NO")
	private String cardNo;

	@Column(name="CLAIM_CODE")
	private BigDecimal claimCode;

	@Column(name="CLAIM_NOTE")
	private String claimNote;

	@Column(name="CODE_REFERENCE")
	private BigDecimal codeReference;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_INSERT")
	private Date dateInsert;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_PROCESS")
	private Date dateProcess;

	@Column(name="FILE_NAME")
	private String fileName;

	@Column(name="FORWARDING_ID")
	private BigDecimal forwardingId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="MESSAGE_TYPE")
	private BigDecimal messageType;

	@Column(name="NOTE_RESEND")
	private String noteResend;

	@Column(name="RECEIVER_BANK_ID")
	private BigDecimal receiverBankId;

	@Column(name="REQUEST_AMOUNT")
	private BigDecimal requestAmount;

	@Column(name="RETURN_CODE")
	private BigDecimal returnCode;

	@Column(name="SENDER_BANK_ID")
	private BigDecimal senderBankId;

	private BigDecimal status;

	private BigInteger stt;

	@Column(name="STT_SHCLOG")
	private BigDecimal sttShclog;

	private String termid;

	@Column(name="TRACE_NUMBER")
	private BigDecimal traceNumber;

	@Column(name="USER_RESEND")
	private String userResend;

	public AchMessageReceive() {
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public BigDecimal getClaimCode() {
		return this.claimCode;
	}

	public void setClaimCode(BigDecimal claimCode) {
		this.claimCode = claimCode;
	}

	public String getClaimNote() {
		return this.claimNote;
	}

	public void setClaimNote(String claimNote) {
		this.claimNote = claimNote;
	}

	public BigDecimal getCodeReference() {
		return this.codeReference;
	}

	public void setCodeReference(BigDecimal codeReference) {
		this.codeReference = codeReference;
	}

	public Date getDateInsert() {
		return this.dateInsert;
	}

	public void setDateInsert(Date dateInsert) {
		this.dateInsert = dateInsert;
	}

	public Date getDateProcess() {
		return this.dateProcess;
	}

	public void setDateProcess(Date dateProcess) {
		this.dateProcess = dateProcess;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public BigDecimal getForwardingId() {
		return this.forwardingId;
	}

	public void setForwardingId(BigDecimal forwardingId) {
		this.forwardingId = forwardingId;
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

	public BigDecimal getMessageType() {
		return this.messageType;
	}

	public void setMessageType(BigDecimal messageType) {
		this.messageType = messageType;
	}

	public String getNoteResend() {
		return this.noteResend;
	}

	public void setNoteResend(String noteResend) {
		this.noteResend = noteResend;
	}

	public BigDecimal getReceiverBankId() {
		return this.receiverBankId;
	}

	public void setReceiverBankId(BigDecimal receiverBankId) {
		this.receiverBankId = receiverBankId;
	}

	public BigDecimal getRequestAmount() {
		return this.requestAmount;
	}

	public void setRequestAmount(BigDecimal requestAmount) {
		this.requestAmount = requestAmount;
	}

	public BigDecimal getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(BigDecimal returnCode) {
		this.returnCode = returnCode;
	}

	public BigDecimal getSenderBankId() {
		return this.senderBankId;
	}

	public void setSenderBankId(BigDecimal senderBankId) {
		this.senderBankId = senderBankId;
	}

	public BigDecimal getStatus() {
		return this.status;
	}

	public void setStatus(BigDecimal status) {
		this.status = status;
	}

	public BigInteger getStt() {
		return this.stt;
	}

	public void setStt(BigInteger stt) {
		this.stt = stt;
	}

	public BigDecimal getSttShclog() {
		return this.sttShclog;
	}

	public void setSttShclog(BigDecimal sttShclog) {
		this.sttShclog = sttShclog;
	}

	public String getTermid() {
		return this.termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

	public BigDecimal getTraceNumber() {
		return this.traceNumber;
	}

	public void setTraceNumber(BigDecimal traceNumber) {
		this.traceNumber = traceNumber;
	}

	public String getUserResend() {
		return this.userResend;
	}

	public void setUserResend(String userResend) {
		this.userResend = userResend;
	}

}