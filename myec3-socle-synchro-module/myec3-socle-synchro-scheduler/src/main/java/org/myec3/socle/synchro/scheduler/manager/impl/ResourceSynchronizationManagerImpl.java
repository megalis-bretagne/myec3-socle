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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationFilter;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationFilterService;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.myec3.socle.synchro.scheduler.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Generic implementation to perform synchronization tasks on a resource with
 * type R. Provides common behaviour that will be inherited.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 * @param <R> concrete oject type, extending Resource. Concrete type of the
 *        resource to synchronize.
 */
public abstract class ResourceSynchronizationManagerImpl<R extends Resource>
		implements ResourceSynchronizationManager<R> {

	/**
	 * Quartz scheduler called by synchronization manager
	 */
	@Autowired
	@Qualifier("parallelSchedulerService")
	protected SchedulerService schedulerService;

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link SynchronizationSubscription} objects.
	 */
	@Autowired
	@Qualifier("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	/**
	 * Business Service injected by Spring container to perform operations on
	 * {@link SynchronizationFilter} objects.
	 */
	@Autowired
	@Qualifier("synchronizationFilterService")
	private SynchronizationFilterService synchronizationFilterService;

	/**
	 * Concrete type of the resource to synchronize
	 */
	private Class<R> entityClass;

	/**
	 * Logger log4j
	 */
	private final Logger logger;

	/**
	 * @return class name
	 */
	public Class<R> getEntityClass() {
		return entityClass;
	}

	/**
	 * @param entityClass the entityClass to set
	 */
	public void setEntityClass(Class<R> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * @return the SchedulerService
	 */
	public SchedulerService getSchedulerService() {
		return schedulerService;
	}

	/**
	 * @param schedulerService the schedulerService to set
	 */
	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	/**
	 * @return the SynchronizationSubscriptionService used to manage subscriptions
	 */
	public SynchronizationSubscriptionService getSynchronizationSubscriptionService() {
		return synchronizationSubscriptionService;
	}

	/**
	 * @param synchronizationSubscriptionService the
	 *                                           synchronizationSubscriptionService
	 *                                           to set
	 */
	public void setSynchronizationSubscriptionService(
			SynchronizationSubscriptionService synchronizationSubscriptionService) {
		this.synchronizationSubscriptionService = synchronizationSubscriptionService;
	}

	/**
	 * @return the SynchronizationFilterService used to manage xml sent
	 */
	public SynchronizationFilterService getSynchronizationFilterService() {
		return synchronizationFilterService;
	}

	/**
	 * @param SynchronizationFilterService the synchronizationFilterService to set
	 */
	public void setSynchronizationFilterService(SynchronizationFilterService synchronizationFilterService) {
		this.synchronizationFilterService = synchronizationFilterService;
	}

	/**
	 * @return the Logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Default constructor. Get the concrete implementation for generic service and
	 * associate corresponding logger
	 */
	@SuppressWarnings("unchecked")
	public ResourceSynchronizationManagerImpl() {
		// .
		@SuppressWarnings("rawtypes")
		Class clazz = getClass();
		Type genericSuperclass = clazz.getGenericSuperclass();

		if (genericSuperclass.equals(Object.class)) {
			this.entityClass = (Class<R>) Resource.class;
		} else {
			while (!(genericSuperclass instanceof ParameterizedType)) {
				clazz = clazz.getSuperclass();
				genericSuperclass = clazz.getGenericSuperclass();
			}
			this.entityClass = (Class<R>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
		}
		this.logger = LoggerFactory.getLogger(entityClass);
	}

	/**
	 * Method called when a resource is persisted into the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public abstract void synchronizeCreation(R resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Method called when a resource is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public abstract void synchronizeDeletion(R resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Method called when a resource is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public abstract void synchronizeUpdate(R resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Method called when a collection of a resource is updated from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public abstract void synchronizeCollectionUpdate(R resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication);

	/**
	 * Method called when a collection of a resource is deleted from the database
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public abstract void synchronizeCollectionRemove(R resource, String relationName, List<Resource> removedResources,
			String sendingApplication);

	/**
	 * Method called in order to create new synchronization job for each
	 * applications which have subscribed at this type of resource
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public abstract void setCommonJob(SynchronizationJobType synchronizationJobType, R resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication);

}
