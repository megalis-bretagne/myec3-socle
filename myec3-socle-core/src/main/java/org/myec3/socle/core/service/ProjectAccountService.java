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

import org.myec3.socle.core.domain.model.ProjectAccount;
import org.myec3.socle.core.service.exceptions.FunctionalAccountCreationException;
import org.myec3.socle.core.service.exceptions.FunctionalAccountDeleteException;
import org.myec3.socle.core.service.exceptions.FunctionalAccountUpdateException;

/**
 * Interface defining Business Services methods and providing
 * {@link ProjectAccount} specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface ProjectAccountService extends ResourceService<ProjectAccount> {

	/**
	 * Retrieve a {@link ProjectAccount} from its email.
	 * 
	 * @param email : email to search for
	 * 
	 * @return found {@link ProjectAccount} with this email, null otherwise. If
	 *         multiple profiles contain the same email, returns null. returns null
	 *         in case of errors.
	 * 
	 * @throws IllegalArgumentException when email is null
	 */
	ProjectAccount findByEmail(String email);

	/**
	 * Find all {@link ProjectAccount} from a given email
	 * 
	 * @param email : email value to filter on
	 * @return the list of found {@link ProjectAccount} instances . Returns an empty
	 *         list if no result found or in case of errors.
	 * 
	 * @throws IllegalArgumentException when the email is null
	 */
	List<ProjectAccount> findAllByEmail(String email);

	/**
	 * Retrieve a {@link ProjectAccount} from its login.
	 * 
	 * @param login : login to search for
	 * 
	 * @return found {@link ProjectAccount} with this login, null otherwise. If
	 *         multiple profiles contain the same login, returns null. returns null
	 *         in case of errors.
	 * 
	 * @throws IllegalArgumentException when login is null
	 */
	ProjectAccount findByLogin(String login);

	/**
	 * {@inheritDoc}
	 * 
	 * @throws FunctionalAccountCreationException in case of errors
	 */
	@Override
	void create(ProjectAccount projectAccount) throws FunctionalAccountCreationException;

	/**
	 * {@inheritDoc}
	 * 
	 * @throws FunctionalAccountUpdateException in case of errors
	 */
	@Override
	ProjectAccount update(ProjectAccount projectAccount) throws FunctionalAccountUpdateException;

	/**
	 * Implements a logical soft delete by deactivating profile from its id.
	 * 
	 * @param id : id of the profile to delete
	 * 
	 * @throws FunctionalAccountDeleteException Exception thrown when an technical
	 *                                          exception occurs
	 * @throws IllegalArgumentException         Exception thrown when the id
	 *                                          argument is null
	 * 
	 */
	void softDelete(Long id) throws FunctionalAccountDeleteException;

	/**
	 * Implements a logical soft delete by deactivating profile from its externalId.
	 * 
	 * @param externalId : externalId of the profile to delete
	 * 
	 * @throws FunctionalAccountDeleteException Exception thrown when an technical
	 *                                          exception occurs
	 * @throws IllegalArgumentException         Exception thrown when the externalId
	 *                                          argument is null
	 * 
	 */
	void softDeleteByExternalId(Long externalId) throws FunctionalAccountDeleteException;

	/**
	 * Find all concrete enabled ProjectAccount instances associated at this email
	 * or username. This method could return objects of ProjectAccount instance
	 * 
	 * @param emailOrUsername : emailOrUsername instance to search for
	 * @return the list of all matching ProjectAccount. Returns an empty list if no
	 *         result found.
	 */
	List<ProjectAccount> findAllEnabledByEmailOrUsername(String emailOrUsername);

}
