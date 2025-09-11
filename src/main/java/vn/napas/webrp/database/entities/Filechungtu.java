package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the FILECHUNGTU database table.
 * 
 */
@Entity
@NamedQuery(name="Filechungtu.findAll", query="SELECT f FROM Filechungtu f")
public class Filechungtu implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal bankid;

	@Temporal(TemporalType.TIMESTAMP)
	private Date datecreate;

	private String filename;

	private BigDecimal stt;

	@Column(name="STT_GDTS")
	private BigDecimal sttGdts;

	private String typefile;

	private String usercreate;

	public Filechungtu() {
	}

	public BigDecimal getBankid() {
		return this.bankid;
	}

	public void setBankid(BigDecimal bankid) {
		this.bankid = bankid;
	}

	public Date getDatecreate() {
		return this.datecreate;
	}

	public void setDatecreate(Date datecreate) {
		this.datecreate = datecreate;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public BigDecimal getSttGdts() {
		return this.sttGdts;
	}

	public void setSttGdts(BigDecimal sttGdts) {
		this.sttGdts = sttGdts;
	}

	public String getTypefile() {
		return this.typefile;
	}

	public void setTypefile(String typefile) {
		this.typefile = typefile;
	}

	public String getUsercreate() {
		return this.usercreate;
	}

	public void setUsercreate(String usercreate) {
		this.usercreate = usercreate;
	}

}