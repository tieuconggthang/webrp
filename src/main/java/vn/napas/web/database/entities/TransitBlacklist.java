package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TRANSIT_BLACKLIST database table.
 * 
 */
@Entity
@Table(name="TRANSIT_BLACKLIST")
@NamedQuery(name="TransitBlacklist.findAll", query="SELECT t FROM TransitBlacklist t")
public class TransitBlacklist implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal acquirer;

	private BigDecimal amount;

	@Column(name="BANK_NAME")
	private String bankName;

	@Column(name="CHECK_IN")
	private String checkIn;

	@Column(name="CHECK_OUT")
	private String checkOut;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_INSERT")
	private Date dateInsert;

	private String description;

	private BigDecimal issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCATION_CODE")
	private String locationCode;

	private BigDecimal origtrace;

	private String pan;

	@Column(name="RETURN_CODE")
	private BigDecimal returnCode;

	private BigDecimal stt;

	private String termid;

	private String token;

	public TransitBlacklist() {
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

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCheckIn() {
		return this.checkIn;
	}

	public void setCheckIn(String checkIn) {
		this.checkIn = checkIn;
	}

	public String getCheckOut() {
		return this.checkOut;
	}

	public void setCheckOut(String checkOut) {
		this.checkOut = checkOut;
	}

	public Date getDateInsert() {
		return this.dateInsert;
	}

	public void setDateInsert(Date dateInsert) {
		this.dateInsert = dateInsert;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getLocationCode() {
		return this.locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
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