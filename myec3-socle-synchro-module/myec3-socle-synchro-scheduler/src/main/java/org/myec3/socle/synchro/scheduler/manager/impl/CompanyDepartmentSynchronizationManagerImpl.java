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
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.CompanyDepartmentService;
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
 * tasks for a {@link CompanyDepartment}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("companyDepartmentSynchronizer")
public class CompanyDepartmentSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<CompanyDepartment> {

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link CompanyDepartment} objects.
	 */
	@Autowired
	@Qualifier("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

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
	public void setCommonJob(SynchronizationJobType synchronizationJobType, CompanyDepartment resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {

		// Populate collections of companyDepartment
		if (!synchronizationJobType.equals(SynchronizationJobType.DELETE)) {
			this.companyDepartmentService.populateCollections(resource);
		}

		List<SynchronizationSubscription> subscriptionList = new ArrayList<SynchronizationSubscription>();

		if (listApplicationIdToResynchronize == null) {
			// get the list of subscription for this type of resource
			subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.COMPANY_DEPARTMENT);
		} else {
			for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
				subscriptionList.add(this.getSynchronizationSubscriptionService().findByResourceTypeAndApplicationId(
						ResourceType.COMPANY_DEPARTMENT, applicationIdToResynchronize));
			}
		}

		// Lunch synchronization job
		this.performSynchronization(synchronizationJobType, resource, subscriptionList, synchronizationType,
				sendingApplication);

	}

	/**
	 * {@inheritDoc}
	 */
	private void performSynchronization(SynchronizationJobType synchronizationJobType, CompanyDepartment resource,
			List<SynchronizationSubscription> subscriptionList, SynchronizationType synchronizationType,
			String sendingApplication) {

		// Save company's list of applications
		List<Application> currentListOfApplications = this.applicationService
				.findAllApplicationByStructure(resource.getCompany());

		// For each subscription contained in subscription list we create a new
		// synchronization job
		for (SynchronizationSubscription subscription : subscriptionList) {
			this.getLogger().info("synchronize " + synchronizationJobType + " for CompanyDepartment application ");

			// Send company department only if it's contains this subscribed
			// application
			if ((currentListOfApplications.contains(subscription.getApplication()))) {
				CompanyDepartment clone = null;

				try {
					clone = (CompanyDepartment) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the CompanyDepartment : " + ex.getCause()
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
				this.getSchedulerService().addImmediateSynchronizationSubscriptionCompanyDepartmentTrigger(
						MyEc3JobConstants.COMPANY_DEPARTMENT_JOB, clone, subscription, synchronizationJobType,
						synchronizationType, sendingApplication);
			}
		}
	}

	/**
	 * Method called when an CompanyDepartment is persisted into the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCreation(CompanyDepartment resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize creation for CompanyDepartment");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.CREATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an CompanyDepartment is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeDeletion(CompanyDepartment resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for CompanyDepartment");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.DELETE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an CompanyDepartment is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeUpdate(CompanyDepartment resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for CompanyDepartment");
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
	public void synchronizeCollectionUpdate(CompanyDepartment resource, String relationName,
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
	public void synchronizeCollectionRemove(CompanyDepartment resource, String relationName,
			List<Resource> removedResources, String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionCreate(CompanyDepartment resource, String relationName,
			List<Resource> createdResources, String sendingApplication) {
		// NOTHING TODO
	}

}
