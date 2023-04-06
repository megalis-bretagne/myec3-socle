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
package org.myec3.socle.webapp.pages.synchroman.application;

import java.util.List;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.beanmodel.PropertyConduit;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.beanmodel.services.PropertyConduitSource;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display the result of the search application page<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/synchroman/application/SearchResult.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 */

@SuppressWarnings("unused")
public class SearchResult extends AbstractPage {

	// Template attributes
	@Persist
	private List<Application> applicationsResult;

	@Property
	private Integer rowIndex;

	@Property
	private Application applicationRow;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		super.initUser();
		if (this.applicationsResult == null) {
			return Index.class;
		}
		return null;
	}

	@Inject
	private BeanModelSource beanModelSource;

	@Component
	private Grid applicationsGrid;

	@SetupRender
	public void setupGrid() {
		applicationsGrid.getSortModel().clear();
		applicationsGrid.getSortModel().updateSort("label");
	}

	/**
	 * @return : bean model
	 */

	public BeanModel<Application> getGridModel() {
		BeanModel<Application> model = this.beanModelSource.createDisplayModel(
				Application.class, this.getMessages());
		model.add("actions", null);
		model.include("label", "name", "actions");
		return model;
	}

	public List<Application> getApplicationsResult() {
		String temp = null;
		for (int i = 0; i < applicationsResult.size(); i++) {
			temp = applicationsResult.get(i).getLabel();
			temp = UnaccentLetter.unaccentString(temp);
			temp = temp.toUpperCase();
			applicationsResult.get(i).setLabel(temp);
		}
		return applicationsResult;
	}

	public void setApplicationsResult(List<Application> applicationsResult) {
		this.applicationsResult = applicationsResult;
	}

	public Integer getResultsNumber() {
		if (null == this.applicationsResult)
			return 0;
		return this.applicationsResult.size();
	}
}