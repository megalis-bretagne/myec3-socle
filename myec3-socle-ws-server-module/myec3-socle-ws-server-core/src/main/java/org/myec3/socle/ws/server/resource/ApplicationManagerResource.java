package org.myec3.socle.ws.server.resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.myec3.socle.core.domain.model.Application;

/**
 * Interface which defines specific REST methods to manage {@link Application} 
 * 
 * @author a602499 on 29/12/2016.
 */
@RequestMapping("/appmanager/")
public interface ApplicationManagerResource {

	/**
	 * Return a list of Applications managed by an Agent.
	 *
	 * @param agentId  : the agent ID
	 *
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if agent is known and body containing a
	 *         List of Applications</li>
	 *         <li>Status 400 "BAD REQUEST" if agentId is null</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.BAD_REQUEST
	 * @see javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
	 */
	@RequestMapping(value = "appsonly", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity getAppManagedByAgent(@PathVariable("agentId") Long agentId);


	/**
	 * Return a list of AgentManagedApplication (Agent who managed an Application) of an Organism.
	 *
	 * @param organismId  : the Organism ID
	 *
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if organism is known and body containing a
	 *         List of AgentManagedApplication</li>
	 *         <li>Status 400 "BAD REQUEST" if organismId is null</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.BAD_REQUEST
	 * @see javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity getAllAppManagersByOrganism(@PathVariable("organismId") Long organismId);
}
