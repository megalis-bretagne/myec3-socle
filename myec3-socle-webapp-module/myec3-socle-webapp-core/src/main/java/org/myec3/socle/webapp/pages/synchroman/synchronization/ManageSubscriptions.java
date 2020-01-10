package org.myec3.socle.webapp.pages.synchroman.synchronization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.synchro.api.SynchronizationService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ManageSubscriptions extends AbstractPage {

	@Inject
	@Service("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	@Inject
	@Service("synchronizationService")
	private SynchronizationService synchronizationService;

	@Persist
	private Resource resource;

	@Persist
	private ResourceType resourceType;

	@Property
	private String selectedMethodType;

	@Persist
	private Set<Application> selectedApplications;

	private Application applicationLoop;

	private List<Application> availableApplications;

	private List<Application> choosenApplications;

	private GenericListEncoder<Application> applicationEncoder;

	@Inject
	private Messages messages;

	// Template properties
	@SuppressWarnings("unused")
	@Property
	@Persist("Flash")
	private String errorMessage;

	@SuppressWarnings("unused")
	@Property
	@Persist("flash")
	private String successMessage;

	@SuppressWarnings("unused")
	@Component(id = "application_subscription_form")
	private Form applicationSubscriptionForm;

	// Form events
	@OnEvent(EventConstants.ACTIVATE)
	public Boolean onActivate() {
		List<SynchronizationSubscription> subscribableApplications = this.synchronizationSubscriptionService
				.findAllByResourceLabel(resourceType);

		this.selectedApplications = new HashSet<Application>();
		setSelectedApplications(selectedApplications);

		this.availableApplications = new ArrayList<Application>();
		for (SynchronizationSubscription synchronizationSubscription : subscribableApplications) {
			this.availableApplications.add(synchronizationSubscription
					.getApplication());
		}
		this.applicationEncoder = new GenericListEncoder<Application>(
				this.availableApplications);

		this.choosenApplications = new ArrayList<Application>();
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		if (this.choosenApplications.isEmpty()) {
			this.errorMessage = this.messages
					.get("empty-choosen-application-message");
			return null;
		}

		List<Long> listOfApplicationIdToSynchronize = new ArrayList<Long>();
		for (Application application : this.choosenApplications) {
			listOfApplicationIdToSynchronize.add(application.getId());
		}

		// Synchronize the resource
		this.synchronizeResource(this.resource,
				listOfApplicationIdToSynchronize);

		this.successMessage = this.messages.get("synchronize-success-message");

		return Boolean.TRUE;
	}

	@OnEvent(component = "cancel", value = EventConstants.ACTION)
	public Object onFormCancel() {
		return Index.class;
	}

	// METHODS
	/**
	 * This method allows to lunch a synchronization of an resource
	 * 
	 * @param resource
	 * @param listApplicationIdToSynchronize
	 */
	public void synchronizeResource(Resource resource,
			List<Long> listApplicationIdToSynchronize) {
		switch (MethodType.valueOf(this.selectedMethodType)) {
		case POST:
			this.synchronizationService.propagateCreation(resource,
					listApplicationIdToSynchronize,
					SynchronizationType.RESYNCHRONIZATION, null);
			break;
		case PUT:
			this.synchronizationService.propagateUpdate(resource,
					listApplicationIdToSynchronize,
					SynchronizationType.RESYNCHRONIZATION, null);
			break;
		case DELETE:
			this.synchronizationService.propagateDeletion(resource,
					listApplicationIdToSynchronize,
					SynchronizationType.RESYNCHRONIZATION, null);
			break;
		}
	}

	// GETTER AND SETTER
	public Set<Application> getSelectedApplications() {
		return selectedApplications;
	}

	public void setSelectedApplications(Set<Application> selectedApplications) {
		this.selectedApplications = selectedApplications;
	}

	public Application getApplicationLoop() {
		return applicationLoop;
	}

	public void setApplicationLoop(Application applicationLoop) {
		this.applicationLoop = applicationLoop;
	}

	public void setSelected(boolean selected) {
		if (selected) {
			this.choosenApplications.add(getApplicationLoop());
		}
	}

	public GenericListEncoder<Application> getApplicationEncoder() {
		return applicationEncoder;
	}

	public List<Application> getAvailableApplications() {
		return availableApplications;
	}

	public void setAvailableApplications(List<Application> availableApplications) {
		this.availableApplications = availableApplications;
	}

	public boolean isSelected() {
		return getSelectedApplications().contains(getApplicationLoop());
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
}
