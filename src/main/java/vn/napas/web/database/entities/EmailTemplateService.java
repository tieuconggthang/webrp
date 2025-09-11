package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the EMAIL_TEMPLATE_SERVICE database table.
 * 
 */
@Entity
@Table(name="EMAIL_TEMPLATE_SERVICE")
@NamedQuery(name="EmailTemplateService.findAll", query="SELECT e FROM EmailTemplateService e")
public class EmailTemplateService implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	@Column(name="SERVICE_CODE")
	private String serviceCode;

	//bi-directional many-to-one association to EmailTemplate
	@ManyToOne
	@JoinColumn(name="TEMPLATE_ID")
	private EmailTemplate emailTemplate;

	public EmailTemplateService() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public EmailTemplate getEmailTemplate() {
		return this.emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

}