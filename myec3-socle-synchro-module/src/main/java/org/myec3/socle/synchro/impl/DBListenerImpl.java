package org.myec3.socle.synchro.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import net.atos.mm.fwk.sysmm.SpoolMess;

import org.myec3.socle.core.constants.MyEc3SpoolMessConstants;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.SynchronizationQueue;
import org.myec3.socle.core.service.SynchronizationQueueService;
import org.myec3.socle.core.service.impl.ServiceManager;
import org.myec3.socle.synchro.api.SynchronizationJobType;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.SynchronizationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

@Named("dbListener")
public class DBListenerImpl {

	private static final Logger log = LoggerFactory
			.getLogger(DBListenerImpl.class);

	private static final int ROW_LIMIT = 50;
	private static final String SPOOLMESS_ID_DB_QUEUE_ERROR = "EBO-WEB-0313";
	private static final String SPOOLMESS_MSG_DB_QUEUE_ERROR = "[~BSID]EBOURGOGNE_BU[~ESID] Une erreur est survenue lors de la r\u00e9cup\u00e9ration des ressources \u00e0 synchroniser depuis la file d'attente";

	@Inject
	@Named("serviceManager")
	private ServiceManager serviceManager;

	@Inject
	@Named("synchronizationService")
	private SynchronizationService synchronizationService;

	@Inject
	@Named("synchronizationQueueService")
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
						.getResourceServiceByResourceType(
								task.getResourceType()).findById(
								task.getResourceId());

				// Lunch synchronization
				if (resource != null) {
					this.propagateSynchronization(
							task.getSynchronizationJobType(), resource,
							SynchronizationType.SYNCHRONIZATION_QUEUE,
							task.getSendingApplication());
				}

				// Remove synchronization task from database
				this.synchronizationQueueService.delete(task);
			}

		} catch (Exception e) {
			log.error(
					"An error has occured during retrieve synchronization tasks from database",
					e);
			log.debug("Sending spoolmess...");
			new SpoolMess(SPOOLMESS_ID_DB_QUEUE_ERROR,
					MyEc3SpoolMessConstants.SPOOLMESS_MINOR_ALERT,
					SPOOLMESS_MSG_DB_QUEUE_ERROR);
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
	public void propagateSynchronization(
			SynchronizationJobType synchronizationJobType, Resource resource,
			SynchronizationType synchronizationType, String sendingApplication) {

		log.debug("Enterring in  propagateSynchronization of DBListener");

		// validate mandatory parameters
		Assert.notNull(synchronizationJobType,
				"synchronizationJobType is mandatory. null value is forbidden");
		Assert.notNull(resource,
				"resource is mandatory. null value is forbidden");

		switch (synchronizationJobType) {
		case CREATE:
			this.synchronizationService.propagateCreation(resource, null,
					synchronizationType, sendingApplication);
			break;
		case UPDATE:
			this.synchronizationService.propagateUpdate(resource, null,
					synchronizationType, sendingApplication);
			break;
		case DELETE:
			this.synchronizationService.propagateDeletion(resource, null,
					synchronizationType, sendingApplication);
			break;
		}
	}
}
