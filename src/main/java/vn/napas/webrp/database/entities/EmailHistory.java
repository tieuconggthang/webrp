package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the EMAIL_HISTORY database table.
 * 
 */
@Entity
@Table(name="EMAIL_HISTORY")
@NamedQuery(name="EmailHistory.findAll", query="SELECT e FROM EmailHistory e")
public class EmailHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="HISTORY_ID")
	private long historyId;

	@Lob
	private String body;

	@Column(name="CC_EMAIL")
	private String ccEmail;

	@Column(name="ERROR_MESSAGE")
	private String errorMessage;

	@Column(name="SEND_STATUS")
	private String sendStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SENT_AT")
	private Date sentAt;

	@Column(name="SENT_BY")
	private String sentBy;

	private String subject;

	@Column(name="TEMPLATE_CODE")
	private String templateCode;

	@Column(name="TO_EMAIL")
	private String toEmail;

	public EmailHistory() {
	}

	public long getHistoryId() {
		return this.historyId;
	}

	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCcEmail() {
		return this.ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSendStatus() {
		return this.sendStatus;
	}

	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}

	public Date getSentAt() {
		return this.sentAt;
	}

	public void setSentAt(Date sentAt) {
		this.sentAt = sentAt;
	}

	public String getSentBy() {
		return this.sentBy;
	}

	public void setSentBy(String sentBy) {
		this.sentBy = sentBy;
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

	public String getToEmail() {
		return this.toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

}