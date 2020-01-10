package org.myec3.socle.webapp.view;

import java.util.Objects;

import org.myec3.socle.core.domain.model.OrganismDepartment;

/**
 * Organism Department View for organization chart
 */
public class OrganismDepartmentView {

	private OrganismDepartment organismDepartment;

	private String nameDisplay;

	public OrganismDepartmentView(OrganismDepartment organismDepartment, String nameDisplay) {
		this.organismDepartment = organismDepartment;
		this.nameDisplay = nameDisplay;
	}

	public String getNameDisplay() {
		return nameDisplay;
	}

	public void setNameDisplay(String nameDisplay) {
		this.nameDisplay = nameDisplay;
	}

	public OrganismDepartment getOrganismDepartment() {
		return organismDepartment;
	}

	public void setOrganismDepartment(OrganismDepartment organismDepartment) {
		this.organismDepartment = organismDepartment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getNameDisplay(), getOrganismDepartment());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OrganismDepartmentView)) {
			return false;
		}

		OrganismDepartmentView other = (OrganismDepartmentView) obj;
		return Objects.equals(other.getOrganismDepartment(), getOrganismDepartment())
				&& Objects.equals(other.getNameDisplay(), getNameDisplay());
	}

}
