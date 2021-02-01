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
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.annotations.PageLoaded;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.enums.RoleProfile;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.RoleService;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

/**
 * Class containing all methods that must be accessible in all pages like
 * authentication, which user is authenticated, his rights etc...<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/AbstractPage.tml
 *
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti<denis.cucchietti@atosorigin.com>
 */
public class AbstractPage {

	private static final Log logger = LogFactory.getLog(AbstractPage.class);
	private static final String PROJECT_USER = "PROJECT";

	public static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("webapp");
	public static final String APPMANAGER_ACTIVE_STRING = BUNDLE.getString("agent.appmanagers.active");
	public static final Boolean APPMANAGER_ACTIVE = (APPMANAGER_ACTIVE_STRING.equalsIgnoreCase("true")
			|| APPMANAGER_ACTIVE_STRING.equals("1"));

	@Inject
	private ReloadableResourceBundleMessageSource messageSource;

	@Inject
	@Named("applicationService")
	protected ApplicationService applicationService;

	@Inject
	@Named("roleService")
	private RoleService roleService;

	@Inject
	private Messages messages;

	@Inject
	private Request request;

	@SessionState
	private Profile loggedProfile;

	private boolean loggedProfileExists;

	@PageLoaded
	/***
	 * init connected profile with openSso header uid
	 */
	public void initUser() {
		SecurityContext context = null;
		Session session = null;
		Profile profile = null;

		if (logger.isDebugEnabled()){
			logger.debug("Liste des headers recus");
			request.getHeaderNames().stream().forEach(p -> logger.debug(p + " " + request.getHeader(p)));
		}

		session = this.request.getSession(true);
//		String userType = request.getHeader("userType");

		// If user is a project user, he doesn't have access to socle
		// so we do well.... nothing !
//		if (userType != null && !PROJECT_USER.equals(userType)) {
			if (null != session) {
				context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
			} else {
				logger.debug("No session found");
			}
			if (null != context) {
				profile = (Profile) context.getAuthentication().getPrincipal();
			} else {
				logger.debug("No context found");
			}
			if (null != profile) {
				this.loggedProfile = profile;
			} else {
				logger.debug("No profile found");
			}
//		}
	}

	/**
	 * @return current logged profile {@link Profile}
	 */
	public Profile getLoggedProfile() {
		return this.loggedProfile;
	}

	/**
	 * @return true if a profile {@link Profile} is logged
	 */
	public boolean getLoggedProfileExists() {
		return this.loggedProfileExists;
	}

	/**
	 * @return true if profile {@link Profile} is a functional administrator
	 *         {@link AdminProfile}
	 */
	public Boolean getIsFunctionalAdmin() {
		return loggedProfileHasOneRole(RoleProfile.ROLE_ADMIN);
	}

	/**
	 * @return true if profile {@link Profile} is a technical administrator
	 *         {@link AdminProfile}
	 */
	public Boolean getIsTechnicalAdmin() {
		return loggedProfileHasOneRole(RoleProfile.ROLE_SUPER_ADMIN);
	}

	/**
	 * @return true if profile {@link Profile} is an administrator (functional or
	 *         technical) {@link AdminProfile}
	 */
	public Boolean getIsAdmin() {
		return loggedProfileHasOneRole(RoleProfile.ROLE_SUPER_ADMIN, RoleProfile.ROLE_ADMIN);
	}

	/**
	 * @return true if profile (@link Profile} is an organism administrator (either
	 *         functionnal/technical admin or global manager)
	 */
	public Boolean getIsOrganismAdmin() {
		return loggedProfileHasOneRole(
				RoleProfile.ROLE_SUPER_ADMIN,
				RoleProfile.ROLE_ADMIN,
				RoleProfile.ROLE_MANAGER_AGENT);
	}

	/**
	 * @return true if the logged user is an agent administrator
	 *         {@link AgentProfile}
	 */
	public Boolean getIsGlobalManagerAgent() {
		return loggedProfileHasOneRole(RoleProfile.ROLE_MANAGER_AGENT);
	}

