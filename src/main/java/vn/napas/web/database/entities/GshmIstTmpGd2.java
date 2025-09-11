package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the GSHM_IST_TMP_GD2 database table.
 * 
 */
@Entity
@Table(name="GSHM_IST_TMP_GD2")
@NamedQuery(name="GshmIstTmpGd2.findAll", query="SELECT g FROM GshmIstTmpGd2 g")
public class GshmIstTmpGd2 implements Serializable {
	private static final long serialVersionUID = 1L;

	private String acquirer;

	private BigDecimal amount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CAP_DATE")
	private Date capDate;

	@Column(name="CH_NEW_AMOUNT")
	private BigDecimal chNewAmount;

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

	private BigDecimal respcode;

	private BigDecimal resspcode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	private BigDecimal shcerror;

	private String transferee;

	@Column(name="TXN_END_TIME")
	private BigDecimal txnEndTime;

	@Column(name="TXN_END_TIME_FORMAT")
	private BigDecimal txnEndTimeFormat;

	public GshmIstTmpGd2() {
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

	public Date getCapDate() {
		return this.capDate;
	}

	public void setCapDate(Date capDate) {
		this.capDate = capDate;
	}

	public BigDecimal getChNewAmount() {
		return this.chNewAmount;
	}

	public void setChNewAmount(BigDecimal chNewAmount) {
		this.chNewAmount = chNewAmount;
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

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public BigDecimal getResspcode() {
		return this.resspcode;
	}

	public void setResspcode(BigDecimal resspcode) {
		this.resspcode = resspcode;
	}

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public BigDecimal getShcerror() {
		return this.shcerror;
	}

	public void setShcerror(BigDecimal shcerror) {
		this.shcerror = shcerror;
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