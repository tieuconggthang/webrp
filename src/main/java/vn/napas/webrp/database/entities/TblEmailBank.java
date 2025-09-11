package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the TBL_EMAIL_BANK database table.
 * 
 */
@Entity
@Table(name="TBL_EMAIL_BANK")
@NamedQuery(name="TblEmailBank.findAll", query="SELECT t FROM TblEmailBank t")
public class TblEmailBank implements Serializable {
	private static final long serialVersionUID = 1L;

	private String active;

	private String email;

	@Column(name="INS_CODE")
	private String insCode;

	private String service;

	@Column(name="SHCLOG_ID")
	private BigDecimal shclogId;

	public TblEmailBank() {
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInsCode() {
		return this.insCode;
	}

	public void setInsCode(String insCode) {
		this.insCode = insCode;
	}

	public String getService() {
		return this.service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public BigDecimal getShclogId() {
		return this.shclogId;
	}

	public void setShclogId(BigDecimal shclogId) {
		this.shclogId = shclogId;
	}

}