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
package org.myec3.socle.core.domain.model.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;

/**
 * Desription of the ProfileType object. It extends {@link Resource} object
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
@Entity
@Audited
public class ProfileType implements Serializable, PE {

	private static final long serialVersionUID = -1002610177496081249L;

	private Long id;
	private ProfileTypeValue value;
	private List<Profile> profiles = new ArrayList<Profile>();
	private List<ProfileTypeRole> roles = new ArrayList<ProfileTypeRole>();

	/**
	 * Default contructor : do nothing
	 */
	public ProfileType() {
	}

	/**
	 * Technical id of the ProfileType
	 * 
	 * @return the id for this resource
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the list of {@link ProfileTypeRole}
	 */
	@OneToMany(mappedBy = "profileType")
	@JsonIgnore
	public List<ProfileTypeRole> getRoles() {
		return this.roles;
	}

	public void setRoles(List<ProfileTypeRole> roles) {
		this.roles = roles;
	}

	/**
	 * @return the list of {@link Profile} corresponding at this {@link ProfileType}
	 */
	@OneToMany(mappedBy = "profileType")
	@JsonIgnore
	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	/**
	 * @return the {@link ProfileTypeValue} corresponding at this
	 *         {@link ProfileType}
	 */
	@Enumerated(EnumType.STRING)
	@Column(unique = true)
	public ProfileTypeValue getValue() {
		return value;
	}

	public void setValue(ProfileTypeValue value) {
		this.value = value;
	}

}