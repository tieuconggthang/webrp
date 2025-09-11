package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the GDTS_TMP database table.
 * 
 */
@Entity
@Table(name="GDTS_TMP")
@NamedQuery(name="GdtsTmp.findAll", query="SELECT g FROM GdtsTmp g")
public class GdtsTmp implements Serializable {
	private static final long serialVersionUID = 1L;

	private String acquirer;

	private BigDecimal amount;

	@Column(name="AMOUNT_RETURN")
	private BigDecimal amountReturn;

	@Column(name="AMOUNT_TS")
	private BigDecimal amountTs;

	@Column(name="BB_BIN")
	private String bbBin;

	private int checked;

	@Column(name="CODE_REF")
	private int codeRef;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_CHECK")
	private Date dateCheck;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TL")
	private Date dateTl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_YC")
	private Date dateYc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	@Column(name="FILENAME_YCTS")
	private String filenameYcts;

	@Column(name="FOR_SYS")
	private String forSys;

	private String issuer;

	@Column(name="KSV_TL")
	private String ksvTl;

	@Column(name="KSV_YC")
	private String ksvYc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private int localTime;

	@Column(name="LOG_ID")
	private BigInteger logId;

	@Column(name="MA_TL")
	private int maTl;

	@Column(name="MA_TS")
	private int maTs;

	@Column(name="MERCHANT_TYPE")
	private int merchantType;

	private int msgtype;

	@Column(name="NOTE_RESEND")
	private String noteResend;

	@Column(name="NOTE_TL")
	private String noteTl;

	@Column(name="NOTE_YC")
	private String noteYc;

	private BigInteger origtrace;

	private String pan;

	@Column(name="PATH_TL")
	private String pathTl;

	private int pcode;

	private int pcode2;

	private int respcode;

	@Column(name="RETURN_CODE")
	private int returnCode;

	@Column(name="REV_DATE")
	private String revDate;

	@Column(name="REVERSE_1")
	private String reverse1;

	@Column(name="SEND_TIME")
	private String sendTime;

	private int status;

	private String termid;

	private int times;

	@Column(name="TO_ACC")
	private String toAcc;

	@Column(name="TRACE_BANK")
	private BigInteger traceBank;

	@Column(name="TSV_TL")
	private String tsvTl;

	@Column(name="TSV_YC")
	private String tsvYc;

	@Column(name="USER_CHECK")
	private String userCheck;

	@Column(name="USER_RESEND")
	private String userResend;

	public GdtsTmp() {
	}

	public String getAcquirer() {
		return this.acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountReturn() {
		return this.amountReturn;
	}

	public void setAmountReturn(BigDecimal amountReturn) {
		this.amountReturn = amountReturn;
	}

	public BigDecimal getAmountTs() {
		return this.amountTs;
	}

	public void setAmountTs(BigDecimal amountTs) {
		this.amountTs = amountTs;
	}

	public String getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(String bbBin) {
		this.bbBin = bbBin;
	}

	public int getChecked() {
		return this.checked;
	}

	public void setChecked(int checked) {
		this.checked = checked;
	}

	public int getCodeRef() {
		return this.codeRef;
	}

	public void setCodeRef(int codeRef) {
		this.codeRef = codeRef;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public Date getDateCheck() {
		return this.dateCheck;
	}

	public void setDateCheck(Date dateCheck) {
		this.dateCheck = dateCheck;
	}

	public Date getDateTl() {
		return this.dateTl;
	}

	public void setDateTl(Date dateTl) {
		this.dateTl = dateTl;
	}

	public Date getDateYc() {
		return this.dateYc;
	}

	public void setDateYc(Date dateYc) {
		this.dateYc = dateYc;
	}

	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public String getFilenameYcts() {
		return this.filenameYcts;
	}

	public void setFilenameYcts(String filenameYcts) {
		this.filenameYcts = filenameYcts;
	}

	public String getForSys() {
		return this.forSys;
	}

	public void setForSys(String forSys) {
		this.forSys = forSys;
	}

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getKsvTl() {
		return this.ksvTl;
	}

	public void setKsvTl(String ksvTl) {
		this.ksvTl = ksvTl;
	}

	public String getKsvYc() {
		return this.ksvYc;
	}

	public void setKsvYc(String ksvYc) {
		this.ksvYc = ksvYc;
	}

	public Date getLocalDate() {
		return this.localDate;
	}

	public void setLocalDate(Date localDate) {
		this.localDate = localDate;
	}

	public int getLocalTime() {
		return this.localTime;
	}

	public void setLocalTime(int localTime) {
		this.localTime = localTime;
	}

	public BigInteger getLogId() {
		return this.logId;
	}

	public void setLogId(BigInteger logId) {
		this.logId = logId;
	}

	public int getMaTl() {
		return this.maTl;
	}

	public void setMaTl(int maTl) {
		this.maTl = maTl;
	}

	public int getMaTs() {
		return this.maTs;
	}

	public void setMaTs(int maTs) {
		this.maTs = maTs;
	}

	public int getMerchantType() {
		return this.merchantType;
	}

	public void setMerchantType(int merchantType) {
		this.merchantType = merchantType;
	}

	public int getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(int msgtype) {
		this.msgtype = msgtype;
	}

	public String getNoteResend() {
		return this.noteResend;
	}

	public void setNoteResend(String noteResend) {
		this.noteResend = noteResend;
	}

	public String getNoteTl() {
		return this.noteTl;
	}

	public void setNoteTl(String noteTl) {
		this.noteTl = noteTl;
	}

	public String getNoteYc() {
		return this.noteYc;
	}

	public void setNoteYc(String noteYc) {
		this.noteYc = noteYc;
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

	public String getPathTl() {
		return this.pathTl;
	}

	public void setPathTl(String pathTl) {
		this.pathTl = pathTl;
	}

	public int getPcode() {
		return this.pcode;
	}

	public void setPcode(int pcode) {
		this.pcode = pcode;
	}

	public int getPcode2() {
		return this.pcode2;
	}

	public void setPcode2(int pcode2) {
		this.pcode2 = pcode2;
	}

	public int getRespcode() {
		return this.respcode;
	}

	public void setRespcode(int respcode) {
		this.respcode = respcode;
	}

	public int getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getRevDate() {
		return this.revDate;
	}

	public void setRevDate(String revDate) {
		this.revDate = revDate;
	}

	public String getReverse1() {
		return this.reverse1;
	}

	public void setReverse1(String reverse1) {
		this.reverse1 = reverse1;
	}

	public String getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTermid() {
		return this.termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

	public int getTimes() {
		return this.times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getToAcc() {
		return this.toAcc;
	}

	public void setToAcc(String toAcc) {
		this.toAcc = toAcc;
	}

	public BigInteger getTraceBank() {
		return this.traceBank;
	}

	public void setTraceBank(BigInteger traceBank) {
		this.traceBank = traceBank;
	}

	public String getTsvTl() {
		return this.tsvTl;
	}

	public void setTsvTl(String tsvTl) {
		this.tsvTl = tsvTl;
	}

	public String getTsvYc() {
		return this.tsvYc;
	}

	public void setTsvYc(String tsvYc) {
		this.tsvYc = tsvYc;
	}

	public String getUserCheck() {
		return this.userCheck;
	}

	public void setUserCheck(String userCheck) {
		this.userCheck = userCheck;
	}

	public String getUserResend() {
		return this.userResend;
	}

	public void setUserResend(String userResend) {
		this.userResend = userResend;
	}

}