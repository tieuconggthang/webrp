package vn.napas.web.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the SHCEXTBINDB database table.
 * 
 */
@Entity
@NamedQuery(name="Shcextbindb.findAll", query="SELECT s FROM Shcextbindb s")
public class Shcextbindb implements Serializable {
	private static final long serialVersionUID = 1L;

	private String cardproduct;

	private String description;

	private String destination;

	@Column(name="ENTITY_ID")
	private String entityId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FILE_DATE")
	private Date fileDate;

	@Column(name="FILE_NAME")
	private String fileName;

	@Column(name="FILE_VERSION")
	private String fileVersion;

	private String highbin;

	private String lowbin;

	@Column(name="NETWORK_DATA")
	private String networkData;

	@Column(name="O_LEVEL")
	private BigDecimal oLevel;

	private String status;

	public Shcextbindb() {
	}

	public String getCardproduct() {
		return this.cardproduct;
	}

	public void setCardproduct(String cardproduct) {
		this.cardproduct = cardproduct;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDestination() {
		return this.destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public Date getFileDate() {
		return this.fileDate;
	}

	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileVersion() {
		return this.fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getHighbin() {
		return this.highbin;
	}

	public void setHighbin(String highbin) {
		this.highbin = highbin;
	}

	public String getLowbin() {
		return this.lowbin;
	}

	public void setLowbin(String lowbin) {
		this.lowbin = lowbin;
	}

	public String getNetworkData() {
		return this.networkData;
	}

	public void setNetworkData(String networkData) {
		this.networkData = networkData;
	}

	public BigDecimal getOLevel() {
		return this.oLevel;
	}

	public void setOLevel(BigDecimal oLevel) {
		this.oLevel = oLevel;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}