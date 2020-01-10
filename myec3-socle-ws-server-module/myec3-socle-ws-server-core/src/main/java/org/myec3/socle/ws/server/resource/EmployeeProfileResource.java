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

import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Interface wich defines specific REST methods to manage
 * {@link EmployeeProfile}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RequestMapping("/employee/")
public interface EmployeeProfileResource {

	/**
	 * This method allows to retrieve an {@link EmployeeProfile} from the database
	 * and returned it to the web service client
	 * 
	 * @param employeeProfileId : the id of the {@link EmployeeProfile} to return
	 * @return HTTP ResponseEntity and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link EmployeeProfile} is known</li>
	 *         <li>Status 404 "NOT FOUND" if no {@link EmployeeProfile} exists with
	 *         this UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{employeeProfileId}", method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getEmployeeProfile(
			@PathVariable("employeeProfileId") Long employeeProfileId);

	/**
	 * Create a new {@link EmployeeProfile} into the database.
	 * 
	 * @param employee : the {@link EmployeeProfile} to create.
	 * @return HTTP ResponseEntity and XML body with:
	 *         <ul>
	 *         <li>Status 201 "Created" if {@link EmployeeProfile} is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity createEmployeeProfile(@RequestBody EmployeeProfile employee);

	/**
	 * Method called in order to update an {@link EmployeeProfile} from database
	 * 
	 * @param employeeProfileId : UID of the {@link EmployeeProfile} to update
	 * @param employee          : the {@link EmployeeProfile} object updated
	 * 
	 * @return HTTP ResponseEntity and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link EmployeeProfile} is updated</li>
	 *         <li>Status 201 "Created" if {@link EmployeeProfile} is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{employeeProfileId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity updateEmployeeProfile(
			@PathVariable("employeeProfileId") Long employeeProfileId,
			@RequestBody EmployeeProfile employee);

	/**
	 * This method allows to delete an {@link EmployeeProfile} from the database.
	 * 
	 * @param employeeProfileId : the UID of the {@link EmployeeProfile} to delete
	 * @return HTTP ResponseEntity and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link EmployeeProfile} is deleted</li>
	 *         <li>Status 404 "Not Found" if {@link EmployeeProfile} is not
	 *         found</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{employeeProfileId}", method = RequestMethod.DELETE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity deleteEmployeeProfile(
			@PathVariable("employeeProfileId") Long employeeProfileId);

}
