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
import org.myec3.socle.core.domain.model.AgentManagedApplication;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.service.AgentManagedApplicationService;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestFormatException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.myec3.socle.ws.server.dto.ListHolder;
import org.myec3.socle.ws.server.resource.AdminGuResource;
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
 * {@link Profile} who have admin {@link Role} on GU {@link Application}.
 * 
 * @author Franck Mori <franck.mori@atosorigin.com>
 */
@RestController
@Scope("prototype")
public class AdminGuResourceImpl implements AdminGuResource {

	private static final Logger logger = LogManager.getLogger(AdminGuResourceImpl.class);

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
	 * {@link EmployeeProfile} objects
	 */
	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	@Autowired
	@Qualifier("agentManagedApplicationService")
	private AgentManagedApplicationService agentManagedApplicationService;

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
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getGuAdministrators(Long companyId, Long organismId) {

		logger.debug("[getGuAdministrators] GET GuAdministrators");

		@SuppressWarnings("unused")
		Company company = null;
		@SuppressWarnings("unused")
		Organism organism = null;

		try {

			if (null != companyId) {
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

			if (null != companyId) {
				List<EmployeeProfile> employeeProfilelist = new ArrayList<EmployeeProfile>();

				employeeProfilelist.addAll(employeeProfileService.findAllGuAdministratorByCompanyId(companyId));

				if (null != employeeProfilelist && !employeeProfilelist.isEmpty()) {

					// Populate employee's collections before returning the
					// response
					for (EmployeeProfile ep : employeeProfilelist) {
						this.employeeProfileValidator.populateResourceCollections(ep);
						this.employeeProfileValidator.filterResource(ep, MethodType.GET);
					}
					return ResponseEntity.ok()
							.contentType(MediaType.APPLICATION_XML).body(new ListHolder(employeeProfilelist));

				} else {
					logger.info("No employeeProfile was found with GU administrator rights");
					throw new WebResourceNotFoundException(companyId, MethodType.GET,
							"No employeeProfile was found with GU administrator rights for thid company Id : "
									+ companyId);
				}

			} else if (null != organismId) {
				List<AgentProfile> agentProfileList = new ArrayList<AgentProfile>();

				agentProfileList.addAll(agentProfileService.findAllGuAdministratorByOrganismId(organismId));

				List<AgentManagedApplication> agentManagedApplications = agentManagedApplicationService
						.findAgentManagedApplicationsByOrganism(getOrganism(organismId));

				if (agentManagedApplications != null) {
					for (AgentManagedApplication app : agentManagedApplications) {
						AgentProfile agent = app.getAgentProfile();
						if (!agentProfileList.contains(agent)) {
							agentProfileList.add(agent);
						}
					}
				}

				if (null != agentProfileList && !agentProfileList.isEmpty()) {

					// Populate employee's collections before returning the
					// response
					for (AgentProfile ap : agentProfileList) {
						this.agentProfileValidator.populateResourceCollections(ap);
						this.agentProfileValidator.filterResource(ap, MethodType.GET);
					}
					return ResponseEntity.ok()
							.contentType(MediaType.APPLICATION_XML).body(new ListHolder(agentProfileList));

				} else {
					logger.info("No agentProfile was found with GU administrator rights");
					throw new WebResourceNotFoundException(organismId, MethodType.GET,
							"No agentProfile was found with GU administrator rights for thid organism Id : "
									+ organismId);
				}

			} else {
				throw new WebRequestFormatException(0L, MethodType.GET, "Wrong paramaters sent in request.");
			}

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
	 * Find the Company with the given UID
	 * 
	 * @param companyId : Company's UID
	 * @return company with the matching UID
	 * @throws WebResourceNotFoundException : no Company could be found with this
	 *                                      UID
	 */
	private Company getCompany(Long companyId) throws WebResourceNotFoundException {

		Company company = this.companyService.findOne(companyId);

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

		Organism organism = this.organismService.findOne(organismId);

		if (organism == null) {

			logger.error("Unknown organism with uid:" + organismId);
			throw new WebResourceNotFoundException(organismId, MethodType.GET, "No organism found with this uid.");

		}

		return organism;
	}

}
