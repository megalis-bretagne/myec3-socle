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
package org.myec3.socle.synchro.api;

import java.util.List;

import org.myec3.socle.core.constants.MyEc3EsbConstants;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing specific methods to notify external
 * applications of a change in socle.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("synchronizationNotificationService")
public class SynchronizationNotificationServiceImpl implements SynchronizationNotificationService {

	@Autowired
	@Qualifier("synchronizationCoreService")
	private SynchronizationService synchronizationService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyCreation(Resource resource) {
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		this.synchronizationService.propagateCreation(resource, null, SynchronizationType.SYNCHRONIZATION,
				MyEc3EsbConstants.getApplicationSendingJmsName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyCreation(Resource resource, SynchronizationConfiguration configSync) {
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(configSync, "configSync is mandatory. null value is forbidden");
		this.synchronizationService.propagateCreation(resource, configSync.getListApplicationIdToResynchronize(),
				configSync.getSynchronizationType(), configSync.getSendingApplication());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyUpdate(Resource resource) {
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		this.synchronizationService.propagateUpdate(resource, null, SynchronizationType.SYNCHRONIZATION,
				MyEc3EsbConstants.getApplicationSendingJmsName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyUpdate(Resource resource, SynchronizationConfiguration configSync) {
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(configSync, "configSync is mandatory. null value is forbidden");
		this.synchronizationService.propagateUpdate(resource, configSync.getListApplicationIdToResynchronize(),
				configSync.getSynchronizationType(), configSync.getSendingApplication());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyDeletion(Resource resource) {
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		this.synchronizationService.propagateDeletion(resource, null, SynchronizationType.SYNCHRONIZATION,
				MyEc3EsbConstants.getApplicationSendingJmsName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyDeletion(Resource resource, SynchronizationConfiguration configSync) {
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(configSync, "configSync is mandatory. null value is forbidden");
		this.synchronizationService.propagateDeletion(resource, configSync.getListApplicationIdToResynchronize(),
				configSync.getSynchronizationType(), configSync.getSendingApplication());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyCollectionUpdate(Resource resource, SynchronizationRelationsName relationName,
			List<Resource> updatedResources, List<Resource> addedResources, List<Resource> removedResources) {
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");
		Assert.notNull(relationName, "relationName is mandatory. null value is forbidden");

		this.synchronizationService.propagateCollectionUpdate(resource, relationName.getValue(), updatedResources,
				addedResources, removedResources, MyEc3EsbConstants.getApplicationSendingJmsName());
	}

}
