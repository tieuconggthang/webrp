package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the BLOCK_DATA_NHTV_SWITCH database table.
 * 
 */
@Entity
@Table(name="BLOCK_DATA_NHTV_SWITCH")
@NamedQuery(name="BlockDataNhtvSwitch.findAll", query="SELECT b FROM BlockDataNhtvSwitch b")
public class BlockDataNhtvSwitch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ATM_GHICO")
	private BigDecimal atmGhico;

	@Column(name="ATM_GHINO")
	private BigDecimal atmGhino;

	@Column(name="BANK_CODE")
	private String bankCode;

	@Column(name="CASHINOUT_IBFT_GHICO")
	private BigDecimal cashinoutIbftGhico;

	@Column(name="CASHINOUT_IBFT_GHINO")
	private BigDecimal cashinoutIbftGhino;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FROM_DATE")
	private Date fromDate;

	private BigDecimal hmsd;

	@Column(name="HMSD_SESSION")
	private BigDecimal hmsdSession;

	@Column(name="HOTRO_CHUYENTIEN_GHICO")
	private BigDecimal hotroChuyentienGhico;

	@Column(name="HOTRO_CHUYENTIEN_GHINO")
	private BigDecimal hotroChuyentienGhino;

	@Column(name="IBFT_GHICO")
	private BigDecimal ibftGhico;

	@Column(name="IBFT_GHINO")
	private BigDecimal ibftGhino;

	@Column(name="IS_LAST_BLOCK_OF_DAY")
	private String isLastBlockOfDay;

	@Column(name="POS_GHICO")
	private BigDecimal posGhico;

	@Column(name="POS_GHINO")
	private BigDecimal posGhino;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="PREVIOUS_LAST_BLOCK_FROM_DATE")
	private Date previousLastBlockFromDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="PREVIOUS_LAST_BLOCK_TO_DATE")
	private Date previousLastBlockToDate;

	@Column(name="PREVIOUS_LAST_HMSD_SESSION")
	private BigDecimal previousLastHmsdSession;

	@Column(name="QRSWITCH_GHICO")
	private BigDecimal qrswitchGhico;

	@Column(name="QRSWITCH_GHINO")
	private BigDecimal qrswitchGhino;

	private BigDecimal status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TO_DATE")
	private Date toDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;

	public BlockDataNhtvSwitch() {
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

	public BigDecimal getCashinoutIbftGhico() {
		return this.cashinoutIbftGhico;
	}

	public void setCashinoutIbftGhico(BigDecimal cashinoutIbftGhico) {
		this.cashinoutIbftGhico = cashinoutIbftGhico;
	}

	public BigDecimal getCashinoutIbftGhino() {
		return this.cashinoutIbftGhino;
	}

	public void setCashinoutIbftGhino(BigDecimal cashinoutIbftGhino) {
		this.cashinoutIbftGhino = cashinoutIbftGhino;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getFromDate() {
		return this.fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public BigDecimal getHmsd() {
		return this.hmsd;
	}

	public void setHmsd(BigDecimal hmsd) {
		this.hmsd = hmsd;
	}

	public BigDecimal getHmsdSession() {
		return this.hmsdSession;
	}

	public void setHmsdSession(BigDecimal hmsdSession) {
		this.hmsdSession = hmsdSession;
	}

	public BigDecimal getHotroChuyentienGhico() {
		return this.hotroChuyentienGhico;
	}

	public void setHotroChuyentienGhico(BigDecimal hotroChuyentienGhico) {
		this.hotroChuyentienGhico = hotroChuyentienGhico;
	}

	public BigDecimal getHotroChuyentienGhino() {
		return this.hotroChuyentienGhino;
	}

	public void setHotroChuyentienGhino(BigDecimal hotroChuyentienGhino) {
		this.hotroChuyentienGhino = hotroChuyentienGhino;
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

	public String getIsLastBlockOfDay() {
		return this.isLastBlockOfDay;
	}

	public void setIsLastBlockOfDay(String isLastBlockOfDay) {
		this.isLastBlockOfDay = isLastBlockOfDay;
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

	public Date getPreviousLastBlockFromDate() {
		return this.previousLastBlockFromDate;
	}

	public void setPreviousLastBlockFromDate(Date previousLastBlockFromDate) {
		this.previousLastBlockFromDate = previousLastBlockFromDate;
	}

	public Date getPreviousLastBlockToDate() {
		return this.previousLastBlockToDate;
	}

	public void setPreviousLastBlockToDate(Date previousLastBlockToDate) {
		this.previousLastBlockToDate = previousLastBlockToDate;
	}

	public BigDecimal getPreviousLastHmsdSession() {
		return this.previousLastHmsdSession;
	}

	public void setPreviousLastHmsdSession(BigDecimal previousLastHmsdSession) {
		this.previousLastHmsdSession = previousLastHmsdSession;
	}

	public BigDecimal getQrswitchGhico() {
		return this.qrswitchGhico;
	}

	public void setQrswitchGhico(BigDecimal qrswitchGhico) {
		this.qrswitchGhico = qrswitchGhico;
	}

	public BigDecimal getQrswitchGhino() {
		return this.qrswitchGhino;
	}

	public void setQrswitchGhino(BigDecimal qrswitchGhino) {
		this.qrswitchGhino = qrswitchGhino;
	}

	public BigDecimal getStatus() {
		return this.status;
	}

	public void setStatus(BigDecimal status) {
		this.status = status;
	}

	public Date getToDate() {
		return this.toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}