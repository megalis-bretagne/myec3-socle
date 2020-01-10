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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestNotAllowedException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.ws.server.dto.ListHolder;
import org.myec3.socle.ws.server.resource.OrganismResource;
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
 * {@link Organism}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RestController
@Scope("prototype")
public class OrganismResourceImpl implements OrganismResource {

	private static final Logger logger = LogManager.getLogger(OrganismResourceImpl.class);

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Autowired
	@Qualifier("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationNotificationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	/**
	 * Validator providing methods that validate {@link Organism} objects before
	 * doing operation
	 */
	@Autowired
	@Qualifier("organismValidator")
	private ResourceValidatorManager<Organism> organismValidator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getOrganism(Long organismId) {
		logger.debug("[getOrganism] GET the organism with id : " + organismId);
		try {
			// Validate the organism's Id before returning the organism
			this.organismValidator.validateGet(organismId);
			Organism foundOrganism = new Organism();
			try {
				// Get the organism from database
				foundOrganism = this.organismService.findOne(organismId);
			} catch (RuntimeException ex) {
				// Internal server error during getting the organism from the
				// database
				logger.error("[getOrganism] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), new Long(0),
								MethodType.GET));
			}

			// Populate organism's collections before returning the response
			this.organismService.populateCollections(foundOrganism);

			logger.info("[getOrganism] GET returning an response 200");
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML)
					.body(foundOrganism);
		} catch (WebApplicationException ex) {
			logger.error("[getOrganism] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[getOrganism] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Organism ID : " + organismId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), new Long(0),
							MethodType.GET));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getAllOrganism() {
		logger.debug("[getAllOrganism] GET all organisms");
		try {
			List<Organism> listOrganisms = new ArrayList<Organism>();

			// We get the list of all organism from the database
			try {
				listOrganisms.addAll(organismService.findAll());
			} catch (RuntimeException ex) {
				// Internal server error during retrieve all organisms from the
				// database
				logger.error("[getAllOrganism] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), new Long(0),
								MethodType.GET));
			}

			// Filter what the server must not return into the xml
			for (Organism organism : listOrganisms) {
				this.organismValidator.filterResource(organism, MethodType.GET);
			}

			logger.info("[getAllOrganism] GET returning an response 200");
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML)
					.body(new ListHolder(listOrganisms));

		} catch (WebApplicationException ex) {
			logger.error("[getAllOrganism] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[getAllOrganism] Internal Server Error : ");
			logger.error("Error Message : An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), new Long(0),
							MethodType.GET));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity updateOrganism(Long organismId, Organism organism) {
		logger.debug("[updateOrganism] PUT organism with id : " + organismId);
		try {
			Organism foundOrganism = null;

			// We check if the organism exists
			foundOrganism = this.organismService.findOne(organism.getId());

			if (foundOrganism == null) {
				// If organism not exists we don't create a new organism but
				// we throw a WebResourceNotFoundException
				throw new WebResourceNotFoundException(organismId, MethodType.PUT,
						"No organism exists with this id : " + organismId);
			} else {
				// Validate and prepare organism before the update
				this.organismValidator.validateUpdate(organism, organismId, foundOrganism);
				try {
					this.organismService.update(organism);

					// Notify the update to external applications
					this.synchronizationNotificationService.notifyUpdate(organism);
				} catch (RuntimeException ex) {
					// Internal server error during the update of the
					// organism into the database
					logger.error("[updateOrganism] Internal Server Error : ");
					logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
					logger.error("Organism ID : " + organismId);
					logger.error("Organism label : " + organism.getLabel());

					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.contentType(MediaType.APPLICATION_XML)
							.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
									"Database error : " + ex.getCause() + " " + ex.getMessage(), organism.getId(),
									MethodType.PUT, "externalId", organism.getExternalId().toString()));
				}

				logger.info("[updateOrganism] PUT returning an response 200");
				return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML)
						.body(organism);
			}

		} catch (WebApplicationException ex) {
			logger.error("[updateOrganism] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.PUT, logger);
		} catch (RuntimeException e) {
			logger.error("[updateOrganism] Internal Server Error : ");
			logger.error("Error Message : An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Organism ID : " + organismId);
			logger.error("Organism label : " + organism.getLabel());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), new Long(0),
							MethodType.PUT));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity createOrganism(Organism organism) {
		logger.debug("[createOrganism] POST Organism : request not allowed");
		throw new WebRequestNotAllowedException(organism.getId(), MethodType.POST);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity deleteOrganism(Long organismId) {
		logger.debug("[deleteOrganism] DELETE Organism : request not allowed");
		throw new WebRequestNotAllowedException(organismId, MethodType.DELETE);
	}

}
