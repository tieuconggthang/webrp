package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the GDTS database table.
 * 
 */
@Entity
@Table(name="GDTS")
@NamedQuery(name="Gdt.findAll", query="SELECT g FROM Gdt g")
public class Gdt implements Serializable {
	private static final long serialVersionUID = 1L;

	private String acceptorname;

	@Column(name="ACH_BEN_ID")
	private BigDecimal achBenId;

	@Column(name="ACH_ISS_ID")
	private BigDecimal achIssId;

	@Column(name="ACQ_CURRENCY_CODE")
	private BigDecimal acqCurrencyCode;

	private String acquirer;

	private BigDecimal amount;

	@Column(name="AMOUNT_GHINO")
	private BigDecimal amountGhino;

	@Column(name="AMOUNT_ISS")
	private BigDecimal amountIss;

	@Column(name="AMOUNT_RETURN_ISS")
	private BigDecimal amountReturnIss;

	@Column(name="AMOUNT_RETURN_SET")
	private BigDecimal amountReturnSet;

	@Column(name="AMOUNT_RETURN1")
	private BigDecimal amountReturn1;

	@Column(name="AMOUNT_RETURN2")
	private BigDecimal amountReturn2;

	@Column(name="AMOUNT_SET")
	private BigDecimal amountSet;

	@Column(name="AMOUNT_TS1")
	private BigDecimal amountTs1;

	@Column(name="AMOUNT_TS2")
	private BigDecimal amountTs2;

	@Column(name="BB_ACCOUNT")
	private String bbAccount;

	@Column(name="BB_BIN")
	private BigDecimal bbBin;

	@Column(name="CANCEL_CODE")
	private BigDecimal cancelCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CANCEL_DATE")
	private Date cancelDate;

	@Column(name="CANCEL_USER")
	private String cancelUser;

	@Column(name="CARDHOLDER_CONV_RATE")
	private BigDecimal cardholderConvRate;

	@Column(name="CODE_REF")
	private BigDecimal codeRef;

	@Column(name="CONTENT_FUND")
	private String contentFund;

	@Column(name="CONV_RATE")
	private BigDecimal convRate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_BNV_TLTL")
	private Date dateBnvTltl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_BNV_YCTL")
	private Date dateBnvYctl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_BNVD")
	private Date dateBnvd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_GHINO")
	private Date dateGhino;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_HOACHTOAN")
	private Date dateHoachtoan;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_REQUEST")
	private Date dateRequest;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_RETURN")
	private Date dateReturn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TL1")
	private Date dateTl1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TL2")
	private Date dateTl2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_TLTL")
	private Date dateTltl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_YC1")
	private Date dateYc1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_YC2")
	private Date dateYc2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_YCTL")
	private Date dateYctl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EDIT_DATE")
	private Date editDate;

	private String f90;

	@Lob
	@Column(name="FILE_ATTACH1")
	private byte[] fileAttach1;

	@Lob
	@Column(name="FILE_ATTACH2")
	private byte[] fileAttach2;

	@Column(name="FILE_BAONO")
	private String fileBaono;

	@Lob
	@Column(name="FILEATTACH_TLTL")
	private byte[] fileattachTltl;

	@Lob
	@Column(name="FILEATTACH_YCTL")
	private byte[] fileattachYctl;

	@Column(name="FILENAME_TLTL")
	private String filenameTltl;

	@Column(name="FILENAME_YCTL")
	private String filenameYctl;

	@Column(name="FILENAME_YCTS")
	private String filenameYcts;

	@Column(name="FILENAME_YCTS2")
	private String filenameYcts2;

	@Column(name="FROM_ACC")
	private String fromAcc;

	@Column(name="FROM_SYS")
	private String fromSys;

	@Column(name="ID_TS")
	private String idTs;

	@Column(name="INS_PCODE")
	private BigDecimal insPcode;

	@Column(name="IS_DTLTL")
	private BigDecimal isDtltl;

