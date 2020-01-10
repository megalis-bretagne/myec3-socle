package org.myec3.socle.ws.server.resource.impl;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.AgentManagedApplication;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.AgentManagedApplicationService;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebInternalServerErrorException;
import org.myec3.socle.core.sync.api.exception.WebRequestFormatException;
import org.myec3.socle.ws.server.dto.ListHolder;
import org.myec3.socle.ws.server.resource.ApplicationManagerResource;
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
 * Created by a602499 on 29/12/2016.
 */
@RestController
@Scope("prototype")
public class ApplicationManagerResourceImpl implements ApplicationManagerResource {

	private static final Logger logger = LogManager.getLogger(ApplicationManagerResourceImpl.class);

	/**
	 * Validator providing methods that validate {@link Organism} objects before
	 * doing operation
	 */
	@Autowired
	@Qualifier("organismValidator")
	private ResourceValidatorManager<Organism> organismValidator;

	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	/**
	 * Validator providing methods that validate {@link AgentProfile} objects before
	 * doing operation
	 */
	@Autowired
	@Qualifier("agentProfileValidator")
	private ResourceValidatorManager<AgentProfile> agentProfileValidator;

	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	@Autowired
	@Qualifier("agentManagedApplicationService")
	private AgentManagedApplicationService agentManagedApplicationService;

	@Override
	public ResponseEntity getAppManagedByAgent(Long agentId) {
		try {
			if (agentId == null) {
				throw new WebRequestFormatException(0L, MethodType.GET,
						"Wrong parameters sent in request, agentId cannot be null.");
			}

			// Validate the agentProfile's Id before returning the agent
			this.agentProfileValidator.validateGet(agentId);

			AgentProfile agent = this.agentProfileService.findOne(agentId);
			List<Application> agentManagedApplications = this.agentManagedApplicationService
					.findApplicationManagedByAgent(agent);

			if (agentManagedApplications == null) {
				throw new WebInternalServerErrorException(MethodType.GET,
						"An error occured when querying for applications managed by agent #" + agentId);
			}

			return ResponseEntity.ok().body(new ListHolder(agentManagedApplications));

		} catch (WebApplicationException ex) {
			logger.error("[ApplicationManagerResource.getAppManagerByApp] WebRequestFormatException: ", ex);
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[ApplicationManagerResource.getAppManagerByApp] Internal Server Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), 0L,
							MethodType.POST));
		}
	}

	@Override
	public ResponseEntity getAllAppManagersByOrganism(Long organismId) {
		try {
			if (organismId == null) {
				throw new WebRequestFormatException(0L, MethodType.GET,
						"Wrong parameters sent in request, organismId cannot be null.");
			}

			// Validate the agentProfile's Id before returning the agent
			this.organismValidator.validateGet(organismId);

			Organism organism = this.organismService.findOne(organismId);
			List<AgentManagedApplication> agentManagedApplications = this.agentManagedApplicationService
					.findAgentManagedApplicationsByOrganism(organism);

			if (agentManagedApplications == null) {
				throw new WebInternalServerErrorException(MethodType.GET,
						"An error occured when querying for applications managed for organism #" + organismId);
			}

			// Populate the agents collections before returning the
			// response
			for (AgentManagedApplication appManaged : agentManagedApplications) {
				AgentProfile agent = appManaged.getAgentProfile();
				this.agentProfileValidator.populateResourceCollections(agent);
				this.agentProfileValidator.filterResource(agent, MethodType.GET);
			}

			return ResponseEntity.ok()
					.body(new ListHolder(agentManagedApplications));

		} catch (WebApplicationException ex) {
			logger.error("[ApplicationManagerResource.getAppManagerByApp] WebRequestFormatException: ", ex);
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[ApplicationManagerResource.getAppManagerByApp] Internal Server Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), 0L,
							MethodType.POST));
		}
	}
}
