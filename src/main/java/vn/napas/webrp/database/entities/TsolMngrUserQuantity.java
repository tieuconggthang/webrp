package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the TSOL_MNGR_USER_QUANTITY database table.
 * 
 */
@Entity
@Table(name="TSOL_MNGR_USER_QUANTITY")
@NamedQuery(name="TsolMngrUserQuantity.findAll", query="SELECT t FROM TsolMngrUserQuantity t")
public class TsolMngrUserQuantity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ID_CHANEL")
	private BigDecimal idChanel;

	private BigDecimal insid;

	private BigDecimal quantity;

	public TsolMngrUserQuantity() {
	}

	public BigDecimal getIdChanel() {
		return this.idChanel;
	}

	public void setIdChanel(BigDecimal idChanel) {
		this.idChanel = idChanel;
	}

	public BigDecimal getInsid() {
		return this.insid;
	}

	public void setInsid(BigDecimal insid) {
		this.insid = insid;
	}

	public BigDecimal getQuantity() {
		return this.quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

}