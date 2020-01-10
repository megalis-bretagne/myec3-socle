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
package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileType;

import java.util.List;

/**
 * This interface define methods to perform specific queries on {@link Role}
 * objects. It only defines new specific methods and inherits methods from
 * {@link ResourceDao}.
 *
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface RoleDao extends ResourceDao<Role> {

	/**
	 * Retrieves all roles holding a given {@link Profile}
	 *
	 * @param profile : {@link Profile} to search on
	 * @return the list of all matching roles, an empty list if there is no
	 * result
	 * @throws RuntimeException in case of errors
	 */
	List<Role> findAllRoleByProfile(Profile profile);

	/**
	 * Retrieves all roles holding a given {@link Application}
	 *
	 * @param application : {@link Application} to search on
	 * @return the list of all matching roles, an empty list if there is no
	 * result
	 * @throws RuntimeException in case of errors
	 */
	List<Role> findAllRoleByApplication(Application application);

	/**
	 * Retrieves all roles holding a given {@link Profile} and a given
	 * {@link Application}
	 *
	 * @param profile     : {@link Profile} to search on
	 * @param application : {@link Application} to search on
	 * @return the list of all matching {@link Role}, an empty list if there is
	 * no result
	 * @throws RuntimeException in case of errors
	 */
	List<Role> findAllRoleByProfileAndApplication(Profile profile,
												  Application application);

	/**
	 * Retrieves all roles holding a given {@link Profile} and a given
	 * {@link Application}
	 *
	 * @param profile     : {@link Profile} to search on
	 * @param application : {@link Application} to search on
	 * @return the list of all matching {@link Role}, an empty list if there is
	 * no result
	 * @throws RuntimeException in case of errors
	 */
	List<Role> findAllRoleByProfileAndApplication(Profile profile,
												  Application application, boolean withoutHidden);


	/**
	 * Retrieves all roles holding a given {@link Profile} type and a given
	 * {@link Application}
	 *
	 * @param profileType : {@link ProfileType} to search on
	 * @param application : {@link Application} to search on
	 * @return the list of all matching roles, an empty list if there is no
	 * result
	 * @throws RuntimeException in case of errors
	 */
	List<Role> findAllRoleByProfileTypeAndApplication(
			ProfileType profileType,
			Application application,
			boolean withoutHidden
	);

	/**
	 * Retrieve the admin role for a given {@link ProfileType} and a given
	 * {@link Application}
	 *
	 * @param profileType : {@link ProfileType} to search on
	 * @param application : {@link Application} to search on
	 * @return the found {@link Role} or null if not found
	 * @throws RuntimeException in case of errors
	 */
	Role findAdminRoleByProfileTypeAndApplication(ProfileType profileType,
												  Application application);

	/**
	 * Retrieve the basic {@link Role} for a given profile type and a given
	 * {@link Application}
	 *
	 * @param profileType : {@link ProfileType} to search on
	 * @param application : {@link Application} to search on
	 * @return the found role or null if not found
	 * @throws RuntimeException in case of errors
	 */
	Role findBasicRoleByProfileTypeAndApplication(ProfileType profileType,
												  Application application);

	/**
	 * Retrieve all roles depending on the filters defined in parameters.
	 * If all parameters are null, retrieve all Applications.
	 *
	 * @param label       : Role label to find. If this parameter is not null,
	 *                    filters on all roles with the same label. Case no
	 *                    matters.
	 * @param profile     : role associated to profile to find. Case no
	 * @param application : role associated to application to find. Case no matters
	 * @param application : role associated to profileType to find. Case no matters
	 * @return the list of all matching roles. Return an empty list if
	 * there's no result. Return null in case of errors.
	 * @throws RuntimeException in case of errors
	 */
	List<Role> findAllByCriteria(String label, String name, Application application);
}
