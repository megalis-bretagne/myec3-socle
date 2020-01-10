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
package org.myec3.socle.core.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements a strategy to perform authentication.
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

	// Logger
	private static final Log logger = LogFactory.getLog(UserDetailsServiceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects ({@link AgentProfile} and {@link EmployeeProfile} )
	 */
	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	/**
	 * Get the user object corresponding to the given login, check the password
	 * stored into the secured session context and grant corresponding rights for
	 * the user.
	 * 
	 * @param uid : user login
	 * @return an UserDetails object representing the authenticated user and his
	 *         rights if the authentication is successfull.
	 */
	@Override
	@Transactional(readOnly = false)
	public UserDetails loadUserByUsername(String uid) {

		logger.debug("Trying to Load the User with uid: " + uid
				+ " and password [PROTECTED] from database and LDAP directory");
		try {
			logger.debug("Searching the user with login: " + uid + " in database");

			Long id = null;
			Profile profile = null;
			try {
				id = new Long(uid);
				profile = this.profileService.findOne(id);
			} catch (Exception e) {
				// When trying to re-authenticate user, username is used insteadOf SSO id
				profile = this.profileService.findByUsername(uid);
			}

			if (null == profile) {
				logger.error("User with login: " + id + " not found in database. Authentication failed for user " + id);
				throw new UsernameNotFoundException("user not found in database");
			}

			if (!profile.isEnabled()) {
				logger.error("User with login: " + id + " is disabled. Authentication failed for user " + id);
				throw new UsernameNotFoundException("user disabled");
			}

			logger.debug("user with login: " + id + " found in database");

			profile = this.profileService.addRoles(profile);

			this.profileService.update(profile);

			logger.debug("user with login: " + id + " authenticated");

			return profile;

		} catch (DataAccessException e) {
			logger.error("Cannot retrieve Data from Database server : " + e.getMessage()
					+ ". Authentication failed for user " + uid);
			throw new UsernameNotFoundException("user not found", e);
		}
	}

}
