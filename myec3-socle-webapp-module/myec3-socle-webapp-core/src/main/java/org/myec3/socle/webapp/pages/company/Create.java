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
package org.myec3.socle.webapp.pages.company;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
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
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.AdministrativeState;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.AdministrativeStateValue;
import org.myec3.socle.core.domain.model.enums.CompanyINSEECat;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.PrefComMedia;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmailService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.StructureTypeService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.entities.MessageEmail;

/**
 * Page used during creation company process {@link Company}<br />
 * 
 * It's the fourth and last step to create a company. In this step your must
 * fill<br />
 * employee administrator attributes.<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 *      Corresponding tapestry template file is :
 *      src/main/resources/org/myec3/socle/webapp/pages/company/Create.tml
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 */
public class Create {

	private static Log logger = LogFactory.getLog(Create.class);

	@Inject
	private Messages messages;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Persist
	private Boolean isLocHallesTheme;

	public void setIsLocHallesTheme(Boolean isLocHallesTheme) {
		this.isLocHallesTheme = isLocHallesTheme;
	}

	public Boolean getIsLocHallesTheme() {
		return isLocHallesTheme;
	}

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("userService")
	private UserService userService;

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
	 * {@link CompanyDepartment} objects
	 */
	@Inject
	@Named("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	/**
	 * Business Service providing methods and specifics operations on Email objects
	 */
	@Inject
	@Named("emailService")
	private EmailService emailService;

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

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Named("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProfileType} objects
	 */
	@Inject
	@Named("profileTypeService")
	private ProfileTypeService profileTypeService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureType} objects
	 */
	@Inject
	@Named("structureTypeService")
	private StructureTypeService structureTypeService;

	@Persist(PersistenceConstants.FLASH)
	private Company company;

	@Component(id = "modification_form")
	private Form form;

	@Persist(PersistenceConstants.FLASH)
	private Establishment establishment;

	@Property
	private User user;

	@Property
	private String email;

	@Property
	private String phone;

	@Property
	private String cellPhone;

	@Property
	private String fax;

	@Persist(PersistenceConstants.FLASH)
	private EmployeeProfile employeeProfile;

	@Inject
	private ComponentResources componentResources;

	// Last step to create a company
	@InjectPage
	private View view;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		if (this.company != null) {
			this.user = new User();
			return Boolean.TRUE;
		} else {
			return Siren.class;
		}
	}

