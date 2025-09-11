package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the ERR_EX database table.
 * 
 */
@Entity
@Table(name="ERR_EX")
@NamedQuery(name="ErrEx.findAll", query="SELECT e FROM ErrEx e")
public class ErrEx implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ERR_CODE")
	private String errCode;

	@Column(name="ERR_DETAIL")
	private String errDetail;

	@Column(name="ERR_MODULE")
	private String errModule;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ERR_TIME")
	private Date errTime;

	public ErrEx() {
	}

	public String getErrCode() {
		return this.errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrDetail() {
		return this.errDetail;
	}

	public void setErrDetail(String errDetail) {
		this.errDetail = errDetail;
	}

	public String getErrModule() {
		return this.errModule;
	}

	public void setErrModule(String errModule) {
		this.errModule = errModule;
	}

	public Date getErrTime() {
		return this.errTime;
	}

	public void setErrTime(Date errTime) {
		this.errTime = errTime;
	}

}