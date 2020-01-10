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

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.organism.Search;

/**
 * Page used to display a success message at the end of company creation process
 * {@link Company}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/View.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class View extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Inject
	@Service("companyService")
	private CompanyService companyService;

	private Company company;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@SuppressWarnings("unused")
	@Property
	private String logoutUrl = MyEc3Constants.J_SPRING_SECURITY_LOGOUT;

	@SuppressWarnings("unused")
	@Property
	private String locHallesLoginUrl = GuWebAppConstants.LOCHALLES_LOGIN_URL;

	@Persist
	private Boolean isLocHallesTheme;

	public void setIsLocHallesTheme(Boolean isLocHallesTheme) {
		this.isLocHallesTheme = isLocHallesTheme;
	}

	public Boolean getIsLocHallesTheme() {
		return isLocHallesTheme;
	}

	// Services
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Search.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.company = this.companyService.findOne(id);
		if (null == this.company) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.company != null) ? this.company.getId() : null;
	}

	// Getters n Setters
	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Boolean getUpdateButton() {
		if (this.getIsTechnicalAdmin()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}
