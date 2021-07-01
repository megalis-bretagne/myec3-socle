/**
 * Copyright (c) 2011 Atos Bourgogne
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
package org.myec3.socle.webapp.pages.company.employee;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.tools.EbDate;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.text.SimpleDateFormat;

/**
 * Page used to display employee details {@link EmployeeProfile}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/employee/View.tml<br
 * />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Anthony Colas <anthony.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class View extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@InjectPage
	@Property
	private View viewPage;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Inject
	@Named("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	private EmployeeProfile employeeProfile;

	@Property
	private boolean isHimselfAndNotAdmin = false;

	@Override
	public Object hasRights(Profile profile) {
		Profile loggedProfile = super.getLoggedProfile();
		if (loggedProfile != null && loggedProfile.equals(profile) && !super.getIsFunctionalAdmin()
				&& !super.getIsManagerEmployee()) {
			this.isHimselfAndNotAdmin = true;
			return Boolean.TRUE;
		}
		return super.hasRights(profile);
	}

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		super.initUser();

		this.employeeProfile = this.employeeProfileService.findOne(id);
		if (null == this.employeeProfile) {
			return Boolean.FALSE;
		}

		if (null == this.employeeProfile.getAddress()) {
			this.employeeProfile.setAddress(new Address());
		}

		// Check if loggedUser can access to this user
		return this.hasRights(this.employeeProfile);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.employeeProfile != null) ? this.employeeProfile.getId() : null;
	}

	@OnEvent(value = "action", component = "regeneratePassword")
	public Object regeneratePasswordOfAgentProfile() {
		// redirect to the main page if the user is not Enable
		if (!this.employeeProfile.getUser().isEnabled()) {
			return viewPage;
		}
		try {
			this.employeeProfile.getUser()
					.setPassword(this.userService.generateHashPassword(this.getMessages().get("default-password")));

			this.employeeProfile.getUser().setModifDatePassword(EbDate.getDateNow());

			this.employeeProfile.getUser()
					.setExpirationDatePassword(EbDate.addDays(this.employeeProfile.getUser().getModifDatePassword(),
							GuWebAppConstants.expirationTimeRegeneratePassword));

			this.userService.update(this.employeeProfile.getUser());
			this.synchronizationService.notifyUpdate(this.employeeProfile);

			this.successMessage = this.getMessages().get("regenerate-password-success");
		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("regenerate-password-error");
		}
		return this;
	}

	// Getters n Setters
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public EmployeeProfile getEmployeeProfile() {
		return this.employeeProfile;
	}

	public SimpleDateFormat getDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat;
	}

	public SimpleDateFormat getTimestampFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return dateFormat;
	}

	public Boolean getModifyDisplay() {
		if (this.hasRights(this.employeeProfile.getEstablishment())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Check if current employee is enabled
	 * @return
	 */
	public Boolean getIsEnabled() {
		if (employeeProfile.getUser() == null) {
			return Boolean.FALSE;
		}
		return employeeProfile.getUser().isEnabled();
	}
}
