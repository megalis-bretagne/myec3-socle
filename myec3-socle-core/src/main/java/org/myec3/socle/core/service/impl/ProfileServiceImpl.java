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
import java.util.Collection;
import java.util.List;

import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.dao.ProfileDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.RoleProfile;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to {@link Profile}
 * objects. These methods complete or override parent methods from Resource
 * services
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 */
@Service("profileService")
public class ProfileServiceImpl extends GenericProfileServiceImpl<Profile, ProfileDao> implements ProfileService {

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void softDelete(Long id) throws ProfileDeleteException {
		throw new UnsupportedOperationException("Cannot perform softDelete on abstract class Profile");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Profile> findAllProfilesByProfileType(ProfileType profileType) {
		// validate parameters
		Assert.notNull(profileType, "profileType is mandatory. null value is forbidden.");

		return this.dao.findAllProfilesByProfileType(profileType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Profile addRoles(Profile profile) {
		// validate parameters
		Assert.notNull(profile, "profile is mandatory. null value is forbidden.");

		Collection<GrantedAuthority> arrayAuths = new ArrayList<GrantedAuthority>();
		arrayAuths.add(new SimpleGrantedAuthority("ROLE_AUTH"));

		List<Role> roles = roleService.findAllRoleByProfileAndApplication(profile,
				this.applicationService.findByName(MyEc3ApplicationConstants.GU));
		for (Role role : roles) {
			if (profile.isAdmin()) {
				if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_SUPER_ADMIN.toString())) {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
				} else if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_ADMIN.toString())) {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
				}
			} else if (profile.isAgent()) {
				if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_MANAGER_AGENT.toString())) {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_MANAGER_AGENT"));
				} else if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_APPLICATION_MANAGER_AGENT.toString())) {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_APPLICATION_MANAGER_AGENT"));
				} else if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_AGENT.toString())) {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_AGENT"));
				} else {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_DEFAULT"));
				}
			} else if (profile.isEmployee()) {
				if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_MANAGER_EMPLOYEE.toString())) {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_MANAGER_EMPLOYEE"));
				} else if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_EMPLOYEE.toString())) {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
				} else {
					arrayAuths.add(new SimpleGrantedAuthority("ROLE_DEFAULT"));
				}
			} else {
				arrayAuths.add(new SimpleGrantedAuthority("ROLE_DEFAULT"));
			}
		}
		profile.setAuthorities(arrayAuths);

		return profile;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Profile findByUsername(String username) {
		return this.dao.findByUsername(username);
	}

	@Override
	public Profile findByUserId(Long userId) {
		return this.dao.findByUserId(userId);
	}

}