	public Boolean getIsApplicationManagerAgent() {
		return loggedProfileHasOneRole(RoleProfile.ROLE_APPLICATION_MANAGER_AGENT);
	}

	/**
	 * @return true if logged user is an agent {@link AgentProfile}
	 */
	public Boolean getIsAgent() {
		return (loggedProfileHasOneRole(RoleProfile.ROLE_DEFAULT)
				&& ProfileTypeValue.AGENT.equals(this.loggedProfile.getProfileType().getValue()))
				||
				(this.loggedProfile.isAgent());
	}

	/**
	 * @return true if logged user is an employee admin {@link EmployeeProfile}
	 */
	public Boolean getIsManagerEmployee() {
		return loggedProfileHasOneRole(RoleProfile.ROLE_MANAGER_EMPLOYEE);
	}

	/**
	 * @return true if logged user is an employee {@link EmployeeProfile}
	 */
	public Boolean getIsEmployee() {
		return (loggedProfileHasOneRole(RoleProfile.ROLE_DEFAULT)
				&& ProfileTypeValue.EMPLOYEE.equals(this.loggedProfile.getProfileType().getValue()))
				||
				(this.loggedProfile.isEmployee());
	}

	/**
	 * @param profile : the current logged user
	 * @return true if the organism of the agent is a member of our platform
	 */
	public Boolean getIsMember(Profile profile) {
		if (profile instanceof AgentProfile) {
			AgentProfile managerAgentProfile = (AgentProfile) profile;
			return managerAgentProfile.getOrganismDepartment().getOrganism().getMember();
		}
		return Boolean.FALSE;
	}

	public Boolean getIsAppManagerActive() {
		return APPMANAGER_ACTIVE;
	}

