package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the ACH_MAP_BANKID database table.
 * 
 */
@Entity
@Table(name="ACH_MAP_BANKID")
@NamedQuery(name="AchMapBankid.findAll", query="SELECT a FROM AchMapBankid a")
public class AchMapBankid implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ACH_BANK_ID")
	private BigDecimal achBankId;

	@Column(name="SHCLOG_ID")
	private BigDecimal shclogId;

	public AchMapBankid() {
	}

	public BigDecimal getAchBankId() {
		return this.achBankId;
	}

	public void setAchBankId(BigDecimal achBankId) {
		this.achBankId = achBankId;
	}

	public BigDecimal getShclogId() {
		return this.shclogId;
	}

	public void setShclogId(BigDecimal shclogId) {
		this.shclogId = shclogId;
	}

}