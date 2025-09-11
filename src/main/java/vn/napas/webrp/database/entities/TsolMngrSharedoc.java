package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the TSOL_MNGR_SHAREDOC database table.
 * 
 */
@Entity
@Table(name="TSOL_MNGR_SHAREDOC")
@NamedQuery(name="TsolMngrSharedoc.findAll", query="SELECT t FROM TsolMngrSharedoc t")
public class TsolMngrSharedoc implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="DOC_NAME")
	private String docName;

	@Column(name="DOC_TITLE")
	private String docTitle;

	private String folder;

	private BigDecimal id;

	@Column(name="IN_SYSTEM")
	private String inSystem;

	private String password;

	@Column(name="SECURITY_LEVEL")
	private String securityLevel;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPLOAD_DATE")
	private Date uploadDate;

	@Column(name="UPLOAD_USER")
	private String uploadUser;

	public TsolMngrSharedoc() {
	}

	public String getDocName() {
		return this.docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocTitle() {
		return this.docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public String getFolder() {
		return this.folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getInSystem() {
		return this.inSystem;
	}

	public void setInSystem(String inSystem) {
		this.inSystem = inSystem;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecurityLevel() {
		return this.securityLevel;
	}

	public void setSecurityLevel(String securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Date getUploadDate() {
		return this.uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getUploadUser() {
		return this.uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

}