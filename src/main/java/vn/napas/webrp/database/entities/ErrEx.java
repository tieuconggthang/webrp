package vn.napas.webrp.database.entities;

import java.io.Serializable;
import jakarta.persistence.*;


/**
 * The persistent class for the ERR_EX database table.
 * 
 */
@Entity
@Table(name="ERR_EX")
@NamedQuery(name="ErrEx.findAll", query="SELECT e FROM ErrEx e")
public class ErrEx implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ErrExPK id;

	@Column(name="ERR_DETAIL")
	private String errDetail;

	public ErrEx() {
	}

	public ErrExPK getId() {
		return this.id;
	}

	public void setId(ErrExPK id) {
		this.id = id;
	}

	public String getErrDetail() {
		return this.errDetail;
	}

	public void setErrDetail(String errDetail) {
		this.errDetail = errDetail;
	}

}