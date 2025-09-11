package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * The persistent class for the TMP_GSHM_IBT database table.
 * 
 */
@Entity
@Table(name="TMP_GSHM_IBT")
@NamedQuery(name="TmpGshmIbt.findAll", query="SELECT t FROM TmpGshmIbt t")
public class TmpGshmIbt implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal amount;

	private String bankid;

	private BigInteger blockid;

	@Column(name="SERVICE_CODE")
	private String serviceCode;

	public TmpGshmIbt() {
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getBankid() {
		return this.bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}

	public BigInteger getBlockid() {
		return this.blockid;
	}

	public void setBlockid(BigInteger blockid) {
		this.blockid = blockid;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

}