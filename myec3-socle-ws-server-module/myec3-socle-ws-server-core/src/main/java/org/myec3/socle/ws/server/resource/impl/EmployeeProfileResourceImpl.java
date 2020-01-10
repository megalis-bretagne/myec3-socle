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
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestNotAllowedException;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.ws.server.resource.EmployeeProfileResource;
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
 * {@link EmployeeProfile}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RestController
@Scope("prototype")
public class EmployeeProfileResourceImpl implements EmployeeProfileResource {

	private static final Logger logger = LogManager.getLogger(EmployeeProfileResourceImpl.class);

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Autowired
	@Qualifier("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationNotificationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Validator providing methods that validate {@link EmployeeProfile} objects
	 * before doing operation
	 */
	@Autowired
	@Qualifier("employeeProfileValidator")
	private ResourceValidatorManager<EmployeeProfile> employeeProfileValidator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getEmployeeProfile(Long employeeProfileId) {
		logger.debug("[getEmployeeProfile] GET EmployeeProfile with id " + employeeProfileId);
		try {
			// Validate the employeeProfile's Id before returning the employee
			this.employeeProfileValidator.validateGet(employeeProfileId);
			EmployeeProfile foundEmployee = new EmployeeProfile();
			try {
				// Get the employee from database
				foundEmployee = this.employeeProfileService.findOne(employeeProfileId);
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// company into the database
				logger.error("[getEmployeeProfile] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Employee profile ID : " + employeeProfileId);

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), employeeProfileId,
								MethodType.GET));
			}

			// Populate employee's collections before returning the response
			this.employeeProfileValidator.populateResourceCollections(foundEmployee);

			// Filter what the server must not return into the xml
			this.employeeProfileValidator.filterResource(foundEmployee, MethodType.GET);