	@OnEvent(EventConstants.PASSIVATE)
	public void onPassivate() {
		this.company = company;
		this.establishment = establishment;
		this.employeeProfile = employeeProfile;
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "email")
	public void validateEmail(String email) {
		if (this.employeeProfileService.emailAlreadyExists(email,
				new EmployeeProfile())) {
			this.form.recordError(this.messages
					.get("recording-duplicate-error-message"));
			logger.info(this.messages.get("recording-duplicate-error-message"));
		}
		if (this.profileService.usernameAlreadyExists(email,
				new EmployeeProfile())) {
			this.form.recordError(this.messages
					.get("recording-username-duplicate-error-message"));
			logger.info(this.messages
					.get("recording-username-duplicate-error-message"));
		}
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {
			// FIXME in service
			List<Application> applications = null;
			List<Company> companyExists = new ArrayList<Company>();

			// In case of french company
			if (null == this.company.getForeignIdentifier()
					|| !this.company.getForeignIdentifier()) {
				companyExists = this.companyService.findAllByCriteria(null,
						this.company.getSiren(), null, null);
			}

			if ((companyExists.size() == 0) && (this.company.getId() == null)) {
				applications = createNewCompany();
			} else {
				applications = updateExistingCompany();
			}

			// CREATE USER
			this.user.setName(this.user.getFirstname() + " "
					+ this.user.getLastname());
			this.user.setLabel(this.user.getName());
			this.user.setEnabled(Boolean.TRUE);
			this.user.setUsername(this.email.toLowerCase());
			String password = this.userService.generatePassword();
			this.user.setPassword(this.userService
					.generateHashPassword(password));
			this.user.setExpirationDatePassword(this.userService.generateExpirationDatePassword());
			this.userService.create(this.user);

			// CREATE EMPLOYEE
			List<Establishment> myEstablishments = establishmentService.findAllEstablishmentsByCompany(this.company);
			if (this.company.getForeignIdentifier() != Boolean.TRUE) {
				for (Establishment isHeadOffice : myEstablishments) {
					if (isHeadOffice.getNic().equals(establishment.getNic())) {
						this.establishment = isHeadOffice;
						break;
					}
				}
			}
			this.employeeProfile.setEstablishment(this.establishment);
			this.employeeProfile.setNic(this.establishment.getNic());
			this.employeeProfile.setName(this.user.getFirstname() + " "
					+ this.user.getLastname());
			this.employeeProfile.setLabel(this.employeeProfile.getName());
			this.employeeProfile.setPrefComMedia(PrefComMedia.EMAIL);
			this.employeeProfile.setEmail(this.email);
			this.employeeProfile.setUser(this.user);
			this.employeeProfile.setAddress(this.establishment.getAddress());
			this.employeeProfile.setCellPhone(this.cellPhone);
			this.employeeProfile.setFax(this.fax);
			this.employeeProfile.setPhone(this.phone);
			this.employeeProfile
					.setCompanyDepartment(this.companyDepartmentService
							.findAllDepartmentByCompany(this.company).get(0));

			// ProfileType
			ProfileType profileType = this.profileTypeService
					.findByValue(ProfileTypeValue.EMPLOYEE);
			this.employeeProfile.setProfileType(profileType);

			List<Role> listRole = new ArrayList<Role>();
			for (Application application : applications) {
				logger.debug("find role for application ID : "
						+ application.getId() + " application name : "
						+ application.getName());
				Role role2;
				// If new company, employee is admin, else employee is a basic
				// user
				if (companyExists.size() == 0) {
					role2 = this.roleService
							.findAdminRoleByProfileTypeAndApplication(
									this.employeeProfile.getProfileType(),
									application);
				}
				// New establishment
				else if (this.establishment.getEmployees().size() == 0) {
					logger.info("ESTABLISHMENT EMPLOYEE : " + this.establishment.getEmployees());
					role2 = this.roleService
							.findAdminRoleByProfileTypeAndApplication(
									this.employeeProfile.getProfileType(),
									application);
				}

				else {
					role2 = this.roleService
							.findBasicRoleByProfileTypeAndApplication(
									this.employeeProfile.getProfileType(),
									application);

				}
				logger.debug("find role : " + role2);
				if (role2 != null) {
					logger.debug("ajout du role : " + role2.getName());
					listRole.add(role2);
				}
			}
			this.employeeProfile.setRoles(listRole);
			this.employeeProfileService.create(this.employeeProfile);
			this.synchronizationService.notifyCreation(this.employeeProfile);

			// we inform the employee admin in the case of the company already
			// exists
			if (companyExists.size() != 0) {
				this.sendNotificationToAdmin(companyExists.get(0));
			}

			// send login and password to the employee
			this.sendNotificationToEmployee(password);

			// Set the company to the view page
			this.view.setCompany(this.company);

			// Clean persistant attributes
			this.componentResources.discardPersistentFieldChanges();
		} catch (Exception e) {
			logger.error("error onSuccess", e);
			this.errorMessage = this.messages.get("recording-error-message");
			return null;
		}

		this.view.setIsLocHallesTheme(this.isLocHallesTheme);
		return this.view;
	}

