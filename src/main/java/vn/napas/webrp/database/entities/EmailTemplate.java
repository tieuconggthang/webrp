package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the EMAIL_TEMPLATE database table.
 * 
 */
@Entity
@Table(name="EMAIL_TEMPLATE")
@NamedQuery(name="EmailTemplate.findAll", query="SELECT e FROM EmailTemplate e")
public class EmailTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TEMPLATE_ID")
	private long templateId;

	@Lob
	private String body;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_AT")
	private Date createdAt;

	@Column(name="CREATED_BY")
	private String createdBy;

	private String description;

	private String direction;

	@Column(name="ORG_CODE")
	private String orgCode;

	@Column(name="ORG_NAME")
	private String orgName;

	private String subject;

	@Column(name="TEMPLATE_CODE")
	private String templateCode;

	@Column(name="TEMPLATE_NAME")
	private String templateName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_AT")
	private Date updatedAt;

	@Column(name="UPDATED_BY")
	private String updatedBy;

	//bi-directional many-to-one association to EmailSendConfig
	@OneToMany(mappedBy="emailTemplate")
	private List<EmailSendConfig> emailSendConfigs;

	//bi-directional many-to-one association to EmailTemplateRecipient
	@OneToMany(mappedBy="emailTemplate")
	private List<EmailTemplateRecipient> emailTemplateRecipients;

	//bi-directional many-to-one association to EmailTemplateService
	@OneToMany(mappedBy="emailTemplate")
	private List<EmailTemplateService> emailTemplateServices;

	public EmailTemplate() {
	}

	public long getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDirection() {
		return this.direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplateCode() {
		return this.templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public List<EmailSendConfig> getEmailSendConfigs() {
		return this.emailSendConfigs;
	}

	public void setEmailSendConfigs(List<EmailSendConfig> emailSendConfigs) {
		this.emailSendConfigs = emailSendConfigs;
	}

	public EmailSendConfig addEmailSendConfig(EmailSendConfig emailSendConfig) {
		getEmailSendConfigs().add(emailSendConfig);
		emailSendConfig.setEmailTemplate(this);

		return emailSendConfig;
	}

	public EmailSendConfig removeEmailSendConfig(EmailSendConfig emailSendConfig) {
		getEmailSendConfigs().remove(emailSendConfig);
		emailSendConfig.setEmailTemplate(null);

		return emailSendConfig;
	}

	public List<EmailTemplateRecipient> getEmailTemplateRecipients() {
		return this.emailTemplateRecipients;
	}

	public void setEmailTemplateRecipients(List<EmailTemplateRecipient> emailTemplateRecipients) {
		this.emailTemplateRecipients = emailTemplateRecipients;
	}

	public EmailTemplateRecipient addEmailTemplateRecipient(EmailTemplateRecipient emailTemplateRecipient) {
		getEmailTemplateRecipients().add(emailTemplateRecipient);
		emailTemplateRecipient.setEmailTemplate(this);

		return emailTemplateRecipient;
	}

	public EmailTemplateRecipient removeEmailTemplateRecipient(EmailTemplateRecipient emailTemplateRecipient) {
		getEmailTemplateRecipients().remove(emailTemplateRecipient);
		emailTemplateRecipient.setEmailTemplate(null);

		return emailTemplateRecipient;
	}

	public List<EmailTemplateService> getEmailTemplateServices() {
		return this.emailTemplateServices;
	}

	public void setEmailTemplateServices(List<EmailTemplateService> emailTemplateServices) {
		this.emailTemplateServices = emailTemplateServices;
	}

	public EmailTemplateService addEmailTemplateService(EmailTemplateService emailTemplateService) {
		getEmailTemplateServices().add(emailTemplateService);
		emailTemplateService.setEmailTemplate(this);

		return emailTemplateService;
	}

	public EmailTemplateService removeEmailTemplateService(EmailTemplateService emailTemplateService) {
		getEmailTemplateServices().remove(emailTemplateService);
		emailTemplateService.setEmailTemplate(null);

		return emailTemplateService;
	}

}