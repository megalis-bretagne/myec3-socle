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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.constants.MyEc3MpsUpdateConstants;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.InseeBorough;
import org.myec3.socle.core.domain.model.InseeCanton;
import org.myec3.socle.core.domain.model.InseeCounty;
import org.myec3.socle.core.domain.model.InseeGeoCode;
import org.myec3.socle.core.domain.model.InseeRegion;
import org.myec3.socle.core.domain.model.MpsUpdateJob;
import org.myec3.socle.core.domain.model.Person;
import org.myec3.socle.core.domain.model.enums.CompanyCategory;
import org.myec3.socle.core.domain.model.enums.MpsUpdateTypeValue;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EstablishmentService;
import org.myec3.socle.core.service.InseeBoroughService;
import org.myec3.socle.core.service.InseeCantonService;
import org.myec3.socle.core.service.InseeCountyService;
import org.myec3.socle.core.service.InseeGeoCodeService;
import org.myec3.socle.core.service.InseeRegionService;
import org.myec3.socle.core.service.MpsUpdateJobService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used to display company's informations {@link Company}<br />
 * 
 * see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 *      Corresponding tapestry template file is :
 *      src/main/resources/org/myec3/socle
 *      /webapp/pages/company/DetailCompany.tml
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * 
 */
public class DetailCompany extends AbstractPage {

	private static final Log logger = LogFactory.getLog(DetailCompany.class);

	private Company company;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Inject
	private Messages messages;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Inject
	@Service("companyService")
	private CompanyService companyService;

	@Inject
	@Service("establishmentService")
	private EstablishmentService establishmentService;

	@Inject
	@Service("mpsUpdateJobService")
	private MpsUpdateJobService mpsUpdateJobService;

	@Inject
	@Service("inseeGeoCodeService")
	private InseeGeoCodeService inseeGeoCodeService;

	@Inject
	@Service("inseeRegionService")
	private InseeRegionService inseeRegionService;

	@Inject
	@Service("inseeCountyService")
	private InseeCountyService inseeCountyService;

	@Inject
	@Service("inseeBoroughService")
	private InseeBoroughService inseeBoroughService;

	@Inject
	@Service("inseeCantonService")
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

	@SuppressWarnings("unused")
	@Property
	private List<Person> responsibles;

	@Property
	private String adminStateLastUpdateFormat;

	@Property
	private String companyCreationDate;

	@Property
	private String companyRadiationDate;

	@Property
	private String companyCategoryLabel;

	private Person responsibleLoop;

