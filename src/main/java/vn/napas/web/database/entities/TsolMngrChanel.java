package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the TSOL_MNGR_CHANEL database table.
 * 
 */
@Entity
@Table(name="TSOL_MNGR_CHANEL")
@NamedQuery(name="TsolMngrChanel.findAll", query="SELECT t FROM TsolMngrChanel t")
public class TsolMngrChanel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String active;

	@Column(name="`DESCRIBE`")
	private String describe;

	private BigDecimal id;

	private String shortname;

	public TsolMngrChanel() {
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getDescribe() {
		return this.describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getShortname() {
		return this.shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

}