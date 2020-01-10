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

import org.myec3.socle.core.domain.model.Organism;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Interface wich defines specific REST methods to manage {@link Organism}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@RequestMapping("/")
public interface OrganismResource {

	/**
	 * This method allows to return an {@link Organism} contained in database by
	 * using is UID.
	 * 
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link Organism} is known</li>
	 *         <li>Status 404 "NOT FOUND" if no {@link Organism} exists with this
	 *         UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "organism/{organismId}", method = RequestMethod.GET, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getOrganism(@PathVariable("organismId") Long organismId);

	/**
	 * This method allows to return all the {@link Organism} contained into the
	 * database.
	 * 
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link Organism} is known and body containing
	 *         a List of {@link Organism}</li>
	 *         <li>Status 404 "NOT FOUND" if no {@link Organism} exists with this
	 *         UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "organisms/", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity getAllOrganism();

	/**
	 * This method allows to create an {@link Organism} into the database. This
	 * method is not allowed for this type of resource.
	 * 
	 * @param organism : the {@link Organism} to create.
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 201 "Created" if {@link Organism} is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "organism/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity createOrganism(@RequestBody Organism organism);

	/**
	 * This method allows to update an {@link Organism} from the database
	 * 
	 * @param organismId : the UID of the {@link Organism} to update.
	 * @param organism   : the {@link Organism} updated.
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link Organism} is updated</li>
	 *         <li>Status 201 "Created" if {@link Organism} is created</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "organism/{organismId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity updateOrganism(@PathVariable("organismId") Long organismId,
			@RequestBody Organism organism);

	/**
	 * This method allows to delete an {@link Organism} from the database by using
	 * its UID. This method is not allowed for this type of resource.
	 * 
	 * @param organismId : the UID of the {@link Organism} to delete
	 * @return HTTP Response and XML body with:
	 *         <ul>
	 *         <li>Status 200 "OK" if {@link Organism} is deleted</li>
	 *         <li>Status 404 "Not Found" if no {@link Organism} exists with the
	 *         given UID</li>
	 *         <li>Status 500 "INTERNAL SERVER ERROR" (if an error occurs during
	 *         process) and body containing an Error object</li>
	 *         <li>Other statuses : errors</li>
	 *         </ul>
	 */
	@RequestMapping(value = "organism/{organismId}", method = RequestMethod.DELETE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	ResponseEntity deleteOrganism(@PathVariable("organismId") Long organismId);

}
