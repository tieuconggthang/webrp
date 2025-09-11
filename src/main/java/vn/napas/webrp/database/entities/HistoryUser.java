package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the HISTORY_USER database table.
 * 
 */
@Entity
@Table(name="HISTORY_USER")
@NamedQuery(name="HistoryUser.findAll", query="SELECT h FROM HistoryUser h")
public class HistoryUser implements Serializable {
	private static final long serialVersionUID = 1L;

	private String content;

	private String function;

	private String result;

	@Temporal(TemporalType.TIMESTAMP)
	private Date sysdatetime;

	private String systime;

	private String url;

	@Column(name="USER_INPUT")
	private String userInput;

	private String userlogin;

	public HistoryUser() {
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFunction() {
		return this.function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getResult() {
		return this.result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Date getSysdatetime() {
		return this.sysdatetime;
	}

	public void setSysdatetime(Date sysdatetime) {
		this.sysdatetime = sysdatetime;
	}

	public String getSystime() {
		return this.systime;
	}

	public void setSystime(String systime) {
		this.systime = systime;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserInput() {
		return this.userInput;
	}

	public void setUserInput(String userInput) {
		this.userInput = userInput;
	}

	public String getUserlogin() {
		return this.userlogin;
	}

	public void setUserlogin(String userlogin) {
		this.userlogin = userlogin;
	}

}