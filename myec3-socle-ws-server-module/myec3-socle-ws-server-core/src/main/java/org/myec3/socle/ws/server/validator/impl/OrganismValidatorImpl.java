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
package org.myec3.socle.ws.server.validator.impl;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.StructureTypeService;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.exception.WebRequestSyntaxException;
import org.myec3.socle.core.sync.api.exception.WebResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * Implementation of the validator that make a validation on {@link Organism}
 * during REST requests.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("organismValidator")
public class OrganismValidatorImpl extends ResourceValidatorManagerImpl<Organism> {

	private static final Logger logger = LogManager.getLogger(OrganismValidatorImpl.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureType} objects
	 */
	@Autowired
	@Qualifier("structureTypeService")
	private StructureTypeService structureTypeService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateGet(Long resourceId) {
		// Check param
		Assert.notNull(resourceId, "[validateGet] Organism's id can't be null");
		logger.debug("[validateGet] of organism ");

		// Check if an organism with this id exists into the database
		if (!this.validateResourceExists(resourceId)) {
			throw new WebResourceNotFoundException(resourceId, MethodType.GET,
					"No organism exist with this id : " + resourceId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean validateResourceExists(Long organismId) {
		// Check param
		Assert.notNull(organismId, "[validateResourceExists] Organism's id can't be null");
		logger.debug("[validateResourceExists] of organism ");

		// Check if an organism exists with this id
		Organism foundOrganism = organismService.findOne(organismId);
		if (foundOrganism != null) {
			return Boolean.TRUE;
		}
		logger.info("[validateResourceExists] of organism : no organism with id : " + organismId + " was found");
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateUpdate(Organism resource, Long resourceId, Organism foundOrganism) {
		// Check params
		Assert.notNull(resource, "[validateUpdate] organism can't be null");
		Assert.notNull(resourceId, "[validateUpdate] resourceId can't be null");
		Assert.notNull(foundOrganism, "[validateUpdate] foundOrganism can't be null");

		logger.debug("[validateUpdate] of organism with id = " + resourceId);

		// Validate bean content
		logger.debug("Validating bean content of organism sent");
		this.validateBeanContent(resource, MethodType.PUT);

		// Check that resource's id is equal at the resourceId
		logger.debug("Checking that resource's id is equal at the resourceId");
		if (!(resource.getId().equals(resourceId))) {
			throw new WebRequestSyntaxException(resource, MethodType.PUT,
					"Id sent not corresponding at the organism id sent : " + resource.getId(), "externalId",
					resource.getExternalId().toString());
		}

		// Prepare the resource before update
		this.prepareBeforeUpdate(resource);
	}

	/**
	 * {@inheritDoc}
	 */
	public void prepareBeforeUpdate(Organism resource) {
		Assert.notNull(resource, "[prepareBeforeUpdate] organism can't be null");

		// Populate collections of the organism
		this.organismService.populateCollections(resource);

		/* Set xml transient values */

		// Adding structuretype
		StructureType structureType = this.structureTypeService.findByValue(StructureTypeValue.ORGANISM);
		resource.setStructureType(structureType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void filterResource(Organism resource, MethodType methodType) {
		Assert.notNull(resource, "[filterResource] resource can't be null");
		Assert.notNull(methodType, "[filterResource] methodType can't be null");

		if (methodType.equals(MethodType.GET)) {
			resource.setChildStructures(new ArrayList<Structure>());
			resource.setParentStructures(new ArrayList<Structure>());

			resource.setApplications(new ArrayList<Application>());
		}
	}

	/**
	 * Unused method for this type of resource
	 */
	@Override
	public void validateCreate(Organism resource) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of resource
	 */
	@Override
	public void validateDelete(Long resourceId) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of resource
	 */
	@Override
	public void validateUpdateCreation(Organism resource) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of resource
	 */
	@Override
	public void prepareResource(Organism organism, MethodType methodType) {
		// NOTHING TODO
	}

	/**
	 * Unused method for this type of resource
	 */
	@Override
	public void populateResourceCollections(Organism resource) {
		// NOTHING TODO
	}
}
