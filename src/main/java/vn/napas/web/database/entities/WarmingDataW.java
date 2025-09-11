package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the WARMING_DATA_WS database table.
 * 
 */
@Entity
@Table(name="WARMING_DATA_WS")
@NamedQuery(name="WarmingDataW.findAll", query="SELECT w FROM WarmingDataW w")
public class WarmingDataW implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal acquirer;

	private BigDecimal amount;

	@Column(name="AMOUNT_TS")
	private BigDecimal amountTs;

	private BigDecimal issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="MA_TS")
	private BigDecimal maTs;

	private BigDecimal origtrace;

	private String pan;

	@Column(name="RETURN_CODE")
	private BigDecimal returnCode;

	private String termid;

	public WarmingDataW() {
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

	public BigDecimal getAmountTs() {
		return this.amountTs;
	}

	public void setAmountTs(BigDecimal amountTs) {
		this.amountTs = amountTs;
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

	public BigDecimal getMaTs() {
		return this.maTs;
	}

	public void setMaTs(BigDecimal maTs) {
		this.maTs = maTs;
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

	public BigDecimal getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(BigDecimal returnCode) {
		this.returnCode = returnCode;
	}

	public String getTermid() {
		return this.termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

}