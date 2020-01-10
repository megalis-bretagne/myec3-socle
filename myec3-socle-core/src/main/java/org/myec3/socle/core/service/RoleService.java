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

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileType;

/**
 * Interface defining Business Services methods and providing {@link Role}
 * specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface RoleService extends ResourceService<Role> {

	/**
	 * Retrieves all roles holding a given profile
	 * 
	 * @param profile
	 *            : profile to search on
	 * @return the list of all matching roles, an empty list if there is no
	 *         result
	 * @throws IllegalArgumentException
	 *             if profile is null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Role> findAllRoleByProfile(Profile profile);

	/**
	 * Retrieves all roles holding a given application
	 * 
	 * @param application
	 *            : application to search on
	 * @return the list of all matching roles, an empty list if there is no
	 *         result
	 * @throws IllegalArgumentException
	 *             if application is null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Role> findAllRoleByApplication(Application application);

	/**
	 * Retrieves all roles holding a given profile and a given application
	 * 
	 * @param profile
	 *            : profile to search on
	 * @param application
	 *            : application to search on
	 * @return the list of all matching roles, an empty list if there is no
	 *         result
	 * @throws IllegalArgumentException
	 *             if profile or application are null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Role> findAllRoleByProfileAndApplication(Profile profile,
			Application application);

	/**
	 * Retrieves all roles holding a given profile and a given application,
	 * with the hidden ones
	 *
	 * @param profile
	 *            : profile to search on
	 * @param application
	 *            : application to search on
	 * @return the list of all matching roles, an empty list if there is no
	 *         result
	 * @throws IllegalArgumentException
	 *             if profile or application are null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Role> findAllRoleByProfileAndApplicationWithoutHidden(Profile profile,
												  Application application);

	/**
	 * Retrieve the admin role for a given profile type and a given application
	 * 
	 * @param profileType
	 *            : profileType to search on
	 * @param application
	 *            : application to search on
	 * @return the found role or null if not found
	 * @throws IllegalArgumentException
	 *             if profileType or application are null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	Role findAdminRoleByProfileTypeAndApplication(ProfileType profileType,
			Application application);

	/**
	 * Retrieve the basic role for a given profile type and a given application
	 * 
	 * @param profileType
	 *            : profileType to search on
	 * @param application
	 *            : application to search on
	 * @return the found role or null if not found
	 * @throws IllegalArgumentException
	 *             if profileType or application are null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	Role findBasicRoleByProfileTypeAndApplication(ProfileType profileType,
			Application application);

	/**
	 * Retrieves all roles holding a given profile type and a given application
	 * 
	 * @param profileType
	 *            : profile type to search on
	 * @param application
	 *            : application to search on
	 * @return the list of all matching roles, an empty list if there is no
	 *         result
	 * @throws IllegalArgumentException
	 *             if profileType or application are null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Role> findAllRoleByProfileTypeAndApplication(ProfileType profileType,
			Application application);

	/**
	 * Retrieves all roles holding a given profile type and a given application
	 *
	 * @param profileType
	 *            : profile type to search on
	 * @param application
	 *            : application to search on
	 * @return the list of all matching roles, an empty list if there is no
	 *         result
	 * @throws IllegalArgumentException
	 *             if profileType or application are null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Role> findAllRoleByProfileTypeAndApplicationWithoutHidden(ProfileType profileType,
													  Application application);

	/**
	 * This method allows to populate role's collections
	 * 
	 * @param role
	 *            : role object that we want to populate
	 * @throws IllegalArgumentException
	 *             if role is null.
	 */
	void populateCollections(Role role);

	/**
	 * This method allows to clean role's collections (before marshalling object
	 * for send the xml into the JMS message)
	 * 
	 * @param role
	 *            : role to clean
	 * 
	 * @throws IllegalArgumentException
	 *             if role is null
	 */
	void cleanCollections(Role role);

	/**
	 * Retrieve all roles depending on the filters defined in parameters.
	 * If all parameters are null, retrieve all Applications.
	 * 
	 * @param label
	 *            : Role label to find. If this parameter is not null,
	 *            filters on all roles with the same label. Case no
	 *            matters.
	 * @param name
	 *            : role name to find
	 * @param application
	 *            : role associated to application to find. Case no matters
	 * @return the list of all matching roles. Return an empty list if
	 *         there's no result. Return null in case of errors.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	public List<Role> findAllByCriteria(String label, 
			String name, Application application);
}