	@Column(name="IS_DTLTS1")
	private BigDecimal isDtlts1;

	@Column(name="IS_DTLTS2")
	private BigDecimal isDtlts2;

	@Column(name="IS_PART_REV")
	private BigDecimal isPartRev;

	@Column(name="IS_RECEIVE1")
	private BigDecimal isReceive1;

	@Column(name="IS_RECEIVE2")
	private BigDecimal isReceive2;

	private String issuer;

	@Column(name="KIEM_DUYET1")
	private BigDecimal kiemDuyet1;

	@Column(name="KIEM_DUYET2")
	private BigDecimal kiemDuyet2;

	@Column(name="KS_GHINO")
	private String ksGhino;

	@Column(name="KSV_BNV")
	private String ksvBnv;

	@Column(name="KSV_TL1")
	private String ksvTl1;

	@Column(name="KSV_TL2")
	private String ksvTl2;

	@Column(name="KSV_TLTL")
	private String ksvTltl;

	@Column(name="KSV_YC1")
	private String ksvYc1;

	@Column(name="KSV_YC2")
	private String ksvYc2;

	@Column(name="KSV_YCTL")
	private String ksvYctl;

	@Column(name="KSVBNV_TLTL")
	private String ksvbnvTltl;

	@Column(name="KSVBNV_YCTL")
	private String ksvbnvYctl;

	@Column(name="KSVD_BNV")
	private BigDecimal ksvdBnv;

	@Column(name="KSVD_GHINO")
	private BigDecimal ksvdGhino;

	@Column(name="KSVD_TL1")
	private BigDecimal ksvdTl1;

	@Column(name="KSVD_TL2")
	private BigDecimal ksvdTl2;

	@Column(name="KSVD_YC1")
	private BigDecimal ksvdYc1;

	@Column(name="KSVD_YC2")
	private BigDecimal ksvdYc2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOCAL_DATE")
	private Date localDate;

	@Column(name="LOCAL_TIME")
	private BigDecimal localTime;

	@Column(name="LOG_ID")
	private BigDecimal logId;

	@Column(name="MA_GHINO")
	private BigDecimal maGhino;

	@Column(name="MA_TL1")
	private BigDecimal maTl1;

	@Column(name="MA_TL2")
	private BigDecimal maTl2;

	@Column(name="MA_TS1")
	private BigDecimal maTs1;

	@Column(name="MA_TS2")
	private BigDecimal maTs2;

	@Column(name="MERCHANT_CODE")
	private String merchantCode;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	private BigDecimal msgtype;

	@Column(name="MSGTYPE_DETAIL")
	private String msgtypeDetail;

	private String mvv;

