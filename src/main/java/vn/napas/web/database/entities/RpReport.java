package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the RP_REPORT database table.
 * 
 */
@Entity
@Table(name="RP_REPORT")
@NamedQuery(name="RpReport.findAll", query="SELECT r FROM RpReport r")
public class RpReport implements Serializable {
	private static final long serialVersionUID = 1L;

	private String active;

	private BigDecimal id;

	@Column(name="IS_NN")
	private BigDecimal isNn;

	@Column(name="IS_TN")
	private BigDecimal isTn;

	@Column(name="REP_CODE")
	private String repCode;

	@Column(name="REP_DESC")
	private String repDesc;

	@Column(name="REP_GROUP")
	private String repGroup;

	public RpReport() {
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public BigDecimal getIsNn() {
		return this.isNn;
	}

	public void setIsNn(BigDecimal isNn) {
		this.isNn = isNn;
	}

	public BigDecimal getIsTn() {
		return this.isTn;
	}

	public void setIsTn(BigDecimal isTn) {
		this.isTn = isTn;
	}

	public String getRepCode() {
		return this.repCode;
	}

	public void setRepCode(String repCode) {
		this.repCode = repCode;
	}

	public String getRepDesc() {
		return this.repDesc;
	}

	public void setRepDesc(String repDesc) {
		this.repDesc = repDesc;
	}

	public String getRepGroup() {
		return this.repGroup;
	}

	public void setRepGroup(String repGroup) {
		this.repGroup = repGroup;
	}

}