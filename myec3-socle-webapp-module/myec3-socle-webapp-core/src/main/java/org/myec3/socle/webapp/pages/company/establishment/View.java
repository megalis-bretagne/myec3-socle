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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.InseeBorough;
import org.myec3.socle.core.domain.model.InseeCanton;
import org.myec3.socle.core.domain.model.InseeCounty;
import org.myec3.socle.core.domain.model.InseeGeoCode;
import org.myec3.socle.core.domain.model.InseeRegion;
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.InseeBoroughService;
import org.myec3.socle.core.service.InseeCantonService;
import org.myec3.socle.core.service.InseeCountyService;
import org.myec3.socle.core.service.InseeGeoCodeService;
import org.myec3.socle.core.service.InseeRegionService;
import org.myec3.socle.core.service.MpsUpdateJobService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;

/**
 * Page used to display the organization chart of an establishment
 * {@link Establishment}.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/company/establishment/View.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Nicolas Buisson <nicolas.buisson@worldline.com>
 */
public class View extends AbstractPage {

	private static final Log logger = LogFactory.getLog(View.class);

	@Inject
	@Named("establishmentService")
	private EstablishmentService establishmentService;

	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	@Inject
	@Named("mpsUpdateJobService")
	private MpsUpdateJobService mpsUpdateJobService;

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

	@Inject
	private Messages messages;

	@InjectPage
	@Property
	private ListEstablishments listEstablismentPage;

	@Property
	private Establishment establishment;

	private Company company;

	private CompanyWSinfo mpsWS = new MpsWsClient();

	@Property
	private String adminStateLastUpdateFormat;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	@Property
	private Boolean hasRightsEstablishment;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {

		if (this.establishment == null && id != null) {
			this.establishment = this.establishmentService.findOne(id);
		} else {
			return Index.class;
		}

		this.company = this.establishment.getCompany();

		if (this.establishment.getAdministrativeState() != null) {
			Date lastUpdateDate = this.establishment.getAdministrativeState().getAdminStateLastUpdated();
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			this.adminStateLastUpdateFormat = formatter.format(lastUpdateDate);
		}

		if (this.establishment.getAddress().getInsee() != null) {

			this.inseeCode = this.establishment.getAddress().getInsee();
			// Find InseeGeoCode in database
			InseeGeoCode inseeGeoCode = this.inseeGeoCodeService
					.findByInseeCode(this.establishment.getAddress().getInsee());

			if (inseeGeoCode != null && inseeGeoCode.getReg() != null) {
				// Find the region
				InseeRegion inseeRegion = this.inseeRegionService.findOne(inseeGeoCode.getReg());
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

		// Check if loggedUser can access to this establishment
		Object hasRightsCompany = this.hasRights(this.company);
		this.hasRightsEstablishment = this.hasRights(this.establishment);
		logger.debug("has rights establish : " + this.hasRightsEstablishment);
		// Visible for all people having rigth on company
		return hasRightsCompany;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.establishment != null) ? this.establishment.getId() : null;
	}

	// Events
	@OnEvent(value = "action", component = "delete")
	public Object removeEstablishment(Long id) {
		Establishment establishmentToDelete = this.establishmentService.findOne(id);
		List<EmployeeProfile> establishmentEmployees = establishmentToDelete.getEmployees();

		Boolean isEmployeeEnable;
		if (establishmentEmployees.size() > 0) {
			isEmployeeEnable = Boolean.TRUE;
		} else {
			isEmployeeEnable = Boolean.FALSE;
		}
		for (EmployeeProfile employeeProfile : establishmentEmployees) {
			if (employeeProfile.getEnabled().equals(Boolean.FALSE)) {
				isEmployeeEnable = Boolean.FALSE;
			}
		}
		if (isEmployeeEnable.equals(Boolean.TRUE)) {
			this.errorMessage = this.messages.get("deleteEstablishment-user-error-message");
			return null;
		} else {
			try {
				logger.info("Establishment to delete : " + establishmentToDelete.getId());

				MpsUpdateJob establishmentUpdateJob = this.mpsUpdateJobService.findOne(establishmentToDelete.getId());

				if (establishmentUpdateJob != null) {
					logger.info("Found MpsUpdateJob to delete : " + establishmentUpdateJob.toString());
					this.mpsUpdateJobService.delete(establishmentUpdateJob);
					logger.info("MpsUpdateJob deleted !");
				}

				this.establishmentService.delete(establishmentToDelete);
				logger.info("Establishment delete success !");

				// Send notification to external applications
				this.synchronizationService.notifyUpdate(this.company);
				this.synchronizationService.notifyDeletion(establishmentToDelete);

				this.listEstablismentPage.setSuccessMessage(this.getMessages().get("delete-success-message"));
				this.listEstablismentPage.setCompany(this.company);
				return this.listEstablismentPage;
			} catch (Exception e) {
				logger.info("An error occured while deleting the establishment : " + e);
				this.errorMessage = this.messages.get("deleteEstablishment-error-message");
				return null;
			}
		}
	}

	// Getters and Setters
	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public Establishment getViewEstablishment() {
		return this.establishment;
	}

	public void setViewEstablishment(Establishment establishment) {
		this.establishment = establishment;
	}

	public Boolean getDeleteButton() {
		if (this.getIsTechnicalAdmin() || this.getIsFunctionalAdmin()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getHasRightOnEstablishment() {
		if (this.hasRights(establishment)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}
