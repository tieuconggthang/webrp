package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TRANSIT_CSRR database table.
 * 
 */
@Entity
@Table(name="TRANSIT_CSRR")
@NamedQuery(name="TransitCsrr.findAll", query="SELECT t FROM TransitCsrr t")
public class TransitCsrr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACQ_CURRENCY_CODE")
	private BigDecimal acqCurrencyCode;

	private BigDecimal acquirer;

	private BigDecimal amount;

	@Column(name="AMOUNT_RECOVER_DEBT")
	private BigDecimal amountRecoverDebt;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_RECOVER_DEBT")
	private Date dateRecoverDebt;

	private BigDecimal issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	private BigDecimal msgtype;

	private BigDecimal origtrace;

	private String pan;

	private BigDecimal pcode;

	private BigDecimal pcode2;

	private BigDecimal respcode;

	@Column(name="SETT_CURRENCY_CODE")
	private BigDecimal settCurrencyCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name="STATUS_RECOVER_DEBT")
	private String statusRecoverDebt;

	private BigDecimal stt;

	private String termid;

	private String token;

	public TransitCsrr() {
	}

	public BigDecimal getAcqCurrencyCode() {
		return this.acqCurrencyCode;
	}

	public void setAcqCurrencyCode(BigDecimal acqCurrencyCode) {
		this.acqCurrencyCode = acqCurrencyCode;
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

	public BigDecimal getAmountRecoverDebt() {
		return this.amountRecoverDebt;
	}

	public void setAmountRecoverDebt(BigDecimal amountRecoverDebt) {
		this.amountRecoverDebt = amountRecoverDebt;
	}

	public BigDecimal getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigDecimal bbBin) {
		this.bbBin = bbBin;
	}

	public Date getDateRecoverDebt() {
		return this.dateRecoverDebt;
	}

	public void setDateRecoverDebt(Date dateRecoverDebt) {
		this.dateRecoverDebt = dateRecoverDebt;
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

	public BigDecimal getMerchantType() {
		return this.merchantType;
	}

	public void setMerchantType(BigDecimal merchantType) {
		this.merchantType = merchantType;
	}

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
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

	public BigDecimal getPcode2() {
		return this.pcode2;
	}

	public void setPcode2(BigDecimal pcode2) {
		this.pcode2 = pcode2;
	}

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public BigDecimal getSettCurrencyCode() {
		return this.settCurrencyCode;
	}

	public void setSettCurrencyCode(BigDecimal settCurrencyCode) {
		this.settCurrencyCode = settCurrencyCode;
	}

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getStatusRecoverDebt() {
		return this.statusRecoverDebt;
	}

	public void setStatusRecoverDebt(String statusRecoverDebt) {
		this.statusRecoverDebt = statusRecoverDebt;
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

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}