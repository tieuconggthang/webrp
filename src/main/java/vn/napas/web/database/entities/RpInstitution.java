package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the RP_INSTITUTION database table.
 * 
 */
@Entity
@Table(name="RP_INSTITUTION")
@NamedQuery(name="RpInstitution.findAll", query="SELECT r FROM RpInstitution r")
public class RpInstitution implements Serializable {
	private static final long serialVersionUID = 1L;

	private String active;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ALLOW_ACQID_FROM")
	private Date allowAcqidFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ALLOW_ACQID_TO")
	private Date allowAcqidTo;

	@Column(name="BANK_CODE")
	private String bankCode;

	@Column(name="BNV_AND_SML")
	private String bnvAndSml;

	private String checksml;

	private String checksml13;

	private String checksml20;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date20;

	private String diachi;

	@Column(name="ENCRYPT_ID")
	private String encryptId;

	@Column(name="FORMAT_SML")
	private String formatSml;

	@Column(name="GROUP_ID")
	private BigDecimal groupId;

	@Column(name="IBT_72")
	private String ibt72;

	@Column(name="INS_CODE")
	private String insCode;

	@Column(name="INS_NAME")
	private String insName;

	@Column(name="INS_TYPE")
	private String insType;

	private String isforeign;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="JOIN_DATE")
	private Date joinDate;

	private String mabm;

	private String mst;

	@Column(name="SHCLOG_ID")
	private String shclogId;

	@Column(name="TCKT_V2")
	private String tcktV2;

	@Column(name="TOKEN_TRANSIT")
	private String tokenTransit;

	@Column(name="VITUAL_SML_GR")
	private String vitualSmlGr;

	public RpInstitution() {
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Date getAllowAcqidFrom() {
		return this.allowAcqidFrom;
	}

	public void setAllowAcqidFrom(Date allowAcqidFrom) {
		this.allowAcqidFrom = allowAcqidFrom;
	}

	public Date getAllowAcqidTo() {
		return this.allowAcqidTo;
	}

	public void setAllowAcqidTo(Date allowAcqidTo) {
		this.allowAcqidTo = allowAcqidTo;
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBnvAndSml() {
		return this.bnvAndSml;
	}

	public void setBnvAndSml(String bnvAndSml) {
		this.bnvAndSml = bnvAndSml;
	}

	public String getChecksml() {
		return this.checksml;
	}

	public void setChecksml(String checksml) {
		this.checksml = checksml;
	}

	public String getChecksml13() {
		return this.checksml13;
	}

	public void setChecksml13(String checksml13) {
		this.checksml13 = checksml13;
	}

	public String getChecksml20() {
		return this.checksml20;
	}

	public void setChecksml20(String checksml20) {
		this.checksml20 = checksml20;
	}

	public Date getDate20() {
		return this.date20;
	}

	public void setDate20(Date date20) {
		this.date20 = date20;
	}

	public String getDiachi() {
		return this.diachi;
	}

	public void setDiachi(String diachi) {
		this.diachi = diachi;
	}

	public String getEncryptId() {
		return this.encryptId;
	}

	public void setEncryptId(String encryptId) {
		this.encryptId = encryptId;
	}

	public String getFormatSml() {
		return this.formatSml;
	}

	public void setFormatSml(String formatSml) {
		this.formatSml = formatSml;
	}

	public BigDecimal getGroupId() {
		return this.groupId;
	}

	public void setGroupId(BigDecimal groupId) {
		this.groupId = groupId;
	}

	public String getIbt72() {
		return this.ibt72;
	}

	public void setIbt72(String ibt72) {
		this.ibt72 = ibt72;
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

	public String getInsType() {
		return this.insType;
	}

	public void setInsType(String insType) {
		this.insType = insType;
	}

	public String getIsforeign() {
		return this.isforeign;
	}

	public void setIsforeign(String isforeign) {
		this.isforeign = isforeign;
	}

	public Date getJoinDate() {
		return this.joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getMabm() {
		return this.mabm;
	}

	public void setMabm(String mabm) {
		this.mabm = mabm;
	}

	public String getMst() {
		return this.mst;
	}

	public void setMst(String mst) {
		this.mst = mst;
	}

	public String getShclogId() {
		return this.shclogId;
	}

	public void setShclogId(String shclogId) {
		this.shclogId = shclogId;
	}

	public String getTcktV2() {
		return this.tcktV2;
	}

	public void setTcktV2(String tcktV2) {
		this.tcktV2 = tcktV2;
	}

	public String getTokenTransit() {
		return this.tokenTransit;
	}

	public void setTokenTransit(String tokenTransit) {
		this.tokenTransit = tokenTransit;
	}

	public String getVitualSmlGr() {
		return this.vitualSmlGr;
	}

	public void setVitualSmlGr(String vitualSmlGr) {
		this.vitualSmlGr = vitualSmlGr;
	}

}