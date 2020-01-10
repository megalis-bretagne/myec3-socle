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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestNotAllowedException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;
import org.myec3.socle.ws.server.dto.ListHolder;
import org.myec3.socle.ws.server.resource.EstablishmentResource;
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
 * {@link Establishment}.
 * 
 * @author Ludovic LAVIGNE <ludovic.lavigne@worldline.com>
 */
@RestController
@Scope("prototype")
public class EstablishmentResourceImpl implements EstablishmentResource {

	private static final Logger logger = LogManager.getLogger(EstablishmentResourceImpl.class);

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
	 * Business Service providing methods and specifics operations on
	 * {@link Establishment} objects
	 */
	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	/**
	 * Validation tools providing methods that validate {@link Establishment}
	 * objects before doing operation
	 */
	@Autowired
	@Qualifier("establishmentValidator")
	private ResourceValidatorManager<Establishment> establishmentValidator;

	@Autowired
	@Qualifier("companyValidator")
	private ResourceValidatorManager<Company> companyValidator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getEstablishment(Long establishmentId) {
		logger.debug("[getEstablishment] GET the establishment with id : " + establishmentId);

		try {
			// Validate the establishment's Id before returning the
			// establishment
			this.establishmentValidator.validateGet(establishmentId);
			Establishment foundEstablishment = new Establishment();

			try {
				// Get the agent from database
				foundEstablishment = establishmentService.findOne(establishmentId);
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// establishment into the database
				logger.error("[getEstablishment] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Establishment ID : " + establishmentId);

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), establishmentId,
								MethodType.GET));
			}

			// Populate resource's collections before returning the response
			this.establishmentValidator.populateResourceCollections(foundEstablishment);

			// Filter what the server must not return into the XML
			this.establishmentValidator.filterResource(foundEstablishment, MethodType.GET);

			logger.info("[getEstablishment] GET returning an response 200");
			return ResponseEntity.ok().body(foundEstablishment);

		} catch (WebApplicationException ex) {
			logger.error("[getEstablishment] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[getEstablishment] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Establishment ID : " + establishmentId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), new Long(0),
							MethodType.GET));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getEstablishmentsByCriteria(Long companyId, Boolean headOffice) {
		logger.debug("[getEstablishment] GET establishments of a given company by criteria with companyId : "
				+ companyId + " and headOffice : " + headOffice.toString());
		try {
			// Validate the company's Id before returning its establishments
			this.companyValidator.validateGet(companyId);
			List<Establishment> foundEstablishments = new ArrayList<Establishment>();

			try {
				Company company = this.companyService.findOne(companyId);

				// retrieve all establishments of a given company or only
				// headOffice, depending of request parameter headOffice
				if (null != headOffice && headOffice == Boolean.TRUE) {
					foundEstablishments.add(establishmentService.findHeadOfficeEstablishmentByCompany(company));
				} else {
					foundEstablishments = establishmentService.findAllEstablishmentsByCompany(company);
				}

			} catch (RuntimeException ex) {
				// Internal server error during the retrieval of
				// company/establishments into the database
				logger.error("[getEstablishmentsByCriteria] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Company ID : " + companyId);

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), companyId,
								MethodType.GET));
			}

			// Populate agent's collections before returning the response
			for (Establishment establishment : foundEstablishments) {
				this.establishmentValidator.populateResourceCollections(establishment);
			}

			// Filter what the server must not return into the XML
			for (Establishment establishment : foundEstablishments) {
				this.establishmentValidator.filterResource(establishment, MethodType.GET);
			}

			logger.info("[getEstablishmentsByCriteria] GET returning an response 200");
			return ResponseEntity.ok().body(new ListHolder(foundEstablishments));

		} catch (WebApplicationException ex) {
			logger.error("[getEstablishmentsByCriteria] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[getEstablishmentsByCriteria] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Establishments of company ID : " + companyId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), companyId,
							MethodType.GET));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity createEstablishment(Establishment establishment) {
		logger.debug("[createEstablishment] POST Establishment");

		try {
			this.establishmentValidator.validateCreate(establishment);

			this.establishmentValidator.prepareResource(establishment, MethodType.POST);

			try {

				this.establishmentService.populateCollections(establishment);
				if (establishment.getForeignIdentifier() == Boolean.FALSE && this.establishmentService
						.findByNic(establishment.getCompany().getSiren(), establishment.getNic()) != null) {
					logger.warn("Trying to create already existing establishment ! SIRET :"
							+ establishment.getCompany().getSiren() + establishment.getNic());
					return ResponseEntity.status(HttpStatus.CONFLICT).build();
				}

				// if no diffusable information, set a default value
				if (establishment.getDiffusableInformations() == null) {
					establishment.setDiffusableInformations(Boolean.FALSE);
				}
				// Create new establishment into the database
				this.establishmentService.create(establishment);

				// retrieve establishment's company and associate it with company
				Company retrievedCompany = this.companyService.findOne(establishment.getCompany().getId());
				this.companyService.populateCollections(retrievedCompany);
				List<Establishment> myEstablishments = retrievedCompany.getEstablishments();

				Establishment establishmentToSynchro = null;
				for (Establishment findEstablishmentToSynchro : myEstablishments) {
					if (findEstablishmentToSynchro.getForeignIdentifier() == Boolean.FALSE) {
						if (findEstablishmentToSynchro.getNic().equals(establishment.getNic())) {
							establishmentToSynchro = findEstablishmentToSynchro;
							logger.debug("Found establishment to synchronize ! " + establishmentToSynchro.toString());
						}
					} else {
						if (findEstablishmentToSynchro.getNationalID() != null) {
							establishmentToSynchro = this.establishmentService
									.findLastForeignCreated(findEstablishmentToSynchro.getNationalID());
							if (establishmentToSynchro.getId() != null) {
								logger.debug("Found foreign establishment to synchronize ! "
										+ establishmentToSynchro.toString());
							}
							break;
						}
					}
				}

				// Notify the creation of establishment to external applications
				this.synchronizationNotificationService.notifyCreation(establishmentToSynchro);
				// Notify the update of associated company to external
				// applications
				this.synchronizationNotificationService.notifyUpdate(retrievedCompany);

			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// establishment into the database
				logger.error("[createEstablishment] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Establishment ID : " + establishment.getId());
				logger.error("Establishment ExternalId : " + establishment.getExternalId());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), establishment.getId(),
								MethodType.POST, "externalId", establishment.getExternalId().toString()));
			}

			// Filter what the server must not return into the XML
			this.establishmentValidator.filterResource(establishment, MethodType.POST);

			logger.info("[createEstablishment] POST returning an response 201");
			return ResponseEntity.status(HttpStatus.CREATED).build();

		} catch (WebApplicationException ex) {
			logger.error("[createEstablishment] WebApplicationException : " + ex.getCause() + " " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.POST, logger);
		} catch (RuntimeException e) {

			logger.error("[createEstablishment] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Establishment ID : " + establishment.getId());
			logger.error("Establishment ExternalId : " + establishment.getExternalId());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), establishment.getId(),
							MethodType.POST, "externalId", establishment.getExternalId().toString()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity updateEstablishment(Long establishmentId, Establishment establishment) {
		logger.debug("[updateEstablishment] PUT Establishment with id : " + establishmentId);
		try {
			// We check if the establishment exists first
			Establishment foundEstablishment = establishmentService.findOne(establishment.getId());
			if (foundEstablishment == null) {
				// we throw a WebResourceNotFoundException
				throw new WebResourceNotFoundException(establishmentId, MethodType.PUT,
						"No establishment exists with this id : " + establishmentId);
			}

			// Validate establishment sent
			this.establishmentValidator.validateUpdate(establishment, establishmentId, foundEstablishment);

			// Populate establishment's collections
			// TODO implementation if necessary, depending of received object
			// state
			// this.establishmentValidator.populateResourceCollections(establishment);

			// Prepare the establishment before persist
			// TODO implementation if necessary, depending of received object
			// state
			// this.establishmentValidator.prepareResource(establishment,
			// MethodType.POST);

			try {
				// Update establishment into the database
				this.establishmentService.update(establishment);

				// Notify the update to external applications
				this.synchronizationNotificationService.notifyUpdate(establishment);
				this.synchronizationNotificationService.notifyUpdate(establishment.getCompany());
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// establishment into the database
				logger.error("[updateEstablishment] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Establishment's ID : " + establishment.getId());
				logger.error("Establishment's ExternalID : " + establishment.getExternalId());

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getMessage(), establishment.getId(), MethodType.POST,
								"ExternalId", establishment.getExternalId().toString()));
			}

			logger.info("[updateEstablishment] PUT returning an response 200");
			return ResponseEntity.ok().body(establishment);
		} catch (WebApplicationException ex) {
			logger.error("[updateEstablishment] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.PUT, logger);
		} catch (RuntimeException e) {

			logger.error("[updateEstablishment] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Establishment's ID : " + establishment.getId());
			logger.error("Establishment's ExternalID : " + establishment.getExternalId());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), establishment.getId(),
							MethodType.POST, "externalId", establishment.getExternalId().toString()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity deleteEstablishment(Long establishmentId) {
		logger.debug("[deleteEstablishment] DELETE Establishment : request not allowed");
		throw new WebRequestNotAllowedException(establishmentId, MethodType.DELETE);
	}

	/**
	 * Retrieve an establishment from his siret If the Siret is wrong MPS webservice
	 * returns a 404. We propagate this 404
	 */
	@Override
	public ResponseEntity getEstablishmentFromMPS(String establishmentSiret) {
		MpsWsClient mpsClient = new MpsWsClient();

		logger.debug("[updateEstablishment] GET Establishment with siret : " + establishmentSiret);

		Establishment establishment;
		try {
			establishment = mpsClient.getEstablishment(establishmentSiret);
			if (establishment.getSiret() != null && !establishment.getSiret().isEmpty()) {
				Company retrievedCompany = this.companyService.findBySiren(establishment.getSiret().substring(0, 9));

				if (retrievedCompany != null) {
					this.companyService.populateCollections(retrievedCompany);
					List<Establishment> myEstablishments = retrievedCompany.getEstablishments();

					for (Establishment findEstablishment : myEstablishments) {
						if (findEstablishment.getForeignIdentifier() == Boolean.FALSE) {
							if (findEstablishment.getNic().equals(establishment.getNic())) {
								logger.debug("Establishment searched already exist !" + findEstablishment.toString());
								return ResponseEntity.status(HttpStatus.CONFLICT).build();
							}
						}
					}
				}

			}
			logger.info("[getEstablishmentFromMPS] GET returning a response 200");
			return ResponseEntity.ok().body(establishment);
		} catch (FileNotFoundException e) {
			logger.info("[getEstablishmentFromMPS] GET returning a response 404 (unknown siret)");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (IOException e) {
			logger.info("[getEstablishmentFromMPS] GET returning a response 500");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
