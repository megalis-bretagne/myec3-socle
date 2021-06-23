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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Page used to search an organism{@link Organism}<br />
 * Redirect to SearchResult page if at least one organism has been found<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/Search.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public class Search extends AbstractPage {

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String infoMessage;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Customer} objects
	 */
	@Inject
	@Named("customerService")
	private CustomerService customerService;

	@Component(id = "organism_search_form")
	private Form organismSearchForm;

	@Property
	private String searchLabel;

	@Property
	private String searchSiren;

	@Property
	private String searchPostalCode;

	@Property
	private String searchCity;

	@Property
	private Customer searchCustomer;

	@Property
	private List<Organism> organismsResult;

	@InjectPage
	private SearchResult searchResultPage;

	// Activation n Passivation
	@OnEvent(EventConstants.ACTIVATE)
	public void activation() {
		super.initUser();
	}

	// Form
	@OnEvent(component = "organism_search_form", value = EventConstants.SUCCESS)
	public Object onSuccess() {
		// If logged user is not the technical admin we must search only
		// organisms associated at the customer of logged user
		if (BooleanUtils.isFalse(this.getIsTechnicalAdmin())) {
			this.searchCustomer = this.getCustomerOfLoggedProfile();
		}

		if (!canLaunchSearch()) {
			this.errorMessage = this.getMessages().get("search-error");
			return null;
		}

		this.organismsResult = this.organismService.findAllByCriteria(
				searchLabel, searchSiren, searchPostalCode, searchCity,
				searchCustomer);
		if (this.organismsResult.isEmpty()) {
			this.infoMessage = this.getMessages().get("empty-result-message");
			return null;
		}
		this.searchResultPage.setOrganismsResult(organismsResult);
		return SearchResult.class;
	}

	/**
	 * list for select Customers
	 */
	public Map<Customer, String> getCustomersList() {
		List<Customer> customersList = this.customerService.findAll();
		Map<Customer, String> customersMap = new LinkedHashMap<>();
		for (Customer customer : customersList) {
			customersMap.put(customer, customer.getLabel());
		}
		return customersMap;
	}

	public ValueEncoder<Customer> getCustomerEncoder() {
		return new GenericListEncoder<>(this.customerService.findAll());

	}

	/**
	 * Check if user can launch search.
	 * Check all field are not null or empty
	 *
	 * @return true if can search
	 */
	private boolean canLaunchSearch() {
		return !(StringUtils.isEmpty(searchLabel) &&
				StringUtils.isEmpty(searchSiren) &&
				StringUtils.isEmpty(searchPostalCode) &&
				StringUtils.isEmpty(searchCity));
	}
}
