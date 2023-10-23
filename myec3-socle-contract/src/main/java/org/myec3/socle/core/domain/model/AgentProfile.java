/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a user profile that is in reality a public agent,
 * member of a public {@link Organism}.
 * <p>
 * This class is synchronized.<br />
 * <p>
 * This class extend generic {@link Profile} class.<br/>
 * <p>
 * This class is audited by global audit mechanism.<br/>
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class AgentProfile extends Profile {

	private static final long serialVersionUID = 2822237455030315913L;

	private Boolean elected;
	private Boolean executive;
	private Boolean representative;
	private Boolean substitute;
	private OrganismDepartment organismDepartment;
	private Set<Competence> competences = new HashSet<>();

	/**
	 * Default constructor. Do nothing.
	 */
	public AgentProfile() {
		super();
	}

	/**
	 * Contructor. Initialize the profile name
	 *
	 * @param name : name of the profile
	 */
	public AgentProfile(String name) {
		super(name);
	}

	/**
	 * Constructor. Initialize the profile name, label, email and function
	 *
	 * @param name     : name of the profile
	 * @param label    : label of the profile
	 * @param email    : email of the profile
	 * @param function : function of the profile
	 */
	public AgentProfile(String name, String label, String email,
			String function, Boolean elected, Boolean executive,
			Boolean representative, Boolean substitute) {
		super(name, label, email, function);
		this.elected = elected;
		this.executive = executive;
		this.representative = representative;
		this.substitute = substitute;
	}

	/**
	 * Constructor. Initialize the profile name, label, email, function, user,
	 * elected and organismDepartment
	 *
	 * @param name               : name of the profile
	 * @param label              : label of the profile
	 * @param email              : email of the profile
	 * @param function           : function of the profile
	 * @param user               : real user associated to this profile
	 * @param elected            : is this agent is elected or not
	 * @param organismDepartment : department of the profile
	 */
	public AgentProfile(String name, String label, String email,
			String function, User user, Boolean elected, Boolean executive,
			Boolean representative, Boolean substitute,
			OrganismDepartment organismDepartment) {
		super(name, label, email, function, user);
		this.elected = elected;
		this.executive = executive;
		this.representative = representative;
		this.substitute = substitute;
		this.organismDepartment = organismDepartment;
	}

	/**
	 * Defines if the agent is elected or not
	 *
	 * @return true if the agent is elected, false otherwise
	 */
	@NotNull
	@Column(nullable = false)
	@XmlElement(required = true)
	public Boolean getElected() {
		if (this.elected == null) {
			this.elected = Boolean.FALSE;
		}
		return elected;
	}

	public void setElected(Boolean elected) {
		this.elected = elected;
	}

	/**
	 * Defines if the agent is the executive of organism
	 *
	 * @return true if the agent is the executive, false otherwise
	 */
	@Column(nullable = false)
	@XmlElement(required = false)
	public Boolean getExecutive() {
		if (this.executive == null) {
			this.executive = Boolean.FALSE;
		}
		return executive;
	}

	public void setExecutive(Boolean executive) {
		this.executive = executive;
	}

	/**
	 * Defines if the agent is the representative at the General Assembly
	 *
	 * @return true if the agent is the representative, false otherwise
	 */
	@Column(nullable = false)
	@XmlElement(required = false)
	public Boolean getRepresentative() {
		if (this.representative == null) {
			this.representative = Boolean.FALSE;
		}
		return representative;
	}

	public void setRepresentative(Boolean representative) {
		this.representative = representative;
	}

	/**
	 * Defines if the agent is the substitute at the General Assembly
	 *
	 * @return true if the agent is the substitute, false otherwise
	 */
	@Column(nullable = false)
	@XmlElement(required = false)
	public Boolean getSubstitute() {
		if (this.substitute == null) {
			this.substitute = Boolean.FALSE;
		}
		return substitute;
	}

	public void setSubstitute(Boolean substitute) {
		this.substitute = substitute;
	}

	/**
	 * Department of the profile organism that the profile is attached to
	 *
	 * @return the department of the profile
	 */
	@NotNull
	@Valid
	@ManyToOne
	@JoinColumn(nullable = false)
	@XmlElement(required = true)
	@JsonIgnore
	public OrganismDepartment getOrganismDepartment() {
		return organismDepartment;
	}

	public void setOrganismDepartment(OrganismDepartment organismDepartment) {
		this.organismDepartment = organismDepartment;
	}

	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(name = "competence_agentprofile", joinColumns = {
			@JoinColumn(name = "agentProfile_id") }, inverseJoinColumns = { @JoinColumn(name = "competence_id") })
	@XmlTransient
	public Set<Competence> getCompetences() {
		return competences;
	}

	public void setCompetences(Set<Competence> competences) {
		this.competences = competences;
	}

	/**
	 * @return false - profile is currently an agent profile
	 */
	@Override
	@Transient
	public boolean isEmployee() {
		return Boolean.FALSE;
	}

	/**
	 * @return true - profile is currently an agent profile
	 */
	@Override
	@Transient
	public boolean isAgent() {
		return Boolean.TRUE;
	}

	/**
	 * @return false - profile is currently an agent profile
	 */
	@Override
	@Transient
	public boolean isAdmin() {
		return Boolean.FALSE;
	}

	/**
	 * Determines if the profile is enabled. i.e. if the profile holders can access
	 * to applications. Cannot be null.
	 *
	 * @return true if the profile is enabled, false otherwise
	 */
	@Override
	@Transient
	public boolean isEnabled() {
		return super.getEnabled();
	}

	/**
	 * Clone an AgentProfile (see PHT-EB-PROD-1129)
	 *
	 * @return an agentProfile for the synchro
	 * @throws CloneNotSupportedException
	 */
	public AgentProfile cloneForSynchro() throws CloneNotSupportedException {
		AgentProfile clone = new AgentProfile();

		// Initializing vars that are not modified by synchro
		clone.setId(this.getId());
		clone.setElected(this.getElected());
		clone.setExecutive(this.getExecutive());
		clone.setRepresentative(this.getRepresentative());
		clone.setSubstitute(this.getSubstitute());
		clone.setEmail(this.getEmail());
		clone.setPhone(this.getPhone());
		clone.setFax(this.getFax());
		clone.setCellPhone(this.getCellPhone());
		clone.setAddress(this.getAddress());
		clone.setFunction(this.getFunction());
		clone.setEnabled(this.getEnabled());
		clone.setName(this.getName());
		clone.setLabel(this.getLabel());
		clone.setExternalId(this.getExternalId());
		clone.setEnabled(this.getEnabled());
		clone.setGrade(this.getGrade());
		clone.setProfileType(this.getProfileType());
		clone.setPrefComMedia(this.getPrefComMedia());
		clone.setUsername(this.getUsername());

		// For this ones i'm not sure... seems to be working for now <3
		clone.setOrganismDepartment(this.getOrganismDepartment());
		clone.setUser(this.getUser());
		clone.setAuthorities(this.getAuthorities());

		// Initializing vars which could be modified by synchro
		clone.setRoles(new ArrayList<Role>(this.getRoles()));

		return clone;
	}

}
