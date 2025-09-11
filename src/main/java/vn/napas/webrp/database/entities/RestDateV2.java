package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the REST_DATE_V2 database table.
 * 
 */
@Entity
@Table(name="REST_DATE_V2")
@NamedQuery(name="RestDateV2.findAll", query="SELECT r FROM RestDateV2 r")
public class RestDateV2 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="AW_DATE")
	private Date awDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="L_DATE")
	private Date lDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="S_DATE")
	private Date sDate;

	private BigDecimal stt;

	private BigDecimal yer;

	public RestDateV2() {
	}

	public Date getAwDate() {
		return this.awDate;
	}

	public void setAwDate(Date awDate) {
		this.awDate = awDate;
	}

	public Date getLDate() {
		return this.lDate;
	}

	public void setLDate(Date lDate) {
		this.lDate = lDate;
	}

	public Date getSDate() {
		return this.sDate;
	}

	public void setSDate(Date sDate) {
		this.sDate = sDate;
	}

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public BigDecimal getYer() {
		return this.yer;
	}

	public void setYer(BigDecimal yer) {
		this.yer = yer;
	}

}