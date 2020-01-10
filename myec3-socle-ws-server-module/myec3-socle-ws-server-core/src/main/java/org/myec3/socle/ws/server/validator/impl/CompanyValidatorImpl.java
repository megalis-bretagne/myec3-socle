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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Person;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.StructureTypeService;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestSyntaxException;
import org.myec3.socle.core.sync.api.exception.WebResourceAlreadyExistsException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * Implementation of the validator that make a validation on {@link Company}
 * during REST requests.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("companyValidator")
public class CompanyValidatorImpl extends ResourceValidatorManagerImpl<Company> {

	private static final Logger logger = LogManager.getLogger(CompanyValidatorImpl.class);

	private CompanyWSinfo mpsWS = new MpsWsClient();

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureType} objects
	 */
	@Autowired
	@Qualifier("structureTypeService")
	private StructureTypeService structureTypeService;

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
	public void validateGet(Long resourceId) {
		// check param
		Assert.notNull(resourceId, "[validateGet] Company's id can't be null");
		logger.debug("[validateGet] of company with id : " + resourceId);

		// Check if a company with this id exists into the database
		if (!this.validateResourceExists(resourceId)) {
			logger.error("[validateGet] of company id : " + resourceId
					+ " throw an WebResourceNotFoundException : No company exist with this id : " + resourceId);
			throw new WebResourceNotFoundException(resourceId, MethodType.GET,
					"No company exist with this id : " + resourceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateCreate(Company resource) {
		// Check param
		Assert.notNull(resource, "[validateCreate] Company can't be null");
		logger.debug("[validateCreate] of company : " + resource.getName());

		// validate Bean content
		this.validateBeanContent(resource, MethodType.POST);

		// Check that the company does not already exist
		logger.debug("Checking that company's SIREN is unique");
		if (companyService.findBySiren(resource.getSiren()) != null) {
			logger.error("[validateCreate] of company " + resource.getName()
					+ " throw an WebResourceAlreadyExistException : siren of company sent already exist in database");
			throw new WebResourceAlreadyExistsException(resource, MethodType.POST, "Siren of company already exists",
					"siren", resource.getSiren());
		}

		// Check that id equal 0
		logger.debug("Checking that company's id equal 0");
		if (!(resource.getId().equals(new Long(0)))) {
			logger.error("[validateCreate] of company " + resource.getName()
					+ " throw an WebRequestSyntaxException : Id of company sent not equal 0");
			throw new WebRequestSyntaxException(resource, MethodType.POST, "Id of company sent not equal 0",
					"externalId", resource.getExternalId().toString());
		}

		// Check that externalId equal 0
		logger.debug("Checking that company's externalId equal 0");
		if (!(resource.getExternalId().equals(new Long(0)))) {
			logger.error("[validateCreate] of company " + resource.getName()
					+ " throw an WebRequestSyntaxException : ExternalId of company sent not equal 0");
			throw new WebRequestSyntaxException(resource, MethodType.POST, "ExternalId of company sent not equal 0",
					"externalId", resource.getExternalId().toString());
		}

		// Check if there are responsibles
		logger.debug("Checking if there are responsibles into the company sent");
		if (resource.getResponsibles().size() > 0) {
			List<Person> listResponsibles = resource.getResponsibles();
			// For each responsible into the company we check that the Id is
			// equals at 0
			for (Person responsable : listResponsibles) {
				if (!(responsable.getId().equals(new Long(0)))) {
					logger.error("[validateCreate] of company " + resource.getName()
							+ " throw an RequestSyntaxException : Id of responsible sent not equal 0");
					throw new WebRequestSyntaxException(resource, MethodType.POST, "Id of responsible sent not equal 0",
							"externalId", resource.getExternalId().toString());
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareResource(Company company, MethodType methodType) {

		// check params
		Assert.notNull(company, "[prepareResource] Company can't be null");
		Assert.notNull(methodType, "[prepareResource] MethodType can't be null");
		logger.debug("[prepareResource] of company : " + company.getName());

		// Set structure type of the company
		logger.debug("Getting stuctureType of company");
		StructureType structureType = this.structureTypeService.findByValue(StructureTypeValue.COMPANY);
		if (structureType == null) {
			throw new NullPointerException("[prepareResource] no structureType found, structureType is null");
		}

		// Set company foreignIdentifier
		logger.debug("Getting foreignIdentifier of company");
		if (company.getRegistrationCountry() != null && company.getRegistrationCountry().equals(Country.FR)) {
			logger.debug("Company is french");
			company.setForeignIdentifier(Boolean.FALSE);
		} else {
			logger.debug("Company is not french");
			company.setForeignIdentifier(Boolean.TRUE);
		}

		// Set default applications to the company
		logger.debug("Setting default applications to the company");
		List<Application> listApplication = applicationService.findAllDefaultApplicationsByStructureType(structureType);

		if (listApplication == null) {
			throw new NullPointerException("[prepareResource] list of default applications of the company is null");
		}
		company.setApplications(listApplication);

		// Set id to null before persist the new company into the
		// database
		logger.debug("Setting company's id to null before persist the new company into the database");
		company.setId(null);

		// For each responsible sent we must set the id to null
		logger.debug("Setting responsible's id to null before persist the new company into the database");
		if (company.getResponsibles().size() > 0) {
			List<Person> listResponsibles = company.getResponsibles();
			for (Person responsable : listResponsibles) {
				responsable.setId(null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean validateResourceExists(Long companyId) {
		// check param
		Assert.notNull(companyId, "Company's id can't be null");
		logger.debug("[validateResourceExists] of company ");

		// Check if a company exists with this id
		Company foundCompany = companyService.findOne(companyId);

		if (foundCompany != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Unused method for this type of Resource
	 */
	@Override
	public void validateUpdate(Company resource, Long resourceId, Company foundCompany) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of Resource
	 */
	@Override
	public void validateDelete(Long resourceId) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of Resource
	 */
	@Override
	public void populateResourceCollections(Company resource) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of Resource
	 */
	@Override
	public void validateUpdateCreation(Company resource) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of Resource
	 */
	@Override
	public void filterResource(Company resource, MethodType methodType) {
		// NOTHING TODO
	}
}
