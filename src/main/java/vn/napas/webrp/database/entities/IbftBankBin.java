package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the IBFT_BANK_BINS database table.
 * 
 */
@Entity
@Table(name="IBFT_BANK_BINS")
@NamedQuery(name="IbftBankBin.findAll", query="SELECT i FROM IbftBankBin i")
public class IbftBankBin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="tidb_id")
	private long tidbId;

	private String bin;

	@Column(name="CARD_ID")
	private short cardId;

	@Column(name="MEMBER_ID")
	private String memberId;

	public IbftBankBin() {
	}

	public long getTidbId() {
		return this.tidbId;
	}

	public void setTidbId(long tidbId) {
		this.tidbId = tidbId;
	}

	public String getBin() {
		return this.bin;
	}

	public void setBin(String bin) {
		this.bin = bin;
	}

	public short getCardId() {
		return this.cardId;
	}

	public void setCardId(short cardId) {
		this.cardId = cardId;
	}

	public String getMemberId() {
		return this.memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

}