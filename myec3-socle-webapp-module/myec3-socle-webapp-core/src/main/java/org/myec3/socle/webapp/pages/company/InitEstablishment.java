/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.company;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.AdministrativeStateValue;
import org.myec3.socle.core.domain.model.enums.CompanyNafCode;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Page used during creation company process {@link Company}<br />
 *
 * It's the third step to create a new company. In this step your must
 * fill<br />
 * employee's establishment attributes.<br />
 *
 * Corresponding tapestry template file is : src/main/resources/org/myec3/socle
 * /webapp/pages/company/Establishment.tml
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
//TOdo : remove this file in some close future !
@Deprecated
@Import(library = { "context:/static/js/nic.js" })
public class InitEstablishment extends AbstractPage {

	private static Log logger = LogFactory.getLog(InitEstablishment.class);

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Inject
	@Service("companyService")
	private CompanyService companyService;

	@Inject
	@Named("establishmentService")
	private EstablishmentService establishmentService;

	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Persist
	private Company company;

	@Property
	private Establishment establishment;

	@Persist
	private Company saveCompany;

	@Persist(PersistenceConstants.FLASH)
	private EmployeeProfile employeeProfile;

	@Persist(PersistenceConstants.FLASH)
	private Company[] companiesList;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private List<Person> persons;

	// Last step to create a company
	@InjectPage
	private Create createPage;

	private boolean submit;

	@Persist
	private Boolean searchEstablishment;

	private Boolean createHeadOffice = Boolean.FALSE;

	@Persist
	private Boolean cancelButton;

	private Boolean establishmentAlreadyExist;

	@Persist
	private Boolean isLocHallesTheme;

	private Boolean isHeadOfficeInList;

	private Boolean alreadyExist = Boolean.FALSE;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private List<Establishment> myEstablishments;

	private Establishment myEstablishment;

	@Property
	private BeanModel<Establishment> myModel;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private Messages messages;

	private CompanyWSinfo mpsWS = new MpsWsClient();

	@Property
	private String radioSelectedValue;

	@Property
	private AdministrativeStateValue radioAdminStateValue;

	@Component(id = "modification_form")
	private Form form;

	public void setIsLocHallesTheme(Boolean isLocHallesTheme) {
		this.isLocHallesTheme = isLocHallesTheme;
	}

	public Boolean getIsLocHallesTheme() {
		return isLocHallesTheme;
	}

