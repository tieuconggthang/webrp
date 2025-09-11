package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the ACH_RETURN_MESSSAGE database table.
 * 
 */
@Entity
@Table(name="ACH_RETURN_MESSSAGE")
@NamedQuery(name="AchReturnMesssage.findAll", query="SELECT a FROM AchReturnMesssage a")
public class AchReturnMesssage implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal acquirer;

	private BigDecimal amount;

	@Column(name="AMOUNT_RETURN")
	private BigDecimal amountReturn;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Column(name="CODE_REF")
	private BigDecimal codeRef;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_INSERT")
	private Date dateInsert;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_SEND")
	private Date dateSend;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	private BigDecimal issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="LOG_ID")
	private BigDecimal logId;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	@Column(name="MESSAGE_RETURN_CODE")
	private BigDecimal messageReturnCode;

	private BigDecimal msgtype;

	private String note;

	private BigDecimal origtrace;

	private String pan;

	private BigDecimal pcode;

	@Column(name="RETURN_CODE")
	private BigDecimal returnCode;

	private BigDecimal status;

	private BigDecimal stt;

	private String termid;

	public AchReturnMesssage() {
	}

	public BigDecimal getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(BigDecimal acquirer) {
		this.acquirer = acquirer;
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

	public BigDecimal getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigDecimal bbBin) {
		this.bbBin = bbBin;
	}

	public BigDecimal getCodeRef() {
		return this.codeRef;
	}

	public void setCodeRef(BigDecimal codeRef) {
		this.codeRef = codeRef;
	}

	public Date getDateInsert() {
		return this.dateInsert;
	}

	public void setDateInsert(Date dateInsert) {
		this.dateInsert = dateInsert;
	}

	public Date getDateSend() {
		return this.dateSend;
	}

	public void setDateSend(Date dateSend) {
		this.dateSend = dateSend;
	}

	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public BigDecimal getIssuer() {
		return this.issuer;
	}

	public void setIssuer(BigDecimal issuer) {
		this.issuer = issuer;
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

	public BigDecimal getLogId() {
		return this.logId;
	}

	public void setLogId(BigDecimal logId) {
		this.logId = logId;
	}

	public BigDecimal getMerchantType() {
		return this.merchantType;
	}

	public void setMerchantType(BigDecimal merchantType) {
		this.merchantType = merchantType;
	}

	public BigDecimal getMessageReturnCode() {
		return this.messageReturnCode;
	}

	public void setMessageReturnCode(BigDecimal messageReturnCode) {
		this.messageReturnCode = messageReturnCode;
	}

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public BigDecimal getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(BigDecimal returnCode) {
		this.returnCode = returnCode;
	}

	public BigDecimal getStatus() {
		return this.status;
	}

	public void setStatus(BigDecimal status) {
		this.status = status;
	}

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public String getTermid() {
		return this.termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

}