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

import java.util.ArrayList;
import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.scheduler.constants.MyEc3JobConstants;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Concrete implementation of synchronization manager to perform synchronization
 * tasks for an {@link Organism}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("organismSynchronizer")
public class OrganismSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<Organism> {

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link Organism} objects.
	 */
	@Autowired
	@Qualifier("organismService")
	private OrganismService organismService;

	/**
	 * Business Service injected by Spring container to manage synchronization tasks
	 * for {@link Company} objects.
	 */
	@Autowired
	@Qualifier("companySynchronizer")
	private ResourceSynchronizationManager<Company> companySynchronizer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCommonJob(SynchronizationJobType synchronizationJobType, Organism resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {

		this.getLogger().info("[setCommonJob] of Organism : " + resource.getName());

		// We populate resource's collections if synchronization job is not
		// DELETE
		if (!synchronizationJobType.equals(SynchronizationJobType.DELETE)) {
			this.organismService.populateCollections(resource);
		}

		List<SynchronizationSubscription> subscriptionList = new ArrayList<SynchronizationSubscription>();

		if (listApplicationIdToResynchronize == null) {
			// get the list of subscription for this type of resource
			subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.ORGANISM);
		} else {
			for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
				subscriptionList.add(this.getSynchronizationSubscriptionService()
						.findByResourceTypeAndApplicationId(ResourceType.ORGANISM, applicationIdToResynchronize));
			}
		}

		// Lunch synchronization job
		this.performSynchronization(synchronizationJobType, resource, subscriptionList, synchronizationType,
				sendingApplication);

	}

	/**
	 * {@inheritDoc}
	 */
	private void performSynchronization(SynchronizationJobType synchronizationJobType, Organism resource,
			List<SynchronizationSubscription> subscriptionList, SynchronizationType synchronizationType,
			String sendingApplication) {

		// Save organism's list of applications because clone method is useless
		List<Application> currentListOfApplications = new ArrayList<Application>();
		currentListOfApplications.addAll(resource.getApplications());

		// For each subscription contained in subscription list we lunch a new
		// synchronization job to subscribed applications
		for (SynchronizationSubscription subscription : subscriptionList) {
			// Send organism only if it's contains this application
			if (currentListOfApplications.contains(subscription.getApplication())) {

				Organism clone = null;

				try {
					clone = (Organism) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the Organism : " + ex.getCause() + " "
							+ ex.getMessage());
				}

				// If synchronization type is not delete we must filter the xml
				// send
				// depending on the subscription
				if (!synchronizationJobType.equals(SynchronizationJobType.DELETE)) {
					// Apply default filters
					this.getSynchronizationFilterService().filter(clone, subscription);

					// If we must send the list of applications we set the saved
					// list
					if (subscription.getSynchronizationFilter().isAllApplicationsDisplayed()) {
						clone.setApplications(currentListOfApplications);
					}
				}

				// We call the scheduler service in order to create a
				// new job witch will execute the correct web service
				// method depending on synchronizationJobType
				this.getSchedulerService().addImmediateSynchronizationSubscriptionOrganismTrigger(
						MyEc3JobConstants.ORGANISM_JOB, clone, subscription, synchronizationJobType,
						synchronizationType, sendingApplication);
			}
		}
	}

	/**
	 * Method called when an Organism is persisted into the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCreation(Organism resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize creation for Organism");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.CREATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an Organism is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeDeletion(Organism resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for Organism");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.DELETE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an Organism is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeUpdate(Organism resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for Organism");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.UPDATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * This method is called when the list of applications of an organism is updated
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionUpdate(Organism resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(relationName, "relationName is mandatory. null value is forbidden");

		this.getLogger()
				.info("[OrganismSynchronizationManagerImpl] synchronizeCollectionUpdate. relationName=" + relationName);

		// If it's an update of childStructure we update the organism
		// corresponding
		if (relationName.equalsIgnoreCase(SynchronizationRelationsName.CHILD_STRUCTURES.getValue())) {
			// We synchronize this resource (the parentstructure)
			this.synchronizeUpdate(resource, null, SynchronizationType.SYNCHRONIZATION, sendingApplication);

			// We must synchronize also the new child structures
			if (addedResources != null) {
				for (Resource addedResource : addedResources) {
					Structure addedStructure = (Structure) addedResource;

					this.getLogger().info(
							"[OrganismSynchronizationManagerImpl] synchronizeCollectionUpdate. childStructure added="
									+ addedStructure.getName());

					if (addedStructure instanceof Organism) {
						this.synchronizeUpdate((Organism) addedStructure, null, SynchronizationType.SYNCHRONIZATION,
								sendingApplication);
					}

					if (addedStructure instanceof Company) {
						companySynchronizer.synchronizeUpdate((Company) addedStructure, null,
								SynchronizationType.SYNCHRONIZATION, sendingApplication);
					}
				}
			}

			// We must synchronize the old child structures
			if (removedResources != null) {
				for (Resource removedResource : removedResources) {
					Structure removedStructure = (Structure) removedResource;

					this.getLogger().info(
							"[OrganismSynchronizationManagerImpl] synchronizeCollectionUpdate. childStructure removed="
									+ removedStructure.getName());

					if (removedStructure instanceof Organism) {
						this.synchronizeUpdate((Organism) removedStructure, null, SynchronizationType.SYNCHRONIZATION,
								sendingApplication);
					}

					if (removedStructure instanceof Company) {
						companySynchronizer.synchronizeUpdate((Company) removedStructure, null,
								SynchronizationType.SYNCHRONIZATION, sendingApplication);
					}
				}
			}
		}

		// IN CASE OF AN UPDATE OF ORGANISM APPLICATIONS COLLECTION
		if (relationName.equalsIgnoreCase(SynchronizationRelationsName.APPLICATIONS.getValue())) {
			// We force the synchronization of the organism
			this.synchronizeUpdate(resource, null, SynchronizationType.SYNCHRONIZATION, sendingApplication);
		}
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionRemove(Organism resource, String relationName, List<Resource> removedResources,
			String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionCreate(Organism resource, String relationName, List<Resource> createdResources,
			String sendingApplication) {
		// NOTHING TODO
	}

}
