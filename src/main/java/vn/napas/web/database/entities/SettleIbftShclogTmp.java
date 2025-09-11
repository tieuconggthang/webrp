package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the SETTLE_IBFT_SHCLOG_TMP database table.
 * 
 */
@Entity
@Table(name="SETTLE_IBFT_SHCLOG_TMP")
@NamedQuery(name="SettleIbftShclogTmp.findAll", query="SELECT s FROM SettleIbftShclogTmp s")
public class SettleIbftShclogTmp implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal acquirer;

	private BigDecimal amount;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CAP_DATE")
	private Date capDate;

	@Column(name="CH_NEW_AMOUNT")
	private BigDecimal chNewAmount;

	private BigDecimal issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	private BigDecimal msgtype;

	private BigDecimal originator;

	private BigDecimal origtrace;

	private String pan;

	private String pcode;

	private BigDecimal respcode;

	@Column(name="SERVICE_CODE")
	private String serviceCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	private BigDecimal shcerror;

	@Column(name="TXN_END_TIME")
	private BigDecimal txnEndTime;

	@Column(name="TXN_END_TIME_FORMAT")
	private BigDecimal txnEndTimeFormat;

	@Column(name="USER_DEFINE")
	private String userDefine;

	public SettleIbftShclogTmp() {
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

	public BigDecimal getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigDecimal bbBin) {
		this.bbBin = bbBin;
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

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public BigDecimal getOriginator() {
		return this.originator;
	}

	public void setOriginator(BigDecimal originator) {
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

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
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

	public String getUserDefine() {
		return this.userDefine;
	}

	public void setUserDefine(String userDefine) {
		this.userDefine = userDefine;
	}

}