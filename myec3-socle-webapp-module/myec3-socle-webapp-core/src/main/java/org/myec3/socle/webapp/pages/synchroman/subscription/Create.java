package org.myec3.socle.webapp.pages.synchroman.subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationFilterService;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Create extends AbstractPage {

	private static Logger logger = LogManager.getLogger(Create.class);

	// Services n pages
	@Inject
	private Messages messages;

	// Template properties
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Inject
	@Named("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Inject
	@Named("synchronizationFilterService")
	private SynchronizationFilterService synchronizationFilterService;

	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@Property
	private SynchronizationSubscription synchronizationSubscription;

	private ResourceType resourceTypeSelected;

	@Component(id = "creation_subscription_form")
	private Form form;

	private Application applicationSelected;

	@InjectPage
	private View viewPage;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		synchronizationSubscription = new SynchronizationSubscription();
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "creation_subscription_form")
	public void onValidate() {
		// We check that the subscription not already exists
		SynchronizationSubscription foundSynchronizationSubscription = this.synchronizationSubscriptionService
				.findByResourceTypeAndApplicationId(resourceTypeSelected, applicationSelected.getId());

		if (foundSynchronizationSubscription != null) {
			logger.info("An subscription with this application and this resource type already exists");
			this.form.recordError(this.messages.get("subscription-already-exists-error"));
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			// Set Values
			synchronizationSubscription.setApplication(applicationSelected);
			synchronizationSubscription.setResourceLabel(resourceTypeSelected);

			// TODO faire ça mieux :o
			synchronizationSubscription.setSynchronizationFilter(synchronizationFilterService.findOne(new Long(1)));

			// We create a new subscription into the database
			this.synchronizationSubscriptionService.create(synchronizationSubscription);

		} catch (Exception ex) {
			logger.error(
					"Error during create a new SynchronizationSubscription : " + ex.getMessage() + " " + ex.getCause());
			this.form.recordError(this.messages.get("subscription-creation-error"));
			return null;
		}

		this.viewPage.setSuccessMessage(this.messages.get("recording-success-message"));
		this.viewPage.setSynchronizationSubscription(synchronizationSubscription);
		return this.viewPage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return Index.class;
	}

	public Map<Application, String> getApplicationsList() {
		List<Application> applicationList = new ArrayList<Application>();
		applicationList = this.applicationService.findAll();

		Map<Application, String> mapApplication = new HashMap<Application, String>();
		for (Application applicationItem : applicationList) {
			mapApplication.put(applicationItem, applicationItem.getName());
		}
		return mapApplication;
	}

	public GenericListEncoder<Application> getApplicationEncoder() {
		List<Application> availableApplications = this.applicationService.findAll();
		GenericListEncoder<Application> encoder = new GenericListEncoder<Application>(availableApplications);
		return encoder;
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

	public Application getApplicationSelected() {
		return applicationSelected;
	}

	public void setApplicationSelected(Application applicationSelected) {
		this.applicationSelected = applicationSelected;
	}

	public ResourceType getResourceTypeSelected() {
		return resourceTypeSelected;
	}

	public void setResourceTypeSelected(ResourceType resourceTypeSelected) {
		this.resourceTypeSelected = resourceTypeSelected;
	}

}
