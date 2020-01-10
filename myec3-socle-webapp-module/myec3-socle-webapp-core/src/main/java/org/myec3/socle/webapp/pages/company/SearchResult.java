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

import java.util.List;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display the search{@link Search} companies{@link Company} result<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/SearchResult.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public class SearchResult extends AbstractPage {

	@Inject
	private BeanModelSource beanModelSource;

	@Persist
	private List<Company> companiesResult;

	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@SuppressWarnings("unused")
	@Property
	private Company companyRow;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		super.initUser();
		if (this.companiesResult == null) {
			return Index.class;
		}
		return null;
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<Company> getGridModel() {
		BeanModel<Company> model = this.beanModelSource.createDisplayModel(
				Company.class, this.getMessages());
		model.add("postalCode", null);
		model.add("city", null);
		model.add("actions", null);
		model.get("companyAcronym").label("Sigle");
		model.include("label", "companyAcronym", "siren", "nic", "postalCode", "city",
				"actions");
		return model;
	}

	/**
	 * @return the list of companies found
	 */
	public List<Company> getCompaniesResult() {
		String temp = null;

		for (int i = 0; i < companiesResult.size(); i++) {
			temp = companiesResult.get(i).getLabel();
			temp = UnaccentLetter.unaccentString(temp);
			temp = temp.toUpperCase();
			companiesResult.get(i).setLabel(temp);
		}

		return companiesResult;
	}

	public void setCompaniesResult(List<Company> companiesResult) {
		this.companiesResult = companiesResult;
	}

	/**
	 * @return the number of companies found
	 */
	public Integer getResultsNumber() {
		if (null == this.companiesResult)
			return 0;
		return this.companiesResult.size();
	}
}
