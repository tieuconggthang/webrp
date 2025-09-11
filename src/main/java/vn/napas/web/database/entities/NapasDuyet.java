package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the NAPAS_DUYET database table.
 * 
 */
@Entity
@Table(name="NAPAS_DUYET")
@NamedQuery(name="NapasDuyet.findAll", query="SELECT n FROM NapasDuyet n")
public class NapasDuyet implements Serializable {
	private static final long serialVersionUID = 1L;

	private String funct;

	@Column(name="IN_SYSTEM")
	private String inSystem;

	@Column(name="KSV_APPROVE")
	private String ksvApprove;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="KSV_DATE")
	private Date ksvDate;

	@Column(name="KSV_NOTE")
	private String ksvNote;

	@Column(name="KSV_STATUS")
	private BigDecimal ksvStatus;

	@Column(name="LOG_ID")
	private BigDecimal logId;

	private BigDecimal stt;

	@Column(name="TSV_APPROVE")
	private String tsvApprove;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TSV_DATE")
	private Date tsvDate;

	@Column(name="TSV_NOTE")
	private String tsvNote;

	@Column(name="TSV_STATUS")
	private BigDecimal tsvStatus;

	public NapasDuyet() {
	}

	public String getFunct() {
		return this.funct;
	}

	public void setFunct(String funct) {
		this.funct = funct;
	}

	public String getInSystem() {
		return this.inSystem;
	}

	public void setInSystem(String inSystem) {
		this.inSystem = inSystem;
	}

	public String getKsvApprove() {
		return this.ksvApprove;
	}

	public void setKsvApprove(String ksvApprove) {
		this.ksvApprove = ksvApprove;
	}

	public Date getKsvDate() {
		return this.ksvDate;
	}

	public void setKsvDate(Date ksvDate) {
		this.ksvDate = ksvDate;
	}

	public String getKsvNote() {
		return this.ksvNote;
	}

	public void setKsvNote(String ksvNote) {
		this.ksvNote = ksvNote;
	}

	public BigDecimal getKsvStatus() {
		return this.ksvStatus;
	}

	public void setKsvStatus(BigDecimal ksvStatus) {
		this.ksvStatus = ksvStatus;
	}

	public BigDecimal getLogId() {
		return this.logId;
	}

	public void setLogId(BigDecimal logId) {
		this.logId = logId;
	}

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public String getTsvApprove() {
		return this.tsvApprove;
	}

	public void setTsvApprove(String tsvApprove) {
		this.tsvApprove = tsvApprove;
	}

	public Date getTsvDate() {
		return this.tsvDate;
	}

	public void setTsvDate(Date tsvDate) {
		this.tsvDate = tsvDate;
	}

	public String getTsvNote() {
		return this.tsvNote;
	}

	public void setTsvNote(String tsvNote) {
		this.tsvNote = tsvNote;
	}

	public BigDecimal getTsvStatus() {
		return this.tsvStatus;
	}

	public void setTsvStatus(BigDecimal tsvStatus) {
		this.tsvStatus = tsvStatus;
	}

}