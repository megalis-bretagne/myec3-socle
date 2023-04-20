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

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.FunctionalAccount;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.User;

/**
 * Interface defining Business Services methods and providing {@link User}
 * specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
public interface UserService extends ResourceService<User> {

    /**
     * Find a {@link User} by its username. The search is performed on an exact
     * matching of the username.
     * 
     * @param username
     *            : username to search on.
     * @return the complete user object with this username. return null in case
     *         of error, if there is several users with this username or if
     *         there is no result.
     * @throws IllegalArgumentException
     *             if username is null.
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
     * @throws IllegalArgumentException
     *             if id is null.
     */
    User findByFunctionalAccountId(Long id);

    /**
     * Generate a simple password
     *
     * @return random alphanumeric password
     */
    String generatePassword();

    /**
     * This method allows to populate user's collections
     * 
     * @param user
     *            : user object that we want to populate
     * @throws IllegalArgumentException
     *             if user is null.
     */
    void populateCollections(User user);

    /**
     * This method allows to clean user's collections (before marshalling object
     * for send the xml into the JMS message)
     * 
     * @param user
     *            : user to clean
     * 
     * @throws IllegalArgumentException
     *             if user is null
     */
    void cleanCollections(User user);
    
    Long getConnectionInfosRelatedUser(ConnectionInfos connectionInfos);
    
    List<User> findUsersByCertificate(String certificate);
    
    Structure findUserOrganismStructure(User user);

}
