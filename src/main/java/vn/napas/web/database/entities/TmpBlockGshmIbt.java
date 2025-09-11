package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the TMP_BLOCK_GSHM_IBT database table.
 * 
 */
@Entity
@Table(name="TMP_BLOCK_GSHM_IBT")
@NamedQuery(name="TmpBlockGshmIbt.findAll", query="SELECT t FROM TmpBlockGshmIbt t")
public class TmpBlockGshmIbt implements Serializable {
	private static final long serialVersionUID = 1L;

	private String bankid;

	private BigInteger blockid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date fromdate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date todate;

	public TmpBlockGshmIbt() {
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

	public Date getFromdate() {
		return this.fromdate;
	}

	public void setFromdate(Date fromdate) {
		this.fromdate = fromdate;
	}

	public Date getTodate() {
		return this.todate;
	}

	public void setTodate(Date todate) {
		this.todate = todate;
	}

}