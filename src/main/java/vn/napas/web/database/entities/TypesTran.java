package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the TYPES_TRAN database table.
 * 
 */
@Entity
@Table(name="TYPES_TRAN")
@NamedQuery(name="TypesTran.findAll", query="SELECT t FROM TypesTran t")
public class TypesTran implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal abb;

	private String des;

	private String meanning;

	@Column(name="MERCHANT_TYPE")
	private BigDecimal merchantType;

	private String pcode;

	public TypesTran() {
	}

	public BigDecimal getAbb() {
		return this.abb;
	}

	public void setAbb(BigDecimal abb) {
		this.abb = abb;
	}

	public String getDes() {
		return this.des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getMeanning() {
		return this.meanning;
	}

	public void setMeanning(String meanning) {
		this.meanning = meanning;
	}

	public BigDecimal getMerchantType() {
		return this.merchantType;
	}

	public void setMerchantType(BigDecimal merchantType) {
		this.merchantType = merchantType;
	}

	public String getPcode() {
		return this.pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

}