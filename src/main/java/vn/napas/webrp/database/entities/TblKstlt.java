package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TBL_KSTLTS database table.
 * 
 */
@Entity
@Table(name="TBL_KSTLTS")
@NamedQuery(name="TblKstlt.findAll", query="SELECT t FROM TblKstlt t")
public class TblKstlt implements Serializable {
	private static final long serialVersionUID = 1L;

	private String active;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="APPLIED_DATE")
	private Date appliedDate;

	@Column(name="ID_KD")
	private BigDecimal idKd;

	@Column(name="INS_CODE")
	private String insCode;

	@Column(name="SHCLOG_ID")
	private BigDecimal shclogId;

	private BigDecimal times;

	private BigDecimal type;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_TIME")
	private Date updateTime;

	private String username;

	public TblKstlt() {
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Date getAppliedDate() {
		return this.appliedDate;
	}

	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}

	public BigDecimal getIdKd() {
		return this.idKd;
	}

	public void setIdKd(BigDecimal idKd) {
		this.idKd = idKd;
	}

	public String getInsCode() {
		return this.insCode;
	}

	public void setInsCode(String insCode) {
		this.insCode = insCode;
	}

	public BigDecimal getShclogId() {
		return this.shclogId;
	}

	public void setShclogId(BigDecimal shclogId) {
		this.shclogId = shclogId;
	}

	public BigDecimal getTimes() {
		return this.times;
	}

	public void setTimes(BigDecimal times) {
		this.times = times;
	}

	public BigDecimal getType() {
		return this.type;
	}

	public void setType(BigDecimal type) {
		this.type = type;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}