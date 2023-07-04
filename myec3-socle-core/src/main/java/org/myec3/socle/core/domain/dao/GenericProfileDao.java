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

import java.util.Date;
import java.util.List;

import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;

/**
 * This interface defines methods to perform queries on {@link Profile} objects
 * or objects extending profiles.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> : instance, child of Profile to query on
 */
public interface GenericProfileDao<T extends Profile> extends ResourceDao<T> {

	/**
	 * Find all concrete T instances (extending profiles) from a given email
	 * 
	 * @param email : email value to filter on
	 * @return the list of found concrete instances child of profiles. Returns an
	 *         empty list if no result found.
	 * 
	 * @throws IllegalArgumentException Exception thrown when the email is null
	 */
	List<T> findAllByEmail(String email);

	/**
	 * Find all concrete T instances (extending profiles) associated to the given
	 * user instance. This method could return objects of any concrete instance,
	 * derived from {@link Profile}
	 * 
	 * @param user : user instance to search for
	 * @return the list of all matching profiles. Returns an empty list if no result
	 *         found.
	 */
	List<T> findAllProfilesByUser(User user);

	/**
	 * Find all concrete T instances (extending profiles) associated to the given
	 * {@link Role} instance. This method could return objects of any concrete
	 * instance, derived from {@link Profile}
	 * 
	 * @param role : role instance to search for
	 * @return the list of all matching profiles. Returns an empty list if no result
	 *         found.
	 */
	List<T> findAllProfilesByRole(Role role);

	/**
	 * Retrieve a {@link Profile} from its email. This method could raise an
	 * exception if several profiles are associated with this email.
	 * 
	 * @param email : email to search for
	 * @return found Profile with this email. Returns null if no result found.
	 */
	T findByEmail(String email);

	/**
	 * Retrive all {@link Profile} matching given criteria. All null valued
	 * parameter is ignored.
	 * 
	 * @param email      : profile email
	 * @param username   : profile username
	 * @param firstname  : profile firstname
	 * @param lastname   : profile lastname
	 * @param sviProfile : profile svi related profile
	 * @return the list of the matching results, empty list if no result.
	 * @throws RuntimeException in case of errors
	 */
	List<T> findAllByCriteria(String email, String username, String firstname, String lastname, Long sviProfile);

	/**
	 * Find all concrete enabled T instances (extending profiles) associated at this
	 * email or username. This method could return objects of any concrete instance,
	 * derived from {@link Profile}
	 * 
	 * @param emailOrUsername : emailOrUsername instance to search for
	 * @return the list of all matching profiles. Returns an empty list if no result
	 *         found.
	 */
	List<T> findAllProfileEnabledByEmailOrUsername(String emailOrUsername);

	/**
	 * Retrieve a list of profiles from a list of ids.
	 * 
	 * @param listOfIds : list of ids to search for
	 * @return found Profiles with this ids. Returns an empty array if no result
	 *         found.
	 */
	List<T> findAllProfilesByListOfId(List<Long> listOfIds);
}