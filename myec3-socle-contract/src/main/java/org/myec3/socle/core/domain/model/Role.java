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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;

/**
 * This class represents a Role. A role is associated at one {@link Application}
 * and one or several {@link Profile} in order to define profile's
 * authorizations on each remote {@link Application}.<br />
 * 
 * This class is synchronized.<br />
 * 
 * This class is audited by global audit mechanism<br />
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class Role extends Resource {

	private static final long serialVersionUID = -5520370917933527899L;

	private Application application;
	private List<Profile> profiles;
	private List<ProfileTypeRole> profileTypes = new ArrayList<ProfileTypeRole>();
	private String description;
	private Boolean enabled;
	private Boolean hidden;

	/**
	 * Default constructor. Do nothing.
	 */
	public Role() {
		super();
	}

	/**
	 * Constructor. Initialize the name of the role and its label.
	 * 
	 * @param name  : name of the role
	 * @param label : label of the role
	 */
	public Role(String name, String label) {
		super(name, label);
	}

	/**
	 * Constructor. Initialize the name of the role, its label and the
	 * {@link Application} associated.
	 * 
	 * @param name        : name of the role
	 * @param label       : label of the role
	 * @param application : linked application
	 */
	public Role(String name, String label, Application application) {
		super(name, label);
		this.application = application;
	}

	/**
	 * Constructor. Initialize the name of the role, its label, the
	 * {@link Application} associated and the list of {@link Profile} who use this
	 * Role.
	 * 
	 * @param name        : name of the role
	 * @param label       : label of the role
	 * @param application : linked application
	 * @param profiles    : a list of profiles who use this role.
	 */
	public Role(String name, String label, Application application,
			List<Profile> profiles) {
		super(name, label);
		this.application = application;
		this.profiles = profiles;
	}

	/**
	 * Constructor. Initialize the name of the role, its label, the
	 * {@link Application} associated and the description of the Role.
	 * 
	 * @param name        : name of the role
	 * @param label       : label of the role
	 * @param application : linked application
	 * @param profiles    : a list of profiles who use this role
	 * @param description : text used to describe the role
	 */
	public Role(String name, String label, Application application,
			List<Profile> profiles, String description) {
		super(name, label);
		this.application = application;
		this.profiles = profiles;
		this.description = description;
	}

	/**
	 * @return the {@link Application} associated at this role
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	@XmlElement(required = true)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * @return a list of {@link Profile} associated at this role
	 */
	@ManyToMany(mappedBy = "roles")
	@XmlTransient
	@JsonIgnore
	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	/**
	 * @param profile : the {@link Profile} to add at the role's list of
	 *                {@link Profile}
	 * @return the role
	 */
	public Role addProfile(Profile profile) {
		this.profiles.add(profile);
		return this;
	}

	/**
	 * @param profiles : a list of {@link Profile} to add to the role
	 * @return the role
	 */
	public Role addProfiles(List<Profile> profiles) {
		this.profiles.addAll(profiles);
		return this;
	}

	/**
	 * @param profile : the {@link Profile} to remove from the role
	 * @return the role
	 */
	public Role removeProfile(Profile profile) {
		this.profiles.remove(profile);
		return this;
	}

	/**
	 * clear all {@link Profile} from the list of profiles contained into the role
	 * 
	 * @return the role with an empty list of profiles
	 */
	public Role clearProfiles() {
		this.profiles.clear();
		return this;
	}

	/**
	 * @return a list of {@link ProfileTypeRole} associated at this role
	 */
	@OneToMany(mappedBy = "role")
	@XmlTransient
	public List<ProfileTypeRole> getProfileTypes() {
		return this.profileTypes;
	}

	public void setProfileTypes(List<ProfileTypeRole> profileTypes) {
		this.profileTypes = profileTypes;
	}

	/**
	 * @return the description of the role.
	 */
	@Column(nullable = true, columnDefinition = "LONGTEXT")
	@XmlTransient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return true if the role is enabled. Otherwise return false.
	 */
	@Transient
	@XmlElement(required = false)
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return true if the role is enabled. Otherwise return false.
	 */
	@XmlElement(required = false)
	public Boolean isHidden() {
		return (hidden == null) ? Boolean.FALSE : hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

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
		Role other = (Role) obj;
		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

}
