package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the NEW_BANK_TABLE database table.
 * 
 */
@Entity
@Table(name="NEW_BANK_TABLE")
@NamedQuery(name="NewBankTable.findAll", query="SELECT n FROM NewBankTable n")
public class NewBankTable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="BANK_CODE")
	private String bankCode;

	@Column(name="BANK_ID")
	private String bankId;

	@Column(name="BANK_ID_CITAD")
	private String bankIdCitad;

	@Column(name="BANK_LOGO_ANDROID")
	private String bankLogoAndroid;

	@Column(name="BANK_LOGO_IOS")
	private String bankLogoIos;

	@Column(name="BANK_NAME")
	private String bankName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="CREATED_USER")
	private String createdUser;

	@Column(name="FULL_NAME")
	private String fullName;

	private BigDecimal id;

	@Column(name="IS_DPP")
	private String isDpp;

	@Column(name="ONL_BANK_ID")
	private BigDecimal onlBankId;

	@Column(name="ONL_ISSUER_BANK_BIN")
	private String onlIssuerBankBin;

	@Column(name="ONL_ISSUER_NAME")
	private String onlIssuerName;

	private String password;

	@Column(name="SECRET_KEY")
	private String secretKey;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;

	@Column(name="UPDATED_USER")
	private String updatedUser;

	@Column(name="URL_FORWARD")
	private String urlForward;

	public NewBankTable() {
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankId() {
		return this.bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankIdCitad() {
		return this.bankIdCitad;
	}

	public void setBankIdCitad(String bankIdCitad) {
		this.bankIdCitad = bankIdCitad;
	}

	public String getBankLogoAndroid() {
		return this.bankLogoAndroid;
	}

	public void setBankLogoAndroid(String bankLogoAndroid) {
		this.bankLogoAndroid = bankLogoAndroid;
	}

	public String getBankLogoIos() {
		return this.bankLogoIos;
	}

	public void setBankLogoIos(String bankLogoIos) {
		this.bankLogoIos = bankLogoIos;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getIsDpp() {
		return this.isDpp;
	}

	public void setIsDpp(String isDpp) {
		this.isDpp = isDpp;
	}

	public BigDecimal getOnlBankId() {
		return this.onlBankId;
	}

	public void setOnlBankId(BigDecimal onlBankId) {
		this.onlBankId = onlBankId;
	}

	public String getOnlIssuerBankBin() {
		return this.onlIssuerBankBin;
	}

	public void setOnlIssuerBankBin(String onlIssuerBankBin) {
		this.onlIssuerBankBin = onlIssuerBankBin;
	}

	public String getOnlIssuerName() {
		return this.onlIssuerName;
	}

	public void setOnlIssuerName(String onlIssuerName) {
		this.onlIssuerName = onlIssuerName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecretKey() {
		return this.secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedUser() {
		return this.updatedUser;
	}

	public void setUpdatedUser(String updatedUser) {
		this.updatedUser = updatedUser;
	}

	public String getUrlForward() {
		return this.urlForward;
	}

	public void setUrlForward(String urlForward) {
		this.urlForward = urlForward;
	}

}