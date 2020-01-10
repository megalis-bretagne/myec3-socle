package org.myec3.socle.core.domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class InseeLegalCategory implements PE {

	@Id
	private int idInsee;
	private String label;
	private int parent;

	public InseeLegalCategory() {
	}

	public int getIdInsee() {
		return idInsee;
	}

	public void setIdInsee(int idInsee) {
		this.idInsee = idInsee;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

}