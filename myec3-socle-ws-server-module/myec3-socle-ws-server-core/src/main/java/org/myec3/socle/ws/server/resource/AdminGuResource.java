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

import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Interface which defines specific REST methods to manage {@link Profile} who
 * have an admin {@link Role} on GU application.
 * 
 * @author Franck Mori <franck.mori@atosorigin.com>
 */
@RequestMapping("/admingu/")
public interface AdminGuResource {

	/**
	 * Return a list of GU administrators.
	 * 
	 * @param companyId  : the company UID
	 * @param organismId : the company UID
	 * 
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if administrator is known and body containing a
	 *         List of Users</li>
	 *         <li>Status 404 "NOT FOUND" if no GU administrator exists for this
	 *         Structure UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 * @see javax.ws.rs.core.Response.Status.OK
	 * @see javax.ws.rs.core.Response.Status.NOT_FOUND
	 * @see javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity getGuAdministrators(
			@PathVariable("companyId") Long companyId,
			@PathVariable("organismId") Long organismId);
}
