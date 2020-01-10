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

import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.meta.ProfileType;

/**
 * Interface defining Business Services methods and providing {@link Profile}
 * specific operations. This interface extends the common
 * {@link GenericProfileService} interface by adding new specific methods
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface ProfileService extends GenericProfileService<Profile> {

	/**
	 * Retrieve the list of all profiles which type is the given type. This
	 * method could return objects of any concrete instance, derived from
	 * profile
	 * 
	 * @param profileType
	 *            : profile type instance to search for
	 * 
	 * @return the list of all matching profiles
	 * 
	 * @throws IllegalArgumentException
	 *             when profileType is null
	 */
	List<Profile> findAllProfilesByProfileType(ProfileType profileType);

	/**
	 * Add roles to profile.
	 * 
	 * @param profile
	 *            : user's profile
	 * 
	 * @return authenticated profile with good roles
	 * 
	 * @throws IllegalArgumentException
	 *             when profile is null
	 */
	Profile addRoles(Profile profile);
	
	/**
	 * Retrieve profile by username
	 * @param username
	 * @return
	 */
	Profile findByUsername(String username);
	
	Profile findByUserId(Long userId);
}
