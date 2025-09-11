package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the MAP_BANKS_2CHAN database table.
 * 
 */
@Entity
@Table(name="MAP_BANKS_2CHAN")
@NamedQuery(name="MapBanks2chan.findAll", query="SELECT m FROM MapBanks2chan m")
public class MapBanks2chan implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACH_BEN")
	private BigDecimal achBen;

	@Column(name="ACH_ISS")
	private BigDecimal achIss;

	private String active;

	@Column(name="BANK_NAME")
	private String bankName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="END_DATE")
	private Date endDate;

	@Column(name="IBFT_BEN")
	private BigDecimal ibftBen;

	@Column(name="IBFT_ISS")
	private BigDecimal ibftIss;

	@Column(name="INS_CODE")
	private String insCode;

	@Column(name="SHCLOG_ID")
	private BigDecimal shclogId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="START_DATE")
	private Date startDate;

	public MapBanks2chan() {
	}

	public BigDecimal getAchBen() {
		return this.achBen;
	}

	public void setAchBen(BigDecimal achBen) {
		this.achBen = achBen;
	}

	public BigDecimal getAchIss() {
		return this.achIss;
	}

	public void setAchIss(BigDecimal achIss) {
		this.achIss = achIss;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getIbftBen() {
		return this.ibftBen;
	}

	public void setIbftBen(BigDecimal ibftBen) {
		this.ibftBen = ibftBen;
	}

	public BigDecimal getIbftIss() {
		return this.ibftIss;
	}

	public void setIbftIss(BigDecimal ibftIss) {
		this.ibftIss = ibftIss;
	}

	public String getInsCode() {
		return this.insCode;
	}

	public void setInsCode(String insCode) {
		this.insCode = insCode;
	}

	public BigDecimal getShclogId() {
		return this.shclogId;
	}

	public void setShclogId(BigDecimal shclogId) {
		this.shclogId = shclogId;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}