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

import org.myec3.socle.core.domain.model.ProjectAccount;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Interface wich defines specific REST methods to manage {@link ProjectAccount}
 * .
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RequestMapping("/project-account/")
public interface ProjectAccountResource {

	/**
	 * This method allows to retrieve a {@link ProjectAccount} from the database and
	 * returned it to the web service client.
	 * 
	 * @param projectAccountId : the UID of the {@link ProjectAccount} to return.
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link ProjectAccount} is found</li>
	 *         <li>Status 400 "bad request" if the request is not correct</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{projectAccountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	ProjectAccount getProjectAccount(@PathVariable("projectAccountId") Long projectAccountId);

	/**
	 * This method allows to create a new {@link ProjectAccount} into the database.
	 * It returns the {@link ProjectAccount} created into an XML + an Http Status =
	 * 201
	 * 
	 * @param projectAccount : the {@link ProjectAccount} to create into the
	 *                       database
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 201 "Created" if {@link ProjectAccount} is created</li>
	 *         <li>Status 400 "bad request" if the request is not correct</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity createProjectAccount(@RequestBody ProjectAccount projectAccount);

	/**
	 * This method allows to update a {@link ProjectAccount} from the database. It
	 * returns the {@link ProjectAccount} updated into an XML + an Http Status 200
	 * 
	 * @param projectAccountId : the UID of the {@link ProjectAccount} to update.
	 * @param projectAccount   : the {@link ProjectAccount} updated.
	 * 
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link ProjectAccount} is updated</li>
	 *         <li>Status 201 "Created" if {@link ProjectAccount} is created</li>
	 *         <li>Status 400 "bad request" if the request is not correct</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{projectAccountId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity updateProjectAccount(@PathVariable("projectAccountId") Long projectAccountId,
			@RequestBody ProjectAccount projectAccount);

	/**
	 * This method allows to disable (set attribute enabled to false) a
	 * {@link ProjectAccount} from the database. It returns only an Http Status 200
	 * in case of success.
	 * 
	 * @param projectAccountExternalId : the external UID of {@link ProjectAccount}
	 *                                 to disable.
	 * 
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link ProjectAccount} is disabled</li>
	 *         <li>Status 400 "bad request" if the request is not correct</li>
	 *         <li>Status 404 "Not Found" if {@link ProjectAccount} is not
	 *         found</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{projectAccountExternalId}", method = RequestMethod.DELETE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity disableProjectAccount(
			@PathVariable("projectAccountExternalId") Long projectAccountExternalId);
}
