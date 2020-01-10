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

import org.myec3.socle.core.domain.model.Company;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Interface wich defines REST methods specific to manage {@link Company}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RequestMapping("/company/")
public interface CompanyResource {

	/**
	 * This method allows to get a {@link Company} from the database and returned it
	 * to the web service client
	 * 
	 * @param companyId : the id of the company to return
	 * @return ResponseEntity : the company to return at the web service client
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link Company} is known</li>
	 *         <li>Status 404 "NOT FOUND" if no {@link Company} exists with this
	 *         UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{companyId}", method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getCompany(@PathVariable("companyId") Long companyId);

	/**
	 * Create a new {@link Company} into the database.
	 * 
	 * @param company : the {@link Company} to create
	 * @return ResponseEntity : the ResponseEntity to return at the web service
	 *         client
	 *         <ul>
	 *         <li>Status 201 "Created" if company is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity createCompany(@RequestBody Company company);

	/**
	 * This method allows to update a company from the database.
	 * 
	 * @param companyId : the ID of the {@link Company} to update
	 * @param company   : the {@link Company} to update
	 * @return HTTP ResponseEntity and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if company is updated</li>
	 *         <li>Status 201 "Created" if company is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{companyId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity updateCompany(@PathVariable("companyId") Long companyId,
			@RequestBody Company company);

	/**
	 * This method allows to delete a {@link Company} from the database
	 * 
	 * @param companyId : the ID of the {@link Company} to delete
	 * @return HTTP ResponseEntity and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if company is deleted</li>
	 *         <li>Status 404 "Not Found" if company is not found</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{companyId}", method = RequestMethod.DELETE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity deleteCompany(@PathVariable("companyId") Long companyId);

}
