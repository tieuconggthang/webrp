package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;


/**
 * The persistent class for the CFG_SYSTEM_PARAM database table.
 * 
 */
@Entity
@Table(name="CFG_SYSTEM_PARAM")
@NamedQuery(name="CfgSystemParam.findAll", query="SELECT c FROM CfgSystemParam c")
public class CfgSystemParam implements Serializable {
	private static final long serialVersionUID = 1L;

	private String description;

	private int enabled;

	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="MODIF_DATE")
	private Date modifDate;

	private String name;

	@Column(name="PARAM_GROUP")
	private String paramGroup;

	@Column(name="PARAM_ORDER")
	private int paramOrder;

	@Column(name="PARAM_TITLE")
	private String paramTitle;

	private String val;

	@Column(name="VAL_INPUT_TYPE")
	private String valInputType;

	@Column(name="VAL_LIST")
	private String valList;

	@Column(name="VAL_MAX")
	private int valMax;

	@Column(name="VAL_MIN")
	private int valMin;

	@Column(name="VAL_UNIT")
	private String valUnit;

	public CfgSystemParam() {
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getEnabled() {
		return this.enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getModifDate() {
		return this.modifDate;
	}

	public void setModifDate(Date modifDate) {
		this.modifDate = modifDate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParamGroup() {
		return this.paramGroup;
	}

	public void setParamGroup(String paramGroup) {
		this.paramGroup = paramGroup;
	}

	public int getParamOrder() {
		return this.paramOrder;
	}

	public void setParamOrder(int paramOrder) {
		this.paramOrder = paramOrder;
	}

	public String getParamTitle() {
		return this.paramTitle;
	}

	public void setParamTitle(String paramTitle) {
		this.paramTitle = paramTitle;
	}

	public String getVal() {
		return this.val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getValInputType() {
		return this.valInputType;
	}

	public void setValInputType(String valInputType) {
		this.valInputType = valInputType;
	}

	public String getValList() {
		return this.valList;
	}

	public void setValList(String valList) {
		this.valList = valList;
	}

	public int getValMax() {
		return this.valMax;
	}

	public void setValMax(int valMax) {
		this.valMax = valMax;
	}

	public int getValMin() {
		return this.valMin;
	}

	public void setValMin(int valMin) {
		this.valMin = valMin;
	}

	public String getValUnit() {
		return this.valUnit;
	}

	public void setValUnit(String valUnit) {
		this.valUnit = valUnit;
	}

}