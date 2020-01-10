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
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
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
 * tasks for an {@link AgentProfile}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("agentSynchronizer")
public class AgentSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<AgentProfile> {

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link AgentProfile} objects.
	 */
	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link Application} objects.
	 */
	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * This method create a new synchronization job for each application that
	 * subscribed at this type of resource : AgentProfile
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setCommonJob(SynchronizationJobType synchronizationJobType, AgentProfile resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {
		this.getLogger().info("[setCommonJob] of AgentProfile : " + resource.getName());

		List<SynchronizationSubscription> subscriptionList = new ArrayList<SynchronizationSubscription>();

		if (listApplicationIdToResynchronize == null) {
			// get the list of subscription for this type of resource
			subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.AGENT_PROFILE);
		} else {
			for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
				subscriptionList.add(this.getSynchronizationSubscriptionService()
						.findByResourceTypeAndApplicationId(ResourceType.AGENT_PROFILE, applicationIdToResynchronize));
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
	private void performSynchronization(SynchronizationJobType synchronizationJobType, AgentProfile resource,
			List<SynchronizationSubscription> subscriptionList, SynchronizationType synchronizationType,
			String sendingApplication) {
		this.getLogger().debug("Enterring in performSynchronization");

		// If synchronizationJobType <> DELETE we check the list of subscription
		if (!synchronizationJobType.equals(synchronizationJobType.DELETE)) {

			// Populate collections of the agentProfile
			this.agentProfileService.populateCollections(resource);

			// Save agent's list of applications because clone method is
			// useless
			List<Role> currentAgentRoles = new ArrayList<Role>();
			currentAgentRoles.addAll(resource.getRoles());

			// For each subscription contained in subscription list we term the
			// list of roles
			for (SynchronizationSubscription subscription : subscriptionList) {

				this.getLogger().debug("Application : " + subscription.getApplication().getName());

				AgentProfile clone = null;

				try {
					clone = (AgentProfile) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the AgentProfile : " + ex.getCause() + " "
							+ ex.getMessage());
				}

				// We filter the resource
				this.getSynchronizationFilterService().filter(clone, subscription);

				// If we must send the list of roles we set the saved
				// list
				if (subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
					clone.setRoles(currentAgentRoles);
				}

				if ((clone.getRoles() != null) && (!clone.getRoles().isEmpty())) {

					this.getLogger()
							.debug("1-[addImmediateSynchronizationSubscriptionAgentProfileTrigger] agent with role : "
									+ clone.getRoles().get(0).getName() + " of application : "
									+ clone.getRoles().get(0).getApplication().getName());

					// We call the scheduler service in order to create a
					// new job which will execute the correct web service
					// method depending on synchronizationJobType
					this.getSchedulerService().addImmediateSynchronizationSubscriptionAgentProfileTrigger(
							MyEc3JobConstants.AGENT_JOB, clone, clone.getRoles(), subscription, synchronizationJobType,
							synchronizationType, sendingApplication);
				}
			}
		}

		// in case of a DELETE we send the agent at all applications.
		else {
			this.getLogger().info("sending DELETE Agent to all applications");
			for (SynchronizationSubscription subscription : subscriptionList) {
				this.getSchedulerService().addImmediateSynchronizationSubscriptionAgentProfileTrigger(
						MyEc3JobConstants.AGENT_JOB, resource, new ArrayList<Role>(), subscription,
						synchronizationJobType, synchronizationType, sendingApplication);
			}
		}
	}

	/**
	 * Method called when an agent is persisted into the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCreation(AgentProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize creation for Agent");

		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.CREATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an agent is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeUpdate(AgentProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for Agent");

		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.UPDATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * Method called when an agent is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeDeletion(AgentProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for Agent");

		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.DELETE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	/**
	 * This method is called when the list of roles of an agent is updated
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionUpdate(AgentProfile resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		this.getLogger().info("synchronizeCollectionUpdate for Agent, relationName = " + relationName);
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(relationName, "relationName is mandatory. null value is forbidden");

		// We check that is the list of roles which have been updated
		if (relationName.equalsIgnoreCase(SynchronizationRelationsName.ROLES.getValue())) {

			// Populate collections of the agent
			this.agentProfileService.populateCollections(resource);

			// Get the list of applications allowing multiples roles
			List<Application> applicationsAllowingMultiplesRoles = this.applicationService
					.findAllApplicationsAllowingMultipleRolesByStructureTypeValue(StructureTypeValue.ORGANISM);

			// Save the list of roles of the agent
			List<Role> currentAgentRoles = new ArrayList<Role>();
			currentAgentRoles.addAll(resource.getRoles());

			// We get all the applications which have subscribe at this type of
			// resource
			List<SynchronizationSubscription> subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.AGENT_PROFILE);

			// For each of theses subscription
			for (SynchronizationSubscription subscription : subscriptionList) {

				AgentProfile clone = null;

				try {
					clone = (AgentProfile) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the AgentProfile : " + ex.getCause() + " "
							+ ex.getMessage());
				}

				List<Role> currentApplicationRoles = new ArrayList<Role>();
				// Save the list of roles of the agent by application
				List<Role> currentAgentRolesByApplication = new ArrayList<Role>();
				List<Role> addedRolesByApplication = new ArrayList<Role>();
				List<Role> removedRolesByApplication = new ArrayList<Role>();

				// Filter ressource
				this.getSynchronizationFilterService().filter(clone, subscription);

				// Get agent's roles for the current application
				if (subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
					currentApplicationRoles = currentAgentRoles;
				} else {
					for (Role roleAgent : currentAgentRoles) {
						if (roleAgent.getApplication().equals(subscription.getApplication())) {
							currentAgentRolesByApplication.add(roleAgent);
						}
					}

					if (addedResources != null) {
						for (Resource roleAdded : addedResources) {
							Role newRole = (Role) roleAdded;
							// We check that the role's application id is equals
							// at
							// the subscription's application id in order to
							// send the
							// agentProfile only at the applications concerned
							if (newRole.getApplication().getId().equals(subscription.getApplication().getId())) {
								currentApplicationRoles.add(newRole);
								addedRolesByApplication.add(newRole);
							}
						}
					}

					if (removedResources != null) {
						for (Resource roleRemoved : removedResources) {
							Role oldRole = (Role) roleRemoved;
							// We check that the role's application id is equals
							// at
							// the subscription's application id in order to
							// send
							// the
							// agentProfile only at the applications concerned
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
						for (Role resourceRole : currentAgentRolesByApplication) {
							if (resourceRole.getApplication().getId().equals(subscription.getApplication().getId())) {
								if (!currentApplicationRoles.contains(resourceRole)) {
									currentApplicationRoles.add(resourceRole);
								}
							}
						}

						// If the agent has no roles for this application we set
						// the attribute enable to false
						if (currentAgentRolesByApplication.isEmpty()) {
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

					this.getLogger().info(
							"About to Launch addImmediateSynchronizationSubscriptionAgentProfileTrigger for subscription url "
									+ subscription.getApplication().getUrl());

					// Attach roles to current agent if we need to synchronize agent with all his
					// roles
					// Problem was : only zero (null) or one role for an agent if
					// SynchronizationSubscription filter contains "allRoleDisplayed" for an
					// application which was not the first of the list ...
					// Link between agent and his roles was broken after loops

					if (subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
						clone.setRoles(currentApplicationRoles);
					}

					this.getLogger().info("Roles for the subscription : " + subscription.getApplication().getName()
							+ " are " + clone.getRoles().toString());

					// We call the scheduler service in order to create
					// a new job which will execute a PUT to the web service
					// in order to disable the agent into the
					// distant application concerned
					this.getSchedulerService().addImmediateSynchronizationSubscriptionAgentProfileTrigger(
							MyEc3JobConstants.AGENT_JOB, clone, currentApplicationRoles, subscription,
							SynchronizationJobType.UPDATE, SynchronizationType.SYNCHRONIZATION, sendingApplication);
				}

			}
		}
	}

	/**
	 * Unused method
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionCreate(AgentProfile resource, String relationName, List<Resource> createdResources,
			String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Unused method
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionRemove(AgentProfile resource, String relationName, List<Resource> removedResources,
			String sendingApplication) {
		// NOTHING TODO
	}

}
