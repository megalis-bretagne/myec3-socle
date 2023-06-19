package org.myec3.socle.webapp.pages.synchroman.subscription;

import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.javascript.StylesheetOptions;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class SearchResult extends AbstractPage {

	// Template attributes
	@Persist
	private List<SynchronizationSubscription> synchronizationSubscriptionResult;

	@Inject
	@Named("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Property
	private SynchronizationSubscription synchronizationSubscriptionRow;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private Messages messages;

	// // Getters n Setters
	@Component
	private Grid synchronizationSubscriptionGrid;

	@SetupRender
	public void setup() {
		javaScriptSupport.importStylesheet(new StylesheetLink("static/css/print.css", new StylesheetOptions("print")));
		synchronizationSubscriptionGrid.getSortModel().clear();
		synchronizationSubscriptionGrid.getSortModel().updateSort("resourceLabel");
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<SynchronizationSubscription> getGridModel() {
		BeanModel<SynchronizationSubscription> model = this.beanModelSource
				.createDisplayModel(SynchronizationSubscription.class, this.messages);
		model.add("actions", null);
		model.add("application", null).sortable(Boolean.TRUE);
		model.include("application", "resourceLabel", "uri", "actions");
		return model;
	}

	// TODO just desactivate not delete
	@OnEvent(value = "action", component = "delete")
	public Object removeSynchronizationSubscription(Long id) {
		SynchronizationSubscription foundSynchronizationSubsciption = this.synchronizationSubscriptionService
				.findOne(id);
		if (foundSynchronizationSubsciption != null) {
			this.synchronizationSubscriptionService.deleteById(id);
			this.synchronizationSubscriptionResult.remove(foundSynchronizationSubsciption);
			this.successMessage = this.messages.get("delete-success");
		}
		return this;
	}

	public List<SynchronizationSubscription> getSynchronizationSubscriptionResult() {
		return synchronizationSubscriptionResult;
	}

	public void setSynchronizationSubscriptionResult(
			List<SynchronizationSubscription> synchronizationSubscriptionResult) {
		this.synchronizationSubscriptionResult = synchronizationSubscriptionResult;
	}

	public Integer getResultsNumber() {
		if (null == this.synchronizationSubscriptionResult)
			return 0;
		return this.synchronizationSubscriptionResult.size();
	}

	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}
}
