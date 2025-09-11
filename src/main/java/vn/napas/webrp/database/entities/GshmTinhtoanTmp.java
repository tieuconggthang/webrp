package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the GSHM_TINHTOAN_TMP database table.
 * 
 */
@Entity
@Table(name="GSHM_TINHTOAN_TMP")
@NamedQuery(name="GshmTinhtoanTmp.findAll", query="SELECT g FROM GshmTinhtoanTmp g")
public class GshmTinhtoanTmp implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal atm;

	@Column(name="BANK_ID")
	private BigDecimal bankId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="END_BLOCK")
	private Date endBlock;

	private BigDecimal ibft;

	private BigDecimal msgtype;

	private String pcode;

	private BigDecimal pos;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="START_BLOCK")
	private Date startBlock;

	public GshmTinhtoanTmp() {
	}

	public BigDecimal getAtm() {
		return this.atm;
	}

	public void setAtm(BigDecimal atm) {
		this.atm = atm;
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

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public String getPcode() {
		return this.pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
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