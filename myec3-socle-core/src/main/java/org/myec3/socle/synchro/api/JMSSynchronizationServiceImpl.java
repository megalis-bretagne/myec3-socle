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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.myec3.socle.core.constants.MyEc3EsbConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.*;
import org.myec3.socle.core.service.impl.ServiceManager;
import org.myec3.socle.core.tools.SynchronizationMarshaller;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationParametersType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.api.quartz.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.jms.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of synchronization service used to send JMS messages to the
 * remote ESB queue IN.
 *
 * @see SynchronizationService
 *
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("synchronizationCoreService")
public class JMSSynchronizationServiceImpl implements SynchronizationService {

	private static final Logger log = LoggerFactory.getLogger(JMSSynchronizationServiceImpl.class);

	@Autowired
	@Qualifier("synchroQuartzSchedulerService")
	private SchedulerService schedulerService;

	@Autowired
	@Qualifier("serviceManager")
	private ServiceManager serviceManager;

	private Session sendSession = null;
	private MessageProducer sender = null;
	private ConnectionFactory factory = null;
	private Connection connect = null;
	private Queue sendQueue = null;

	/**
	 * Initialize JMS Connection Factory
	 */
	public JMSSynchronizationServiceImpl() {

	}

	/**
	 * Method started by Spring (see jmsInMyec3Context.xml) to activate the
	 * connection to the remote ESB
	 *
	 */
	@PostConstruct
	public void startConnection() {
		try {
			factory = new ActiveMQConnectionFactory(MyEc3EsbConstants.getInUsername(),
					MyEc3EsbConstants.getInPassword(), MyEc3EsbConstants.getInUrl());
			connect = factory.createConnection(MyEc3EsbConstants.getInUsername(), MyEc3EsbConstants.getInPassword());

			sendSession = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
			sendQueue = sendSession.createQueue(MyEc3EsbConstants.getInUsername());
			sender = sendSession.createProducer(sendQueue);

			log.info("Application " + MyEc3EsbConstants.getApplicationSendingJmsName() + " is connected to broker");
		} catch (Exception e) {
			log.error("L'application " + MyEc3EsbConstants.getApplicationSendingJmsName()
					+ " n'arrive pas à se connecter au broker JMS " + MyEc3EsbConstants.getInUrl(),e);
		}
	}

	/**
	 * Check if the connection to the remote ESB is OK or restart it. This method is
	 * launched by quartz intermittently.
	 */
	public void checkJmsConnectionListener() {
		log.debug("Enterring in checkJmsConnectionListener");
		try {
			Assert.notNull(sender.getDeliveryMode());
		} catch (Exception e) {
			log.error("An error has occured when checking the current connection", e);
			// Try to restart connection
			log.info("Trying to restart connection to remote ESB queue");
			this.startConnection();
		}
	}

	/**
	 * Display informations into the console if quartz job has failed X times
	 */
	public void logErrorMessage() {
		log.info("Application : " + MyEc3EsbConstants.getApplicationSendingJmsName() + ".Erreur après "
				+ MyEc3EsbConstants.getMaxAttempts() + " tentatives de transmission du message JMS au broker : "
				+ MyEc3EsbConstants.getInUrl());
	}

