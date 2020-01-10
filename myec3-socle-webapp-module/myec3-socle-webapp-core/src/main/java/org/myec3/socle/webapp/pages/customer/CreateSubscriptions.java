package org.myec3.socle.webapp.pages.customer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

public class CreateSubscriptions extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@SuppressWarnings("unused")
	@Property
	private String errorMessage;

	@Property
	private List<Application> availableApplications;

	private GenericListEncoder<Application> applicationEncoder;

	@Persist
	private Customer customer;

	@Persist
	private UploadedFile logo;

	@Persist
	private Set<Application> selectedApplications;

	private Application applicationLoop;

	@InjectPage
	private CreateFirstAdmin createAdminPage;

	@Inject
	private ComponentResources componentResources;

	@OnEvent(EventConstants.ACTIVATE)
	public void onActivate() {
		if (this.selectedApplications == null) {
			this.selectedApplications = new HashSet<Application>();
		}

		this.availableApplications = this.applicationService.findAll();

		this.applicationEncoder = new GenericListEncoder<Application>(
				this.availableApplications);
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			List<Application> apps = new ArrayList<Application>();
			for (Application application : this.selectedApplications) {
				apps.add(application);
			}
			this.customer.setApplications(apps);
			this.createAdminPage.setCustomer(this.customer);
			this.createAdminPage.setLogo(this.logo);
		} catch (Exception e) {
			this.errorMessage = this.getMessages().get(
					"recording-error-message");
			return null;
		}
		return this.createAdminPage;
	}

	/* Getter and Setter */

	public Application getApplicationLoop() {
		return applicationLoop;
	}

	public void setApplicationLoop(Application applicationLoop) {
		this.applicationLoop = applicationLoop;
	}

	public boolean isSelected() {
		return getSelectedApplications().contains(getApplicationLoop());
	}

	public void setSelected(boolean selected) {
		if (selected) {
			getSelectedApplications().add(getApplicationLoop());
		} else {
			getSelectedApplications().remove(getApplicationLoop());
		}
	}

	public Set<Application> getSelectedApplications() {
		return selectedApplications;
	}

	public GenericListEncoder<Application> getApplicationEncoder() {
		return applicationEncoder;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public UploadedFile getLogo() {
		return logo;
	}

	public void setLogo(UploadedFile logo) {
		this.logo = logo;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public void discardPersistentFields() {
		this.componentResources.discardPersistentFieldChanges();
	}
}
