/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p/>
 * This file is part of MyEc3.
 * <p/>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p/>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.company.establishment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.AdministrativeStateValue;
import org.myec3.socle.core.domain.model.enums.CompanyNafCode;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;

import javax.inject.Named;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Page used to create an establishment {@link Establishment}.<br />
 * Both via company {@link Establishment} creation and edition.
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/company/establishment/Create.tml
 *
 * @author Matthieu GASPARD <matthieu.gaspard@worldline.com>
 */

public class Create extends AbstractPage {

	private static final Log logger = LogFactory.getLog(Create.class);

	@SuppressWarnings("unused")
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String errorMessage;

	@Inject
	@Named("establishmentService")
	private EstablishmentService establishmentService;

	@Inject
	@Named("companyService")
	private CompanyService companyService;

	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	@Inject
	@Named("inseeGeoCodeService")
	private InseeGeoCodeService inseeGeoCodeService;

	@Inject
	@Named("inseeRegionService")
	private InseeRegionService inseeRegionService;

	@Inject
	@Named("inseeCountyService")
	private InseeCountyService inseeCountyService;

	@Inject
	@Named("inseeBoroughService")
	private InseeBoroughService inseeBoroughService;

	@Inject
	@Named("inseeCantonService")
	private InseeCantonService inseeCantonService;

	@Property
	@Persist
	private Establishment establishment;

	@Persist
	private Company company;

	@Property
	private CompanyNafCode companyNafCode;

	@Property
	@Persist
	private String inseeCode;

	@Property
	@Persist
	private String inseeRegionLabel;

	@Property
	@Persist
	private String inseeCountyLabel;

	@Property
	@Persist
	private String inseeBoroughLabel;

	@Property
	@Persist
	private String inseeCantonLabel;

	@Property
	@Persist
	private String inseeTownLabel;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private List<Establishment> myEstablishments;

	private Establishment myEstablishment;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String establishmentNic;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private Messages messages;

	private CompanyWSinfo mpsWS = new MpsWsClient();

	@Component(id = "modification_form")
	private Form form;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist
	private Boolean isLocHallesTheme;

	@Property
	private Long radioSelectedValue;

	@Persist
	private String nic;

	private Boolean submit;

	// Used to specify if from company creation or not !
	@Persist
	private boolean fromCompanyCreation;

	private Boolean headOfficeInList = Boolean.FALSE;

	@Persist
	private Boolean searchEstablishment;

	@Persist
	private Boolean isMpsError;

	@Persist
	private Long companyIdFromBo;

	@InjectPage
	@Property
	private org.myec3.socle.webapp.pages.company.establishment.View viewEstablishmentsPage;

	// Last step to create a company
	@InjectPage
	private org.myec3.socle.webapp.pages.company.Create createPage;

