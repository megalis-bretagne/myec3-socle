package org.myec3.socle.webapp.pages.synchroman.subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

public class Search extends AbstractPage {

	// Pages n Services
	@Inject
	@Named("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	// Template attributes
	@Component(id = "synchronization_subscription_search_form")
	private Form synchronizationSearchForm;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String infoMessage;

	@Inject
	private Messages messages;

	@InjectPage
	private SearchResult searchResultPage;

	private List<Application> applicationList;

	private ResourceType resourceTypeSelected;

	private Application applicationSelected;

	private GenericListEncoder<Application> applicationEncoder;

	// Form events
	@OnEvent(EventConstants.ACTIVATE)
	public Boolean onActivate() {
		this.applicationList = this.applicationService.findAll();
		this.applicationEncoder = new GenericListEncoder<Application>(this.applicationList);
		return Boolean.TRUE;
	}

	@OnEvent(component = "synchronization_subscription_search_form", value = EventConstants.SUCCESS)
	public Object onSuccess() {
		List<SynchronizationSubscription> synchronizationSubscriptionResult = new ArrayList<SynchronizationSubscription>();
		try {
			if (null != resourceTypeSelected || null != applicationSelected) {
				synchronizationSubscriptionResult = this.synchronizationSubscriptionService
						.findAllByCriteria(resourceTypeSelected, applicationSelected);
			} else {
				synchronizationSubscriptionResult = this.synchronizationSubscriptionService.findAll();
			}
			if ((synchronizationSubscriptionResult == null) || (synchronizationSubscriptionResult.isEmpty())) {
				this.infoMessage = this.messages.get("empty-result-message");
				return null;
			}
		} catch (Exception e) {
			this.synchronizationSearchForm.recordError(this.messages.get("error-message"));
		}
		this.searchResultPage.setSynchronizationSubscriptionResult(synchronizationSubscriptionResult);
		return SearchResult.class;
	}

	public Map<Application, String> getApplicationsList() {
		Map<Application, String> mapApplication = new HashMap<Application, String>();
		for (Application applicationItem : this.applicationList) {
			mapApplication.put(applicationItem, applicationItem.getName());
		}
		return mapApplication;
	}

	public GenericListEncoder<Application> getApplicationEncoder() {
		return this.applicationEncoder;
	}

	public Map<ResourceType, String> getResourceTypeModel() {
		ResourceType[] availableResourceType = ResourceType.values();

		Map<ResourceType, String> mapResourceType = new HashMap<ResourceType, String>();
		for (ResourceType resourceType : availableResourceType) {
			mapResourceType.put(resourceType, resourceType.toString());
		}
		return mapResourceType;
	}

	public GenericListEncoder<ResourceType> getResourceTypeEncoder() {
		ResourceType[] availableResourceType = ResourceType.values();
		List<ResourceType> listResourceType = new ArrayList<ResourceType>();
		for (ResourceType resourceType : availableResourceType) {
			listResourceType.add(resourceType);
		}
		GenericListEncoder<ResourceType> encoder = new GenericListEncoder<ResourceType>(listResourceType);
		return encoder;
	}

	public ResourceType getResourceTypeSelected() {
		return resourceTypeSelected;
	}

	public void setResourceTypeSelected(ResourceType resourceTypeSelected) {
		this.resourceTypeSelected = resourceTypeSelected;
	}

	public Application getApplicationSelected() {
		return applicationSelected;
	}

	public void setApplicationSelected(Application applicationSelected) {
		this.applicationSelected = applicationSelected;
	}
}
