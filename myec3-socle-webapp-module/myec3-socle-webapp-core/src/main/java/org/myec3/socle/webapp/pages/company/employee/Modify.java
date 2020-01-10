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
package org.myec3.socle.webapp.pages.company.employee;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to modify an employee{@link EmployeeProfile} of a company
 * {@link Company}<br />
 *
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/employee/Modify.tml<br
 * />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public final class Modify extends AbstractPage {

	private static final Log logger = LogFactory.getLog(Modify.class);

	// Services n pages
	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Inject
	@Named("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	@InjectPage
	@Property
	private View viewPage;

	@InjectPage
	@Property
	private ListEmployees listEmployeesPage;

	// Template properties
	@SuppressWarnings("unused")
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String errorMessage;

	@Property
	private EmployeeProfile employeeProfile;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private Establishment oldEstablishment;

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

	// Page Activation n Passivation

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		if (this.employeeProfile != null && oldEstablishment == null) {
			oldEstablishment = this.employeeProfile.getEstablishment();
		}
		return listEmployeesPage;
	}

	/**
	 * @param id : organism id
	 * @return : boolean
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		super.initUser();

		this.employeeProfile = this.employeeProfileService.findOne(id);
		if (null == this.employeeProfile) {
			return false;
		}

		// redirect to the main page if the user is not Enable
		if (!this.employeeProfile.getUser().isEnabled()) {
			return viewPage;
		}
		if (null == this.employeeProfile.getAddress()) {
			this.employeeProfile.setAddress(new Address());
		}

		if (oldEstablishment == null) {
			oldEstablishment = this.employeeProfile.getEstablishment();
		}

		// Check if loggedUser can access to this user
		return this.hasRights(this.employeeProfile);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.employeeProfile != null) ? this.employeeProfile.getId() : null;
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			// Find out if profile establishemnt changed
			if (oldEstablishment != null && !this.employeeProfile.getEstablishment().equals(oldEstablishment)) {
				this.removeEmployeeManagerRole(this.employeeProfile);
			}

			this.employeeProfileService.update(this.employeeProfile);
			this.synchronizationService.notifyUpdate(this.employeeProfile);
		} catch (Exception e) {
			logger.error("An error occured when saving employee !", e);
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}

		if (isHimselfAndNotAdmin) {
			this.viewPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
			this.viewPage.setEmployeeProfile(this.employeeProfile);
			return this.viewPage;
		}
		this.listEmployeesPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.listEmployeesPage.setCompany(this.employeeProfile.getCompanyDepartment().getCompany());
		this.listEmployeesPage.setCompanyDepartment(this.employeeProfile.getCompanyDepartment());
		return this.listEmployeesPage;
	}

	public boolean isHimselfAndNotAdmin() {
		return isHimselfAndNotAdmin;
	}

	public void setHimselfAndNotAdmin(boolean isHimselfAndNotAdmin) {
		this.isHimselfAndNotAdmin = isHimselfAndNotAdmin;
	}
}
