/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

/**
 * This class represents a {@link Department} of an {@link Organism}. This class
 * extend generic {@link Department} class. <br />
 * 
 * This class is synchronized. <br />
 * 
 * This class is audited by global audit mechanism.<br/>
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class OrganismDepartment extends Department implements
		Comparable<OrganismDepartment> {

	private static final long serialVersionUID = -4864477194582573930L;
	private Organism organism;
	private String abbreviation;
	private OrganismDepartment parentDepartment;
	private List<AgentProfile> agents = new ArrayList<AgentProfile>();
	private List<OrganismDepartment> organismDepartments;

	private Boolean rootDepartment;

	/**
	 * Default constructor. Do nothing.
	 */
	public OrganismDepartment() {
		super();
	}

	/**
	 * Contructor. Initialize the OrganismDepartment's name and label
	 * 
	 * @param name  : the name of the organism Department
	 * @param label : the label of the organism Department
	 */
	public OrganismDepartment(String name, String label) {
		super(name, label);
	}

	/**
	 * Contructor. Initialize the organism's Department name, label and organism
	 * 
	 * @param name    : the name of the Department
	 * @param label   : the label of the Department
	 * @param company : the organism of the Department
	 */
	public OrganismDepartment(String name, String label, Organism organism) {
		super(name, label);
		this.organism = organism;
	}

	/**
	 * The organism of the Department
	 * 
	 * @return organism of the Department
	 */
	@NotNull
	@Valid
	@ManyToOne
	@XmlElement(required = true)
	@JoinColumn(nullable = false)
	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	/**
	 * The organism's Department abreviation
	 * 
	 * @return abreviation
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * Determines if the organismDepartment is a root Department.
	 * 
	 * @return true if an organismDepartment is a root department.
	 */
	@XmlElement(required = true)
	@Transient
	public Boolean isRootDepartment() {
		if (this.rootDepartment == null) {
			return (this.parentDepartment == null);
		}
		return this.rootDepartment;
	}

	public void setRootDepartment(Boolean rootDepartment) {
		this.rootDepartment = rootDepartment;
	}

	/**
	 * OrganismDepartement parent of the current organismDepartment.
	 * 
	 * @return OrganismDepartement parent.
	 */
	@ManyToOne
	@JoinColumn(nullable = true)
	@XmlElement(required = false)
	public OrganismDepartment getParentDepartment() {
		return parentDepartment;
	}

	public void setParentDepartment(OrganismDepartment parentDepartment) {
		this.parentDepartment = parentDepartment;
	}

	/**
	 * List of agents of the organismDepartment.
	 * 
	 * @return list of agents.
	 */
	@OneToMany(mappedBy = "organismDepartment")
	@XmlTransient
	@JsonIgnore
	public List<AgentProfile> getAgents() {
		return agents;
	}

	public void setAgents(List<AgentProfile> agents) {
		this.agents = agents;
	}

	/**
	 * Add a new agent to the organsimDeartement's agents.
	 * 
	 * @param agent to add
	 * @return Updated of organsimDeartement.
	 */
	public OrganismDepartment addAgent(AgentProfile agent) {
		this.agents.add(agent);
		return this;
	}

	/**
	 * Add a list agents to the organsimDepartment's agents.
	 * 
	 * @param List of agents to add
	 * @return Updated organsimDepartment.
	 */
	public OrganismDepartment addAgents(List<AgentProfile> agents) {
		this.agents.addAll(agents);
		return this;
	}

	/**
	 * Delete an agent from the organsimDeartement's agents.
	 * 
	 * @param agent to delete
	 * @return Updated organsimDepartement.
	 */
	public OrganismDepartment removeAgent(AgentProfile agent) {
		this.agents.remove(agent);
		return this;
	}

	/**
	 * Clear agents list of the organsimDepartment.
	 * 
	 * @return Updated organismDepartment.
	 */
	public OrganismDepartment clearAgents() {
		this.agents.clear();
		return this;
	}

	/**
	 * List of organismDeparments attached to the current organismDepartment.
	 * 
	 * @return sub organism departement.
	 */
	@OneToMany(mappedBy = "parentDepartment")
	@XmlTransient
	@JsonIgnore
	public List<OrganismDepartment> getOrganismDepartments() {
		return organismDepartments;
	}

	public void setOrganismDepartments(
			List<OrganismDepartment> organismDepartments) {
		this.organismDepartments = organismDepartments;
	}

	/**
	 * Check if the organismDepartment is empty
	 * 
	 * @return TRUE if the organismDepartment is empty else FALSE
	 */
	@Transient
	public Boolean getIsEmpty() {
		if (!this.agents.isEmpty()) {
			return Boolean.FALSE;
		}

		if (!this.organismDepartments.isEmpty()) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getLabel() == null) ? 0 : this.getLabel().hashCode());
		result = prime * result
				+ ((this.getId() == null) ? 0 : this.getId().hashCode());
		return result;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		OrganismDepartment other = (OrganismDepartment) obj;
		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(OrganismDepartment o) {

		if (null == this.getLabel() || null == o.getLabel()) {
			return -1;
		}

		return this.getLabel().compareTo(o.getLabel());
	}
}
