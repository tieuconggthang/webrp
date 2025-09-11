package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TBL_SESSION database table.
 * 
 */
@Entity
@Table(name="TBL_SESSION")
@NamedQuery(name="TblSession.findAll", query="SELECT t FROM TblSession t")
public class TblSession implements Serializable {
	private static final long serialVersionUID = 1L;

	private String bankalias;

	private String bankid;

	private String bankname;

	@Column(name="BROWSER_NAME")
	private String browserName;

	private String checkaccountlogin;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_INSERT")
	private Date dateInsert;

	private String filename;

	private String groupid;

	@Column(name="IN_SYSTEM")
	private String inSystem;

	private String isnn;

	private String sessionid;

	private BigDecimal sid;

	private String ssreportname;

	private String ssreportparams;

	private String ssreportvalues;

	private String userid;

	private String username;

	private String userright;

	public TblSession() {
	}

	public String getBankalias() {
		return this.bankalias;
	}

	public void setBankalias(String bankalias) {
		this.bankalias = bankalias;
	}

	public String getBankid() {
		return this.bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}

	public String getBankname() {
		return this.bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBrowserName() {
		return this.browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	public String getCheckaccountlogin() {
		return this.checkaccountlogin;
	}

	public void setCheckaccountlogin(String checkaccountlogin) {
		this.checkaccountlogin = checkaccountlogin;
	}

	public Date getDateInsert() {
		return this.dateInsert;
	}

	public void setDateInsert(Date dateInsert) {
		this.dateInsert = dateInsert;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getGroupid() {
		return this.groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getInSystem() {
		return this.inSystem;
	}

	public void setInSystem(String inSystem) {
		this.inSystem = inSystem;
	}

	public String getIsnn() {
		return this.isnn;
	}

	public void setIsnn(String isnn) {
		this.isnn = isnn;
	}

	public String getSessionid() {
		return this.sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public BigDecimal getSid() {
		return this.sid;
	}

	public void setSid(BigDecimal sid) {
		this.sid = sid;
	}

	public String getSsreportname() {
		return this.ssreportname;
	}

	public void setSsreportname(String ssreportname) {
		this.ssreportname = ssreportname;
	}

	public String getSsreportparams() {
		return this.ssreportparams;
	}

	public void setSsreportparams(String ssreportparams) {
		this.ssreportparams = ssreportparams;
	}

	public String getSsreportvalues() {
		return this.ssreportvalues;
	}

	public void setSsreportvalues(String ssreportvalues) {
		this.ssreportvalues = ssreportvalues;
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

	public String getUserright() {
		return this.userright;
	}

	public void setUserright(String userright) {
		this.userright = userright;
	}

}