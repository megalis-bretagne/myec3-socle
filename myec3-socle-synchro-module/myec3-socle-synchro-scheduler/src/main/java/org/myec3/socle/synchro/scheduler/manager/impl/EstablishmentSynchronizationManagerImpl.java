package org.myec3.socle.synchro.scheduler.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.scheduler.constants.MyEc3JobConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("establishmentSynchronizer")
public class EstablishmentSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<Establishment> {

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link Establishment} objects.
	 */
	@Autowired
	@Qualifier("establishmentService")
	private EstablishmentService establishmentService;

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link Application} objects.
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCommonJob(SynchronizationJobType synchronizationJobType, Establishment resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {

		// Populate collections of establishment
		if (!synchronizationJobType.equals(SynchronizationJobType.DELETE)) {
			this.establishmentService.populateCollections(resource);
		}

		List<SynchronizationSubscription> subscriptionList = new ArrayList<SynchronizationSubscription>();

		if (listApplicationIdToResynchronize == null) {
			// get the list of subscription for this type of resource
			subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.ESTABLISHMENT);
		} else {
			for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
				subscriptionList.add(this.getSynchronizationSubscriptionService()
						.findByResourceTypeAndApplicationId(ResourceType.ESTABLISHMENT, applicationIdToResynchronize));
			}
		}

		// Lunch synchronization job
		this.performSynchronization(synchronizationJobType, resource, subscriptionList, synchronizationType,
				sendingApplication);
	}

	/**
	 * Method called when an Establishment is persisted into the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCreation(Establishment resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize creation for Establishment");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.CREATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an Establishment is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeDeletion(Establishment resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for Establishment");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.DELETE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an Establishment is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeUpdate(Establishment resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for Establishment");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.UPDATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionUpdate(Establishment resource, String relationName,
			List<Resource> updatedResources, List<Resource> addedResources, List<Resource> removedResources,
			String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionRemove(Establishment resource, String relationName,
			List<Resource> removedResources, String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionCreate(Establishment resource, String relationName,
			List<Resource> createdResources, String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * {@inheritDoc}
	 */
	private void performSynchronization(SynchronizationJobType synchronizationJobType, Establishment resource,
			List<SynchronizationSubscription> subscriptionList, SynchronizationType synchronizationType,
			String sendingApplication) {

		// Save company's list of applications
		List<Application> currentListOfApplications = this.applicationService
				.findAllApplicationByStructure(resource.getCompany());

		// For each subscription contained in subscription list we create a new
		// synchronization job
		for (SynchronizationSubscription subscription : subscriptionList) {
			this.getLogger().info("synchronize " + synchronizationJobType + " for Establishment application ");

			// Send establishment only if it's contains this subscribed
			// application
			if ((currentListOfApplications.contains(subscription.getApplication()))) {
				Establishment clone = null;

				try {
					clone = (Establishment) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the Establishment : " + ex.getCause()
							+ " " + ex.getMessage());
				}

				// If synchronization type is not delete we must filter the xml
				// sent
				// depending on the subscription
				if (!synchronizationJobType.equals(SynchronizationJobType.DELETE)) {
					this.getSynchronizationFilterService().filter(clone, subscription);
				}

				// We call the scheduler service in order to create a
				// new job witch will execute the correct web service
				// method depending on synchronizationJobType
				this.getSchedulerService().addImmediateSynchronizationSubscriptionEstablishmentTrigger(
						MyEc3JobConstants.ESTABLISHMENT_JOB, clone, subscription, synchronizationJobType,
						synchronizationType, sendingApplication);
			}
		}
	}
}
