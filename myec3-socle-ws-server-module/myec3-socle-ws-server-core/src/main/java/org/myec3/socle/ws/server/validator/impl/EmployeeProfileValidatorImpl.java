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
package org.myec3.socle.ws.server.validator.impl;

import java.util.ArrayList;
import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Person;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.enums.RoleProfile;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.PersonService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.sync.api.ClassType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestSyntaxException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.core.sync.api.exception.WebResourceRelationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * Implementation of the validator that make a validation on
 * {@link EmployeeProfile} during REST requests.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("employeeProfileValidator")
public class EmployeeProfileValidatorImpl extends ResourceValidatorManagerImpl<EmployeeProfile> {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link CompanyDepartment} objects
	 */
	@Autowired
	@Qualifier("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Autowired
	@Qualifier("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on {@link Person}
	 * objects
	 */
	@Autowired
	@Qualifier("personService")
	private PersonService personService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProfileType} objects
	 */
	@Autowired
	@Qualifier("profileTypeService")
	private ProfileTypeService profileTypeService;

	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateGet(Long resourceId) {
		// Check param
		Assert.notNull(resourceId, "[validateGet] resourceId can't be null");
		logger.debug("[validateGet] of employee with id : " + resourceId);
		// Check if an employee with this id exists into the database
		if (!this.validateResourceExists(resourceId)) {
			throw new WebResourceNotFoundException(resourceId, MethodType.GET,
					"No employee exist with this id : " + resourceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateCreate(EmployeeProfile resource) {
		// Check param
		Assert.notNull(resource, "[validateCreate] employee can't be null");
		logger.debug("[validateCreate] of employee : " + resource.getName());

		// Validate bean content
		this.validateBeanContent(resource, MethodType.POST);

		// We check that the user's password is not null
		logger.debug("Checking that the user's password of the employee sent is not null");
		if (resource.getUser().getPassword() == null) {
			throw new WebRequestSyntaxException(resource, MethodType.POST, "Employee's password can't be null",
					"externalId", resource.getExternalId().toString());
		}

		// Check that id of employee equal 0
		logger.debug("Checking that id of employee sent equal 0");
		if (!(resource.getId().equals(new Long(0)))) {
			throw new WebRequestSyntaxException(resource, MethodType.POST,
					"Id of employeeProfile sent not equals 0 : " + resource.getId(), "externalId",
					resource.getExternalId().toString());
		}

		// Check that externalId of employee equal 0
		logger.debug("Checking that externalId of employee sent equal 0");
		if (!(resource.getExternalId().equals(new Long(0)))) {
			throw new WebRequestSyntaxException(resource, MethodType.POST,
					"ExternalId of employeeProfile sent not equals 0", "externalId",
					resource.getExternalId().toString());
		}

		// get companyDepartment of the employee
		// check that companyDepartment Id = 0
		logger.debug("Checking that companyDepartment's id of employee sent equal 0");
		if (!(resource.getCompanyDepartment().getId().equals(new Long(0)))) {
			throw new WebRequestSyntaxException(resource, MethodType.POST,
					"CompanyDepartment's Id of employeeProfile sent not equals 0 : "
							+ resource.getCompanyDepartment().getId(),
					"externalId", resource.getExternalId().toString());
		}

		// check that employee's companyDepartment's externalId = 0
		logger.debug("Checking that companyDepartment's externalId of employee sent equal 0");
		if (!(resource.getCompanyDepartment().getExternalId().equals(new Long(0)))) {
			throw new WebRequestSyntaxException(resource, MethodType.POST,
					"CompanyDepartment's ExternalId of employeeProfile sent not equals 0 : "
							+ resource.getCompanyDepartment().getExternalId(),
					"externalId", resource.getExternalId().toString());
		}

		// Check that user's externalId of employee equals 0
		logger.debug("Checking that user's externalId of employee sent equal 0");
		if (!resource.getUser().getExternalId().equals(new Long(0))) {
			throw new WebRequestSyntaxException(resource, MethodType.POST,
					"User's ExternalId of employeeProfile sent not equals 0", "externalId",
					resource.getExternalId().toString());
		}

		// Check that user's id of employee equals 0
		logger.debug("Checking that user's id of employee sent equal 0");
		if (!resource.getUser().getId().equals(new Long(0))) {
			throw new WebRequestSyntaxException(resource, MethodType.POST,
					"User's Id of employeeProfile sent not equals 0", "externalId",
					resource.getExternalId().toString());
		}

		// Check employee's role
		this.validEmployeeRole(resource, MethodType.POST);

		// Check email and username
		this.validEmailAndUserName(resource, MethodType.POST);

		// Check company of the employee
		this.validCompany(resource, MethodType.POST);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdate(EmployeeProfile resource, Long resourceId, EmployeeProfile foundEmployee) {
		// Check params
		Assert.notNull(resource, "[validateUpdate] employee can't be null");
		Assert.notNull(resourceId, "[validateUpdate] resourceId can't be null");
		Assert.notNull(foundEmployee, "[validateUpdate] foundEmployee can't be null");

		logger.debug("[validateUpdate] of employee with id = " + resourceId);

		User foundUser = null;

		// Validate bean content
		logger.debug("Validating bean content of employee sent");
		this.validateBeanContent(resource, MethodType.PUT);

		if (!(resource.getId().equals(new Long(-1)))) {
			// Check that resource's id is equal at the resourceId
			logger.debug("Checking that resource's id is equal at the resourceId");
			if (!(resource.getId().equals(resourceId))) {
				throw new WebRequestSyntaxException(resource, MethodType.PUT,
						"Id sent not corresponding at the employeeProfile's id sent : " + resource.getId(),
						"externalId", resource.getExternalId().toString());
			}

			// We check that the user of the employee exists
			logger.debug("Checking that the user of the employee exists");
			foundUser = userService.findOne(resource.getUser().getId());
			if (foundUser == null) {
				throw new WebResourceNotFoundException(resource, MethodType.PUT,
						"No user exists with this Id : " + resource.getUser().getId(), "externalId",
						resource.getExternalId().toString());
			}

			// If email have been modified we check that the employee's new
			// email not already exists into the database (into EmployeeProfile
			// table)
			if (!(resource.getEmail().equals(foundEmployee.getEmail()))) {
				logger.debug("Checking that the employee's new email not already exists into the database");
				// we check that the employee's new email not already exists
				// into the database
				this.validateEmailUniqueConstraints(resource, resource.getEmail(), MethodType.PUT);
			}

			// If the username have been modified we check that the employee's
			// username not already exists into the database (table User and
			// ProjectAccount)
			if (!(resource.getUser().getUsername().equals(foundUser.getUsername()))) {
				logger.debug("Checking  that the employee's username not already exists into the database");
				this.validateUsernameUniqueContraints(resource, resource.getUser().getUsername(), MethodType.PUT);
			}

			// Check that the companyDepartment of the employee sent exists
			logger.debug("Checking that the companyDepartment of the employee sent exists");
			if (companyDepartmentService.findOne(resource.getCompanyDepartment().getId()) == null) {
				// No Company Department exists for this employee
				logger.error("[validateUpdate] of employee"
						+ " throw an WebResourceRelationNotFoundException : No company department exists with id : "
						+ resource.getCompanyDepartment().getId());

				throw new WebResourceRelationNotFoundException(resource,
						resource.getCompanyDepartment().getExternalId(), MethodType.PUT,
						"No company department exists with this Id : " + resource.getCompanyDepartment().getId(),
						"externalId", resource.getExternalId().toString(), ClassType.COMPANY_DEPARTMENT);
			}

			// Check that the company of the employee sent exists
			logger.debug("Checking that the company of the employee sent exists");
			if (companyService.findOne(resource.getCompanyDepartment().getCompany().getId()) == null) {
				// No Company exists for this employee
				logger.error("[validateUpdate] of employee"
						+ " throw an WebResourceRelationNotFoundException : No company exists with Id : "
						+ resource.getCompanyDepartment().getCompany().getId());

				throw new WebResourceRelationNotFoundException(resource,
						resource.getCompanyDepartment().getCompany().getId(), MethodType.PUT,
						"No company exists with this Id : " + resource.getCompanyDepartment().getCompany().getId(),
						"externalId", resource.getExternalId().toString(), ClassType.COMPANY);
			}

			// If the employee has an parentDepartment we must check if it
			// exists into the database
			logger.debug("Checking if the employee has an parentDepartment");
			if (resource.getCompanyDepartment().getParentDepartment() != null) {
				// Check if parent department exists into the database
				if (this.companyDepartmentService
						.findOne(resource.getCompanyDepartment().getParentDepartment().getId()) == null) {
					throw new WebResourceRelationNotFoundException(resource,
							resource.getCompanyDepartment().getParentDepartment().getId(), MethodType.PUT,
							"No parent department exists with this Id : "
									+ resource.getCompanyDepartment().getParentDepartment().getId(),
							"externalId", resource.getExternalId().toString(), ClassType.COMPANY_DEPARTMENT);
				}

				// check if the company contained into the parent department
				// exists into the database
				if (this.companyService
						.findOne(resource.getCompanyDepartment().getParentDepartment().getCompany().getId()) == null) {
					throw new WebResourceRelationNotFoundException(resource,
							resource.getCompanyDepartment().getParentDepartment().getCompany().getId(), MethodType.PUT,
							"No company exists with this Id : "
									+ resource.getCompanyDepartment().getParentDepartment().getCompany().getId(),
							"externalId", resource.getExternalId().toString(), ClassType.COMPANY);
				}
			}
		} else {

			// We check that the user of the employee exists
			logger.debug("Checking that the user of the employee exists");
			foundUser = userService.findByExternalId(resource.getUser().getExternalId());
			if (foundUser == null) {
				throw new WebResourceNotFoundException(resource, MethodType.PUT,
						"No user exists with this Id : " + resource.getUser().getExternalId(), "externalId",
						resource.getExternalId().toString());
			}

			// If email have been modified we check that the employee's new
			// email not already exists into the database (table Profile and
			// ProjectAccount)
			if (!(resource.getEmail().equals(foundEmployee.getEmail()))) {
				logger.debug("Checking that the employee's new email not already exists into the database");
				// we check that the employee's new email not already exists
				// into the database
				this.validateEmailUniqueConstraints(resource, resource.getEmail(), MethodType.PUT);
			}

			// If the username have been modified we check that the employee's
			// username not already exists into the database (table User and
			// ProjectAccount)
			if (!(resource.getUser().getUsername().equals(foundUser.getUsername()))) {
				logger.debug("Checking  that the employee's username not already exists into the database");
				this.validateUsernameUniqueContraints(resource, resource.getUser().getUsername(), MethodType.PUT);
			}

			// Check that the companyDepartment of the employee sent exists
			logger.debug("Checking that the companyDepartment of the employee sent exists");
			if (companyDepartmentService.findByExternalId(resource.getCompanyDepartment().getExternalId()) == null) {
				// No Company Department exists for this employee
				logger.error("[validateUpdate] of employee"
						+ " throw an WebResourceRelationNotFoundException : No company department exists with id : "
						+ resource.getCompanyDepartment().getExternalId());

				throw new WebResourceRelationNotFoundException(resource,
						resource.getCompanyDepartment().getExternalId(), MethodType.PUT,
						"No company department exists with this Id : "
								+ resource.getCompanyDepartment().getExternalId(),
						"externalId", resource.getExternalId().toString(), ClassType.COMPANY_DEPARTMENT);
			}

			// Check that the company of the employee sent exists
			logger.debug("Checking that the company of the employee sent exists");
			if (companyService.findByExternalId(resource.getCompanyDepartment().getCompany().getExternalId()) == null) {
				// No Company exists for this employee
				logger.error("[validateUpdate] of employee"
						+ " throw an WebResourceRelationNotFoundException : No company exists with Id : "
						+ resource.getCompanyDepartment().getCompany().getExternalId());

				throw new WebResourceRelationNotFoundException(resource,
						resource.getCompanyDepartment().getCompany().getId(), MethodType.PUT,
						"No company exists with this Id : "
								+ resource.getCompanyDepartment().getCompany().getExternalId(),
						"externalId", resource.getExternalId().toString(), ClassType.COMPANY);
			}

			// If the employee has an parentDepartment we must check if it
			// exists into the database
			logger.debug("Checking if the employee has an parentDepartment");
			if (resource.getCompanyDepartment().getParentDepartment() != null) {
				// Check if parent department exists into the database
				if (this.companyDepartmentService.findByExternalId(
						resource.getCompanyDepartment().getParentDepartment().getExternalId()) == null) {
					throw new WebResourceRelationNotFoundException(resource,
							resource.getCompanyDepartment().getParentDepartment().getExternalId(), MethodType.PUT,
							"No parent department exists with this Id : "
									+ resource.getCompanyDepartment().getParentDepartment().getExternalId(),
							"externalId", resource.getExternalId().toString(), ClassType.COMPANY_DEPARTMENT);
				}

				// check if the company contained into the parent department
				// exists into the database
				if (this.companyService.findByExternalId(
						resource.getCompanyDepartment().getParentDepartment().getCompany().getExternalId()) == null) {
					throw new WebResourceRelationNotFoundException(resource,
							resource.getCompanyDepartment().getParentDepartment().getCompany().getExternalId(),
							MethodType.PUT,
							"No company exists with this Id : " + resource.getCompanyDepartment().getParentDepartment()
									.getCompany().getExternalId(),
							"externalId", resource.getExternalId().toString(), ClassType.COMPANY);
				}
			}
		}

		// IN ALL CASES WE MUST CHECK
		// We test if the employee have at least one role
		logger.debug("Checking that the employee has at least one role");
		List<Role> listRoles = resource.getRoles();
		if (listRoles.size() < 1) {
			throw new WebRequestSyntaxException(resource, MethodType.PUT, "Employee must have at least one role",
					"externalId", resource.getExternalId().toString());
		}

		// Check if the password is correct
		logger.debug("Checking if employee's password is correct");
		if (resource.getUser().getNewPassword() != null) {

			String enteredOldPassword = resource.getUser().getPassword();
			String storedPassword = foundUser.getPassword();

			// Test with password encrypted with Scrypt encryption and SHA1
			// encrypted
			if (!userService.isPasswordOk(enteredOldPassword, storedPassword)) {
				// the value of old password is not correct
				throw new WebRequestSyntaxException(resource, MethodType.PUT,
						"Employee's old password is not correct - employee's id : " + resource.getId(), "externalId",
						resource.getExternalId().toString());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdateCreation(EmployeeProfile resource) {
		// Check param
		Assert.notNull(resource, "[validateUpdateCreation] Employee can't be null");
		logger.debug("[validateUpdateCreation] of employee with name = " + resource.getName());

		// Validate bean content
		this.validateBeanContent(resource, MethodType.PUT);

		// We check that the user's password is not null
		logger.debug("Checking that the user's password of the employee sent is not null");
		if (resource.getUser().getPassword() == null) {
			throw new WebRequestSyntaxException(resource, MethodType.PUT,
					"Employee's password can't be null " + resource.getId(), "externalId",
					resource.getExternalId().toString());
		}

		// Check employee's role
		this.validEmployeeRole(resource, MethodType.PUT);

		// Check email and username
		this.validEmailAndUserName(resource, MethodType.PUT);

		// Check company of the employee
		this.validCompany(resource, MethodType.PUT);
	}

	/**
	 * Populate Employee's company's collections
	 * 
	 * @param company : the employee's company to populate
	 */
	public void populateCompanyCollections(Company company) {
		// Check param
		Assert.notNull(company, "[populateCompanyCollections] Employee's company can't be null");
		logger.debug("[populateCompanyCollections] of employee's company with id = " + company.getId()
				+ " and ExternalId = " + company.getExternalId());
		company.setResponsibles(personService.findAllPersonByCompany(company));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareResource(EmployeeProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[prepareResource] employee can't be null");
		Assert.notNull(methodType, "[prepareResource] methodType can't be null");

		logger.debug("[prepareResource] of employee's with id = " + resource.getId() + " and ExternalId = "
				+ resource.getExternalId());

		if ((methodType.equals(MethodType.POST) || (methodType.equals(MethodType.PUT)))) {

			// Find establishment to link the employee with
			if (resource.getCompanyDepartment().getCompany().getSiren() != null
					&& resource.getCompanyDepartment().getNic() != null) {

				Establishment establishment = this.establishmentService.findByNic(
						resource.getCompanyDepartment().getCompany().getSiren(),
						resource.getCompanyDepartment().getNic());

				if (establishment != null) {
					resource.setEstablishment(establishment);
					resource.setNic(resource.getCompanyDepartment().getNic());
				}
			} else {
				logger.error("Couldn't not find establishment with following siren/nic : "
						+ resource.getCompanyDepartment().getCompany().getSiren() + "/"
						+ resource.getCompanyDepartment().getNic());
			}

			// generate expirationDatePassword
			resource.getUser().setExpirationDatePassword(this.userService.generateExpirationDatePassword());

			// Populate employee content
			logger.debug("Populate employee content");
			this.populateResource(resource, methodType);

			// set employee's id to null before insert the employee into the
			// database
			logger.debug("Setting employee's id to null before insert the employee into the database");
			resource.setId(null);

			// set user's id to null before persist the user into the
			// database
			logger.debug("Setting user's id to null before insert the user into the database");
			resource.getUser().setId(null);
			logger.debug("Creating employee's user into the database");

			// Set sviProfile to null before persist
			if (resource.getUser().getSviProfile() != null) {
				resource.getUser().getSviProfile().setId(null);
			}
			userService.create(resource.getUser());

			// populate company's collection of employee
			logger.debug("Populate company's collection of the employee");
			this.populateCompanyCollections(resource.getCompanyDepartment().getCompany());

			// set company's structures relation collection empty else there are
			// lazy exception
			resource.getCompanyDepartment().getCompany().setChildStructures(new ArrayList<Structure>());
			resource.getCompanyDepartment().getCompany().setParentStructures(new ArrayList<Structure>());
		}
	}

	/**
	 * This method allows to populate employeeProfile's attributes witch are
	 * mandatories
	 * 
	 * @param employeeProfile : employeeProfile
	 * @param methodType      : method type (GET, PUT, POST, DELETE)
	 */
	@SuppressWarnings("unused")
	public void populateResource(EmployeeProfile employeeProfile, MethodType methodType) {
		// Check params
		Assert.notNull(employeeProfile, "[populateResource] employee can't be null");
		Assert.notNull(methodType, "[populateResource] methodType can't be null");

		logger.debug("[populateResource] of employee with name = " + employeeProfile.getName());

		logger.debug("Finding company by externalId");
		// Get company of the employee
		Company company = companyService
				.findByExternalId(employeeProfile.getCompanyDepartment().getCompany().getExternalId());

		// get the root companyDeparment of the employee
		logger.debug("Getting the root companyDeparment of the employee");
		CompanyDepartment rootCompanyDepartment = companyDepartmentService.findRootCompanyDepartmentByCompany(company);

		logger.debug("RootCompanyDepartment found : " + rootCompanyDepartment.getName());

		// We check if root companyDepartment is null
		if (rootCompanyDepartment != null) {
			// set companyDepartment of the employee
			logger.debug("Setting the root companyDeparment to the employee");
			employeeProfile.setCompanyDepartment(rootCompanyDepartment);
		} else {
			throw new WebResourceRelationNotFoundException(employeeProfile,
					employeeProfile.getCompanyDepartment().getId(), methodType,
					"Root company department of employeeProfile sent not found", "externalId",
					employeeProfile.getExternalId().toString(), ClassType.COMPANY_DEPARTMENT);
		}

		// Set all roles to the employee
		logger.debug("Setting all roles to the employee");
		this.populateEmployeeRoles(employeeProfile);
	}

	/**
	 * This method allows to populate employee's roles
	 * 
	 * @param employeeProfile : employeeProfile to populate
	 */
	public void populateEmployeeRoles(EmployeeProfile employeeProfile) {
		// Check param
		Assert.notNull(employeeProfile, "[populateEmployeeRoles] employee can't be null");
		logger.debug("[populateEmployeeRoles] of employee with name = " + employeeProfile.getName());

		// ProfileType
		ProfileType profileType = this.profileTypeService.findByValue(ProfileTypeValue.EMPLOYEE);

		if (profileType == null) {
			throw new NullPointerException("[populateEmployeeRoles] profileType is null");
		}

		// set profileType to the employee
		employeeProfile.setProfileType(profileType);

		Role employeeRole = employeeProfile.getRoles().get(0);

		// find all application of company
		List<Application> applications = new ArrayList<Application>();
		applications = applicationService
				.findAllApplicationByStructure(employeeProfile.getCompanyDepartment().getCompany());

		// Create a new list of roles
		List<Role> listNewRoles = new ArrayList<Role>();

		// Get the list of roles of the employee
		for (Application application : applications) {

			Role role = null;

			if (employeeRole.getName().equals(RoleProfile.ROLE_MANAGER_EMPLOYEE.toString())) {
				// add admin role for this application
				role = this.roleService.findAdminRoleByProfileTypeAndApplication(employeeProfile.getProfileType(),
						application);

			} else if (employeeRole.getName().equals(RoleProfile.ROLE_DEFAULT.toString())) {
				// add default role for this application
				role = this.roleService.findBasicRoleByProfileTypeAndApplication(employeeProfile.getProfileType(),
						application);
			}

			if (role != null) {
				// Add new role to the list of roles
				listNewRoles.add(role);
			}
		}

		if (listNewRoles.size() > 0) {
			// set roles to the employee
			employeeProfile.setRoles(listNewRoles);
		} else {
			throw new EmptyResultDataAccessException("[populateEmployeeRoles] Employee's list of new roles is empty",
					0);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateResourceCollections(EmployeeProfile resource) {
		// Check param
		Assert.notNull(resource, "[populateResourceCollections] employee can't be null");
		logger.debug("[populateResourceCollections] of employee with name = " + resource.getName());
		employeeProfileService.populateCollections(resource);
	}

	/**
	 * This method allows to validate employee's role
	 * 
	 * @param resource   : employeeProfile to validate
	 * @param methodType : method type (GET, PUT, POST, DELETE)
	 */
	public void validEmployeeRole(EmployeeProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[validEmployeeRole] Employee can't be null");
		Assert.notNull(methodType, "[validEmployeeRole] MethodType can't be null");

		// Check that the employee has only one role
		// get the list of roles of the employee
		logger.debug("Checking that the employee has only one role");
		List<Role> listRoles = resource.getRoles();
		if (listRoles.size() != 1) {
			throw new WebRequestSyntaxException(resource, methodType, "Employee must have one role", "externalId",
					resource.getExternalId().toString());
		}

		// Check that the employee's role is ROLE_MANAGER_EMPLOYEE or
		// ROLE_DEFAULT
		logger.debug("Checking that the employee's role is ROLE_MANAGER_EMPLOYEE or ROLE_DEFAULT");
		if ((!(listRoles.get(0).getName().equals(RoleProfile.ROLE_MANAGER_EMPLOYEE.toString())))
				&& (!(listRoles.get(0).getName().equals(RoleProfile.ROLE_DEFAULT.toString())))) {

			throw new WebRequestSyntaxException(resource, methodType,
					"Employee's role value must be equals to ROLE_MANAGER_EMPLOYEE or ROLE_DEFAULT", "externalId",
					resource.getExternalId().toString());
		}
	}

	/**
	 * This method allows to validate email and username of the employee
	 * 
	 * @param resource   : employeeProfile to validate
	 * @param methodType : method type (GET, PUT, POST, DELETE)
	 */
	public void validEmailAndUserName(EmployeeProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[validEmailAndUserName] Employee can't be null");
		Assert.notNull(methodType, "[validEmailAndUserName] MethodType can't be null");

		// Check that the employee's email not already exists into the database
		// (table EmployeeProfile only)
		logger.debug("Checking that the employee's mail not already exists into the database");
		this.validateEmailUniqueConstraints(resource, resource.getEmail(), methodType);

		// Check that the employee's username not already exists into the
		// database (table User and ProjectAccount)
		logger.debug("Checking that the employee's username not already exists into the database");
		this.validateUsernameUniqueContraints(resource, resource.getUser().getUsername(), methodType);
	}

	/**
	 * This method allows to validate the company of the employee profile
	 * 
	 * @param resource   : employeeProfile to validate
	 * @param methodType : method type (GET, PUT, POST, DELETE)
	 */
	public void validCompany(EmployeeProfile resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[validCompany] Employee can't be null");
		Assert.notNull(methodType, "[validCompany] MethodType can't be null");

		// CHECK COMPANY
		// Check that the company externalId not equal 0
		logger.debug("Checking that company's externalId of employee sent not equal 0");
		if (resource.getCompanyDepartment().getCompany().getExternalId().equals(new Long(0))) {
			throw new WebRequestSyntaxException(resource, methodType,
					"Company's externalId of employeeProfile sent equals 0 : "
							+ resource.getCompanyDepartment().getCompany().getExternalId(),
					"externalId", resource.getExternalId().toString());
		}

		// Check that the company ID not equal 0
		logger.debug("Checking that company's id of employee sent not equal 0");
		if (resource.getCompanyDepartment().getCompany().getId().equals(new Long(0))) {
			throw new WebRequestSyntaxException(resource, methodType,
					"Company's Id of employeeProfile sent equals 0 : "
							+ resource.getCompanyDepartment().getCompany().getId(),
					"externalId", resource.getExternalId().toString());
		}

		// In case of the application sending the employee know the id of
		// the company, we search the company by it's ID
		if (!(resource.getCompanyDepartment().getCompany().getId().equals(new Long(-1)))) {

			logger.debug("Checking that the company of the employee sent exists with id");
			Company foundCompany = companyService.findOne(resource.getCompanyDepartment().getCompany().getId());

			if (foundCompany == null) {
				// No Company exists for this employee
				logger.error("[validCompany] of employee"
						+ " throw an WebResourceRelationNotFoundException : No company exists with this Id : "
						+ resource.getCompanyDepartment().getCompany().getId());

				throw new WebResourceRelationNotFoundException(resource,
						resource.getCompanyDepartment().getCompany().getId(), methodType,
						"No company exists with this Id : " + resource.getCompanyDepartment().getCompany().getId(),
						"externalId", resource.getExternalId().toString(), ClassType.COMPANY);
			}

			// If the application sending the employee doesn't know the id of
			// the company, it must be equals at -1 but the externalId is
			// required. We search the employee profile's company by its
			// externalId
		} else {
			// Check that the company of the employee sent exists
			logger.debug("Checking that the company of the employee sent exists with externalId");
			Company foundCompany = companyService
					.findByExternalId(resource.getCompanyDepartment().getCompany().getExternalId());

			if (foundCompany == null) {
				// No Company exists for this employee
				logger.error("[validCompany] of employee"
						+ " throw an WebResourceRelationNotFoundException : No company exists with this Id : "
						+ resource.getCompanyDepartment().getCompany().getExternalId());

				throw new WebResourceRelationNotFoundException(resource,
						resource.getCompanyDepartment().getCompany().getId(), methodType,
						"No company exists with this Id : "
								+ resource.getCompanyDepartment().getCompany().getExternalId(),
						"externalId", resource.getExternalId().toString(), ClassType.COMPANY);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean validateResourceExists(Long employeeId) {
		// Check param
		Assert.notNull(employeeId, "[validateResourceExistsById] employeeId can't be null");
		logger.debug("[validateResourceExists] of employee ");
		// Check if an employee exists with this id
		EmployeeProfile foundEmployee = employeeProfileService.findOne(employeeId);
		if (foundEmployee != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filterResource(EmployeeProfile resource, MethodType methodType) {
		Assert.notNull(resource, "[filterResource] resource can't be null");
		Assert.notNull(methodType, "[filterResource] methodType can't be null");

		// In all cases we set password and controlKeyNewPassword of user to
		// null
		resource.getUser().setPassword(null);
		resource.getUser().setControlKeyNewPassword(null);

		if (methodType.equals(MethodType.GET)) {

			// We don't send the list of applications of the company
			resource.getCompanyDepartment().getCompany().setApplications(new ArrayList<Application>());

			// Hide company's structures relations
			resource.getCompanyDepartment().getCompany().setParentStructures(new ArrayList<Structure>());
			resource.getCompanyDepartment().getCompany().setChildStructures(new ArrayList<Structure>());

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * Unused method for this type of resource
	 */
	@Override
	public void validateDelete(Long resourceId) {
		// NOTHING TODO
	}
}
