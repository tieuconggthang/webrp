package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the CHECK_ERROR database table.
 * 
 */
@Entity
@Table(name="CHECK_ERROR")
@NamedQuery(name="CheckError.findAll", query="SELECT c FROM CheckError c")
public class CheckError implements Serializable {
	private static final long serialVersionUID = 1L;

	private String content;

	@Column(name="PROC_NAME")
	private String procName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date tgian;

	public CheckError() {
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getProcName() {
		return this.procName;
	}

	public void setProcName(String procName) {
		this.procName = procName;
	}

	public Date getTgian() {
		return this.tgian;
	}

	public void setTgian(Date tgian) {
		this.tgian = tgian;
	}

}