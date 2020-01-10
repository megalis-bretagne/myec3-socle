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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Submit;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.StructureService;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used during creation company process {@link Company}<br />
 * 
 * It's the first step to create a company. In this step you inquire if the
 * company to create is<br />
 * <ul>
 * <li>A company based in France</li>
 * <li>A company based in other country</li>
 * </ul>
 * 
 * In case of your company is based in France you must fill the SIREN/SIRET of
 * the company.<br />
 * In the other case you must fill the DAS number etc... of the company.<br />
 * 
 * Corresponding tapestry template file is :<br />
 * src/main/resources/org/myec3/socle/webapp/pages/company/Siren.tml<br />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * 
 */
public class Siren {

	private static final Log logger = LogFactory.getLog(Siren.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Structure} objects
	 */
	@Inject
	@Service("structureService")
	private StructureService structureService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Inject
	@Service("companyService")
	private CompanyService companyService;

	@Inject
	private Messages messages;

	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Property
	private Company company;

	@Component(id = "modification_form")
	private Form formFrenchCompany;

	@SuppressWarnings("unused")
	@Component(id = "modification_foreign_form")
	private Form formForeignCompany;

	@SuppressWarnings("unused")
	@Component
	private Submit submitFrenchCompany;

	@SuppressWarnings("unused")
	@Component
	private Submit submitForeignCompany;

	// Second step to create a company
	@InjectPage
	private InitCompany createPage;

	@Inject
	private Request request;

	@Property
	@Persist
	private Boolean isLocHallesTheme;

	// Form events
	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		this.company = new Company();

		if ((this.isLocHallesTheme == null)
				&& ("lochalles").equals(request.getParameter("theme"))) {
			this.isLocHallesTheme = Boolean.TRUE;
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		this.createPage.setPersonHolders(null);

		if (!this.company.getForeignIdentifier()) {
			try {
				this.company.setSiren(this.company.getSiren());
				this.company.setLabel("");
				this.company.setNic(this.company.getNic());
				if (this.company.getAddress() == null) {
					this.company.setAddress(new Address());
				}
				this.company.getAddress().setCountry(Country.FR);
				this.company.getAddress().setPostalCode("");
				this.createPage.setCompany(this.company);
			} catch (Exception e) {
				this.errorMessage = this.messages
						.get("recording-error-message");
				logger.error("error during succes siren", e);
				return null;
			}
		} else {
			try {
				this.company.setNationalID(this.company.getNationalID());
				this.company.setLabel("");
				if (this.company.getAddress() == null) {
					this.company.setAddress(new Address());
				}
				this.createPage.setCompany(this.company);
			} catch (Exception e) {
				this.errorMessage = this.messages
						.get("recording-error-message");
				logger.error("error during succes siren", e);
				return null;
			}
		}
		this.createPage.setSuccessMessage(this.messages
				.get("recording-success-message"));
		this.createPage.setIsLocHallesTheme(this.isLocHallesTheme);

		return this.createPage;
	}

	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {
		if (this.formFrenchCompany != null) {
			if (null != this.company.getSiren()) {
				if (!this.structureService
						.isSirenValid(this.company.getSiren())) {
					this.formFrenchCompany.recordError(this.messages
							.get("invalid-siren-error"));
				} else {
					if (null != this.company.getNic()) {
						if (!this.companyService.isSiretValid(
								this.company.getSiren(), this.company.getNic())) {
							this.formFrenchCompany.recordError(this.messages
									.get("invalid-siret-error"));
						}
					}
				}
			}
		}
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return Index.class;
	}

	void onSelectedFromSubmitFrenchCompany() {
		this.company.setForeignIdentifier(Boolean.FALSE);
	}

	void onSelectedFromSubmitForeignCompany() {
		this.company.setForeignIdentifier(Boolean.TRUE);
	}
}
