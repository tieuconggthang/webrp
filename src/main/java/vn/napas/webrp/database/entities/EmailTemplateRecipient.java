package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the EMAIL_TEMPLATE_RECIPIENT database table.
 * 
 */
@Entity
@Table(name="EMAIL_TEMPLATE_RECIPIENT")
@NamedQuery(name="EmailTemplateRecipient.findAll", query="SELECT e FROM EmailTemplateRecipient e")
public class EmailTemplateRecipient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="EMAIL_ADDR")
	private String emailAddr;

	@Column(name="EMAIL_TYPE")
	private String emailType;

	//bi-directional many-to-one association to EmailTemplate
	@ManyToOne
	@JoinColumn(name="TEMPLATE_ID")
	private EmailTemplate emailTemplate;

	public EmailTemplateRecipient() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmailAddr() {
		return this.emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getEmailType() {
		return this.emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public EmailTemplate getEmailTemplate() {
		return this.emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

}