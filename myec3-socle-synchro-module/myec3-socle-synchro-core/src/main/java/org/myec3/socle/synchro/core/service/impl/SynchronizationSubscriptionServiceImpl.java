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
package org.myec3.socle.synchro.core.service.impl;

import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.impl.AbstractGenericServiceImpl;
import org.myec3.socle.synchro.core.domain.dao.SynchronizationSubscriptionDao;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Concrete Service implementation providing methods specific to
 * {@link SynchronizationSubscription} objects used to manage subscriptions of
 * the applications witch have need to be synchronized.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("synchronizationSubscriptionService")
public class SynchronizationSubscriptionServiceImpl
		extends AbstractGenericServiceImpl<SynchronizationSubscription, SynchronizationSubscriptionDao>
		implements SynchronizationSubscriptionService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationSubscription> findAllByApplicationId(Long applicationId) {
		return this.dao.findAllByApplicationId(applicationId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationSubscription> findAllByResourceLabel(ResourceType resourceType) {
		return this.dao.findAllByResourceLabel(resourceType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationSubscription> findByResourceTypeAndApplicationId(ResourceType resourceType,
			Long applicationId) {
		return this.dao.findByResourceTypeAndApplicationId(resourceType, applicationId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Application> findAllApplicationsSubscribe() {
		return this.dao.findAllApplicationsSubscribe();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationSubscription> findAllByCriteria(ResourceType resourceType, Application application) {
		return this.dao.findAllByCriteria(resourceType, application);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SynchronizationSubscription> findAllByResourceLabelAndCustomer(ResourceType resourceType,
			Customer customer) {
		Assert.notNull(resourceType, "resourceType is mandatory. null value is forbidden.");
		Assert.notNull(customer, "customer is mandatory. null value is forbidden.");

		return this.dao.findAllByResourceLabelAndCustomer(resourceType, customer);
	}
}
