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

import java.util.Date;
import java.util.List;

import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.exceptions.ProfileCreationException;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.myec3.socle.core.service.exceptions.ProfileUpdateException;

/**
 * This interface provide common method signatures for all resources
 * implementing {@link Profile}. WARNING : it is not an interface for
 * manipulation generic Profile type. All methods defined here should be applied
 * on a child of Profile. This interface exists in order to maximize reuse
 * between child implementations of Services manipulation children of Profile.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> : concrete Profile implementation
 */
public interface GenericProfileService<T extends Profile> extends ResourceService<T> {

	/**
	 * Implements a logical soft delete by deactivating profile from its id.
	 * 
	 * Modify the employee email by prefixing by {@link #getSoftDeletePrefix()} and
	 * suffixing by {@link #getSoftDeleteSuffix()}. Modify the employee username by
	 * prefixing by {@link #getSoftDeletePrefix()} and suffixing by
	 * {@link #getSoftDeleteSuffix()}. Disable employee by setting its property
	 * {@link EmployeeProfile#isEnabled()} to {@link Boolean#FALSE}
	 * 
	 * @param id : id of the profile to delete
	 * 
	 * @throws ProfileDeleteException   Exception thrown when an technical exception
	 *                                  occurs
	 * @throws IllegalArgumentException Exception thrown when the id argument is
	 *                                  null
	 * 
	 */
	void softDelete(Long id) throws ProfileDeleteException;

	/**
	 * Implements a logical soft delete by deactivating profile from a profile
	 * object.
	 * 
	 * @see #softDelete(Long)
	 * 
	 * @param profile : profile object to delete
	 * 
	 * @throws ProfileDeleteException   Exception thrown when an technical exception
	 *                                  occurs
	 * 
	 * @throws IllegalArgumentException Exception thrown when the profile object was
	 *                                  never persisted (not a detached entity)
	 */
	void softDelete(T profile) throws ProfileDeleteException;

	/**
	 * @return prefix added during a soft delete
	 */
	String getSoftDeletePrefix();

	/**
	 * @return suffix added during a soft delete
	 */
	String getSoftDeleteSuffix();

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
	 * Test if it already exist an other profile holding the given email
	 * 
	 * @param email   : email to search
	 * @param profile : profile that should hold the email.
	 * @return true if email is already holded by an other profile (from the same
	 *         concrete class, T) than the actual profile, false otherwise
	 * @throws IllegalArgumentException when email or profile is null
	 */
	Boolean emailAlreadyExists(String email, T profile);

	/**
	 * Test if it already exist an other profile holding the given username
	 * 
	 * @param username : username to search
	 * @param profile  : profile that should hold the username.
	 * @return true if username is already holded by an other profile (from the same
	 *         concrete class, T) than the actual profile, false otherwise
	 * @throws IllegalArgumentException when username or profile is null
	 */
	Boolean usernameAlreadyExists(String username, T profile);

	/**
	 * This method implements specific business for profile creation by adding
	 * profile type and the possibility to raise a business specific exception for
	 * profile creation
	 * 
	 * @param profile : profile to create
	 * @return
	 * @throws ProfileCreationException when when profile is invalid or a technical
	 *                                  error occurs
	 * @throws IllegalArgumentException when profile is null or already persisted
	 */
	@Override
	void create(T profile) throws ProfileCreationException;

	/**
	 * This method implements specific business for profile update by adding profile
	 * type and the possibility to raise a business specific exception for profile
	 * update
	 * 
	 * @param profile : profile to update
	 * @throws ProfileUpdateException   when profile is invalid or a technical error
	 *                                  occurs
	 * @throws IllegalArgumentException when profile is null
	 */
	@Override
	T update(T profile) throws ProfileUpdateException;

	/**
	 * Retrieve the list of all profiles associated to the given user instance. This
	 * method could return objects of any concrete instance, derived from profile
	 * 
	 * @param user : user instance to search for
	 * 
	 * @return the list of all matching profiles
	 * 
	 * @throws IllegalArgumentException when user is null
	 */
	List<T> findAllProfilesByUser(User user);

	/**
	 * Retrieve the list of all profiles associated to the given role instance. This
	 * method could return objects of any concrete instance, derived from profile
	 * 
	 * @param role : role instance to search for
	 * 
	 * @return the list of all matching profiles
	 * 
	 * @throws IllegalArgumentException when role is null
	 */
	List<T> findAllProfilesByRole(Role role);

	/**
	 * Retrieve a profile from its email.
	 * 
	 * @param email : email to search for
	 * 
	 * @return found Profile with this email, null oitherwise. If multiple profiles
	 *         contain the same email, returns null.
	 * 
	 * @throws IllegalArgumentException when email is null
	 */
	T findByEmail(String email);

	/**
	 * Retrive all Profile matching given criteria. All null valued parameter is
	 * ignored.
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
	 * derived from profile
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
