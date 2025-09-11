package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TBL_HTTHT_DUPLICATE_BK database table.
 * 
 */
@Entity
@Table(name="TBL_HTTHT_DUPLICATE_BK")
@NamedQuery(name="TblHtthtDuplicateBk.findAll", query="SELECT t FROM TblHtthtDuplicateBk t")
public class TblHtthtDuplicateBk implements Serializable {
	private static final long serialVersionUID = 1L;

	private String acceptorname;

	@Column(name="ACH_BEN_ID")
	private BigDecimal achBenId;

	@Column(name="ACH_ISS_ID")
	private BigDecimal achIssId;

	private BigDecimal acquirer;

	private BigDecimal amount;

	@Column(name="AMOUNT_ISS")
	private BigDecimal amountIss;

	@Column(name="AMOUNT_TLTH")
	private BigDecimal amountTlth;

	@Column(name="AMOUNT_YCTH")
	private BigDecimal amountYcth;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Column(name="CARDHOLDER_CONV_RATE")
	private BigDecimal cardholderConvRate;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_HOACHTOAN")
	private Date dateHoachtoan;

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

	@Column(name="FROM_ACC")
	private String fromAcc;

	@Column(name="IS_PART_REV")
	private BigDecimal isPartRev;

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

	@Column(name="MERCHANT_CODE")
	private String merchantCode;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	private BigDecimal msgtype;

	private String mvv;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_EDIT_DATE")
	private Date napasEditDate;

	private BigDecimal origtrace;

	private String pan;

	private BigDecimal pcode;

	private BigDecimal pcode2;

	@Column(name="POS_ENTRY_CODE")
	private BigDecimal posEntryCode;

	@Column(name="RC_HOACHTOAN")
	private BigDecimal rcHoachtoan;

	private BigDecimal respcode;

	@Column(name="SETTLEMENT_RATE")
	private BigDecimal settlementRate;

	@Column(name="STATUS_KD_TLTH")
	private BigDecimal statusKdTlth;

	@Column(name="STATUS_KD_YCTH")
	private BigDecimal statusKdYcth;

	@Column(name="STATUS_KDTL_BNV")
	private BigDecimal statusKdtlBnv;

	@Column(name="STATUS_KDYC_BNV")
	private BigDecimal statusKdycBnv;

	private BigDecimal stt;

	private String tcc;

	private String termid;

	@Column(name="TO_ACC")
	private String toAcc;

	private String token;

	@Column(name="TRACK2_SERVICE_CODE")
	private String track2ServiceCode;

	@Column(name="UPDATE_RPT")
	private BigDecimal updateRpt;

	@Column(name="UPDATE_WS_ACQBEN")
	private BigDecimal updateWsAcqben;

	@Column(name="UPDATE_WS_ISS")
	private BigDecimal updateWsIss;

	@Column(name="USER_HOACHTOAN")
	private String userHoachtoan;

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

	public TblHtthtDuplicateBk() {
	}

	public String getAcceptorname() {
		return this.acceptorname;
	}

	public void setAcceptorname(String acceptorname) {
		this.acceptorname = acceptorname;
	}

	public BigDecimal getAchBenId() {
		return this.achBenId;
	}

	public void setAchBenId(BigDecimal achBenId) {
		this.achBenId = achBenId;
	}

	public BigDecimal getAchIssId() {
		return this.achIssId;
	}

	public void setAchIssId(BigDecimal achIssId) {
		this.achIssId = achIssId;
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

	public BigDecimal getAmountIss() {
		return this.amountIss;
	}

	public void setAmountIss(BigDecimal amountIss) {
		this.amountIss = amountIss;
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

	public BigDecimal getBbBin() {
		return this.bbBin;
	}

	public void setBbBin(BigDecimal bbBin) {
		this.bbBin = bbBin;
	}

	public BigDecimal getCardholderConvRate() {
		return this.cardholderConvRate;
	}

	public void setCardholderConvRate(BigDecimal cardholderConvRate) {
		this.cardholderConvRate = cardholderConvRate;
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

	public String getFromAcc() {
		return this.fromAcc;
	}

	public void setFromAcc(String fromAcc) {
		this.fromAcc = fromAcc;
	}

	public BigDecimal getIsPartRev() {
		return this.isPartRev;
	}

	public void setIsPartRev(BigDecimal isPartRev) {
		this.isPartRev = isPartRev;
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

	public String getMerchantCode() {
		return this.merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
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

	public Date getNapasEditDate() {
		return this.napasEditDate;
	}

	public void setNapasEditDate(Date napasEditDate) {
		this.napasEditDate = napasEditDate;
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

	public BigDecimal getPcode2() {
		return this.pcode2;
	}

	public void setPcode2(BigDecimal pcode2) {
		this.pcode2 = pcode2;
	}

	public BigDecimal getPosEntryCode() {
		return this.posEntryCode;
	}

	public void setPosEntryCode(BigDecimal posEntryCode) {
		this.posEntryCode = posEntryCode;
	}

	public BigDecimal getRcHoachtoan() {
		return this.rcHoachtoan;
	}

	public void setRcHoachtoan(BigDecimal rcHoachtoan) {
		this.rcHoachtoan = rcHoachtoan;
	}

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public BigDecimal getSettlementRate() {
		return this.settlementRate;
	}

	public void setSettlementRate(BigDecimal settlementRate) {
		this.settlementRate = settlementRate;
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

	public String getTcc() {
		return this.tcc;
	}

	public void setTcc(String tcc) {
		this.tcc = tcc;
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

	public String getTrack2ServiceCode() {
		return this.track2ServiceCode;
	}

	public void setTrack2ServiceCode(String track2ServiceCode) {
		this.track2ServiceCode = track2ServiceCode;
	}

	public BigDecimal getUpdateRpt() {
		return this.updateRpt;
	}

	public void setUpdateRpt(BigDecimal updateRpt) {
		this.updateRpt = updateRpt;
	}

	public BigDecimal getUpdateWsAcqben() {
		return this.updateWsAcqben;
	}

	public void setUpdateWsAcqben(BigDecimal updateWsAcqben) {
		this.updateWsAcqben = updateWsAcqben;
	}

	public BigDecimal getUpdateWsIss() {
		return this.updateWsIss;
	}

	public void setUpdateWsIss(BigDecimal updateWsIss) {
		this.updateWsIss = updateWsIss;
	}

	public String getUserHoachtoan() {
		return this.userHoachtoan;
	}

	public void setUserHoachtoan(String userHoachtoan) {
		this.userHoachtoan = userHoachtoan;
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