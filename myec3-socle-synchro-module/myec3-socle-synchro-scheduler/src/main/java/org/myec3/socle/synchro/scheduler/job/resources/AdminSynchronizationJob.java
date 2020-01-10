package org.myec3.socle.synchro.scheduler.job.resources;

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.springframework.stereotype.Component;

/**
 * Concrete job implementation used when the resource to synchronize is an
 * {@link AdminProfile}. This class implements abstract methods declared into
 * ResourcesSynchronizationJob.
 * 
 * This class use a REST client to send the resource.
 * 
 * @see ResourcesSynchronizationJob<T>
 * @see org.myec3.socle.ws.client.ResourceWsClient<T>
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
@Component
public class AdminSynchronizationJob extends
		ResourcesSynchronizationJob<AdminProfile> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage create(AdminProfile resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {

		return resourceWsClient.post(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage delete(AdminProfile resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {

		return resourceWsClient.delete(resource, synchronizationSubscription);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage update(AdminProfile resource,
			SynchronizationSubscription synchronizationSubscription,
			ResourceWsClient resourceWsClient) {
		return resourceWsClient.put(resource, synchronizationSubscription);
	}

}
