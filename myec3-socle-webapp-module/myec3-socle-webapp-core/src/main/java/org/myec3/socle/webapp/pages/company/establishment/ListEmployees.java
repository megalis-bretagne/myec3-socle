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
package org.myec3.socle.webapp.pages.company.establishment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.beanmodel.PropertyConduit;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.beanmodel.services.PropertyConduitSource;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Page used to list all employees {@link EmployeeProfile} of a establishment
 * {@link Establishment}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/establishment/
 * ListEmployees.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
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
	@Named("establishmentService")
	private EstablishmentService establishmentService;

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
	@Property
	private Company company;

	@Property
	private Establishment establishment;

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
		this.establishment = this.establishmentService.findOne(id);
		if (this.establishment == null) {
			return Boolean.FALSE;
		}

		// Check if loggedUser can access to this company
		return this.hasRights(this.establishment.getCompany());
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.establishment != null) ? this.establishment.getId() : null;
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
			this.establishment = employeeToDelete.getEstablishment();
			this.company = employeeToDelete.getEstablishment().getCompany();

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
			List<Establishment> establishments = this.establishmentService.findAllEstablishmentsByCompany(this.company);
			List<EmployeeProfile> employeeProfiles = new ArrayList<EmployeeProfile>();
			for (Establishment establishment : establishments) {
				employeeProfiles
						.addAll(this.employeeProfileService.findAllEmployeeProfilesByEstablishment(establishment));
			}
			return employeeProfiles;
		}
		return this.employeeProfileService.findAllEmployeeProfilesByEstablishment(this.establishment);
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<EmployeeProfile> getGridModel() {
		BeanModel<EmployeeProfile> model = this.beanModelSource.createDisplayModel(EmployeeProfile.class,
				this.getMessages());

		PropertyConduit propCdtAttributeUser = this.propertyConduitSource.create(EmployeeProfile.class, "user");
		PropertyConduit propCdtAttributeExpirationDatePassword = this.propertyConduitSource.create(EmployeeProfile.class, "user.expirationDatePassword");

		model.add("user", propCdtAttributeUser);
		model.add("expirationDatePassword", propCdtAttributeExpirationDatePassword).sortable(true);
		model.add("actions", null);
		model.include("user", "email", "username","expirationDatePassword", "actions");
		return model;
	}

	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
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

	public Boolean getModifyDisplay() {
		if (this.hasRights(establishment)) {
			logger.info("OK");
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
		if (employeeProfileRow.getUser() == null) {
			return Boolean.FALSE;
		}
		return employeeProfileRow.getUser().isEnabled();
	}
}
