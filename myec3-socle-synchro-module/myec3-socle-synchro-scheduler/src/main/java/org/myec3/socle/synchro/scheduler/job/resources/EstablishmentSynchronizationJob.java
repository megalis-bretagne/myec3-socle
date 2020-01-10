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
package org.myec3.socle.synchro.scheduler.job.resources;

import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.springframework.stereotype.Component;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link Establishment}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 * 
 * This class use a REST client to send the resource.
 * 
 * @see ResourcesSynchronizationJob<T>
 * @see org.myec3.socle.ws.client.ResourceWsClient<T>
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 * 
 */
@Component
public class EstablishmentSynchronizationJob extends
		ResourcesSynchronizationJob<Establishment> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage create(Establishment resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		return resourceWsClient.post(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage delete(Establishment resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {

		return resourceWsClient.delete(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage update(Establishment resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {

		return resourceWsClient.put(resource, synchronizationSubscription);
	}
}
