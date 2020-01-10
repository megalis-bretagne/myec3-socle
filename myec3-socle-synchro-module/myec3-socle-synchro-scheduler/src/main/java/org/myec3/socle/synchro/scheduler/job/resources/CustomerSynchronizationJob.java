package org.myec3.socle.synchro.scheduler.job.resources;

import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.springframework.stereotype.Component;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link Customer}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 * 
 * This class use a REST client to send the resource.
 * 
 * @see ResourcesSynchronizationJob<T>
 * @see org.myec3.socle.ws.client.ResourceWsClient<T>
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component
public class CustomerSynchronizationJob extends
		ResourcesSynchronizationJob<Customer> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage create(Customer resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		return resourceWsClient.post(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage update(Customer resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		return resourceWsClient.put(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage delete(Customer resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		return resourceWsClient.delete(resource, synchronizationSubscription);
	}

}
