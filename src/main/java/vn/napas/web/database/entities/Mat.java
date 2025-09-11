package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the MATS database table.
 * 
 */
@Entity
@Table(name="MATS")
@NamedQuery(name="Mat.findAll", query="SELECT m FROM Mat m")
public class Mat implements Serializable {
	private static final long serialVersionUID = 1L;

	private String active;

	@Column(name="MERCHANT_TYPE")
	private String merchantType;

	@Column(name="MERCHANT_TYPE2")
	private String merchantType2;

	@Column(name="TS_CODE")
	private BigDecimal tsCode;

	@Column(name="TS_CODE2")
	private BigDecimal tsCode2;

	@Column(name="TS_ID")
	private BigDecimal tsId;

	@Column(name="TS_MEANNING")
	private String tsMeanning;

	@Column(name="TS_MEANNING_OLD")
	private String tsMeanningOld;

	@Column(name="TS_TYPE")
	private BigDecimal tsType;

	public Mat() {
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getMerchantType() {
		return this.merchantType;
	}

	public void setMerchantType(String merchantType) {
		this.merchantType = merchantType;
	}

	public String getMerchantType2() {
		return this.merchantType2;
	}

	public void setMerchantType2(String merchantType2) {
		this.merchantType2 = merchantType2;
	}

	public BigDecimal getTsCode() {
		return this.tsCode;
	}

	public void setTsCode(BigDecimal tsCode) {
		this.tsCode = tsCode;
	}

	public BigDecimal getTsCode2() {
		return this.tsCode2;
	}

	public void setTsCode2(BigDecimal tsCode2) {
		this.tsCode2 = tsCode2;
	}

	public BigDecimal getTsId() {
		return this.tsId;
	}

	public void setTsId(BigDecimal tsId) {
		this.tsId = tsId;
	}

	public String getTsMeanning() {
		return this.tsMeanning;
	}

	public void setTsMeanning(String tsMeanning) {
		this.tsMeanning = tsMeanning;
	}

	public String getTsMeanningOld() {
		return this.tsMeanningOld;
	}

	public void setTsMeanningOld(String tsMeanningOld) {
		this.tsMeanningOld = tsMeanningOld;
	}

	public BigDecimal getTsType() {
		return this.tsType;
	}

	public void setTsType(BigDecimal tsType) {
		this.tsType = tsType;
	}

}