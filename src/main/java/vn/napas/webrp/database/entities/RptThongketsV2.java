package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the RPT_THONGKETS_V2 database table.
 * 
 */
@Entity
@Table(name="RPT_THONGKETS_V2")
@NamedQuery(name="RptThongketsV2.findAll", query="SELECT r FROM RptThongketsV2 r")
public class RptThongketsV2 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="CREAT_USER")
	private String creatUser;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_TIME")
	private Date createTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date fromdate;

	private BigDecimal gdgl;

	@Column(name="GDGL_AMOUNT")
	private BigDecimal gdglAmount;

	private BigDecimal gdht1;

	@Column(name="GDHT1_AMOUNT")
	private BigDecimal gdht1Amount;

	private BigDecimal gdht2;

	@Column(name="GDHT2_AMOUNT")
	private BigDecimal gdht2Amount;

	private BigDecimal gdkht1;

	@Column(name="GDKHT1_AMOUNT")
	private BigDecimal gdkht1Amount;

	private BigDecimal gdkht2;

	@Column(name="GDKHT2_AMOUNT")
	private BigDecimal gdkht2Amount;

	private BigDecimal gdqh1;

	@Column(name="GDQH1_AMOUNT")
	private BigDecimal gdqh1Amount;

	private BigDecimal gdqh2;

	@Column(name="GDQH2_AMOUNT")
	private BigDecimal gdqh2Amount;

	private BigDecimal gdtc;

	@Column(name="GDTC_AMOUNT")
	private BigDecimal gdtcAmount;

	private BigDecimal gdts1;

	@Column(name="GDTS1_AMOUNT")
	private BigDecimal gdts1Amount;

	private BigDecimal gdts2;

	@Column(name="GDTS2_AMOUNT")
	private BigDecimal gdts2Amount;

	private String inmonth;

	private String matb;

	private BigDecimal nh;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="QH_TIME1")
	private Date qhTime1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="QH_TIME2")
	private Date qhTime2;

	private String rolenh;

	private String tennh;

	@Temporal(TemporalType.TIMESTAMP)
	private Date todate;

	private BigDecimal tongts;

	public RptThongketsV2() {
	}

	public String getCreatUser() {
		return this.creatUser;
	}

	public void setCreatUser(String creatUser) {
		this.creatUser = creatUser;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getFromdate() {
		return this.fromdate;
	}

	public void setFromdate(Date fromdate) {
		this.fromdate = fromdate;
	}

	public BigDecimal getGdgl() {
		return this.gdgl;
	}

	public void setGdgl(BigDecimal gdgl) {
		this.gdgl = gdgl;
	}

	public BigDecimal getGdglAmount() {
		return this.gdglAmount;
	}

	public void setGdglAmount(BigDecimal gdglAmount) {
		this.gdglAmount = gdglAmount;
	}

	public BigDecimal getGdht1() {
		return this.gdht1;
	}

	public void setGdht1(BigDecimal gdht1) {
		this.gdht1 = gdht1;
	}

	public BigDecimal getGdht1Amount() {
		return this.gdht1Amount;
	}

	public void setGdht1Amount(BigDecimal gdht1Amount) {
		this.gdht1Amount = gdht1Amount;
	}

	public BigDecimal getGdht2() {
		return this.gdht2;
	}

	public void setGdht2(BigDecimal gdht2) {
		this.gdht2 = gdht2;
	}

	public BigDecimal getGdht2Amount() {
		return this.gdht2Amount;
	}

	public void setGdht2Amount(BigDecimal gdht2Amount) {
		this.gdht2Amount = gdht2Amount;
	}

	public BigDecimal getGdkht1() {
		return this.gdkht1;
	}

	public void setGdkht1(BigDecimal gdkht1) {
		this.gdkht1 = gdkht1;
	}

	public BigDecimal getGdkht1Amount() {
		return this.gdkht1Amount;
	}

	public void setGdkht1Amount(BigDecimal gdkht1Amount) {
		this.gdkht1Amount = gdkht1Amount;
	}

	public BigDecimal getGdkht2() {
		return this.gdkht2;
	}

	public void setGdkht2(BigDecimal gdkht2) {
		this.gdkht2 = gdkht2;
	}

	public BigDecimal getGdkht2Amount() {
		return this.gdkht2Amount;
	}

	public void setGdkht2Amount(BigDecimal gdkht2Amount) {
		this.gdkht2Amount = gdkht2Amount;
	}

	public BigDecimal getGdqh1() {
		return this.gdqh1;
	}

	public void setGdqh1(BigDecimal gdqh1) {
		this.gdqh1 = gdqh1;
	}

	public BigDecimal getGdqh1Amount() {
		return this.gdqh1Amount;
	}

	public void setGdqh1Amount(BigDecimal gdqh1Amount) {
		this.gdqh1Amount = gdqh1Amount;
	}

	public BigDecimal getGdqh2() {
		return this.gdqh2;
	}

	public void setGdqh2(BigDecimal gdqh2) {
		this.gdqh2 = gdqh2;
	}

	public BigDecimal getGdqh2Amount() {
		return this.gdqh2Amount;
	}

	public void setGdqh2Amount(BigDecimal gdqh2Amount) {
		this.gdqh2Amount = gdqh2Amount;
	}

	public BigDecimal getGdtc() {
		return this.gdtc;
	}

	public void setGdtc(BigDecimal gdtc) {
		this.gdtc = gdtc;
	}

	public BigDecimal getGdtcAmount() {
		return this.gdtcAmount;
	}

	public void setGdtcAmount(BigDecimal gdtcAmount) {
		this.gdtcAmount = gdtcAmount;
	}

	public BigDecimal getGdts1() {
		return this.gdts1;
	}

	public void setGdts1(BigDecimal gdts1) {
		this.gdts1 = gdts1;
	}

	public BigDecimal getGdts1Amount() {
		return this.gdts1Amount;
	}

	public void setGdts1Amount(BigDecimal gdts1Amount) {
		this.gdts1Amount = gdts1Amount;
	}

	public BigDecimal getGdts2() {
		return this.gdts2;
	}

	public void setGdts2(BigDecimal gdts2) {
		this.gdts2 = gdts2;
	}

	public BigDecimal getGdts2Amount() {
		return this.gdts2Amount;
	}

	public void setGdts2Amount(BigDecimal gdts2Amount) {
		this.gdts2Amount = gdts2Amount;
	}

	public String getInmonth() {
		return this.inmonth;
	}

	public void setInmonth(String inmonth) {
		this.inmonth = inmonth;
	}

	public String getMatb() {
		return this.matb;
	}

	public void setMatb(String matb) {
		this.matb = matb;
	}

	public BigDecimal getNh() {
		return this.nh;
	}

	public void setNh(BigDecimal nh) {
		this.nh = nh;
	}

	public Date getQhTime1() {
		return this.qhTime1;
	}

	public void setQhTime1(Date qhTime1) {
		this.qhTime1 = qhTime1;
	}

	public Date getQhTime2() {
		return this.qhTime2;
	}

	public void setQhTime2(Date qhTime2) {
		this.qhTime2 = qhTime2;
	}

	public String getRolenh() {
		return this.rolenh;
	}

	public void setRolenh(String rolenh) {
		this.rolenh = rolenh;
	}

	public String getTennh() {
		return this.tennh;
	}

	public void setTennh(String tennh) {
		this.tennh = tennh;
	}

	public Date getTodate() {
		return this.todate;
	}

	public void setTodate(Date todate) {
		this.todate = todate;
	}

	public BigDecimal getTongts() {
		return this.tongts;
	}

	public void setTongts(BigDecimal tongts) {
		this.tongts = tongts;
	}

}