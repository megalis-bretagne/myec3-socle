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
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;
import java.util.List;

/**
 * Page used to search companies{@link Company} from the databse<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/Search.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public class Search extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Inject
	@Named("companyService")
	private CompanyService companyService;

	@Component(id = "company_search_form")
	private Form companySearchForm;

	@Property
	private String searchLabel;
	
	@Property
	private String searchAcronym;

	@Property
	private String searchSiren;

	@Property
	private String searchPostalCode;

	@Property
	private String searchCity;

	@Property
	private List<Company> companiesResult;

	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String infoMessage;

	@InjectPage
	private SearchResult searchResultPage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		super.initUser();
		return this.hasRightsToManageCompanies();
	}

	@OnEvent(component = "company_search_form", value = EventConstants.VALIDATE)
	public void formValidation() {
	    if ((null == searchLabel) && (null == searchAcronym)
		    && (null == searchSiren) && (null == searchPostalCode)
		    && (null == searchCity)) {
		this.companySearchForm.recordError(this.getMessages().get(
			"empty-filter-error"));
	    }
	}

	@OnEvent(component = "company_search_form", value = EventConstants.SUCCESS)
	public Object onSuccess() {
		this.companiesResult = this.companyService.findAllByCriteria(
				searchLabel, searchAcronym, searchSiren, searchPostalCode, searchCity);
		if (this.companiesResult.isEmpty()) {
			this.infoMessage = this.getMessages().get("empty-result-message");
			return null;
		}

		this.searchResultPage.setCompaniesResult(companiesResult);
		return SearchResult.class;
	}

}
