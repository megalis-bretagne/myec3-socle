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

import java.util.List;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.entities.MenuItem;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Component class used to create navigation menu<br>
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/components/NavigationMenu.tml
 * 
 * @author Anthony Colas <anthony.colas@atos.net>
 * 
 */
public class NavigationMenu extends AbstractPage {

	private static final Log logger = LogFactory.getLog(NavigationMenu.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link AgentProfile} objects
	 */
	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link EmployeeProfile} objects
	 */
	@Inject
	@Named("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	@Inject
	private ComponentResources resources;

	@SuppressWarnings("unused")
	@Parameter(required = true)
	@Property
	private List<MenuItem> items;

	@Property
	private MenuItem currentItem;

	@Property
	private MenuItem activeItem;

	@Property
	private String activePackage;

	@Inject
	private PageRenderLinkSource renderLinkSource;

	@SuppressWarnings("unused")
	@SetupRender
	private void retrieveCurrentPage() {
		Class<?> currentPage = this.resources.getPage().getClass();
		logger.debug("SetupRender - Current page " + currentPage.getCanonicalName());
		this.activePackage = this.getPackageFromClass(currentPage);
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	public String getCurrentItemPath() {

		Class<?> page = currentItem.getPage();

		String p = "";
		if (currentItem.getName().equalsIgnoreCase(this.getMessages().get("my-portal-label"))) {

			if (this.getLoggedProfileExists() && this.getLoggedProfile() != null) {
				Customer customer = null;
				if (this.getLoggedProfile().isAdmin()) {
					customer = ((AdminProfile) this.getLoggedProfile()).getCustomer();
				} else if (this.getLoggedProfile().isAgent()) {
					customer = ((AgentProfile) this.getLoggedProfile()).getOrganismDepartment().getOrganism()
							.getCustomer();
				}

				if (customer != null) {
					return customer.getPortalUrl();
				}
			}
			return GuWebAppConstants.MY_PORTAL_URL;
		}
		if (currentItem.getPage().getName().equalsIgnoreCase("org.myec3.socle.webapp.pages.organism.DetailOrganism")
				|| (currentItem.getPage().getName()
						.equalsIgnoreCase("org.myec3.socle.webapp.pages.organism.department.View"))) {
			AgentProfile agentProfile = this.agentProfileService.findOne(this.getLoggedProfile().getId());
			p = agentProfile.getOrganismDepartment().getOrganism().getId().toString();
		} else if (currentItem.getPage().getName()
				.equalsIgnoreCase("org.myec3.socle.webapp.pages.company.DetailCompany")) {
			EmployeeProfile employeeProfile = this.employeeProfileService.findOne(this.getLoggedProfile().getId());
			p = employeeProfile.getCompanyDepartment().getCompany().getId().toString();
		}

		Link l = this.renderLinkSource.createPageRenderLinkWithContext(page, p);
		return l.toURI();
	}

	public boolean getCurrentItemIsActive() {

		// Item is active if current page is same as active page or one of the
		// children of active page
		if (this.currentItem.getPage() != null) {
			String currentItemPagePackage = this.getPackageFromClass(this.currentItem.getPage());
			if (logger.isDebugEnabled()) {
				logger.debug("GetCurrentItemIsActive - Current item package : " + currentItemPagePackage);
				logger.debug("GetCurrentItemIsActive - Active package : " + activePackage);
			}
			if (activePackage.equals(currentItemPagePackage)
					|| (!this.currentItem.isExactMatch() && activePackage.startsWith(currentItemPagePackage))) {
				this.activeItem = this.currentItem;
				return true;
			}
		}
		return false;
	}

	public boolean getActiveItemHasChildren() {

		if (this.activeItem != null) {
			List<MenuItem> children = this.activeItem.getChildren();
			return children != null && children.size() > 0;
		}
		return false;
	}

	private String getPackageFromClass(Class<?> c) {

		String cannonicalName = c.getCanonicalName();
		String[] parts = cannonicalName.split("\\.");

		StringBuilder result = new StringBuilder(parts[0]);
		int iterations = parts.length - 1;
		for (int i = 1; i < iterations; i++) {
			result.append(".").append(parts[i]);
		}

		return result.toString();
	}
}
