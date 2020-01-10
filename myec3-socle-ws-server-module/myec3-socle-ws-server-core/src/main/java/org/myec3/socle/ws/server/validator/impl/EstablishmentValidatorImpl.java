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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.sync.api.ClassType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestSyntaxException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.core.sync.api.exception.WebResourceRelationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * Implementation of the validation tool that make a validation on
 * {@link Establishment} during REST requests.
 * 
 * @author Ludovic LAVIGNE <ludovic.lavigne@worldline.com>
 */
@Component("establishmentValidator")
public class EstablishmentValidatorImpl extends ResourceValidatorManagerImpl<Establishment> {

	private static final Logger logger = LogManager.getLogger(EstablishmentValidatorImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Establishment} objects
	 */
	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean validateResourceExists(Long resourceId) {
		// Check establishmentId parameter
		Assert.notNull(resourceId, "[validateResourceExists] Establishment's id can't be null");
		logger.debug("[validateResourceExists] of establishment ");

		// Check if an establishment exists with this id
		Establishment foundEstablishment = establishmentService.findOne(resourceId);
		if (foundEstablishment != null) {
			return Boolean.TRUE;
		}
		logger.info(
				"[validateResourceExists] of establishment : no establishment with id : " + resourceId + " was found");
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateGet(Long resourceId) {
		// Check establishmentId parameter
		Assert.notNull(resourceId, "[validateGet] Establishment's id can't be null");
		logger.debug("[validateGet] of establishment ");

		// Check if an establishment with this id exists into the database
		if (!this.validateResourceExists(resourceId)) {
			throw new WebResourceNotFoundException(resourceId, MethodType.GET,
					"No establishment exists with this id : " + resourceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateCreate(Establishment resource) {
		// Check parameter
		Assert.notNull(resource, "[validateCreate] Establishment can't be null");
		logger.debug("[validateCreate] of establishment : " + resource.getName());

		// validate Bean content
		this.validateBeanContent(resource, MethodType.POST);

		// Check that id equal 0
		logger.debug("Checking that establishment's id equal 0");
		if (!(resource.getId().equals(new Long(0)))) {
			logger.error("[validateCreate] of establishment " + resource.getName()
					+ " throw an WebRequestSyntaxException : Id of establihsment sent not equal 0");
			throw new WebRequestSyntaxException(resource, MethodType.POST, "Id of establishment sent not equal 0",
					"externalId", resource.getExternalId().toString());
		}

		// Check that externalId equal 0
		logger.debug("Checking that establishment's externalId equal 0");
		if (!(resource.getExternalId().equals(new Long(0)))) {
			logger.error("[validateCreate] of establishment " + resource.getName()
					+ " throw an WebRequestSyntaxException : ExternalId of establishment sent not equal 0");
			throw new WebRequestSyntaxException(resource, MethodType.POST,
					"ExternalId of establishment sent not equal 0", "externalId", resource.getExternalId().toString());
		}

		// Check company of the establishment
		this.validCompany(resource, MethodType.POST);
	}

	/**
	 * This method allows to validate the company of the establishment
	 * 
	 * @param resource   : employeeProfile to validate
	 * @param methodType : method type (GET, PUT, POST, DELETE)
	 */
	public void validCompany(Establishment resource, MethodType methodType) {
		// Check parameters
		Assert.notNull(resource, "[validCompany] Establishment can't be null");
		Assert.notNull(methodType, "[validCompany] MethodType can't be null");

		// CHECK COMPANY
		// Check that the company externalId not equal 0
		logger.debug("Checking that company's externalId of establishment sent not equal 0");
		if (resource.getCompany().getExternalId().equals(new Long(0))) {
			throw new WebRequestSyntaxException(resource, methodType,
					"Company's externalId of establishment sent equals 0 : " + resource.getCompany().getExternalId(),
					"externalId", resource.getExternalId().toString());
		}

		// Check that the company ID not equal 0
		logger.debug("Checking that company's id of establishment sent not equal 0");
		if (resource.getCompany().getId().equals(new Long(0))) {
			throw new WebRequestSyntaxException(resource, methodType,
					"Company's Id of establishment sent equals 0 : " + resource.getCompany().getId(), "externalId",
					resource.getExternalId().toString());
		}

		// In case of the application sending the establishment know the id of
		// the company, we search the company by it's ID
		if (!(resource.getCompany().getId().equals(new Long(-1)))) {

			logger.debug("Checking that the company of the establishment sent exists with id");
			Company foundCompany = companyService.findOne(resource.getCompany().getId());

			if (foundCompany == null) {
				// No Company exists for this establishment
				logger.error("[validCompany] of establishment"
						+ " throw an WebResourceRelationNotFoundException : No company exists with this Id : "
						+ resource.getCompany().getId());

				throw new WebResourceRelationNotFoundException(resource, resource.getCompany().getId(), methodType,
						"No company exists with this Id : " + resource.getCompany().getId(), "externalId",
						resource.getExternalId().toString(), ClassType.COMPANY);
			}

			// If the application sending the employee doesn't know the id of
			// the company, it must be equals at -1 but the externalId is
			// required. We search the establishment's company by its
			// externalId
		} else {
			// Check that the company of the employee sent exists
			logger.debug("Checking that the company of the establishment sent exists with externalId");
			Company foundCompany = companyService.findByExternalId(resource.getCompany().getExternalId());

			if (foundCompany == null) {
				// No Company exists for this employee
				logger.error("[validCompany] of establishment"
						+ " throw an WebResourceRelationNotFoundException : No company exists with this external Id : "
						+ resource.getCompany().getExternalId());

				throw new WebResourceRelationNotFoundException(resource, resource.getCompany().getId(), methodType,
						"No company exists with this external Id : " + resource.getCompany().getExternalId(),
						"externalId", resource.getExternalId().toString(), ClassType.COMPANY);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdate(Establishment resource, Long resourceId, Establishment foundResource) {
		// Check parameters
		Assert.notNull(resource, "[validateUpdate] establishment can't be null");
		Assert.notNull(resourceId, "[validateUpdate] resourceId can't be null");
		Assert.notNull(foundResource, "[validateUpdate] foundEstablishment can't be null");

		logger.debug("[validateUpdate] of establishment with id : " + resourceId);

		// Validate bean content
		logger.debug("Validating bean content of establishment sent");
		this.validateBeanContent(resource, MethodType.PUT);

		// Check that resource's id is equal at the resourceId
		logger.debug("Checking that resource's id is equal at the resourceId");
		if (!(resource.getId().equals(resourceId))) {
			throw new WebRequestSyntaxException(resource, MethodType.PUT,
					"Id sent not corresponding at the establishment id sent : " + resource.getId(), "externalId",
					resource.getExternalId().toString());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdateCreation(Establishment resource) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateDelete(Long resourceId) {
		// NOTHING TODO
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareResource(Establishment resource, MethodType methodType) {
		// NOTHING TODO
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filterResource(Establishment resource, MethodType methodType) {
		// Check establishment parameter
		Assert.notNull(resource, "[prepareResource] Establishment can't be null");
		Assert.notNull(methodType, "[prepareResource] MethodType can't be null");
		// remove company's applications
		resource.getCompany().setApplications(new ArrayList<Application>());

		// remove company's unused fields after enterprise/establishments dissociation
		resource.getCompany().setAddress(null);
		resource.getCompany().setPhone(null);
		resource.getCompany().setFax(null);
		resource.getCompany().setNic(null);
		resource.getCompany().setEmail(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateResourceCollections(Establishment resource) {
		// Check establishment parameter
		Assert.notNull(resource, "Establishment can't be null");
		logger.debug("[populateResourceCollections] of establishment : " + resource.getName());

		// populate company's collections
		companyService.populateCollections(resource.getCompany());

	}

}
