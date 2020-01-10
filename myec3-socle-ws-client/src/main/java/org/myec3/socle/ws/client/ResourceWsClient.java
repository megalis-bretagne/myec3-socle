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
package org.myec3.socle.ws.client;

import javax.ws.rs.core.Response;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.impl.ResourceWsClientImpl;

/**
 * Interface that defines methods available for {@link ResourceWsClientImpl} in
 * order to make operation on {@link Resource} on the server via WebService
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface ResourceWsClient {

	/**
	 * Allows to retrieve a resource from the server
	 * 
	 * @param T                           : the resource to retrieve
	 * @param synchronizationSubscription : object that represent the api
	 *                                    subscription
	 * @return ClientResponse : a client (in-bound) HTTP response.
	 */
	public Response get(Resource resource, SynchronizationSubscription synchronizationSubscription);

	/**
	 * Allows to retrieve a resource from the server by the resource's ID
	 * 
	 * @param id                          : the resource's ID to retrieve
	 * @param synchronizationSubscription : object that represent the api
	 *                                    subscription
	 * @return ClientResponse : a client (in-bound) HTTP response.
	 */
	public Response get(Long id, SynchronizationSubscription synchronizationSubscription);

	/**
	 * POST (create) a resource on the server
	 * 
	 * @param T                           : the resource to create on the server
	 * @param synchronizationSubscription : object that represent the api
	 *                                    subscription
	 * @return ResponseMessage {@link ResponseMessage} : a custom response which
	 *         contain the HTTP response and an object Error {@link Error} if an
	 *         error has occured during the operation.
	 */
	public ResponseMessage post(Resource resource, SynchronizationSubscription synchronizationSubscription);

	/**
	 * PUT (update) a resource on the server
	 * 
	 * @param T                           : the distant resource to update on the
	 *                                    server
	 * @param synchronizationSubscription : object that represent the api
	 *                                    subscription
	 * @return ResponseMessage {@link ResponseMessage} : a custom response which
	 *         contain the HTTP response and an object Error {@link Error} if an
	 *         error has occured during the operation.
	 */
	public ResponseMessage put(Resource resource, SynchronizationSubscription synchronizationSubscription);

	/**
	 * PUT (update) a resource on the server whith complete attributes (i.e :
	 * password, etc.)
	 * 
	 * @param T                           : the distant resource to update on the
	 *                                    server
	 * @param synchronizationSubscription : object that represent the api
	 *                                    subscription
	 * @return ResponseMessage {@link ResponseMessage} : a custom response which
	 *         contain the HTTP response and an object Error {@link Error} if an
	 *         error has occured during the operation.
	 */
	public ResponseMessage putComplete(Resource resource, SynchronizationSubscription synchronizationSubscription);

	/**
	 * DELETE (delete) a resource on the server
	 * 
	 * @param T                           : the distant resource to delete on the
	 *                                    server
	 * @param synchronizationSubscription : object that represent the api
	 *                                    subscription
	 * @return ResponseMessage {@link ResponseMessage} : a custom response which
	 *         contain the HTTP response and an object Error {@link Error} if an
	 *         error has occured during the operation.
	 */
	public ResponseMessage delete(Resource resource, SynchronizationSubscription synchronizationSubscription);

}
