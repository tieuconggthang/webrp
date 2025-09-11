package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the TBLTRANG_THAI_GSHM database table.
 * 
 */
@Entity
@Table(name="TBLTRANG_THAI_GSHM")
@NamedQuery(name="TbltrangThaiGshm.findAll", query="SELECT t FROM TbltrangThaiGshm t")
public class TbltrangThaiGshm implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigInteger blockid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endtimesum;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endtimetoecom;

	@Column(name="SERVICE_CODE")
	private String serviceCode;

	@Temporal(TemporalType.TIMESTAMP)
	private Date starttime;

	private String status;

	public TbltrangThaiGshm() {
	}

	public BigInteger getBlockid() {
		return this.blockid;
	}

	public void setBlockid(BigInteger blockid) {
		this.blockid = blockid;
	}

	public Date getEndtimesum() {
		return this.endtimesum;
	}

	public void setEndtimesum(Date endtimesum) {
		this.endtimesum = endtimesum;
	}

	public Date getEndtimetoecom() {
		return this.endtimetoecom;
	}

	public void setEndtimetoecom(Date endtimetoecom) {
		this.endtimetoecom = endtimetoecom;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public Date getStarttime() {
		return this.starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}