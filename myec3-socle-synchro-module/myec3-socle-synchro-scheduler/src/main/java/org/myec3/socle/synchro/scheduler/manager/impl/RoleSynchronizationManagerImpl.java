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

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.RoleService;
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
 * tasks for an {@link Role}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("roleSynchronizer")
public class RoleSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<Role> {

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link AgentProfile} objects.
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link EmployeeProfile} objects.
	 */
	@Autowired
	@Qualifier("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link Role} objects.
	 */
	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCommonJob(SynchronizationJobType synchronizationJobType, Role resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {
		// populate the list of profiles of this role
		this.roleService.populateCollections(resource);
		List<Profile> profiles = resource.getProfiles();
		List<SynchronizationSubscription> subscriptionList;

		for (Profile profile : profiles) {
			// Initialize subscriptionList
			subscriptionList = new ArrayList<SynchronizationSubscription>();

			// If profile is an agent we get the list of applications witch
			// have subscribed to this type of resource (AgentProfile)
			if (profile.isAgent()) {
				if (listApplicationIdToResynchronize == null) {
					// get the list of subscription for this type of resource
					subscriptionList = this.getSynchronizationSubscriptionService()
							.findAllByResourceLabel(ResourceType.AGENT_PROFILE);
				} else {
					for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
						subscriptionList
								.addAll(this.getSynchronizationSubscriptionService().findByResourceTypeAndApplicationId(
										ResourceType.AGENT_PROFILE, applicationIdToResynchronize));
					}
				}
			} else if (profile.isEmployee()) {
				if (listApplicationIdToResynchronize == null) {
					// get the list of subscription for this type of resource
					subscriptionList = this.getSynchronizationSubscriptionService()
							.findAllByResourceLabel(ResourceType.EMPLOYEE_PROFILE);
				} else {
					for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
						subscriptionList
								.addAll(this.getSynchronizationSubscriptionService().findByResourceTypeAndApplicationId(
										ResourceType.EMPLOYEE_PROFILE, applicationIdToResynchronize));
					}
				}

			}
			// Lunch synchronization job
			this.performSynchronization(synchronizationJobType, resource, profile, subscriptionList,
					synchronizationType, sendingApplication);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	private void performSynchronization(SynchronizationJobType synchronizationJobType, Role resource, Profile profile,
			List<SynchronizationSubscription> subscriptionList, SynchronizationType synchronizationType,
			String sendingApplication) {

		if (profile.isAgent()) {

			// For each subscription contained in subscription list we check
			// that application id contained in resource is equals at the
			// application id contained in the subscription
			for (SynchronizationSubscription synchronizationSubscription : subscriptionList) {
				if (resource.getApplication().getId().equals(synchronizationSubscription.getApplication().getId())) {

					// We populate the agentProfile's collections
					agentProfileService.populateCollections((AgentProfile) profile);

					// We set the agentProfile's roles
					List<Role> listRoles = roleService.findAllRoleByProfileAndApplication(profile,
							synchronizationSubscription.getApplication());

					profile.setRoles(listRoles);

					// We call the scheduler service in order to create a
					// new job witch will execute the correct web service
					// method depending on synchronizationJobType
					this.getSchedulerService().addImmediateSynchronizationSubscriptionAgentProfileTrigger(
							MyEc3JobConstants.AGENT_JOB, (AgentProfile) profile, listRoles, synchronizationSubscription,
							synchronizationJobType, synchronizationType, sendingApplication);
				}
			}
		} else if (profile.isEmployee()) {

			// For each subscription contained in subscription list we check
			// that application id contained in resource is equals at the
			// application id contained in the subscription
			for (SynchronizationSubscription synchronizationSubscription : subscriptionList) {
				if (resource.getApplication().getId().equals(synchronizationSubscription.getApplication().getId())) {

					// We populate the employeeProfile's collections
					employeeProfileService.populateCollections((EmployeeProfile) profile);

					// We set the employeeProfile's roles
					List<Role> listRoles = roleService.findAllRoleByProfileAndApplication(profile,
							synchronizationSubscription.getApplication());

					profile.setRoles(listRoles);

					// We call the scheduler service in order to create a
					// new job witch will execute the correct web service
					// method depending on synchronizationJobType
					this.getSchedulerService().addImmediateSynchronizationSubscriptionEmployeeProfileTrigger(
							MyEc3JobConstants.EMPLOYEE_JOB, (EmployeeProfile) profile, listRoles,
							synchronizationSubscription, synchronizationJobType, synchronizationType,
							sendingApplication);
				}
			}
		}
	}

	/**
	 * Method called when a role is persisted into the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCreation(Role resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		// Nothing TODO
		this.getLogger().info("synchronize creation for Role");
	}

	/**
	 * Method called when a role is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeDeletion(Role resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for Role");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.UPDATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when a role is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeUpdate(Role resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for Role");
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
	public void synchronizeCollectionUpdate(Role resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionRemove(Role resource, String relationName, List<Resource> removedResources,
			String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionCreate(Role resource, String relationName, List<Resource> createdResources,
			String sendingApplication) {
		// NOTHING TODO
	}

}
