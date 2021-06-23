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
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.Civility;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.components.EmployeeForm;
import org.myec3.socle.webapp.entities.MessageEmail;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Page used to create an employee {@link EmployeeProfile}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/employee/Create.tml<br
 * />
 *
 * 
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Create extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationNotificationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Inject
	@Named("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	/**
	 * Business Service providing methods and specifics operations on Email objects
	 */
	@Inject
	@Named("emailService")
	private EmailService emailService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link CompanyDepartment} objects
	 */
	@Inject
	@Named("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	@Inject
	@Named("establishmentService")
	private EstablishmentService establishmentService;

	@Inject
	@Named("companyService")
	private CompanyService companyService;

	@SuppressWarnings("unused")
	@Component(id = "employee_form")
	private EmployeeForm form;

	@InjectPage
	private ModifyRoles modifyRolesPage;

	// Template properties
	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Property
	private EmployeeProfile employeeProfile;

	@SuppressWarnings("unused")
	@InjectPage
	@Property
	private ListEmployees listEmployeesPage;

	// Page Activation n Passivation
	@OnEvent(EventConstants.ACTIVATE)
	public void activation() {
		super.initUser();
	}

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id, Long idDepartment) {
		if (this.employeeProfile == null) {
			this.employeeProfile = new EmployeeProfile();
		}
		if (null == this.employeeProfile.getUser()) {
			this.employeeProfile.setUser(new User());
			this.employeeProfile.getUser().setCivility(Civility.MR);
		}

		CompanyDepartment companyDepartment = this.companyDepartmentService.findOne(idDepartment);
		if (companyDepartment != null && companyDepartment.getId() != null
				&& companyDepartment.getCompany().getId().equals(id)) {
			this.employeeProfile.setCompanyDepartment(this.companyDepartmentService.findOne(idDepartment));
		} else {
			this.employeeProfile.setEstablishment(this.establishmentService.findOne(idDepartment));
			this.employeeProfile.setCompanyDepartment(this.companyDepartmentService
					.findRootCompanyDepartmentByCompany(this.employeeProfile.getEstablishment().getCompany()));
			this.employeeProfile.setNic(this.employeeProfile.getEstablishment().getCompany().getNic());
		}

		if (null == this.employeeProfile.getAddress()) {
			this.employeeProfile.setAddress(new Address());
			this.employeeProfile.getAddress().setCountry(Country.FR);

			// if establishment has address, initialize employee's address with it (simplify
			// creation)
			if (this.employeeProfile.getEstablishment() != null) {
				Establishment employeeEstablishment = this.employeeProfile.getEstablishment();
				if (employeeEstablishment.getAddress() != null) {
					this.employeeProfile.getAddress()
							.setPostalAddress(employeeEstablishment.getAddress().getPostalAddress());
					this.employeeProfile.getAddress().setPostalCode(employeeEstablishment.getAddress().getPostalCode());
					this.employeeProfile.getAddress().setCity(employeeEstablishment.getAddress().getCity());
					this.employeeProfile.getAddress().setCanton(employeeEstablishment.getAddress().getCanton());
				}
			}
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.employeeProfile);
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {
			// CREATE USER
			this.employeeProfile.getUser().setName(
					this.employeeProfile.getUser().getLastname() + " " + this.employeeProfile.getUser().getFirstname());
			this.employeeProfile.getUser().setLabel(this.employeeProfile.getUser().getName());
			this.employeeProfile.getUser().setUsername(this.employeeProfile.getEmail());

			// password for mail
			String password = this.userService.generatePassword();
			this.employeeProfile.getUser().setExpirationDatePassword(this.userService.generateExpirationDatePassword());
			this.employeeProfile.getUser().setPassword(this.userService.generateHashPassword(password));
			userService.create(this.employeeProfile.getUser());
			User user = userService.findByName(this.employeeProfile.getUser().getName());
			this.employeeProfile.setUser(user);

			// EMPLOYEE
			this.employeeProfile.setName(
					this.employeeProfile.getUser().getLastname() + " " + this.employeeProfile.getUser().getFirstname());
			this.employeeProfile.setLabel(this.employeeProfile.getName());
			this.employeeProfile.setNic(this.employeeProfile.getEstablishment().getCompany().getNic());

			// create the employee
			this.employeeProfileService.create(this.employeeProfile);

			// Send email notification to the employee
			sendNotificationToEmployee(password);

			// Notify external applications
			this.synchronizationNotificationService.notifyCreation(this.employeeProfile);

		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}

		// Prepare next page
		this.modifyRolesPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.modifyRolesPage.setEmployeeProfile(this.employeeProfile);

		return this.modifyRolesPage;
	}

	public Object onPassivate() {
		List<Long> result = new ArrayList<>();
		if (this.employeeProfile.getEstablishment() != null) {
			result.add(this.employeeProfile.getEstablishment().getCompany().getId());
			result.add(this.employeeProfile.getEstablishment().getId());
		} else {
			result.add(this.employeeProfile.getCompanyDepartment().getCompany().getId());
			result.add(this.employeeProfile.getCompanyDepartment().getId());
		}
		return result;
	}

	/**
	 * Send email to the employee
	 * 
	 * @param password : the employee's password
	 */
	public void sendNotificationToEmployee(String password) {
		// message mail
		StringBuilder message = new StringBuilder();
		message.append(this.getMessages().get("login-message"));
		message.append(this.employeeProfile.getEmail());
		message.append("\n");
		message.append(this.getMessages().get("password-message"));
		message.append(password);
		message.append("\n\n");
		message.append(this.getMessages().get("courtesy-message"));

		MessageEmail messageEmail = new MessageEmail(ProfileTypeValue.EMPLOYEE,
				MessageEmail.EmailContext.EMPLOYEE_COMPANY_ADD, this.employeeProfile.getEmail(), password,
				this.employeeProfile.getCompanyDepartment().getCompany().getLabel(),
				this.employeeProfile.getEstablishment());

		String[] recipients = new String[1];
		recipients[0] = this.employeeProfile.getEmail();

		this.emailService.silentSendMail(MyEc3EmailConstants.getSender(), MyEc3EmailConstants.getFrom(), recipients,
				this.getMessages().get("subject-message"), messageEmail.generateContent(this.getMessages(), null));
	}
}
