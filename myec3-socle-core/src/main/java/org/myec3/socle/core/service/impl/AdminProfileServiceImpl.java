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

import org.myec3.socle.core.domain.dao.AdminProfileDao;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.AdminProfileService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link AdminProfile} objects. These methods complete or override parent
 * methods from {@link ProfileServiceImpl} or {@link ResourceServiceImpl}
 * services
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 */
@Service("adminProfileService")
public class AdminProfileServiceImpl extends GenericProfileServiceImpl<AdminProfile, AdminProfileDao>
		implements AdminProfileService {

	/**
	 * Business service providing methods and specifics operations on {@link User}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	private UserService userService;

	/**
	 * Business service providing methods and specifics operations on {@link Role}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	@Override
	public void populateCollections(AdminProfile adminProfile) {
		Assert.notNull(adminProfile, "Cannot populate collections of AdminProfile : admin cannot be null");
		this.userService.populateCollections(adminProfile.getUser());
		adminProfile.setRoles(this.roleService.findAllRoleByProfile(adminProfile));
	}

	@Transactional(readOnly = false)
	@Override
	public void softDelete(Long id) throws ProfileDeleteException {
		// TODO : to implement
	}

	@Override
	public void create(AdminProfile entity) {
		super.create(entity);
		saveProfileInKeycloak(entity);
	}

	@Override
	public AdminProfile update(AdminProfile entity) {
		AdminProfile updatedEntity = super.update(entity);
		saveProfileInKeycloak(updatedEntity);
		return updatedEntity;
	}
}
