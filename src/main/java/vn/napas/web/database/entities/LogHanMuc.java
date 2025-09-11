package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the LOG_HAN_MUC database table.
 * 
 */
@Entity
@Table(name="LOG_HAN_MUC")
@NamedQuery(name="LogHanMuc.findAll", query="SELECT l FROM LogHanMuc l")
public class LogHanMuc implements Serializable {
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

	public LogHanMuc() {
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