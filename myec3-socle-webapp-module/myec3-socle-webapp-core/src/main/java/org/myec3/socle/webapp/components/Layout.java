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
package org.myec3.socle.webapp.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.enums.RoleProfile;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.entities.MenuItem;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Component class used to construct the layout depending on logged used<br>
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/components/Layout.tml
 * 
 * @author Loic Frering <loic.frering@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Anthony Colas <anthony.colas@atosorigin.com>
 */
public class Layout extends AbstractPage {

	@SuppressWarnings("unused")
	@Inject
	private ComponentResources resources;

	@Inject
	private PageRenderLinkSource linkSource;

	@Property
	@SuppressWarnings("unused")
	private MenuItem mainMenuLoop;

	@Property
	@SuppressWarnings("unused")
	private MenuItem subMenuLoop;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Named("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Named("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Named("applicationService")
	private ApplicationService applicationService;

	@SuppressWarnings("unused")
	@Inject
	private PersistentLocale persistentLocaleService;

	@Property
	private int copyright_year = Calendar.getInstance().get(Calendar.YEAR);

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

//	@SuppressWarnings("unused")
//	@Property
//	private String logoutUrl = MyEc3Constants.J_SPRING_SECURITY_LOGOUT;

	@SuppressWarnings("unused")
	@Property
	private String logoutUrl = GuWebAppConstants.KEYCLOAK_BASE_URL +
			"/auth/realms/megalis/protocol/openid-connect/logout";

	@SuppressWarnings("unused")
	public Link getRedirectUri() {
		return linkSource.createPageRenderLink(this.resources.getPage().getClass());
	}
	
	@Property
	private String legalNoticeUrl = GuWebAppConstants.LEGAL_NOTICE_URL;

	/**
	 * @return List of menu items corresponding at the logged user
	 */
	public List<MenuItem> getMainMenuList() {
		List<MenuItem> mainMenuList = new ArrayList<MenuItem>();
		if (this.getLoggedProfileExists()) {
			mainMenuList.add(new MenuItem(this.getMessages().get("homepage-label"), Index.class, true));

			mainMenuList.add(new MenuItem(this.getMessages().get("my-portal-label"), null, true));

			Profile profile = profileService.findOne(this.getLoggedProfile().getId());
			List<Role> roles = roleService.findAllRoleByProfileAndApplication(this.getLoggedProfile(),
					this.applicationService.findByName(MyEc3ApplicationConstants.GU));
			for (Role role : roles) {
				if (profile.isAdmin()) {
					mainMenuList.add(new MenuItem(this.getMessages().get("newOrganism-label"),
							org.myec3.socle.webapp.pages.organism.Create.class, true));
					mainMenuList.add(new MenuItem(this.getMessages().get("searchOrganism-label"),
							org.myec3.socle.webapp.pages.organism.Search.class, true));

					// Check if profile has right to manage companies
					if (isLoggedProfileAllowedToManageCompanies(profile)) {
						mainMenuList.add(new MenuItem(this.getMessages().get("newCompany-label"),
								org.myec3.socle.webapp.pages.company.Siren.class, true));
						mainMenuList.add(new MenuItem(this.getMessages().get("searchCompany-label"),
								org.myec3.socle.webapp.pages.company.Search.class, true));
					}
				} else if (profile.isAgent()) {
					if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_MANAGER_AGENT.toString())) {
						// If the organism of the agent is no longer member
						// the agent musn't manage it
						if (super.getIsMember(profile)) {
							mainMenuList.add(new MenuItem(this.getMessages().get("viewOrganism-label"),
									org.myec3.socle.webapp.pages.organism.DetailOrganism.class, true));
							mainMenuList.add(new MenuItem(this.getMessages().get("viewDepartment-label"),
									org.myec3.socle.webapp.pages.organism.department.View.class, true));
						}
					}
				} else if (profile.isEmployee()) {
					if (role.getName().equalsIgnoreCase(RoleProfile.ROLE_MANAGER_EMPLOYEE.toString())) {
						mainMenuList.add(new MenuItem(this.getMessages().get("viewCompany-label"),
								org.myec3.socle.webapp.pages.company.DetailCompany.class, true));
					}
				}

			}
			// }
		} else
			mainMenuList.add(new MenuItem(this.getMessages().get("login-label"), Index.class, true));

		return mainMenuList;
	}

	/**
	 * @return true if current logged user is an agent{@link AgentProfile} (Admin or
	 *         not). It's only based on Profile Type, not on authorities (roles)
	 */
	@Override
	public Boolean getIsAgent() {
		if (!this.getLoggedProfileExists()) {
			return Boolean.FALSE;
		}

		if ((null != this.getLoggedProfile()) && (null != this.getLoggedProfile().getId())) {
			Profile profile = this.getLoggedProfile();
			if (ProfileTypeValue.AGENT == profile.getProfileType().getValue()) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	/**
	 * @return true if current logged user is an employye {@link EmployeeProfile}
	 *         (Admin or not). It's only based on Profile Type, not on authorities
	 *         (roles)
	 */
	@Override
	public Boolean getIsEmployee() {
		if (!this.getLoggedProfileExists()) {
			return Boolean.FALSE;
		}

		if ((null != this.getLoggedProfile()) && (null != this.getLoggedProfile().getId())) {
			Profile profile = getLoggedProfile();
			if (ProfileTypeValue.EMPLOYEE == profile.getProfileType().getValue()) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	public Boolean getHasCustomLogo() {
		if (this.getLoggedProfileExists()) {
			Profile profile = this.getLoggedProfile();
			if (!profile.isEmployee()) {
				Customer customer = this.getCustomerOfLoggedProfile();
				if ((customer != null) && (customer.getLogoUrl() != null) && !(customer.getLogoUrl().isEmpty())) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	public String getCustomerLogoUrl() {
		Customer customer = this.getCustomerOfLoggedProfile();
		String logoPath = "";
		if (customer != null) {
			logoPath = customer.getLogoUrl();
		}
		return logoPath;
	}

}
