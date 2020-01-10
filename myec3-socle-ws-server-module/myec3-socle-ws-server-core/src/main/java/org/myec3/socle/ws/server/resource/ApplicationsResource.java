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
package org.myec3.socle.ws.server.resource;

import org.myec3.socle.core.domain.model.Application;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface wich defines specific REST methods to manage {@link Application}.
 * 
 * @author brian clozel <brian.clozel@atosorigin.com>
 */
@RequestMapping("/applications/")
public interface ApplicationsResource {

	/**
	 * Return a list of applications. Default behavior: return the list of all
	 * applications (i.e. /applications/?type=all)
	 * 
	 * Optional query parameters for search :
	 * 
	 * @param employeeId : (needed when type=subscribed or subscribable) the
	 *                   employee UID
	 * @param agentId    : (needed when type=subscribed or subscribable) the agent
	 *                   UID
	 * @param companyId  : the company UID
	 * @param organismId : the company UID
	 * 
	 * @param type       : string indicating what kind of applications are
	 *                   requested:
	 *                   <ul>
	 *                   <li>subscribed: subscribed by the agent/employee's
	 *                   organism</li>
	 *                   <li>subscribable: not subscribed already, but subscribable
	 *                   by the agent/employee's organism</li>
	 *                   <li>all (default value): all applications available</li>
	 *                   </ul>
	 * 
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if profile is known and body containing a List of
	 *         Application</li>
	 *         <li>Status 404 "NOT FOUND" if no profile exists with this UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 * @see javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity getApplications(@RequestParam("agentId") Long agentId,
			@RequestParam("employeeId") Long employeeId,
			@RequestParam("companyId") Long companyId,
			@RequestParam("organismId") Long organismId,
			@RequestParam("type") String type);
}