	void setupRender() {

		// Used to render the Establishment Array
		myModel = beanModelSource.createDisplayModel(Establishment.class, messages);
		myModel.add("postalAddress", null);
		myModel.add("postalCode", null);
		myModel.add("city", null);
		myModel.add("select", null);
		myModel.include("label", "nic", "postalAddress", "postalCode", "city", "select");
		myModel.get("label").label("Nom de l'établissement");
		myModel.get("nic").label("Code NIC");
		myModel.get("select").label("Sélectionner");

		if (this.company.getId() != null) {
			this.companyService.populateCollections(company);

			// If we have establishments, populate the array
			if (this.company.getEstablishments().size() > 0) {
				myEstablishments = establishmentService.findAllEstablishmentsByCompany(this.company);
				for (Establishment isHeadOffice : myEstablishments) {
					if (isHeadOffice.getIsHeadOffice().equals(Boolean.TRUE)) {
						this.isHeadOfficeInList = Boolean.TRUE;
						break;
					}
				}
			}
		}
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		this.errorMessage = null;
		this.cancelButton = false;
		this.searchEstablishment = false;

		// init employeeProfile
		this.employeeProfile = new EmployeeProfile();
		this.employeeProfile.setCreatedUserId(this.getUserIdLogged());

		if (this.establishment == null) {
			this.establishment = new Establishment();
		} else if (this.establishment.getSiret() != null) {
			if (!(this.establishment.getSiret().substring(0, 9).equals(this.saveCompany.getSiren()))) {
				this.establishment = new Establishment();
			}
		}

		// tapestry hack to save the company
		if (saveCompany != null) {
			this.company = saveCompany;
		} else {
			saveCompany = this.company;
		}

		if (this.company != null) {

			if (this.company.getId() != null) {
				this.companyService.populateCollections(company);
			}

			logger.debug("Is Foreign Company ? " + this.company.getForeignIdentifier());
			if (!this.company.getForeignIdentifier()) {

				if (this.company.getNic() != null || this.establishment.getNic() != null) {
					this.establishment.setSiret(this.company.getSiren() + this.company.getNic());
					myEstablishments = this.company.getEstablishments();
					for (Establishment isHeadOffice : myEstablishments) {

						if (!(isHeadOffice.getNic().equals(this.company.getNic()))) {
							this.establishment.setNic(this.company.getNic());
						} else {
							this.alreadyExist = Boolean.TRUE;
							this.establishment.setNic(this.company.getNic());
						}

						if (this.establishment.getNic() != null
								&& this.establishment.getNic().equals(isHeadOffice.getNic())
								&& this.establishment.getNic().equals(this.company.getNic())) {
							this.company.setNic(this.establishment.getNic());
						}

						// check that establishment not already exist
						if (this.establishment.getNic() != null) {
							if (isHeadOffice.getSiret().equals(this.company.getSiren() + this.company.getNic())) {
								if ((this.alreadyExist.equals(Boolean.TRUE))) {
									logger.info("Establishment already exist : " + this.company.getSiren()
											+ this.company.getNic());
									this.form.recordError(
											this.getMessages().get("establishmentAlreadyExist-error-message"));
									this.establishment.setSiret(null);
									Address resetAddress = new Address();
									this.establishment.setAddress(resetAddress);
									return null;
								}
							}
						}

					}
					if (this.establishment.getNic() != null || this.company.getNic() != null) {
						if (!this.companyService.isSiretValid(this.company.getSiren(), this.company.getNic())) {
							this.form.recordError(this.messages.get("invalid-siret-error"));
							Address resetAddress = new Address();
							this.establishment.setAddress(resetAddress);
							this.establishment.setSiret(null);
							return null;
						} else {

							if (this.establishment.getNic() == null) {
								this.establishment.setNic(this.company.getNic());
								this.establishment.setSiret(this.company.getSiren() + this.company.getNic());
							}

							try {
								this.establishment = this.mpsWS
										.getEstablishment(this.company.getSiren() + this.company.getNic());
							} catch (Exception e) {
								Address resetAddress = new Address();
								this.establishment.setAddress(resetAddress);
								this.errorMessage = this.getMessages().get("mpsEstablishment-error-message");
								return null;
							}
						}
					}
					logger.debug("Is the establishment head office ? : " + this.establishment.getIsHeadOffice());
					establishment.setCompany(this.company);
				}
			} else {

				if (this.establishment.getAddress() == null) {
					this.establishment.setAddress(new Address());
				}
				establishment.setCompany(this.company);
			}

			if (this.establishment.getAddress() == null) {
				this.establishment.setAddress(new Address());
			}
			this.persons = this.saveCompany.getResponsibles();
			this.setEmployeeAdress();

		} else {
			return Siren.class;
		}

		return Boolean.TRUE;

	}

