package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the TBL_SCB database table.
 * 
 */
@Entity
@Table(name="TBL_SCB")
@NamedQuery(name="TblScb.findAll", query="SELECT t FROM TblScb t")
public class TblScb implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="AMOUNT_BEN")
	private BigDecimal amountBen;

	@Column(name="AMOUNT_ISS")
	private BigDecimal amountIss;

	@Column(name="AMOUNT_NET")
	private BigDecimal amountNet;

	@Column(name="BANK_ID")
	private String bankId;

	public TblScb() {
	}

	public BigDecimal getAmountBen() {
		return this.amountBen;
	}

	public void setAmountBen(BigDecimal amountBen) {
		this.amountBen = amountBen;
	}

	public BigDecimal getAmountIss() {
		return this.amountIss;
	}

	public void setAmountIss(BigDecimal amountIss) {
		this.amountIss = amountIss;
	}

	public BigDecimal getAmountNet() {
		return this.amountNet;
	}

	public void setAmountNet(BigDecimal amountNet) {
		this.amountNet = amountNet;
	}

	public String getBankId() {
		return this.bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

}