	@Persist(PersistenceConstants.FLASH)
	private boolean createHeadOffice;

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		if (this.companyIdFromBo != null) {
			return this.onActivate(companyIdFromBo);
		}
		// super.initUser();
		return this.onActivate(null);
	}

	/**
	 * @param id : company id
	 * @return : current page if logged user has rights to access at this page else
	 *         return to Index
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		// reset MpsError to false, see https://jira.worldline.com/browse/MTSMAXIGP-454
		this.isMpsError = Boolean.FALSE;
		logger.debug("Entering activate with following id : " + id);
		logger.debug("From company creation ? " + fromCompanyCreation);

		// Retrieve the company by it's ID if not provided from CompanyCreation
		// companyIdFromBo given from listEstablishments can be different from id
		// we set it back to avoid having previous company
		if (this.companyIdFromBo != null && this.companyIdFromBo != id) {
			logger.debug("Changing id from " + id + " to " + this.companyIdFromBo);
			id = this.companyIdFromBo;
		}

		if (id != null) {
			if ((this.company == null) || (this.company.getId() == null) || (!(this.company.getId().equals(id)))) {
				this.company = this.companyService.findOne(id);
			}
		}

		if (company != null) {
			// We've got a company
			if (this.getNic() != null ) {
				this.establishmentNic = this.nic;
				this.initEstablishment();
				this.onSelectedFromSearchEstablishment();
				this.setNic(null);
			}

			if ((this.establishment == null) || company.getForeignIdentifier() || (this.establishment != null && this.establishment.getSiret() != null
					&& (!(this.establishment.getSiret().substring(0, 9).equals(this.company.getSiren()))))) {
				logger.trace("No establishment provided.");
				// Initiate everything needed for establishment creation !
				this.initEstablishment();
			} else if (this.establishmentNic == null && this.establishment != null
					&& this.establishment.getSiret() != null
					&& (this.establishment.getSiret().substring(0, 9).equals(this.company.getSiren()))) {
				logger.trace("No establishment provided.");
				// Initiate everything needed for establishment creation !
				// Same company but new creation procedure !
				this.initEstablishment();
			}

			if (this.establishment != null && this.establishment.getNic() != null) {
				if (!(this.establishment.getNic().isEmpty())) {
					this.establishmentNic = this.establishment.getNic();
				}
			}
		} else {
			return Boolean.FALSE;
		}
		if (fromCompanyCreation) {
			// Everyone can come here from there :) Words to live by.
			return Boolean.TRUE;
		}
		return this.hasRights(this.company);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.company != null) ? this.company.getId() : null;
	}

	// Form events
	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			if (this.submit != null && this.submit) {

				logger.info("SIRET : " + this.establishment.getSiret());
				logger.info("Company NIC : " + this.company.getNic());

				this.establishment.setName(this.establishment.getLabel());

				if (this.company.getForeignIdentifier()) {
					this.establishment.setForeignIdentifier(Boolean.TRUE);
					this.establishment.setNationalID(this.company.getNationalID());
					this.establishment.setAdministrativeState(this.company.getAdministrativeState());
					this.establishment.setIsHeadOffice(Boolean.FALSE);
				} else {
					this.establishment.setForeignIdentifier(Boolean.FALSE);
				}

				if (this.establishment.getDiffusableInformations() == null) {
					this.establishment.setDiffusableInformations(Boolean.FALSE);
				}

				Date date = new Date();
				if (this.establishment.getAdministrativeState() == null) {
					AdministrativeState adminState = new AdministrativeState();
					adminState.setAdminStateLastUpdated(date);
					adminState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);
					this.establishment.setAdministrativeState(adminState);
				}

				// No Mps response ?
				if (this.establishment.getLastUpdate() == null) {
					this.establishment.setLastUpdate(date);
				}
				this.establishment.setCreatedUserId(this.getUserIdLogged());

				logger.debug("ApeCode : " + this.establishment.getApeCode());

				// find nafCode
				if (this.companyNafCode == null) {
					CompanyNafCode[] nafCodes = CompanyNafCode.values();
					for (CompanyNafCode nafCode : nafCodes) {
						if (nafCode.getApeCode().equalsIgnoreCase(this.establishment.getApeCode())) {
							this.companyNafCode = nafCode;
						}
					}
				}

				if (this.companyNafCode != null) {
					logger.debug("companyNafCode is not null, setting ApeCode and ApeLabel");
					this.establishment.setApeCode(companyNafCode.getApeCode());
					logger.debug("ApeCode : " + this.company.getApeCode());
					this.establishment.setApeNafLabel(this.getApeNaflabelValue(this.companyNafCode.name()));
					logger.debug("ApeLabel : " + this.company.getApeNafLabel());
				}

				// in case headOffice is null (no MPS response)
				if (this.establishment.getIsHeadOffice() == null) {
					this.establishment.setIsHeadOffice(Boolean.FALSE);
				}

				// Go to next page
				if (fromCompanyCreation) {
					// Go to company creation last step
					EmployeeProfile newEmployee = new EmployeeProfile();
					newEmployee.setAddress(this.company.getAddress());
					newEmployee.setCreatedUserId(this.getUserIdLogged());

					this.createPage.setEmployeeProfile(newEmployee);
					this.createPage.setIsLocHallesTheme(this.isLocHallesTheme);

					// Is that a new establishment ?
					if (this.radioSelectedValue == null) {
						// It is
						this.createPage.setEstablishment(this.establishment);

						// Now we need to check for the company headOffice
						if (!this.establishment.getIsHeadOffice() && !this.company.hasHeadOffice()
								&& this.company.getEstablishments().size() == 0) {
							if (this.company.getForeignIdentifier() || !this.initiateFrenchHeadOffice(this.company)) {
								// This is a foreign company. We will consider 1st establishment as headOffice
								this.establishment.setIsHeadOffice(Boolean.TRUE);
							}
						}
						this.createPage.setCompany(this.company);
					} else {
						// User selected existing establishment !
						// TODO : what does that stand for ? this.company.setResponsibles(this.persons);
						Establishment establishmentToSet = null;
						for (Establishment findEstablishment : myEstablishments) {
							// Find it with it's nic
							if (findEstablishment.getId().equals(radioSelectedValue)) {
								establishmentToSet = findEstablishment;
								break;
							}
						}
						if (establishmentToSet == null) {
							logger.error(
									"Going to throw IllegalStateException : This is a critical failure. This should NEVER EVER HAPPEND");
							throw new IllegalStateException(
									"This is a critical failure. This should NEVER EVER HAPPEND");
						}
						this.createPage.setEstablishment(establishmentToSet);
						this.createPage.setCompany(this.company);
					}
					this.createPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
					cleanSessionElements();
					return this.createPage;
				} else {
					// Go to viewEstablishment page !
					logger.debug("Creating BO Establishment : " + this.establishment.toString());
					logger.info("Establishment company :" + this.establishment.getCompany());
					this.company.addEstablishment(this.establishment);
					logger.trace("Added establishments to company...");
					this.company = this.companyService.update(this.company);

					logger.trace("Updated Company !");

					myEstablishments = company.getEstablishments();

					Establishment establishmentToSynchro = null;
					for (Establishment findEstablishmentToSynchro : myEstablishments) {
						if (findEstablishmentToSynchro.getForeignIdentifier() == Boolean.FALSE) {
							if (findEstablishmentToSynchro.getNic().equals(this.establishment.getNic())) {
								establishmentToSynchro = findEstablishmentToSynchro;
								logger.debug(
										"Found establishment to synchronize ! " + establishmentToSynchro.toString());
							}
						} else {
							if (findEstablishmentToSynchro.getNationalID() != null) {
								establishmentToSynchro = this.establishmentService
										.findLastForeignCreated(findEstablishmentToSynchro.getNationalID());
								if (establishmentToSynchro.getId() != null) {
									logger.debug("Found foreign establishment to synchronize ! "
											+ establishmentToSynchro.toString());
								}
								break;
							}
						}
					}

					// Send notification to external applications
					this.synchronizationService.notifyUpdate(this.company);
					if (establishmentToSynchro != null) {
						this.synchronizationService.notifyCreation(establishmentToSynchro);
					} else {
						logger.info("Synchronization Error : couldn't find establishment to synchronize in database !");
					}

					// TODO : do better than that !
					// Here the goal is to find the created establishment and initate view page with
					// it !
					for (Establishment establishmentLoopElement : myEstablishments) {
						if (!this.company.getForeignIdentifier()) {
							if (establishmentLoopElement.getNic().equals(establishment.getNic())) {
								logger.info("establishmentLoopElement : " + establishmentLoopElement);
								this.viewEstablishmentsPage
										.setSuccessMessage(this.getMessages().get("creating-success-message"));
								this.viewEstablishmentsPage.setViewEstablishment(establishmentLoopElement);
								cleanSessionElements();
								return this.viewEstablishmentsPage;
							}
						} else {
							if (establishmentLoopElement.getName().equals(establishment.getName())) {
								logger.info("establishmentLoopElement : " + establishmentLoopElement);
								this.viewEstablishmentsPage
										.setSuccessMessage(this.getMessages().get("creating-success-message"));
								this.viewEstablishmentsPage.setViewEstablishment(establishmentLoopElement);
								cleanSessionElements();
								return this.viewEstablishmentsPage;
							}
						}
					}
					logger.error(
							"Establishment saved before is not in company establishments. Throwing IllegalStateException");
					throw new IllegalStateException(
							"Fatal error : Establishment saved before is not in company establishments!");
				}
			} else if (this.searchEstablishment != null && this.searchEstablishment) {
				return this;
			} else if (createHeadOffice) {
				logger.trace("In createHeadOffice");

				// Creating headOffice is like serach establishment with headOffice NIC
				if (this.company.getNic() != null) {
					establishmentNic = company.getNic();
				} else {
					establishmentNic = company.getSiretHeadOffice().substring(9, 14);
				}
				this.createHeadOffice = false;
				this.onSelectedFromSearchEstablishment();
				return this.onSuccess();
			}
		} catch (Exception e) {
			logger.error("Error happened recording establishment !", e);
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}
		return this;
	}

	private void cleanSessionElements() {
		this.searchEstablishment = null;
	}

	private boolean initiateFrenchHeadOffice(Company company) {
		logger.info("Initiating head office for company " + company.getSiren());
		Establishment headOfficeEstablishment = new Establishment();
		if (company.getSiretHeadOffice() != null) {
			headOfficeEstablishment.setCreatedUserId(this.getUserIdLogged());
			headOfficeEstablishment.setSiret(company.getSiretHeadOffice());
			logger.info("headOffice SIRET : " + company.getSiretHeadOffice());
			try {
				headOfficeEstablishment = this.mpsWS.updateEstablishmentInfo(headOfficeEstablishment, company);
			} catch (FileNotFoundException e) {
				logger.error("Error happened fetching info", e);
				this.errorMessage = this.getMessages().get("mps-error-message");
				this.isMpsError = Boolean.TRUE;
			} catch (IOException e) {
				logger.error("Error happened fetching info", e);
				this.errorMessage = this.getMessages().get("mps-error-message");
				this.isMpsError = Boolean.TRUE;
			} catch (Exception e) {
				logger.error("Error happened fetching info", e);
				this.errorMessage = this.getMessages().get("mps-error-message");
				this.isMpsError = Boolean.TRUE;
			}

			// Some companies may have a SIREN in France but there head office is in an
			// other country so when we
			// try to get the information of the head office (Siren + 00017) we receive from
			// API enterprise
			// some incomplete data
			if (headOfficeEstablishment.getPays().getValue().equals("FRANCE")) {
				logger.info("HeadOffice : " + headOfficeEstablishment);
				headOfficeEstablishment
						.setName(company.getLabel() + MyEc3Constants.HEAD_OFFICE_ESTABLISHMENT_LABEL_POSTFIX);
				headOfficeEstablishment.setCompany(company);
				headOfficeEstablishment.setEmail(company.getEmail());

				if (headOfficeEstablishment.getAddress().getCanton() == null) {
					headOfficeEstablishment.getAddress().setCanton("Aucun");
				}

				// load insee info for head office
				if (headOfficeEstablishment.getAddress().getInsee() != null) {
					InseeGeoCode headOfficeInseeCode = this.inseeGeoCodeService
							.findByInseeCode(headOfficeEstablishment.getAddress().getInsee());
					if (headOfficeInseeCode != null) {
						headOfficeEstablishment.getAddress().setInsee(headOfficeInseeCode.getInseeCode());
					} else {
						headOfficeEstablishment.getAddress().setInsee(null);
					}
				}

				company.setNic(headOfficeEstablishment.getNic());
				company.addEstablishment(headOfficeEstablishment);
				logger.debug("Company Establishements : " + company.getEstablishments());
				return true;
			}

		} else {
			logger.info("Company Head Office SIRET is null ! ");
		}
		return false;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return View.class;
	}

	/**
	 * Searching establishement with nic
	 */
	void onSelectedFromSearchEstablishment() {

		if (establishmentNic != null) {

			logger.debug("Searching with establishmentNic : " + this.establishmentNic);
			this.searchEstablishment = Boolean.TRUE;

			if (myEstablishments == null) {
				myEstablishments = company.getEstablishments();
			}

			// Check if establishment already exists
			for (Establishment establishementLoop : myEstablishments) {
				if (establishementLoop.getNic().equals(this.establishmentNic)) {
					logger.warn("Establishment already exist : " + this.company.getSiren() + this.company.getNic());
					this.form.recordError(this.getMessages().get("establishmentAlreadyExist-error-message"));
					this.establishmentNic = null;
					return;
				}
			}

			String establishmentSiret = this.company.getSiren() + this.establishmentNic;

			if (!this.companyService.isSiretValid(establishmentSiret, "")) {
				// TODO : Change message ? J'ai rentré un NIC et on me dit siret invalide !
				this.form.recordError(this.messages.get("invalid-siret-error"));
				this.establishmentNic = null;
				this.initEstablishment();
				logger.error("This is no valid siret ! " + establishmentSiret);
				return;
			}

			// Update establishemnt data
			this.establishment.setCompany(this.company);
			this.establishment.setNic(this.establishmentNic);
			this.establishment.setSiret(establishmentSiret);

			// Let's call MPS to get infos
			try {
				this.establishment = this.mpsWS.updateEstablishmentInfo(this.establishment, this.company);
				if (this.isMpsError == null || this.isMpsError.equals(Boolean.FALSE)) {

					if (this.establishment.getAddress().getCanton() == null) {
						this.establishment.getAddress().setCanton("Aucun");
					}

					if (this.establishment.getAddress().getInsee() != null) {
						// Find InseeGeoCode in database
						InseeGeoCode inseeGeoCode = this.inseeGeoCodeService
								.findByInseeCode(this.establishment.getAddress().getInsee());

						if (inseeGeoCode != null) {
							this.inseeCode = inseeGeoCode.getInseeCode();
							this.establishment.getAddress().setInsee(inseeGeoCode.getInseeCode());

							if (inseeGeoCode.getReg() != null) {
								// Find the region
								InseeRegion inseeRegion = this.inseeRegionService.findOne(inseeGeoCode.getReg());
								if (inseeRegion != null) {
									logger.debug("Insee Region : " + inseeRegion.getNccenr());
									this.inseeRegionLabel = inseeRegion.getRegion() + " - " + inseeRegion.getNccenr();
								}
							} else {
								this.inseeRegionLabel = "Non connue";
							}

							if (inseeGeoCode.getDep() != null) {
								// Find the department
								InseeCounty inseeCounty = this.inseeCountyService.findById(inseeGeoCode.getDep());
								if (inseeCounty != null) {
									logger.debug("Insee Departement : " + inseeCounty.getNccenr());
									this.inseeCountyLabel = inseeCounty.getDep() + " - " + inseeCounty.getNccenr();
								}
							} else {
								this.inseeCountyLabel = "Non connu";
							}

							if (inseeGeoCode.getReg() != null && inseeGeoCode.getDep() != null
									&& inseeGeoCode.getAr() != null) {
								// Find the borough
								InseeBorough inseeBorough = this.inseeBoroughService.findBourough(inseeGeoCode.getReg(),
										inseeGeoCode.getDep(), inseeGeoCode.getAr());
								if (inseeBorough != null) {
									logger.debug("Insee Arrondissement : " + inseeBorough.getNccenr());
									this.inseeBoroughLabel = inseeBorough.getAr() + " - " + inseeBorough.getNccenr();
								}
							} else {
								this.inseeBoroughLabel = "Non connu";
							}

							if (inseeGeoCode.getReg() != null && inseeGeoCode.getDep() != null
									&& inseeGeoCode.getCt() != null) {
								if (inseeGeoCode.getCt() > this.inseeCantonService.getCantonMaxId()) {
									this.inseeCantonLabel = "Non connu";
								} else {
									// Find the canton
									InseeCanton inseeCanton = this.inseeCantonService.findCanton(inseeGeoCode.getReg(),
											inseeGeoCode.getDep(), inseeGeoCode.getCt());

									logger.debug("Insee Canton : " + inseeCanton.getNccenr());
									this.inseeCantonLabel = inseeCanton.getCanton() + " - " + inseeCanton.getNccenr();
								}
							} else {
								this.inseeCantonLabel = "Non connu";
							}
						} else {
							// couldn't find inseeCode in db, set it to null
							// prevent fail on create
							logger.debug("InseeCode not in db, setting it to null.");
							this.establishment.getAddress().setInsee(null);
						}
					}
					this.isMpsError = Boolean.FALSE;
				}
			} catch (FileNotFoundException e) {
				logger.error("No MPS response with establishment siret");
				this.errorMessage = this.getMessages().get("mps-error-message");
				this.isMpsError = Boolean.TRUE;
			} catch (IOException e) {
				logger.error("Unable to connect to MPS");
				this.errorMessage = this.getMessages().get("mps-error-message");
				this.isMpsError = Boolean.TRUE;
			} catch (Exception e) {
				logger.error("Error happened fetching info");
				this.errorMessage = this.getMessages().get("mps-error-message");
				this.isMpsError = Boolean.TRUE;
			}
		} else {
			logger.error("EstablishmentNic is null !");
			this.errorMessage = this.getMessages().get("establishmentNic-error-message");
		}
	}

	/**
	 * Perform form validation per each form
	 */
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {
		if (this.submit != null && this.submit && this.radioSelectedValue == null) {
			// Validate new establishment submission
			if (!this.company.getForeignIdentifier()) {
				// French company
				if (!this.companyService.isSiretValid(this.company.getSiren(), this.establishment.getNic())) {
					this.form.recordError(this.messages.get("invalid-siret-error"));
					Address resetAddress = new Address();
					this.establishment.setAddress(resetAddress);
				}
			} else {
				// Foreign company
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

			// Elements needed for french and foreign companies
			if (null == this.establishment.getLabel()) {
				this.form.recordError(this.messages.get("label-required-message"));
			}

			if (null == this.establishment.getEmail()) {
				this.form.recordError(this.messages.get("email-required-message"));
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
		}
	}

	void onSelectedFromSubmit() {
		this.submit = Boolean.TRUE;
	}

	void onSelectedFromCreateHeadOffice() {
		this.createHeadOffice = true;
		this.searchEstablishment = false;
	}

	public Boolean getPostalAddressDisplay() {
		if (this.establishment.getAddress().getPostalAddress() != null
				&& !this.establishment.getAddress().getPostalAddress().isEmpty()
				&& !(this.isMpsError.equals(Boolean.TRUE))) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Establishment getMyEstablishment() {
		return myEstablishment;
	}

	public void setMyEstablishment(Establishment myEstablishment) {
		this.myEstablishment = myEstablishment;
	}

	public Boolean getPostalCodeDisplay() {
		if (this.establishment.getAddress().getPostalCode() != null
				&& !this.establishment.getAddress().getPostalCode().isEmpty()
				&& !(this.isMpsError.equals(Boolean.TRUE))) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getCityDisplay() {
		if (this.establishment.getAddress().getCity() != null && !this.establishment.getAddress().getCity().isEmpty()
				&& !(this.isMpsError.equals(Boolean.TRUE))) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getCantonDisplay() {
		if (this.establishment.getAddress().getCanton() != null
				&& !this.establishment.getAddress().getCanton().isEmpty() && this.isMpsError.equals(Boolean.TRUE)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getNicDisplay() {
		if (this.establishment.getNic() != null && !this.establishment.getNic().isEmpty()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getApeCodeDisplay() {
		if (this.establishment.getApeCode() != null && !this.establishment.getApeCode().isEmpty()
				&& !(this.isMpsError.equals(Boolean.TRUE))) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getSiretDisplay() {
		if (this.establishment.getSiret() != null && !this.establishment.getSiret().isEmpty()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getAdministrativeStateValueDisplay() {
		if (this.establishment.getAdministrativeState().getAdminStateValue() != null
				&& !this.establishment.getAdministrativeState().getAdminStateValue().getLabel().isEmpty()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getInseeCodeDisplay() {
		if ((this.inseeCode != null && !this.inseeCode.isEmpty() && !(this.isMpsError.equals(Boolean.TRUE)))
				|| (this.establishment.getAddress().getInsee() != null)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
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

	public void setCompany(Company company) {
		this.company = company;
	}

	public Company getCompany() {
		return this.company;
	}

	public boolean isFromCompanyCreation() {
		return fromCompanyCreation;
	}

	public void setFromCompanyCreation(boolean fromCompanyCreation) {
		this.fromCompanyCreation = fromCompanyCreation;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public Boolean getIsLocHallesTheme() {
		return isLocHallesTheme;
	}

	public void setIsLocHallesTheme(Boolean isLocHallesTheme) {
		this.isLocHallesTheme = isLocHallesTheme;
	}

	public Boolean getArrayInitializeDisplay() {
		return !this.company.getEstablishments().isEmpty();
	}

	public Boolean getShowHeadOffice() {
		// we don't change establishmentNic value
		Boolean wasEstablishmentNic;
		if (this.establishmentNic == null) {
			wasEstablishmentNic = Boolean.FALSE;
		} else {
			wasEstablishmentNic = Boolean.TRUE;
		}
		// Case establishment is head office but boolean is not set
		if (this.establishmentNic == null && this.company.getNic() != null && !(this.company.getNic().isEmpty())) {
			this.establishmentNic = this.company.getNic();
		} else if (this.company.getSiretHeadOffice() == null
				&& (this.company.getNic() == null || this.company.getNic().isEmpty())) {
			if (wasEstablishmentNic.equals(Boolean.FALSE)) {
				this.establishmentNic = null;
			}
			return Boolean.FALSE;
		}

		if (myEstablishments != null) {
			for (Establishment establishementLoop : myEstablishments) {
				if (establishementLoop.getNic().equals(this.establishmentNic)) {
					// reset establishmentNic so the field stay empty
					if (wasEstablishmentNic.equals(Boolean.FALSE)) {
						this.establishmentNic = null;
					}
					return Boolean.FALSE;
				}
			}
		}
		// check if establishment exists or if actual establishment is head office
		if (this.establishment != null) {
			if (this.establishment.getIsHeadOffice() == null) {
				this.establishment.setIsHeadOffice(Boolean.FALSE);
			}
			// reset establishmentNic so the field stay empty
			if (wasEstablishmentNic.equals(Boolean.FALSE)) {
				this.establishmentNic = null;
			}
			return (!this.headOfficeInList && !this.establishment.getIsHeadOffice());
		}
		// reset establishmentNic so the field stay empty
		if (wasEstablishmentNic.equals(Boolean.FALSE)) {
			this.establishmentNic = null;
		}
		return Boolean.TRUE;
	}

	public Boolean isHeadOfficeInList() {
		return headOfficeInList;
	}

	public void setHeadOfficeInList(Boolean isHeadOfficeInList) {
		this.headOfficeInList = isHeadOfficeInList;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	private void initEstablishment() {
		logger.debug("Creating new Establishment !");
		this.establishment = new Establishment();
		this.establishment.setCreatedUserId(this.getUserIdLogged());
		this.establishment.setCompany(this.company);
		Address address = new Address();
		this.establishment.setAddress(address);
	}

	public BeanModel<Establishment> getMyEstablishmentModel() {
		// Used to render the Establishment Array
		BeanModel<Establishment> myEstablishmentModel = beanModelSource.createDisplayModel(Establishment.class,
				messages);
		if (!this.company.getForeignIdentifier()) {
			logger.trace("This is a French company !");
			if (isFromCompanyCreation()) {
				logger.trace("We came from company creation");
				// We're creating company, we need more infos !
				myEstablishmentModel.add("postalAddress", null);
				myEstablishmentModel.add("postalCode", null);
				myEstablishmentModel.add("city", null);
				myEstablishmentModel.add("select", null);
				myEstablishmentModel.include("label", "nic", "postalAddress", "postalCode", "city", "select");
				myEstablishmentModel.get("label").label("Nom de l'établissement");
				myEstablishmentModel.get("nic").label("Code NIC");
				myEstablishmentModel.get("select").label("Sélectionner");
			} else {
				logger.trace("We're not from company creation");
				myEstablishmentModel.include("label", "nic");
				myEstablishmentModel.get("label").label("Nom de l'établissement");
				myEstablishmentModel.get("nic").label("Code NIC");
			}
		} else {
			logger.trace("This is a Foreign company !");
			myEstablishmentModel.include("label");
			myEstablishmentModel.get("label").label("Nom de l'établissement");
		}

		if (this.company.getId() != null) {
			this.companyService.populateCollections(company);
			// If we have establishments, populate the array
			if (this.company.getEstablishments().size() > 0) {
				myEstablishments = establishmentService.findAllEstablishmentsByCompany(this.company);
				if (isFromCompanyCreation()) {
					for (Establishment isHeadOffice : myEstablishments) {
						if (isHeadOffice.getIsHeadOffice()) {
							this.headOfficeInList = Boolean.TRUE;
						}
					}
				}
			}
		}
		return myEstablishmentModel;
	}

	public List<Establishment> getMyEstablishmentsList() {
		if (this.company.getId() != null) {
			this.companyService.populateCollections(this.company);
		}
		return this.company.getEstablishments();
	}

	public void setCompanyIdFromBo(Long idFromBo) {
		this.companyIdFromBo = idFromBo;
	}

}
