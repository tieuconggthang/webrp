package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the CHECKED database table.
 * 
 */
@Entity
@NamedQuery(name="Checked.findAll", query="SELECT c FROM Checked c")
public class Checked implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal del;

	private BigDecimal re;

	private String str;

	public Checked() {
	}

	public BigDecimal getDel() {
		return this.del;
	}

	public void setDel(BigDecimal del) {
		this.del = del;
	}

	public BigDecimal getRe() {
		return this.re;
	}

	public void setRe(BigDecimal re) {
		this.re = re;
	}

	public String getStr() {
		return this.str;
	}

	public void setStr(String str) {
		this.str = str;
	}

}