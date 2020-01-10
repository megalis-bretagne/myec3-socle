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

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Concrete implementation of synchronization manager to perform synchronization
 * tasks for an {@link User}
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("userSynchronizer")
public class UserSynchronizationManagerImpl extends ResourceSynchronizationManagerImpl<User> {

	/**
	 * Business Service injected by Spring container to manage synchronization tasks
	 * for {@link AgentProfile} objects.
	 */
	@Autowired
	@Qualifier("agentSynchronizer")
	private ResourceSynchronizationManager<AgentProfile> agentSynchronizerManager;

	/**
	 * Business Service injected by Spring container to manage synchronization tasks
	 * for {@link EmployeeProfile} objects.
	 */
	@Autowired
	@Qualifier("employeeSynchronizer")
	private ResourceSynchronizationManager<EmployeeProfile> employeeSynchronizerManager;

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link User} objects.
	 */
	@Autowired
	@Qualifier("userService")
	private UserService userService;

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setCommonJob(SynchronizationJobType synchronizationJobType, User resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {
		// NOTHING TODO
		this.getLogger().debug("setCommonJob for User, not implemented");
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCreation(User resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		// NOTHING TODO
		this.getLogger().debug("synchronize creation for User, not implemented");
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeDeletion(User resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		// NOTHING TODO
		this.getLogger().debug("synchronize deletion for User, not implemented");
	}

	/**
	 * Method called when a user is updated from the database. In this case we need
	 * to synchronize the profile associated at this user
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeUpdate(User resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication) {
		this.getLogger().info("synchronize update for User");
		// Validate parameters
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(synchronizationType, "synchronizationType is mandatory. null value is forbidden");

		this.userService.populateCollections(resource);

		for (Profile profile : resource.getProfiles()) {
			if (isSynchronizable(profile)) {
				if (profile.isAgent()) {
					this.agentSynchronizerManager.synchronizeUpdate((AgentProfile) profile,
							listApplicationIdToResynchronize, synchronizationType, sendingApplication);
				} else {
					this.employeeSynchronizerManager.synchronizeUpdate((EmployeeProfile) profile,
							listApplicationIdToResynchronize, synchronizationType, sendingApplication);
				}
			}
		}
	}

	/**
	 * This method allows to verify if the event it's not an delete.
	 * 
	 * @param profile : the profile to update
	 * @return Boolean
	 */
	public Boolean isSynchronizable(Profile profile) {
		Assert.notNull(profile, "profile is mandatory. null value is forbidden");
		if (!(profile.getEnabled()) && !(profile.getUser().isEnabled())) {
			this.getLogger().info("synchronize update for User cancelled");
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionUpdate(User resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionRemove(User resource, String relationName, List<Resource> removedResources,
			String sendingApplication) {
		// NOTHING TODO
	}

	/**
	 * Method unused for this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeCollectionCreate(User resource, String relationName, List<Resource> createdResources,
			String sendingApplication) {
		// NOTHING TODO
	}
}
