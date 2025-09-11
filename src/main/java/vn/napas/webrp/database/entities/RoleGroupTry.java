package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the ROLE_GROUP_TRY database table.
 * 
 */
@Entity
@Table(name="ROLE_GROUP_TRY")
@NamedQuery(name="RoleGroupTry.findAll", query="SELECT r FROM RoleGroupTry r")
public class RoleGroupTry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="BANK_ROLE")
	private String bankRole;

	private String description;

	@Column(name="FUNCT_ROLE")
	private String functRole;

	@Column(name="IN_SYSTEM")
	private String inSystem;

	@Column(name="PAGE_ACCESS")
	private String pageAccess;

	private String rolename;

	@Column(name="STAFF_ROLE")
	private String staffRole;

	public RoleGroupTry() {
	}

	public String getBankRole() {
		return this.bankRole;
	}

	public void setBankRole(String bankRole) {
		this.bankRole = bankRole;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFunctRole() {
		return this.functRole;
	}

	public void setFunctRole(String functRole) {
		this.functRole = functRole;
	}

	public String getInSystem() {
		return this.inSystem;
	}

	public void setInSystem(String inSystem) {
		this.inSystem = inSystem;
	}

	public String getPageAccess() {
		return this.pageAccess;
	}

	public void setPageAccess(String pageAccess) {
		this.pageAccess = pageAccess;
	}

	public String getRolename() {
		return this.rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getStaffRole() {
		return this.staffRole;
	}

	public void setStaffRole(String staffRole) {
		this.staffRole = staffRole;
	}

}