package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the TBL_HT_DCND database table.
 * 
 */
@Entity
@Table(name="TBL_HT_DCND")
@NamedQuery(name="TblHtDcnd.findAll", query="SELECT t FROM TblHtDcnd t")
public class TblHtDcnd implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACH_BEN_ID")
	private BigInteger achBenId;

	@Column(name="ACH_ISS_ID")
	private BigInteger achIssId;

	private BigInteger acquirer;

	private BigDecimal amount;

	@Column(name="BB_BIN")
	private BigInteger bbBin;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Column(name="DCND_REQ_CODE")
	private BigInteger dcndReqCode;

	@Column(name="DCND_REQ_CONTENT")
	private String dcndReqContent;

	@Column(name="DCND_REQ_FILE")
	private String dcndReqFile;

	@Column(name="DCND_REQ_KSV")
	private String dcndReqKsv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DCND_REQ_KSV_DATE")
	private Date dcndReqKsvDate;

	@Column(name="DCND_REQ_KSV_STATUS")
	private BigInteger dcndReqKsvStatus;

	@Column(name="DCND_REQ_NOTE")
	private String dcndReqNote;

	@Column(name="DCND_REQ_TSV")
	private String dcndReqTsv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DCND_REQ_TSV_DATE")
	private Date dcndReqTsvDate;

	@Column(name="DCND_RESP_CODE")
	private BigInteger dcndRespCode;

	@Column(name="DCND_RESP_KSV")
	private String dcndRespKsv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DCND_RESP_KSV_DATE")
	private Date dcndRespKsvDate;

	@Column(name="DCND_RESP_KSV_STATUS")
	private BigInteger dcndRespKsvStatus;

	@Column(name="DCND_RESP_NOTE")
	private String dcndRespNote;

	@Column(name="DCND_RESP_TSV")
	private String dcndRespTsv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DCND_RESP_TSV_DATE")
	private Date dcndRespTsvDate;

	private BigInteger issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigInteger localTime;

	private BigInteger msgtype;

	private BigInteger origtrace;

	private String pan;

	private BigInteger pcode;

	private BigInteger pcode2;

	private BigInteger respcode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name="SHCLOG_ID")
	private BigInteger shclogId;

	private BigInteger stt;

	private String termid;

	@Column(name="TO_ACC")
	private String toAcc;

	private String token;

	@Column(name="UPDATE_WS_ACQBEN")
	private BigInteger updateWsAcqben;

	@Column(name="UPDATE_WS_ISS")
	private BigInteger updateWsIss;

	public TblHtDcnd() {
	}

	public BigInteger getAchBenId() {
		return this.achBenId;
	}

	public void setAchBenId(BigInteger achBenId) {
		this.achBenId = achBenId;
	}

	public BigInteger getAchIssId() {
		return this.achIssId;
	}

	public void setAchIssId(BigInteger achIssId) {
		this.achIssId = achIssId;
	}

	public BigInteger getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(BigInteger acquirer) {
		this.acquirer = acquirer;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigInteger getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigInteger bbBin) {
		this.bbBin = bbBin;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public BigInteger getDcndReqCode() {
		return this.dcndReqCode;
	}

	public void setDcndReqCode(BigInteger dcndReqCode) {
		this.dcndReqCode = dcndReqCode;
	}

	public String getDcndReqContent() {
		return this.dcndReqContent;
	}

	public void setDcndReqContent(String dcndReqContent) {
		this.dcndReqContent = dcndReqContent;
	}

	public String getDcndReqFile() {
		return this.dcndReqFile;
	}

	public void setDcndReqFile(String dcndReqFile) {
		this.dcndReqFile = dcndReqFile;
	}

	public String getDcndReqKsv() {
		return this.dcndReqKsv;
	}

	public void setDcndReqKsv(String dcndReqKsv) {
		this.dcndReqKsv = dcndReqKsv;
	}

	public Date getDcndReqKsvDate() {
		return this.dcndReqKsvDate;
	}

	public void setDcndReqKsvDate(Date dcndReqKsvDate) {
		this.dcndReqKsvDate = dcndReqKsvDate;
	}

	public BigInteger getDcndReqKsvStatus() {
		return this.dcndReqKsvStatus;
	}

	public void setDcndReqKsvStatus(BigInteger dcndReqKsvStatus) {
		this.dcndReqKsvStatus = dcndReqKsvStatus;
	}

	public String getDcndReqNote() {
		return this.dcndReqNote;
	}

	public void setDcndReqNote(String dcndReqNote) {
		this.dcndReqNote = dcndReqNote;
	}

	public String getDcndReqTsv() {
		return this.dcndReqTsv;
	}

	public void setDcndReqTsv(String dcndReqTsv) {
		this.dcndReqTsv = dcndReqTsv;
	}

	public Date getDcndReqTsvDate() {
		return this.dcndReqTsvDate;
	}

	public void setDcndReqTsvDate(Date dcndReqTsvDate) {
		this.dcndReqTsvDate = dcndReqTsvDate;
	}

	public BigInteger getDcndRespCode() {
		return this.dcndRespCode;
	}

	public void setDcndRespCode(BigInteger dcndRespCode) {
		this.dcndRespCode = dcndRespCode;
	}

	public String getDcndRespKsv() {
		return this.dcndRespKsv;
	}

	public void setDcndRespKsv(String dcndRespKsv) {
		this.dcndRespKsv = dcndRespKsv;
	}

	public Date getDcndRespKsvDate() {
		return this.dcndRespKsvDate;
	}

	public void setDcndRespKsvDate(Date dcndRespKsvDate) {
		this.dcndRespKsvDate = dcndRespKsvDate;
	}

	public BigInteger getDcndRespKsvStatus() {
		return this.dcndRespKsvStatus;
	}

	public void setDcndRespKsvStatus(BigInteger dcndRespKsvStatus) {
		this.dcndRespKsvStatus = dcndRespKsvStatus;
	}

	public String getDcndRespNote() {
		return this.dcndRespNote;
	}

	public void setDcndRespNote(String dcndRespNote) {
		this.dcndRespNote = dcndRespNote;
	}

	public String getDcndRespTsv() {
		return this.dcndRespTsv;
	}

	public void setDcndRespTsv(String dcndRespTsv) {
		this.dcndRespTsv = dcndRespTsv;
	}

	public Date getDcndRespTsvDate() {
		return this.dcndRespTsvDate;
	}

	public void setDcndRespTsvDate(Date dcndRespTsvDate) {
		this.dcndRespTsvDate = dcndRespTsvDate;
	}

	public BigInteger getIssuer() {
		return this.issuer;
	}

	public void setIssuer(BigInteger issuer) {
		this.issuer = issuer;
	}

	public Date getLocalDate() {
		return this.localDate;
	}

	public void setLocalDate(Date localDate) {
		this.localDate = localDate;
	}

	public BigInteger getLocalTime() {
		return this.localTime;
	}

	public void setLocalTime(BigInteger localTime) {
		this.localTime = localTime;
	}

	public BigInteger getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigInteger msgtype) {
		this.msgtype = msgtype;
	}

	public BigInteger getOrigtrace() {
		return this.origtrace;
	}

	public void setOrigtrace(BigInteger origtrace) {
		this.origtrace = origtrace;
	}

	public String getPan() {
		return this.pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public BigInteger getPcode() {
		return this.pcode;
	}

	public void setPcode(BigInteger pcode) {
		this.pcode = pcode;
	}

	public BigInteger getPcode2() {
		return this.pcode2;
	}

	public void setPcode2(BigInteger pcode2) {
		this.pcode2 = pcode2;
	}

	public BigInteger getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigInteger respcode) {
		this.respcode = respcode;
	}

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public BigInteger getShclogId() {
		return this.shclogId;
	}

	public void setShclogId(BigInteger shclogId) {
		this.shclogId = shclogId;
	}

	public BigInteger getStt() {
		return this.stt;
	}

	public void setStt(BigInteger stt) {
		this.stt = stt;
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

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public BigInteger getUpdateWsAcqben() {
		return this.updateWsAcqben;
	}

	public void setUpdateWsAcqben(BigInteger updateWsAcqben) {
		this.updateWsAcqben = updateWsAcqben;
	}

	public BigInteger getUpdateWsIss() {
		return this.updateWsIss;
	}

	public void setUpdateWsIss(BigInteger updateWsIss) {
		this.updateWsIss = updateWsIss;
	}

}