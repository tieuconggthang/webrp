package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the GD_TIMEOUT database table.
 * 
 */
@Entity
@Table(name="GD_TIMEOUT")
@NamedQuery(name="GdTimeout.findAll", query="SELECT g FROM GdTimeout g")
public class GdTimeout implements Serializable {
	private static final long serialVersionUID = 1L;

	private String acceptorname;

	private String acquirer;

	@Column(name="ADD_INFO")
	private String addInfo;

	private BigDecimal amount;

	@Column(name="BB_ACCOUNT")
	private String bbAccount;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Column(name="COL_TEMP")
	private String colTemp;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_HOACHTOAN")
	private Date dateHoachtoan;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_KSV_TL")
	private Date dateKsvTl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TL")
	private Date dateTl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TSV_TL")
	private Date dateTsvTl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	@Column(name="FROM_ACC")
	private String fromAcc;

	@Column(name="FROM_SYS")
	private String fromSys;

	@Column(name="ID_RPT")
	private BigDecimal idRpt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="INSERT_DATE")
	private Date insertDate;

	private String issuer;

	@Column(name="KSV_HOACHTOAN")
	private String ksvHoachtoan;

	@Column(name="KSV_TL")
	private String ksvTl;

	@Column(name="KSVD_TL")
	private BigDecimal ksvdTl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	private BigDecimal msgtype;

	private String mvv;

	@Column(name="NOTE_TL")
	private String noteTl;

	private BigDecimal origtrace;

	private String pan;

	@Column(name="PATH_TL")
	private String pathTl;

	private BigDecimal pcode;

	private BigDecimal pcode2;

	private BigDecimal respcode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	private String status;

	private String termid;

	@Column(name="TO_ACC")
	private String toAcc;

	@Column(name="TRAN_CASE")
	private String tranCase;

	@Column(name="TSV_HOACHTOAN")
	private String tsvHoachtoan;

	@Column(name="TSV_TL")
	private String tsvTl;

	@Column(name="TT_HOACHTOAN")
	private BigDecimal ttHoachtoan;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_RPT_DATE")
	private Date updateRptDate;

	public GdTimeout() {
	}

	public String getAcceptorname() {
		return this.acceptorname;
	}

	public void setAcceptorname(String acceptorname) {
		this.acceptorname = acceptorname;
	}

	public String getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getAddInfo() {
		return this.addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
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

	public String getColTemp() {
		return this.colTemp;
	}

	public void setColTemp(String colTemp) {
		this.colTemp = colTemp;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public Date getDateHoachtoan() {
		return this.dateHoachtoan;
	}

	public void setDateHoachtoan(Date dateHoachtoan) {
		this.dateHoachtoan = dateHoachtoan;
	}

	public Date getDateKsvTl() {
		return this.dateKsvTl;
	}

	public void setDateKsvTl(Date dateKsvTl) {
		this.dateKsvTl = dateKsvTl;
	}

	public Date getDateTl() {
		return this.dateTl;
	}

	public void setDateTl(Date dateTl) {
		this.dateTl = dateTl;
	}

	public Date getDateTsvTl() {
		return this.dateTsvTl;
	}

	public void setDateTsvTl(Date dateTsvTl) {
		this.dateTsvTl = dateTsvTl;
	}

	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
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

	public BigDecimal getIdRpt() {
		return this.idRpt;
	}

	public void setIdRpt(BigDecimal idRpt) {
		this.idRpt = idRpt;
	}

	public Date getInsertDate() {
		return this.insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getKsvHoachtoan() {
		return this.ksvHoachtoan;
	}

	public void setKsvHoachtoan(String ksvHoachtoan) {
		this.ksvHoachtoan = ksvHoachtoan;
	}

	public String getKsvTl() {
		return this.ksvTl;
	}

	public void setKsvTl(String ksvTl) {
		this.ksvTl = ksvTl;
	}

	public BigDecimal getKsvdTl() {
		return this.ksvdTl;
	}

	public void setKsvdTl(BigDecimal ksvdTl) {
		this.ksvdTl = ksvdTl;
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

	public String getMvv() {
		return this.mvv;
	}

	public void setMvv(String mvv) {
		this.mvv = mvv;
	}

	public String getNoteTl() {
		return this.noteTl;
	}

	public void setNoteTl(String noteTl) {
		this.noteTl = noteTl;
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

	public String getPathTl() {
		return this.pathTl;
	}

	public void setPathTl(String pathTl) {
		this.pathTl = pathTl;
	}

	public BigDecimal getPcode() {
		return this.pcode;
	}

	public void setPcode(BigDecimal pcode) {
		this.pcode = pcode;
	}

	public BigDecimal getPcode2() {
		return this.pcode2;
	}

	public void setPcode2(BigDecimal pcode2) {
		this.pcode2 = pcode2;
	}

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
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

	public String getTranCase() {
		return this.tranCase;
	}

	public void setTranCase(String tranCase) {
		this.tranCase = tranCase;
	}

	public String getTsvHoachtoan() {
		return this.tsvHoachtoan;
	}

	public void setTsvHoachtoan(String tsvHoachtoan) {
		this.tsvHoachtoan = tsvHoachtoan;
	}

	public String getTsvTl() {
		return this.tsvTl;
	}

	public void setTsvTl(String tsvTl) {
		this.tsvTl = tsvTl;
	}

	public BigDecimal getTtHoachtoan() {
		return this.ttHoachtoan;
	}

	public void setTtHoachtoan(BigDecimal ttHoachtoan) {
		this.ttHoachtoan = ttHoachtoan;
	}

	public Date getUpdateRptDate() {
		return this.updateRptDate;
	}

	public void setUpdateRptDate(Date updateRptDate) {
		this.updateRptDate = updateRptDate;
	}

}