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

import org.myec3.socle.core.domain.model.Establishment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface which defines REST methods specific to manage {@link Establishment}
 * .
 * 
 * @author Ludovic LAVIGNE <ludovic.lavigne@worldline.com>
 */
@RequestMapping("/establishment/")
public interface EstablishmentResource {

	/**
	 * This method allows to get a {@link Establishment} from the database and
	 * returned it to the web service client
	 * 
	 * @param establishmentId : the id of the establishment to return
	 * @return Response : the establishment to return at the web service client
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link Establishment} is known</li>
	 *         <li>Status 404 "NOT FOUND" if no {@link Establishment} exists with
	 *         this UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{establishmentId}", method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getEstablishment(@PathVariable("establishmentId") Long establishmentId);

	/**
	 * This method allows to return all the {@link Establishment} depending of
	 * criteria.
	 * 
	 * @param companyId  : the id of a given company (mandatory)
	 * @param headOffice : retrieve only the headOffice of the company
	 * 
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link Establishment} is known and body
	 *         containing a List of {@link Establishment}</li>
	 *         <li>Status 404 "NOT FOUND" if no {@link Establishment} exists with
	 *         this UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getEstablishmentsByCriteria(
			@RequestParam("companyId") Long companyId,
			@RequestParam(value = "headOffice", defaultValue = "false", required = false) Boolean headOffice);

	/**
	 * Create a new {@link Establishment} into the database.
	 * 
	 * @param establishment : the {@link Establishment} to create
	 * @return Response : the response to return at the web service client
	 *         <ul>
	 *         <li>Status 201 "Created" if establishment is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity createEstablishment(@RequestBody Establishment establishment);

	/**
	 * This method allows to update a establishment from the database.
	 * 
	 * @param establishmentId : the ID of the {@link Establishment} to update
	 * @param company         : the {@link Establishment} to update
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if company is updated</li>
	 *         <li>Status 201 "Created" if company is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{establishmentId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity updateEstablishment(
			@PathVariable("establishmentId") Long establishmentId,
			@RequestBody Establishment establishment);

	/**
	 * This method allows to delete a {@link Establishment} from the database
	 * 
	 * @param companyId : the ID of the {@link Establishment} to delete
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if company is deleted</li>
	 *         <li>Status 404 "Not Found" if company is not found</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "{establishmentId}", method = RequestMethod.DELETE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity deleteEstablishment(
			@PathVariable("establishmentId") Long establishmentId);

	/**
	 * This method allows to get a {@link Establishment} from the API MPS (not our
	 * Database) and returned it to the web service client
	 * 
	 * @param establishmentSiret : the siret of the establishment to return
	 * @return Response : the establishment to return at the web service client
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link Establishment} is known</li>
	 *         <li>Status 404 "NOT FOUND" if no {@link Establishment} exists with
	 *         this Siret</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "/MPS/{establishmentSiret}", method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getEstablishmentFromMPS(
			@PathVariable("establishmentSiret") String establishmentSiret);

}