	/**
	 * validate form
	 */
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {

		if (this.radioSelectedValue == null) {
			if (!this.searchEstablishment && !this.cancelButton) {
				if (!this.createHeadOffice) {
					if (this.company.getForeignIdentifier() != Boolean.TRUE) {
						if (!this.companyService.isSiretValid(this.company.getSiren(), this.establishment.getNic())) {
							this.form.recordError(this.messages.get("invalid-siret-error"));
							Address resetAddress = new Address();
							this.establishment.setAddress(resetAddress);
						}
					}

					if (null == this.establishment.getLabel()) {
						this.form.recordError(this.messages.get("label-required-message"));
					}

					if (null == this.establishment.getEmail()) {
						this.form.recordError(this.messages.get("email-required-message"));
					}

					if (null == this.establishment.getPhone()) {
						this.form.recordError(this.messages.get("phone-required-message"));
					}

					if (null == this.establishment.getAddress().getPostalAddress()) {
						this.form.recordError(this.messages.get("postalAddress-required-message"));
					}

					if (null == this.establishment.getAddress().getPostalCode()) {
						this.form.recordError(this.messages.get("postalCode-required-message"));
					}

					if (null == this.establishment.getAddress().getCity()) {
						this.form.recordError(this.messages.get("city-required-message"));
					}

					if (null == this.establishment.getApeCode()) {
						this.form.recordError(this.messages.get("apeCode-required-message"));
					}

					if (this.company.getForeignIdentifier()) {

						if (null == this.establishment.getAddress().getPostalAddress()) {
							this.form.recordError(this.messages.get("postalAddress-required-message"));
						}

						if (null == this.establishment.getAddress().getPostalAddress()) {
							this.form.recordError(this.messages.get("postalCode-required-message"));
						}

						if (null == this.establishment.getAddress().getCity()) {
							this.form.recordError(this.messages.get("city-required-message"));
						}
					}
				}
			}
		}
		this.cancelButton = false;
		this.searchEstablishment = false;
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			if (submit) {

				logger.debug("Company NIC : " + company.getNic());
				this.createPage.setCompany(this.company);
				this.createPage.setEmployeeProfile(this.employeeProfile);
				this.createPage.setIsLocHallesTheme(this.isLocHallesTheme);

				// Case company/establishment creation
				if (this.radioSelectedValue == null) {
					this.createPage.setEstablishment(this.establishment);
					this.createPage.setSuccessMessage(this.getMessages().get("recording-success-message"));

					// If the establishment is not the head office
					if ((this.establishment.getIsHeadOffice() != Boolean.TRUE)
							&& (this.company.getForeignIdentifier() != Boolean.TRUE)) {

						myEstablishments = this.company.getEstablishments();
						for (Establishment isHeadOffice : myEstablishments) {

							// check that the head office not already created
							if (isHeadOffice.getIsHeadOffice().equals(Boolean.TRUE)) {
								this.company.setSiretHeadOffice(this.company.getSiren() + isHeadOffice.getNic());
								this.isHeadOfficeInList = Boolean.TRUE;
							}

							// check the the establishment does not already exist
							if (isHeadOffice.getNic().equals(establishment.getNic())) {
								this.establishmentAlreadyExist = Boolean.TRUE;
							}
						}

						// Head office does not exist, create it
						if (this.isHeadOfficeInList != Boolean.TRUE && this.company.getEstablishments().size() == 0) {
							logger.info("headOffice SIRET : " + this.company.getSiretHeadOffice());
							Establishment headOfficeEstablishment = this.mpsWS
									.getEstablishment(this.company.getSiretHeadOffice());
							logger.info("HeadOffice : " + headOfficeEstablishment);
							headOfficeEstablishment.setName(
									company.getLabel() + MyEc3Constants.HEAD_OFFICE_ESTABLISHMENT_LABEL_POSTFIX);
							headOfficeEstablishment.setCompany(this.company);
							headOfficeEstablishment.setEmail(this.company.getEmail());

							if (headOfficeEstablishment.getAddress().getCanton() == null) {
								headOfficeEstablishment.getAddress().setCanton("Aucun");
							}

							this.company.setNic(headOfficeEstablishment.getNic());
							this.company.addEstablishment(headOfficeEstablishment);
							logger.info("Company Establishements : " + company.getEstablishments());
						}
						logger.info("Company Establishements : " + company.getEstablishments());
						this.company.setResponsibles(this.persons);

					}
				} else {

					// we picked an already existing establishment from the Array
					this.company.setResponsibles(this.persons);
					Establishment establishmentToSet = new Establishment();
					for (Establishment findEstablishment : myEstablishments) {

						// Find it with it's nic
						if (findEstablishment.getNic().equals(radioSelectedValue)) {
							establishmentToSet = findEstablishment;
							break;
						}
					}
					this.createPage.setEstablishment(establishmentToSet);

				}

				if (establishmentAlreadyExist != Boolean.TRUE) {
					this.createPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
					return this.createPage;
				} else {
					// if establishment already exist, display error
					this.form.recordError(this.getMessages().get("duplicateEstablishment-error-message"));
					return null;
				}
			} else if (searchEstablishment) {

				return this;
			}

			// Search for the headoffice by pressing the correct button
			else if (createHeadOffice) {
				logger.info("In createHeadOffice");
				establishment.setCompany(company);
				establishment.setSiret(this.company.getSiretHeadOffice());

				// Temporary use the head office nic
				this.establishment.setNic(this.company.getSiretHeadOffice().substring(9, 14));
				this.company.setNic(this.company.getSiretHeadOffice().substring(9, 14));
				this.saveCompany.setNic(this.company.getSiretHeadOffice().substring(9, 14));
				return this;
			} else {
				this.setCompany(this.company);
				return null;
			}
		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}
	}

	private void setEmployeeAdress() {
		this.employeeProfile.setAddress(new Address());
		this.employeeProfile.getAddress().setPostalAddress(this.saveCompany.getAddress().getPostalAddress());
		this.employeeProfile.getAddress().setPostalCode(this.saveCompany.getAddress().getPostalCode());
		this.employeeProfile.getAddress().setCity(this.saveCompany.getAddress().getCity());
		if (this.employeeProfile.getAddress().getCountry() == null) {
			if (null != this.saveCompany.getAddress().getCountry()) {
				this.employeeProfile.getAddress().setCountry(this.saveCompany.getAddress().getCountry());
			} else {
				this.employeeProfile.getAddress().setCountry(Country.FR);
			}
		}
		if (this.employeeProfile.getAddress() == null) {
			this.employeeProfile.setAddress(new Address());
			this.employeeProfile.getAddress().setCountry(Country.FR);
		}
	}

	public Boolean getHeadOfficeDisplay() {

		// check if establishment exist or if actual establishment is head office
		if (this.establishment.getIsHeadOffice() == null) {
			this.establishment.setIsHeadOffice(Boolean.FALSE);
		}

		if ((this.establishment.getIsHeadOffice().equals(Boolean.TRUE)) || (this.isHeadOfficeInList == Boolean.TRUE)) {
			return Boolean.TRUE;

		} else {
			return Boolean.FALSE;
		}

	}

	public Boolean getApeCodeDisplay() {
		if (this.establishment.getApeCode() != null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}

	}

	public Boolean getPostalAddressDisplay() {
		if (this.establishment.getAddress().getPostalAddress() != null
				&& this.establishment.getAddress().getPostalAddress() != "") {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getPostalCodeDisplay() {
		if (this.establishment.getAddress().getPostalCode() != null
				&& this.establishment.getAddress().getPostalCode() != "") {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}

	}

	public Boolean getCityDisplay() {
		if (this.establishment.getAddress().getCity() != null && this.establishment.getAddress().getCity() != "") {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getArrayInitializeDisplay() {
		if (this.company.getEstablishments().isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	public Map<String, String> getListOfCompanies() {
		Map<String, String> companies = new LinkedHashMap<String, String>();
		if (this.companiesList != null) {
			for (Company company : this.companiesList) {
				StringBuffer label = new StringBuffer();
				label.append(company.getAddress().getCity());
				label.append(" ");
				label.append(company.getAddress().getPostalCode());
				label.append(" ");
				label.append(company.getAddress().getPostalAddress());
				companies.put(company.getNic(), label.toString());
			}
		}
		companies.put("00000", this.getMessages().get("new-establishement"));
		return companies;
	}

	public ValueEncoder<String> getCompanyEncoder() {
		Company[] comp = this.companiesList;
		List<String> cmps = new ArrayList<String>();
		if (comp != null) {
			for (Company company : comp) {
				cmps.add(company.getNic());
			}
		}
		return new GenericListEncoder<String>(cmps);
	}

	public Company getCompany() {
		return saveCompany;
	}

	public void setCompany(Company company) {
		this.saveCompany = company;
	}

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	void onSelectedFromUpdate() {
		this.submit = false;
	}

	void onSelectedFromSubmit() {
		this.submit = true;
	}

	void onSelectedFromSearchEstablishment() {
		this.searchEstablishment = true;
		establishment.setCompany(company);
		establishment.setSiret(this.company.getSiren() + this.establishment.getNic());
		this.company.setNic(this.establishment.getNic());
		if (!this.companyService.isSiretValid(this.company.getSiren(), this.establishment.getNic())) {
			Address resetAddress = new Address();
			this.establishment.setAddress(resetAddress);
		}
		try {
			this.mpsWS.updateEstablishmentInfo(establishment, company);
		} catch (Exception e) {
			Address resetAddress = new Address();
			this.establishment.setAddress(resetAddress);
			this.errorMessage = this.getMessages().get("mpsEstablishment-error-message");
		}
	}

	void onSelectedFromCreateHeadOffice() {
		this.createHeadOffice = true;
	}

	void onSelectedFromCancelButton() {
		this.establishment = new Establishment();
		this.company.setNic(null);
		this.cancelButton = true;
		this.radioSelectedValue = null;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public Establishment getMyEstablishment() {
		return myEstablishment;
	}

	public void setMyEstablishment(Establishment myEstablishment) {
		this.myEstablishment = myEstablishment;
	}

	public AdministrativeStateValue getSTATUS_ACTIVE() {
		return AdministrativeStateValue.STATUS_ACTIVE;
	}

	public AdministrativeStateValue getSTATUS_CEASED() {
		return AdministrativeStateValue.STATUS_CEASED;
	}

	public ValueEncoder<CompanyNafCode> getApeCodeEncoder() {
		CompanyNafCode[] comp = CompanyNafCode.values();
		List<CompanyNafCode> cmps = new ArrayList<CompanyNafCode>();
		for (CompanyNafCode companyNafCode : comp) {
			cmps.add(companyNafCode);
		}
		return new GenericListEncoder<CompanyNafCode>(cmps);
	}

	public String getApeNaflabelValue(String key) {
		return this.messages.get(key);
	}

	public Map<CompanyNafCode, String> getListOfCompanyApeCode() {
		Map<CompanyNafCode, String> companies = new LinkedHashMap<CompanyNafCode, String>();
		CompanyNafCode[] companiesList = CompanyNafCode.values();
		for (CompanyNafCode companyNafCode : companiesList) {
			companies.put(companyNafCode, companyNafCode + " - " + this.getApeNaflabelValue(companyNafCode.name()));
		}

		return companies;
	}
}