	@Column(name="NAPAS_BN")
	private BigDecimal napasBn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NAPAS_EDIT_DATE")
	private Date napasEditDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="NGAY_XL")
	private Date ngayXl;

	@Column(name="NOTE_BAONO")
	private String noteBaono;

	@Column(name="NOTE_TL1")
	private String noteTl1;

	@Column(name="NOTE_TL2")
	private String noteTl2;

	@Column(name="NOTE_YC1")
	private String noteYc1;

	@Column(name="NOTE_YC2")
	private String noteYc2;

	@Column(name="NOTE_YCTL")
	private String noteYctl;

	private BigDecimal origrespcode;

	private BigDecimal origtrace;

	private String pan;

	@Column(name="PATH_TL1")
	private String pathTl1;

	@Column(name="PATH_TL2")
	private String pathTl2;

	private BigDecimal pcode;

	private BigDecimal pcode2;

	@Column(name="POS_ENTRY_CODE")
	private BigDecimal posEntryCode;

	@Column(name="QRPAY_20")
	private String qrpay20;

	@Column(name="RC_HOACHTOAN")
	private BigDecimal rcHoachtoan;

	private String refnum;

	private BigDecimal respcode;

	@Column(name="REVERSE_1")
	private String reverse1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name="SETTLEMENT_RATE")
	private BigDecimal settlementRate;

	@Column(name="STATUS_SEND")
	private BigDecimal statusSend;

	@Column(name="STATUS_YCTL")
	private BigDecimal statusYctl;

	private BigInteger stt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SYNC_BN_ECOM")
	private Date syncBnEcom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SYNC_TS_ECOM")
	private Date syncTsEcom;

	private String tcc;

	private String termid;

	@Column(name="TO_ACC")
	private String toAcc;

	private String token;

	@Column(name="TRACE_BANK")
	private BigDecimal traceBank;

	@Column(name="TRACK2_SERVICE_CODE")
	private String track2ServiceCode;

	@Column(name="TRAN_CASE")
	private String tranCase;

	@Column(name="TRANSIT_CSRR")
	private String transitCsrr;

	@Column(name="TSV_TL1")
	private String tsvTl1;

	@Column(name="TSV_TL2")
	private String tsvTl2;

	@Column(name="TSV_TLTL")
	private String tsvTltl;

	@Column(name="TSV_YC1")
	private String tsvYc1;

	@Column(name="TSV_YC2")
	private String tsvYc2;

	@Column(name="TSV_YCTL")
	private String tsvYctl;

	@Column(name="TSVBNV_TLTL")
	private String tsvbnvTltl;

	@Column(name="TSVBNV_YCTL")
	private String tsvbnvYctl;

	@Column(name="UPDATE_RPT")
	private BigDecimal updateRpt;

	@Column(name="UPDATE_RPT2")
	private BigDecimal updateRpt2;

	@Column(name="UPDATE_WS_ACQ")
	private BigDecimal updateWsAcq;

	@Column(name="UPDATE_WS_ISS")
	private BigDecimal updateWsIss;

	@Column(name="USER_GHINO")
	private String userGhino;

	@Column(name="USER_HOACHTOAN")
	private String userHoachtoan;

	@Column(name="USER_KD1")
	private String userKd1;

	@Column(name="USER_KD2")
	private String userKd2;

	public Gdt() {
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

	public BigDecimal getAcqCurrencyCode() {
		return this.acqCurrencyCode;
	}

	public void setAcqCurrencyCode(BigDecimal acqCurrencyCode) {
		this.acqCurrencyCode = acqCurrencyCode;
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

	public BigDecimal getAmountGhino() {
		return this.amountGhino;
	}

	public void setAmountGhino(BigDecimal amountGhino) {
		this.amountGhino = amountGhino;
	}

	public BigDecimal getAmountIss() {
		return this.amountIss;
	}

	public void setAmountIss(BigDecimal amountIss) {
		this.amountIss = amountIss;
	}

	public BigDecimal getAmountReturnIss() {
		return this.amountReturnIss;
	}

	public void setAmountReturnIss(BigDecimal amountReturnIss) {
		this.amountReturnIss = amountReturnIss;
	}

	public BigDecimal getAmountReturnSet() {
		return this.amountReturnSet;
	}

	public void setAmountReturnSet(BigDecimal amountReturnSet) {
		this.amountReturnSet = amountReturnSet;
	}

	public BigDecimal getAmountReturn1() {
		return this.amountReturn1;
	}

	public void setAmountReturn1(BigDecimal amountReturn1) {
		this.amountReturn1 = amountReturn1;
	}

	public BigDecimal getAmountReturn2() {
		return this.amountReturn2;
	}

	public void setAmountReturn2(BigDecimal amountReturn2) {
		this.amountReturn2 = amountReturn2;
	}

	public BigDecimal getAmountSet() {
		return this.amountSet;
	}

	public void setAmountSet(BigDecimal amountSet) {
		this.amountSet = amountSet;
	}

	public BigDecimal getAmountTs1() {
		return this.amountTs1;
	}

	public void setAmountTs1(BigDecimal amountTs1) {
		this.amountTs1 = amountTs1;
	}

	public BigDecimal getAmountTs2() {
		return this.amountTs2;
	}

	public void setAmountTs2(BigDecimal amountTs2) {
		this.amountTs2 = amountTs2;
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

	public BigDecimal getCancelCode() {
		return this.cancelCode;
	}

	public void setCancelCode(BigDecimal cancelCode) {
		this.cancelCode = cancelCode;
	}

	public Date getCancelDate() {
		return this.cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getCancelUser() {
		return this.cancelUser;
	}

	public void setCancelUser(String cancelUser) {
		this.cancelUser = cancelUser;
	}

	public BigDecimal getCardholderConvRate() {
		return this.cardholderConvRate;
	}

	public void setCardholderConvRate(BigDecimal cardholderConvRate) {
		this.cardholderConvRate = cardholderConvRate;
	}

	public BigDecimal getCodeRef() {
		return this.codeRef;
	}

	public void setCodeRef(BigDecimal codeRef) {
		this.codeRef = codeRef;
	}

	public String getContentFund() {
		return this.contentFund;
	}

	public void setContentFund(String contentFund) {
		this.contentFund = contentFund;
	}

	public BigDecimal getConvRate() {
		return this.convRate;
	}

	public void setConvRate(BigDecimal convRate) {
		this.convRate = convRate;
	}

	public Date getDateBnvTltl() {
		return this.dateBnvTltl;
	}

	public void setDateBnvTltl(Date dateBnvTltl) {
		this.dateBnvTltl = dateBnvTltl;
	}

	public Date getDateBnvYctl() {
		return this.dateBnvYctl;
	}

	public void setDateBnvYctl(Date dateBnvYctl) {
		this.dateBnvYctl = dateBnvYctl;
	}

	public Date getDateBnvd() {
		return this.dateBnvd;
	}

	public void setDateBnvd(Date dateBnvd) {
		this.dateBnvd = dateBnvd;
	}

	public Date getDateGhino() {
		return this.dateGhino;
	}

	public void setDateGhino(Date dateGhino) {
		this.dateGhino = dateGhino;
	}

	public Date getDateHoachtoan() {
		return this.dateHoachtoan;
	}

	public void setDateHoachtoan(Date dateHoachtoan) {
		this.dateHoachtoan = dateHoachtoan;
	}

	public Date getDateRequest() {
		return this.dateRequest;
	}

	public void setDateRequest(Date dateRequest) {
		this.dateRequest = dateRequest;
	}

	public Date getDateReturn() {
		return this.dateReturn;
	}

	public void setDateReturn(Date dateReturn) {
		this.dateReturn = dateReturn;
	}

	public Date getDateTl1() {
		return this.dateTl1;
	}

	public void setDateTl1(Date dateTl1) {
		this.dateTl1 = dateTl1;
	}

	public Date getDateTl2() {
		return this.dateTl2;
	}

	public void setDateTl2(Date dateTl2) {
		this.dateTl2 = dateTl2;
	}

	public Date getDateTltl() {
		return this.dateTltl;
	}

	public void setDateTltl(Date dateTltl) {
		this.dateTltl = dateTltl;
	}

	public Date getDateYc1() {
		return this.dateYc1;
	}

	public void setDateYc1(Date dateYc1) {
		this.dateYc1 = dateYc1;
	}

	public Date getDateYc2() {
		return this.dateYc2;
	}

	public void setDateYc2(Date dateYc2) {
		this.dateYc2 = dateYc2;
	}

	public Date getDateYctl() {
		return this.dateYctl;
	}

	public void setDateYctl(Date dateYctl) {
		this.dateYctl = dateYctl;
	}

	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public String getF90() {
		return this.f90;
	}

	public void setF90(String f90) {
		this.f90 = f90;
	}

	public byte[] getFileAttach1() {
		return this.fileAttach1;
	}

	public void setFileAttach1(byte[] fileAttach1) {
		this.fileAttach1 = fileAttach1;
	}

	public byte[] getFileAttach2() {
		return this.fileAttach2;
	}

	public void setFileAttach2(byte[] fileAttach2) {
		this.fileAttach2 = fileAttach2;
	}

	public String getFileBaono() {
		return this.fileBaono;
	}

	public void setFileBaono(String fileBaono) {
		this.fileBaono = fileBaono;
	}

	public byte[] getFileattachTltl() {
		return this.fileattachTltl;
	}

	public void setFileattachTltl(byte[] fileattachTltl) {
		this.fileattachTltl = fileattachTltl;
	}

	public byte[] getFileattachYctl() {
		return this.fileattachYctl;
	}

	public void setFileattachYctl(byte[] fileattachYctl) {
		this.fileattachYctl = fileattachYctl;
	}

	public String getFilenameTltl() {
		return this.filenameTltl;
	}

	public void setFilenameTltl(String filenameTltl) {
		this.filenameTltl = filenameTltl;
	}

	public String getFilenameYctl() {
		return this.filenameYctl;
	}

	public void setFilenameYctl(String filenameYctl) {
		this.filenameYctl = filenameYctl;
	}

	public String getFilenameYcts() {
		return this.filenameYcts;
	}

	public void setFilenameYcts(String filenameYcts) {
		this.filenameYcts = filenameYcts;
	}

	public String getFilenameYcts2() {
		return this.filenameYcts2;
	}

	public void setFilenameYcts2(String filenameYcts2) {
		this.filenameYcts2 = filenameYcts2;
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

	public String getIdTs() {
		return this.idTs;
	}

	public void setIdTs(String idTs) {
		this.idTs = idTs;
	}

	public BigDecimal getInsPcode() {
		return this.insPcode;
	}

	public void setInsPcode(BigDecimal insPcode) {
		this.insPcode = insPcode;
	}

	public BigDecimal getIsDtltl() {
		return this.isDtltl;
	}

	public void setIsDtltl(BigDecimal isDtltl) {
		this.isDtltl = isDtltl;
	}

	public BigDecimal getIsDtlts1() {
		return this.isDtlts1;
	}

	public void setIsDtlts1(BigDecimal isDtlts1) {
		this.isDtlts1 = isDtlts1;
	}

	public BigDecimal getIsDtlts2() {
		return this.isDtlts2;
	}

	public void setIsDtlts2(BigDecimal isDtlts2) {
		this.isDtlts2 = isDtlts2;
	}

	public BigDecimal getIsPartRev() {
		return this.isPartRev;
	}

	public void setIsPartRev(BigDecimal isPartRev) {
		this.isPartRev = isPartRev;
	}

	public BigDecimal getIsReceive1() {
		return this.isReceive1;
	}

	public void setIsReceive1(BigDecimal isReceive1) {
		this.isReceive1 = isReceive1;
	}

	public BigDecimal getIsReceive2() {
		return this.isReceive2;
	}

	public void setIsReceive2(BigDecimal isReceive2) {
		this.isReceive2 = isReceive2;
	}

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public BigDecimal getKiemDuyet1() {
		return this.kiemDuyet1;
	}

	public void setKiemDuyet1(BigDecimal kiemDuyet1) {
		this.kiemDuyet1 = kiemDuyet1;
	}

	public BigDecimal getKiemDuyet2() {
		return this.kiemDuyet2;
	}

	public void setKiemDuyet2(BigDecimal kiemDuyet2) {
		this.kiemDuyet2 = kiemDuyet2;
	}

	public String getKsGhino() {
		return this.ksGhino;
	}

	public void setKsGhino(String ksGhino) {
		this.ksGhino = ksGhino;
	}

	public String getKsvBnv() {
		return this.ksvBnv;
	}

	public void setKsvBnv(String ksvBnv) {
		this.ksvBnv = ksvBnv;
	}

	public String getKsvTl1() {
		return this.ksvTl1;
	}

	public void setKsvTl1(String ksvTl1) {
		this.ksvTl1 = ksvTl1;
	}

	public String getKsvTl2() {
		return this.ksvTl2;
	}

	public void setKsvTl2(String ksvTl2) {
		this.ksvTl2 = ksvTl2;
	}

	public String getKsvTltl() {
		return this.ksvTltl;
	}

	public void setKsvTltl(String ksvTltl) {
		this.ksvTltl = ksvTltl;
	}

	public String getKsvYc1() {
		return this.ksvYc1;
	}

	public void setKsvYc1(String ksvYc1) {
		this.ksvYc1 = ksvYc1;
	}

	public String getKsvYc2() {
		return this.ksvYc2;
	}

	public void setKsvYc2(String ksvYc2) {
		this.ksvYc2 = ksvYc2;
	}

	public String getKsvYctl() {
		return this.ksvYctl;
	}

	public void setKsvYctl(String ksvYctl) {
		this.ksvYctl = ksvYctl;
	}

	public String getKsvbnvTltl() {
		return this.ksvbnvTltl;
	}

	public void setKsvbnvTltl(String ksvbnvTltl) {
		this.ksvbnvTltl = ksvbnvTltl;
	}

	public String getKsvbnvYctl() {
		return this.ksvbnvYctl;
	}

	public void setKsvbnvYctl(String ksvbnvYctl) {
		this.ksvbnvYctl = ksvbnvYctl;
	}

	public BigDecimal getKsvdBnv() {
		return this.ksvdBnv;
	}

	public void setKsvdBnv(BigDecimal ksvdBnv) {
		this.ksvdBnv = ksvdBnv;
	}

	public BigDecimal getKsvdGhino() {
		return this.ksvdGhino;
	}

	public void setKsvdGhino(BigDecimal ksvdGhino) {
		this.ksvdGhino = ksvdGhino;
	}

	public BigDecimal getKsvdTl1() {
		return this.ksvdTl1;
	}

	public void setKsvdTl1(BigDecimal ksvdTl1) {
		this.ksvdTl1 = ksvdTl1;
	}

	public BigDecimal getKsvdTl2() {
		return this.ksvdTl2;
	}

	public void setKsvdTl2(BigDecimal ksvdTl2) {
		this.ksvdTl2 = ksvdTl2;
	}

	public BigDecimal getKsvdYc1() {
		return this.ksvdYc1;
	}

	public void setKsvdYc1(BigDecimal ksvdYc1) {
		this.ksvdYc1 = ksvdYc1;
	}

	public BigDecimal getKsvdYc2() {
		return this.ksvdYc2;
	}

	public void setKsvdYc2(BigDecimal ksvdYc2) {
		this.ksvdYc2 = ksvdYc2;
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

	public BigDecimal getMaGhino() {
		return this.maGhino;
	}

	public void setMaGhino(BigDecimal maGhino) {
		this.maGhino = maGhino;
	}

	public BigDecimal getMaTl1() {
		return this.maTl1;
	}

	public void setMaTl1(BigDecimal maTl1) {
		this.maTl1 = maTl1;
	}

	public BigDecimal getMaTl2() {
		return this.maTl2;
	}

	public void setMaTl2(BigDecimal maTl2) {
		this.maTl2 = maTl2;
	}

	public BigDecimal getMaTs1() {
		return this.maTs1;
	}

	public void setMaTs1(BigDecimal maTs1) {
		this.maTs1 = maTs1;
	}

	public BigDecimal getMaTs2() {
		return this.maTs2;
	}

	public void setMaTs2(BigDecimal maTs2) {
		this.maTs2 = maTs2;
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

	public String getMsgtypeDetail() {
		return this.msgtypeDetail;
	}

	public void setMsgtypeDetail(String msgtypeDetail) {
		this.msgtypeDetail = msgtypeDetail;
	}

	public String getMvv() {
		return this.mvv;
	}

	public void setMvv(String mvv) {
		this.mvv = mvv;
	}

	public BigDecimal getNapasBn() {
		return this.napasBn;
	}

	public void setNapasBn(BigDecimal napasBn) {
		this.napasBn = napasBn;
	}

	public Date getNapasEditDate() {
		return this.napasEditDate;
	}

	public void setNapasEditDate(Date napasEditDate) {
		this.napasEditDate = napasEditDate;
	}

	public Date getNgayXl() {
		return this.ngayXl;
	}

	public void setNgayXl(Date ngayXl) {
		this.ngayXl = ngayXl;
	}

	public String getNoteBaono() {
		return this.noteBaono;
	}

	public void setNoteBaono(String noteBaono) {
		this.noteBaono = noteBaono;
	}

	public String getNoteTl1() {
		return this.noteTl1;
	}

	public void setNoteTl1(String noteTl1) {
		this.noteTl1 = noteTl1;
	}

	public String getNoteTl2() {
		return this.noteTl2;
	}

	public void setNoteTl2(String noteTl2) {
		this.noteTl2 = noteTl2;
	}

	public String getNoteYc1() {
		return this.noteYc1;
	}

	public void setNoteYc1(String noteYc1) {
		this.noteYc1 = noteYc1;
	}

	public String getNoteYc2() {
		return this.noteYc2;
	}

	public void setNoteYc2(String noteYc2) {
		this.noteYc2 = noteYc2;
	}

	public String getNoteYctl() {
		return this.noteYctl;
	}

	public void setNoteYctl(String noteYctl) {
		this.noteYctl = noteYctl;
	}

	public BigDecimal getOrigrespcode() {
		return this.origrespcode;
	}

	public void setOrigrespcode(BigDecimal origrespcode) {
		this.origrespcode = origrespcode;
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

	public String getPathTl1() {
		return this.pathTl1;
	}

	public void setPathTl1(String pathTl1) {
		this.pathTl1 = pathTl1;
	}

	public String getPathTl2() {
		return this.pathTl2;
	}

	public void setPathTl2(String pathTl2) {
		this.pathTl2 = pathTl2;
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

	public String getQrpay20() {
		return this.qrpay20;
	}

	public void setQrpay20(String qrpay20) {
		this.qrpay20 = qrpay20;
	}

	public BigDecimal getRcHoachtoan() {
		return this.rcHoachtoan;
	}

	public void setRcHoachtoan(BigDecimal rcHoachtoan) {
		this.rcHoachtoan = rcHoachtoan;
	}

	public String getRefnum() {
		return this.refnum;
	}

	public void setRefnum(String refnum) {
		this.refnum = refnum;
	}

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public String getReverse1() {
		return this.reverse1;
	}

	public void setReverse1(String reverse1) {
		this.reverse1 = reverse1;
	}

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public BigDecimal getSettlementRate() {
		return this.settlementRate;
	}

	public void setSettlementRate(BigDecimal settlementRate) {
		this.settlementRate = settlementRate;
	}

	public BigDecimal getStatusSend() {
		return this.statusSend;
	}

	public void setStatusSend(BigDecimal statusSend) {
		this.statusSend = statusSend;
	}

	public BigDecimal getStatusYctl() {
		return this.statusYctl;
	}

	public void setStatusYctl(BigDecimal statusYctl) {
		this.statusYctl = statusYctl;
	}

	public BigInteger getStt() {
		return this.stt;
	}

	public void setStt(BigInteger stt) {
		this.stt = stt;
	}

	public Date getSyncBnEcom() {
		return this.syncBnEcom;
	}

	public void setSyncBnEcom(Date syncBnEcom) {
		this.syncBnEcom = syncBnEcom;
	}

	public Date getSyncTsEcom() {
		return this.syncTsEcom;
	}

	public void setSyncTsEcom(Date syncTsEcom) {
		this.syncTsEcom = syncTsEcom;
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

	public BigDecimal getTraceBank() {
		return this.traceBank;
	}

	public void setTraceBank(BigDecimal traceBank) {
		this.traceBank = traceBank;
	}

	public String getTrack2ServiceCode() {
		return this.track2ServiceCode;
	}

	public void setTrack2ServiceCode(String track2ServiceCode) {
		this.track2ServiceCode = track2ServiceCode;
	}

	public String getTranCase() {
		return this.tranCase;
	}

	public void setTranCase(String tranCase) {
		this.tranCase = tranCase;
	}

	public String getTransitCsrr() {
		return this.transitCsrr;
	}

	public void setTransitCsrr(String transitCsrr) {
		this.transitCsrr = transitCsrr;
	}

	public String getTsvTl1() {
		return this.tsvTl1;
	}

	public void setTsvTl1(String tsvTl1) {
		this.tsvTl1 = tsvTl1;
	}

	public String getTsvTl2() {
		return this.tsvTl2;
	}

	public void setTsvTl2(String tsvTl2) {
		this.tsvTl2 = tsvTl2;
	}

	public String getTsvTltl() {
		return this.tsvTltl;
	}

	public void setTsvTltl(String tsvTltl) {
		this.tsvTltl = tsvTltl;
	}

	public String getTsvYc1() {
		return this.tsvYc1;
	}

	public void setTsvYc1(String tsvYc1) {
		this.tsvYc1 = tsvYc1;
	}

	public String getTsvYc2() {
		return this.tsvYc2;
	}

	public void setTsvYc2(String tsvYc2) {
		this.tsvYc2 = tsvYc2;
	}

	public String getTsvYctl() {
		return this.tsvYctl;
	}

	public void setTsvYctl(String tsvYctl) {
		this.tsvYctl = tsvYctl;
	}

	public String getTsvbnvTltl() {
		return this.tsvbnvTltl;
	}

	public void setTsvbnvTltl(String tsvbnvTltl) {
		this.tsvbnvTltl = tsvbnvTltl;
	}

	public String getTsvbnvYctl() {
		return this.tsvbnvYctl;
	}

	public void setTsvbnvYctl(String tsvbnvYctl) {
		this.tsvbnvYctl = tsvbnvYctl;
	}

	public BigDecimal getUpdateRpt() {
		return this.updateRpt;
	}

	public void setUpdateRpt(BigDecimal updateRpt) {
		this.updateRpt = updateRpt;
	}

	public BigDecimal getUpdateRpt2() {
		return this.updateRpt2;
	}

	public void setUpdateRpt2(BigDecimal updateRpt2) {
		this.updateRpt2 = updateRpt2;
	}

	public BigDecimal getUpdateWsAcq() {
		return this.updateWsAcq;
	}

	public void setUpdateWsAcq(BigDecimal updateWsAcq) {
		this.updateWsAcq = updateWsAcq;
	}

	public BigDecimal getUpdateWsIss() {
		return this.updateWsIss;
	}

	public void setUpdateWsIss(BigDecimal updateWsIss) {
		this.updateWsIss = updateWsIss;
	}

	public String getUserGhino() {
		return this.userGhino;
	}

	public void setUserGhino(String userGhino) {
		this.userGhino = userGhino;
	}

	public String getUserHoachtoan() {
		return this.userHoachtoan;
	}

	public void setUserHoachtoan(String userHoachtoan) {
		this.userHoachtoan = userHoachtoan;
	}

	public String getUserKd1() {
		return this.userKd1;
	}

	public void setUserKd1(String userKd1) {
		this.userKd1 = userKd1;
	}

	public String getUserKd2() {
		return this.userKd2;
	}

	public void setUserKd2(String userKd2) {
		this.userKd2 = userKd2;
	}

}