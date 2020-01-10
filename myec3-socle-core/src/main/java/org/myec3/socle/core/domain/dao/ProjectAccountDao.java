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

import org.myec3.socle.core.domain.model.ProjectAccount;

/**
 * This interface define methods to perform specific queries on
 * {@link ProjectAccount} objects). It only defines new specific methods and
 * inherits methods from {@link ResourceDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface ProjectAccountDao extends ResourceDao<ProjectAccount> {

	/**
	 * Retrieve a {@link ProjectAccount} from its email.
	 * 
	 * @param email
	 *            : email to search for
	 * 
	 * @return found {@link ProjectAccount} with this email, null otherwise. If
	 *         multiple profiles contain the same email, returns null. returns
	 *         null in case of errors.
	 */
	ProjectAccount findByEmail(String email);

	/**
	 * Find all {@link ProjectAccount} from a given email
	 * 
	 * @param email
	 *            : email value to filter on
	 * @return the list of found {@link ProjectAccount} instances . Returns an
	 *         empty list if no result found or in case of errors.
	 */
	List<ProjectAccount> findAllByEmail(String email);

	/**
	 * Retrieve a {@link ProjectAccount} from its login.
	 * 
	 * @param login
	 *            : login to search for
	 * 
	 * @return found {@link ProjectAccount} with this login, null otherwise. If
	 *         multiple profiles contain the same login, returns null. returns
	 *         null in case of errors.
	 */
	ProjectAccount findByLogin(String login);

	/**
	 * Find all concrete enabled {@link ProjectAccount} instances associated at
	 * this email or username. This method could return objects of
	 * {@link ProjectAccount} instance
	 * 
	 * @param emailOrUsername
	 *            : emailOrUsername instance to search for
	 * @return the list of all matching {@link ProjectAccount}. Returns an empty
	 *         list if no result found.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<ProjectAccount> findAllEnabledByEmailOrUsername(String emailOrUsername);
}
