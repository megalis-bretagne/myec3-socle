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
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestFormatException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.ws.server.dto.ListHolder;
import org.myec3.socle.ws.server.resource.ApplicationsResource;
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
 * Concrete implementation providing specific methods to manage
 * {@link Application}.
 * 
 * @author brian clozel <brian.clozel@atosorigin.com>
 */
@RestController
@Scope("prototype")
public class ApplicationsResourceImpl implements ApplicationsResource {

	private static final Logger logger = LogManager.getLogger(ApplicationsResourceImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * Validator providing methods that validate {@link EmployeeProfile} objects
	 * before doing operation
	 */
	@Autowired
	@Qualifier("employeeProfileValidator")
	private ResourceValidatorManager<EmployeeProfile> employeeProfileValidator;

	/**
	 * Validator providing methods that validate {@link AgentProfile} objects before
	 * doing operation
	 */
	@Autowired
	@Qualifier("agentProfileValidator")
	private ResourceValidatorManager<AgentProfile> agentProfileValidator;

	/**
	 * Validator providing methods that validate {@link Company} objects before
	 * doing operation
	 */
	@Autowired
	@Qualifier("companyValidator")
	private ResourceValidatorManager<Company> companyValidator;

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
	public final ResponseEntity getApplications(Long agentId, Long employeeId, Long companyId, Long organismId,
			String type) {

		logger.debug("[getApplications] GET Applications");

		List<Application> listapps = new ArrayList<Application>();
		EmployeeProfile employee = null;
		AgentProfile agent = null;
		@SuppressWarnings("unused")
		Company company = null;
		@SuppressWarnings("unused")
		Organism organism = null;

		boolean profileIdIsNotNull = (agentId != null || employeeId != null);

		try {

			/*
			 * Validate then retrieve profile info from database
			 */

			// retrieve agentprofile from database
			if (agentId != null) {

				// Validate the agentProfile's Id before returning the agent
				this.agentProfileValidator.validateGet(agentId);

				try {
					// retrieve the agent from database
					agent = getAgentProfile(agentId);

				} catch (RuntimeException ex) {
					// Internal server error while querying the database
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.contentType(MediaType.APPLICATION_XML)
							.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
									"Database error : " + ex.getCause() + " " + ex.getMessage(), agentId,
									MethodType.GET));
				}

				// retrieve employeeprofile from database
			} else if (employeeId != null) {

				// Validate the employeeProfile's Id before returning the
				// employee
				this.employeeProfileValidator.validateGet(employeeId);

				try {
					// retrieve the employee from database
					employee = getEmployeeProfile(employeeId);

				} catch (RuntimeException ex) {
					// Internal server error while querying the database
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.contentType(MediaType.APPLICATION_XML)
							.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
									"Database error : " + ex.getCause() + " " + ex.getMessage(), employeeId,
									MethodType.GET));
				}

			} else if (null != companyId) {
				// Validate the companyId's Id before returning the company
				this.companyValidator.validateGet(companyId);

				try {
					// retrieve the company from database
					company = getCompany(companyId);

				} catch (RuntimeException ex) {
					// Internal server error while querying the database
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.contentType(MediaType.APPLICATION_XML)
							.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
									"Database error : " + ex.getCause() + " " + ex.getMessage(), companyId,
									MethodType.GET));
				}

			} else if (null != organismId) {
				// Validate the organismId's Id before returning the organism
				this.organismValidator.validateGet(organismId);

				try {
					// retrieve the organism from database
					organism = getOrganism(organismId);

				} catch (RuntimeException ex) {
					// Internal server error while querying the database
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.contentType(MediaType.APPLICATION_XML)
							.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
									"Database error : " + ex.getCause() + " " + ex.getMessage(), organismId,
									MethodType.GET));
				}
			}

			/*
			 * Get corresponding applications (given the query parameters)
			 */

			// get all applications available
			if (type == null || type.equals("all")) {

				if (null != companyId) {
					listapps.addAll(
							applicationService.findAllApplicationByStructure(companyService.findOne(companyId)));
				} else if (null != organismId) {
					listapps.addAll(
							applicationService.findAllApplicationByStructure(organismService.findOne(organismId)));
				} else {
					listapps.addAll(applicationService.findAll());
				}

				// get applications subscribed by a profile
			} else if (type.equals("subscribed") && profileIdIsNotNull) {

				// get applications subscribed by an agent
				if (agent != null) {

					this.agentProfileValidator.filterResource(agent, MethodType.GET);

					for (Role role : agent.getRoles()) {
						if (!listapps.contains(role.getApplication())) {
							listapps.add(role.getApplication());
						}
					}

					// get applications subscribed by an employee
				} else if (employee != null) {

					for (Role role : roleService.findAllRoleByProfile(employee)) {
						if (!listapps.contains(role.getApplication())) {
							listapps.add(role.getApplication());
						}
					}

				}

				// get applications subscribable by the profile's structure
			} else if (type.equals("subscribable") && profileIdIsNotNull) {

				// get applications subscribed by an agent
				if (agent != null) {

					StructureType structType = agent.getOrganismDepartment().getOrganism().getStructureType();

					listapps.addAll(
							applicationService.findAllApplicationSubscribableByStructureType(structType, Boolean.TRUE));

					// get applications subscribed by an employee
				} else if (employeeId != null) {

				}

				// wrong parameters
			} else {

				throw new WebRequestFormatException(0L, MethodType.GET, "Wrong paramaters sent in request.");

			}

			return ResponseEntity.ok().body(new ListHolder(listapps));

		} catch (WebApplicationException ex) {
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_XML)
					.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
							"An unexpected error has occured : " + e.getCause() + " " + e.getMessage(), 0L,
							MethodType.POST));
		}

	}

	/**
	 * Find the EmployeeProfile with the given UID
	 * 
	 * @param employeeId : EmployeeProfile's UID
	 * @return employeeProfile with the matching UID
	 * @throws WebResourceNotFoundException : no EmployeeProfile could be found with
	 *                                      this UID
	 */
	private EmployeeProfile getEmployeeProfile(Long employeeId) throws WebResourceNotFoundException {

		EmployeeProfile employee = employeeProfileService.findOne(employeeId);

		if (employee == null) {

			logger.error("Unknown employee with uid:" + employeeId);
			throw new WebResourceNotFoundException(employeeId, MethodType.GET, "No employee found with this uid.");

		}

		return employee;
	}

	/**
	 * Find the AgentProfile with the given UID
	 * 
	 * @param employeeId : AgentProfile's UID
	 * @return agentProfile with the matching UID
	 * @throws WebResourceNotFoundException : no AgentProfile could be found with
	 *                                      this UID
	 */
	private AgentProfile getAgentProfile(Long agentId) throws WebResourceNotFoundException {

		AgentProfile agent = agentProfileService.findOne(agentId);

		if (agent == null) {

			logger.error("Unknown agent with uid:" + agentId);
			throw new WebResourceNotFoundException(agentId, MethodType.GET, "No agent found with this uid.");

		}

		return agent;
	}

	/**
	 * Find the Company with the given UID
	 * 
	 * @param companyId : Company's UID
	 * @return company with the matching UID
	 * @throws WebResourceNotFoundException : no Company could be found with this
	 *                                      UID
	 */
	private Company getCompany(Long companyId) throws WebResourceNotFoundException {

		Company company = companyService.findOne(companyId);

		if (company == null) {

			logger.error("Unknown company with uid:" + companyId);
			throw new WebResourceNotFoundException(companyId, MethodType.GET, "No company found with this uid.");

		}

		return company;
	}

	/**
	 * Find the Organism with the given UID
	 * 
	 * @param companyId : Organism's UID
	 * @return company with the matching UID
	 * @throws WebResourceNotFoundException : no Organism could be found with this
	 *                                      UID
	 */
	private Organism getOrganism(Long organismId) throws WebResourceNotFoundException {

		Organism organism = organismService.findOne(organismId);

		if (organism == null) {

			logger.error("Unknown organism with uid:" + organismId);
			throw new WebResourceNotFoundException(organismId, MethodType.GET, "No organism found with this uid.");

		}

		return organism;
	}
}