	private List<Application> updateExistingCompany() {
		List<Application> applications;
		if (this.company.getSiretHeadOffice() != null
				&& this.company.getSiretHeadOffice().length() == 15) {
			this.company.setNic(this.company.getSiretHeadOffice().substring(9, 14));
		}
		applications = this.applicationService
				.findAllApplicationByStructure(this.company);
		this.establishment.setName(this.establishment.getLabel());

		if (this.establishment.getAddress().getCanton() == null) {
			this.establishment.getAddress().setCanton("Aucun");
		}

		if (this.establishment.getId() == null) {

			// we cannot send synchro without getting establishment id from db
			List<Establishment> myEstablishments = company.getEstablishments();

			Establishment establishmentToSynchro = null;
			for (Establishment findEstablishmentToSynchro : myEstablishments) {
				if (findEstablishmentToSynchro.getForeignIdentifier() == Boolean.FALSE) {
					if (findEstablishmentToSynchro.getNic().equals(
							this.establishment.getNic())) {
						logger.debug("findEstablishmentToSynchro : " + findEstablishmentToSynchro.toString());
						establishmentToSynchro = findEstablishmentToSynchro;
						logger.debug("Found establishment to synchronize ! "
								+ establishmentToSynchro.toString());
					}
				} else {
					if (findEstablishmentToSynchro.getNationalID() != null) {
						establishmentToSynchro = this.establishmentService
								.findLastForeignCreated(findEstablishmentToSynchro
										.getNationalID());
						if (establishmentToSynchro.getId() != null) {
							logger.debug("Found foreign establishment to synchronize ! "
									+ establishmentToSynchro.toString());
						}
						break;
					}
				}
			}

			this.company.getEstablishments().add(this.establishment);
			this.company = this.companyService.update(this.company);
			// Send notification to external applications
			if (establishmentToSynchro != null) {
				this.synchronizationService.notifyCreation(establishmentToSynchro);
			} else {
				logger.info("Synchronization Error : couldn't find establishment to synchronize in database !");
			}
		}
		return applications;
	}

	private List<Application> createNewCompany() {
		List<Application> applications;
		this.company.setExternalId(new Long(0));
		StructureType structureType = this.structureTypeService
				.findByValue(StructureTypeValue.COMPANY);
		logger.debug("structureType "
				+ structureType.getValue().toString());
		applications = this.applicationService
				.findAllDefaultApplicationsByStructureType(structureType);
		for (Application application : applications) {
			this.company.addApplication(application);
			logger.debug("add application by default "
					+ application.getName());

		}
		// FIXME not in coface ws
		if (this.company.getRCS() == null) {
			this.company.setRCS(Boolean.FALSE);
		}
		if (this.company.getRM() == null) {
			this.company.setRM(Boolean.FALSE);
		}
		if (this.company.getLegalCategory() == null) {
			this.company.setLegalCategory(CompanyINSEECat.AUTRE);
		}
		if (this.company.getForeignIdentifier() == Boolean.TRUE) {
			this.company.setRegistrationCountry(this.company.getAddress().getCountry());
		} else {
			this.company.setRegistrationCountry(Country.FR);
		}

		this.company.setName(this.company.getLabel());
		this.company.setAcronym(MyEc3Constants.ACRONYM_ENT);
		this.company.setAddress(this.establishment.getAddress());
		this.company.setInsee("");

		this.establishment.setName(this.establishment.getLabel());
		logger.debug("ESTABLISHMENT :" + this.establishment);

		if (this.company.getSiretHeadOffice() != null) {
			this.company.setNic(this.company.getSiretHeadOffice().substring(9, 14));
		}
		logger.debug("COMPANY : " + this.company);
		if (this.company.getForeignIdentifier() == Boolean.TRUE &&
				this.company.getEstablishments().size() == 0) {
			establishment.setIsHeadOffice(Boolean.TRUE);
			establishment.setDiffusableInformations(Boolean.FALSE);
			establishment.setApeCode(this.company.getApeCode());
			establishment.setApeNafLabel(this.company.getApeNafLabel());
			establishment.setForeignIdentifier(Boolean.TRUE);
			establishment.setNationalID(this.company.getNationalID());
			Address establishmentAddress = establishment.getAddress();
			establishmentAddress.setCountry(this.company.getRegistrationCountry());
			establishment.setAddress(establishmentAddress);

			AdministrativeState adminState = new AdministrativeState();

			Date date = new Date();
			adminState.setAdminStateLastUpdated(date);
			adminState.setAdminStateValue(AdministrativeStateValue.STATUS_ACTIVE);

			this.company.setAdministrativeState(adminState);
			this.company.setDiffusableInformations(Boolean.FALSE);
			this.establishment.setAdministrativeState(adminState);
		}

		if (this.establishment.getAddress().getCanton() == null) {
			this.establishment.getAddress().setCanton("Aucun");
		}

		if (this.establishment.getCompany() == null) {
			this.establishment.setCompany(company);
		}

		this.company.addEstablishment(establishment);

		if (this.company.getApeNafLabel() == null) {
			this.company.setApeNafLabel(this.establishment.getApeNafLabel());
		}

		if (this.company.getLastUpdate() == null) {
			Date date = new Date();
			this.company.setLastUpdate(date);
		}

		if (this.company.getAdministrativeState() == null
				|| (this.company.getAdministrativeState()
						.getAdminStateValue() == null
						&& this.company
								.getAdministrativeState()
								.getAdminStateLastUpdated() == null)) {

			AdministrativeState adminState = new AdministrativeState();
			adminState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);
			this.company.setAdministrativeState(adminState);
		}

