package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the GSHM_TINHTOAN_GD2 database table.
 * 
 */
@Entity
@Table(name="GSHM_TINHTOAN_GD2")
@NamedQuery(name="GshmTinhtoanGd2.findAll", query="SELECT g FROM GshmTinhtoanGd2 g")
public class GshmTinhtoanGd2 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ATM_GHICO")
	private BigDecimal atmGhico;

	@Column(name="ATM_GHINO")
	private BigDecimal atmGhino;

	@Column(name="BANK_CODE")
	private String bankCode;

	@Column(name="BANK_ID")
	private BigDecimal bankId;

	@Column(name="CASHINOUT_GHICO")
	private BigDecimal cashinoutGhico;

	@Column(name="CASHINOUT_GHINO")
	private BigDecimal cashinoutGhino;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="END_BLOCK")
	private Date endBlock;

	@Column(name="HTCT_GHICO")
	private BigDecimal htctGhico;

	@Column(name="HTCT_GHINO")
	private BigDecimal htctGhino;

	@Column(name="IBFT_GHICO")
	private BigDecimal ibftGhico;

	@Column(name="IBFT_GHINO")
	private BigDecimal ibftGhino;

	@Column(name="POS_GHICO")
	private BigDecimal posGhico;

	@Column(name="POS_GHINO")
	private BigDecimal posGhino;

	@Column(name="QRSW_GHICO")
	private BigDecimal qrswGhico;

	@Column(name="QRSW_GHINO")
	private BigDecimal qrswGhino;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="START_BLOCK")
	private Date startBlock;

	public GshmTinhtoanGd2() {
	}

	public BigDecimal getAtmGhico() {
		return this.atmGhico;
	}

	public void setAtmGhico(BigDecimal atmGhico) {
		this.atmGhico = atmGhico;
	}

	public BigDecimal getAtmGhino() {
		return this.atmGhino;
	}

	public void setAtmGhino(BigDecimal atmGhino) {
		this.atmGhino = atmGhino;
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

	public BigDecimal getCashinoutGhico() {
		return this.cashinoutGhico;
	}

	public void setCashinoutGhico(BigDecimal cashinoutGhico) {
		this.cashinoutGhico = cashinoutGhico;
	}

	public BigDecimal getCashinoutGhino() {
		return this.cashinoutGhino;
	}

	public void setCashinoutGhino(BigDecimal cashinoutGhino) {
		this.cashinoutGhino = cashinoutGhino;
	}

	public Date getEndBlock() {
		return this.endBlock;
	}

	public void setEndBlock(Date endBlock) {
		this.endBlock = endBlock;
	}

	public BigDecimal getHtctGhico() {
		return this.htctGhico;
	}

	public void setHtctGhico(BigDecimal htctGhico) {
		this.htctGhico = htctGhico;
	}

	public BigDecimal getHtctGhino() {
		return this.htctGhino;
	}

	public void setHtctGhino(BigDecimal htctGhino) {
		this.htctGhino = htctGhino;
	}

	public BigDecimal getIbftGhico() {
		return this.ibftGhico;
	}

	public void setIbftGhico(BigDecimal ibftGhico) {
		this.ibftGhico = ibftGhico;
	}

	public BigDecimal getIbftGhino() {
		return this.ibftGhino;
	}

	public void setIbftGhino(BigDecimal ibftGhino) {
		this.ibftGhino = ibftGhino;
	}

	public BigDecimal getPosGhico() {
		return this.posGhico;
	}

	public void setPosGhico(BigDecimal posGhico) {
		this.posGhico = posGhico;
	}

	public BigDecimal getPosGhino() {
		return this.posGhino;
	}

	public void setPosGhino(BigDecimal posGhino) {
		this.posGhino = posGhino;
	}

	public BigDecimal getQrswGhico() {
		return this.qrswGhico;
	}

	public void setQrswGhico(BigDecimal qrswGhico) {
		this.qrswGhico = qrswGhico;
	}

	public BigDecimal getQrswGhino() {
		return this.qrswGhino;
	}

	public void setQrswGhino(BigDecimal qrswGhino) {
		this.qrswGhino = qrswGhino;
	}

	public Date getStartBlock() {
		return this.startBlock;
	}

	public void setStartBlock(Date startBlock) {
		this.startBlock = startBlock;
	}

}