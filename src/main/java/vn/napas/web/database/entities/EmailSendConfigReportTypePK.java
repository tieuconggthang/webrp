package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The primary key class for the EMAIL_SEND_CONFIG_REPORT_TYPE database table.
 * 
 */
@Embeddable
public class EmailSendConfigReportTypePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CONFIG_ID", insertable=false, updatable=false)
	private long configId;

	@Column(name="REPORT_TYPE")
	private String reportType;

	public EmailSendConfigReportTypePK() {
	}
	public long getConfigId() {
		return this.configId;
	}
	public void setConfigId(long configId) {
		this.configId = configId;
	}
	public String getReportType() {
		return this.reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EmailSendConfigReportTypePK)) {
			return false;
		}
		EmailSendConfigReportTypePK castOther = (EmailSendConfigReportTypePK)other;
		return 
			(this.configId == castOther.configId)
			&& this.reportType.equals(castOther.reportType);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.configId ^ (this.configId >>> 32)));
		hash = hash * prime + this.reportType.hashCode();
		
		return hash;
	}
}