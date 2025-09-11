package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the MATS_IBT database table.
 * 
 */
@Entity
@Table(name="MATS_IBT")
@NamedQuery(name="MatsIbt.findAll", query="SELECT m FROM MatsIbt m")
public class MatsIbt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="TS_CODE")
	private BigDecimal tsCode;

	@Column(name="TS_MEANNING")
	private String tsMeanning;

	public MatsIbt() {
	}

	public BigDecimal getTsCode() {
		return this.tsCode;
	}

	public void setTsCode(BigDecimal tsCode) {
		this.tsCode = tsCode;
	}

	public String getTsMeanning() {
		return this.tsMeanning;
	}

	public void setTsMeanning(String tsMeanning) {
		this.tsMeanning = tsMeanning;
	}

}