package org.myec3.socle.webapp.pages.synchroman.subscription;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class DetailsSubscription extends AbstractPage {

	private static Logger logger = LogManager.getLogger(DetailsSubscription.class);

	@Inject
	@Named("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	private SynchronizationSubscription synchronizationSubscription;

	// Activate n Passivate
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return SearchResult.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		// Get synchronization log from the database
		this.synchronizationSubscription = this.synchronizationSubscriptionService.findOne(id);

		if (null == this.synchronizationSubscription) {
			logger.info("Finding synchronization subscription failed");
			return Boolean.FALSE;
		}

		logger.debug("Finding synchronization subscription successful");
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.synchronizationSubscription != null) ? this.synchronizationSubscription.getId() : null;
	}

	// GETTER AND SETTER
	public SynchronizationSubscription getSynchronizationSubscription() {
		return synchronizationSubscription;
	}

	public void setSynchronizationSubscription(SynchronizationSubscription synchronizationSubscription) {
		this.synchronizationSubscription = synchronizationSubscription;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
}
