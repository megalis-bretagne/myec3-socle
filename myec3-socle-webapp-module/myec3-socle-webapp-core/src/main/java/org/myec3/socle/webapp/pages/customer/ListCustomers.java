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
package org.myec3.socle.webapp.pages.customer;

import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to display the customers{@link Customer} list {@link Customer}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/customer/ListCustomers.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class ListCustomers extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("customerService")
	private CustomerService customerService;

	@SuppressWarnings("unused")
	@Property
	private List<AgentProfile> agentProfilesResult;

	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@SuppressWarnings("unused")
	@Property
	private Customer customerRow;

	@Inject
	private BeanModelSource beanModelSource;

	// Getters n Setters
	public List<Customer> getCustomerList() {
		return this.customerService.findAll();
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<Customer> getGridModel() {
		BeanModel<Customer> model = this.beanModelSource.createDisplayModel(
				Customer.class, this.getMessages());
		model.add("actions", null);
		model.include("label", "email", "actions");
		return model;
	}

	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public Integer getResultsNumber() {
		if (null == this.getCustomerList())
			return 0;
		return this.getCustomerList().size();
	}
}