	/**
	 * @return true if user has at least one authority
	 */
	public Boolean hasAuthorities() {
		Collection<GrantedAuthority> authorities = this.loggedProfile.getAuthorities();
		if ((null == authorities) || (authorities.isEmpty())) {
			logger.debug("profile has no authority");
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	/**
	 * @return the current web context path
	 */
	public String getWebContext() {
		return request.getContextPath();
	}

	/**
	 * @return the messages tapestry component
	 */
	public Messages getMessages() {
		return messages;
	}

	/**
	 * @return the Resource Message Spring component
	 */
	public ReloadableResourceBundleMessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * @return the customer of the logged user
	 */
	public Customer getCustomerOfLoggedProfile() {
		Customer customer = null;
		Profile profile = this.getLoggedProfile();
		if (profile instanceof AdminProfile) {
			customer = ((AdminProfile) profile).getCustomer();
		} else if (profile instanceof AgentProfile) {
			customer = ((AgentProfile) profile).getOrganismDepartment().getOrganism().getCustomer();
		}
		return customer;
	}

	public Boolean isLoggedProfileAllowedToManageCompanies(Profile profile) {
		if (loggedProfileExists) {
			if (profile.isAdmin()) {
				if (this.getIsTechnicalAdmin()) {
					return Boolean.TRUE;
				} else {
					return ((AdminProfile) profile).getCustomer().isAuthorizedToManageCompanies();
				}
			}
		}
		return Boolean.FALSE;
	}

	public Object hasRightsToManageCompanies() {
		if (loggedProfileExists) {
			if (loggedProfile.isAdmin()) {
				AdminProfile adminProfile = (AdminProfile) this.loggedProfile;
				if ((this.getIsTechnicalAdmin()) || (adminProfile.getCustomer().isAuthorizedToManageCompanies())) {
					return Boolean.TRUE;
				}
			}
		}
		return Index.class;
	}

	/**
	 *
	 * @param profile of agent or employee {@link EmployeeProfile} in the GUI
	 * @return true if admin has the right to modify the selected profile
	 */
	public Object hasRights(Profile profile) {
		if (loggedProfileExists) {
			if (!loggedProfile.isAdmin()) {
				if (profile instanceof AgentProfile) {
					AgentProfile admin = (AgentProfile) this.loggedProfile;
					AgentProfile agentProfile = (AgentProfile) profile;
					// We check if the organism of the agent admin is member of
					// platform
					if (this.getIsMember(agentProfile)) {
						logger.debug("profile " + loggedProfile.getUsername() + " has rights to modify this profile : "
								+ profile.getUsername());
						if (agentProfile.getOrganismDepartment().getOrganism()
								.equals(admin.getOrganismDepartment().getOrganism())) {
							return Boolean.TRUE;
						}
					}
				} else if (profile instanceof EmployeeProfile) {
					EmployeeProfile admin = (EmployeeProfile) this.loggedProfile;
					EmployeeProfile employeeProfile = (EmployeeProfile) profile;

					if (employeeProfile.getCompanyDepartment().getCompany()
							.equals(admin.getCompanyDepartment().getCompany())) {
						logger.debug("profile " + loggedProfile.getUsername() + " has rights to modify this profile : "
								+ profile.getUsername());
						return Boolean.TRUE;
					}
				}
			} else {
				if (profile instanceof EmployeeProfile) {
					AdminProfile adminProfile = (AdminProfile) this.loggedProfile;
					if (this.isLoggedProfileAllowedToManageCompanies(adminProfile)) {
						return Boolean.TRUE;
					}
				} else {
					logger.debug("profile " + loggedProfile.getUsername() + " has rights to modify this profile : "
							+ profile.getUsername());
					return Boolean.TRUE;
				}
			}
		}
		logger.debug("profile " + loggedProfile.getUsername() + " has no right to modify this profile : "
				+ profile.getUsername());
		return Index.class;
	}

	/**
	 *
	 * @param structure : company {@link Company} or organism {@link Organism} we
	 *                  want to modify
	 * @return true if user has the right to modify
	 */
	public Object hasRights(Structure structure) {
		if (this.loggedProfileExists) {
			if (!loggedProfile.isAdmin()) {
				if (loggedProfile instanceof AgentProfile) {
					AgentProfile admin = (AgentProfile) this.loggedProfile;
					// Check if the organism of the admin is member of platform
					if (this.getIsMember(admin)) {
						logger.debug("profile " + admin.getUsername() + " is member of platform");
						if (admin.getOrganismDepartment().getOrganism().equals((Organism) structure)) {
							logger.debug("profile " + loggedProfile.getUsername()
									+ " has rights to modify this structure : " + structure.getId());
							return Boolean.TRUE;
						}
					}
				} else if (loggedProfile instanceof EmployeeProfile) {
					EmployeeProfile admin = (EmployeeProfile) this.loggedProfile;
					if (admin.getCompanyDepartment().getCompany().equals((Company) structure)) {
						logger.debug("profile " + loggedProfile.getUsername()
								+ " has rights to modify this structure : " + structure.getId());
						return Boolean.TRUE;
					}
				}
			} else {
				if (structure instanceof Company) {
					AdminProfile adminProfile = (AdminProfile) this.loggedProfile;
					if (this.isLoggedProfileAllowedToManageCompanies(adminProfile)) {
						return Boolean.TRUE;
					}
				} else {
					logger.debug("profile " + loggedProfile.getUsername() + " has rights to modify this structure : "
							+ structure.getId());
					return Boolean.TRUE;
				}
			}
		}
		logger.debug("profile " + loggedProfile.getUsername() + " has no rights to modify this structure : "
				+ structure.getId());
		return Index.class;
	}

	public Boolean hasRights(Establishment establishment) {
		if (this.loggedProfileExists) {
			if (!loggedProfile.isAdmin()) {
				if (loggedProfile instanceof EmployeeProfile) {
					EmployeeProfile admin = (EmployeeProfile) this.loggedProfile;
					if (admin.getEstablishment().equals(establishment)) {
						logger.info("profile " + loggedProfile.getUsername()
								+ " has rights to modify this establishment : " + establishment.getId());
						return Boolean.TRUE;
					}
				}
			} else {
				if (establishment instanceof Establishment) {
					AdminProfile adminProfile = (AdminProfile) this.loggedProfile;
					if (this.isLoggedProfileAllowedToManageCompanies(adminProfile)) {
						return Boolean.TRUE;
					}
				} else {
					logger.info("profile " + loggedProfile.getUsername() + " has rights to modify this establishment : "
							+ establishment.getId());
					return Boolean.TRUE;
				}
			}
		}
		logger.info("profile " + loggedProfile.getUsername() + " has no rights to modify this establishment : "
				+ establishment.getId());
		return Boolean.FALSE;
	}

	public Boolean hasRightsOnOrganism(Organism organism) {
		if (this.loggedProfileExists) {
			if (!loggedProfile.isAdmin()) {
				if (loggedProfile instanceof AgentProfile) {
					AgentProfile admin = (AgentProfile) this.loggedProfile;
					if (admin.getOrganismDepartment().getOrganism()
							.equals(organism)
							&& this.getIsGlobalManagerAgent()) {

						logger.info("profile " + loggedProfile.getUsername()
								+ " has rights to modify this organism : "
								+ organism.getId());
						return Boolean.TRUE;
					}
				}
			}
		}
		logger.info("profile " + loggedProfile.getUsername()
				+ " has no rights to modify this organism : "
				+ organism.getId());
		return Boolean.FALSE;
	}

	public Boolean hasRightsToManageOrganismApplications(Organism organism) {
		if (this.loggedProfileExists) {
			if (!loggedProfile.isAdmin()) {
				if (loggedProfile instanceof AgentProfile) {
					AgentProfile admin = (AgentProfile) this.loggedProfile;
					if (admin.getOrganismDepartment().getOrganism()
							.equals(organism)
							&& this.getIsApplicationManagerAgent()) {

						logger.info("profile " + loggedProfile.getUsername()
								+ " has rights to manage applications for this organism : "
								+ organism.getId());
						return Boolean.TRUE;
					}
				}
			}
		}
		logger.info("profile " + loggedProfile.getUsername()
				+ " has no rights to manage applications for this organism : "
				+ organism.getId());
		return Boolean.FALSE;
	}

	public void removeEmployeeManagerRole(EmployeeProfile employeeProfile) {
		List<Application> applications;
		List<Role> listRole = new ArrayList<Role>();

		applications = this.applicationService
				.findAllApplicationByStructure(
						employeeProfile.getCompanyDepartment().getCompany());

		for (Application application : applications) {

			if (application.getName().equals("GU")) {

				listRole = application.getRoles();

				for (Role role : listRole) {
					if (role.getName().equals("ROLE_MANAGER_EMPLOYEE")) {
						employeeProfile.removeRole(role);
						employeeProfile.addRoles(
								roleService.findAllByCriteria(null, "ROLE_DEFAULT", application));
					}
				}

			}
		}
		logger.debug("Employee New Roles : " + listRole);
	}

	public Boolean belongsToEstablishment(Company company) {
		EmployeeProfile employee = (EmployeeProfile) this.loggedProfile;

		if (employee.getEstablishment().getCompany().equals(company)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Return true if the logged user has at least one of the role passed in
	 * parameters
	 * 
	 * @param roleProfiles the roles to check
	 * @return Boolean.TRUE if the user has been granted at least one of the roles
	 */
	private Boolean loggedProfileHasOneRole(RoleProfile... roleProfiles) {
		if (this.getLoggedProfileExists()) {
			Collection<GrantedAuthority> authorities = this.loggedProfile.getAuthorities();

			for (RoleProfile roleProfile : roleProfiles) {
				if (hasSpecificAuthority(authorities, roleProfile)) {
					logger.debug("profile is " + roleProfile);
					return Boolean.TRUE;
				}
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * Return true if the RoleProfile is in the collection of Authorities (case
	 * insensitive)
	 * 
	 * @param authorities
	 * @param roleProfile
	 * @return
	 */
	private Boolean hasSpecificAuthority(Collection<GrantedAuthority> authorities, RoleProfile roleProfile) {
		for (GrantedAuthority authority : authorities) {
			if (roleProfile.toString().equalsIgnoreCase(authority.getAuthority())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
}
