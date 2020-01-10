package org.myec3.socle.webapp.pages.synchroman.subscription;

import javax.inject.Named;

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
public class View extends AbstractPage {

	@Inject
	@Named("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	private SynchronizationSubscription synchronizationSubscription;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.synchronizationSubscription = this.synchronizationSubscriptionService.findOne(id);

		if (null == this.synchronizationSubscription) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.synchronizationSubscription != null) ? this.synchronizationSubscription.getId() : null;
	}

	// Getters n Setters
	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public SynchronizationSubscription getSynchronizationSubscription() {
		return synchronizationSubscription;
	}

	public void setSynchronizationSubscription(SynchronizationSubscription synchronizationSubscription) {
		this.synchronizationSubscription = synchronizationSubscription;
	}
}