	@OnEvent(EventConstants.ACTIVATE)
	public void onActivate() {
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.company = this.companyService.findOne(id);

		if (null == this.company) {
			return Index.class;
		}

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		if (this.company.getAdministrativeState() != null) {
			Date lastUpdateDate = this.company.getAdministrativeState().getAdminStateLastUpdated();
			String lastUpdateDateFormat = formatter.format(lastUpdateDate);
			this.adminStateLastUpdateFormat = lastUpdateDateFormat;
		} else {
			this.adminStateLastUpdateFormat = "Non connue";
		}

		if (this.company.getCreationDate() != null) {
			Date creationDate = this.company.getCreationDate();
			String creationDateFormat = formatter.format(creationDate);
			this.companyCreationDate = creationDateFormat;
		} else {
			this.companyCreationDate = "Non connue";
		}

		if (this.company.getRadiationDate() != null) {
			Date radiationDate = this.company.getRadiationDate();
			String radiationDateFormat = formatter.format(radiationDate);
			this.companyRadiationDate = radiationDateFormat;
		} else {
			this.companyRadiationDate = "Non connue";
		}

		if (this.company.getCompanyCategory() == null) {
			this.companyCategoryLabel = CompanyCategory.NON_RENSEIGNEE.getLabel();
		} else if (this.company.getCompanyCategory().equals(CompanyCategory.NON_RENSEIGNEE)) {
			this.companyCategoryLabel = CompanyCategory.NON_RENSEIGNEE.getLabel();
		} else if (this.company.getCompanyCategory().equals(CompanyCategory.COMMERCE)) {
			this.companyCategoryLabel = CompanyCategory.COMMERCE.getLabel();
		} else if (this.company.getCompanyCategory().equals(CompanyCategory.INDUSTRIE)) {
			this.companyCategoryLabel = CompanyCategory.INDUSTRIE.getLabel();
		} else if (this.company.getCompanyCategory().equals(CompanyCategory.SERVICE)) {
			this.companyCategoryLabel = CompanyCategory.SERVICE.getLabel();
		}

		if (this.company.getInsee() != null) {
			this.inseeCode = this.company.getInsee();
			// Find InseeGeoCode in database
			InseeGeoCode inseeGeoCode = this.inseeGeoCodeService.findByInseeCode(this.company.getInsee());

			if (inseeGeoCode != null && inseeGeoCode.getReg() != null) {
				// Find the region
				InseeRegion inseeRegion = this.inseeRegionService.findOne(inseeGeoCode.getReg());
				if (inseeRegion != null) {
					this.inseeRegionLabel = inseeRegion.getRegion() + " - " + inseeRegion.getNccenr();
				} else {
					this.inseeRegionLabel = "Non connue";
				}
			} else {
				this.inseeRegionLabel = "Non connue";
			}

			if (inseeGeoCode != null && inseeGeoCode.getDep() != null) {
				// Find the department
				InseeCounty inseeCounty = this.inseeCountyService.findById(inseeGeoCode.getDep());
				if (inseeCounty != null) {
					this.inseeCountyLabel = inseeCounty.getDep() + " - " + inseeCounty.getNccenr();
				} else {
					this.inseeCountyLabel = "Non connu";
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
				} else {
					this.inseeBoroughLabel = "Non connu";
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
		this.responsibles = this.company.getResponsibles();

		// Check if loggedUser can access to this company
		return this.hasRights(this.company);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.company != null) ? this.company.getId() : null;
	}

	@OnEvent(value = "action", component = "update")
	public Object updateEstablishment(Long id) {
		try {
			logger.info("Starting Manual update. Company id : " + id);
			List<MpsUpdateJob> ressourcesToUpdate = new ArrayList<MpsUpdateJob>();

			Company thisCompany = companyService.findOne(id);

			// Create and set MpsUpdateJob
			MpsUpdateJob updateCompany = new MpsUpdateJob();
			updateCompany.setId(id);
			updateCompany.setPriority(MpsUpdateTypeValue.MANUAL.getLabel());
			updateCompany.setType(ResourceType.COMPANY.getLabel());

			ressourcesToUpdate.add(updateCompany);

			ressourcesToUpdate.addAll(
					establishmentService.getEstablishmentToUpdateByCompany(thisCompany, MpsUpdateTypeValue.MANUAL));

			// Insert into DB
			for (MpsUpdateJob jobToInsert : ressourcesToUpdate) {
				logger.info("Insert MpsUpdateJob : " + jobToInsert.toString());
				mpsUpdateJobService.create(jobToInsert);
				logger.info("Insert ok");
			}
			this.successMessage = this.messages.get("updateCompany-success-message");
			return null;

		} catch (Exception e) {
			logger.debug("An error occured : " + e);
			this.errorMessage = this.messages.get("updateEstablishment-error-message");
			return null;
		}
	}

	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public Company getCompany() {
		if (company.getWebsite() != null && !company.getWebsite().isEmpty()) {
			if (!company.getWebsite().toLowerCase().contains("http")) {
				company.setWebsite("http://" + company.getWebsite());
			}
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Person getResponsibleLoop() {
		return responsibleLoop;
	}

	public void setResponsibleLoop(Person responsibleLoop) {
		this.responsibleLoop = responsibleLoop;
	}

	public Boolean getUpdateButton() {
		if ((this.getIsTechnicalAdmin() || this.getIsFunctionalAdmin())
				&& (this.company.getForeignIdentifier().equals(Boolean.FALSE))) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getInseeCodeDisplay() {
		logger.debug("Insee : " + this.company.getInsee());
		if (this.company.getInsee() != null && !this.company.getInsee().isEmpty()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean getPhysicalPersonDisplay(String typePerson) {
		if (typePerson.equals("PM")) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public Boolean physicalPersonDisplay(String typePerson) {
		if (typePerson.equals("PM")) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public Boolean MoralPersonDisplay(String typePerson) {
		if (typePerson.equals("PP")) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}
