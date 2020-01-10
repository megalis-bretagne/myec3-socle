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
package org.myec3.socle.core.service;

import java.util.List;

import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;

/**
 * Interface defining Business Services methods and providing
 * {@link ProfileTypeRole} specific operations.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
public interface ProfileTypeRoleService extends IGenericService<ProfileTypeRole> {

	/**
	 * Retrieve all ProfileTypeRoles by their {@link ProfileType}
	 * 
	 * @param profileType : {@link ProfileType} to search on
	 * @return the list of the found ProfileTypeRoles, an empty list in case of
	 *         errors
	 * @throws IllegalArgumentException if profileType is null.
	 */
	List<ProfileTypeRole> findAllProfileTypeRoleByProfileType(ProfileType profileType);

	/**
	 * Retrieve all ProfileTypeRoles by their {@link Role}
	 * 
	 * @param role : {@link Role} to search on
	 * @return the list of the found ProfileTypeRoles, an empty list in case of
	 *         errors
	 * @throws IllegalArgumentException if role is null.
	 */
	List<ProfileTypeRole> findAllProfileTypeRoleByRole(Role role);
}
