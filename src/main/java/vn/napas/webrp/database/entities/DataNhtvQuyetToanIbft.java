package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the DATA_NHTV_QUYET_TOAN_IBFT database table.
 * 
 */
@Entity
@Table(name="DATA_NHTV_QUYET_TOAN_IBFT")
@NamedQuery(name="DataNhtvQuyetToanIbft.findAll", query="SELECT d FROM DataNhtvQuyetToanIbft d")
public class DataNhtvQuyetToanIbft implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="BANK_CODE")
	private String bankCode;

	@Column(name="CASHOUT_IBFT_GHICO")
	private BigDecimal cashoutIbftGhico;

	@Column(name="CASHOUT_IBFT_GHINO")
	private BigDecimal cashoutIbftGhino;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FROM_DATE")
	private Date fromDate;

	private BigDecimal hmsd;

	@Column(name="HOTRO_CHUYENTIEN_GHICO")
	private BigDecimal hotroChuyentienGhico;

	@Column(name="HOTRO_CHUYENTIEN_GHINO")
	private BigDecimal hotroChuyentienGhino;

	@Column(name="IBFT_GHICO")
	private BigDecimal ibftGhico;

	@Column(name="IBFT_GHINO")
	private BigDecimal ibftGhino;

	@Column(name="QRSWITCH_GHICO")
	private BigDecimal qrswitchGhico;

	@Column(name="QRSWITCH_GHINO")
	private BigDecimal qrswitchGhino;

	@Column(name="SESSION_NUM")
	private BigDecimal sessionNum;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SETTLEMENT_DATE")
	private Date settlementDate;

	@Column(name="SETTLEMENT_STATUS")
	private String settlementStatus;

	private BigDecimal status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TO_DATE")
	private Date toDate;

	@Column(name="TRANS_TYPE")
	private String transType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;

	public DataNhtvQuyetToanIbft() {
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public BigDecimal getCashoutIbftGhico() {
		return this.cashoutIbftGhico;
	}

	public void setCashoutIbftGhico(BigDecimal cashoutIbftGhico) {
		this.cashoutIbftGhico = cashoutIbftGhico;
	}

	public BigDecimal getCashoutIbftGhino() {
		return this.cashoutIbftGhino;
	}

	public void setCashoutIbftGhino(BigDecimal cashoutIbftGhino) {
		this.cashoutIbftGhino = cashoutIbftGhino;
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

	public BigDecimal getSessionNum() {
		return this.sessionNum;
	}

	public void setSessionNum(BigDecimal sessionNum) {
		this.sessionNum = sessionNum;
	}

	public Date getSettlementDate() {
		return this.settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getSettlementStatus() {
		return this.settlementStatus;
	}

	public void setSettlementStatus(String settlementStatus) {
		this.settlementStatus = settlementStatus;
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

	public String getTransType() {
		return this.transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

}