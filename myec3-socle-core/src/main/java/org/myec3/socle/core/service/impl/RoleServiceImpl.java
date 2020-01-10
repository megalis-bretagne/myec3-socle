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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.domain.dao.RoleDao;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to {@link Role}
 * objects. These methods complete or override parent methods from
 * {@link ResourceServiceImpl}
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("roleService")
public class RoleServiceImpl extends ResourceServiceImpl<Role, RoleDao> implements RoleService {

	// Logger
	private static final Log logger = LogFactory.getLog(RoleServiceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects ({@link AgentProfile} and {@link EmployeeProfile})
	 */
	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Role> findAllRoleByProfile(Profile profile) {
		Assert.notNull(profile, "profile is mandatory. null value is forbiden");

		return this.dao.findAllRoleByProfile(profile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Role> findAllRoleByApplication(Application application) {
		Assert.notNull(application, "application is mandatory. null value is forbiden");

		return this.dao.findAllRoleByApplication(application);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Role> findAllRoleByProfileTypeAndApplication(ProfileType profileType, Application application) {
		Assert.notNull(profileType, "profile type is mandatory. null value is forbiden");
		Assert.notNull(application, "application is mandatory. null value is forbiden");

		return this.dao.findAllRoleByProfileTypeAndApplication(profileType, application, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Role> findAllRoleByProfileTypeAndApplicationWithoutHidden(ProfileType profileType,
			Application application) {
		Assert.notNull(profileType, "profile type is mandatory. null value is forbiden");
		Assert.notNull(application, "application is mandatory. null value is forbiden");

		return this.dao.findAllRoleByProfileTypeAndApplication(profileType, application, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role findAdminRoleByProfileTypeAndApplication(ProfileType profileType, Application application) {
		Assert.notNull(profileType, "profile type is mandatory. null value is forbiden");
		Assert.notNull(application, "application is mandatory. null value is forbiden");

		return this.dao.findAdminRoleByProfileTypeAndApplication(profileType, application);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role findBasicRoleByProfileTypeAndApplication(ProfileType profileType, Application application) {
		Assert.notNull(profileType, "profile type is mandatory. null value is forbiden");
		Assert.notNull(application, "application is mandatory. null value is forbiden");

		return this.dao.findBasicRoleByProfileTypeAndApplication(profileType, application);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Role> findAllRoleByProfileAndApplication(Profile profile, Application application) {
		Assert.notNull(profile, "profile is mandatory. null value is forbiden");
		Assert.notNull(application, "application is mandatory. null value is forbiden");

		return this.dao.findAllRoleByProfileAndApplication(profile, application, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Role> findAllRoleByProfileAndApplicationWithoutHidden(Profile profile, Application application) {
		Assert.notNull(profile, "profile is mandatory. null value is forbiden");
		Assert.notNull(application, "application is mandatory. null value is forbiden");

		return this.dao.findAllRoleByProfileAndApplication(profile, application, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(Role role) {
		Assert.notNull(role, "role is mandatory. null value is forbiden");

		logger.info("populate Role's collections");
		role.setProfiles(profileService.findAllProfilesByRole(role));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(Role role) {
		Assert.notNull(role, "role is mandatory. null value is forbiden");

		logger.info("cleaning collections of role");
		role.setProfiles(new ArrayList<Profile>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Role> findAllByCriteria(String label, String name, Application application) {
		return this.dao.findAllByCriteria(label, name, application);
	}
}
