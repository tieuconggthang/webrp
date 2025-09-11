package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the RP_INSTITUTION_ECOM database table.
 * 
 */
@Entity
@Table(name="RP_INSTITUTION_ECOM")
@NamedQuery(name="RpInstitutionEcom.findAll", query="SELECT r FROM RpInstitutionEcom r")
public class RpInstitutionEcom implements Serializable {
	private static final long serialVersionUID = 1L;

	private String active;

	@Column(name="ENCRYPT_ID")
	private String encryptId;

	@Column(name="INS_CODE")
	private String insCode;

	@Column(name="INS_NAME")
	private String insName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="JOIN_DATE")
	private Date joinDate;

	@Column(name="SHCLOG_ID")
	private String shclogId;

	public RpInstitutionEcom() {
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getEncryptId() {
		return this.encryptId;
	}

	public void setEncryptId(String encryptId) {
		this.encryptId = encryptId;
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

	public Date getJoinDate() {
		return this.joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getShclogId() {
		return this.shclogId;
	}

	public void setShclogId(String shclogId) {
		this.shclogId = shclogId;
	}

}