		if (this.establishment.getAddress().getInsee() != null
				&& this.establishment.getIsHeadOffice().booleanValue()
				&& this.establishment.getForeignIdentifier() == Boolean.FALSE) {

			this.company.setInsee(this.establishment.getAddress().getInsee());
		}
		this.companyService.create(this.company);
		this.synchronizationService.notifyCreation(this.company);
		this.synchronizationService.notifyCreation(this.establishment);
		return applications;
	}

	/**
	 * Send notification to admin when someone wants to create a company which
	 * already exists
	 * 
	 * @throws MessagingException
	 */
	private void sendNotificationToAdmin(Company company)
			throws MessagingException {
		if (null != company) {
			StringBuffer message = new StringBuffer();
			message.append(this.messages.get("login-message"));
			message.append(this.email);
			message.append("\n\n");
			message.append(this.messages.get("courtesy-message"));

			MessageEmail messageEmail = new MessageEmail(
					MessageEmail.EmailContext.EMPLOYEE_COMPANY_ALREADY_EXISTS,
					this.email, this.user, this.company.getLabel(), this.establishment);

			// send mail to gu admin if enabled
			List<EmployeeProfile> adminGuEmployees = this.employeeProfileService
					.findAllGuAdministratorEnabledByCompanyId(company.getId());

			if (adminGuEmployees.size() > 0) {
				String[] recipients = new String[adminGuEmployees.size()];
				int i = 0;
				for (EmployeeProfile employeeProfile : adminGuEmployees) {
					// Check if admin is enabled
					recipients[i] = employeeProfile.getEmail();
					i++;
				}

				this.emailService.silentSendMail(MyEc3EmailConstants.getSender(),
						MyEc3EmailConstants.getFrom(), recipients,
						this.messages.get("subject-newuser-company-message"),
						messageEmail.generateContent(this.messages, null));
			}
		}
	}

	/**
	 * Send notification to new employee created with login and password
	 * 
	 * @throws MessagingException
	 */
	public void sendNotificationToEmployee(String password)
			throws MessagingException {
		StringBuffer message = new StringBuffer();
		message.append(this.messages.get("login-message"));
		message.append(this.email);
		message.append("\n");
		message.append(this.messages.get("password-message"));
		message.append(password);
		message.append("\n\n");
		message.append(this.messages.get("courtesy-message"));

		MessageEmail messageEmail = new MessageEmail(ProfileTypeValue.AGENT,
				MessageEmail.EmailContext.EMPLOYEE_COMPANY_CREATE, this.email,
				password, this.company.getLabel());
		String[] recipients = new String[1];
		recipients[0] = this.email;

		// Send email to new employee
		this.emailService.silentSendMail(MyEc3EmailConstants.getSender(),
				MyEc3EmailConstants.getFrom(), recipients,
				this.messages.get("subject-message"),
				messageEmail.generateContent(this.messages, null));
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Establishment getEstablishment() {
		return establishment;
	}

	public void setEstablishment(Establishment establishment) {
		this.establishment = establishment;
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
		return employeeProfile;
	}
}
