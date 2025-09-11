package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the USERS_GROUP_BAK_210624_M database table.
 * 
 */
@Entity
@Table(name="USERS_GROUP_BAK_210624_M")
@NamedQuery(name="UsersGroupBak210624M.findAll", query="SELECT u FROM UsersGroupBak210624M u")
public class UsersGroupBak210624M implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal admin;

	@Column(name="AUTHEN_STATUS")
	private BigDecimal authenStatus;

	@Column(name="BANK_CODE")
	private String bankCode;

	private String chucvu;

	@Column(name="COUNT_INVALID")
	private BigDecimal countInvalid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_CHANGEPASS")
	private Date dateChangepass;

	private String department;

	private String email;

	private String fax;

	private BigDecimal fullpan;

	private BigDecimal groupid;

	private BigDecimal guest;

	@Column(name="IBT_72")
	private String ibt72;

	@Column(name="IN_SYSTEM")
	private String inSystem;

	@Column(name="INS_BRANCH_ID")
	private BigDecimal insBranchId;

	private BigDecimal insid;

	private BigDecimal ksv;

	private String password;

	@Column(name="PASSWORD_SML")
	private String passwordSml;

	@Column(name="PASSWORD_SML_BK")
	private String passwordSmlBk;

	private String password1;

	private String password2;

	private String password3;

	private String password4;

	private String password5;

	private String phone;

	@Column(name="REQUETS_CHANGEPASS")
	private BigDecimal requetsChangepass;

	@Column(name="STATUS_LOCK")
	private BigDecimal statusLock;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TIME_CREATE")
	private Date timeCreate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TIME_LOCK")
	private Date timeLock;

	private BigDecimal tsv;

	@Column(name="USER_BNV")
	private String userBnv;

	@Column(name="USER_COUNT")
	private BigDecimal userCount;

	private String userid;

	private String username;

	public UsersGroupBak210624M() {
	}

	public BigDecimal getAdmin() {
		return this.admin;
	}

	public void setAdmin(BigDecimal admin) {
		this.admin = admin;
	}

	public BigDecimal getAuthenStatus() {
		return this.authenStatus;
	}

	public void setAuthenStatus(BigDecimal authenStatus) {
		this.authenStatus = authenStatus;
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getChucvu() {
		return this.chucvu;
	}

	public void setChucvu(String chucvu) {
		this.chucvu = chucvu;
	}

	public BigDecimal getCountInvalid() {
		return this.countInvalid;
	}

	public void setCountInvalid(BigDecimal countInvalid) {
		this.countInvalid = countInvalid;
	}

	public Date getDateChangepass() {
		return this.dateChangepass;
	}

	public void setDateChangepass(Date dateChangepass) {
		this.dateChangepass = dateChangepass;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public BigDecimal getFullpan() {
		return this.fullpan;
	}

	public void setFullpan(BigDecimal fullpan) {
		this.fullpan = fullpan;
	}

	public BigDecimal getGroupid() {
		return this.groupid;
	}

	public void setGroupid(BigDecimal groupid) {
		this.groupid = groupid;
	}

	public BigDecimal getGuest() {
		return this.guest;
	}

	public void setGuest(BigDecimal guest) {
		this.guest = guest;
	}

	public String getIbt72() {
		return this.ibt72;
	}

	public void setIbt72(String ibt72) {
		this.ibt72 = ibt72;
	}

	public String getInSystem() {
		return this.inSystem;
	}

	public void setInSystem(String inSystem) {
		this.inSystem = inSystem;
	}

	public BigDecimal getInsBranchId() {
		return this.insBranchId;
	}

	public void setInsBranchId(BigDecimal insBranchId) {
		this.insBranchId = insBranchId;
	}

	public BigDecimal getInsid() {
		return this.insid;
	}

	public void setInsid(BigDecimal insid) {
		this.insid = insid;
	}

	public BigDecimal getKsv() {
		return this.ksv;
	}

	public void setKsv(BigDecimal ksv) {
		this.ksv = ksv;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordSml() {
		return this.passwordSml;
	}

	public void setPasswordSml(String passwordSml) {
		this.passwordSml = passwordSml;
	}

	public String getPasswordSmlBk() {
		return this.passwordSmlBk;
	}

	public void setPasswordSmlBk(String passwordSmlBk) {
		this.passwordSmlBk = passwordSmlBk;
	}

	public String getPassword1() {
		return this.password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return this.password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getPassword3() {
		return this.password3;
	}

	public void setPassword3(String password3) {
		this.password3 = password3;
	}

	public String getPassword4() {
		return this.password4;
	}

	public void setPassword4(String password4) {
		this.password4 = password4;
	}

	public String getPassword5() {
		return this.password5;
	}

	public void setPassword5(String password5) {
		this.password5 = password5;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public BigDecimal getRequetsChangepass() {
		return this.requetsChangepass;
	}

	public void setRequetsChangepass(BigDecimal requetsChangepass) {
		this.requetsChangepass = requetsChangepass;
	}

	public BigDecimal getStatusLock() {
		return this.statusLock;
	}

	public void setStatusLock(BigDecimal statusLock) {
		this.statusLock = statusLock;
	}

	public Date getTimeCreate() {
		return this.timeCreate;
	}

	public void setTimeCreate(Date timeCreate) {
		this.timeCreate = timeCreate;
	}

	public Date getTimeLock() {
		return this.timeLock;
	}

	public void setTimeLock(Date timeLock) {
		this.timeLock = timeLock;
	}

	public BigDecimal getTsv() {
		return this.tsv;
	}

	public void setTsv(BigDecimal tsv) {
		this.tsv = tsv;
	}

	public String getUserBnv() {
		return this.userBnv;
	}

	public void setUserBnv(String userBnv) {
		this.userBnv = userBnv;
	}

	public BigDecimal getUserCount() {
		return this.userCount;
	}

	public void setUserCount(BigDecimal userCount) {
		this.userCount = userCount;
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}