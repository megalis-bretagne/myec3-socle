package org.myec3.socle.webapp.pages.synchroman.subscription;

import java.util.*;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationFilter;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationFilterService;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Modify extends AbstractPage {

	private static Logger logger = LogManager.getLogger(Modify.class);

	// Services n pages
	@Inject
	private Messages messages;

	@Inject
	@Service("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Inject
	@Named("synchronizationFilterService")
	private SynchronizationFilterService synchronizationFilterService;

	@InjectPage
	private DetailsSubscription detailsSubscriptionPage;

	// Template properties
	@Property
	private String errorMessage;

	@Property
	private SynchronizationSubscription synchronizationSubscription;

	@Property
	private boolean applicationsDisplayed;

	@Property
	private boolean rolesDisplayed;

	@Component(id = "modification_form")
	private Form form;

	private Application applicationSelected;

	// Page Activation n Passivation
	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return this.detailsSubscriptionPage;
	}

	/**
	 * @param id : synchronizationSubscription id
	 * @return : boolean
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Boolean onActivate(Long id) {
		logger.debug("Enterring into method onActivate with id : {}", id);
		this.synchronizationSubscription = this.synchronizationSubscriptionService.findOne(id);

		if (null == this.synchronizationSubscription) {
			return Boolean.FALSE;
		}

		this.applicationSelected = this.synchronizationSubscription.getApplication();

		this.rolesDisplayed = this.synchronizationSubscription.getSynchronizationFilter().isAllRolesDisplayed();

		this.applicationsDisplayed = this.synchronizationSubscription.getSynchronizationFilter().isAllApplicationsDisplayed();

		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		logger.debug("Enterring into method onPassivate");
		return (this.synchronizationSubscription != null) ? this.synchronizationSubscription.getId() : null;
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		logger.debug("Enterring into method OnSuccess");
		try {
			this.synchronizationSubscription.setApplication(this.applicationSelected);
			SynchronizationFilter synchronizationFilter = synchronizationFilterService.findByApplicationsDisplayedAndByRolesDisplayed(this.applicationsDisplayed,this.rolesDisplayed);
			this.synchronizationSubscription.setSynchronizationFilter(synchronizationFilter);
			this.synchronizationSubscriptionService.update(this.synchronizationSubscription);
		} catch (Exception e) {
			logger.error("error onSuccess {}", e.getMessage());
			this.errorMessage = this.messages.get("recording-error-message");
			return null;
		}

		this.detailsSubscriptionPage.setSuccessMessage(this.messages.get("recording-success-message"));
		this.detailsSubscriptionPage.setSynchronizationSubscription(this.synchronizationSubscription);
		return this.detailsSubscriptionPage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		logger.debug("Enterring into method onFormCancel");
		this.detailsSubscriptionPage.setSynchronizationSubscription(this.synchronizationSubscription);
		return View.class;
	}

	// Getters n Setters
	public Map<Application, String> getApplicationsList() {
		List<Application> applicationList = this.applicationService.findAll();
		Map<Application, String> mapApplication = new HashMap<>();

		for (Application applicationItem : applicationList) {
			mapApplication.put(applicationItem, applicationItem.getName());
		}
		return mapApplication;
	}

	public GenericListEncoder<Application> getApplicationEncoder() {
		List<Application> availableApplications = this.applicationService.findAll();
		return new GenericListEncoder<>(availableApplications);
	}

	public Map<ResourceType, String> getResourceTypeModel() {
		ResourceType[] availableResourceType = ResourceType.values();

		EnumMap<ResourceType, String> mapResourceType = new EnumMap<>(ResourceType.class);
		for (ResourceType resourceType : availableResourceType) {
			mapResourceType.put(resourceType, resourceType.toString());
		}
		return mapResourceType;
	}

	public GenericListEncoder<ResourceType> getResourceTypeEncoder() {
		ResourceType[] availableResourceType = ResourceType.values();
		List<ResourceType> listResourceType = new ArrayList<>(Arrays.asList(availableResourceType));
		return new GenericListEncoder<>(listResourceType);
	}

	public Application getApplicationSelected() {
		return applicationSelected;
	}

	public void setApplicationSelected(Application applicationSelected) {
		this.applicationSelected = applicationSelected;
	}
}
