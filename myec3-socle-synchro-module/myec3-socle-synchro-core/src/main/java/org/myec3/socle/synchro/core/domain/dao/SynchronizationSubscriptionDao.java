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
package org.myec3.socle.synchro.core.domain.dao;

import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;

/**
 * This interface define methods to perform specific queries on
 * {@link SynchronizationSubscription} objects). It only defines new specific
 * methods and inherits methods from {@link GenericSynchronizationDao}.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface SynchronizationSubscriptionDao extends
		GenericSynchronizationDao<SynchronizationSubscription> {

	SynchronizationSubscription findByResourceTypeAndApplicationId(
			ResourceType resourceType, Long applicationId);

	/**
	 * Find all {@link SynchronizationSubscription} associated to a given
	 * application ID.
	 * 
	 * @param applicationId
	 *            : the ID of an applicatio
	 * 
	 * @return List of {@link SynchronizationSubscription} associated to this
	 *         application. Return an empty list if no results or null in case
	 *         of error.
	 * 
	 */
	List<SynchronizationSubscription> findAllByApplicationId(Long applicationId);

	/**
	 * Find all {@link SynchronizationSubscription} associated to a given
	 * {@link ResourceType}.
	 * 
	 * @param resourceType
	 *            : the resourceType to filter on
	 * 
	 * @return List of {@link SynchronizationSubscription} associated to this
	 *         {@link ResourceType}. Return an empty list if no results or null
	 *         in case of error.
	 * 
	 */
	List<SynchronizationSubscription> findAllByResourceLabel(
			ResourceType resourceType);

	/**
	 * Find all {@link Application} subscribed to the synchrnization mecanism.
	 * 
	 * 
	 * @return List of {@link Application} subscribed to the synchrnization
	 *         mecanism. Return an empty list if no results or null in case of
	 *         error.
	 * 
	 */
	List<Application> findAllApplicationsSubscribe();

	/**
	 * Retrive all {@link SynchronizationSubscription} matching given criteria.
	 * All null valued parameter is ignored.
	 * 
	 * @param resourceType
	 *            : the {@link ResourceType} to filter on
	 * 
	 * @param application
	 *            : the application (@see Application) to filter on
	 * 
	 * @return List of {@link SynchronizationSubscription} with filtered
	 *         parameters. Return an empty list if no results or null in case of
	 *         error.
	 */
	List<SynchronizationSubscription> findAllByCriteria(
			ResourceType resourceType, Application application);

	/**
	 * Find all {@link SynchronizationSubscription} associated to a given
	 * {@link ResourceType} and a {@link Customer}
	 * 
	 * @param resourceType
	 *            : the resourceType to filter on
	 * @param customer
	 *            : the {@link Customer} to filter on
	 * 
	 * @return List of {@link SynchronizationSubscription} associated to this
	 *         {@link ResourceType} and {@link Customer}. Return an empty list
	 *         if no results or null in case of error.
	 * 
	 */
	List<SynchronizationSubscription> findAllByResourceLabelAndCustomer(
			ResourceType resourceType, Customer customer);
}
