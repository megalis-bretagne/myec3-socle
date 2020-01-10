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
package org.myec3.socle.ws.server.resource;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Interface which defines specific REST methods to {@link AgentProfile}
 *
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RequestMapping("/agent/")
public interface AgentProfileResource {

	/**
	 * This method allows to get an AgentProfile from the database and returned it
	 * to the web service client
	 *
	 * @param agentProfileId : the id of the agentProfile to return
	 * @return HTTP ResponseEntity and XML body with:
	 * <ul>
	 * <li>Status 200 "OK" if agentProfile is known</li>
	 * <li>Status 404 "NOT FOUND" if no agentProfile exists with this
	 * UID</li>
	 * <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 * process) and body containing an Error object</li>
	 * <li>Other statuses : errors</li>
	 * </ul>
	 */
	@RequestMapping(value = "{agentProfileId}", method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getAgentProfile(@PathVariable("agentProfileId") Long agentProfileId,
								   @RequestParam("withHiddenRoles") Boolean withHiddenRoles);

	/**
	 * Method called in order to create a new agentProfile into database
	 * (Unauthorized method)
	 *
	 * @param agent : the {@link AgentProfile} to create
	 * @return HTTP ResponseEntity and XML body with:
	 * <ul>
	 * <li>Status 201 "Created" if agentProfile is created</li>
	 * <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 * process) and body containing an Error object</li>
	 * <li>Other statuses : errors</li>
	 * </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity createAgentProfile(@RequestBody AgentProfile agent);

	/**
	 * Method called in order to update an {@link AgentProfile} from database
	 *
	 * @param agentProfileId : Id of agentProfile to update
	 * @param agent          : the {@link AgentProfile} object
	 * @return HTTP ResponseEntity and XML body with:
	 * <ul>
	 * <li>Status 200 "OK" if agentProfile is updated</li>
	 * <li>Status 201 "Created" if agentProfile is created</li>
	 * <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 * process) and body containing an Error object</li>
	 * <li>Other statuses : errors</li>
	 * </ul>
	 */
	@RequestMapping(value = "{agentProfileId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity updateAgentProfile(@PathVariable("agentProfileId") Long agentProfileId,
									  @RequestBody AgentProfile agent);

	/**
	 * Method called in order to delete an agentProfile from database (Unauthorized
	 * method)
	 *
	 * @param agentProfileId : Id of agentProfile to delete
	 * @return HTTP ResponseEntity and XML body with:
	 * <ul>
	 * <li>Status 200 "OK" if agentProfile is deleted</li>
	 * <li>Status 404 "Not Found" if agentProfile is not found</li>
	 * <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 * process) and body containing an Error object</li>
	 * <li>Other statuses : errors</li>
	 * </ul>
	 */
	@RequestMapping(value = "{agentProfileId}", method = RequestMethod.DELETE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity deleteAgentProfile(@PathVariable("agentProfileId") Long agentProfileId);

	/**
	 * This method allows to get a dashbord of AgentProfile from the database and returned it
	 * to the web service client
	 *
	 * @param agentProfileId : Id of agentProfile to delete
	 * @return HTTP ResponseEntity and XML body with:
	 * <ul>
	 * <li>Status 200 "OK" if agentProfile is deleted</li>
	 * <li>Status 404 "Not Found" if agentProfile is not found</li>
	 * process) and body containing an Error object</li>
	 * <li>Other statuses : errors</li>
	 * </ul>
	 */
	//@CrossOrigin Only for local tests
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity getAgentDashboard(@RequestHeader("uid") Long agentProfileId);

	/**
	 * This method allows to create or update a dashboard of AgentProfile from the database and returned it
	 * to the web service client
	 *
	 * @param agentProfileId : Id of agentProfile
	 * @return HTTP ResponseEntity and XML body with:
	 * <ul>
	 * <li>Status 200 "OK" if agentProfile is created or updated</li>
	 * <li>Status 404 "Not Found" if agentProfile is not found</li>
	 * <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 * process) and body containing an Error object</li>
	 * <li>Other statuses: errors</li>
	 * </ul>
	 */
	//@CrossOrigin Only for local tests
	@RequestMapping(value = "/dashboard", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity createAgentDashboard(@RequestHeader("uid") Long agentProfileId, @RequestBody String dashboard);
	

	/** 
	 * Return the organismdId and ApiKey of the organism of agent
	 * 
	 * @param agentProfileId : The AgentProfile Id
	 * 
	 * @return HTTP ResponseEntity and XML body with:
	 * <ul>
	 * <li>Status 200 "OK" organismDepartment is found and body containing an OrganismDepartmentIdAndApiKeyDto</li>
	 * <li>Status 404 "NOT FOUND" if organismDepartment not found</li>
	 * <li>Other statuses: errors</li>
	 * </ul>
	 */
	//@CrossOrigin Only for local tests
	@RequestMapping(value = "/organism", method = RequestMethod.GET)
	ResponseEntity getOrganismDepartmentAndApiKeyByAgentProfileId(@RequestHeader("uid") Long agentProfileId);

}
