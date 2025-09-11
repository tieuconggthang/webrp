package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the BANK_BLACKLIST database table.
 * 
 */
@Entity
@Table(name="BANK_BLACKLIST")
@NamedQuery(name="BankBlacklist.findAll", query="SELECT b FROM BankBlacklist b")
public class BankBlacklist implements Serializable {
	private static final long serialVersionUID = 1L;

	private String bankid;

	private String bankname;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_INSERT_FILE")
	private Date dateInsertFile;

	public BankBlacklist() {
	}

	public String getBankid() {
		return this.bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}

	public String getBankname() {
		return this.bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public Date getDateInsertFile() {
		return this.dateInsertFile;
	}

	public void setDateInsertFile(Date dateInsertFile) {
		this.dateInsertFile = dateInsertFile;
	}

}