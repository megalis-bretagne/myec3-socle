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
package org.myec3.socle.webapp.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.AcronymsList;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;
import org.myec3.socle.core.domain.model.meta.StructureType;
import org.myec3.socle.core.domain.model.meta.StructureTypeApplication;
import org.myec3.socle.core.service.AcronymsListService;
import org.myec3.socle.core.service.AdminProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.ProfileTypeRoleService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.core.service.StructureTypeApplicationService;
import org.myec3.socle.core.service.StructureTypeService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.core.domain.model.SynchronizationFilter;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchronizationFilterService;
import org.myec3.socle.synchro.core.service.SynchronizationSubscriptionService;

/**
 * Page used to init database.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/Init.tml
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public class Init extends AbstractPage {

	// Application constants
	private static final String DEFAULT_APPLICATION_NAME = "GU";
	private static final String DEFAULT_APPLICATION_LABEL = "Administration de mon entité";
	private static final String DEFAULT_APPLICATION_URL = "http://url";

	private static final String APPLICATION_SITE_INTERNET_NAME = "Application - Portalgen";
	private static final String APPLICATION_SITE_INTERNET_LABEL = "Site internet";

	private static final String APPLICATION_MSPL_NAME = "Application - SPL";
	private static final String APPLICATION_MSPL_LABEL = "Mon Service Public en Ligne";

	// Role constants
	private static final String ROLE_DEFAULT_NAME = "ROLE_DEFAULT";
	private static final String ROLE_ADMIN_AGENT_NAME = "ROLE_MANAGER_AGENT";
	private static final String ROLE_ADMIN_EMPLOYEE_NAME = "ROLE_MANAGER_EMPLOYEE";
	private static final String ROLE_ADMIN_NAME = "ROLE_ADMIN";
	private static final String ROLE_SUPER_ADMIN_NAME = "ROLE_SUPER_ADMIN";
	private static final String ROLE_DEFAULT_LABEL = "Utilisateur du service";
	private static final String ROLE_ADMIN_LABEL = "Administrateur";

	// Role site internet
	private static final String ROLE_SUPER_ADMIN_SITE_INTERNET_NAME = "ROLE_SUPER_ADMIN_PORTALGEN";
	private static final String ROLE_CUSTOMER_SITE_INTERNET_NAME = "ROLE_CUSTOMER_PORTALGEN";
	private static final String ROLE_WRITER_SITE_INTERNET_NAME = "ROLE_WRITER_PORTALGEN";
	private static final String ROLE_CONTRIBUTOR_SITE_INTERNET_NAME = "ROLE_CONTRIBUTOR_PORTALGEN";
	private static final String ROLE_ADMIN_SITE_INTERNET_NAME = "ROLE_ADMIN_PORTALGEN";

	// Role MSPL
	private static final String ROLE_SUPER_ADMIN_MSPL_NAME = "ROLE_SPL_SUPER_ADMIN";
	private static final String ROLE_CUSTOMER_MSPL_NAME = "ROLE_SPL_ADMIN_CUSTOMER";
	private static final String ROLE_ADMIN_MSPL_NAME = "ROLE_SPL_ADMIN";
	private static final String ROLE_AGENT_MSPL_NAME = "ROLE_SPL_AGENT";

	// Super Admin constants
	private static final String SUPER_ADMIN_NAME = "Administrateur Technique";
	private static final String SUPER_ADMIN_EMAIL_USERNAME = "super.admin@e-bourgogne.fr";
	private static final String SUPER_ADMIN_DEFAULT_PASSWORD = "superadmin";

	// Admin constants
	private static final String ADMIN_NAME = "Administrateur Fonctionnel";
	private static final String ADMIN_EMAIL_USERNAME = "admin@e-bourgogne.fr";
	private static final String ADMIN_DEFAULT_PASSWORD = "admin";

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AdminProfile} objects
	 */
	@Inject
	@Named("adminProfileService")
	private AdminProfileService adminProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AcronymsList} objects
	 */
	@Inject
	@Named("acronymsListService")
	private AcronymsListService acronymsListService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureType} objects
	 */
	@Inject
	@Named("structureTypeService")
	private StructureTypeService structureTypeService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProfileType} objects
	 */
	@Inject
	@Named("profileTypeService")
	private ProfileTypeService profileTypeService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link StructureTypeApplication} objects
	 */
	@Inject
	@Named("structureTypeApplicationService")
	private StructureTypeApplicationService structureTypeApplicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProfileTypeRole} objects
	 */
	@Inject
	@Named("profileTypeRoleService")
	private ProfileTypeRoleService profileTypeRoleService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on {@link User}
	 * objects
	 */
	@Inject
	@Named("userService")
	private UserService userService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link SynchronizationFilter} objects
	 */
	@Inject
	@Named("synchronizationFilterService")
	private SynchronizationFilterService synchronizationFilterService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link SynchronizationSubscription} objects
	 */
	@Inject
	@Named("synchronizationSubscriptionService")
	private SynchronizationSubscriptionService synchronizationSubscriptionService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Customer} objects
	 */
	@Inject
	@Named("customerService")
	private CustomerService customerService;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		if (this.getLoggedProfileExists()) {
			return Index.class;
		}
		return Boolean.TRUE;
	}

	@OnEvent(component = "initDatabase", value = EventConstants.ACTION)
	public Object onInitDatabase() {

		// Init AcronymsList
		this.initAcronymsList();

		// Init Default application
		this.initDefaultApplication();

		// Init default Profile types
		this.initDefaultProfileTypes();

		// Init default roles
		this.initDefaultRoles();

		// Init default customer
		this.initDefaultCustomerAndAdmins();

		// Init profileTypeRole for applications
		this.initDefaultProfileTypeRoleByApplication();

		// Init default structure types
		this.initDefaultStructureTypes();

		// Init default structureTypeApplication
		this.initDefaultStructureTypeApplication();

		// Init synchronization filters
		this.initDefaultSynchronizationFiltersAndSubscriptions();

		this.successMessage = "Initialisation de la base de données terminée";
		return null;
	}

	public void initAcronymsList() {
		if (this.acronymsListService.findAll().isEmpty()) {
			char first = 'a';
			char second = '0';
			char third = 'a';

			while (true) {
				AcronymsList acronym = new AcronymsList();

				acronym.setValue(first + "" + second + "" + third);
				acronym.setAvailable(Boolean.TRUE);
				this.acronymsListService.create(acronym);

				if (++third > 'z') {
					third = 'a';
					if (++second > '9') {
						second = '0';
						if (++first > 'z') {
							break;
						}
					}
				}

			}
		}
	}

	public void initDefaultApplication() {
		// Check that application table is empty
		if (this.applicationService.findAll().isEmpty()) {
			Application application = new Application();
			application.setName(DEFAULT_APPLICATION_NAME);
			application.setLabel(DEFAULT_APPLICATION_LABEL);
			application.setUrl(this.getMessageSource().getMessage("access.url.socle", null, Locale.FRENCH));
			this.applicationService.create(application);

			// Add Site internet application
			Application aSiteInternet = new Application();
			aSiteInternet.setName(APPLICATION_SITE_INTERNET_NAME);
			aSiteInternet.setLabel(APPLICATION_SITE_INTERNET_LABEL);
			aSiteInternet.setUrl(this.getMessageSource().getMessage("access.url.portalgen", null, Locale.FRENCH));
			this.applicationService.create(aSiteInternet);

			// Add MSPL application
			Application aMSPL = new Application();
			aMSPL.setName(APPLICATION_MSPL_NAME);
			aMSPL.setLabel(APPLICATION_MSPL_LABEL);
			aMSPL.setUrl(this.getMessageSource().getMessage("access.url.mspl", null, Locale.FRENCH));
			this.applicationService.create(aMSPL);
		}

	}

	public void initDefaultProfileTypes() {
		if (this.profileTypeService.findAll().isEmpty()) {
			ProfileType agentType = new ProfileType();
			agentType.setValue(ProfileTypeValue.AGENT);
			this.profileTypeService.create(agentType);

			ProfileType employeeType = new ProfileType();
			employeeType.setValue(ProfileTypeValue.EMPLOYEE);
			this.profileTypeService.create(employeeType);

			ProfileType adminType = new ProfileType();
			adminType.setValue(ProfileTypeValue.ADMIN);
			this.profileTypeService.create(adminType);
		}
	}

	public void initDefaultRoles() {
		if (this.roleService.findAll().isEmpty()) {
			// Retrieve default application
			Application defaultApplication = this.applicationService.findByName(DEFAULT_APPLICATION_NAME);

			// ROLES FOR DEFAULT APPLICATION
			Role roleDefault = new Role();
			roleDefault.setName(ROLE_DEFAULT_NAME);
			roleDefault.setLabel(ROLE_DEFAULT_LABEL);
			roleDefault.setApplication(defaultApplication);
			this.roleService.create(roleDefault);

			Role roleAdminAgent = new Role();
			roleAdminAgent.setName(ROLE_ADMIN_AGENT_NAME);
			roleAdminAgent.setLabel(ROLE_ADMIN_LABEL);
			roleAdminAgent.setApplication(defaultApplication);
			this.roleService.create(roleAdminAgent);

			Role roleAdminEmployee = new Role();
			roleAdminEmployee.setName(ROLE_ADMIN_EMPLOYEE_NAME);
			roleAdminEmployee.setLabel(ROLE_ADMIN_LABEL);
			roleAdminEmployee.setApplication(defaultApplication);
			this.roleService.create(roleAdminEmployee);

			Role roleAdmin = new Role();
			roleAdmin.setName(ROLE_ADMIN_NAME);
			roleAdmin.setLabel(ROLE_ADMIN_LABEL);
			roleAdmin.setApplication(defaultApplication);
			this.roleService.create(roleAdmin);

			Role roleSuperAdmin = new Role();
			roleSuperAdmin.setName(ROLE_SUPER_ADMIN_NAME);
			roleSuperAdmin.setLabel("Super administrateur");
			roleSuperAdmin.setApplication(defaultApplication);
			this.roleService.create(roleSuperAdmin);

			// Add Site internet ROLES
			Application aSiteInternet = this.applicationService.findByName(APPLICATION_SITE_INTERNET_NAME);

			Role roleSuperAdminPortalgen = new Role();
			roleSuperAdminPortalgen.setName(ROLE_SUPER_ADMIN_SITE_INTERNET_NAME);
			roleSuperAdminPortalgen.setLabel("Super administrateur");
			roleSuperAdminPortalgen.setApplication(aSiteInternet);
			this.roleService.create(roleSuperAdminPortalgen);

			Role roleCustomerPortalgen = new Role();
			roleCustomerPortalgen.setName(ROLE_CUSTOMER_SITE_INTERNET_NAME);
			roleCustomerPortalgen.setLabel("Client");
			roleCustomerPortalgen.setApplication(aSiteInternet);
			this.roleService.create(roleCustomerPortalgen);

			Role roleWriterPortalgen = new Role();
			roleWriterPortalgen.setName(ROLE_WRITER_SITE_INTERNET_NAME);
			roleWriterPortalgen.setLabel("Rédacteur");
			roleWriterPortalgen.setApplication(aSiteInternet);
			this.roleService.create(roleWriterPortalgen);

			Role roleContributorPortalgen = new Role();
			roleContributorPortalgen.setName(ROLE_CONTRIBUTOR_SITE_INTERNET_NAME);
			roleContributorPortalgen.setLabel("Contributeur");
			roleContributorPortalgen.setApplication(aSiteInternet);
			this.roleService.create(roleContributorPortalgen);

			Role roleAdminPortalgen = new Role();
			roleAdminPortalgen.setName(ROLE_ADMIN_SITE_INTERNET_NAME);
			roleAdminPortalgen.setLabel("Administrateur");
			roleAdminPortalgen.setApplication(aSiteInternet);
			this.roleService.create(roleAdminPortalgen);

			// Add MSPL Roles
			Application aMSPL = this.applicationService.findByName(APPLICATION_MSPL_NAME);

			Role roleSuperAdminMSPL = new Role();
			roleSuperAdminMSPL.setName(ROLE_SUPER_ADMIN_MSPL_NAME);
			roleSuperAdminMSPL.setLabel("Super administrateur");
			roleSuperAdminMSPL.setApplication(aMSPL);
			this.roleService.create(roleSuperAdminMSPL);

			Role roleAdminCustomerMSPL = new Role();
			roleAdminCustomerMSPL.setName(ROLE_CUSTOMER_MSPL_NAME);
			roleAdminCustomerMSPL.setLabel("Client");
			roleAdminCustomerMSPL.setApplication(aMSPL);
			this.roleService.create(roleAdminCustomerMSPL);

			Role roleAdminMSPL = new Role();
			roleAdminMSPL.setName(ROLE_ADMIN_MSPL_NAME);
			roleAdminMSPL.setLabel("Administrateur SPL");
			roleAdminMSPL.setApplication(aMSPL);
			this.roleService.create(roleAdminMSPL);

			Role roleAgentMSPL = new Role();
			roleAgentMSPL.setName(ROLE_AGENT_MSPL_NAME);
			roleAgentMSPL.setLabel("Agent SPL");
			roleAgentMSPL.setApplication(aMSPL);
			this.roleService.create(roleAgentMSPL);
		}
	}

	public void initDefaultCustomerAndAdmins() {
		Customer defaultCustomer = new Customer();
		Address address = new Address();

		if (this.customerService.findAll().isEmpty()) {
			// Create default customer
			defaultCustomer.setAuthorizedToManageCompanies(Boolean.TRUE);
			defaultCustomer.setLabel("Atos Exploitation");
			defaultCustomer.setName("ATOS");
			defaultCustomer.setPortalUrl(this.getMessageSource().getMessage("access.url.portail", null, Locale.FRENCH));

			// Add applications to this customer
			List<Application> customerApplications = new ArrayList<Application>();
			customerApplications.add(this.applicationService.findByName(DEFAULT_APPLICATION_NAME));
			customerApplications.add(this.applicationService.findByName(APPLICATION_SITE_INTERNET_NAME));
			customerApplications.add(this.applicationService.findByName(APPLICATION_MSPL_NAME));

			defaultCustomer.setApplications(customerApplications);
			this.customerService.create(defaultCustomer);
		}

		if (this.adminProfileService.findAll().isEmpty()) {
			// Admin address
			address.setCity("City");
			address.setPostalCode("Postal Code");
			address.setPostalAddress("Postal address");

			// Admin Profile type
			ProfileType adminProfileType = this.profileTypeService.findByValue(ProfileTypeValue.ADMIN);

			// Create Super Admin
			if (this.adminProfileService.findByName(SUPER_ADMIN_NAME) == null) {
				AdminProfile superAdmin = new AdminProfile();
				superAdmin.setName(SUPER_ADMIN_NAME);
				superAdmin.setLabel(SUPER_ADMIN_NAME);
				superAdmin.setAddress(address);
				superAdmin.setEmail(SUPER_ADMIN_EMAIL_USERNAME);
				superAdmin.setEnabled(Boolean.TRUE);
				User superAdminUser = new User();
				superAdminUser.setName(SUPER_ADMIN_NAME);
				superAdminUser.setFirstname("Administrateur");
				superAdminUser.setLastname("Technique");
				superAdminUser.setEnabled(Boolean.TRUE);
				superAdminUser.setUsername(SUPER_ADMIN_EMAIL_USERNAME);
				superAdminUser.setTemporaryPassword(SUPER_ADMIN_DEFAULT_PASSWORD);
				this.userService.create(superAdminUser);
				superAdmin.setUser(superAdminUser);
				superAdmin.setProfileType(adminProfileType);
				superAdmin.setCustomer(defaultCustomer);

				// Set super admin roles
				List<Role> superAdminRoles = new ArrayList<Role>();
				superAdminRoles.add(this.roleService.findByName(ROLE_SUPER_ADMIN_NAME));
				superAdminRoles.add(this.roleService.findByName(ROLE_SUPER_ADMIN_SITE_INTERNET_NAME));
				superAdminRoles.add(this.roleService.findByName(ROLE_SUPER_ADMIN_MSPL_NAME));

				superAdmin.setRoles(superAdminRoles);
				this.adminProfileService.create(superAdmin);
			}

			// Create Functionnal Admin
			if (this.adminProfileService.findByName(ADMIN_NAME) == null) {
				AdminProfile admin = new AdminProfile();
				admin.setName(ADMIN_NAME);
				admin.setLabel(ADMIN_NAME);
				admin.setAddress(address);
				admin.setEmail(ADMIN_EMAIL_USERNAME);
				admin.setEnabled(Boolean.TRUE);
				User adminUser = new User();
				adminUser.setName(ADMIN_NAME);
				adminUser.setFirstname("Administrateur");
				adminUser.setLastname("Fonctionnel");
				adminUser.setEnabled(Boolean.TRUE);
				adminUser.setUsername(ADMIN_EMAIL_USERNAME);
				adminUser.setTemporaryPassword(ADMIN_DEFAULT_PASSWORD);
				this.userService.create(adminUser);
				admin.setUser(adminUser);
				admin.setProfileType(adminProfileType);
				admin.setCustomer(defaultCustomer);

				// Set customer roles
				List<Role> customerAdminRoles = new ArrayList<Role>();
				customerAdminRoles.add(this.roleService.findByName(ROLE_ADMIN_NAME));
				customerAdminRoles.add(this.roleService.findByName(ROLE_CUSTOMER_SITE_INTERNET_NAME));
				customerAdminRoles.add(this.roleService.findByName(ROLE_CUSTOMER_MSPL_NAME));

				admin.setRoles(customerAdminRoles);

				this.adminProfileService.create(admin);
			}
		}

	}

	public void initDefaultProfileTypeRoleByApplication() {
		if (this.profileTypeRoleService.findAll().isEmpty()) {
			ProfileType profileTypeAgent = this.profileTypeService.findByValue(ProfileTypeValue.AGENT);
			ProfileType profileTypeEmployee = this.profileTypeService.findByValue(ProfileTypeValue.EMPLOYEE);
			ProfileType profileTypeAdmin = this.profileTypeService.findByValue(ProfileTypeValue.ADMIN);

			// DEFAULT APPLICATION

			Role roleDefault = this.roleService.findByName(ROLE_DEFAULT_NAME);

			// create profileTypeRole for default role AGENT
			ProfileTypeRole profileTypeRoleAgentDefault = new ProfileTypeRole();
			profileTypeRoleAgentDefault.setDefaultAdmin(Boolean.FALSE);
			profileTypeRoleAgentDefault.setDefaultBasic(Boolean.TRUE);
			profileTypeRoleAgentDefault.setProfileType(profileTypeAgent);
			profileTypeRoleAgentDefault.setRole(roleDefault);
			this.profileTypeRoleService.create(profileTypeRoleAgentDefault);

			// create profileTypeRole for default role EMPLOYEE
			ProfileTypeRole profileTypeRoleEmployeeDefault = new ProfileTypeRole();
			profileTypeRoleEmployeeDefault.setDefaultAdmin(Boolean.FALSE);
			profileTypeRoleEmployeeDefault.setDefaultBasic(Boolean.TRUE);
			profileTypeRoleEmployeeDefault.setProfileType(profileTypeEmployee);
			profileTypeRoleEmployeeDefault.setRole(roleDefault);
			this.profileTypeRoleService.create(profileTypeRoleEmployeeDefault);

			// create profileTypeRole for admin role AGENT
			Role roleAdminAgent = this.roleService.findByName(ROLE_ADMIN_AGENT_NAME);

			ProfileTypeRole profileTypeRoleAgentAdmin = new ProfileTypeRole();
			profileTypeRoleAgentAdmin.setDefaultAdmin(Boolean.TRUE);
			profileTypeRoleAgentAdmin.setDefaultBasic(Boolean.FALSE);
			profileTypeRoleAgentAdmin.setProfileType(profileTypeAgent);
			profileTypeRoleAgentAdmin.setRole(roleAdminAgent);
			this.profileTypeRoleService.create(profileTypeRoleAgentAdmin);

			// create profileTypeRole for admin role EMPLOYEE
			Role roleAdminEmployee = this.roleService.findByName(ROLE_ADMIN_EMPLOYEE_NAME);

			ProfileTypeRole profileTypeRoleEmployeeAdmin = new ProfileTypeRole();
			profileTypeRoleEmployeeAdmin.setDefaultAdmin(Boolean.TRUE);
			profileTypeRoleEmployeeAdmin.setDefaultBasic(Boolean.FALSE);
			profileTypeRoleEmployeeAdmin.setProfileType(profileTypeEmployee);
			profileTypeRoleEmployeeAdmin.setRole(roleAdminEmployee);
			this.profileTypeRoleService.create(profileTypeRoleEmployeeAdmin);

			Role roleAdminDefault = this.roleService.findByName(ROLE_ADMIN_NAME);

			// create profileTypeRole for admin role ADMIN
			ProfileTypeRole profileTypeRoleAdminDefault = new ProfileTypeRole();
			profileTypeRoleAdminDefault.setDefaultAdmin(Boolean.FALSE);
			profileTypeRoleAdminDefault.setDefaultBasic(Boolean.TRUE);
			profileTypeRoleAdminDefault.setProfileType(profileTypeAdmin);
			profileTypeRoleAdminDefault.setRole(roleAdminDefault);
			this.profileTypeRoleService.create(profileTypeRoleAdminDefault);

			// APPLICATION SITE INTERNET
			Role roleSuperAdminPortalgen = this.roleService.findByName(ROLE_SUPER_ADMIN_SITE_INTERNET_NAME);

			// create profileTypeRole for super admin in portalgen
			ProfileTypeRole profileTypeRoleSuperAdminPortalgen = new ProfileTypeRole();
			profileTypeRoleSuperAdminPortalgen.setDefaultAdmin(Boolean.TRUE);
			profileTypeRoleSuperAdminPortalgen.setDefaultBasic(Boolean.FALSE);
			profileTypeRoleSuperAdminPortalgen.setProfileType(profileTypeAdmin);
			profileTypeRoleSuperAdminPortalgen.setRole(roleSuperAdminPortalgen);
			this.profileTypeRoleService.create(profileTypeRoleSuperAdminPortalgen);

			Role roleCustomerPortalgen = this.roleService.findByName(ROLE_CUSTOMER_SITE_INTERNET_NAME);

			// create profileTypeRole for customer in portalgen
			ProfileTypeRole profileTypeRoleCustomerPortalgen = new ProfileTypeRole();
			profileTypeRoleCustomerPortalgen.setDefaultAdmin(Boolean.FALSE);
			profileTypeRoleCustomerPortalgen.setDefaultBasic(Boolean.TRUE);
			profileTypeRoleCustomerPortalgen.setProfileType(profileTypeAdmin);
			profileTypeRoleCustomerPortalgen.setRole(roleCustomerPortalgen);
			this.profileTypeRoleService.create(profileTypeRoleCustomerPortalgen);

			Role roleAdminPortalgen = this.roleService.findByName(ROLE_ADMIN_SITE_INTERNET_NAME);

			// create profileTypeRole ADMIN for AGENT_PROFILE in portalgen
			ProfileTypeRole profileTypeRoleAdminPortalgen = new ProfileTypeRole();
			profileTypeRoleAdminPortalgen.setDefaultAdmin(Boolean.TRUE);
			profileTypeRoleAdminPortalgen.setDefaultBasic(Boolean.FALSE);
			profileTypeRoleAdminPortalgen.setProfileType(profileTypeAgent);
			profileTypeRoleAdminPortalgen.setRole(roleAdminPortalgen);
			this.profileTypeRoleService.create(profileTypeRoleAdminPortalgen);

			Role roleWriterPortalgen = this.roleService.findByName(ROLE_WRITER_SITE_INTERNET_NAME);

			// create profileTypeRole WRITER for AGENT_PROFILE in portalgen
			ProfileTypeRole profileTypeRoleWriterPortalgen = new ProfileTypeRole();
			profileTypeRoleWriterPortalgen.setDefaultAdmin(Boolean.FALSE);
			profileTypeRoleWriterPortalgen.setDefaultBasic(Boolean.TRUE);
			profileTypeRoleWriterPortalgen.setProfileType(profileTypeAgent);
			profileTypeRoleWriterPortalgen.setRole(roleWriterPortalgen);
			this.profileTypeRoleService.create(profileTypeRoleWriterPortalgen);

			Role roleContributorPortalgen = this.roleService.findByName(ROLE_CONTRIBUTOR_SITE_INTERNET_NAME);

			// create profileTypeRole CONTRIBUTOR for AGENT_PROFILE in portalgen
			ProfileTypeRole profileTypeRoleContributorPortalgen = new ProfileTypeRole();
			profileTypeRoleContributorPortalgen.setDefaultAdmin(Boolean.FALSE);
			profileTypeRoleContributorPortalgen.setDefaultBasic(Boolean.TRUE);
			profileTypeRoleContributorPortalgen.setProfileType(profileTypeAgent);
			profileTypeRoleContributorPortalgen.setRole(roleContributorPortalgen);
			this.profileTypeRoleService.create(profileTypeRoleContributorPortalgen);

			// APPLICATION MSPL

			// create profileTypeRole for super admin in MSPL
			Role roleSuperAdminMSPL = this.roleService.findByName(ROLE_SUPER_ADMIN_MSPL_NAME);
			ProfileTypeRole profileTypeRoleSuperAdminMSPL = new ProfileTypeRole();
			profileTypeRoleSuperAdminMSPL.setDefaultAdmin(Boolean.TRUE);
			profileTypeRoleSuperAdminMSPL.setDefaultBasic(Boolean.FALSE);
			profileTypeRoleSuperAdminMSPL.setProfileType(profileTypeAdmin);
			profileTypeRoleSuperAdminMSPL.setRole(roleSuperAdminMSPL);
			this.profileTypeRoleService.create(profileTypeRoleSuperAdminMSPL);

			// create profileTypeRole for customer in MSPL
			Role roleCustomerMSPL = this.roleService.findByName(ROLE_CUSTOMER_MSPL_NAME);
			ProfileTypeRole profileTypeRoleCustomerMSPL = new ProfileTypeRole();
			profileTypeRoleCustomerMSPL.setDefaultAdmin(Boolean.FALSE);
			profileTypeRoleCustomerMSPL.setDefaultBasic(Boolean.TRUE);
			profileTypeRoleCustomerMSPL.setProfileType(profileTypeAdmin);
			profileTypeRoleCustomerMSPL.setRole(roleCustomerMSPL);
			this.profileTypeRoleService.create(profileTypeRoleCustomerMSPL);

			// create profileTypeRole admin for AGENT_PROFILE in MSPL
			Role roleAdminMSPL = this.roleService.findByName(ROLE_ADMIN_MSPL_NAME);
			ProfileTypeRole profileTypeRoleAdminMSPL = new ProfileTypeRole();
			profileTypeRoleAdminMSPL.setDefaultAdmin(Boolean.TRUE);
			profileTypeRoleAdminMSPL.setDefaultBasic(Boolean.FALSE);
			profileTypeRoleAdminMSPL.setProfileType(profileTypeAgent);
			profileTypeRoleAdminMSPL.setRole(roleAdminMSPL);
			this.profileTypeRoleService.create(profileTypeRoleAdminMSPL);

			// create profileTypeRole agent for AGENT_PROFILE in MSPL
			Role roleAgentMSPL = this.roleService.findByName(ROLE_AGENT_MSPL_NAME);
			ProfileTypeRole profileTypeRoleAgentMSPL = new ProfileTypeRole();
			profileTypeRoleAgentMSPL.setDefaultAdmin(Boolean.FALSE);
			profileTypeRoleAgentMSPL.setDefaultBasic(Boolean.TRUE);
			profileTypeRoleAgentMSPL.setProfileType(profileTypeAgent);
			profileTypeRoleAgentMSPL.setRole(roleAgentMSPL);
			this.profileTypeRoleService.create(profileTypeRoleAgentMSPL);
		}
	}

	public void initDefaultStructureTypes() {
		StructureType organismType = new StructureType();
		organismType.setValue(StructureTypeValue.ORGANISM);
		this.structureTypeService.create(organismType);

		StructureType companyType = new StructureType();
		companyType.setValue(StructureTypeValue.COMPANY);
		this.structureTypeService.create(companyType);
	}

	public void initDefaultStructureTypeApplication() {
		Application defaultApplication = this.applicationService.findByName(DEFAULT_APPLICATION_NAME);

		// StructureTypeApplication for ORGANISM and Default application
		StructureType structureTypeOrganism = this.structureTypeService.findByValue(StructureTypeValue.ORGANISM);

		StructureTypeApplication strucAppOrg = new StructureTypeApplication();
		strucAppOrg.setApplication(defaultApplication);
		strucAppOrg.setStructureType(structureTypeOrganism);
		strucAppOrg.setSubscribable(Boolean.FALSE);
		strucAppOrg.setDefaultSubscription(Boolean.TRUE);
		strucAppOrg.setMultipleRoles(Boolean.FALSE);
		this.structureTypeApplicationService.create(strucAppOrg);

		// StructureTypeApplication for COMPANY and Default application
		StructureType structureTypeCompany = this.structureTypeService.findByValue(StructureTypeValue.COMPANY);

		StructureTypeApplication strucAppCompany = new StructureTypeApplication();
		strucAppCompany.setApplication(defaultApplication);
		strucAppCompany.setStructureType(structureTypeCompany);
		strucAppCompany.setSubscribable(Boolean.FALSE);
		strucAppCompany.setDefaultSubscription(Boolean.TRUE);
		strucAppCompany.setMultipleRoles(Boolean.FALSE);
		this.structureTypeApplicationService.create(strucAppCompany);

		// SITE INTERNET
		Application aSiteInternet = this.applicationService.findByName(APPLICATION_SITE_INTERNET_NAME);

		StructureTypeApplication strucAppOrgSiteInternet = new StructureTypeApplication();
		strucAppOrgSiteInternet.setApplication(aSiteInternet);
		strucAppOrgSiteInternet.setStructureType(structureTypeOrganism);
		strucAppOrgSiteInternet.setSubscribable(Boolean.TRUE);
		strucAppOrgSiteInternet.setDefaultSubscription(Boolean.FALSE);
		strucAppOrgSiteInternet.setMultipleRoles(Boolean.FALSE);
		this.structureTypeApplicationService.create(strucAppOrgSiteInternet);

		// MSPL
		Application aMSPL = this.applicationService.findByName(APPLICATION_MSPL_NAME);

		StructureTypeApplication strucAppOrgMSPL = new StructureTypeApplication();
		strucAppOrgMSPL.setApplication(aMSPL);
		strucAppOrgMSPL.setStructureType(structureTypeOrganism);
		strucAppOrgMSPL.setSubscribable(Boolean.TRUE);
		strucAppOrgMSPL.setDefaultSubscription(Boolean.FALSE);
		strucAppOrgMSPL.setMultipleRoles(Boolean.FALSE);
		this.structureTypeApplicationService.create(strucAppOrgMSPL);
	}

	public void initDefaultSynchronizationFiltersAndSubscriptions() {

		// FILTERS//

		// Filter 1
		SynchronizationFilter defaultFilter1 = new SynchronizationFilter();
		defaultFilter1.setAllApplicationsDisplayed(Boolean.FALSE);
		defaultFilter1.setAllRolesDisplayed(Boolean.FALSE);
		this.synchronizationFilterService.create(defaultFilter1);

		// Filter 2
		SynchronizationFilter defaultFilter2 = new SynchronizationFilter();
		defaultFilter2.setAllApplicationsDisplayed(Boolean.TRUE);
		defaultFilter2.setAllRolesDisplayed(Boolean.FALSE);
		this.synchronizationFilterService.create(defaultFilter2);

		// Filter 3
		SynchronizationFilter defaultFilter3 = new SynchronizationFilter();
		defaultFilter3.setAllApplicationsDisplayed(Boolean.FALSE);
		defaultFilter3.setAllRolesDisplayed(Boolean.TRUE);
		this.synchronizationFilterService.create(defaultFilter3);

		// SUBSCRIPTIONS//

		// Site internet synchronization subscriptions
		Application aSiteInternet = this.applicationService.findByName(APPLICATION_SITE_INTERNET_NAME);

		SynchronizationSubscription subscriptionSiteInternetAdmin = new SynchronizationSubscription();
		subscriptionSiteInternetAdmin.setApplication(aSiteInternet);
		subscriptionSiteInternetAdmin.setUri(this.getMessageSource()
				.getMessage("synchronization.subscription.portalgen.admin.uri", null, Locale.FRENCH));
		subscriptionSiteInternetAdmin.setResourceLabel(ResourceType.ADMIN_PROFILE);
		subscriptionSiteInternetAdmin.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionSiteInternetAdmin);

		SynchronizationSubscription subscriptionSiteInternetCustomer = new SynchronizationSubscription();
		subscriptionSiteInternetCustomer.setApplication(aSiteInternet);
		subscriptionSiteInternetCustomer.setUri(this.getMessageSource()
				.getMessage("synchronization.subscription.portalgen.customer.uri", null, Locale.FRENCH));
		subscriptionSiteInternetCustomer.setResourceLabel(ResourceType.CUSTOMER);
		subscriptionSiteInternetCustomer.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionSiteInternetCustomer);

		SynchronizationSubscription subscriptionSiteInternetAgent = new SynchronizationSubscription();
		subscriptionSiteInternetAgent.setApplication(aSiteInternet);
		subscriptionSiteInternetAgent.setUri(this.getMessageSource()
				.getMessage("synchronization.subscription.portalgen.agent.uri", null, Locale.FRENCH));
		subscriptionSiteInternetAgent.setResourceLabel(ResourceType.AGENT_PROFILE);
		subscriptionSiteInternetAgent.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionSiteInternetAgent);

		SynchronizationSubscription subscriptionSiteInternetOrganism = new SynchronizationSubscription();
		subscriptionSiteInternetOrganism.setApplication(aSiteInternet);
		subscriptionSiteInternetOrganism.setUri(this.getMessageSource()
				.getMessage("synchronization.subscription.portalgen.organism.uri", null, Locale.FRENCH));
		subscriptionSiteInternetOrganism.setResourceLabel(ResourceType.ORGANISM);
		subscriptionSiteInternetOrganism.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionSiteInternetOrganism);

		// MSPL synchronization subscriptions
		Application aMSPL = this.applicationService.findByName(APPLICATION_MSPL_NAME);

		SynchronizationSubscription subscriptionMSPLAdmin = new SynchronizationSubscription();
		subscriptionMSPLAdmin.setApplication(aMSPL);
		subscriptionMSPLAdmin.setUri(
				this.getMessageSource().getMessage("synchronization.subscription.mspl.admin.uri", null, Locale.FRENCH));
		subscriptionMSPLAdmin.setResourceLabel(ResourceType.ADMIN_PROFILE);
		subscriptionMSPLAdmin.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionMSPLAdmin);

		SynchronizationSubscription subscriptionMSPLCustomer = new SynchronizationSubscription();
		subscriptionMSPLCustomer.setApplication(aMSPL);
		subscriptionMSPLCustomer.setUri(this.getMessageSource()
				.getMessage("synchronization.subscription.mspl.customer.uri", null, Locale.FRENCH));
		subscriptionMSPLCustomer.setResourceLabel(ResourceType.CUSTOMER);
		subscriptionMSPLCustomer.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionMSPLCustomer);

		SynchronizationSubscription subscriptionMSPLAgent = new SynchronizationSubscription();
		subscriptionMSPLAgent.setApplication(aMSPL);
		subscriptionMSPLAgent.setUri(
				this.getMessageSource().getMessage("synchronization.subscription.mspl.agent.uri", null, Locale.FRENCH));
		subscriptionMSPLAgent.setResourceLabel(ResourceType.AGENT_PROFILE);
		subscriptionMSPLAgent.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionMSPLAgent);

		SynchronizationSubscription subscriptionMSPLOrganism = new SynchronizationSubscription();
		subscriptionMSPLOrganism.setApplication(aMSPL);
		subscriptionMSPLOrganism.setUri(this.getMessageSource()
				.getMessage("synchronization.subscription.mspl.organism.uri", null, Locale.FRENCH));
		subscriptionMSPLOrganism.setResourceLabel(ResourceType.ORGANISM);
		subscriptionMSPLOrganism.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionMSPLOrganism);

		SynchronizationSubscription subscriptionMSPLOrganismDepartment = new SynchronizationSubscription();
		subscriptionMSPLOrganismDepartment.setApplication(aMSPL);
		subscriptionMSPLOrganismDepartment.setUri(this.getMessageSource()
				.getMessage("synchronization.subscription.mspl.organismDepartment.uri", null, Locale.FRENCH));
		subscriptionMSPLOrganismDepartment.setResourceLabel(ResourceType.ORGANISM_DEPARTMENT);
		subscriptionMSPLOrganismDepartment.setSynchronizationFilter(defaultFilter1);
		this.synchronizationSubscriptionService.create(subscriptionMSPLOrganismDepartment);
	}

	// @OnEvent(component = "initStructureType", value = EventConstants.ACTION)
	// public Object onInitStructureType() {
	// StructureType organismType = new StructureType();
	// organismType.setLabel("Type organisme");
	// organismType.setName("Structure Type Organism");
	// organismType.setValue(StructureTypeValue.ORGANISM);
	//
	// StructureType companyType = new StructureType();
	// companyType.setLabel("Type entreprise");
	// companyType.setName("Structure Type Company");
	// companyType.setValue(StructureTypeValue.COMPANY);
	//
	// structureTypeService.create(organismType);
	// structureTypeService.create(companyType);
	//
	// organismType = structureTypeService
	// .findByName("Structure Type Organism");
	// companyType = structureTypeService.findByName("Structure Type Company");
	//
	// Application gu = applicationService.findByName("GU");
	//
	// StructureTypeApplication strucAppOrg = new StructureTypeApplication();
	// strucAppOrg.setApplication(gu);
	// strucAppOrg.setStructureType(organismType);
	// strucAppOrg.setSubscribable(Boolean.FALSE);
	// strucAppOrg.setDefaultSubscription(Boolean.TRUE);
	// strucAppOrg.setLabel(strucAppOrg.getApplication().getLabel() + "-"
	// + strucAppOrg.getStructureType().getLabel());
	// strucAppOrg.setName("StructureTypeApplication "
	// + strucAppOrg.getApplication().getLabel() + "-"
	// + strucAppOrg.getStructureType().getLabel());
	//
	// StructureTypeApplication strucAppCompany = new
	// StructureTypeApplication();
	// strucAppCompany.setApplication(gu);
	// strucAppCompany.setStructureType(companyType);
	// strucAppCompany.setSubscribable(Boolean.FALSE);
	// strucAppCompany.setDefaultSubscription(Boolean.TRUE);
	// strucAppCompany.setLabel(strucAppOrg.getApplication().getLabel() + "-"
	// + strucAppOrg.getStructureType().getLabel());
	// strucAppCompany.setName("StructureTypeApplication "
	// + strucAppOrg.getApplication().getLabel() + "-"
	// + strucAppOrg.getStructureType().getLabel());
	//
	// this.structureTypeApplicationService.create(strucAppOrg);
	// this.structureTypeApplicationService.create(strucAppCompany);
	//
	// this.successMessage = "Initialisation terminée";
	// return null;
	// }

	// Form events
	// @OnEvent(component = "profileTypeRole_form", value =
	// EventConstants.SUCCESS)
	// public void onProfileTypeRoleFormSuccess() {
	// Role role = this.roleService.findById(this.idRole);
	// ProfileType profileType = this.profileTypeService
	// .findByValue(profileTypeValue);
	//
	// if (null == role) {
	// this.successMessage = "Role introuvable";
	// } else {
	// if (null == profileType) {
	// this.successMessage = "ProfileType introuvable";
	// } else {
	// this.newProfileTypeRole.setRole(role);
	// this.newProfileTypeRole.setProfileType(profileType);
	// this.newProfileTypeRole.setLabel("ProfileTypeRole "
	// + role.getLabel() + "-" + profileType.getValue());
	// this.newProfileTypeRole.setName("ProfileTypeRole "
	// + role.getLabel() + "-" + profileType.getValue());
	// this.profileTypeRoleService.create(this.newProfileTypeRole);
	// this.successMessage = "ProfileTypeRole enregistré";
	// }
	// }
	//
	// }
	//
	// @OnEvent(component = "structureTypeApplication_form", value =
	// EventConstants.SUCCESS)
	// public void onStructureTypeApplicationFormSuccess() {
	// Application application = this.applicationService
	// .findById(this.idApplication);
	// StructureType structureType = this.structureTypeService
	// .findByValue(structureTypeValue);
	//
	// if (null == application) {
	// this.successMessage = "Application introuvable";
	// } else {
	// if (null == structureType) {
	// this.successMessage = "StructureType introuvable";
	// } else {
	// this.newStructureTypeApplication.setApplication(application);
	// this.newStructureTypeApplication
	// .setStructureType(structureType);
	// this.newStructureTypeApplication
	// .setLabel("StructureTypeApplication "
	// + application.getLabel() + "-"
	// + structureType.getValue());
	// this.newStructureTypeApplication
	// .setName("StructureTypeApplication "
	// + application.getLabel() + "-"
	// + structureType.getValue());
	// this.structureTypeApplicationService
	// .create(this.newStructureTypeApplication);
	// this.successMessage = "StructureTypeApplication enregistré";
	// }
	// }
	//
	// }

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

}
