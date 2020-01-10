package org.myec3.socle.webapp.pages.synchroman.role;

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
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.ProfileTypeRoleService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.exceptions.ApplicationCreationException;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@SuppressWarnings("unused")
public class Create extends AbstractPage {

	private static Logger logger = LogManager.getLogger(Create.class);

	// Services n pages
	@Inject
	private Messages messages;

	// Template properties
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@InjectPage
	private CreateDefaultRoleStatus createDefaultRoleStatus;

	@Property
	private Role role;

	@Property
	private ProfileTypeRole profileTypeRole;

	@Property
	private ProfileTypeValue profileTypeValueSelected;

	@Persist
	@Property
	private ProfileType profileTypeSelected;

	@Persist
	@Property
	private ProfileTypeValue profileTypeValue;

	@Property
	private Boolean defaultAdminSelected;

	@Property
	private Boolean defaultBasicSelected;

	@Property
	private String name;

	@Property
	private String label;

	@Property
	private String description;

	@Inject
	@Named("roleService")
	private RoleService roleService;

	// List<ProfileType> listProfileTypeValue;

	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@Inject
	@Named("profileTypeRoleService")
	private ProfileTypeRoleService profileTypeRoleService;

	@Inject
	@Named("profileTypeService")
	private ProfileTypeService profileTypeService;

	@Component(id = "creation_role_form")
	private Form form;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		role = new Role();
		// Check if loggedUser can access to this page
		return this.getIsAdmin();
	}

	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "creation_role_form")
	public void onValidate() {
		// We check if role with this name does not already exists
		// for the selected application
		Role foundRole = this.roleService.findByName(role.getName());
		if (foundRole != null) {
			this.form.recordError(this.messages.get("role-exists-error"));
		}
		// We check if the user has selected Admin as profileType
		if (profileTypeValueSelected != null && profileTypeValueSelected.name() == "ADMIN") {
			this.form.recordError(this.messages.get("not-allowed-error"));
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		/* create role */
		/* default value of label = name */
		// this.role.setLabel(this.role.getName());
		/* default value of enabled = true */
		this.role.setEnabled(Boolean.TRUE);
		try {
			this.roleService.create(role);
		} catch (ApplicationCreationException e) {
			this.errorMessage = this.messages.get("recording-error-message");
			return null;
		}
		/* create ProfileTypeRole */
		List<ProfileType> listProfileTypeSelected = new ArrayList<ProfileType>();

		// In case of the user has selected TOUS
		if (profileTypeValueSelected == null) {
			listProfileTypeSelected = this.profileTypeService.findAll();
			ProfileType AdminType = listProfileTypeSelected.get(0);
			listProfileTypeSelected.remove(AdminType);
		} else {
			// The user has selected only one structure type
			listProfileTypeSelected.add(this.profileTypeService.findByValue(profileTypeValueSelected));
		}

		// profileTypeSelected =
		// this.profileTypeService.findByValue(profileTypeValueSelected);
		for (ProfileType profileTypeSelected : listProfileTypeSelected) {
			ProfileTypeRole profileTypeRole = new ProfileTypeRole();
			profileTypeRole.setProfileType(profileTypeSelected);
			profileTypeRole.setRole(role);
			profileTypeRole.setDefaultAdmin(Boolean.FALSE);
			profileTypeRole.setDefaultBasic(Boolean.FALSE);
			this.profileTypeRoleService.create(profileTypeRole);
		}

		/*
		 * this.profileTypeRole.setDefaultAdmin(defaultAdminSelected);
		 * this.profileTypeRole.setDefaultBasic(defaultBasicSelected);
		 */

		/* We add the new role to selected application */
		this.role.getApplication().addRole(role);
		// We set the role to the next page
		this.createDefaultRoleStatus.setRole(this.role);
		return this.createDefaultRoleStatus;
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

	/**
	 * list for select ProfileType
	 */
//	public Map<ProfileType, String> getProfileTypeValuesList() {
//		List<ProfileType> listAvailableProfileType = this.profileTypeService
//				.findAll();
//		listAvailableProfileType.remove(0);
//		Map<ProfileType, String> mapProfileType = new HashMap<ProfileType, String>();
//		for (ProfileType profileType : listAvailableProfileType) {
//			mapProfileType.put(profileType, profileType.getValue()
//					.toString());
//		}
//		return mapProfileType;
//	}
//	
//	public ValueEncoder<ProfileType> getProfileTypeValueEncoder() {
//		List<ProfileType> listAvailableProfileType = this.profileTypeService
//				.findAll();
//		listAvailableProfileType.remove(0);
//		return  new GenericValueEncoder<ProfileType>(
//				listAvailableProfileType);
//	}
}