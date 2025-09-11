package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the EMAIL_WARMING_BANK2 database table.
 * 
 */
@Entity
@Table(name="EMAIL_WARMING_BANK2")
@NamedQuery(name="EmailWarmingBank2.findAll", query="SELECT e FROM EmailWarmingBank2 e")
public class EmailWarmingBank2 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="BANK_ID")
	private BigDecimal bankId;

	@Column(name="BANK_NAME")
	private String bankName;

	private String email;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="SEND_TIME")
	private Date sendTime;

	private BigDecimal stt;

	@Column(name="TYPE_WARMING")
	private String typeWarming;

	public EmailWarmingBank2() {
	}

	public BigDecimal getBankId() {
		return this.bankId;
	}

	public void setBankId(BigDecimal bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public BigDecimal getStt() {
		return this.stt;
	}

	public void setStt(BigDecimal stt) {
		this.stt = stt;
	}

	public String getTypeWarming() {
		return this.typeWarming;
	}

	public void setTypeWarming(String typeWarming) {
		this.typeWarming = typeWarming;
	}

}