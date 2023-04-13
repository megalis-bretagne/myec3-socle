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
package org.myec3.socle.webapp.pages.user;

import java.util.HashMap;
import java.util.List;

import org.apache.tapestry5.beanmodel.PropertyConduit;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.beanmodel.services.PropertyConduitSource;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.ProfileSearch;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to display the result of user search page<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/user/SearchResult.tml<br />
 * 
 * This page is only visible by technical administrator (role SUPER_ADMIN).<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class SearchResult extends AbstractPage {

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	@Component(id = "profileGrid")
	private Grid profileGrid;

	@Property
	private Profile profileRow;
	
	@Property
	private ProfileSearch profileSearchRow;

	@Persist
	private List<Profile> profileResult;
	
	@Persist
	private List<ProfileSearch> profileSearchResult;
	
	@Persist
	private HashMap<String,String> profileResultWithStructure;

	@SetupRender
	public void setupGrid() {
		profileGrid.getSortModel().clear();
	}

	/**
	 * @return : bean model, used for e-bourgogne
	 */
	public BeanModel<ProfileSearch> getProfileGridModel() {
		BeanModel<ProfileSearch> model = this.beanModelSource.createDisplayModel(
			ProfileSearch.class, this.getMessages());

		PropertyConduit propCdtAttributeSviProfile = this.propertyConduitSource.create(ProfileSearch.class, "searchProfile.user.sviProfile.id");
		PropertyConduit propCdtAttributeUse = this.propertyConduitSource.create(ProfileSearch.class, "searchProfile.user.lastname");
		PropertyConduit propCdtAttributeEmail = this.propertyConduitSource.create(ProfileSearch.class, "searchProfile.email");
		PropertyConduit propCdtAttributeUserName = this.propertyConduitSource.create(ProfileSearch.class, "searchProfile.user.username");
		PropertyConduit propCdtAttributeStructure = this.propertyConduitSource.create(ProfileSearch.class, "searchStructure.label");


		model.add("sviProfile", propCdtAttributeSviProfile).sortable(true);
		model.get("sviProfile").label("ID Tel").sortable(true);
		model.add("user", propCdtAttributeUse).sortable(true);
		model.add("email", propCdtAttributeEmail).sortable(true);
		model.add("login", propCdtAttributeUserName).sortable(true);
		model.add("structure", propCdtAttributeStructure).sortable(true);
		model.add("actions", null);		
		model.include("sviProfile", "user", "email", "login", "structure", "actions");
		return model;
	}
	
	/**
	 * @return : bean model, used for Megalis
	 *
	 * CODE MORT ?????
	 */
	public BeanModel<ProfileSearch> getMegalisProfileGridModel() {
		BeanModel<ProfileSearch> model = this.beanModelSource.createDisplayModel(
			ProfileSearch.class, this.getMessages());
		model.add("user", null);
		model.add("login", null);
		model.add("email", null);
		model.add("structure", null);
		model.add("actions", null);
		model.include("user", "email", "login", "structure", "actions");
		return model;
	}
	
	public List<ProfileSearch> getProfileSearchResult(){
	    return this.profileSearchResult;
	}
	
	public void setProfileSearchResult(List<ProfileSearch> profileSearchResult){
	    this.profileSearchResult = profileSearchResult;
	}

	/**
	 * @return the number of rows
	 */
	public Integer getResultsNumber() {
		if (null == this.profileSearchResult)
			return 0;
		return this.profileSearchResult.size();
	}

	/**
	 * @return true if current row is an agent profile
	 */
	@Override
	public Boolean getIsAgent() {
		if (this.profileSearchRow.getSearchProfile() instanceof AgentProfile) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
