package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the GSHM_TINHTOAN database table.
 * 
 */
@Entity
@Table(name="GSHM_TINHTOAN")
@NamedQuery(name="GshmTinhtoan.findAll", query="SELECT g FROM GshmTinhtoan g")
public class GshmTinhtoan implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal atm;

	@Column(name="BANK_CODE")
	private String bankCode;

	@Column(name="BANK_ID")
	private BigDecimal bankId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="END_BLOCK")
	private Date endBlock;

	private BigDecimal ibft;

	private BigDecimal pos;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="START_BLOCK")
	private Date startBlock;

	public GshmTinhtoan() {
	}

	public BigDecimal getAtm() {
		return this.atm;
	}

	public void setAtm(BigDecimal atm) {
		this.atm = atm;
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public BigDecimal getBankId() {
		return this.bankId;
	}

	public void setBankId(BigDecimal bankId) {
		this.bankId = bankId;
	}

	public Date getEndBlock() {
		return this.endBlock;
	}

	public void setEndBlock(Date endBlock) {
		this.endBlock = endBlock;
	}

	public BigDecimal getIbft() {
		return this.ibft;
	}

	public void setIbft(BigDecimal ibft) {
		this.ibft = ibft;
	}

	public BigDecimal getPos() {
		return this.pos;
	}

	public void setPos(BigDecimal pos) {
		this.pos = pos;
	}

	public Date getStartBlock() {
		return this.startBlock;
	}

	public void setStartBlock(Date startBlock) {
		this.startBlock = startBlock;
	}

}