/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.ws.server.resource.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestNotAllowedException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.ws.server.dto.OrganismDepartmentIdAndApiKeyDto;
import org.myec3.socle.ws.server.dto.ProfileDto;
import org.myec3.socle.ws.server.resource.AgentProfileResource;
import org.myec3.socle.ws.server.utils.ResponseUtils;
import org.myec3.socle.ws.server.validator.ResourceValidatorManager;
import org.myec3.socle.ws.server.validator.impl.AgentProfileValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.WebApplicationException;

/**
 * Concrete implementation providing specific REST methods to manage
 * {@link AgentProfile}.
 *
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RestController
@Scope("prototype")
public class AgentProfileResourceImpl implements AgentProfileResource {
	private static final Logger logger = LogManager.getLogger(AgentProfileResourceImpl.class);

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Autowired
	@Qualifier("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationNotificationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProfileService} objects
	 */
	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	/**
	 * Validator providing methods that validate {@link AgentProfile} objects before
	 * doing operation
	 */
	@Autowired
	@Qualifier("agentProfileValidator")
	private ResourceValidatorManager<AgentProfile> agentProfileValidator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getAgentProfile(Long agentProfileId, Boolean withHiddenRoles) {
		logger.debug("[getAgentProfile] GET AgentProfile");
		try {
			// Validate the agentProfile's Id before returning the agent
			this.agentProfileValidator.validateGet(agentProfileId);
			AgentProfile foundAgent = new AgentProfile();
			try {
				// Get the agent from database
				foundAgent = agentProfileService.findOne(agentProfileId);
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// company into the database
				logger.error("[getAgentProfile] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Agent profile ID : " + agentProfileId);

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), agentProfileId,
								MethodType.GET));

			}

			// Populate agent's collections before returning the ResponseEntity
			this.agentProfileValidator.populateResourceCollections(foundAgent);

			// Filter what the server must not return into the xml
			((AgentProfileValidatorImpl) this.agentProfileValidator).filterResource(foundAgent, MethodType.GET,
					withHiddenRoles);

			logger.info("[getAgentProfile] GET returning an ResponseEntity 200");
			return ResponseEntity.ok().body(foundAgent);

		} catch (WebApplicationException ex) {
			logger.error("[getAgentProfile] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[getAgentProfile] Internal Server Error : ");
			logger.error("Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Agent profile ID : " + agentProfileId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), agentProfileId,
							MethodType.GET));

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity createAgentProfile(AgentProfile agent) {
		logger.debug("[createAgentProfile] POST agent profile : request not allowed");
		throw new WebRequestNotAllowedException(new Long(0), MethodType.POST);
	}

	/**
	 * Method called is the ressource does not exists during a PUT
	 *
	 * @param agent : agent to put if it does not exist during a PUT
	 * @return ResponseEntity : the ResponseEntity to return at the web service
	 * client
	 */
	public ResponseEntity updateCreationAgentProfile(AgentProfile agent) {
		logger.debug("[updateCreationAgentProfile] AgentProfile");
		try {
			Assert.notNull(agent, "AgentProfile cannot be null");

			// Validate agentProfile before the creation
			this.agentProfileValidator.validateUpdateCreation(agent);
			try {
				// Create the new agent profile
				this.agentProfileService.create(agent);

				// Notify the creation to external applications
				this.synchronizationNotificationService.notifyCreation(agent);

			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// agent into the database
				logger.error("[updateCreationAgentProfile] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Agent profile name : " + agent.getName());

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), null, MethodType.PUT));
			}

			// Filter what the server must not return into the xml
			this.agentProfileValidator.filterResource(agent, MethodType.PUT);

			logger.info("[updateCreationAgentProfile] PUT returning an ResponseEntity 201");
			// Return ResponseEntity at the client
			return ResponseEntity.status(HttpStatus.CREATED).body(agent);
		} catch (WebApplicationException ex) {
			logger.error("[updateCreationAgentProfile] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.PUT, logger);
		} catch (RuntimeException e) {

			logger.error("[updateCreationAgentProfile] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Agent profile name : " + agent.getName());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), agent.getId(), MethodType.PUT,
							"externalId", agent.getExternalId().toString()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity updateAgentProfile(Long agentProfileId, AgentProfile agent) {
		logger.debug("[updateAgentProfile] PUT AgentProfile");
		try {
			// We check if the agentProfile exists else we must redirect the
			// request like a POST
			AgentProfile foundAgent = this.agentProfileService.findOne(agent.getId());
			if (foundAgent == null) {
				return this.updateCreationAgentProfile(agent);
			} else {
				// Validate agentProfile before the update
				this.agentProfileValidator.validateUpdate(agent, agentProfileId, foundAgent);
				// Prepare ressource before the update
				this.agentProfileValidator.prepareResource(agent, MethodType.PUT);
				logger.info("Username : " + agent.getUsername());
				logger.info("Profile ID : " + agent.getId());
				Boolean usernameAlreadyExist = this.profileService.usernameAlreadyExists(agent.getEmail(), agent);
				try {

					if (!usernameAlreadyExist.booleanValue()) {
						logger.info("Updating agent username with email !");
						agent.getUser().setUsername(agent.getEmail());
					} else {

						logger.info("Setting old username back");
						agent.getUser().setUsername(foundAgent.getUser().getUsername());
					}

					this.agentProfileService.update(agent);

					// Notify the update to external applications
					this.synchronizationNotificationService.notifyUpdate(agent);
				} catch (RuntimeException ex) {
					// Internal server error during the creation of the new
					// agent into the database
					logger.error("[updateAgentProfile] Internal Server Error : ");
					logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
					logger.error("Agent profile ID : " + agentProfileId);

					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.contentType(MediaType.APPLICATION_XML)
							.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
									"Database error : " + ex.getCause() + " " + ex.getMessage(), agentProfileId,
									MethodType.PUT));
				}

				// Filter what the server must not return into the xml
				this.agentProfileValidator.filterResource(foundAgent, MethodType.PUT);

				logger.info("[updateAgentProfile] PUT returning an ResponseEntity 200");
				return ResponseEntity.ok().body(agent);
			}
		} catch (WebApplicationException ex) {
			logger.error("[updateAgentProfile] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.PUT, logger);
		} catch (RuntimeException e) {

			logger.error("[updateAgentProfile] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Agent profile ID : " + agentProfileId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), agent.getId(), MethodType.PUT,
							"externalId", agent.getExternalId().toString()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity deleteAgentProfile(Long agentProfileId) {
		logger.debug("[deleteAgentProfile] DELETE agent profile : request not allowed");
		throw new WebRequestNotAllowedException(agentProfileId, MethodType.DELETE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getAgentDashboard(Long agentProfileId) {
		logger.debug("[getAgentDashboard] GET AgentProfile dashboard");
		try {
			String dashboard = agentProfileService.getDashboard(agentProfileId);
			logger.info("[getAgentDashboard] GET returning an ResponseEntity 200");
			return ResponseEntity.ok().body(dashboard);
		} catch (WebResourceNotFoundException ex) {
			// Internal server error during the creation of the new
			logger.error("[getAgentDashboard] WebResourceNotFoundException", ex);
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity createAgentDashboard(Long agentProfileId, String dashboard) {
		logger.debug("[createAgentDashboard] POST AgentProfile dashboard");
		try {
			String dashboardResult = agentProfileService.updateOrCreateDashboard(agentProfileId, dashboard);
			logger.info("[createAgentDashboard] GET returning an ResponseEntity 200");
			return ResponseEntity.ok().body(dashboardResult);
		} catch (WebResourceNotFoundException ex) {
			// Internal server error during the creation of the new
			// agent into the database
			logger.error("[createAgentDashboard] WebResourceNotFoundException", ex);

			return ResponseEntity.notFound().build();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getOrganismDepartmentAndApiKeyByAgentProfileId(Long agentProfileId) {
		logger.debug("[getOrganismDepartmentAndApiKeyByAgentProfileId] GET OrganismID and Pastell ApiKey");
		OrganismDepartment organismDepartment = agentProfileService.getOrganismDepartmentByAgentProfileId(agentProfileId);
		if (organismDepartment != null) {
			return ResponseEntity.ok(new OrganismDepartmentIdAndApiKeyDto(organismDepartment.getId(), organismDepartment.getOrganism().getApiKey(), organismDepartment.getLabel()));
		} else {
			return  ResponseEntity.notFound().build();
		}
	}

}
