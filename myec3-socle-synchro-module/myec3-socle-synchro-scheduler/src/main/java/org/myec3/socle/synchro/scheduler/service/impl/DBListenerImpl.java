package org.myec3.socle.synchro.scheduler.service.impl;

import java.util.List;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Synchronized;
import org.myec3.socle.core.service.ServiceManager;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationQueue;
import org.myec3.socle.synchro.core.service.SynchronizationQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("dbListener")
public class DBListenerImpl {

	private static final Logger log = LoggerFactory.getLogger(DBListenerImpl.class);

	private static final int ROW_LIMIT = 50;

	@Autowired
	@Qualifier("serviceManager")
	private ServiceManager serviceManager;

	@Autowired
	@Qualifier("synchronizationService")
	private SynchronizationService synchronizationService;

	@Autowired
	@Qualifier("synchronizationQueueService")
	private SynchronizationQueueService synchronizationQueueService;

	public void retrieveSynchronizationTasks() {
		log.info("Looking for objets to synchronize from database");
		List<SynchronizationQueue> tasksList = null;

		try {

			// Check if synchronization tasks exist
			tasksList = this.synchronizationQueueService.findAll(ROW_LIMIT);

			for (SynchronizationQueue task : tasksList) {
				log.debug("task : " + task.getId());

				Resource resource = (Resource) this.serviceManager
						.getResourceServiceByResourceType(task.getResourceType()).findOne(task.getResourceId());

				// Lunch synchronization
				if ((resource != null) && (resource.getClass().isAnnotationPresent(Synchronized.class))) {
					this.propagateSynchronization(task.getSynchronizationJobType(), resource,
							SynchronizationType.SYNCHRONIZATION_QUEUE, task.getSendingApplication());
				}

				// Remove synchronization task from database
				this.synchronizationQueueService.deleteById(task.getId());
			}

		} catch (Exception e) {
			log.error("An error has occured during retrieve synchronization tasks from database", e);
		}
	}

	/**
	 * Propagate the synchronization depending on the synchronization job type
	 * 
	 * @param synchronizationJobType
	 * @param resource
	 * @param synchronizationType
	 * @param sendingApplication
	 */
	public void propagateSynchronization(SynchronizationJobType synchronizationJobType, Resource resource,
			SynchronizationType synchronizationType, String sendingApplication) {

		log.debug("Enterring in  propagateSynchronization of DBListener");

		// validate mandatory parameters
		Assert.notNull(synchronizationJobType, "synchronizationJobType is mandatory. null value is forbidden");
		Assert.notNull(resource, "resource is mandatory. null value is forbidden");

		switch (synchronizationJobType) {
		case CREATE:
			this.synchronizationService.propagateCreation(resource, null, synchronizationType, sendingApplication);
			break;
		case UPDATE:
			this.synchronizationService.propagateUpdate(resource, null, synchronizationType, sendingApplication);
			break;
		case DELETE:
			this.synchronizationService.propagateDeletion(resource, null, synchronizationType, sendingApplication);
			break;
		}
	}
}
