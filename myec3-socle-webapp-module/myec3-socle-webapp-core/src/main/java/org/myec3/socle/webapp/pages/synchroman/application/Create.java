package org.myec3.socle.webapp.pages.synchroman.application;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.ResourceService;
import org.myec3.socle.core.service.StructureTypeApplicationService;
import org.myec3.socle.core.service.StructureTypeService;
import org.myec3.socle.core.service.exceptions.ApplicationCreationException;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@SuppressWarnings("unused")
public class Create extends AbstractPage {

	// Services n pages
	@Inject
	private Messages messages;

	@Inject
	@Service("structureTypeService")
	private StructureTypeService structureTypeService;

	@Inject
	@Service("applicationService")
	private ResourceService<Application> applicationService;

	@Inject
	@Service("structureTypeApplicationService")
	private StructureTypeApplicationService structureTypeApplicationService;

	@Inject
	@Named("customerService")
	private CustomerService customerService;

	// Template properties
	@Property
	@Persist("Flash")
	private String errorMessage;

	@Property
	private Application application;

	@Property
	private StructureTypeValue structureTypeValueSelected;

	@Component(id = "creation_application_form")
	private Form form;

	@InjectPage
	private CreateAppStructureTypeAssociations createAppStructureTypeAssociations;

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		this.application = new Application();
		return Boolean.TRUE;
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "name")
	public void validateName(String name) {

		// Check if an application with the same name already exists
		Application foundApplication = this.applicationService.findByName(name);

		if (foundApplication != null) {
			this.form.recordError(this.messages.get("application-exists-error"));
		}
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			this.applicationService.create(application);
		} catch (ApplicationCreationException e) {
			this.errorMessage = this.messages.get("recording-error-message");
			return null;
		}

		StructureType structureTypeSubscriptionSelected = null;
		if (structureTypeValueSelected != null) {
			structureTypeSubscriptionSelected = structureTypeService.findByValue(structureTypeValueSelected);
		}

		// We add application to current customer
		Customer currentCustomer = getCustomerOfLoggedProfile();
		currentCustomer.addApplication(application);
		customerService.update(currentCustomer);

		List<StructureType> listStructureTypeSelected = new ArrayList<StructureType>();

		// In case of the user has selected TOUS
		if (structureTypeSubscriptionSelected == null) {
			listStructureTypeSelected = this.structureTypeService.findAll();
		} else {
			// The user has selected only one structure type
			listStructureTypeSelected.add(structureTypeSubscriptionSelected);
		}

		// We create structureTypeApplication of the application
		for (StructureType structureType : listStructureTypeSelected) {
			StructureTypeApplication structureTypeApplication = new StructureTypeApplication();
			structureTypeApplication.setStructureType(structureType);
			structureTypeApplication.setApplication(this.application);
			structureTypeApplication.setStructureType(structureType);
			structureTypeApplication.setDefaultSubscription(Boolean.FALSE);
			structureTypeApplication.setSubscribable(Boolean.FALSE);

			try {
				// We create the structureTypeApplication
				structureTypeApplicationService.create(structureTypeApplication);
			} catch (RuntimeException ex) {
				this.errorMessage = this.messages.get("recording-error-message");
			}
		}

		// We set the application to the next page
		this.createAppStructureTypeAssociations.setApplication(application);

		// We return the second step
		return this.createAppStructureTypeAssociations;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return Index.class;
	}

//	public Map<StructureType, String> getStructureTypeSubscriptionModel() {
//		List<StructureType> listAvailableStructureType = this.structureTypeService
//				.findAll();
//
//		Map<StructureType, String> mapStructureType = new HashMap<StructureType, String>();
//		for (StructureType structureType : listAvailableStructureType) {
//			mapStructureType.put(structureType, structureType.getValue()
//					.toString());
//		}
//		return mapStructureType;
//	}
//	
//	public ValueEncoder<StructureType> getStructureTypeSubscriptionEncoder() {
//		List<StructureType> listAvailableStructureType = this.structureTypeService
//				.findAll();
//		return  new GenericValueEncoder<StructureType>(
//				listAvailableStructureType);
//	}

}
