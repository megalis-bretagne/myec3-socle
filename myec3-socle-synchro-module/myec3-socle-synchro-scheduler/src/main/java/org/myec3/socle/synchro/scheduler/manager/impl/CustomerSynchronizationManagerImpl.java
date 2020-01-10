package org.myec3.socle.synchro.scheduler.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.scheduler.constants.MyEc3JobConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Concrete implementation of synchronization manager to perform synchronization
 * tasks for an {@link Customer}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("customerSynchronizer")
public class CustomerSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<Customer> {

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link Customer} objects.
	 */
	@Autowired
	@Qualifier("customerService")
	private CustomerService customerService;

	@Override
	public void setCommonJob(SynchronizationJobType synchronizationJobType, Customer resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {

		List<SynchronizationSubscription> subscriptionList = new ArrayList<SynchronizationSubscription>();

		// We populate resource's collections if synchronization job is not
		// DELETE
		if (!synchronizationJobType.equals(SynchronizationJobType.DELETE)) {
			this.customerService.populateCollections(resource);
		}

		if (listApplicationIdToResynchronize == null) {
			// get the list of subscription for this type of resource
			subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.CUSTOMER);
		} else {
			for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
				subscriptionList.add(this.getSynchronizationSubscriptionService()
						.findByResourceTypeAndApplicationId(ResourceType.CUSTOMER, applicationIdToResynchronize));
			}
		}

		// Save customer's list of applications
		List<Application> currentListOfApplications = new ArrayList<Application>();
		currentListOfApplications.addAll(resource.getApplications());

		// For each subscription contained in subscription list we lunch a new
		// synchronization job to subscribed applications
		for (SynchronizationSubscription subscription : subscriptionList) {

			if (currentListOfApplications.contains(subscription.getApplication())) {
				this.getSchedulerService().addImmediateSynchronizationSubscriptionCustomerTrigger(
						MyEc3JobConstants.CUSTOMER_JOB, resource, subscription, synchronizationJobType,
						synchronizationType, sendingApplication);
			}
		}

	}

	@Override
	public void synchronizeCreation(Customer resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize creation for Customer");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.CREATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);

	}

	@Override
	public void synchronizeDeletion(Customer resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for Customer");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.DELETE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	@Override
	public void synchronizeUpdate(Customer resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for Customer");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.UPDATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);

	}

	@Override
	public void synchronizeCollectionUpdate(Customer resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		// NOTHING TODO
	}

	@Override
	public void synchronizeCollectionCreate(Customer resource, String relationName, List<Resource> createdResources,
			String sendingApplication) {
		// NOTHING TODO
	}

	@Override
	public void synchronizeCollectionRemove(Customer resource, String relationName, List<Resource> removedResources,
			String sendingApplication) {
		// NOTHING TODO
	}
}
