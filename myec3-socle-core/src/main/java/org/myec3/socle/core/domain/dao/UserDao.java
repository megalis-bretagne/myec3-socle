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

import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.FunctionalAccount;
import org.myec3.socle.core.domain.model.User;

import java.util.List;

/**
 * DAO interface for {@link User} objects. This interface defines global methods
 * that could be called on {@link User}
 *
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface UserDao extends ResourceDao<User> {

	/**
	 * Find a {@link User} by its username. The search is performed on an exact
	 * matching of the username.
	 *
	 * @param username
	 *            : username to search on.
	 * @return the complete user object with this username. return null in case
	 *         of error, if there is several users with this username or if
	 *         there is no result.
	 */
	User findByUsername(String username);

	/**
	 * Find a {@link User} user that is associated to a
	 * {@link FunctionalAccount} by its id
	 *
	 * @param id
	 *            : functional id to search on
	 * @return the complete user object associated to this functional account
	 *         id. return null in case of error, if there is several users with
	 *         this functional account id or if there is no result
	 */
	User findByFunctionalAccountId(Long id);

	Long findUserIdByConnectionInfos(ConnectionInfos connectionInfos);

	List<User> findUsersByCertificate(String certificate);
}