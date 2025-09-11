package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the EMAIL_WARMING_BANK database table.
 * 
 */
@Entity
@Table(name="EMAIL_WARMING_BANK")
@NamedQuery(name="EmailWarmingBank.findAll", query="SELECT e FROM EmailWarmingBank e")
public class EmailWarmingBank implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="BANK_ID")
	private BigDecimal bankId;

	private String email;

	private BigDecimal stt;

	@Column(name="TYPE_WARMING")
	private String typeWarming;

	public EmailWarmingBank() {
	}

	public BigDecimal getBankId() {
		return this.bankId;
	}

	public void setBankId(BigDecimal bankId) {
		this.bankId = bankId;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
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