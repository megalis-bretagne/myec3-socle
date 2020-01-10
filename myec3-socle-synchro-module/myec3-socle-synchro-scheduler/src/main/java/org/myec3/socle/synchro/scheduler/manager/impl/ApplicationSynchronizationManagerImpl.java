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
package org.myec3.socle.synchro.scheduler.manager.impl;

import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Concrete implementation of synchronization manager to perform synchronization
 * tasks for an {@link Application}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("applicationSynchronizer")
public class ApplicationSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<Application> {

	/**
	 * Business Service injected by Spring container to manage synchronization tasks
	 * for {@link Company} objects.
	 */
	@Autowired
	@Qualifier("companySynchronizer")
	private ResourceSynchronizationManager<Company> companySynchronizer;

	/**
	 * Business Service injected by Spring container to manage synchronization tasks
	 * for {@link Organism} objects.
	 */
	@Autowired
	@Qualifier("organismSynchronizer")
	private ResourceSynchronizationManager<Organism> organismSynchronizer;

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link Application} objects.
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setCommonJob(SynchronizationJobType synchronizationJobType, Application resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {
		// NOTHING TODO
		this.getLogger().debug("setCommonJob for Application, not implemented");
	}

	/**
	 * Method not used for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCreation(Application resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		// NOTHING TODO
		this.getLogger().debug("synchronize creation for Application, not implemented");
	}

	/**
	 * Method called when an application is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeDeletion(Application resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for Application");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.applicationService.populateCollections(resource);
		for (Structure structure : resource.getStructures()) {
			if (structure.isCompany()) {
				this.companySynchronizer.synchronizeUpdate((Company) structure, listApplicationIdToResynchronize,
						synchronizationType, sendingApplication);
			} else {
				this.organismSynchronizer.synchronizeUpdate((Organism) structure, listApplicationIdToResynchronize,
						synchronizationType, sendingApplication);
			}
		}
	}

	/**
	 * Method called when an application is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeUpdate(Application resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for Application");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.applicationService.populateCollections(resource);
		for (Structure structure : resource.getStructures()) {
			if (structure.isCompany()) {
				// We update the company
				this.companySynchronizer.synchronizeUpdate((Company) structure, listApplicationIdToResynchronize,
						synchronizationType, sendingApplication);
			} else {
				// We update the organism
				this.organismSynchronizer.synchronizeUpdate((Organism) structure, listApplicationIdToResynchronize,
						synchronizationType, sendingApplication);
			}
		}
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionUpdate(Application resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionRemove(Application resource, String relationName, List<Resource> removedResources,
			String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionCreate(Application resource, String relationName, List<Resource> createdResources,
			String sendingApplication) {
		// NOTHING TODO
	}
}
