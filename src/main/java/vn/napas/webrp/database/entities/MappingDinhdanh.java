package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the MAPPING_DINHDANH database table.
 * 
 */
@Entity
@Table(name="MAPPING_DINHDANH")
@NamedQuery(name="MappingDinhdanh.findAll", query="SELECT m FROM MappingDinhdanh m")
public class MappingDinhdanh implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="INS_CODE")
	private String insCode;

	@Column(name="INS_NAME")
	private String insName;

	@Column(name="SHCLOG_ID_NEW")
	private BigDecimal shclogIdNew;

	@Column(name="SHCLOG_ID_OLD")
	private BigDecimal shclogIdOld;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="START_DATE")
	private Date startDate;

	public MappingDinhdanh() {
	}

	public String getInsCode() {
		return this.insCode;
	}

	public void setInsCode(String insCode) {
		this.insCode = insCode;
	}

	public String getInsName() {
		return this.insName;
	}

	public void setInsName(String insName) {
		this.insName = insName;
	}

	public BigDecimal getShclogIdNew() {
		return this.shclogIdNew;
	}

	public void setShclogIdNew(BigDecimal shclogIdNew) {
		this.shclogIdNew = shclogIdNew;
	}

	public BigDecimal getShclogIdOld() {
		return this.shclogIdOld;
	}

	public void setShclogIdOld(BigDecimal shclogIdOld) {
		this.shclogIdOld = shclogIdOld;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}