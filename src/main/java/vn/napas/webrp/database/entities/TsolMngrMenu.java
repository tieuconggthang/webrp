package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the TSOL_MNGR_MENU database table.
 * 
 */
@Entity
@Table(name="TSOL_MNGR_MENU")
@NamedQuery(name="TsolMngrMenu.findAll", query="SELECT t FROM TsolMngrMenu t")
public class TsolMngrMenu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="FUNCT_ROLE")
	private String functRole;

	@Column(name="LIST_BANK_TYPE")
	private String listBankType;

	@Column(name="LIST_CHANEL_ID")
	private String listChanelId;

	@Column(name="LIST_USER_TYPE")
	private String listUserType;

	@Column(name="MENU_ID")
	private BigDecimal menuId;

	@Column(name="MENU_NAME")
	private String menuName;

	@Column(name="MENU_PARENTID")
	private BigDecimal menuParentid;

	@Column(name="MENU_URL")
	private String menuUrl;

	private String rolename;

	public TsolMngrMenu() {
	}

	public String getFunctRole() {
		return this.functRole;
	}

	public void setFunctRole(String functRole) {
		this.functRole = functRole;
	}

	public String getListBankType() {
		return this.listBankType;
	}

	public void setListBankType(String listBankType) {
		this.listBankType = listBankType;
	}

	public String getListChanelId() {
		return this.listChanelId;
	}

	public void setListChanelId(String listChanelId) {
		this.listChanelId = listChanelId;
	}

	public String getListUserType() {
		return this.listUserType;
	}

	public void setListUserType(String listUserType) {
		this.listUserType = listUserType;
	}

	public BigDecimal getMenuId() {
		return this.menuId;
	}

	public void setMenuId(BigDecimal menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return this.menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public BigDecimal getMenuParentid() {
		return this.menuParentid;
	}

	public void setMenuParentid(BigDecimal menuParentid) {
		this.menuParentid = menuParentid;
	}

	public String getMenuUrl() {
		return this.menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getRolename() {
		return this.rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

}