	/**
	 * {@inheritDoc}
	 */
	public void propagateCUD(Resource resource, List<Long> listApplicationIdToResynchronize,
							 SynchronizationType synchronizationType, SynchronizationJobType synchronizationJobType, int nbAttempts) {

		try {
			OutputStream baOutputStream = this.cleanAndMarshalResource(resource);

			MapMessage msg = this.sendSession.createMapMessage();

			msg.setObject(SynchronizationParametersType.RESOURCE.toString(), baOutputStream.toString());
			msg.setObject(SynchronizationParametersType.LIST_APPLICATION_ID.toString(),
					listApplicationIdToResynchronize);
			msg.setObject(SynchronizationParametersType.SYNCHRONIZATION_TYPE.toString(),
					synchronizationType.toString());
			msg.setObject(SynchronizationParametersType.SYNCHRONIZATION_JOB_TYPE.toString(),
					synchronizationJobType.toString());
			msg.setObject(SynchronizationParametersType.SENDING_APPLICATION.toString(),
					MyEc3EsbConstants.getApplicationSendingJmsName());

			this.sender.send(msg, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY,
					MyEc3EsbConstants.MESSAGE_LIFESPAN);
		} catch (Exception e) {
			log.error("An error has occured during send jms to ActiveMQ broker " + MyEc3EsbConstants.getInUrl(), e);
			log.error("[RESOURCE ID] " + resource.getId());
			log.error("[RESOURCE TYPE] " + resource.getClass().getSimpleName());
			log.error("[SYNCHRONIZATION TYPE] " + synchronizationType.toString());
			log.error("[METHOD] " + synchronizationJobType.toString());
			log.error("[NB ATTEMPS] " + nbAttempts);

			if (nbAttempts < MyEc3EsbConstants.getMaxAttempts()) {
				// We check the jms connection
				this.checkJmsConnectionListener();

				this.schedulerService.addImmediatePropagateCUDTrigger(resource, listApplicationIdToResynchronize,
						synchronizationType, synchronizationJobType, ++nbAttempts);

				log.error("Application : " + MyEc3EsbConstants.getApplicationSendingJmsName()
						+ ".Erreur lors de la transmission du message JMS au broker : " + MyEc3EsbConstants.getInUrl());

			} else {
				this.logErrorMessage();
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void propagateCC(Resource resource, String relationName, List<Resource> createdResources,
							String sendingApplication, int nbAttempts) {
		List<String> resourcesStream = null;
		OutputStream outputStream = null;

		try {
			MapMessage msg = this.sendSession.createMapMessage();
			outputStream = this.cleanAndMarshalResource(resource);

			msg.setObject(SynchronizationParametersType.RESOURCE.toString(), outputStream.toString());
			msg.setObject(SynchronizationParametersType.SYNCHRONIZATION_JOB_TYPE.toString(),
					SynchronizationJobType.COLLECTION_CREATE.toString());
			msg.setObject(SynchronizationParametersType.RELATION_NAME.toString(), relationName);
			msg.setObject(SynchronizationParametersType.SENDING_APPLICATION.toString(),
					MyEc3EsbConstants.getApplicationSendingJmsName());

			if (createdResources != null) {
				resourcesStream = new ArrayList<String>();
				for (Resource createdResource : createdResources) {
					outputStream = this.cleanAndMarshalResource(createdResource);
					resourcesStream.add(outputStream.toString());
				}
			}

			msg.setObject(SynchronizationParametersType.UPDATED_RESOURCES.toString(), resourcesStream);

			this.sender.send(msg, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY,
					MyEc3EsbConstants.MESSAGE_LIFESPAN);

		} catch (Exception e) {
			log.error("An error has occured during send jms to ActiveMQ broker " + MyEc3EsbConstants.getInUrl(), e);
			log.error("[RESOURCE ID] " + resource.getId());
			log.error("[RELATION NAME] " + relationName);
			log.error("[SENDING APPLICATION] " + sendingApplication);
			log.error("[NB ATTEMPS] " + nbAttempts);

			if (nbAttempts < MyEc3EsbConstants.getMaxAttempts()) {
				// We check the jms connection
				this.checkJmsConnectionListener();

				this.schedulerService.addImmediatePropagateCCTrigger(resource, relationName, createdResources,
						sendingApplication, ++nbAttempts);
				log.error("Application : " + MyEc3EsbConstants.getApplicationSendingJmsName()
						+ ".Erreur lors de la transmission du message JMS au broker : " + MyEc3EsbConstants.getInUrl());

			} else {
				this.logErrorMessage();
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void propagateCU(Resource resource, String relationName, List<Resource> updatedResources,
							List<Resource> addedResources, List<Resource> removedResources, String sendingApplication, int nbAttempts) {
		List<String> resourcesStream = null;
		OutputStream outputStream = null;

		try {
			MapMessage msg = this.sendSession.createMapMessage();

			outputStream = this.cleanAndMarshalResource(resource);

			msg.setObject(SynchronizationParametersType.RESOURCE.toString(), outputStream.toString());

			msg.setObject(SynchronizationParametersType.SYNCHRONIZATION_JOB_TYPE.toString(),
					SynchronizationJobType.COLLECTION_UPDATE.toString());
			msg.setObject(SynchronizationParametersType.RELATION_NAME.toString(), relationName);
			msg.setObject(SynchronizationParametersType.SENDING_APPLICATION.toString(),
					MyEc3EsbConstants.getApplicationSendingJmsName());

			if (updatedResources != null) {
				resourcesStream = new ArrayList<String>();
				for (Resource updatedResource : updatedResources) {
					outputStream = this.cleanAndMarshalResource(updatedResource);
					resourcesStream.add(outputStream.toString());
				}
				msg.setObject(SynchronizationParametersType.UPDATED_RESOURCES.toString(), resourcesStream);
			}

			msg.setObject(SynchronizationParametersType.UPDATED_RESOURCES.toString(), resourcesStream);
			if (addedResources != null) {
				resourcesStream = new ArrayList<String>();
				for (Resource addedResource : addedResources) {
					outputStream = this.cleanAndMarshalResource(addedResource);
					resourcesStream.add(outputStream.toString());
				}
				msg.setObject(SynchronizationParametersType.ADDED_RESOURCES.toString(), resourcesStream);
			}

			msg.setObject(SynchronizationParametersType.ADDED_RESOURCES.toString(), resourcesStream);
			if (removedResources != null) {
				resourcesStream = new ArrayList<String>();
				for (Resource removedResource : removedResources) {
					outputStream = this.cleanAndMarshalResource(removedResource);
					resourcesStream.add(outputStream.toString());
				}

				msg.setObject(SynchronizationParametersType.REMOVED_RESOURCES.toString(), resourcesStream);
			}

			this.sender.send(msg, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY,
					MyEc3EsbConstants.MESSAGE_LIFESPAN);

		} catch (Exception e) {
			log.error("An error has occured during send jms to ActiveMQ broker " + MyEc3EsbConstants.getInUrl(), e);
			log.error("[RESOURCE ID] " + resource.getId());
			log.error("[RELATION NAME] " + relationName);
			log.error("[SENDING APPLICATION] " + sendingApplication);
			log.error("[NB ATTEMPS] " + nbAttempts);

			if (nbAttempts < MyEc3EsbConstants.getMaxAttempts()) {
				// We check the jms connection
				this.checkJmsConnectionListener();

				this.schedulerService.addImmediatePropagateCUTrigger(resource, relationName, updatedResources,
						addedResources, removedResources, sendingApplication, ++nbAttempts);

			} else {
				this.logErrorMessage();
			}
		}
	}

	public Boolean isSynchronizable(Resource resource) {
		if (resource.getClass().isAnnotationPresent(Synchronized.class)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propagateCollectionCreate(Resource resource, String relationName, List<Resource> createdResources,
										  String sendingApplication) {
		if (this.isSynchronizable(resource)) {
			this.schedulerService.addImmediatePropagateCCTrigger(resource, relationName, createdResources,
					sendingApplication, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propagateCollectionUpdate(Resource resource, String relationName, List<Resource> updatedResources,
										  List<Resource> addedResources, List<Resource> removedResources, String sendingApplication) {
		if (this.isSynchronizable(resource)) {
			this.schedulerService.addImmediatePropagateCUTrigger(resource, relationName, updatedResources,
					addedResources, removedResources, sendingApplication, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propagateCreation(Resource resource, List<Long> listApplicationIdToResynchronize,
								  SynchronizationType synchronizationType, String sendingApplication) {
		this.schedulerService.addImmediatePropagateCUDTrigger(resource, listApplicationIdToResynchronize,
				synchronizationType, SynchronizationJobType.CREATE, 0);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propagateDeletion(Resource resource, List<Long> listApplicationIdToResynchronize,
								  SynchronizationType synchronizationType, String sendingApplication) {
		if (this.isSynchronizable(resource)) {
			this.schedulerService.addImmediatePropagateCUDTrigger(resource, listApplicationIdToResynchronize,
					synchronizationType, SynchronizationJobType.DELETE, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propagateUpdate(Resource resource, List<Long> listApplicationIdToResynchronize,
								SynchronizationType synchronizationType, String sendingApplication) {
		if (this.isSynchronizable(resource)) {
			if (resource.getClass().getSuperclass().equals(Profile.class)) {
				// synchronize if the profile is enabled, do nothing
				if (((Profile) resource).getEnabled().equals(Boolean.FALSE)) {
					this.schedulerService.addImmediatePropagateCUDTrigger(resource, listApplicationIdToResynchronize,
							synchronizationType, SynchronizationJobType.DELETE, 0);
				}
			}

			this.schedulerService.addImmediatePropagateCUDTrigger(resource, listApplicationIdToResynchronize,
					synchronizationType, SynchronizationJobType.UPDATE, 0);
		}
	}

	/**
	 * Method used to clean resource collections before marshalling
	 *
	 * @param resource : the {@link Resource} to clean
	 * @return ByteArrayOutputStream
	 */
	public ByteArrayOutputStream cleanAndMarshalResource(Resource resource) {
		Assert.notNull(resource, "resource can't be null");

		// We clean resource collections
		this.cleanResourceCollections(resource);

		// To finish, we marshall the resource and return the
		// ByteArrayOutputStream
		return SynchronizationMarshaller.marshalResource(resource);
	}

	/**
	 * This method allows to clean collections of resource unused for marshalling
	 * the resource.
	 *
	 * If you not use this step an error "Laisy initilize collection of roles"
	 * occure during the marshall of the object (because we are not into the
	 * hibernate transaction)
	 *
	 * @param resource : the {@link Resource} to clean
	 */
	public void cleanResourceCollections(Resource resource) {
		Assert.notNull(resource, "resource can't be null");
		log.debug("Cleaning resource " + resource.getClass().getSimpleName());

		if (AgentProfile.class.equals(resource.getClass())) {
			((AgentProfileService) this.serviceManager.getResourceServiceByClassName(resource))
					.cleanCollections((AgentProfile) resource);
		}
		if (EmployeeProfile.class.equals(resource.getClass())) {
			((EmployeeProfileService) this.serviceManager.getResourceServiceByClassName(resource))
					.cleanCollections((EmployeeProfile) resource);
		}
		if (Organism.class.equals(resource.getClass())) {
			((OrganismService) this.serviceManager.getResourceServiceByClassName(resource))
					.cleanCollections((Organism) resource);
		}
		if (OrganismDepartment.class.equals(resource.getClass())) {
			((OrganismDepartmentService) this.serviceManager.getResourceServiceByClassName(resource))
					.cleanCollections((OrganismDepartment) resource);
		}
		if (Company.class.equals(resource.getClass())) {
			((CompanyService) this.serviceManager.getResourceServiceByClassName(resource))
					.cleanCollections((Company) resource);
		}
		if (CompanyDepartment.class.equals(resource.getClass())) {
			((CompanyDepartmentService) this.serviceManager.getResourceServiceByClassName(resource))
					.cleanCollections((CompanyDepartment) resource);
		}
		if (Establishment.class.equals(resource.getClass())) {
			((EstablishmentService) this.serviceManager.getResourceServiceByClassName(resource))
					.cleanCollections((Establishment) resource);
		}
	}
}