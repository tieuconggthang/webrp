package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the TGTT_20 database table.
 * 
 */
@Entity
@Table(name="TGTT_20")
@NamedQuery(name="Tgtt20.findAll", query="SELECT t FROM Tgtt20 t")
public class Tgtt20 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="tidb_id")
	private long tidbId;

	@Column(name="TGTT_ID")
	private String tgttId;

	@Column(name="TGTT_NAME")
	private String tgttName;

	@Column(name="TGTT_SHORT")
	private String tgttShort;

	public Tgtt20() {
	}

	public long getTidbId() {
		return this.tidbId;
	}

	public void setTidbId(long tidbId) {
		this.tidbId = tidbId;
	}

	public String getTgttId() {
		return this.tgttId;
	}

	public void setTgttId(String tgttId) {
		this.tgttId = tgttId;
	}

	public String getTgttName() {
		return this.tgttName;
	}

	public void setTgttName(String tgttName) {
		this.tgttName = tgttName;
	}

	public String getTgttShort() {
		return this.tgttShort;
	}

	public void setTgttShort(String tgttShort) {
		this.tgttShort = tgttShort;
	}

}