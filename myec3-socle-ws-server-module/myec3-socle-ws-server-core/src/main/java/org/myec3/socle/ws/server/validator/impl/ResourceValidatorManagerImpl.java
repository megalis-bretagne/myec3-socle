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

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.ProjectAccount;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.ProjectAccountService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebAttributeAlreadyExistsException;
import org.myec3.socle.core.sync.api.exception.WebRequestSyntaxException;
import org.myec3.socle.ws.server.validator.ResourceValidatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * Implementation of the validator manager that describes all method that
 * validator must inherit
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("resourceValidatorManager")
public abstract class ResourceValidatorManagerImpl<T extends Resource> implements ResourceValidatorManager<T> {

	protected static final Logger logger = LogManager.getLogger(ResourceValidatorManagerImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProjectAccount} objects
	 */
	@Autowired
	@Qualifier("projectAccountService")
	private ProjectAccountService projectAccountService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Autowired
	@Qualifier("userService")
	private UserService userService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void validateBeanContent(T resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[validateBeanContent] resource can't be null");
		Assert.notNull(methodType, "[validateBeanContent] methodType can't be null");
		logger.debug(
				"[validateBeanContent] of " + resource.getClass().getSimpleName() + " with id : " + resource.getId());

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Set<ConstraintViolation<T>> invalidValuesOfResource = validator.validate(resource, Default.class);

		if ((invalidValuesOfResource.size() > 0) || (resource.getId() == null) || (resource.getExternalId() == null)) {

			StringBuilder message = new StringBuilder();
			int cpt = 0;

			if (resource.getId() == null) {
				cpt++;
			}
			if (resource.getExternalId() == null) {
				cpt++;
			}

			message.append((invalidValuesOfResource.size() + cpt) + " error(s) : ");

			if (resource.getId() == null) {
				message.append("Id can't be null");
			}
			if (resource.getExternalId() == null) {
				message.append(" ExternalId can't be null");
			}

			for (ConstraintViolation value : invalidValuesOfResource) {
				message.append(value.getPropertyPath() + " of " + resource.getClass().getSimpleName() + " : "
						+ value.getMessage() + " (" + value.getInvalidValue() + ")");
				message.append(" - ");
			}

			logger.error("ERROR(S) : " + message.toString());
			if (resource.getExternalId() != null) {
				throw new WebRequestSyntaxException(resource, methodType, message.toString(), "externalId",
						resource.getExternalId().toString());
			}
			throw new WebRequestSyntaxException(resource.getId(), methodType, message.toString());
		}
	}

