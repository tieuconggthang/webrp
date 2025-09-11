package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the DATA_NHTV_QUYET_TOAN_IBFT_CT database table.
 * 
 */
@Entity
@Table(name="DATA_NHTV_QUYET_TOAN_IBFT_CT")
@NamedQuery(name="DataNhtvQuyetToanIbftCt.findAll", query="SELECT d FROM DataNhtvQuyetToanIbftCt d")
public class DataNhtvQuyetToanIbftCt implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal acquirer;

	@Column(name="ACQUIRER_ORG")
	private BigDecimal acquirerOrg;

	@Column(name="ACQUIRER_TMP")
	private BigDecimal acquirerTmp;

	private BigDecimal amount;

	@Column(name="BANK_CODE_ACQ")
	private String bankCodeAcq;

	@Column(name="BANK_CODE_BIN")
	private String bankCodeBin;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Column(name="BB_BIN_ORG")
	private BigDecimal bbBinOrg;

	@Column(name="BB_BIN_TMP")
	private BigDecimal bbBinTmp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FROM_DATE")
	private Date fromDate;

	private BigDecimal issuer;

	private BigDecimal msgtype;

	private String note;

	private String pcode;

	@Column(name="SERVICE_CODE")
	private String serviceCode;

	@Column(name="SESSION_NUM")
	private BigDecimal sessionNum;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name="SETTLEMENT_STATUS")
	private String settlementStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TO_DATE")
	private Date toDate;

	@Column(name="TRANS_NUM")
	private BigDecimal transNum;

	@Column(name="TRANS_TYPE")
	private String transType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;

	@Column(name="USER_DEFINE")
	private String userDefine;

	public DataNhtvQuyetToanIbftCt() {
	}

	public BigDecimal getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(BigDecimal acquirer) {
		this.acquirer = acquirer;
	}

	public BigDecimal getAcquirerOrg() {
		return this.acquirerOrg;
	}

	public void setAcquirerOrg(BigDecimal acquirerOrg) {
		this.acquirerOrg = acquirerOrg;
	}

	public BigDecimal getAcquirerTmp() {
		return this.acquirerTmp;
	}

	public void setAcquirerTmp(BigDecimal acquirerTmp) {
		this.acquirerTmp = acquirerTmp;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getBankCodeAcq() {
		return this.bankCodeAcq;
	}

	public void setBankCodeAcq(String bankCodeAcq) {
		this.bankCodeAcq = bankCodeAcq;
	}

	public String getBankCodeBin() {
		return this.bankCodeBin;
	}

	public void setBankCodeBin(String bankCodeBin) {
		this.bankCodeBin = bankCodeBin;
	}

	public BigDecimal getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigDecimal bbBin) {
		this.bbBin = bbBin;
	}

	public BigDecimal getBbBinOrg() {
		return this.bbBinOrg;
	}

	public void setBbBinOrg(BigDecimal bbBinOrg) {
		this.bbBinOrg = bbBinOrg;
	}

	public BigDecimal getBbBinTmp() {
		return this.bbBinTmp;
	}

	public void setBbBinTmp(BigDecimal bbBinTmp) {
		this.bbBinTmp = bbBinTmp;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getFromDate() {
		return this.fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public BigDecimal getIssuer() {
		return this.issuer;
	}

	public void setIssuer(BigDecimal issuer) {
		this.issuer = issuer;
	}

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPcode() {
		return this.pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public BigDecimal getSessionNum() {
		return this.sessionNum;
	}

	public void setSessionNum(BigDecimal sessionNum) {
		this.sessionNum = sessionNum;
	}

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getSettlementStatus() {
		return this.settlementStatus;
	}

	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	public Date getToDate() {
		return this.toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public BigDecimal getTransNum() {
		return this.transNum;
	}

	public void setTransNum(BigDecimal transNum) {
		this.transNum = transNum;
	}

	public String getTransType() {
		return this.transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUserDefine() {
		return this.userDefine;
	}

	public void setUserDefine(String userDefine) {
		this.userDefine = userDefine;
	}

}