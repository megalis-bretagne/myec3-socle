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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;

/**
 * Description of the ProfileTypeRole object. It extends from {@link Resource}
 * object.
 * 
 * This class represents the link between a {@link ProfileType} and a
 * {@link Role} in order to retrieve the role available for a given ProfileType.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
@Entity
@Audited
public class ProfileTypeRole implements Serializable, PE {

	private static final long serialVersionUID = 6686146664680844209L;

	private Long id;
	private ProfileType profileType;
	private Role role;
	private Boolean defaultAdmin;
	private Boolean defaultBasic;

	/**
	 * Default constructor : call the {@link Resource} default constructor : do
	 * nothing
	 */
	public ProfileTypeRole() {
	}

	/**
	 * Constructor. Initialize defaultAdmin and defaultBasic.
	 * 
	 * @param defaultAdmin : true if the Role of the profile type is Default Admin
	 * @param defaultBasic : true if the Role of the profile type is Default Admin
	 */
	public ProfileTypeRole(Boolean defaultAdmin, Boolean defaultBasic) {
		this.defaultAdmin = defaultAdmin;
		this.defaultBasic = defaultBasic;
	}

	/**
	 * Technical id of the ProfileTypeRole
	 * 
	 * @return the unique identifier
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
	 * @return true if the {@link Role} associated at this {@link profileType} is
	 *         the default admin role.
	 */
	@Column(nullable = false)
	public Boolean getDefaultAdmin() {
		return defaultAdmin;
	}

	public void setDefaultAdmin(Boolean defaultAdmin) {
		this.defaultAdmin = defaultAdmin;
	}

	/**
	 * @return true if the {@link Role} associated at this {@link profileType} is a
	 *         basic role.
	 */
	@Column(nullable = false)
	public Boolean getDefaultBasic() {
		return defaultBasic;
	}

	public void setDefaultBasic(Boolean defaultBasic) {
		this.defaultBasic = defaultBasic;
	}

	/**
	 * @return the {@link Role} corresponding at this ProfileTypeRole
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * @return the {@link ProfileType} associated
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	public ProfileType getProfileType() {
		return this.profileType;
	}

	public void setProfileType(ProfileType profileType) {
		this.profileType = profileType;
	}

}
