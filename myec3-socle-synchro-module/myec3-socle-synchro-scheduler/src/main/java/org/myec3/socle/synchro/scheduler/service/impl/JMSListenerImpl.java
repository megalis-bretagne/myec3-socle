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
package org.myec3.socle.synchro.scheduler.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.tools.SynchronizationMarshaller;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationParametersType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.config.SynchroSchedulerConfig;
import org.myec3.socle.synchro.scheduler.constants.MyEc3EsbConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Concrete implementation of synchronization listener used to retrieve the JMS
 * contained into the JMS queue.
 * 
 * This class retrieve incoming JMS messages, convert the xml rerieved into an
 * known resource object and initialize the synchronization process.
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("jmsListener")
public class JMSListenerImpl implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(JMSListenerImpl.class);

	private Connection connect = null;
	private Session receiveSession = null;
	private Queue receiveQueue = null;

	/**
	 * Business Service injected by Spring container to perform operations used to
	 * synchronize resource annoted by @Synchronize
	 */
	@Autowired
	@Qualifier("synchronizationService")
	private SynchronizationService synchronizationService;

	/**
	 * Default constructor. Do nothing.
	 */
	public JMSListenerImpl() {
	}

	/**
	 * Provides connection to the JMS queue defined into the ESB. And start the
	 * receive queue listener from specific ESB address.
	 */
	@PostConstruct
	public void startListener() {
		try {
			// Create new connection factory by using constants defined into
			// MyEc3EsbConstants
			ConnectionFactory factory = new ActiveMQConnectionFactory(MyEc3EsbConstants.getOutUsername(),
					MyEc3EsbConstants.getOutPassword(), MyEc3EsbConstants.getOutUrl());

			// Create the connection to the broker.
			connect = factory.createConnection(MyEc3EsbConstants.getOutUsername(), MyEc3EsbConstants.getOutPassword());

			// Create the connection session.
			receiveSession = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the receive queue
			receiveQueue = receiveSession.createQueue(MyEc3EsbConstants.getOutUsername());

			// Create the message consumer in order to catch JMS contained into
			// queue OUT
			MessageConsumer qReceiver = receiveSession.createConsumer(receiveQueue);
			qReceiver.setMessageListener(this);

			// Start connection
			connect.start();

			log.info("Successfully connected to remote ESB queue");
		} catch (Exception e) {
			log.error("Failed to initialize connection to remote ESB queue", e);
		}
	}

	/**
	 * Refresh current connection to JMS queue OUT. This method is called by a
	 * quartz job defined into {@link SynchroSchedulerConfig}
	 */
	public void refreshListener() {
		log.debug("Enterring in  refreshListener");
		try {
			// Close current connection to queue out
			Assert.notNull(this.connect);
			this.connect.close();

			// Create a new connection to queue out
			this.startListener();
		} catch (Exception e) {
			log.error("An error has occured when closing the current connection", e);
			// Try to reconnect the listener
			this.startListener();
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void onMessage(javax.jms.Message aMessage) {
		// validate parameters
		Assert.notNull(aMessage, "aMessage is mandatory. null value is forbidden");

		try {
			// Default return type of message received by JMS
			if (aMessage instanceof ActiveMQMapMessage) {
				log.debug("aMessage is an instance of ActiveMQMapMessage");
				ActiveMQMapMessage activeMQMapMessage = (ActiveMQMapMessage) aMessage;
				Map<String, Object> contentMap = activeMQMapMessage.getContentMap();

				// Initialize variables
				SynchronizationJobType synchronizationJobType = null;
				SynchronizationType synchronizationType = null;
				List<Long> listApplicationIdToResynchronize = null;
				Resource resource = null;
				String relationName = null;
				String sendingApplication = null;
				List<Resource> createdResources = null;
				List<Resource> updatedResources = null;
				List<Resource> addedResources = null;
				List<Resource> removedResources = null;

				// Retrieve parameters from the JMS message
				Object synchronizationTypeObj = contentMap
						.get(SynchronizationParametersType.SYNCHRONIZATION_TYPE.toString());
				Object synchronizationJobTypeObj = contentMap
						.get(SynchronizationParametersType.SYNCHRONIZATION_JOB_TYPE.toString());
				Object resourceObj = contentMap.get(SynchronizationParametersType.RESOURCE.toString());
				Object listApplicationIdToResynchronizeObj = contentMap
						.get(SynchronizationParametersType.LIST_APPLICATION_ID.toString());
				Object relationNameObj = contentMap.get(SynchronizationParametersType.RELATION_NAME.toString());
				Object sendingApplicationObj = contentMap
						.get(SynchronizationParametersType.SENDING_APPLICATION.toString());
				List<Object> createdResourcesObj = (List<Object>) contentMap
						.get(SynchronizationParametersType.CREATED_RESOURCES.toString());
				List<Object> updatedResourcesObj = (List<Object>) contentMap
						.get(SynchronizationParametersType.UPDATED_RESOURCES.toString());
				List<Object> addedResourcesObj = (List<Object>) contentMap
						.get(SynchronizationParametersType.ADDED_RESOURCES.toString());
				List<Object> removedResourcesObj = (List<Object>) contentMap
						.get(SynchronizationParametersType.REMOVED_RESOURCES.toString());

				resource = (Resource) SynchronizationMarshaller.unmarshalResource(resourceObj.toString());
				synchronizationJobType = SynchronizationJobType.valueOf(synchronizationJobTypeObj.toString());

				if (synchronizationTypeObj != null) {
					synchronizationType = SynchronizationType.valueOf(synchronizationTypeObj.toString());
				}

				if (listApplicationIdToResynchronizeObj != null) {
					listApplicationIdToResynchronize = (List<Long>) listApplicationIdToResynchronizeObj;
				}

				if (relationNameObj != null) {
					relationName = relationNameObj.toString();
				}

				if (sendingApplicationObj != null) {
					sendingApplication = sendingApplicationObj.toString();
				}

				if (createdResourcesObj != null) {
					createdResources = new ArrayList<Resource>();
					for (Object createdResourceObj : createdResourcesObj) {
						createdResources.add(
								(Resource) SynchronizationMarshaller.unmarshalResource(createdResourceObj.toString()));
					}
				}

				if (updatedResourcesObj != null) {
					updatedResources = new ArrayList<Resource>();
					for (Object updatedResourceObj : updatedResourcesObj) {
						updatedResources.add(
								(Resource) SynchronizationMarshaller.unmarshalResource(updatedResourceObj.toString()));
					}
				}

				if (addedResourcesObj != null) {
					addedResources = new ArrayList<Resource>();
					for (Object addedResourceObj : addedResourcesObj) {
						addedResources.add(
								(Resource) SynchronizationMarshaller.unmarshalResource(addedResourceObj.toString()));
					}
				}

				if (removedResourcesObj != null) {
					removedResources = new ArrayList<Resource>();
					for (Object removedResourceObj : removedResourcesObj) {
						removedResources.add(
								(Resource) SynchronizationMarshaller.unmarshalResource(removedResourceObj.toString()));
					}
				}

				// Propagate synchronization depending on synchronizationJobType
				this.propagateSynchronization(synchronizationJobType, resource, listApplicationIdToResynchronize,
						synchronizationType, sendingApplication, relationName, createdResources, updatedResources,
						addedResources, removedResources);

			} else {
				log.warn("Warning: A message was discarded because it could not be processed "
						+ "as a org.apache.activemq.command.ActiveMQMapMessage : "
						+ aMessage.getClass().getSimpleName());
			}
		} catch (Exception e) {
			log.error("JMSException while parsing incoming JMS.", e);
		}
	}

	/**
	 * Propagate the synchronization depending on the synchronization job type
	 * 
	 * @param synchronizationJobType           : synchronizationJobType type of job
	 *                                         synchronization (@see
	 *                                         SynchronizationJobType.class)
	 * @param resource                         : the resource synchronized
	 * @param listApplicationIdToResynchronize : the list of applications to
	 *                                         synchronize (if null, all
	 *                                         applications are synchronized)
	 * @param synchronizationType              : the type of synchronization (@see
	 *                                         SynchronizationType.class)
	 * @param sendingApplication               : the name of the application sending
	 *                                         JMS
	 * @param relationName                     : the name of the collection updated
	 *                                         (creation ,deletion or update).
	 * @param createdResources                 : the list of created resources
	 *                                         contained in the new collection
	 * @param updatedResources                 : the list of resources updated into
	 *                                         the collection
	 * @param addedResources                   : the list of new resources added
	 *                                         into the collection
	 * @param removedResources                 : the list of old resources removed
	 *                                         from the collection
	 */
	public void propagateSynchronization(SynchronizationJobType synchronizationJobType, Resource resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication, String relationName, List<Resource> createdResources,
			List<Resource> updatedResources, List<Resource> addedResources, List<Resource> removedResources) {
		log.debug("Enterring in  propagateSynchronization");
		// validate mandatory parameters
		Assert.notNull(synchronizationJobType, "synchronizationJobType is mandatory. null value is forbidden");
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");

		switch (synchronizationJobType) {
		case CREATE:
			this.synchronizationService.propagateCreation(resource, listApplicationIdToResynchronize,
					synchronizationType, sendingApplication);
			break;
		case UPDATE:
			this.synchronizationService.propagateUpdate(resource, listApplicationIdToResynchronize, synchronizationType,
					sendingApplication);
			break;
		case DELETE:
			this.synchronizationService.propagateDeletion(resource, listApplicationIdToResynchronize,
					synchronizationType, sendingApplication);
			break;
		case COLLECTION_CREATE:
			this.synchronizationService.propagateCollectionCreate(resource, relationName, createdResources,
					sendingApplication);
			break;
		case COLLECTION_UPDATE:
			this.synchronizationService.propagateCollectionUpdate(resource, relationName, updatedResources,
					addedResources, removedResources, sendingApplication);
			break;
		}
	}
}