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
package org.myec3.socle.webapp.pages.company.establishment;

import java.util.List;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to list all establishments{@link Establishment} of a company
 * {@link Company}<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/establishment/
 * ListEstablishments.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
public class ListEstablishments extends AbstractPage {

	private static final Log logger = LogFactory.getLog(ListEstablishments.class);

	// Services

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Inject
	@Named("companyService")
	private CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Establishment} objects
	 */
	@Inject
	@Named("establishmentService")
	private EstablishmentService establishmentService;

	// Template attributes

	private Company company;

	@Property
	private List<Establishment> establishmentsList;

	@Property
	private Integer rowIndex;

	@Property
	private Establishment establishmentRow;

	@Inject
	private PropertyConduitSource propertyConduitSource;

	@Inject
	private BeanModelSource beanModelSource;

	@InjectPage
	private org.myec3.socle.webapp.pages.company.establishment.Create establishmentPage;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation(Long id) {
		super.initUser();
		this.company = this.companyService.findOne(id);
		this.establishmentsList = this.establishmentService.findAllEstablishmentsByCompany(this.company);
		if (this.company == null) {
			return Boolean.FALSE;
		}

		// Check if loggedUser can access to this company
		return this.hasRights(this.company);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		if (this.company != null) {
			return this.company.getId();
		}
		return null;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object Activation() {
		return Index.class;
	}

	@OnEvent(value = "action", component = "createEstablishment")
	public Object createEstablishment(Long id) {

		logger.info("Redirecting to establishment creation from BO");
		this.establishmentPage.setFromCompanyCreation(false);
		this.establishmentPage.setCompanyIdFromBo(id);
		return this.establishmentPage;
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<Establishment> getGridModel() {
		BeanModel<Establishment> model = this.beanModelSource.createDisplayModel(Establishment.class,
				this.getMessages());

		PropertyConduit propCdtAttributePostalAddress = this.propertyConduitSource.create(Establishment.class,
				"address.postalAddress");
		PropertyConduit propCdtAttributePostalCode = this.propertyConduitSource.create(Establishment.class,
				"address.postalCode");
		PropertyConduit propCdtAttributeCity = this.propertyConduitSource.create(Establishment.class, "address.city");

		model.add("postalAddress", propCdtAttributePostalAddress).sortable(false);
		model.add("postalCode", propCdtAttributePostalCode).sortable(true);
		model.add("city", propCdtAttributeCity).sortable(true);
		model.include("label", "nic", "postalAddress", "postalCode", "city", "email");
		return model;
	}

	// Getters and Setters
	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public Integer getResultsNumber() {
		return (this.establishmentsList == null) ? 0 : this.establishmentsList.size();
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Boolean getIsHeadOfficeDisplay() {
		if (this.establishmentRow.getIsHeadOffice().equals(Boolean.TRUE)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}
