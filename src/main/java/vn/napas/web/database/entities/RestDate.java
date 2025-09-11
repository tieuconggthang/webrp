package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the REST_DATE database table.
 * 
 */
@Entity
@Table(name="REST_DATE")
@NamedQuery(name="RestDate.findAll", query="SELECT r FROM RestDate r")
public class RestDate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ADDITIONAL_WD")
	private Date additionalWd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="L_DATE")
	private Date lDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="S_DATE")
	private Date sDate;

	private BigDecimal stt;

	private BigDecimal yer;

	public RestDate() {
	}

	public Date getAdditionalWd() {
		return this.additionalWd;
	}

	public void setAdditionalWd(Date additionalWd) {
		this.additionalWd = additionalWd;
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