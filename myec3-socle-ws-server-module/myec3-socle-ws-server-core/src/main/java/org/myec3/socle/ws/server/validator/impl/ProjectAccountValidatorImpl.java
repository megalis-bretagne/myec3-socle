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

import org.myec3.socle.core.domain.model.ProjectAccount;
import org.myec3.socle.core.service.ProjectAccountService;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestSyntaxException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * Implementation of the validator that make a validation on
 * {@link ProjectAccount} during REST requests.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("projectAccountValidator")
public class ProjectAccountValidatorImpl extends ResourceValidatorManagerImpl<ProjectAccount> {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProjectAccount} objects
	 */
	@Autowired
	@Qualifier("projectAccountService")
	private ProjectAccountService projectAccountService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateCreate(ProjectAccount resource) {
		// Check param
		Assert.notNull(resource, "[validateCreate] ProjectAccount can't be null");
		logger.debug("[validateCreate] of project account : " + resource.getName());

		// validate Bean content
		this.validateBeanContent(resource, MethodType.POST);

		// Check that id equal 0
		logger.debug("Checking that resource's id equal 0");
		if (!(resource.getId().equals(new Long(0)))) {
			logger.error("[validateCreate] of project account " + resource.getName()
					+ " throw an WebRequestSyntaxException : Id of project account sent not equal 0");
			throw new WebRequestSyntaxException(resource.getId(), MethodType.POST,
					"Id of project account sent not equal 0");
		}

		// Check that externalId equal 0
		logger.debug("Checking that resource's externalId equal 0");
		if (!resource.getExternalId().equals(new Long(0))) {
			logger.error("[validateCreate] of project account " + resource.getName()
					+ " throw an WebRequestSyntaxException : ExternalId of project account sent not equal 0");
			throw new WebRequestSyntaxException(resource.getId(), MethodType.POST,
					"ExternalId of project account sent not equal 0");
		}

		// check that the projectAccount's login doesn't already exists
		// into the user and project account table of the database
		logger.debug("Checking that the projectAccount's login doesn't already exists into the database");
		this.validateUsernameUniqueContraints(resource, resource.getLogin(), MethodType.POST);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdate(ProjectAccount resource, Long resourceId, ProjectAccount foundProjectAccount) {
		// Check params
		Assert.notNull(resource, "[validateUpdate] ProjectAccount can't be null");
		Assert.notNull(resourceId, "[validateUpdate] ProjectAccount's id can't be null");
		Assert.notNull(resourceId, "[validateUpdate] Found projectAccount can't be null");
		logger.debug("[validateUpdate] of project account with name = " + resource.getName());

		// validate Bean content
		this.validateBeanContent(resource, MethodType.PUT);

		// check that id sent is equals at the id into the resource
		logger.debug("Checking that id sent is equals at the id contained into the resource");
		if (!resource.getExternalId().equals(resourceId)) {
			logger.error("[validateUpdate] of project account "
					+ " throw an WebRequestSyntaxException : Id sent not equals at the resource Id, resourceId "
					+ resource.getExternalId() + ", Id : " + resourceId);
			throw new WebRequestSyntaxException(resource.getId(), MethodType.PUT,
					"Id sent not equals at the resource Id, resourceId " + resource.getExternalId() + ", Id : "
							+ resourceId);
		}

		// if projectAccount login has been updated we check that its doesn't
		// already exists into the user and project account table of the database
		logger.debug("Checking that the projectAccount's login doesn't already exists into the database");
		if (!(resource.getLogin().equals(foundProjectAccount.getLogin()))) {
			this.validateUsernameUniqueContraints(resource, resource.getLogin(), MethodType.PUT);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdateCreation(ProjectAccount resource) {
		// Check params
		Assert.notNull(resource, "[validateUpdateCreation] ProjectAccount can't be null");
		logger.debug("[validateUpdateCreation] of project account with name = " + resource.getName());

		// validate Bean content
		this.validateBeanContent(resource, MethodType.PUT);

		// check that the projectAccount's login doesn't already exists
		// into the
		// user and project account table of the database
		logger.debug("Checking that the projectAccount's login doesn't already exists into the database");
		this.validateUsernameUniqueContraints(resource, resource.getLogin(), MethodType.PUT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateDelete(Long resourceId) {
		// Check param
		Assert.notNull(resourceId, "[validateDelete] ProjectAccount's id can't be null");
		logger.debug("[validateDelete] of project account with id = " + resourceId);

		// Check if resource exists before deletion
		if (!this.validateResourceExists(resourceId)) {
			// No project account exists with this externalId
			logger.error("[validateDelete] of project account"
					+ " throw an WebResourceNotFoundException : No project account exists with this ExternalId : "
					+ resourceId);
			throw new WebResourceNotFoundException(resourceId, MethodType.DELETE,
					"No project account exists with this ExternalId : " + resourceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareResource(ProjectAccount resource, MethodType methodType) {
		// Check params
		Assert.notNull(resource, "[prepareResource] ProjectAccount can't be null");
		Assert.notNull(methodType, "[prepareResource] MethodType can't be null");
		logger.debug("[prepareResource] of project account with name = " + resource.getName());

		if (methodType.equals(MethodType.POST) || methodType.equals(MethodType.PUT)) {
			// Set id to null before persist the new project account into the
			// database
			logger.debug("Setting id to null before persist the new project account into the database");
			resource.setId(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean validateResourceExists(Long resourceId) {
		// Check params
		Assert.notNull(resourceId, "[validateResourceExists] ProjectAccount's externalId can't be null");
		logger.debug("[validateResourceExists] of project account with externalId : " + resourceId);

		// Check if a project account exists with this ExternalId
		ProjectAccount foundProjectAccount = projectAccountService.findByExternalId(resourceId);

		if (foundProjectAccount != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filterResource(ProjectAccount resource, MethodType methodType) {
		Assert.notNull(resource, "[filterResource] resource can't be null");
		Assert.notNull(methodType, "[filterResource] methodType can't be null");

		resource.setPassword(null);
	}

	/**
	 * Unused method for this type of resource
	 */
	@Override
	public void validateGet(Long resourceId) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of resource
	 */
	@Override
	public void populateResourceCollections(ProjectAccount resource) {
		// NOTHING TODO
	}
}
