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
package org.myec3.socle.webapp.pages.synchroman.role;

import java.util.List;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display the result of the search role page<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/synchroman/role/SearchResult.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 */

@SuppressWarnings("unused")
public class SearchResult extends AbstractPage {

	// Template attributes
	@Persist
	private List<Role> rolesResult;

	@Property
	private Integer rowIndex;

	@Property
	private Role roleRow;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		super.initUser();
		if (this.rolesResult == null) {
			return Index.class;
		}
		return null;
	}

	@Inject
	private BeanModelSource beanModelSource;

	@Component
	private Grid rolesGrid;

	@SetupRender
	public void setupGrid() {
		rolesGrid.getSortModel().clear();
		rolesGrid.getSortModel().updateSort("name");
	}

	/**
	 * @return : bean model
	 */

	public BeanModel<Role> getGridModel() {
		BeanModel<Role> model = this.beanModelSource.createDisplayModel(
				Role.class, this.getMessages());
		model.add("actions", null);
		PropertyConduit propCdtCustomer = this.propertyConduitSource
		.create(Role.class, "application.name");
		model.add("application", propCdtCustomer);
		model.include("label", "name", "application", "actions");
		return model;
	}

	public List<Role> getRolesResult() {
		String temp = null;
		for (int i = 0; i < rolesResult.size(); i++) {
			temp = rolesResult.get(i).getLabel();
			temp = UnaccentLetter.unaccentString(temp);
			temp = temp.toUpperCase();
			rolesResult.get(i).setLabel(temp);
		}
		return rolesResult;
	}

	public void setRolesResult(List<Role> rolesResult) {
		this.rolesResult = rolesResult;
	}

	public Integer getResultsNumber() {
		if (null == this.rolesResult)
			return 0;
		return this.rolesResult.size();
	}
}