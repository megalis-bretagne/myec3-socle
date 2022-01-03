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
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myec3.socle.core.domain.dao.EmployeeProfileDao;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.service.exceptions.ProfileCreationException;
import org.myec3.socle.core.service.exceptions.ProfileDeleteException;
import org.myec3.socle.core.service.exceptions.ProfileUpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to
 * {@link EmployeeProfile} objects. These methods complete or override parent
 * methods from profile or Resource services
 * 
 * @author Matthieu Dubromez <matthieu.dubromez@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
@Service("employeeProfileService")
public class EmployeeProfileServiceImpl extends GenericProfileServiceImpl<EmployeeProfile, EmployeeProfileDao>
		implements EmployeeProfileService {

	private static final Log logger = LogFactory.getLog(EmployeeProfileServiceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects. The concrete service implementation is injected by
	 * Spring container
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects. The concrete service implementation is injected by Spring container
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProfileType} objects. The concrete service implementation is injected
	 * by Spring container
	 */
	@Autowired
	@Qualifier("profileTypeService")
	private ProfileTypeService profileTypeService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EmployeeProfile> findAllEmployeeProfilesByCompanyDepartment(CompanyDepartment companyDepartment) {
		Assert.notNull(companyDepartment, "companyDepartment is mandatory. null value is forbidden");

		return this.dao.findAllEmployeeByCompanyDepartment(companyDepartment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EmployeeProfile> findAllEmployeeProfilesByEstablishment(Establishment establishment) {
		Assert.notNull(establishment, "establishment is mandatory. null value is forbidden");

		return this.dao.findAllByEstablishment(establishment);
	}

	@Override
	public List<EmployeeProfile> findAllEmployeeProfilesByCompanyAndApplication(Company company, Application application) {
		Assert.notNull(company, "company is mandatory. null value is forbidden");
		Assert.notNull(application, "application is mandatory. null value is forbidden");
		return this.dao.findAllEmployeeProfilesByCompanyAndApplication(company,application);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void create(EmployeeProfile employeeProfile) throws ProfileCreationException {
		Assert.notNull(employeeProfile, "Cannot create employeeProfile: employeeProfile cannot be null");
		Assert.isNull(employeeProfile.getId(),
				"Cannot create employeeProfile that have already be persisted (id not null)");

		// Set unique name for employee
		if (employeeProfile.getName() != null) {
			employeeProfile.setName("Employee " + employeeProfile.getName() + Calendar.getInstance().getTimeInMillis());
		}

		// Adding profileType
		ProfileType profileType = this.profileTypeService.findByValue(ProfileTypeValue.EMPLOYEE);
		employeeProfile.setProfileType(profileType);

		List<Role> employeeRoles = new ArrayList<>();

		// If employee has no roles we set at least basic roles
		if (employeeProfile.getRoles().isEmpty()) {
			List<Application> applications = employeeProfile.getCompanyDepartment().getCompany().getApplications();

			Role role = null;
			for (Application application : applications) {
				role = this.roleService.findBasicRoleByProfileTypeAndApplication(employeeProfile.getProfileType(),
						application);
				if (role != null) {
					employeeRoles.add(role);
				}
			}
			employeeProfile.setRoles(employeeRoles);
		}

		try {
			super.create(employeeProfile);
			if (employeeProfile.getAlfUserName() == null) {
				employeeProfile.setAlfUserName(employeeProfile.getId() + "@"
						+ employeeProfile.getCompanyDepartment().getCompany().getTenantIdentifier());
				super.update(employeeProfile);
			}
		} catch (RuntimeException re) {
			throw new ProfileCreationException("Cannot create Employee " + employeeProfile, re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public void softDelete(Long id) throws ProfileDeleteException {
		// validate parameters
		Assert.notNull(id, "profile id argument cannot be null");

		// just soft delete employee
		try {
			EmployeeProfile employeeProfile = this.findOne(id);
			employeeProfile = deactivateProfile(employeeProfile);
			// delete establishment relation for establishment delete
			employeeProfile.setEstablishment(null);
			update(employeeProfile);
		} catch (RuntimeException re) {
			throw new ProfileDeleteException("Cannot perform soft delete Employee with id " + id, re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	@Override
	public EmployeeProfile update(EmployeeProfile employeeProfile) throws ProfileUpdateException {
		Assert.notNull(employeeProfile, "Cannot update employeeProfile: employeeProfile cannot be null");

		try {
			// validate bean. If this is not performed here, exception is thrown
			// and ignored !
			validateResource(employeeProfile);

			ProfileType profileType = profileTypeService.findByValue(ProfileTypeValue.EMPLOYEE);

			employeeProfile.setProfileType(profileType);

			// Get the user from the database
			User foundUser = userService.findOne(employeeProfile.getUser().getId());

			// We check if the password have been updated
			if (employeeProfile.getUser().getNewPassword() != null) {
				// We set the new password into the old password
				employeeProfile.getUser().setPassword(employeeProfile.getUser().getNewPassword());
			}

			// reattach user object to the agent else the user is not updated
			foundUser.reattach(employeeProfile.getUser());
			employeeProfile.setUser(foundUser);

			return super.update(employeeProfile);
			// return resourceDao.merge(employeeProfile);
		} catch (RuntimeException re) {
			throw new ProfileUpdateException("Cannot update Employee " + employeeProfile, re);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateCollections(EmployeeProfile employeeProfile) {
		Assert.notNull(employeeProfile,
				"Cannot populate collections of employeeProfile: employeeProfile cannot be null");
		logger.info("populate EmployeeProfile's collections");
		User clonedUser = null;
		try {
			clonedUser = (User) employeeProfile.getUser().clone();
			userService.populateCollections(clonedUser);
		} catch (CloneNotSupportedException e) {
			logger.error("Cannot clone User. " + employeeProfile.getUser(), e);
		}
		employeeProfile.setRoles(roleService.findAllRoleByProfile(employeeProfile));
		companyService.populateCollections(employeeProfile.getCompanyDepartment().getCompany());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanCollections(EmployeeProfile employeeProfile) {
		// validate parameters
		Assert.notNull(employeeProfile, "Cannot clean collections of employeeProfile: employeeProfile cannot be null");
		logger.info("cleaning collections of employeeProfile");

		userService.cleanCollections(employeeProfile.getUser());
		employeeProfile.setRoles(new ArrayList<>());
		companyService.cleanCollections(employeeProfile.getCompanyDepartment().getCompany());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EmployeeProfile> findAllGuAdministratorByCompanyId(Long companyId) {
		Assert.notNull(companyId, "companyId is mandatory. null value is forbidden");

		return this.dao.findAllGuAdministratorByCompanyId(companyId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EmployeeProfile> findAllGuAdministratorEnabledByCompanyId(Long companyId) {
		Assert.notNull(companyId, "companyId is mandatory. null value is forbidden");
		return this.dao.findAllGuAdministratorEnabledByCompanyId(companyId);
	}

	@Override
	public EmployeeProfile findEmployeeProfileByIdSdm(long idSdm) {
		return this.dao.findEmployeeProfileByIdSdm(idSdm);
	}
}
