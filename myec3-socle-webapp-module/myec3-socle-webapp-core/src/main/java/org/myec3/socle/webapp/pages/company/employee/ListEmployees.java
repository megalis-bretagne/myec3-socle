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
import java.util.List;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to list all employees{@link EmployeeProfile} of a company
 * {@link Company}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/employee/
 * ListEmployees.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ListEmployees extends AbstractPage {

	private static final Log logger = LogFactory.getLog(ListEmployees.class);

	// Services
	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link CompanyDepartment} objects
	 */
	@Inject
	@Named("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Inject
	@Named("companyService")
	private CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Inject
	@Named("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	// Template attributes
	private CompanyDepartment companyDepartment;

	private Company company;

	private EmployeeProfile employeeProfile;

	@SuppressWarnings("unused")
	@Property
	private List<EmployeeProfile> employeeProfilesResult;

	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@Property
	private EmployeeProfile employeeProfileRow;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	@Inject
	private BeanModelSource beanModelSource;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation(Long id) {
		super.initUser();

		// REMOVED : USELESS AND BUGGY AFTER MERGE SIPPEREC (multiple ressources with
		// same id)
		// this.companyDepartment = this.companyDepartmentService.findOne(id);
		// if (null == this.companyDepartment) {
		// its organism
		this.company = this.companyService.findOne(id);
		this.companyDepartment = this.companyDepartmentService.findRootCompanyDepartmentByCompany(this.company);
		if (null == this.company) {
			return Boolean.FALSE;
		}
		// }

		// Check if loggedUser can access to this company
		return this.hasRights(this.companyDepartment.getCompany());
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		if (this.company != null) {
			return this.company.getId();
		} else {
			if (this.companyDepartment != null) {
				return this.companyDepartment.getId();
			}
		}
		return null;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		return Index.class;
	}

	// Events
	@OnEvent(value = "action", component = "delete")
	public Object removeEmployeeProfile(Long id) {
		try {
			EmployeeProfile employeeToDelete = this.employeeProfileService.findOne(id);
			this.setCompanyDepartment(employeeToDelete.getCompanyDepartment());
			this.setCompany(employeeToDelete.getCompanyDepartment().getCompany());
			this.employeeProfileService.softDelete(id);
			this.synchronizationService.notifyDeletion(employeeToDelete);
			this.successMessage = this.getMessages().get("delete-success");
		} catch (Exception e) {
			logger.error("An error has occured during the deletion of the employee", e);
		}
		return this;
	}

	@OnEvent(value = "action", component = "disable")
	public Object disableAgentProfile(Long id) {
		EmployeeProfile employeeToDisable = this.employeeProfileService.findOne(id);
		employeeToDisable.getUser().setEnabled(false);

		// Used for e-bourgogne GRC
		employeeToDisable.getUser().setLastname(employeeToDisable.getUser().getLastname() + " (désactivé)");

		this.employeeProfileService.update(employeeToDisable);
		this.synchronizationService.notifyUpdate(employeeToDisable);
		this.successMessage = this.getMessages().get("disable-success");
		return this;

	}

	@OnEvent(value = "action", component = "enable")
	public Object enableAgentProfile(Long id) {
		EmployeeProfile employeeToEnable = this.employeeProfileService.findOne(id);
		employeeToEnable.getUser().setEnabled(true);

		// Used for e-bourgogne GRC
		employeeToEnable.getUser().setLastname(employeeToEnable.getUser().getLastname().replace(" (désactivé)", ""));

		this.employeeProfileService.update(employeeToEnable);
		this.synchronizationService.notifyUpdate(employeeToEnable);
		this.successMessage = this.getMessages().get("enable-success");
		return this;
	}

	// Getters n Setters
	public List<EmployeeProfile> getEmployeeProfileList() {
		if (this.company != null) {
			List<CompanyDepartment> companyDepartments = this.companyDepartmentService
					.findAllDepartmentByCompany(this.company);
			List<EmployeeProfile> employeeProfiles = new ArrayList<EmployeeProfile>();
			for (CompanyDepartment companyDepartment : companyDepartments) {
				employeeProfiles.addAll(
						this.employeeProfileService.findAllEmployeeProfilesByCompanyDepartment(companyDepartment));
			}
			return employeeProfiles;
		}
		return this.employeeProfileService.findAllEmployeeProfilesByCompanyDepartment(this.companyDepartment);
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<EmployeeProfile> getGridModel() {
		BeanModel<EmployeeProfile> model = this.beanModelSource.createDisplayModel(EmployeeProfile.class,
				this.getMessages());
		model.add("actions", null);

		PropertyConduit propCdtAttributeUser = this.propertyConduitSource.create(EmployeeProfile.class, "user");
		PropertyConduit propCdtAttributeExpirationDatePassword = this.propertyConduitSource
				.create(EmployeeProfile.class, "user.expirationDatePassword");

		model.add("user", propCdtAttributeUser);
		model.add("expirationDatePassword", propCdtAttributeExpirationDatePassword).sortable(true);
		model.include("user", "email", "expirationDatePassword", "actions");
		return model;
	}

	public CompanyDepartment getCompanyDepartment() {
		return companyDepartment;
	}

	public void setCompanyDepartment(CompanyDepartment companyDepartment) {
		this.companyDepartment = companyDepartment;
	}

	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Integer getResultsNumber() {
		if (null == this.getEmployeeProfileList())
			return 0;
		return this.getEmployeeProfileList().size();
	}

	/**
	 * Check if the EmployeeProfileRow is the current LoggedUser
	 * 
	 * @return TRUE if the EmployeeProfileRow is the currentLoggedUser, else FALSE
	 */
	public Boolean getIsEmployeeRowLogged() {
		if (this.getLoggedProfileExists()) {
			if ((null != this.getLoggedProfile()) && (null != this.getLoggedProfile().getId())) {
				if (this.getLoggedProfile().equals(this.employeeProfileRow)) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	public Boolean getBelongsToEstablishment() {
		if (this.getLoggedProfileExists()) {
			if ((null != this.getLoggedProfile()) && (null != this.getLoggedProfile().getId())) {
				EmployeeProfile myUser = this.employeeProfileService.findOne(getLoggedProfile().getId());
				if (myUser != null) {
					if (this.employeeProfileRow.getEstablishment().equals(myUser.getEstablishment())) {
						return Boolean.TRUE;
					}
				} else if (this.getIsAdmin()) {
					return Boolean.TRUE;
				}
			}
		}

		return Boolean.FALSE;
	}

}
