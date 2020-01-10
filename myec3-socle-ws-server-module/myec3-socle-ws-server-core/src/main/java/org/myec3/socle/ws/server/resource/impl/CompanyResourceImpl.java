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
package org.myec3.socle.ws.server.resource.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.AdministrativeState;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.InseeGeoCode;
import org.myec3.socle.core.domain.model.Person;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.AdministrativeStateValue;
import org.myec3.socle.core.domain.model.enums.CompanyCategory;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.InseeGeoCodeService;
import org.myec3.socle.core.service.InseeLegalCategoryService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestNotAllowedException;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;
import org.myec3.socle.ws.server.resource.CompanyResource;
import org.myec3.socle.ws.server.utils.ResponseUtils;
import org.myec3.socle.ws.server.validator.ResourceValidatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Concrete implementation providing specific REST methods to manage
 * {@link Company}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RestController
@Scope("prototype")
public class CompanyResourceImpl implements CompanyResource {

	private static final Logger logger = LogManager.getLogger(CompanyResourceImpl.class);

	private CompanyWSinfo mpsWS = new MpsWsClient();

	@Autowired
	@Qualifier("inseeLegalCategoryService")
	private InseeLegalCategoryService inseeLegalCategoryService;

	@Autowired
	@Qualifier("inseeGeoCodeService")
	private InseeGeoCodeService inseeGeoCodeService;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Autowired
	@Qualifier("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationNotificationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * Validator providing methods that validate {@link Company} objects before
	 * doing operation
	 */
	@Autowired
	@Qualifier("companyValidator")
	private ResourceValidatorManager<Company> companyValidator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getCompany(Long companyId) {
		logger.debug("[getCompany] GET the company with id : " + companyId);
		try {
			// Validate the company's Id before returning the company
			this.companyValidator.validateGet(companyId);
			Company foundCompany = new Company();
			try {
				// Get the company from database
				foundCompany = this.companyService.findOne(companyId);
			} catch (RuntimeException ex) {
				// Internal server error during getting the company from the
				// database
				logger.error("[getCompany] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), new Long(0),
								MethodType.GET));
			}

			// Populate company before returning the response
			this.companyService.populateCollections(foundCompany);

			// Return the company in the response
			logger.info("[getCompany] GET returning an response 200");

			return ResponseEntity.ok().body(foundCompany);
		} catch (WebApplicationException ex) {
			logger.error("[getCompany] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[getCompany] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR,
							"Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), new Long(0),
							MethodType.GET));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity createCompany(Company company) {
		logger.debug("[createCompany] POST Company");

		Date date = new Date();

		try {

			// set Administrative state, can be override by MPS
			AdministrativeState adminState = new AdministrativeState();
			adminState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);
			company.setAdministrativeState(adminState);

			// Set company foreignIdentifier
			logger.debug("Getting foreignIdentifier of company");
			if (company.getRegistrationCountry() != null && company.getRegistrationCountry().equals(Country.FR)) {
				logger.debug("Company is french");
				company.setForeignIdentifier(Boolean.FALSE);
			} else {
				logger.debug("Company is not french");
				company.setForeignIdentifier(Boolean.TRUE);
			}

			// set default CompanyCategory
			company.setCompanyCategory(CompanyCategory.NON_RENSEIGNEE);

			Establishment establishment = new Establishment();

			// If siren is valid, try to get the company informations from MPS
			if (company.getSiren() != null && !company.getSiren().isEmpty()
					&& companyService.isSirenValid(company.getSiren())) {
				logger.info("Updating company infos with MPS");
				this.mpsWS.updateCompanyInfo(company, inseeLegalCategoryService);
				company.setLastUpdate(date);

				if (company.getResponsibles().size() > 0) {
					List<Person> listResponsibles = company.getResponsibles();
					// For each responsible into the company we have to set id=0 to create them
					for (Person responsable : listResponsibles) {
						responsable.setId(new Long(0));
					}
				}

				// if SIRET is valid, try to get the establishment informations from MPS
				if (company.getNic() != null && !company.getNic().isEmpty()
						&& companyService.isSiretValid(company.getSiren(), company.getNic())) {

					try {
						logger.info("Updating establishment infos with MPS");
						establishment = this.mpsWS.getEstablishment(company.getSiren() + company.getNic());

						// Find this inseeGeoCode from db
						if (establishment.getAddress().getInsee() != null) {
							InseeGeoCode inseeGeoCode = inseeGeoCodeService
									.findByInseeCode(establishment.getAddress().getInsee());
							establishment.getAddress().setInsee(inseeGeoCode.getInseeCode());
						} else {
							// we can't find the insee code, set it to null to continue establishment
							// creation
							establishment.getAddress().setInsee(null);
						}

						// Required establishment fields
						establishment.setEmail(company.getEmail());
						establishment.setName(company.getName());

						if (establishment.getApeNafLabel() != null) {
							company.setApeNafLabel(establishment.getApeNafLabel());
						}

						establishment.setCompany(company);
						company.addEstablishment(establishment);
					} catch (IOException e) {
						logger.error("Unable to retrieve establishment informations from MPS.");
					}
				}
			}

			// even if company siren is invalid, company informations can be save in db
			// No establishments, we need to create one
			if (company.getEstablishments().size() == 0) {
				establishment = this.createBasicEstablishment(company);
				company.addEstablishment(establishment);
			}

			// Validate company sent
			this.companyValidator.validateCreate(company);

			// Prepare the company before persist
			this.companyValidator.prepareResource(company, MethodType.POST);

			try {
				// Set id to null before persist the new company into the
				// database
				logger.debug("Setting company's id to null before persist the new company into the database");
				company.setId(null);

				// Persist company into the database
				this.companyService.create(company);

				// Notify the creation to external applications
				this.synchronizationNotificationService.notifyCreation(company);
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// company into the database
				logger.error("[createCompany] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Company's ID : " + company.getId());
				logger.error("Company's ExternalID : " + company.getExternalId());

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getMessage(), company.getId(), MethodType.POST, "ExternalId",
								company.getExternalId().toString()));
			}

