package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the MAP_BANKS database table.
 * 
 */
@Entity
@Table(name="MAP_BANKS")
@NamedQuery(name="MapBank.findAll", query="SELECT m FROM MapBank m")
public class MapBank implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="BANK_NAME")
	private String bankName;

	private BigDecimal ben;

	@Column(name="INS_CODE")
	private String insCode;

	private BigDecimal iss;

	@Column(name="SHCLOG_ID")
	private BigDecimal shclogId;

	public MapBank() {
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public BigDecimal getBen() {
		return this.ben;
	}

	public void setBen(BigDecimal ben) {
		this.ben = ben;
	}

	public String getInsCode() {
		return this.insCode;
	}

	public void setInsCode(String insCode) {
		this.insCode = insCode;
	}

	public BigDecimal getIss() {
		return this.iss;
	}

	public void setIss(BigDecimal iss) {
		this.iss = iss;
	}

	public BigDecimal getShclogId() {
		return this.shclogId;
	}

	public void setShclogId(BigDecimal shclogId) {
		this.shclogId = shclogId;
	}

}