package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TCK_PRINTDETAIL database table.
 * 
 */
@Entity
@Table(name="TCK_PRINTDETAIL")
@NamedQuery(name="TckPrintdetail.findAll", query="SELECT t FROM TckPrintdetail t")
public class TckPrintdetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private String mappingkey;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="PRINT_DATE")
	private Date printDate;

	@Column(name="PRINT_FILE")
	private String printFile;

	@Column(name="PRINT_SEC")
	private String printSec;

	@Column(name="PRINT_SEQ")
	private BigDecimal printSeq;

	@Column(name="PRINT_USER")
	private String printUser;

	public TckPrintdetail() {
	}

	public String getMappingkey() {
		return this.mappingkey;
	}

	public void setMappingkey(String mappingkey) {
		this.mappingkey = mappingkey;
	}

	public Date getPrintDate() {
		return this.printDate;
	}

	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}

	public String getPrintFile() {
		return this.printFile;
	}

	public void setPrintFile(String printFile) {
		this.printFile = printFile;
	}

	public String getPrintSec() {
		return this.printSec;
	}

	public void setPrintSec(String printSec) {
		this.printSec = printSec;
	}

	public BigDecimal getPrintSeq() {
		return this.printSeq;
	}

	public void setPrintSeq(BigDecimal printSeq) {
		this.printSeq = printSeq;
	}

	public String getPrintUser() {
		return this.printUser;
	}

	public void setPrintUser(String printUser) {
		this.printUser = printUser;
	}

}