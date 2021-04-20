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
package org.myec3.socle.synchro.scheduler.service.impl;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.constants.MyEc3JobConstants;
import org.myec3.socle.synchro.scheduler.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of synchronization services. This service creates the jobs and
 * launch it. This allow to 'release' transaction in order to complete database
 * store operations.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@Component("synchronizationService")
public class SynchronizationServiceImpl implements SynchronizationService {

	/**
	 * Business Service injected by Spring container to create triggers using the
	 * simple quartz scheduler.
	 */
	@Autowired
	@Qualifier("simpleSchedulerService")
	private SchedulerService schedulerService;

	/**
	 * Default constructor. Do nothing.
	 */
	public SynchronizationServiceImpl() {
		super();
	}

	/**
	 * Method called when a resource is persisted into database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void propagateCreation(Resource resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		schedulerService.addImmediateCreationTrigger(MyEc3JobConstants.CREATION_JOB, resource,
				listApplicationIdToResynchronize, synchronizationType, sendingApplication);
	}

	/**
	 * Method called when a resource is deleted from database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void propagateDeletion(Resource resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		schedulerService.addImmediateDeletionTrigger(MyEc3JobConstants.DELETION_JOB, resource,
				listApplicationIdToResynchronize, synchronizationType, sendingApplication);
	}

	/**
	 * Method called when a resource is updated from database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void propagateUpdate(Resource resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		schedulerService.addImmediateUpdateTrigger(MyEc3JobConstants.UPDATE_JOB, resource,
				listApplicationIdToResynchronize, synchronizationType, sendingApplication);

	}

	/**
	 * Method called when a collection of resource is updated from database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void propagateCollectionUpdate(Resource resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		schedulerService.addImmediateCollectionUpdateTrigger(MyEc3JobConstants.COLLECTION_UPDATE_JOB, resource,
				relationName, updatedResources, addedResources, removedResources, sendingApplication);

	}

	/**
	 * Method called when a collection of resource is created into the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void propagateCollectionCreate(Resource resource, String relationName, List<Resource> createdResources,
			String sendingApplication) {
		schedulerService.addImmediateCollectionCreateTrigger(MyEc3JobConstants.COLLECTION_CREATE_JOB, resource,
				relationName, createdResources, sendingApplication);
	}
}
