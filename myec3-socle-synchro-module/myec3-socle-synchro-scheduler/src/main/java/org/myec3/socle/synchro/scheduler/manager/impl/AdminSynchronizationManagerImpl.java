package org.myec3.socle.synchro.scheduler.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.AdminProfileService;
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
 * tasks for an {@link AdminProfile}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("adminSynchronizer")
public class AdminSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<AdminProfile> {

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link AdminProfile} objects.
	 */
	@Autowired
	@Qualifier("adminProfileService")
	private AdminProfileService adminProfileService;

	@Override
	public void setCommonJob(SynchronizationJobType synchronizationJobType, AdminProfile resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {

		List<SynchronizationSubscription> subscriptionList = new ArrayList<SynchronizationSubscription>();

		if (listApplicationIdToResynchronize == null) {
			// get the list of subscription for this type of resource
			subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.ADMIN_PROFILE);
		} else {
			for (Long applicationIdToResynchronize : listApplicationIdToResynchronize) {
				subscriptionList.addAll(this.getSynchronizationSubscriptionService()
						.findByResourceTypeAndApplicationId(ResourceType.ADMIN_PROFILE, applicationIdToResynchronize));
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
	private void performSynchronization(SynchronizationJobType synchronizationJobType, AdminProfile resource,
			List<SynchronizationSubscription> subscriptionList, SynchronizationType synchronizationType,
			String sendingApplication) {
		this.getLogger().debug("Enterring in performSynchronization");

		// If synchronizationJobType <> DELETE we check the list of subscription
		if (!synchronizationJobType.equals(synchronizationJobType.DELETE)) {

			// Populate collections of the AdminProfile
			this.adminProfileService.populateCollections(resource);

			// Save admin's list of applications because clone method is
			// useless
			List<Role> currentAdminRoles = new ArrayList<Role>();
			currentAdminRoles.addAll(resource.getRoles());

			// For each subscription contained in subscription list we term the
			// list of roles
			for (SynchronizationSubscription subscription : subscriptionList) {

				this.getLogger().debug("Application : " + subscription.getApplication().getName());

				AdminProfile clone = null;

				try {
					clone = (AdminProfile) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the AdminProfile : " + ex.getCause() + " "
							+ ex.getMessage());
				}

				// We filter the resource
				this.getSynchronizationFilterService().filter(clone, subscription);

				// If we must send the list of roles we set the saved
				// list
				if (subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
					clone.setRoles(currentAdminRoles);
				}

				if ((clone.getRoles() != null) && (!clone.getRoles().isEmpty())) {

					this.getLogger()
							.debug("1-[addImmediateSynchronizationSubscriptionAdminProfileTrigger] admin with role : "
									+ clone.getRoles().get(0).getName() + " of application : "
									+ clone.getRoles().get(0).getApplication().getName());

					// We call the scheduler service in order to create a
					// new job which will execute the correct web service
					// method depending on synchronizationJobType
					this.getSchedulerService().addImmediateSynchronizationSubscriptionAdminProfileTrigger(
							MyEc3JobConstants.ADMIN_JOB, clone, clone.getRoles(), subscription, synchronizationJobType,
							synchronizationType, sendingApplication);
				}
			}
		}

		// in case of a DELETE we send the admin at all applications.
		else {
			this.getLogger().info("sending DELETE Admin to all applications");
			for (SynchronizationSubscription subscription : subscriptionList) {
				this.getSchedulerService().addImmediateSynchronizationSubscriptionAdminProfileTrigger(
						MyEc3JobConstants.ADMIN_JOB, resource, new ArrayList<Role>(), subscription,
						synchronizationJobType, synchronizationType, sendingApplication);
			}
		}
	}

	@Override
	public void synchronizeCreation(AdminProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize creation for AdminProfile");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.CREATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);

	}

	@Override
	public void synchronizeDeletion(AdminProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize deletion for AdminProfile");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.DELETE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);
	}

	@Override
	public void synchronizeUpdate(AdminProfile resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for AdminProfile");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.setCommonJob(SynchronizationJobType.UPDATE, resource, listApplicationIdToResynchronize,
				synchronizationType, sendingApplication);

	}

	@Override
	public void synchronizeCollectionUpdate(AdminProfile resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		this.getLogger().info("synchronizeCollectionUpdate for Admin, relationName = " + relationName);
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(relationName, "relationName is mandatory. null value is forbidden");

		// We check that is the list of roles which have been updated
		if (relationName.equalsIgnoreCase(SynchronizationRelationsName.ROLES.getValue())) {

			// Populate collections of the adminProfile
			this.adminProfileService.populateCollections(resource);

			// Save the list of roles of the admin
			List<Role> currentAdminRoles = new ArrayList<Role>();
			currentAdminRoles.addAll(resource.getRoles());

			// We get all the applications which have subscribe at this type of
			// resource
			List<SynchronizationSubscription> subscriptionList = this.getSynchronizationSubscriptionService()
					.findAllByResourceLabel(ResourceType.ADMIN_PROFILE);

			// For each of theses subscription
			for (SynchronizationSubscription subscription : subscriptionList) {

				AdminProfile clone = null;

				try {
					clone = (AdminProfile) resource.clone();
				} catch (CloneNotSupportedException ex) {
					this.getLogger().error("An error has occured during clone the AdminProfile : " + ex.getCause() + " "
							+ ex.getMessage());
				}

				List<Role> currentApplicationRoles = new ArrayList<Role>();
				// Save the list of roles of the admin by application
				List<Role> currentAdminRolesByApplication = new ArrayList<Role>();
				List<Role> addedRolesByApplication = new ArrayList<Role>();
				List<Role> removedRolesByApplication = new ArrayList<Role>();

				// Filter ressource
				this.getSynchronizationFilterService().filter(clone, subscription);

				// Get admin's roles for the current application
				if (subscription.getSynchronizationFilter().isAllRolesDisplayed()) {
					currentApplicationRoles = currentAdminRoles;
				} else {
					for (Role roleAdmin : currentAdminRoles) {
						if (roleAdmin.getApplication().equals(subscription.getApplication())) {
							currentAdminRolesByApplication.add(roleAdmin);
						}
					}

					if (addedResources != null) {
						for (Resource roleAdded : addedResources) {
							Role newRole = (Role) roleAdded;
							// We check that the role's application id is equals
							// at
							// the subscription's application id in order to
							// send the
							// adminProfile only at the applications concerned
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
							// adminProfile only at the applications concerned
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
						for (Role resourceRole : currentAdminRolesByApplication) {
							if (resourceRole.getApplication().getId().equals(subscription.getApplication().getId())) {
								if (!currentApplicationRoles.contains(resourceRole)) {
									currentApplicationRoles.add(resourceRole);
								}
							}
						}

						// If the admin has no roles for this application we set
						// the attribute enable to false
						if (currentAdminRolesByApplication.isEmpty()) {
							clone.setEnabled(Boolean.FALSE);
						} else {
							clone.setEnabled(Boolean.TRUE);
						}

						// If it's not an application allowing multiples roles
						// we must send only one role into the xml
						if (clone.getEnabled()) {
							clone.setRoles(addedRolesByApplication);
							currentApplicationRoles.removeAll(removedRolesByApplication);
						} else {
							clone.setRoles(removedRolesByApplication);
						}
					}
				}

				if (!currentApplicationRoles.isEmpty()) {
					// We call the scheduler service in order to create
					// a new job which will execute a PUT to the web service
					// in order to disable the agent into the
					// distant application concerned
					this.getSchedulerService().addImmediateSynchronizationSubscriptionAdminProfileTrigger(
							MyEc3JobConstants.ADMIN_JOB, clone, currentApplicationRoles, subscription,
							SynchronizationJobType.UPDATE, SynchronizationType.SYNCHRONIZATION, sendingApplication);
				}

			}
		}
	}

	@Override
	public void synchronizeCollectionCreate(AdminProfile resource, String relationName, List<Resource> createdResources,
			String sendingApplication) {
		// NOTHING TODO
	}

	@Override
	public void synchronizeCollectionRemove(AdminProfile resource, String relationName, List<Resource> removedResources,
			String sendingApplication) {
		// NOTHING TODO
	}

}
