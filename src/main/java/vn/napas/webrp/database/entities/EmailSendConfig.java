package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the EMAIL_SEND_CONFIG database table.
 * 
 */
@Entity
@Table(name="EMAIL_SEND_CONFIG")
@NamedQuery(name="EmailSendConfig.findAll", query="SELECT e FROM EmailSendConfig e")
public class EmailSendConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CONFIG_ID")
	private long configId;

	private String active;

	@Column(name="CONFIG_NAME")
	private String configName;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_AT")
	private Date createdAt;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CRON_EXPRESSION")
	private String cronExpression;

	@Column(name="REPORT_PERIOD")
	private String reportPeriod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_AT")
	private Date updatedAt;

	@Column(name="UPDATED_BY")
	private String updatedBy;

	//bi-directional many-to-one association to EmailTemplate
	@ManyToOne
	@JoinColumn(name="MAIL_TEMPLATE_ID")
	private EmailTemplate emailTemplate;

	//bi-directional many-to-one association to EmailSendConfigReportType
	@OneToMany(mappedBy="emailSendConfig")
	private List<EmailSendConfigReportType> emailSendConfigReportTypes;

	public EmailSendConfig() {
	}

	public long getConfigId() {
		return this.configId;
	}

	public void setConfigId(long configId) {
		this.configId = configId;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getConfigName() {
		return this.configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
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

	public String getCronExpression() {
		return this.cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getReportPeriod() {
		return this.reportPeriod;
	}

	public void setReportPeriod(String reportPeriod) {
		this.reportPeriod = reportPeriod;
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

	public EmailTemplate getEmailTemplate() {
		return this.emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public List<EmailSendConfigReportType> getEmailSendConfigReportTypes() {
		return this.emailSendConfigReportTypes;
	}

	public void setEmailSendConfigReportTypes(List<EmailSendConfigReportType> emailSendConfigReportTypes) {
		this.emailSendConfigReportTypes = emailSendConfigReportTypes;
	}

	public EmailSendConfigReportType addEmailSendConfigReportType(EmailSendConfigReportType emailSendConfigReportType) {
		getEmailSendConfigReportTypes().add(emailSendConfigReportType);
		emailSendConfigReportType.setEmailSendConfig(this);

		return emailSendConfigReportType;
	}

	public EmailSendConfigReportType removeEmailSendConfigReportType(EmailSendConfigReportType emailSendConfigReportType) {
		getEmailSendConfigReportTypes().remove(emailSendConfigReportType);
		emailSendConfigReportType.setEmailSendConfig(null);

		return emailSendConfigReportType;
	}

}