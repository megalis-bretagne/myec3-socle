package org.myec3.socle.ws.server.resource.impl;

import javax.ws.rs.WebApplicationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.InseeBorough;
import org.myec3.socle.core.domain.model.InseeCanton;
import org.myec3.socle.core.domain.model.InseeCounty;
import org.myec3.socle.core.domain.model.InseeGeoCode;
import org.myec3.socle.core.domain.model.InseeGlobal;
import org.myec3.socle.core.domain.model.InseeRegion;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.InseeBoroughService;
import org.myec3.socle.core.service.InseeCantonService;
import org.myec3.socle.core.service.InseeCountyService;
import org.myec3.socle.core.service.InseeGeoCodeService;
import org.myec3.socle.core.service.InseeRegionService;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.ws.server.resource.InseeResource;
import org.myec3.socle.ws.server.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("prototype")
public class InseeResourceImpl implements InseeResource {

	private static final Logger logger = LogManager.getLogger(InseeResourceImpl.class);

	@Autowired
	@Qualifier("inseeGeoCodeService")
	private InseeGeoCodeService inseeGeoCodeService;

	@Autowired
	@Qualifier("inseeRegionService")
	private InseeRegionService inseeRegionService;

	@Autowired
	@Qualifier("inseeCountyService")
	private InseeCountyService inseeCountyService;

	@Autowired
	@Qualifier("inseeBoroughService")
	private InseeBoroughService inseeBoroughService;

	@Autowired
	@Qualifier("inseeCantonService")
	private InseeCantonService inseeCantonService;

	@Autowired
	@Qualifier("companyService")
	private CompanyService companyService;

	@Override
	public ResponseEntity getInseeInfoByCompany(Long companyId) {
		logger.debug("[getInseeInfoByInseeCode] GET following company : " + companyId);

		try {
			Company company = this.companyService.findOne(companyId);
			logger.debug("Found company : " + company.toString());
			InseeGlobal inseeInfos = new InseeGlobal();
			if (company.getInsee() != null && !company.getInsee().isEmpty()) {
				InseeGeoCode insee = this.inseeGeoCodeService.findByInseeCode(company.getInsee());
				logger.debug("Found inseeCode : " + insee);
				if (insee != null) {
					inseeInfos.setInseeCode(insee.getInseeCode());
					if (insee != null && insee.getReg() != null) {
						// Find the region
						InseeRegion inseeRegion = inseeRegionService.findOne(insee.getReg());
						if (inseeRegion != null) {
							logger.debug("Insee Region : " + inseeRegion.getNccenr());
							inseeInfos.setInseeRegion(inseeRegion.getNccenr());
						} else {
							logger.debug("Couldn't find inseeRegion !");
							inseeInfos.setInseeRegion("Non connue");
						}
					}

					if (insee != null && insee.getDep() != null) {
						// Find the department
						InseeCounty inseeCounty = inseeCountyService.findById(insee.getDep());
						if (inseeCounty != null) {
							logger.debug("Insee Departement : " + inseeCounty.getNccenr());
							inseeInfos.setInseeCounty(inseeCounty.getNccenr());
						} else {
							logger.debug("Couldn't find inseeCounty !");
							inseeInfos.setInseeCounty("Non connu");
						}
					} else {
						logger.debug("Couldn't find inseeCounty !");
						inseeInfos.setInseeCounty("Non connu");
					}

					if (insee != null && insee.getReg() != null && insee.getDep() != null && insee.getAr() != null) {
						// Find the borough
						InseeBorough inseeBorough = inseeBoroughService.findBourough(insee.getReg(), insee.getDep(),
								insee.getAr());
						if (inseeBorough != null) {
							logger.debug("Insee Arrondissement : " + inseeBorough.getNccenr());
							inseeInfos.setInseeBorough(inseeBorough.getNccenr());
						} else {
							logger.debug("Couldn't find inseeBorough !");
							inseeInfos.setInseeBorough("Non connu");
						}
					} else {
						logger.debug("Couldn't find inseeBorough !");
						inseeInfos.setInseeBorough("Non connu");
					}

					if (insee != null && insee.getReg() != null && insee.getDep() != null && insee.getCt() != null) {
						// Find the canton
						InseeCanton inseeCanton = inseeCantonService.findCanton(insee.getReg(), insee.getDep(),
								insee.getCt());

						if (inseeCanton == null || insee.getCt() == 99) {
							logger.debug("Couldn't find inseeCanton !");
							inseeInfos.setInseeCanton("Non connu");
						} else {
							logger.debug("Insee Canton : " + inseeCanton.getNccenr());
							inseeInfos.setInseeCanton(inseeCanton.getNccenr());
						}
					} else {
						logger.debug("Couldn't find inseeCanton !");
						inseeInfos.setInseeCanton("Non connu");
					}

					logger.info("[getInseeInfoByInseeCode] GET returning an response 200");
					return ResponseEntity.ok().body(inseeInfos);
				} else {
					// in case company have an inseeCode but it is not valid
					logger.info("Company have an invalid inseeCode ! Id : " + companyId);
					this.setEmptyInseeInfos(inseeInfos);
					return ResponseEntity.ok().body(inseeInfos);
				}
			} else {
				// in case company doesn't have inseeCode
				logger.info("Company doesn't have an inseeCode ! Id : " + companyId);
				this.setEmptyInseeInfos(inseeInfos);

				return ResponseEntity.ok().body(inseeInfos);
			}
		} catch (WebApplicationException ex) {
			logger.error("[getInseeInfoByInseeCode] WebApplicationException : " + ex.getMessage());
			return ResponseUtils.errorResponse(ex, MethodType.GET, logger);
		} catch (RuntimeException e) {
			logger.error("[getInseeInfoByInseeCode] Internal Server Error : ");
			logger.error(
					"Error Message :" + "An unexpected error has occured : " + e.getCause() + " " + e.getMessage());
			logger.error("Company ID : " + companyId);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	public void setEmptyInseeInfos(InseeGlobal inseeInfos) {
		inseeInfos.setInseeBorough("Non connu");
		inseeInfos.setInseeCanton("Non connu");
		inseeInfos.setInseeCode("Non connu");
		inseeInfos.setInseeCounty("Non connu");
		inseeInfos.setInseeRegion("Non connue");
	}

}
