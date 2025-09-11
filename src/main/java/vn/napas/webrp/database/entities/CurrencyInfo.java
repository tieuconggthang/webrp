package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the CURRENCY_INFO database table.
 * 
 */
@Entity
@Table(name="CURRENCY_INFO")
@NamedQuery(name="CurrencyInfo.findAll", query="SELECT c FROM CurrencyInfo c")
public class CurrencyInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="APPLIED_DATE")
	private Date appliedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="APPLIED_DATE_SELL")
	private Date appliedDateSell;

	@Column(name="BUY_FEE_EXH_FLAT")
	private BigDecimal buyFeeExhFlat;

	@Column(name="BUY_FEE_EXH_RATIO")
	private BigDecimal buyFeeExhRatio;

	@Column(name="BUY_RATE")
	private BigDecimal buyRate;

	@Column(name="BUY_RATE_QD")
	private BigDecimal buyRateQd;

	@Column(name="BUY_RATE_TCTV")
	private BigDecimal buyRateTctv;

	@Column(name="CURRENCY_CODE")
	private String currencyCode;

	@Column(name="CURRENCY_ID")
	private int currencyId;

	@Column(name="CURRENCY_REF")
	private String currencyRef;

	@Column(name="INSTITUTION_CODE")
	private String institutionCode;

	@Column(name="MAX_RATIO")
	private BigDecimal maxRatio;

	@Column(name="SELL_FEE_EXH_FLAT")
	private BigDecimal sellFeeExhFlat;

	@Column(name="SELL_FEE_EXH_RATIO")
	private BigDecimal sellFeeExhRatio;

	@Column(name="SELL_RATE")
	private BigDecimal sellRate;

	@Column(name="SELL_RATE_QD")
	private BigDecimal sellRateQd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATE_TIME")
	private Date updateTime;

	@Column(name="USER_INSERT")
	private String userInsert;

	public CurrencyInfo() {
	}

	public Date getAppliedDate() {
		return this.appliedDate;
	}

	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}

	public Date getAppliedDateSell() {
		return this.appliedDateSell;
	}

	public void setAppliedDateSell(Date appliedDateSell) {
		this.appliedDateSell = appliedDateSell;
	}

	public BigDecimal getBuyFeeExhFlat() {
		return this.buyFeeExhFlat;
	}

	public void setBuyFeeExhFlat(BigDecimal buyFeeExhFlat) {
		this.buyFeeExhFlat = buyFeeExhFlat;
	}

	public BigDecimal getBuyFeeExhRatio() {
		return this.buyFeeExhRatio;
	}

	public void setBuyFeeExhRatio(BigDecimal buyFeeExhRatio) {
		this.buyFeeExhRatio = buyFeeExhRatio;
	}

	public BigDecimal getBuyRate() {
		return this.buyRate;
	}

	public void setBuyRate(BigDecimal buyRate) {
		this.buyRate = buyRate;
	}

	public BigDecimal getBuyRateQd() {
		return this.buyRateQd;
	}

	public void setBuyRateQd(BigDecimal buyRateQd) {
		this.buyRateQd = buyRateQd;
	}

	public BigDecimal getBuyRateTctv() {
		return this.buyRateTctv;
	}

	public void setBuyRateTctv(BigDecimal buyRateTctv) {
		this.buyRateTctv = buyRateTctv;
	}

	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public int getCurrencyId() {
		return this.currencyId;
	}

	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencyRef() {
		return this.currencyRef;
	}

	public void setCurrencyRef(String currencyRef) {
		this.currencyRef = currencyRef;
	}

	public String getInstitutionCode() {
		return this.institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public BigDecimal getMaxRatio() {
		return this.maxRatio;
	}

	public void setMaxRatio(BigDecimal maxRatio) {
		this.maxRatio = maxRatio;
	}

	public BigDecimal getSellFeeExhFlat() {
		return this.sellFeeExhFlat;
	}

	public void setSellFeeExhFlat(BigDecimal sellFeeExhFlat) {
		this.sellFeeExhFlat = sellFeeExhFlat;
	}

	public BigDecimal getSellFeeExhRatio() {
		return this.sellFeeExhRatio;
	}

	public void setSellFeeExhRatio(BigDecimal sellFeeExhRatio) {
		this.sellFeeExhRatio = sellFeeExhRatio;
	}

	public BigDecimal getSellRate() {
		return this.sellRate;
	}

	public void setSellRate(BigDecimal sellRate) {
		this.sellRate = sellRate;
	}

	public BigDecimal getSellRateQd() {
		return this.sellRateQd;
	}

	public void setSellRateQd(BigDecimal sellRateQd) {
		this.sellRateQd = sellRateQd;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUserInsert() {
		return this.userInsert;
	}

	public void setUserInsert(String userInsert) {
		this.userInsert = userInsert;
	}

}