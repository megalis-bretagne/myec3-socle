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
package org.myec3.socle.webapp.pages.organism;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
import org.myec3.socle.core.domain.model.OrganismStatus;
import org.myec3.socle.core.domain.model.enums.OrganismMemberStatus;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display the result of the search organism page<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/SearchResult.tml<br
 * />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public class SearchResult extends AbstractPage {

	// Template attributes
	@Persist
	private List<Organism> organismsResult;

	@Property
	private Integer rowIndex;

	@Property
	private Organism organismRow;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		super.initUser();
		if (this.organismsResult == null) {
			return Index.class;
		}
		return null;
	}

	@Inject
	private BeanModelSource beanModelSource;

	@Component
	private Grid organismsGrid;

	@SetupRender
	public void setupGrid() {
		organismsGrid.getSortModel().clear();
		organismsGrid.getSortModel().updateSort("label");
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<Organism> getGridModel() {
		BeanModel<Organism> model = this.beanModelSource.createDisplayModel(
				Organism.class, this.getMessages());
		PropertyConduit propCdtCustomer = this.propertyConduitSource
				.create(Organism.class, "customer.label");
		model.add("customer", propCdtCustomer);
		PropertyConduit propCdtAttributeCode = this.propertyConduitSource
				.create(Organism.class, "address.postalCode");
		model.add("postalCode", propCdtAttributeCode);
		PropertyConduit propCdtAttributeCity = this.propertyConduitSource
				.create(Organism.class, "address.city");
		model.add("city", propCdtAttributeCity);
		model.add("actions", null);

		// Add customer if loggedUser is a technical administrator
		if (this.getIsTechnicalAdmin()) {
			model.include("customer", "member", "label", "siren", "phone", "postalCode",
					"city", "actions");
		} else {
			model.include("member", "label", "siren", "phone", "postalCode",
					"city", "actions");
		}
		return model;
	}

	public List<Organism> getOrganismsResult() {
		String temp = null;
		for (int i = 0; i < organismsResult.size(); i++) {
			temp = organismsResult.get(i).getLabel();
			temp = UnaccentLetter.unaccentString(temp);
			temp = temp.toUpperCase();
			organismsResult.get(i).setLabel(temp);
		}
		return organismsResult;
	}

	public void setOrganismsResult(List<Organism> organismsResult) {
		this.organismsResult = organismsResult;
	}

	public Integer getResultsNumber() {
		if (null == this.organismsResult)
			return 0;
		return this.organismsResult.size();
	}

	public Boolean getIsAdherent() {
		if (null != this.organismRow.getOrganismStatus() && !this.organismRow.getOrganismStatus().isEmpty()) {
			Set<OrganismStatus> organismStatuses = organismRow.getOrganismStatus();
			Date date = organismStatuses.iterator().next().getDate();
			String label = organismStatuses.iterator().next().getStatus().getLabel();
			for (OrganismStatus organismStatus : organismStatuses) {
				Date date_tmp = organismStatus.getDate();
				if (date_tmp.after(date)) {
					date = date_tmp;
					label = organismStatus.getStatus().getLabel();
				}
			}
      if (label.equals(OrganismMemberStatus.ADHERENT.getLabel())) {
        return Boolean.TRUE;
      }
		}
		return Boolean.FALSE;
	}
}
