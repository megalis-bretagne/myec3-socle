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

import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.meta.ProfileType;

/**
 * This interface define methods to perform specific queries on {@link Profile}
 * objects). It only defines new specific methods and inherits methods from
 * {@link GenericProfileDao}.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface ProfileDao extends GenericProfileDao<Profile> {

	/**
	 * Find all concrete instances (extending profiles) wich type is the given
	 * type. This method could return objects of any concrete instance, derived
	 * from profile
	 * 
	 * @param profileType
	 *            : profile type instance to search for
	 * @return the list of all matching profiles. Returns an empty list if no
	 *         result found or technical errors
	 */
	List<Profile> findAllProfilesByProfileType(ProfileType profileType);

	/**
	 * Retrieve profile by username
	 * 
	 * @param username
	 * @return
	 */
	Profile findByUsername(String username);

	Profile findByUserId(Long userId);
}