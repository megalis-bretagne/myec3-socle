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
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.List;

/**
 * Page used to display employee's{@link EmployeeProfile}
 * roles{@link Role}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/employee/ViewRoles.
 * tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ViewRoles extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Inject
	@Named("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@SuppressWarnings("unused")
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String errorMessage;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Property
	@SuppressWarnings("unused")
	private Application applicationLoop;

	@Property
	@SuppressWarnings("unused")
	private List<Role> roles;

	@Property
	private Role roleLoop;

	private List<Application> systemApplications;

	private EmployeeProfile employeeProfile;

	// Page Activation n Passivation
	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	/**
	 * @param id : profile id
	 * @return : current page if logged user is allowed to display this page else
	 *         return Index page
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long profileId) {
		super.initUser();

		this.employeeProfile = this.employeeProfileService.findOne(profileId);
		if (null == this.employeeProfile) {
			return Boolean.FALSE;
		}
		roles = this.employeeProfile.getRoles();

		this.systemApplications = this.applicationService.findAllDefaultApplicationsByStructureType(
				this.employeeProfile.getCompanyDepartment().getCompany().getStructureType());

		// Remove application that have the attribute rolesManageable at true in
		// order to have the possibility to manage
		// their roles
		List<Application> listApplicationsToRemove = this.applicationService
				.findAllApplicationsWithManageableRolesByStructureType(
						this.employeeProfile.getCompanyDepartment().getCompany().getStructureType());
		this.systemApplications.removeAll(listApplicationsToRemove);

		// Check if loggedUser can access to this user
		return this.hasRights(this.employeeProfile);
	}

	public Object onPassivate() {
		return (this.employeeProfile != null) ? this.employeeProfile.getId() : null;
	}

	// Getters n Setter
	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public Boolean getDisplayedApplication() {
		if (this.systemApplications.contains(roleLoop.getApplication())) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
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
