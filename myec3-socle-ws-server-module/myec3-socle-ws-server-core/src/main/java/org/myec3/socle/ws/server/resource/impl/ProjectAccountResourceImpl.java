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

import javax.ws.rs.WebApplicationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.ProjectAccount;
import org.myec3.socle.core.service.ProjectAccountService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestNotAllowedException;
import org.myec3.socle.ws.server.resource.ProjectAccountResource;
import org.myec3.socle.ws.server.validator.ResourceValidatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestController;

/**
 * Concrete implementation providing specific REST methods to manage
 * {@link ProjectAccount}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RestController
@Scope("prototype")
public class ProjectAccountResourceImpl implements ProjectAccountResource {

	private static final Logger logger = LogManager.getLogger(ProjectAccountResourceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProjectAccount} objects
	 */
	@Autowired
	@Qualifier("projectAccountService")
	private ProjectAccountService projectAccountService;

	/**
	 * Validator providing methods that validate {@link ProjectAccount} objects
	 * before doing operation
	 */
	@Autowired
	@Qualifier("projectAccountValidator")
	private ResourceValidatorManager<ProjectAccount> projectAccountValidator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectAccount getProjectAccount(Long projectAccountId) {
		logger.debug("[getProjectAccount] GET project account : request not allowed");
		throw new WebRequestNotAllowedException(projectAccountId, MethodType.GET);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity createProjectAccount(ProjectAccount projectAccount) {
		logger.debug("[createProjectAccount] POST ProjectAccount");
		try {
			// Validate project account sent
			this.projectAccountValidator.validateCreate(projectAccount);

			// prepare project account before persist
			this.projectAccountValidator.prepareResource(projectAccount, MethodType.POST);

			try {
				// Persist project account into the database
				this.projectAccountService.create(projectAccount);
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// project account into the database
				logger.error("[createProjectAccount] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("ProjectAccount ID : " + projectAccount.getId());
				logger.error("ProjectAccount ExternalID : " + projectAccount.getExternalId());
				logger.error("ProjectAccount Login : " + projectAccount.getLogin());
				logger.error("ProjectAccount Email : " + projectAccount.getEmail());

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getMessage(), projectAccount.getId(), MethodType.POST));
			}

			// Filter what the server must not return into the xml
			this.projectAccountValidator.filterResource(projectAccount, MethodType.POST);

			// return HTTP 201 + the new project account
			logger.info("[createProjectAccount] POST returning an response 201 for projectAccount with id= "
					+ projectAccount.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(projectAccount);
		} catch (WebApplicationException ex) {
			logger.error("[createProjectAccount] WebApplicationException : " + ex.getMessage());
			return errorResponse(ex, projectAccount);
		} catch (RuntimeException e) {
			logger.error("[createProjectAccount] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("ProjectAccount's ID : " + projectAccount.getId());
			logger.error("ProjectAccount's ExternalID : " + projectAccount.getExternalId());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), projectAccount.getId(),
							MethodType.POST));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity updateProjectAccount(Long projectAccountId, ProjectAccount projectAccount) {
		logger.debug("[updateProjectAccount] PUT ProjectAccount");
		try {
			ProjectAccount foundProjectAccount = this.projectAccountService
					.findByExternalId(projectAccount.getExternalId());
			if (foundProjectAccount == null) {
				return this.updateCreationProjectAccount(projectAccount);
			} else {
				// Validate project account sent
				projectAccountValidator.validateUpdate(projectAccount, projectAccountId, foundProjectAccount);
				try {
					// We set the id of the projectAccount because it can be
					// null
					projectAccount.setId(foundProjectAccount.getId());

					// Update the project account
					projectAccountService.update(projectAccount);

				} catch (RuntimeException ex) {
					// Internal server error during the update of the new
					// project account from the database
					logger.error("[updateProjectAccount] Internal Server Error : ");
					logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
					logger.error("ProjectAccount ID : " + projectAccountId);
					logger.error("ProjectAccount ExternalID : " + projectAccount.getExternalId());
					logger.error("ProjectAccount Login : " + projectAccount.getLogin());
					logger.error("ProjectAccount Email : " + projectAccount.getEmail());

					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.contentType(MediaType.APPLICATION_XML)
							.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
									"Database error : " + ex.getMessage(), projectAccount.getId(), MethodType.PUT));
				}

				// Filter what the server must not return into the xml
				this.projectAccountValidator.filterResource(projectAccount, MethodType.PUT);

				// return HTTP 200 + the project account updated
				logger.info("[updateProjectAccount] PUT returning an response 200 for projectAccount with id= "
						+ projectAccount.getId());
				return ResponseEntity.ok().body(projectAccount);
			}
		} catch (WebApplicationException ex) {
			logger.error("[updateProjectAccount] WebApplicationException : " + ex.getMessage());
			return errorResponse(ex, projectAccount);
		} catch (RuntimeException e) {
			logger.error("[updateProjectAccount] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("ProjectAccount's ID : " + projectAccount.getId());
			logger.error("ProjectAccount's ExternalID : " + projectAccount.getExternalId());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), projectAccount.getId(),
							MethodType.PUT, "externalId", projectAccount.getExternalId().toString()));
		}
	}

	/**
	 * Method called is the ressource does not exists during a PUT
	 * 
	 * @param projectAccount : the project acount to post
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 201 "Created" if projectAccount is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	public ResponseEntity updateCreationProjectAccount(ProjectAccount projectAccount) {
		try {
			Assert.notNull(projectAccount, "ProjectAccount cannot be null");

			// Validate projectAccount before the creation
			this.projectAccountValidator.validateUpdateCreation(projectAccount);

			// Prepare resource before persist
			this.projectAccountValidator.prepareResource(projectAccount, MethodType.PUT);
			try {
				this.projectAccountService.create(projectAccount);
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// projectAccount into the database
				logger.error("[updateCreationProjectAccount] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("ProjectAccount name : " + projectAccount.getName());
				logger.error("ProjectAccount Login : " + projectAccount.getLogin());
				logger.error("ProjectAccount Email : " + projectAccount.getEmail());

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), null, MethodType.PUT));
			}

			// Filter what the server must not return into the xml
			this.projectAccountValidator.filterResource(projectAccount, MethodType.PUT);

			logger.info("[updateCreationProjectAccount] PUT returning an response 201 for projectAccount with id= "
					+ projectAccount.getId());
			// Return response at the client
			return ResponseEntity.status(HttpStatus.CREATED).body(projectAccount);
		} catch (WebApplicationException ex) {
			logger.error("[updateCreationProjectAccount] WebApplicationException : " + ex.getMessage());

			return errorResponse(ex, projectAccount);
		} catch (RuntimeException e) {
			logger.error("[updateCreationProjectAccount] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Project account name : " + projectAccount.getName());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), projectAccount.getId(),
							MethodType.PUT));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity disableProjectAccount(Long projectAccountExternalId) {
		logger.debug("[disableProjectAccount] DELETE ProjectAccount");

		try {
			// Validate projectaccount's id sent
			projectAccountValidator.validateDelete(projectAccountExternalId);

			try {
				// update the project account into the database
				projectAccountService.softDeleteByExternalId(projectAccountExternalId);
			} catch (RuntimeException ex) {
				logger.error("[disableProjectAccount] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("ProjectAccount externalId : " + projectAccountExternalId);

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getMessage(), projectAccountExternalId, MethodType.DELETE));
			}
			// return HTTP 200
			logger.info("[disableProjectAccount] DELETE returning an response 200 for projectAccount with externalId= "
					+ projectAccountExternalId);
			return ResponseEntity.ok().build();

		} catch (WebApplicationException ex) {
			logger.error("[disableProjectAccount] WebApplicationException : " + ex.getMessage());
			return errorResponse(ex, null);
		} catch (RuntimeException e) {
			logger.error("[disableProjectAccount] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("ProjectAccount's externalID : " + projectAccountExternalId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), projectAccountExternalId,
							MethodType.DELETE));
		}
	}

	/**
	 * Method which allows to log the webapplicationException catched and return an
	 * {@link Error} to the webservice client.
	 * 
	 * @param exception : the WebApplicationException catched
	 * @return an {@link Error} object to the webservice client
	 */
	public ResponseEntity errorResponse(WebApplicationException exception, ProjectAccount projectAccount) {
		Assert.notNull(exception, "WebApplicationException can't be null");
		if (exception.getResponse().getEntity().getClass().equals(Error.class)) {
			Error error = (Error) exception.getResponse().getEntity();
			logger.error("[STATUS] : " + exception.getResponse().getStatus());
			logger.error("[LABEL] : " + error.getErrorLabel());
			logger.error("[ERROR CODE] : " + error.getErrorCode());
			logger.error("[MESSAGE] : " + error.getErrorMessage());
			logger.error("[METHOD TYPE] : " + error.getMethodType());
			logger.error("[RESOURCE ID] : " + error.getResourceId());
			logger.error("[RESOURCE EXTERNALID] : " + error.getResourceId());
			if (projectAccount != null) {
				logger.error("[RESOURCE MAIL] : " + projectAccount.getEmail());
				logger.error("[RESOURCE LOGIN] : " + projectAccount.getLogin());
			}
			return ResponseEntity.status(exception.getResponse().getStatus()).contentType(MediaType.APPLICATION_XML)
					.body(error);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
				.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
						"An unexpected error has occured : " + exception.getCause() + " " + exception.getMessage(),
						new Long(0),
						MethodType.GET));
	}
}