	/**
	 * This method allows to validate that the email not already exists into the
	 * database
	 * 
	 * @param resource   : the resource to validate
	 * @param email      : the email of the resource to validate
	 * @param methodType : type of method to test (get/put/post/delete)
	 * 
	 * @throws RuntimeException
	 */
	public void validateEmailUniqueConstraints(T resource, String email, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[validateEmailUniqueConstraints] resource can't be null");
		Assert.notNull(email, "[validateEmailUniqueConstraints] email can't be null");
		Assert.notNull(methodType, "[validateEmailUniqueConstraints] methodType can't be null");
		logger.debug("[validateEmailUniqueConstraints] of " + resource.getClass().getSimpleName());

		// check that the AgentProfile's email doesn't already exists
		// for an other agentProfile into the the database
		/*
		 * if (resource.getClass().equals(AgentProfile.class)) { AgentProfile
		 * agentProfile = (AgentProfile) resource;
		 * 
		 * List<AgentProfile> foundAgentProfile = (List<AgentProfile>)
		 * agentProfileService .findAllByEmail(agentProfile.getEmail());
		 * 
		 * if (foundAgentProfile.size() > 0) { // An AgentProfile with this email
		 * already exists logger.error("[validateEmailUniqueConstraints] of " +
		 * resource.getClass().getSimpleName() + " " + resource.getName() +
		 * " throw an WebAttributeAlreadyExistsException : An agent with this email already exists"
		 * ); throw new WebAttributeAlreadyExistsException(resource, methodType,
		 * "An agent with this email already exists : " + email, "externalId",
		 * resource.getExternalId().toString()); } }
		 */

		// check that the EmployeeProfile's email doesn't already exists
		// for an other employeeProfile into the the database
		if (resource.getClass().equals(EmployeeProfile.class)) {
			EmployeeProfile employeeProfile = (EmployeeProfile) resource;
			AgentProfile agentProfile = new AgentProfile();

			// If the email have been updated
			List<EmployeeProfile> foundEmployeeProfile = (List<EmployeeProfile>) employeeProfileService
					.findAllByEmail(employeeProfile.getEmail());

			if (foundEmployeeProfile.size() == 0) {
				agentProfile.setEmail(employeeProfile.getEmail());
				if (this.agentProfileService.emailAlreadyExists(agentProfile.getEmail(), agentProfile)) {
					// An AgentProfile with this email already exists
					logger.error("[validateEmailUniqueConstraints] of " + resource.getClass().getSimpleName() + " "
							+ resource.getName()
							+ " throw an WebAttributeAlreadyExistsException : An agent with this email already exists");
					throw new WebAttributeAlreadyExistsException(resource, methodType,
							"An agent with this email already exists : " + email, "externalId",
							resource.getExternalId().toString());
				}
			} else {
				// An EmployeeProfile with this email already exists
				logger.error("[validateEmailUniqueConstraints] of " + resource.getClass().getSimpleName() + " "
						+ resource.getName()
						+ " throw an WebAttributeAlreadyExistsException : An employee with this email already exists");
				throw new WebAttributeAlreadyExistsException(resource, methodType,
						"An employee with this email already exists : " + email, "externalId",
						resource.getExternalId().toString());
			}
		}
	}

	/**
	 * This method allows to validate that the login not already exists into the
	 * database
	 * 
	 * @param resource   : the resource to validate
	 * @param username   : the username to validate
	 * @param methodType : the method type called (GET, POST, PUT, DELETE)
	 * @throws RuntimeException
	 */
	public void validateUsernameUniqueContraints(T resource, String username, MethodType methodType)
			throws RuntimeException {
		// Check params
		Assert.notNull(resource, "[validateUsernameUniqueContraints] resource can't be null");
		Assert.notNull(username, "[validateUsernameUniqueContraints] username can't be null");
		Assert.notNull(methodType, "[validateUsernameUniqueContraints] methodType can't be null");
		logger.debug("[validateUsernameUniqueContraints] of " + resource.getClass().getSimpleName());

		// check that the resource's login doesn't already exists
		// into the project account table of the database
		ProjectAccount foundProjectAccount = projectAccountService.findByLogin(username);
		if (foundProjectAccount != null) {
			// A project account with this login already exists
			logger.error("[validateUsernameUniqueContraints] of " + resource.getClass().getSimpleName() + " "
					+ resource.getName()
					+ " throw an WebAttributeAlreadyExistsException : A project account with this login already exists");
			throw new WebAttributeAlreadyExistsException(resource, methodType,
					"A project account with this login already exists : " + username, "externalId",
					resource.getExternalId().toString());
		}

		// check that the resource's login doesn't already exists
		// into the user table of the database
		User foundUser = userService.findByUsername(username);
		if (foundUser != null) {
			if (this.employeeProfileService.findByEmail(foundUser.getUsername()) != null) {
				// A user with this login (username) already exists
				logger.error("[validateUsernameUniqueContraints] of " + resource.getClass().getSimpleName() + " "
						+ resource.getName()
						+ " throw an WebAttributeAlreadyExistsException : A user with this login already exists");
				throw new WebAttributeAlreadyExistsException(resource, methodType,
						"A user with this login already exists : " + username, "externalId",
						resource.getExternalId().toString());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Boolean validateResourceExists(Long resourceId);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void validateGet(Long resourceId);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void validateCreate(T resource);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void validateUpdate(T resource, Long resourceId, T foundResource);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void validateUpdateCreation(T resource);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void validateDelete(Long resourceId);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void prepareResource(T resource, MethodType methodType);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void filterResource(T resource, MethodType methodType);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void populateResourceCollections(T resource);
}
