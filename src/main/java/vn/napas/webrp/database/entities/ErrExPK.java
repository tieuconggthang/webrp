package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The primary key class for the ERR_EX database table.
 * 
 */
@Embeddable
public class ErrExPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ERR_CODE")
	private String errCode;

	@Column(name="ERR_MODULE")
	private String errModule;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ERR_TIME")
	private java.util.Date errTime;

	public ErrExPK() {
	}
	public String getErrCode() {
		return this.errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getErrModule() {
		return this.errModule;
	}
	public void setErrModule(String errModule) {
		this.errModule = errModule;
	}
	public java.util.Date getErrTime() {
		return this.errTime;
	}
	public void setErrTime(java.util.Date errTime) {
		this.errTime = errTime;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ErrExPK)) {
			return false;
		}
		ErrExPK castOther = (ErrExPK)other;
		return 
			this.errCode.equals(castOther.errCode)
			&& this.errModule.equals(castOther.errModule)
			&& this.errTime.equals(castOther.errTime);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.errCode.hashCode();
		hash = hash * prime + this.errModule.hashCode();
		hash = hash * prime + this.errTime.hashCode();
		
		return hash;
	}
}