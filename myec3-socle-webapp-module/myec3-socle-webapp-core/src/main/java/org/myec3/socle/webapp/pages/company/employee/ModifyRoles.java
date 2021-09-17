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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.StructureApplicationService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to modify roles{@link Role} of an employee{@link EmployeeProfile}
 * of a company {@link Company}<br />
 *
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/employee/ModifyRoles.
 * tml<br />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 *
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ModifyRoles extends AbstractPage {

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

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

    @Inject
    @Named("structureApplicationService")
    private StructureApplicationService structureApplicationService;


	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@SuppressWarnings("unused")
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String errorMessage;

	@InjectPage
	private View viewPage;

	@InjectPage
	private ViewRoles viewRolesPage;

	private EmployeeProfile employeeProfile;

	@Property
	private List<Role> employeeRoles;

	private Role roleLoop;

	@SuppressWarnings("unused")
	private Role roleSelected;

	private List<Role> selectedRoles;

	/**
	 * List of the applications whose role must not be managed
	 */
	private List<Application> systemApplications;

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	/**
	 * @param profileId : profile id
	 * @return : current page if logged user is allowed to display this page else
	 *         return Index page
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long profileId) {
		super.initUser();

		this.selectedRoles = new ArrayList<>();

		this.employeeProfile = this.employeeProfileService.findOne(profileId);
		if (null == this.employeeProfile) {
			return Boolean.FALSE;
		}
		// redirect to the main page if the user is not Enable
		if (!this.employeeProfile.getUser().isEnabled()) {
			return viewRolesPage;
		}

		this.systemApplications = this.applicationService.findAllDefaultApplicationsByStructureType(
				this.employeeProfile.getCompanyDepartment().getCompany().getStructureType());

		// Remove application that have the attribute rolesManageable at true in
		// order to have the possibility to manage
		// their roles
		List<Application> listApplicationsToRemove = this.applicationService
				.findAllApplicationsWithManageableRolesByStructureType(
						this.employeeProfile.getCompanyDepartment().getCompany().getStructureType());
		this.systemApplications.removeAll(listApplicationsToRemove);

		// Manage roles of the employee
		this.employeeRoles = this.employeeProfile.getRoles();
		if ((null == this.employeeRoles) || (this.employeeRoles.isEmpty())) {
			this.employeeRoles = new ArrayList<>();
			List<Application> applications = this.employeeProfile.getCompanyDepartment().getCompany().getApplications();
			for (Application application : applications) {
				Role role = this.roleService
						.findBasicRoleByProfileTypeAndApplication(this.employeeProfile.getProfileType(), application);
				if (role != null)
					this.employeeRoles.add(role);
			}
		}

		// Check if loggedUser can access to this user
		return this.hasRights(this.employeeProfile);

    }

	public Object onPassivate() {
		return (this.employeeProfile != null) ? this.employeeProfile.getId() : null;
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			// Retrieve added and removed roles
			List<Role> employeeCurrentRoles = this.employeeProfile.getRoles();
			List<Resource> rolesRemoved = new ArrayList<>();
			List<Resource> rolesAdded = new ArrayList<>();

			// Roles removed
			for (Role role : employeeCurrentRoles) {
				if (!this.selectedRoles.contains(role)) {
					rolesRemoved.add(role);
				}
			}

            // Roles added
            for (Role role : this.selectedRoles) {
                if (!employeeCurrentRoles.contains(role)) {
                    if (isTooMuchSubscription(role)) {
                        this.errorMessage = String.format(this.getMessages().get("toomuchsubscription-error-message"), role.getApplication().getLabel());
                        return null;
                    }
                    rolesAdded.add(role);
                }
            }

			this.employeeProfile.setRoles(this.selectedRoles);
			this.employeeProfileService.update(this.employeeProfile);

			// If something has changed, notify the update to external
			// applications
			if (!rolesRemoved.isEmpty() || !rolesAdded.isEmpty()) {
				this.synchronizationService.notifyCollectionUpdate(this.employeeProfile,
						SynchronizationRelationsName.ROLES, null, rolesAdded, rolesRemoved);
			}

			this.viewRolesPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
			this.viewRolesPage.setEmployeeProfile(this.employeeProfile);
			return this.viewRolesPage;
		} catch (Exception ex) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}
	}

    private boolean isTooMuchSubscription(Role role) {
		Long nbMaxLicenses = this.structureApplicationService.findByStructureAndApplication(this.employeeProfile.getCompanyDepartment().getCompany(), role.getApplication()).getNbMaxLicenses();
		if(nbMaxLicenses == null){
			return false;
		}
        List<EmployeeProfile> employeeProfiles = this.employeeProfileService.findAllEmployeeProfilesByCompanyAndApplication(this.employeeProfile.getCompanyDepartment().getCompany(), role.getApplication());
        long nbSubscription = employeeProfiles.size();
        return nbSubscription >= nbMaxLicenses;

    }

    @OnEvent(EventConstants.CANCELED)
    public Object onFormCancel() {
        this.viewPage.setEmployeeProfile(this.employeeProfile);
        return View.class;
    }

	// Getters n Setters

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

	public GenericListEncoder<Role> getRoleEncoder() {
		List<Role> availableRoles = this.roleService.findAllRoleByProfileTypeAndApplication(
				this.employeeProfile.getProfileType(), this.roleLoop.getApplication());
		return new GenericListEncoder<>(availableRoles);
	}

	public Map<Role, String> getRolesModel() {
		List<Role> availableRoles = this.roleService.findAllRoleByProfileTypeAndApplication(
				this.employeeProfile.getProfileType(), this.roleLoop.getApplication());

		Map<Role, String> roles = new LinkedHashMap<>();
		for (Role role : availableRoles) {
			roles.put(role, role.getLabel());
		}

		return roles;
	}

	public GenericListEncoder<Role> getEmployeeRolesEncoder() {
		return new GenericListEncoder<>(this.employeeRoles);
	}

	public Role getRoleLoop() {
		return roleLoop;
	}

	public void setRoleLoop(Role roleLoop) {
		this.roleLoop = roleLoop;
	}

	public Role getRoleSelected() {
		return roleLoop;
	}

	public void setRoleSelected(Role roleSelected) {
		this.selectedRoles.add(roleSelected);
	}

	/**
	 * Returns TRUE if the current application can be displayed to the user (for
	 * role management);
	 *
	 * @return
	 */
	public Boolean getDisplayedApplication() {
		if (this.systemApplications.contains(roleLoop.getApplication())) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}
