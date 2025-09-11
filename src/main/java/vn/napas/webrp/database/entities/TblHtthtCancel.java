package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TBL_HTTHT_CANCEL database table.
 * 
 */
@Entity
@Table(name="TBL_HTTHT_CANCEL")
@NamedQuery(name="TblHtthtCancel.findAll", query="SELECT t FROM TblHtthtCancel t")
public class TblHtthtCancel implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal acquirer;

	private BigDecimal amount;

	@Column(name="AMOUNT_TLTH")
	private BigDecimal amountTlth;

	@Column(name="AMOUNT_YCTH")
	private BigDecimal amountYcth;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_KD_TLTH")
	private Date dateKdTlth;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_KD_YCTH")
	private Date dateKdYcth;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_KDTL_BNV")
	private Date dateKdtlBnv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_KDYC_BNV")
	private Date dateKdycBnv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TLTH")
	private Date dateTlth;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_YCTH")
	private Date dateYcth;

	@Column(name="DESC_TLTH")
	private String descTlth;

	@Column(name="DESC_YCTH")
	private String descYcth;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	@Column(name="FILENAME_TL")
	private String filenameTl;

	@Column(name="FILENAME_YC")
	private String filenameYc;

	private BigDecimal isrev;

	private BigDecimal issuer;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="LOG_ID")
	private BigDecimal logId;

	@Column(name="MA_TLTH")
	private BigDecimal maTlth;

	@Column(name="MA_YCTH")
	private BigDecimal maYcth;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	private BigDecimal msgtype;

	private BigDecimal origtrace;

	private String pan;

	private BigDecimal pcode;

	private BigDecimal respcode;

	@Column(name="STATUS_KD_TLTH")
	private BigDecimal statusKdTlth;

	@Column(name="STATUS_KD_YCTH")
	private BigDecimal statusKdYcth;

	@Column(name="STATUS_KDTL_BNV")
	private BigDecimal statusKdtlBnv;

	@Column(name="STATUS_KDYC_BNV")
	private BigDecimal statusKdycBnv;

	private BigDecimal stt;

	private String termid;

	@Column(name="UPDATE_RPT")
	private BigDecimal updateRpt;

	@Column(name="USER_KD_TLTH")
	private String userKdTlth;

	@Column(name="USER_KD_YCTH")
	private String userKdYcth;

	@Column(name="USER_KDTL_BNV")
	private String userKdtlBnv;

	@Column(name="USER_KDTL_KSVBNV")
	private String userKdtlKsvbnv;

	@Column(name="USER_KDYC_BNV")
	private String userKdycBnv;

	@Column(name="USER_KDYC_KSVBNV")
	private String userKdycKsvbnv;

	@Column(name="USER_TLTH")
	private String userTlth;

	@Column(name="USER_YCTH")
	private String userYcth;

	public TblHtthtCancel() {
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

	public BigDecimal getAmountTlth() {
		return this.amountTlth;
	}

	public void setAmountTlth(BigDecimal amountTlth) {
		this.amountTlth = amountTlth;
	}

	public BigDecimal getAmountYcth() {
		return this.amountYcth;
	}

	public void setAmountYcth(BigDecimal amountYcth) {
		this.amountYcth = amountYcth;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public Date getDateKdTlth() {
		return this.dateKdTlth;
	}

	public void setDateKdTlth(Date dateKdTlth) {
		this.dateKdTlth = dateKdTlth;
	}

	public Date getDateKdYcth() {
		return this.dateKdYcth;
	}

	public void setDateKdYcth(Date dateKdYcth) {
		this.dateKdYcth = dateKdYcth;
	}

	public Date getDateKdtlBnv() {
		return this.dateKdtlBnv;
	}

	public void setDateKdtlBnv(Date dateKdtlBnv) {
		this.dateKdtlBnv = dateKdtlBnv;
	}

	public Date getDateKdycBnv() {
		return this.dateKdycBnv;
	}

	public void setDateKdycBnv(Date dateKdycBnv) {
		this.dateKdycBnv = dateKdycBnv;
	}

	public Date getDateTlth() {
		return this.dateTlth;
	}

	public void setDateTlth(Date dateTlth) {
		this.dateTlth = dateTlth;
	}

	public Date getDateYcth() {
		return this.dateYcth;
	}

	public void setDateYcth(Date dateYcth) {
		this.dateYcth = dateYcth;
	}

	public String getDescTlth() {
		return this.descTlth;
	}

	public void setDescTlth(String descTlth) {
		this.descTlth = descTlth;
	}

	public String getDescYcth() {
		return this.descYcth;
	}

	public void setDescYcth(String descYcth) {
		this.descYcth = descYcth;
	}

	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public String getFilenameTl() {
		return this.filenameTl;
	}

	public void setFilenameTl(String filenameTl) {
		this.filenameTl = filenameTl;
	}

	public String getFilenameYc() {
		return this.filenameYc;
	}

	public void setFilenameYc(String filenameYc) {
		this.filenameYc = filenameYc;
	}

	public BigDecimal getIsrev() {
		return this.isrev;
	}

	public void setIsrev(BigDecimal isrev) {
		this.isrev = isrev;
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

	public BigDecimal getLogId() {
		return this.logId;
	}

	public void setLogId(BigDecimal logId) {
		this.logId = logId;
	}

	public BigDecimal getMaTlth() {
		return this.maTlth;
	}

	public void setMaTlth(BigDecimal maTlth) {
		this.maTlth = maTlth;
	}

	public BigDecimal getMaYcth() {
		return this.maYcth;
	}

	public void setMaYcth(BigDecimal maYcth) {
		this.maYcth = maYcth;
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

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public BigDecimal getStatusKdTlth() {
		return this.statusKdTlth;
	}

	public void setStatusKdTlth(BigDecimal statusKdTlth) {
		this.statusKdTlth = statusKdTlth;
	}

	public BigDecimal getStatusKdYcth() {
		return this.statusKdYcth;
	}

	public void setStatusKdYcth(BigDecimal statusKdYcth) {
		this.statusKdYcth = statusKdYcth;
	}

	public BigDecimal getStatusKdtlBnv() {
		return this.statusKdtlBnv;
	}

	public void setStatusKdtlBnv(BigDecimal statusKdtlBnv) {
		this.statusKdtlBnv = statusKdtlBnv;
	}

	public BigDecimal getStatusKdycBnv() {
		return this.statusKdycBnv;
	}

	public void setStatusKdycBnv(BigDecimal statusKdycBnv) {
		this.statusKdycBnv = statusKdycBnv;
	}

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public String getTermid() {
		return this.termid;
	}

	public void setTermid(String termid) {
		this.termid = termid;
	}

	public BigDecimal getUpdateRpt() {
		return this.updateRpt;
	}

	public void setUpdateRpt(BigDecimal updateRpt) {
		this.updateRpt = updateRpt;
	}

	public String getUserKdTlth() {
		return this.userKdTlth;
	}

	public void setUserKdTlth(String userKdTlth) {
		this.userKdTlth = userKdTlth;
	}

	public String getUserKdYcth() {
		return this.userKdYcth;
	}

	public void setUserKdYcth(String userKdYcth) {
		this.userKdYcth = userKdYcth;
	}

	public String getUserKdtlBnv() {
		return this.userKdtlBnv;
	}

	public void setUserKdtlBnv(String userKdtlBnv) {
		this.userKdtlBnv = userKdtlBnv;
	}

	public String getUserKdtlKsvbnv() {
		return this.userKdtlKsvbnv;
	}

	public void setUserKdtlKsvbnv(String userKdtlKsvbnv) {
		this.userKdtlKsvbnv = userKdtlKsvbnv;
	}

	public String getUserKdycBnv() {
		return this.userKdycBnv;
	}

	public void setUserKdycBnv(String userKdycBnv) {
		this.userKdycBnv = userKdycBnv;
	}

	public String getUserKdycKsvbnv() {
		return this.userKdycKsvbnv;
	}

	public void setUserKdycKsvbnv(String userKdycKsvbnv) {
		this.userKdycKsvbnv = userKdycKsvbnv;
	}

	public String getUserTlth() {
		return this.userTlth;
	}

	public void setUserTlth(String userTlth) {
		this.userTlth = userTlth;
	}

	public String getUserYcth() {
		return this.userYcth;
	}

	public void setUserYcth(String userYcth) {
		this.userYcth = userYcth;
	}

}