			// return HTTP 201 + the new company
			logger.info("Return ID : " + company.getId());
			logger.info("Return external ID : " + company.getExternalId().toString());
			logger.info("[createCompany] POST returning an response 201");
			return ResponseEntity.status(HttpStatus.CREATED).body(company);

		} catch (WebApplicationException ex) {
			logger.error("[createCompany] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.POST, logger);
		} catch (RuntimeException e) {

			logger.error("[createCompany] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Company's ID : " + company.getId());
			logger.error("Company's ExternalID : " + company.getExternalId());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), company.getId(), MethodType.POST,
							"externalId", company.getExternalId().toString()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity updateCompany(Long companyId, Company company) {
		logger.debug("[updateCompany] PUT Company : request not allowed");
		throw new WebRequestNotAllowedException(companyId, MethodType.PUT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity deleteCompany(Long companyId) {
		logger.debug("[deleteCompany] DELETE Company : request not allowed");
		throw new WebRequestNotAllowedException(companyId, MethodType.DELETE);
	}

	/**
	 * Method to create establishment with basic informations in database
	 *
	 * @param company : the the linked company
	 * @return the establishment that will be created
	 */
	private Establishment createBasicEstablishment(Company company) {
		Establishment establishment = new Establishment();
		establishment.setIsHeadOffice(Boolean.FALSE);
		establishment.setCompany(company);
		establishment.setForeignIdentifier(Boolean.FALSE);
		establishment.setApeCode(company.getApeCode());
		establishment.setAddress(company.getAddress());
		establishment.setEmail(company.getEmail());
		establishment.setName(company.getName() + " - JEB");
		establishment.setDiffusableInformations(Boolean.FALSE);

		AdministrativeState adminState = new AdministrativeState();
		adminState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);
		establishment.setAdministrativeState(adminState);

		return establishment;
	}
}
