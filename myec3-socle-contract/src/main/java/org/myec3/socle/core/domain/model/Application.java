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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;

/**
 * This class represents a MyEc3 application. An application is a business
 * service, accessible via a web interface, an url. Applications are independant
 * but connected through a MyEc3 platform. Application provide business
 * functionnalities to MyEc3 users. Applications must be declared in MyEc3
 * platform in order to grant or refuse access for specific user profiles or
 * structures. Each application can be associated to specific roles that can be
 * hold by profiles. Access to applications can be filtered at a structure level
 * by specifying structure types restriction to limit access to these
 * types.<br/>
 * 
 * This class is synchronized.<br />
 * 
 * This class extend generic resource class.<br/>
 * 
 * This class is audited by global audit mechanism.<br/>
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
@Entity
@Audited
@Synchronized
@XmlRootElement
public class Application extends Resource {

	private static final long serialVersionUID = 4683202690736075965L;

	private List<Role> roles;
	private List<Structure> structures;
	private List<StructureTypeApplication> structureTypes;
	private List<Customer> customers;
	private String url;
	private String pictoUrl;
	private String description;

	/**
	 * Default constructor. Do nothing.
	 */
	public Application() {
		super();
	}

	/**
	 * Constructor. Initialize application name and label
	 * 
	 * @param name  : application name
	 * @param label : application label (to be displayed)
	 */
	public Application(String name, String label) {
		super(name, label);
	}

	/**
	 * Constructor. Initialize application name, label, roles and structures
	 * 
	 * @param name       : application name
	 * @param label      : application label (to be displayed)
	 * @param roles      : list of roles that can be hold on this application
	 * @param structures : list of structures that provide access to this
	 *                   application to their user profiles
	 */
	public Application(String name, String label, List<Role> roles,
			List<Structure> structures) {
		super(name, label);
		this.roles = roles;
		this.structures = structures;
	}

	/**
	 * Constructor. Initialize application name, label, roles, structures and url.
	 * 
	 * @param name       : application name
	 * @param label      : application label (to be displayed)
	 * @param roles      : list of roles that can be hold on this application
	 * @param structures : list of structures that provide access to this
	 *                   application to their user profiles
	 * @param url        : url of this application
	 */
	public Application(String name, String label, List<Role> roles,
			List<Structure> structures, String url, String pictoUrl, String description) {
		super(name, label);
		this.roles = roles;
		this.structures = structures;
		this.url = url;
		this.pictoUrl = pictoUrl;
		this.description = description;
	}

	/**
	 * Roles that can be hold by {@link Profile} on this application. A {@link Role}
	 * define the association between profiles and application, allowing to define
	 * access restrictions to this application.
	 * 
	 * @see Profile
	 * @see Role
	 * 
	 * @return the list of roles for this application
	 */
	@OneToMany(mappedBy = "application", cascade = { CascadeType.ALL })
	@XmlTransient
	@JsonIgnore
	public List<Role> getRoles() {
		if (this.roles == null) {
			this.roles = new ArrayList<Role>();
		}
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Add a new role to this application, even if the role is already in list.
	 * 
	 * @param role : the new role to add
	 * @return the modified application
	 */
	public Application addRole(Role role) {
		this.roles.add(role);
		return this;
	}

	/**
	 * Add new roles to this application, even if roles are already in list.
	 * 
	 * @param roles : list of roles to add
	 * @return the modified application
	 */
	public Application addRoles(List<Role> roles) {
		this.roles.addAll(roles);
		return this;
	}

	/**
	 * Remove a given role from the application list. If the role does not exist,
	 * nothing is done
	 * 
	 * @param role : role to remove
	 * @return the modified application
	 */
	public Application removeRole(Role role) {
		this.roles.remove(role);
		return this;
	}

	/**
	 * Remove all roles from roles list
	 * 
	 * @return the modified application
	 */
	public Application clearRoles(List<Role> role) {
		this.roles.clear();
		return this;
	}

	/**
	 * {@link Structure} that can access to this application. i.e. structures that
	 * can grant access to this application to any user profile that is attached to
	 * the structure hierarchy.
	 * 
	 * @see Structure
	 * 
	 * @return the list of structures that can access to this application
	 */
	@ManyToMany(mappedBy = "applications")
	@XmlTransient
	@JsonIgnore
	public List<Structure> getStructures() {
		if (this.structures == null) {
			this.structures = new ArrayList<Structure>();
		}
		return structures;
	}

	public void setStructures(List<Structure> structures) {
		this.structures = structures;
	}

	/**
	 * Add a new structure to this application, even if the structure is already in
	 * list.
	 * 
	 * @param structure : the new structure to add
	 * @return the modified application
	 */
	public Application addStructure(Structure structure) {
		this.structures.add(structure);
		return this;
	}

	/**
	 * Add new structures to this application, even if structures are already in
	 * list.
	 * 
	 * @param structures : list of structures to add
	 * @return the modified application
	 */
	public Application addStructures(List<Structure> structures) {
		this.structures.addAll(structures);
		return this;
	}

	/**
	 * Remove a given structure from the application list. If the structure does not
	 * exist, nothing is done
	 * 
	 * @param structure : structure to remove
	 * @return the modified application
	 */
	public Application removeStructure(Structure structure) {
		this.structures.remove(structure);
		return this;
	}

	/**
	 * Remove all structures from structures list
	 * 
	 * @return the modified application
	 */
	public Application clearStructures(List<Structure> structures) {
		this.structures.clear();
		return this;
	}

	/**
	 * Url of this application. i.e. url where the application could be reach by
	 * user profiles
	 * 
	 * @return the url of the application
	 */
	@Column(nullable = false)
	@XmlElement(required = true)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Url of the picto corresponding to this application.
	 *
	 * @return the url of picto corresponding to the application
	 */
	@XmlElement(required = false)
	public String getPictoUrl() {
		return pictoUrl;
	}

	public void setPictoUrl(String pictoUrl) {
		this.pictoUrl = pictoUrl;
	}

	/**
	 * StructureTypes that can be associated to this application in order to grant
	 * access to structure user profiles. This allow to filter access to
	 * applications depending on specific types.
	 * 
	 * @see StructureTypeApplication
	 * 
	 * @return the list of structureTypes for this application
	 */
	@OneToMany(mappedBy = "application")
	@XmlTransient
	@JsonIgnore
	public List<StructureTypeApplication> getStructureTypes() {
		if (this.structureTypes == null) {
			this.structureTypes = new ArrayList<StructureTypeApplication>();
		}
		return this.structureTypes;
	}

	public void setStructureTypes(List<StructureTypeApplication> structureTypes) {
		this.structureTypes = structureTypes;
	}

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToMany(mappedBy = "applications")
	@XmlTransient
	@JsonIgnore
	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
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
	 * 
	 * Two applications are equals if only ids are equals
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
		Application other = (Application) obj;
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
	 * Descroption of the application. i.e. what the goal of the application is
	 *
	 * @return the url of the application
	 */
	@XmlElement(required = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Application [url=" + url + ", getName()=" + getName() + ", pictoUrl=" + pictoUrl + ", description="
				+ description + "]";
	}

}