			logger.info("[getEmployeeProfile] GET returning an response 200");
			return ResponseEntity.ok().body(foundEmployee);

		} catch (WebApplicationException ex) {
			logger.error("[getEmployeeProfile] WebApplicationException : " + ex.getCause() + " " + ex.getMessage());
			return errorResponse(ex, null);
		} catch (RuntimeException e) {

			logger.error("[getEmployeeProfile] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Employee profile ID : " + employeeProfileId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(),
							employeeProfileId, MethodType.GET));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity createEmployeeProfile(EmployeeProfile employee) {
		logger.debug("[createEmployeeProfile] POST EmployeeProfile");
		try {
			this.employeeProfileValidator.validateCreate(employee);

			this.employeeProfileValidator.prepareResource(employee, MethodType.POST);
			try {
				// Create new employee into the database
				this.employeeProfileService.create(employee);

				// Notify the creation to external applications
				this.synchronizationNotificationService.notifyCreation(employee);
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// company into the database
				logger.error("[createEmployeeProfile] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Employee profile ID : " + employee.getId());
				logger.error("Employee profile ExternalId : " + employee.getExternalId());
				logger.error("Employee profile Login : " + employee.getUsername());
				logger.error("Employee profile Mail : " + employee.getEmail());

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), employee.getId(),
								MethodType.POST, "externalId", employee.getExternalId().toString()));
			}

			// Filter what the server must not return into the xml
			this.employeeProfileValidator.filterResource(employee, MethodType.POST);

			logger.info("[createEmployeeProfile] POST returning an response 201");
			return ResponseEntity.status(HttpStatus.CREATED).body(employee);

		} catch (WebApplicationException ex) {
			logger.error("[createEmployeeProfile] WebApplicationException : " + ex.getCause() + " " + ex.getMessage());
			return errorResponse(ex, employee);
		} catch (RuntimeException e) {

			logger.error("[createEmployeeProfile] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Employee profile ID : " + employee.getId());
			logger.error("Employee profile ExternalId : " + employee.getExternalId());
			logger.error("Employee profile Login : " + employee.getUsername());
			logger.error("Employee profile Mail : " + employee.getEmail());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), employee.getId(), MethodType.POST,
							"externalId", employee.getExternalId().toString()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity updateEmployeeProfile(Long employeeProfileId, EmployeeProfile employee) {
		logger.debug("[updateEmployeeProfile] PUT EmployeeProfile");
		try {
			EmployeeProfile foundEmployee = null;

			// We check if the employee exists
			if (employee.getId().equals(new Long(-1))) {
				foundEmployee = this.employeeProfileService.findByExternalId(employee.getExternalId());
			} else {
				foundEmployee = this.employeeProfileService.findOne(employee.getId());
			}

			if (foundEmployee == null) {
				return this.updateCreationEmployeeProfile(employee);
			} else {

				// Validate employeeProfile before update the employee
				this.employeeProfileValidator.validateUpdate(employee, employeeProfileId, foundEmployee);
				try {
					this.employeeProfileService.update(employee);

					// Notify the update to external applications
					this.synchronizationNotificationService.notifyUpdate(employee);
				} catch (RuntimeException ex) {
					// Internal server error during the creation of the new
					// company into the database
					logger.error("[updateEmployeeProfile] Internal Server Error : ");
					logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
					logger.error("Employee profile ID : " + employeeProfileId);
					logger.error("Employee profile Login : " + employee.getUsername());
					logger.error("Employee profile Mail : " + employee.getEmail());

					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.contentType(MediaType.APPLICATION_XML)
							.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
									"Database error : " + ex.getCause() + " " + ex.getMessage(), employee.getId(),
									MethodType.PUT, "externalId", employee.getExternalId().toString()));
				}

				// Filter what the server must not return into the xml
				this.employeeProfileValidator.filterResource(employee, MethodType.PUT);

				logger.info("[updateEmployeeProfile] PUT returning an response 200");
				return ResponseEntity.ok().body(employee);
			}
		} catch (WebApplicationException ex) {
			logger.error("[updateEmployeeProfile] WebApplicationException : " + ex.getCause() + " " + ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (RuntimeException e) {

			logger.error("[updateEmployeeProfile] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Employee profile ID : " + employee.getId());
			logger.error("Employee profile ExternalId : " + employee.getExternalId());
			logger.error("Employee profile Login : " + employee.getUsername());
			logger.error("Employee profile Mail : " + employee.getEmail());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), employee.getId(), MethodType.PUT,
							"externalId", employee.getExternalId().toString()));
		}
	}

	/**
	 * Method called is the ressource does not exists during a PUT
	 * 
	 * @param employee : employee to post
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 201 "Created" if employeeProfile is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	public ResponseEntity updateCreationEmployeeProfile(EmployeeProfile employee) {
		logger.debug("[updateCreationEmployeeProfile] PUT like POST EmployeeProfile");
		try {
			Assert.notNull(employee, "EmployeeProfile cannot be null");

			// Validate employeeProfile before the creation
			this.employeeProfileValidator.validateUpdateCreation(employee);

			this.employeeProfileValidator.prepareResource(employee, MethodType.POST);
			try {
				this.employeeProfileService.create(employee);

				// Notify the creation to external applications
				this.synchronizationNotificationService.notifyCreation(employee);
			} catch (RuntimeException ex) {
				// Internal server error during the creation of the new
				// employee into the database
				logger.error("[updateCreationEmployeeProfile] Internal Server Error : ");
				logger.error("Error Message :" + "Database error : " + ex.getCause() + " " + ex.getMessage());
				logger.error("Employee profile name : " + employee.getName());
				logger.error("Employee profile Login : " + employee.getUsername());
				logger.error("Employee profile Mail : " + employee.getEmail());

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.contentType(MediaType.APPLICATION_XML)
						.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
								"Database error : " + ex.getCause() + " " + ex.getMessage(), null, MethodType.PUT));
			}

			// Filter what the server must not return into the xml
			this.employeeProfileValidator.filterResource(employee, MethodType.PUT);

			logger.info("[updateCreationEmployeeProfile] PUT returning an response 201");
			// Return response at the client
			return ResponseEntity.status(HttpStatus.CREATED).body(employee);
		} catch (WebApplicationException ex) {
			logger.error("[updateCreationEmployeeProfile] WebApplicationException : " + ex.getCause() + " "
					+ ex.getMessage());
			return errorResponse(ex, employee);
		} catch (RuntimeException e) {
			logger.error("[updateCreationEmployeeProfile] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Employee profile name : " + employee.getName());
			logger.error("Employee profile Login : " + employee.getUsername());
			logger.error("Employee profile Mail : " + employee.getEmail());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getMessage(), employee.getId(), MethodType.PUT)
									.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity deleteEmployeeProfile(Long employeeProfileId) {
		logger.debug("[deleteEmployeeProfile] DELETE EmployeeProfile : request not allowed");
		throw new WebRequestNotAllowedException(employeeProfileId, MethodType.DELETE);
	}

	/**
	 * Method which allows to log the webapplicationException catched and return an
	 * {@link Error} to the webservice client.
	 * 
	 * @param exception : the WebApplicationException catched
	 * @return an {@link Error} object to the webservice client
	 */
	public ResponseEntity errorResponse(WebApplicationException exception, EmployeeProfile employeeProfile) {
		Assert.notNull(exception, "WebApplicationException can't be null");
		if (exception.getResponse().getEntity().getClass().equals(Error.class)) {
			Error error = (Error) exception.getResponse().getEntity();
			logger.error("[STATUS] : " + exception.getResponse().getStatus());
			logger.error("[LABEL] : " + error.getErrorLabel());
			logger.error("[ERROR CODE] : " + error.getErrorCode());
			logger.error("[MESSAGE] : " + error.getErrorMessage());
			logger.error("[METHOD TYPE] : " + error.getMethodType());
			logger.error("[RESOURCE ID] : " + error.getResourceId());
			if (employeeProfile != null) {
				logger.error("[RESOURCE MAIL] : " + employeeProfile.getEmail());
				logger.error("[RESOURCE LOGIN] : " + employeeProfile.getUsername());
			}
		}

		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

}
