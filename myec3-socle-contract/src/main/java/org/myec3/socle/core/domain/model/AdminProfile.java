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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;


/**
 * This class represents a user profile with an administrator responsibility.
 * This class extend generic {@link Profile} class. A user associated to an
 * admin profile can play an administrator role on myec3 applications<br/>
 * 
 * This class is audited by global audit mechanism.<br/>
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class AdminProfile extends Profile {

	private static final long serialVersionUID = 2822237455030315913L;

	private Customer customer;

	/**
	 * Default constructor. Do nothing.
	 */
	public AdminProfile() {
		super();
	}

	/**
	 * Contructor. Initialize the profile name
	 * 
	 * @param name
	 *            : name of the profile
	 */
	public AdminProfile(String name) {
		super(name);
	}

	/**
	 * Constructor. Initialize the profile name, label, email and function
	 * 
	 * @param name
	 *            : name of the profile
	 * @param label
	 *            : label of the profile
	 * @param email
	 *            : email of the profile
	 * @param function
	 *            : function of the profile
	 */
	public AdminProfile(String name, String label, String email, String function) {
		super(name, label, email, function);
	}

	/**
	 * Constructor. Initialize the profile name, label, email, function and user
	 * 
	 * @param name
	 *            : name of the profile
	 * @param label
	 *            : label of the profile
	 * @param email
	 *            : email of the profile
	 * @param function
	 *            : function of the profile
	 * @param user
	 *            : real user associated to this profile
	 */
	public AdminProfile(String name, String label, String email,
			String function, User user) {
		super(name, label, email, function, user);
	}

	/**
	 * @return the {@link Customer} of the AdminProfile
	 */
	@NotNull
	@ManyToOne
	@JoinColumn(nullable = true)
	@XmlElement(required = true)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return false - profile is currently an admin profile
	 */
	@Override
	@Transient
	public boolean isEmployee() {
		return Boolean.FALSE;
	}

	/**
	 * @return false - profile is currently an admin profile
	 */
	@Override
	@Transient
	public boolean isAgent() {
		return Boolean.FALSE;
	}

	/**
	 * @return true - profile is currently an admin profile
	 */
	@Override
	@Transient
	public boolean isAdmin() {
		return Boolean.TRUE;
	}

	/**
	 * Determines if the profile is enabled. i.e. if the profile holders can
	 * access to applications. Cannot be null.
	 * 
	 * @return true if the profile is enabled, false otherwise
	 */
	@Override
	@Transient
	public boolean isEnabled() {
		return super.getEnabled();
	}
}
