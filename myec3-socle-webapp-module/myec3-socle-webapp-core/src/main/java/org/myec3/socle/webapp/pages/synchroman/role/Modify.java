/**
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.synchroman.role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.ProfileTypeRoleService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to modify the role{@link Role}<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/synchroman/role/Modify.tml<br
 * />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 */

@SuppressWarnings("unused")
public class Modify extends AbstractPage {

	private static final Logger logger = LogManager.getLogger(Modify.class);

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

	@InjectPage
	private DetailRole detailsRolePage;

	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@Inject
	@Named("profileTypeRoleService")
	private ProfileTypeRoleService profileTypeRoleService;

	@Inject
	@Named("profileTypeService")
	private ProfileTypeService profileTypeService;

	@Property
	private Role role;

	@Property
	private ProfileTypeRole profileTypeRole;

	@Property
	private ProfileTypeValue profileTypeValueSelected;

	@Property
	private ProfileType profileTypeSelected;

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

	@Property
	private StructureTypeValue structureTypeValueSelected;

	// Services n pages
	@Inject
	private Messages messages;

	@Property
	private String errorMessage;

	@Component(id = "modification_form")
	private Form form;

	private List<ProfileTypeRole> profileTypeRoleList;

	// Page Activation n Passivation
	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	/**
	 * event activate
	 */

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return this.detailsRolePage;
	}

	@OnEvent(EventConstants.ACTIVATE)

	public Object onActivate(Long id) {
		this.role = this.roleService.findOne(id);
		if (null == this.role) {
			logger.error("[OnActivate] no role was found with with id : " + id);
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.role != null) ? this.role.getId() : null;
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		logger.debug("Enterring into method OnSuccess");
		try {
			this.roleService.update(this.role);
		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}
		this.detailsRolePage.setSuccessMessage(this.messages.get("recording-success-message"));
		this.detailsRolePage.setRole(this.role);
		return this.detailsRolePage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		this.detailsRolePage.setRole(this.role);
		return View.class;
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {
		// Check if a role with the same name already exists
		Role foundRole = this.roleService.findByName(role.getName());
		if (foundRole != null && foundRole.getId().equals(this.role.getId())) {
			this.form.recordError(this.messages.get("role-exists-error"));
		}
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
//	public Map<ProfileType, String> getProfileTypeValueList() {
//		List<ProfileType> listAvailableProfileType = this.profileTypeService
//				.findAll();
//		listAvailableProfileType.remove(1);
//		Map<ProfileType, String> mapProfileType = new HashMap<ProfileType, String>();
//		for (ProfileType profileType : listAvailableProfileType) {
//			mapProfileType.put(profileType, profileType.getValue()
//					.toString());
//		}
//		return mapProfileType;
//	}

	public ValueEncoder<ProfileType> getProfileTypeValueEncoder() {
		List<ProfileType> listAvailableProfileType = this.profileTypeService.findAll();
		return new GenericListEncoder<ProfileType>(listAvailableProfileType);
	}

}
