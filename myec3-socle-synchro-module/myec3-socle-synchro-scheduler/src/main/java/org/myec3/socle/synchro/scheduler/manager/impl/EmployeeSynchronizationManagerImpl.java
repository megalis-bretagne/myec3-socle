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
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.scheduler.constants.MyEc3JobConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Concrete implementation of synchronization manager to perform synchronization
 * tasks for an {@link EmployeeProfile}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("employeeSynchronizer")
public class EmployeeSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<EmployeeProfile> {

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link EmployeeProfile} objects.
	 */
	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

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
	public void setCommonJob(SynchronizationJobType synchronizationJobType, EmployeeProfile resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {

		this.getLogger().info("[setCommonJob] of EmployeeProfile : " + resource.getName());

		List<SynchronizationSubscription> subscriptionList = new ArrayList<SynchronizationSubscription>();

		if (listApplicationIdToResynchronize == null) {
			// get the list of subscription for this type of resource
			subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.EMPLOYEE_PROFILE);
		} else {
			for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
				subscriptionList.addAll(this.getSynchronizationSubscriptionService().findByResourceTypeAndApplicationId(
						ResourceType.EMPLOYEE_PROFILE, applicationIdToResynchronize));
			}
		}

		// Lunch synchronization job
		this.performSynchronization(synchronizationJobType, resource, subscriptionList, synchronizationType,
				sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("static-access")
	private void performSynchronization(SynchronizationJobType synchronizationJobType, EmployeeProfile resource,
			List<SynchronizationSubscription> subscriptionList, SynchronizationType synchronizationType,
			String sendingApplication) {

		// If synchronizationJobType <> DELETE we check the list of subscription
		if (!synchronizationJobType.equals(synchronizationJobType.DELETE)) {

			// Populate collections of the employeeProfile
			this.employeeProfileService.populateCollections(resource);

			// Save employee's list of applications because clone method is
			// useless
			List<Role> currentEmployeeRoles = new ArrayList<Role>();
			currentEmployeeRoles.addAll(resource.getRoles());

			for (SynchronizationSubscription subscription : subscriptionList) {
				this.getLogger().debug("Application : " + subscription.getApplication().getName());

				EmployeeProfile clone = null;

				try {
					clone = (EmployeeProfile) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the EmployeeProfile : " + ex.getCause()
							+ " " + ex.getMessage());
				}

				// We filter the resource
				this.getSynchronizationFilterService().filter(clone, subscription);

				// If we must send the list of roles we set the saved
				// list
				if (subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
					clone.setRoles(currentEmployeeRoles);
				}

				if ((clone.getRoles() != null) && (!clone.getRoles().isEmpty())) {
					this.getSchedulerService().addImmediateSynchronizationSubscriptionEmployeeProfileTrigger(
							MyEc3JobConstants.EMPLOYEE_JOB, clone, clone.getRoles(), subscription,
							synchronizationJobType, synchronizationType, sendingApplication);
				}
			}
		} else {
			// in case of a DELETE we send the employee at all applications.
			this.getLogger().info("sending DELETE Employee to all applications");
			for (SynchronizationSubscription subscription : subscriptionList) {
				this.getSchedulerService().addImmediateSynchronizationSubscriptionEmployeeProfileTrigger(
						MyEc3JobConstants.EMPLOYEE_JOB, resource, new ArrayList<Role>(), subscription,
						synchronizationJobType, synchronizationType, sendingApplication);
			}
		}
	}

	/**
	 * Method called when an employee is persisted into the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCreation(EmployeeProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize creation for Employee");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.CREATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an employee is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeDeletion(EmployeeProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for Employee");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.DELETE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an employee is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeUpdate(EmployeeProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for Employee");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.UPDATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * This method is called when the list of roles of an employee is updated
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionUpdate(EmployeeProfile resource, String relationName,
			List<Resource> updatedResources, List<Resource> addedResources, List<Resource> removedResources,
			String sendingApplication) {
		this.getLogger().info("synchronizeCollectionUpdate for Employee, relationName = " + relationName);
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(relationName, "relationName is mandatory. null value is forbidden");

		// We check that is the list of roles which have been updated
		if (relationName.equalsIgnoreCase(SynchronizationRelationsName.ROLES.getValue())) {

			// Populate collections of the employee
			this.employeeProfileService.populateCollections(resource);

			// Get the list of applications allowing multiples roles
			List<Application> applicationsAllowingMultiplesRoles = this.applicationService
					.findAllApplicationsAllowingMultipleRolesByStructureTypeValue(StructureTypeValue.ORGANISM);

			// Save the list of roles of the employee
			List<Role> currentEmployeeRoles = new ArrayList<Role>();
			currentEmployeeRoles.addAll(resource.getRoles());

			// We get all the applications which are subscribe at this type of
			// resource
			List<SynchronizationSubscription> subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.EMPLOYEE_PROFILE);

			// For each of theses subscriptions
			for (SynchronizationSubscription subscription : subscriptionList) {

				EmployeeProfile clone = null;

				try {
					clone = (EmployeeProfile) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the EmployeeProfile : " + ex.getCause()
							+ " " + ex.getMessage());
				}

				List<Role> currentApplicationRoles = new ArrayList<Role>();
				// Save the list of roles of the employee by application
				List<Role> currentEmployeeRolesByApplication = new ArrayList<Role>();
				List<Role> addedRolesByApplication = new ArrayList<Role>();
				List<Role> removedRolesByApplication = new ArrayList<Role>();

				// Filter ressource
				this.getSynchronizationFilterService().filter(clone, subscription);

				// Get employee's roles for the current application
				if (subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
					currentApplicationRoles = currentEmployeeRoles;
				} else {
					for (Role roleEmployee : currentEmployeeRoles) {
						if (roleEmployee.getApplication().equals(subscription.getApplication())) {
							currentEmployeeRolesByApplication.add(roleEmployee);
						}
					}

					if (addedResources != null) {
						for (Resource roleAdded : addedResources) {
							Role newRole = (Role) roleAdded;
							// We check that the role's application id is equals at
							// the
							// subscription's application id in order to send the
							// employeeProfile only at the applications concerned
							if (newRole.getApplication().getId().equals(subscription.getApplication().getId())) {
								currentApplicationRoles.add(newRole);
								addedRolesByApplication.add(newRole);
							}
						}
					}

					if (removedResources != null) {
						for (Resource roleRemoved : removedResources) {
							Role oldRole = (Role) roleRemoved;
							// We check that the role's application id is equals at
							// the
							// subscription's application id in order to send the
							// employeeProfile only at the applications concerned
							if (oldRole.getApplication().getId().equals(subscription.getApplication().getId())) {
								oldRole.setEnabled(Boolean.FALSE);
								currentApplicationRoles.add(oldRole);
								removedRolesByApplication.add(oldRole);
							}
						}
					}

					if (!currentApplicationRoles.isEmpty()) {
						// We set the resource's list of roles with only
						// roles corresponding at the resource and the
						// application which has subscribed at this type of
						// resource
						this.getLogger().debug("Role(s) modified : " + currentApplicationRoles.toString());
						for (Role resourceRole : currentEmployeeRolesByApplication) {
							if (resourceRole.getApplication().getId().equals(subscription.getApplication().getId())) {
								if (!currentApplicationRoles.contains(resourceRole)) {
									currentApplicationRoles.add(resourceRole);
								}
							}
						}

						// If the employee has no roles for this application we
						// set the attribute enable to false
						if (currentEmployeeRolesByApplication.isEmpty()) {
							clone.setEnabled(Boolean.FALSE);
						} else {
							clone.setEnabled(Boolean.TRUE);
						}

						// If it's not an application allowing multiples roles
						// we must send only one role into the xml
						if (!applicationsAllowingMultiplesRoles.contains(subscription.getApplication())) {
							if (clone.getEnabled()) {
								clone.setRoles(addedRolesByApplication);
								currentApplicationRoles.removeAll(removedRolesByApplication);
							} else {
								clone.setRoles(removedRolesByApplication);
							}
						} else {
							// We can set all roles into the xml file
							clone.setRoles(currentApplicationRoles);
						}
					}
				}

				if (!currentApplicationRoles.isEmpty()) {
					// We call the scheduler service in order to create
					// a new job which will execute a PUT to the web service
					// in order to disable the employee into the distant
					// application concerned
					this.getSchedulerService().addImmediateSynchronizationSubscriptionEmployeeProfileTrigger(
							MyEc3JobConstants.EMPLOYEE_JOB, clone, currentApplicationRoles, subscription,
							SynchronizationJobType.UPDATE, SynchronizationType.SYNCHRONIZATION, sendingApplication);

				}
			}
		}
	}

	/**
	 * Method unused for this type of resource
	 */
	@Override
	public void synchronizeCollectionRemove(EmployeeProfile resource, String relationName,
			List<Resource> removedResources, String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 */
	@Override
	public void synchronizeCollectionCreate(EmployeeProfile resource, String relationName,
			List<Resource> createdResources, String sendingApplication) {
		// NOTHING TODO

	}

}
