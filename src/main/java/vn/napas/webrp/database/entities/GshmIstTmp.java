package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the GSHM_IST_TMP database table.
 * 
 */
@Entity
@Table(name="GSHM_IST_TMP")
@NamedQuery(name="GshmIstTmp.findAll", query="SELECT g FROM GshmIstTmp g")
public class GshmIstTmp implements Serializable {
	private static final long serialVersionUID = 1L;

	private String acquirer;

	private BigDecimal amount;

	private String issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	private BigDecimal msgtype;

	private String originator;

	private BigDecimal origtrace;

	private String pan;

	private String pcode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	private String transferee;

	@Column(name="TXN_END_TIME")
	private BigDecimal txnEndTime;

	@Column(name="TXN_END_TIME_FORMAT")
	private BigDecimal txnEndTimeFormat;

	public GshmIstTmp() {
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

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
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

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public String getOriginator() {
		return this.originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
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

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getTransferee() {
		return this.transferee;
	}

	public void setTransferee(String transferee) {
		this.transferee = transferee;
	}

	public BigDecimal getTxnEndTime() {
		return this.txnEndTime;
	}

	public void setTxnEndTime(BigDecimal txnEndTime) {
		this.txnEndTime = txnEndTime;
	}

	public BigDecimal getTxnEndTimeFormat() {
		return this.txnEndTimeFormat;
	}

	public void setTxnEndTimeFormat(BigDecimal txnEndTimeFormat) {
		this.txnEndTimeFormat = txnEndTimeFormat;
	}

}