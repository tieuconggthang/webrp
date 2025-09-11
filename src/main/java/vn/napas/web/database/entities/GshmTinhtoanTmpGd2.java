package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the GSHM_TINHTOAN_TMP_GD2 database table.
 * 
 */
@Entity
@Table(name="GSHM_TINHTOAN_TMP_GD2")
@NamedQuery(name="GshmTinhtoanTmpGd2.findAll", query="SELECT g FROM GshmTinhtoanTmpGd2 g")
public class GshmTinhtoanTmpGd2 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ATM_GHICO")
	private BigDecimal atmGhico;

	@Column(name="ATM_GHINO")
	private BigDecimal atmGhino;

	@Column(name="BANK_ID")
	private BigDecimal bankId;

	@Column(name="CASHINOUT_GHICO")
	private BigDecimal cashinoutGhico;

	@Column(name="CASHINOUT_GHINO")
	private BigDecimal cashinoutGhino;

	@Column(name="CH_NEW_AMOUNT")
	private BigDecimal chNewAmount;

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

	private BigDecimal msgtype;

	private BigDecimal pcode;

	@Column(name="POS_GHICO")
	private BigDecimal posGhico;

	@Column(name="POS_GHINO")
	private BigDecimal posGhino;

	@Column(name="QRSW_GHICO")
	private BigDecimal qrswGhico;

	@Column(name="QRSW_GHINO")
	private BigDecimal qrswGhino;

	private BigDecimal respcode;

	@Column(name="SERVICE_CODE")
	private String serviceCode;

	private BigDecimal shcerror;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="START_BLOCK")
	private Date startBlock;

	@Column(name="USER_DEFINE")
	private String userDefine;

	public GshmTinhtoanTmpGd2() {
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

	public BigDecimal getChNewAmount() {
		return this.chNewAmount;
	}

	public void setChNewAmount(BigDecimal chNewAmount) {
		this.chNewAmount = chNewAmount;
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

	public BigDecimal getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(BigDecimal msgtype) {
		this.msgtype = msgtype;
	}

	public BigDecimal getPcode() {
		return this.pcode;
	}

	public void setPcode(BigDecimal pcode) {
		this.pcode = pcode;
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

	public BigDecimal getRespcode() {
		return this.respcode;
	}

	public void setRespcode(BigDecimal respcode) {
		this.respcode = respcode;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public BigDecimal getShcerror() {
		return this.shcerror;
	}

	public void setShcerror(BigDecimal shcerror) {
		this.shcerror = shcerror;
	}

	public Date getStartBlock() {
		return this.startBlock;
	}

	public void setStartBlock(Date startBlock) {
		this.startBlock = startBlock;
	}

	public String getUserDefine() {
		return this.userDefine;
	}

	public void setUserDefine(String userDefine) {
		this.userDefine = userDefine;
	}

}