/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p/>
 * This file is part of MyEc3.
 * <p/>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p/>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.components;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.services.ComponentResultProcessorWrapper;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Component class used to manage (add or modify) employee profile
 * {@link EmployeeProfile}<br>
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/components/EmployeeForm.tml
 *
 * @author Anthony Colas <anthony.colas@atos.net>
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 *
 */
public class EmployeeForm extends AbstractPage {

	private static Log logger = LogFactory.getLog(EmployeeForm.class);

	// Services n pages
	@SuppressWarnings("rawtypes")
	@Environmental
	private ComponentEventResultProcessor componentEventResultProcessor;

	@Inject
	private ComponentResources componentResources;

	@Parameter(required = true)
	@Property
	private EmployeeProfile employeeProfile;

	@Parameter(required = true, defaultPrefix = "prop")
	@Property
	private Object cancelRedirect;

	@Property
	private SelectModel establishmentModel;

	@Component(id = "modification_form")
	private Form form;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Service("profileService")
	private ProfileService profileService;

	@Inject
	@Service("establishmentService")
	private EstablishmentService establishmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Inject
	@Service("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {

		// Check if email already exists (only for employee profile)
		if (this.employeeProfile.getEmail() == null
				|| this.employeeProfile.getEmail().contains(" ")
				|| this.employeeProfileService.emailAlreadyExists(
						this.employeeProfile.getEmail(), this.employeeProfile)) {
			form.recordError(this.getMessages().get(
					"recording-duplicate-error-message"));
		}

		// In case of creation of new employeeProfile (email equals username)
		if (this.getNewEmployeeProfile()) {
			// Check that username not already exists
			if (this.employeeProfile.getEmail() != null
					&& this.profileService.usernameAlreadyExists(
							this.employeeProfile.getEmail(), this.employeeProfile)) {
				form.recordError(this.getMessages().get(
						"recording-error-message-username"));
			}
		} else if (this.profileService.usernameAlreadyExists(
				this.employeeProfile.getUsername(), this.employeeProfile)) {
			form.recordError(this.getMessages().get(
					"recording-error-message-username"));
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public void onSuccess() {
		final ComponentResultProcessorWrapper callback = new ComponentResultProcessorWrapper(
				componentEventResultProcessor);
		this.componentResources.triggerEvent("modification_form", null,
				callback);
	}

	@OnEvent(EventConstants.CANCELED)
	public Object cancelRedirect() {
		return this.cancelRedirect;
	}

	// Getters
	public Boolean getNewEmployeeProfile() {
		if (null == this.employeeProfile.getId())
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

	public ValueEncoder<Establishment> getEstablishmentEncoder() {
		return new GenericListEncoder<Establishment>(establishmentService.findAllEstablishmentsByCompany(
				employeeProfile.getCompanyDepartment().getCompany()));
	}

	public Map<Establishment, String> getEstablishments() {
		Map<Establishment, String> establishments = new LinkedHashMap<Establishment, String>();
		List<Establishment> myEstablishments = establishmentService.findAllEstablishmentsByCompany(
				employeeProfile.getCompanyDepartment().getCompany());
		for (Establishment establishment : myEstablishments) {
			String printableEstablishment = " - " + establishment.getLabel();
			if (establishment.getNic() != null && !establishment.getNic().isEmpty()) {
				printableEstablishment = establishment.getNic() + printableEstablishment;
			} else if (establishment.getNic() != null && establishment.getNic().isEmpty()) {
				printableEstablishment = establishment.getLabel();
			}
			establishments.put(establishment, printableEstablishment);
		}
		return establishments;
	}

}
