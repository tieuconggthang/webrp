package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TCK_TRANSDETAIL database table.
 * 
 */
@Entity
@Table(name="TCK_TRANSDETAIL")
@NamedQuery(name="TckTransdetail.findAll", query="SELECT t FROM TckTransdetail t")
public class TckTransdetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private String ach;

	private BigDecimal acquirer;

	private BigDecimal amount;

	@Column(name="BB_ACCOUNT")
	private String bbAccount;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	@Column(name="EDIT_USER")
	private String editUser;

	@Column(name="FROM_ACC")
	private String fromAcc;

	@Column(name="FROM_SYS")
	private String fromSys;

	private BigDecimal issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	private String mappingkey;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	private BigDecimal msgtype;

	private BigDecimal origrespcode;

	private BigDecimal origtrace;

	private String pan;

	private BigDecimal pcode;

	@Column(name="PROC_CODE")
	private BigDecimal procCode;

	private String refnum;

	private BigDecimal respcode;

	private String status;

	private String termid;

	@Column(name="TO_ACC")
	private String toAcc;

	public TckTransdetail() {
	}

	public String getAch() {
		return this.ach;
	}

	public void setAch(String ach) {
		this.ach = ach;
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

	public String getBbAccount() {
		return this.bbAccount;
	}

	public void setBbAccount(String bbAccount) {
		this.bbAccount = bbAccount;
	}

	public BigDecimal getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigDecimal bbBin) {
		this.bbBin = bbBin;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public String getEditUser() {
		return this.editUser;
	}

	public void setEditUser(String editUser) {
		this.editUser = editUser;
	}

	public String getFromAcc() {
		return this.fromAcc;
	}

	public void setFromAcc(String fromAcc) {
		this.fromAcc = fromAcc;
	}

	public String getFromSys() {
		return this.fromSys;
	}

	public void setFromSys(String fromSys) {
		this.fromSys = fromSys;
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

	public BigDecimal getLocalTime() {
		return this.localTime;
	}

	public void setLocalTime(BigDecimal localTime) {
		this.localTime = localTime;
	}

	public String getMappingkey() {
		return this.mappingkey;
	}

	public void setMappingkey(String mappingkey) {
		this.mappingkey = mappingkey;
	}

	public BigDecimal getMerchantType() {
		return this.merchantType;
	}

	public void setMerchantType(BigDecimal merchantType) {
		this.merchantType = merchantType;
	}

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public BigDecimal getOrigrespcode() {
		return this.origrespcode;
	}

	public void setOrigrespcode(BigDecimal origrespcode) {
		this.origrespcode = origrespcode;
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

	public BigDecimal getPcode() {
		return this.pcode;
	}

	public void setPcode(BigDecimal pcode) {
		this.pcode = pcode;
	}

	public BigDecimal getProcCode() {
		return this.procCode;
	}

	public void setProcCode(BigDecimal procCode) {
		this.procCode = procCode;
	}

	public String getRefnum() {
		return this.refnum;
	}

	public void setRefnum(String refnum) {
		this.refnum = refnum;
	}

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTermid() {
		return this.termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

	public String getToAcc() {
		return this.toAcc;
	}

	public void setToAcc(String toAcc) {
		this.toAcc = toAcc;
	}

}