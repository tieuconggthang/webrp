package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the EMAIL_SEND_CONFIG_REPORT_TYPE database table.
 * 
 */
@Entity
@Table(name="EMAIL_SEND_CONFIG_REPORT_TYPE")
@NamedQuery(name="EmailSendConfigReportType.findAll", query="SELECT e FROM EmailSendConfigReportType e")
public class EmailSendConfigReportType implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private EmailSendConfigReportTypePK id;

	//bi-directional many-to-one association to EmailSendConfig
	@ManyToOne
	@JoinColumn(name="CONFIG_ID")
	private EmailSendConfig emailSendConfig;

	public EmailSendConfigReportType() {
	}

	public EmailSendConfigReportTypePK getId() {
		return this.id;
	}

	public void setId(EmailSendConfigReportTypePK id) {
		this.id = id;
	}

	public EmailSendConfig getEmailSendConfig() {
		return this.emailSendConfig;
	}

	public void setEmailSendConfig(EmailSendConfig emailSendConfig) {
		this.emailSendConfig = emailSendConfig;
	}

}