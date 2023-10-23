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

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.constants.MyEc3PasswordConstants;
import org.myec3.socle.core.domain.dao.UserDao;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.SviProfile;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.StructureService;
import org.myec3.socle.core.service.SviProfileService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.util.MyEc3PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Concrete Servicve implementation providing methods specific to {@link User}
 * objects. These methods complete or override parent methods from
 * {@link ResourceServiceImpl} services
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Service("userService")
public class UserServiceImpl extends ResourceServiceImpl<User, UserDao> implements UserService {

	// Webapp constants
	/*
	 * private static final String GU_BUNDLE_NAME = "webapp"; private static final
	 * ResourceBundle GU_BUNDLE = ResourceBundle .getBundle(GU_BUNDLE_NAME);
	 */

	public static int expirationTimePassword = 365;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects ({@link AgentProfile} and {@link EmployeeProfile} )
	 */
	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link SviProfile} objects
	 */
	@Autowired
	@Qualifier("sviProfileService")
	private SviProfileService sviProfileService;

	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	@Autowired
	@Qualifier("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	@Autowired
	@Qualifier("structureService")
	private StructureService structureService;

	private final Log logger = LogFactory.getLog(UserServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByUsername(String username) {
		Assert.notNull(username, "username is mandatory and cannot be null");
		return this.dao.findByUsername(username);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByFunctionalAccountId(Long id) {
		Assert.notNull(id, "functional account id is mandatory and canot be null");
		return this.dao.findByFunctionalAccountId(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(User user) {

		Assert.notNull(user, "user is mandatory and canot be null");

		// Set unique name for user
		if (user.getName() != null) {
			user.setName("User " + user.getName() + Calendar.getInstance().getTimeInMillis());
		}

		// Create sviProfile for user
		SviProfile sviProfile = new SviProfile();
		this.sviProfileService.create(sviProfile);

		// Set the sviProfile to the user
		user.setSviProfile(sviProfile);

		// create user
		super.create(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generatePassword() {
		return MyEc3PasswordUtils.generatePassword();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(User user) {
		Assert.notNull(user, "user is mandatory, null value is forbidden");

		user.setProfiles(profileService.findAllProfilesByUser(user));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(User user) {
		Assert.notNull(user, "user is mandatory, null value is forbidden");

		logger.info("cleaning collections of user");
		user.setProfiles(new ArrayList<Profile>());
	}

	public Long getConnectionInfosRelatedUser(ConnectionInfos connectionInfos) {

		Long userId = this.dao.findUserIdByConnectionInfos(connectionInfos);
		return userId;
	}

	public List<User> findUsersByCertificate(String certificate) {
		return this.dao.findUsersByCertificate(certificate);
	}

	public Structure findUserOrganismStructure(User user) {
		Assert.notNull(user, "user is mandatory, null value is forbidden");

		// get the Profile
		Profile userProfile = this.profileService.findByUserId(user.getId());

		if (userProfile != null) {
			// get the AgentProfile
			AgentProfile userAgentProfile = this.agentProfileService.findOne(userProfile.getId());

			if (userAgentProfile != null) {
				// get the OrganismDepartment
				OrganismDepartment userOrganismDepartment = this.organismDepartmentService
						.findOne(userAgentProfile.getOrganismDepartment().getId());
				// get the Organism

				if (userOrganismDepartment != null) {
					Organism userOrganism = this.organismService.findOne(userOrganismDepartment.getOrganism().getId());

					if (userOrganism != null) {
						// get the Structure
						Structure userStructure = this.structureService.findOne(userOrganism.getId());

						if (userStructure != null) {
							// return the structure
							return userStructure;
						}
					}
				}
			}
		}
		// one of the "find" returns null, we can't find structure
		return null;
	}

}
