package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the THAOHP database table.
 * 
 */
@Entity
@NamedQuery(name="Thaohp.findAll", query="SELECT t FROM Thaohp t")
public class Thaohp implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal check1;

	public Thaohp() {
	}

	public BigDecimal getCheck1() {
		return this.check1;
	}

	public void setCheck1(BigDecimal check1) {
		this.check1 = check1;
	}

}