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
package org.myec3.socle.core.domain.dao;

import java.util.List;

import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;

/**
 * This interface define methods to perform specific queries on
 * {@link ProfileTypeRole} objects. It only defines new specific methods and
 * inherits methods from {@link ResourceDao}.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public interface ProfileTypeRoleDao extends NoResourceGenericDao<ProfileTypeRole> {

	/**
	 * Retrieve all ProfileTypeRoles by their {@link ProfileType}
	 * 
	 * @param profileType
	 *            : {@link ProfileType} to search on
	 * @return the list of the found ProfileTypeRoles, an empty list in case of
	 *         errors
	 */
	List<ProfileTypeRole> findAllProfileTypeRoleByProfileType(
			ProfileType profileType);

	/**
	 * Retrieve all ProfileTypeRoles by their {@link Role}
	 * 
	 * @param role
	 *            : {@link Role} to search on
	 * @return the list of the found ProfileTypeRoles, an empty list in case of
	 *         errors
	 */
	List<ProfileTypeRole> findAllProfileTypeRoleByRole(Role role);
}