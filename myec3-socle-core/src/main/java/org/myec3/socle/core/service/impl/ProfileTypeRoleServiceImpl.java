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

import java.util.List;

import org.myec3.socle.core.domain.dao.ProfileTypeRoleDao;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;
import org.myec3.socle.core.service.ProfileTypeRoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link ProfileTypeRole} objects. These methods complete or override parent
 * methods from {@link ProfileServiceImpl} or {@link ResourceServiceImpl}
 * services
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Service("profileTypeRoleService")
public class ProfileTypeRoleServiceImpl extends AbstractGenericServiceImpl<ProfileTypeRole, ProfileTypeRoleDao>
		implements ProfileTypeRoleService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProfileTypeRole> findAllProfileTypeRoleByProfileType(ProfileType profileType) {
		Assert.notNull(profileType, "profileType is mandatory. null value is forbidden");

		return this.dao.findAllProfileTypeRoleByProfileType(profileType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProfileTypeRole> findAllProfileTypeRoleByRole(Role role) {
		Assert.notNull(role, "role is mandatory. null value is forbidden");

		return this.dao.findAllProfileTypeRoleByRole(role);
	}

}
