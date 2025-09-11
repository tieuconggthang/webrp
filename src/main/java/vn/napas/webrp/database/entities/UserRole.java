package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the USER_ROLE database table.
 * 
 */
@Entity
@Table(name="USER_ROLE")
@NamedQuery(name="UserRole.findAll", query="SELECT u FROM UserRole u")
public class UserRole implements Serializable {
	private static final long serialVersionUID = 1L;

	private String rolename;

	private String userid;

	public UserRole() {
	}

	public String getRolename() {
		return this.rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}