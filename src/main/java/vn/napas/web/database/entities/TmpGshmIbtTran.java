package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the TMP_GSHM_IBT_TRANS database table.
 * 
 */
@Entity
@Table(name="TMP_GSHM_IBT_TRANS")
@NamedQuery(name="TmpGshmIbtTran.findAll", query="SELECT t FROM TmpGshmIbtTran t")
public class TmpGshmIbtTran implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACCOUNT_NO")
	private String accountNo;

	@Column(name="ACQ_ID")
	private String acqId;

	private BigInteger amount;

	@Column(name="BEN_ID")
	private String benId;

	@Column(name="ISS_ID")
	private String issId;

	@Column(name="LOCAL_DATE")
	private String localDate;

	@Column(name="LOCAL_TIME")
	private String localTime;

	private String mti;

	@Column(name="PROC_CODE")
	private String procCode;

	@Column(name="RESPONSE_CODE")
	private String responseCode;

	@Column(name="SERVICE_CODE")
	private String serviceCode;

	@Column(name="SETTLE_DATE")
	private String settleDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TNX_STAMP")
	private Date tnxStamp;

	@Column(name="USER_DEFINE")
	private String userDefine;

	public TmpGshmIbtTran() {
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAcqId() {
		return this.acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}

	public BigInteger getAmount() {
		return this.amount;
	}

	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}

	public String getBenId() {
		return this.benId;
	}

	public void setBenId(String benId) {
		this.benId = benId;
	}

	public String getIssId() {
		return this.issId;
	}

	public void setIssId(String issId) {
		this.issId = issId;
	}

	public String getLocalDate() {
		return this.localDate;
	}

	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}

	public String getLocalTime() {
		return this.localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public String getMti() {
		return this.mti;
	}

	public void setMti(String mti) {
		this.mti = mti;
	}

	public String getProcCode() {
		return this.procCode;
	}

	public void setProcCode(String procCode) {
		this.procCode = procCode;
	}

	public String getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getSettleDate() {
		return this.settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public Date getTnxStamp() {
		return this.tnxStamp;
	}

	public void setTnxStamp(Date tnxStamp) {
		this.tnxStamp = tnxStamp;
	}

	public String getUserDefine() {
		return this.userDefine;
	}

	public void setUserDefine(String userDefine) {
		this.userDefine = userDefine;
	}

}