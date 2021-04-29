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
package org.myec3.socle.webapp.pages.company.establishment;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.CompanyNafCode;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;

import javax.inject.Named;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Page used to modify an establishment {@link Establishment}.<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/company/establishment/Modify.tml
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 * @author Matthieu GASPARD <matthieu.gaspard@worldline.com>
 */

public class Modify extends AbstractPage {

	private static final Log logger = LogFactory.getLog(Modify.class);

	@SuppressWarnings("unused")
	@Persist(PersistenceConstants.FLASH)
	@Property
	private String errorMessage;

	@Inject
	@Named("establishmentService")
	private EstablishmentService establishmentService;

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
	@Persist(PersistenceConstants.FLASH)
	private String inseeCode;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String inseeRegionLabel;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String inseeCountyLabel;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String inseeBoroughLabel;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String inseeCantonLabel;

	@Property
	private Establishment establishment;

	@Property
	private CompanyNafCode companyNafCode;

	@Inject
	private Messages messages;

	@Component(id = "modification_form")
	private Form form;

	@InjectPage
	@Property
	private View viewEstablishmentsPage;

	private CompanyWSinfo mpsWS = new MpsWsClient();

	/**
	 * @param id : establishment id
	 * @return : current page if logged user has rights to access at this page else
	 *         return to Index
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		// Retrieve the establishment by it's ID
		this.establishment = this.establishmentService.findOne(id);
		if (null == this.establishment) {
			return false;
		}

		// find nafCode
		if (this.companyNafCode == null) {
			CompanyNafCode[] nafCodes = CompanyNafCode.values();
			for (CompanyNafCode nafCode : nafCodes) {
				if (nafCode.getApeCode().equalsIgnoreCase(this.establishment.getApeCode()))
					this.companyNafCode = nafCode;
			}
		}

		if (this.establishment.getAddress().getInsee() != null) {

			this.inseeCode = this.establishment.getAddress().getInsee();
			// Find InseeGeoCode in database
			InseeGeoCode inseeGeoCode = this.inseeGeoCodeService
					.findByInseeCode(this.establishment.getAddress().getInsee());

			if (inseeGeoCode != null && inseeGeoCode.getReg() != null) {
				// Find the region
				InseeRegion inseeRegion = this.inseeRegionService.findById(inseeGeoCode.getReg());
				if (inseeRegion != null) {
					this.inseeRegionLabel = inseeRegion.getRegion() + " - " + inseeRegion.getNccenr();
				}
			} else {
				this.inseeRegionLabel = "Non connue";
			}

			if (inseeGeoCode != null && inseeGeoCode.getDep() != null) {
				// Find the department
				InseeCounty inseeCounty = this.inseeCountyService.findById(inseeGeoCode.getDep());
				if (inseeCounty != null) {
					this.inseeCountyLabel = inseeCounty.getDep() + " - " + inseeCounty.getNccenr();
				}
			} else {
				this.inseeCountyLabel = "Non connu";
			}

			if (inseeGeoCode != null && inseeGeoCode.getReg() != null && inseeGeoCode.getDep() != null
					&& inseeGeoCode.getAr() != null) {
				// Find the borough
				InseeBorough inseeBorough = this.inseeBoroughService.findBourough(inseeGeoCode.getReg(),
						inseeGeoCode.getDep(), inseeGeoCode.getAr());
				if (inseeBorough != null) {
					this.inseeBoroughLabel = inseeBorough.getAr() + " - " + inseeBorough.getNccenr();
				}
			} else {
				this.inseeBoroughLabel = "Non connu";
			}

			if (inseeGeoCode != null && inseeGeoCode.getReg() != null && inseeGeoCode.getDep() != null
					&& inseeGeoCode.getCt() != null) {
				// Find the canton
				InseeCanton inseeCanton = this.inseeCantonService.findCanton(inseeGeoCode.getReg(),
						inseeGeoCode.getDep(), inseeGeoCode.getCt());

				if (inseeCanton == null || inseeGeoCode.getCt() == 99) {
					this.inseeCantonLabel = "Non connu";
				} else {
					this.inseeCantonLabel = inseeCanton.getCanton() + " - " + inseeCanton.getNccenr();
				}
			} else {
				this.inseeCantonLabel = "Non connu";
			}

		}
		// Check if loggedUser can access to this user
		// TODO : check on establishment
		return this.hasRights(this.establishment);
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return this.viewEstablishmentsPage;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.establishment != null) ? this.establishment.getId() : null;
	}

	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {

		if (BooleanUtils.isFalse(this.establishment.getCompany().getForeignIdentifier())) {
			if (null == this.establishment.getSiret() || !mpsWS.establishmentExist(this.establishment)) {
				this.form.recordError(this.getMessages().get("invalid-siret-error"));
			}
		}

		if (null == this.establishment.getLabel()) {
			this.form.recordError(this.getMessages().get("label-required-message"));
		}

		if (null == this.establishment.getEmail() || this.establishment.getEmail().isEmpty()) {
			this.form.recordError(this.getMessages().get("email-required-message"));
		}


		if (null == this.establishment.getAddress().getPostalAddress()
				|| this.establishment.getAddress().getPostalAddress().isEmpty()) {
			this.form.recordError(this.getMessages().get("postalAddress-required-message"));
		}

		if (null == this.establishment.getAddress().getPostalCode()
				|| this.establishment.getAddress().getPostalCode().isEmpty()) {
			this.form.recordError(this.getMessages().get("postalCode-required-message"));
		}

		if (null == this.establishment.getAddress().getCity() || this.establishment.getAddress().getCity().isEmpty()) {
			this.form.recordError(this.getMessages().get("city-required-message"));
		}

		if (null == this.companyNafCode || this.companyNafCode.getApeCode().isEmpty()) {
			this.form.recordError(this.messages.get("apeCode-required-message"));
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {

			if (this.companyNafCode != null) {
				this.establishment.setApeCode(companyNafCode.getApeCode());
				this.establishment.setApeNafLabel(this.getApeNaflabelValue(this.companyNafCode.name()));
			}
			// Update the establishment
			logger.debug("Updating BO Establishment : " + this.establishment.toString());
			this.establishmentService.update(this.establishment);

			// Send notification to external applications
			this.synchronizationService.notifyUpdate(this.establishment);

		} catch (Exception e) {
			this.errorMessage = this.getMessages().get("recording-error-message");
			logger.error(e);
			return null;
		}

		this.viewEstablishmentsPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.viewEstablishmentsPage.setViewEstablishment(this.establishment);
		return this.viewEstablishmentsPage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return View.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void activation() {
		super.initUser();
	}

	public Boolean getPostalAddressDisplay() {
		if (this.establishment.getAddress().getPostalAddress() != null
				&& !this.establishment.getAddress().getPostalAddress().isEmpty()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getPostalCodeDisplay() {
		return this.establishment.getAddress().getPostalCode() != null
				&& !this.establishment.getAddress().getPostalCode().isEmpty();
	}

	public Boolean getCityDisplay() {
		return this.establishment.getAddress().getCity() != null
				&& !this.establishment.getAddress().getCity().isEmpty();
	}

	public Boolean getCantonDisplay() {
		return this.establishment.getAddress().getCanton() != null
				&& !this.establishment.getAddress().getCanton().isEmpty();
	}

	public Boolean getNicDisplay() {
		return this.establishment.getNic() != null && !this.establishment.getNic().isEmpty();
	}

	public Boolean getApeCodeDisplay() {
		return this.companyNafCode != null && !this.companyNafCode.getApeCode().isEmpty();
	}

	/**
	 * Enable Update Siret for Admin only
	 * @return
	 */
	public Boolean getDisableSiret() {
		return !this.getIsAdmin();
	}

	public Boolean getAdministrativeStateValueDisplay() {
		return this.establishment.getAdministrativeState() != null
				&& this.establishment.getAdministrativeState().getAdminStateValue() != null
				&& !this.establishment.getAdministrativeState().getAdminStateValue().getLabel().isEmpty();
	}

	public ValueEncoder<CompanyNafCode> getApeCodeEncoder() {
		return new GenericListEncoder<>(Arrays.asList(CompanyNafCode.values()));
	}

	public String getApeNaflabelValue(String key) {
		return this.messages.get(key);
	}

	public Map<CompanyNafCode, String> getListOfCompanyApeCode() {
		Map<CompanyNafCode, String> companies = new LinkedHashMap<>();
		CompanyNafCode[] companiesList = CompanyNafCode.values();
		for (CompanyNafCode nafcode : companiesList) {
			companies.put(nafcode, nafcode + " - " + this.getApeNaflabelValue(nafcode.name()));
		}
		return companies;
	}

	public Boolean getHasRightOnEstablishment() {
		return this.hasRights(establishment);
	}

	public Boolean getInseeCodeDisplay() {
		return this.inseeCode != null && !this.inseeCode.isEmpty();
	}
}
