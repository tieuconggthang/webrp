package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the HIS_ONLINE_API_ACCESS database table.
 * 
 */
@Entity
@Table(name="HIS_ONLINE_API_ACCESS")
@NamedQuery(name="HisOnlineApiAccess.findAll", query="SELECT h FROM HisOnlineApiAccess h")
public class HisOnlineApiAccess implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="ADD_INFO")
	private String addInfo;

	@Column(name="API_METHOD")
	private String apiMethod;

	@Column(name="API_PARAM")
	private String apiParam;

	@Column(name="API_URI")
	private String apiUri;

	@Column(name="CLIENT_TYPE")
	private String clientType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_ACCESS")
	private Date dateAccess;

	private BigInteger id;

	@Column(name="REMOTE_ADDR")
	private String remoteAddr;

	@Column(name="RESPONSE_BODY")
	private String responseBody;

	private String username;

	public HisOnlineApiAccess() {
	}

	public String getAddInfo() {
		return this.addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	public String getApiMethod() {
		return this.apiMethod;
	}

	public void setApiMethod(String apiMethod) {
		this.apiMethod = apiMethod;
	}

	public String getApiParam() {
		return this.apiParam;
	}

	public void setApiParam(String apiParam) {
		this.apiParam = apiParam;
	}

	public String getApiUri() {
		return this.apiUri;
	}

	public void setApiUri(String apiUri) {
		this.apiUri = apiUri;
	}

	public String getClientType() {
		return this.clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public Date getDateAccess() {
		return this.dateAccess;
	}

	public void setDateAccess(Date dateAccess) {
		this.dateAccess = dateAccess;
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getRemoteAddr() {
		return this.remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getResponseBody() {
		return this.responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}