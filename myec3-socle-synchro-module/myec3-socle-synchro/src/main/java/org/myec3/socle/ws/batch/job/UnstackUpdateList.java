package org.myec3.socle.ws.batch.job;

import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Component
public class UnstackUpdateList {

    private static Logger logger = LoggerFactory.getLogger(UnstackUpdateList.class);

    @Autowired
    @Qualifier("mpsUpdateJobService")
    private MpsUpdateJobService mpsUpdateJobService;

    @Autowired
    @Qualifier("companyService")
    private CompanyService companyService;

    @Autowired
    @Qualifier("establishmentService")
    private EstablishmentService establishmentService;

    @Autowired
    @Qualifier("personService")
    private PersonService personService;

    @Autowired
    @Qualifier("inseeGeoCodeService")
    private InseeGeoCodeService inseeGeoCodeService;

    @Autowired
    @Qualifier("inseeLegalCategoryService")
    private InseeLegalCategoryService inseeLegalCategoryService;

    @Autowired
    @Qualifier("employeeProfileService")
    private EmployeeProfileService employeeProfileService;

    @Autowired
    @Qualifier("companySynchronizer")
    private ResourceSynchronizationManager<Company> companySynchronizer;

    @Autowired
    @Qualifier("establishmentSynchronizer")
    private ResourceSynchronizationManager<Establishment> establishmentSynchronizer;

    private CompanyWSinfo mpsWS = new MpsWsClient();

    private int updateErrors;

    private static final String MPS_UPDATE_BUNDLE_NAME = "mpsUpdate";
    private static final ResourceBundle MPSUPDATE_BUNDLE = ResourceBundle.getBundle(MPS_UPDATE_BUNDLE_NAME);
    public static final String UPDATE_LIMIT_MPS = MPSUPDATE_BUNDLE.getString("mpsUpdate.limit");

    public void unstackUpdateResource() {

        updateErrors = 0;

        Map<Long, String> updateErrorId = new HashMap();

        logger.debug("Unstack CRON !");

        // We get all the MpsUpdateJob from BDD, with limit
        List<MpsUpdateJob> listToUnstack = this.mpsUpdateJobService.findLimit(Integer.valueOf(UPDATE_LIMIT_MPS));

        if (listToUnstack.isEmpty()) {
            logger.info("No resources to update !");
        }

        // go threw each of the resource to update
        for (MpsUpdateJob resourceToUnstack : listToUnstack) {

            // check if the hostname matches the db entry
                logger.debug("Processing resource : " + resourceToUnstack.toString());

                // if it's a Company
                if (resourceToUnstack.getType().equals(ResourceType.COMPANY.getLabel())) {

                    // find the company from db
                    Company companyToUpdate = this.companyService.findOne(resourceToUnstack.getId());

                    // if Company from db is not null and is not a foreign
                    if (companyToUpdate != null && companyToUpdate.getForeignIdentifier().equals(Boolean.FALSE)) {
                        Company tmpCompany = new Company();
                        Establishment tmpEstablishment = new Establishment();

                        try {
                            // get the company data from MPS
                            // first check if siren is valid
                            if (companyToUpdate.getSiren() != null && companyToUpdate.getSiren().length() == 9
                                    && this.companyService.isSirenValid(companyToUpdate.getSiren())) {
                                tmpCompany = this.mpsWS.updateCompany(companyToUpdate.getSiren(),
                                        inseeLegalCategoryService);
                            }
                            // siren wasn't valid
                            else {
                                logger.warn("Company Siren is null or invalid ! Not contacting MPS for SIREN : "
                                        + companyToUpdate.getSiren());
                                this.companyUpdateError(companyToUpdate, updateErrorId, resourceToUnstack);
                                continue;
                            }

                            // We need the need to update Company with HeadOffice informations
                            // Get the headOffice data from MPS
                            if (tmpCompany.getNic() != null && tmpCompany.getNic().length() == 5
                                    && this.companyService.isSiretValid(companyToUpdate.getSiren(),
                                    tmpCompany.getNic())) {
                                tmpEstablishment = this.mpsWS
                                        .getEstablishment(companyToUpdate.getSiren() + tmpCompany.getNic());
                            } else {
                                logger.warn(
                                        "Company HeadOffice Siret is invalid or null ! Not contacting MPS for SIRET : "
                                                + companyToUpdate.getSiren() + tmpCompany.getNic());
                                this.companyUpdateError(companyToUpdate, updateErrorId, resourceToUnstack);
                                continue;
                            }
                        } catch (FileNotFoundException e) {
                            logger.warn("Contacting MPS with invalid siret : " + e);
                            this.companyUpdateError(companyToUpdate, updateErrorId, resourceToUnstack);
                            continue;

                        } catch (IOException e) {
                            logger.warn("MPS is not responding : " + e);
                            this.companyUpdateError(companyToUpdate, updateErrorId, resourceToUnstack);
                            continue;
                        } catch (Exception e) {
                            logger.warn("Error occured while requesting MPS : " + e);
                            this.companyUpdateError(companyToUpdate, updateErrorId, resourceToUnstack);
                            continue;
                        }

                        // compare lastUpdate in case headOffice need update
                        // not necessary to update Establishment headOffice
                        // as
                        // he'll
                        // be in update queue
                        if (resourceToUnstack.getPriority().equals(MpsUpdateTypeValue.MANUAL.getLabel())
                                || companyToUpdate.getLastUpdate() == null
                                || companyToUpdate.getLastUpdate().before(tmpEstablishment.getLastUpdate())) {
                            Date date = new Date();
                            companyToUpdate.setLastUpdate(date);
                            companyToUpdate.setCreationDate(tmpCompany.getCreationDate());

                            if (tmpEstablishment.getAddress().getInsee() != null
                                    && tmpEstablishment.getAddress().getInsee() != null) {
                                InseeGeoCode insee = this.inseeGeoCodeService
                                        .findByInseeCode(tmpEstablishment.getAddress().getInsee());
                                if (insee != null) {
                                    companyToUpdate.setInsee(insee.getInseeCode());
                                }
                            }

                            // update apeCode
                            if (tmpEstablishment.getApeCode() != null) {
                                companyToUpdate.setApeCode(tmpEstablishment.getApeCode());
                            } else if (companyToUpdate.getApeCode() == null) {
                                companyToUpdate.setApeCode("");
                            }

                            if (tmpEstablishment.getApeNafLabel() != null) {
                                companyToUpdate.setApeNafLabel(tmpEstablishment.getApeNafLabel());
                            } else if (companyToUpdate.getApeNafLabel() == null) {
                                companyToUpdate.setApeNafLabel("");
                            }
                            companyToUpdate.setNic(tmpCompany.getNic());
                            companyToUpdate.setLabel(tmpCompany.getLabel());

                            if(tmpCompany.getLegalCategory() != null){
                                companyToUpdate.setLegalCategory(tmpCompany.getLegalCategory());
                            }else{
                                logger.info("Company InseeLegalCategory is null. Setting it to 'Autre'");
                                CompanyINSEECat companyINSEECat = CompanyINSEECat.getByValue("AUTRE");
                                companyToUpdate.setLegalCategory(companyINSEECat);
                            }

                            AdministrativeState administrativeState = new AdministrativeState();
                            if (tmpCompany.getAdministrativeState() != null) {
                                if (tmpCompany.getAdministrativeState().getAdminStateValue().toString()
                                        .equals("Actif")) {
                                    administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_ACTIVE);
                                } else if (tmpCompany.getAdministrativeState().getAdminStateValue().toString()
                                        .equals("Cessée")) {
                                    administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_CEASED);
                                }
                                administrativeState.setAdminStateLastUpdated(
                                        tmpCompany.getAdministrativeState().getAdminStateLastUpdated());
                            } else {
                                administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);
                                administrativeState.setAdminStateLastUpdated(date);
                            }
                            companyToUpdate.setAdministrativeState(administrativeState);

                            companyToUpdate.setAddress(tmpCompany.getAddress());
                            try {
                                // persist the update in database
                                logger.debug("Update Company " + companyToUpdate.getId() + " completed.");

                                List<Person> tmpPersonList = tmpCompany.getResponsibles();

                                if (!CollectionUtils.isEmpty(tmpPersonList)) {
                                    for (Person companyResponsible : tmpPersonList) {
                                        companyResponsible.setCivility(Civility.MR);
                                        companyResponsible.setCompany(companyToUpdate);
                                        if (companyResponsible.getType() == null) {
                                            companyResponsible.setType("PP");
                                        }
                                    }
                                    companyToUpdate.clearResponsibles();
                                    companyToUpdate.setResponsibles(tmpPersonList);
                                }

                                this.companyService.update(companyToUpdate);

                                // Send notification to external
                                // applications
                                SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
                                String sendingApplication = "BATCH";
                                companySynchronizer.synchronizeUpdate(companyToUpdate, null, synchronizationType, sendingApplication);


                            } catch (Exception e) {
                                logger.error("Error while updating Company : " + e);
                                this.companyUpdateError(companyToUpdate, updateErrorId, resourceToUnstack);
                                continue;
                            }
                        } else {
                            logger.debug("No update for the Company :" + companyToUpdate.toString());
                            // necessaire ?
                            Date dateCompany = new Date();
                            companyToUpdate.setLastUpdate(dateCompany);
                            this.companyService.update(companyToUpdate);
                        }
                    } else {
                        logger.info("Error : Couldn't find company in database or company is a foreign company !");
                    }
                    this.mpsUpdateJobService.deleteById(resourceToUnstack.getId());
                    logger.info("mpsUpdateJobService deleted !");

                    // establishment update case
                } else if (resourceToUnstack.getType().equals(ResourceType.ESTABLISHMENT.getLabel())) {

                    // find the establishment from bdd
                    Establishment establishmentToUpdate = this.establishmentService
                            .findOne(resourceToUnstack.getId());
                    establishmentToUpdate.setEmployees(
                            employeeProfileService.findAllEmployeeProfilesByEstablishment(establishmentToUpdate));
                    companyService.populateCollections(establishmentToUpdate.getCompany());
                    // if establishment is in db and not null
                    if (establishmentToUpdate != null) {
                        logger.debug("Updating Establishment with following SIRET : "
                                + establishmentToUpdate.getSiret());
                        if (establishmentToUpdate.getSiret() == null) {
                            logger.warn("Establishment siret is null !");
                            this.establishmentUpdateError(establishmentToUpdate, updateErrorId, resourceToUnstack);
                            continue;
                        }
                        Establishment tmpEstablishment = new Establishment();

                        try {
                            // get the MPS data of the establishment
                            if (establishmentToUpdate.getCompany().getSiren().length() == 9
                                    && establishmentToUpdate.getNic().length() == 5
                                    && this.companyService.isSiretValid(
                                    establishmentToUpdate.getCompany().getSiren(),
                                    establishmentToUpdate.getNic())) {
                                tmpEstablishment = this.mpsWS
                                        .getEstablishment(establishmentToUpdate.getCompany().getSiren()
                                                + establishmentToUpdate.getNic());
                            } else {
                                logger.warn("Establishment siret is invalid ! Not contacting MPS for SIRET :"
                                        + establishmentToUpdate.getCompany().getSiren()
                                        + establishmentToUpdate.getNic());
                                this.establishmentUpdateError(establishmentToUpdate, updateErrorId,
                                        resourceToUnstack);
                                continue;
                            }
                        } catch (FileNotFoundException e) {
                            logger.error("Contacting MPS with invalid siret : " + resourceToUnstack.toString(), e);
                            this.establishmentUpdateError(establishmentToUpdate, updateErrorId, resourceToUnstack);
                            continue;

                        } catch (IOException e) {
                            logger.error("MPS is not responding : " + resourceToUnstack.toString(),e);
                            this.establishmentUpdateError(establishmentToUpdate, updateErrorId, resourceToUnstack);
                            continue;

                        } catch (Exception e) {
                            logger.error("Error while requesting MPS : " + resourceToUnstack.toString(),e);
                            this.establishmentUpdateError(establishmentToUpdate, updateErrorId, resourceToUnstack);
                            continue;
                        }

                        // compare lastUpdate in case establishment need
                        // update
                        if (resourceToUnstack.getPriority().equals(MpsUpdateTypeValue.MANUAL.getLabel())
                                || (establishmentToUpdate.getLastUpdate() == null) || (establishmentToUpdate
                                .getLastUpdate().before(tmpEstablishment.getLastUpdate()))) {

                            // update the establishment
                            Date date = new Date();
                            establishmentToUpdate.setLastUpdate(date);
                            establishmentToUpdate.setAddress(tmpEstablishment.getAddress());

                            if (tmpEstablishment.getAddress().getInsee() != null) {
                                InseeGeoCode insee = this.inseeGeoCodeService
                                        .findByInseeCode(tmpEstablishment.getAddress().getInsee());
                                establishmentToUpdate.getAddress().setInsee(insee.getInseeCode());
                            }

                            if (tmpEstablishment.getApeCode() != null) {
                                establishmentToUpdate.setApeCode(tmpEstablishment.getApeCode());
                            } else if (establishmentToUpdate.getApeCode() == null) {
                                establishmentToUpdate.setApeCode("");
                            }

                            if (tmpEstablishment.getApeNafLabel() != null) {
                                establishmentToUpdate.setApeNafLabel(tmpEstablishment.getApeNafLabel());
                            } else if (establishmentToUpdate.getApeNafLabel() == null) {
                                establishmentToUpdate.setApeNafLabel("");
                            }

                            establishmentToUpdate.setIsHeadOffice(tmpEstablishment.getIsHeadOffice());
                            AdministrativeState administrativeState = new AdministrativeState();
                            if (tmpEstablishment.getAdministrativeState() != null) {
                                if (tmpEstablishment.getAdministrativeState().getAdminStateValue().toString()
                                        .equals("Actif")) {
                                    administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_ACTIVE);
                                } else if (tmpEstablishment.getAdministrativeState().getAdminStateValue().toString()
                                        .equals("Cessée")) {
                                    administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_CEASED);
                                }
                                administrativeState.setAdminStateLastUpdated(
                                        tmpEstablishment.getAdministrativeState().getAdminStateLastUpdated());
                            } else {
                                administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);
                                administrativeState.setAdminStateLastUpdated(date);
                            }

                            establishmentToUpdate.setAdministrativeState(administrativeState);

                            try {
                                logger.debug("mpsUpdateJobService deleted !");

                                // persist the update in database
                                this.establishmentService.update(establishmentToUpdate);
                                logger.debug(
                                        "Update Establishment " + establishmentToUpdate.getId() + " completed.");

                                // Send notification to external
                                // applications
                                SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
                                String sendingApplication = "BATCH";
                                establishmentSynchronizer.synchronizeUpdate(establishmentToUpdate, null, synchronizationType, sendingApplication);

                            } catch (Exception e) {
                                logger.error("Error while updating Establishment : " + resourceToUnstack.toString(), e);
                                this.updateErrors += 1;
                                updateErrorId.put(establishmentToUpdate.getId(),
                                        ResourceType.ESTABLISHMENT.getLabel());
                                establishmentToUpdate = this.establishmentService
                                        .findOne(establishmentToUpdate.getId());
                                Date dateEstablishment = new Date();
                                establishmentToUpdate.setLastUpdate(dateEstablishment);
                                AdministrativeState administrativeStateEstablishment = new AdministrativeState();

                                administrativeStateEstablishment
                                        .setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);
                                administrativeStateEstablishment.setAdminStateLastUpdated(date);

                                establishmentToUpdate.setAdministrativeState(administrativeStateEstablishment);

                                // can throw exception if error validation
                                this.establishmentService.update(establishmentToUpdate);
                                continue;
                            } finally {
                                logger.info("Deleting mpsUpdate in DB {}", resourceToUnstack.getId());
                                this.mpsUpdateJobService.deleteById(resourceToUnstack.getId());
                                logger.info("mpsUpdateJobService deleted !");
                                continue;
                            }

                        }
                        // no update needed
                        else {
                            logger.warn("No update for the Establishment :" + establishmentToUpdate.toString());
                            this.mpsUpdateJobService.deleteById(resourceToUnstack.getId());
                            logger.debug("mpsUpdateJobService deleted !");
                            // necessaire ?
                            Date dateEstablishment = new Date();
                            establishmentToUpdate.setLastUpdate(dateEstablishment);
                            this.establishmentService.update(establishmentToUpdate);
                        }
                    } else {
                        logger.warn("Error : Couldn't find establishment in database !");
                        logger.info("Deleting mpsUpdate in DB {}", resourceToUnstack.getId());
                        this.mpsUpdateJobService.deleteById(resourceToUnstack.getId());
                        logger.info("mpsUpdateJobService deleted !");
                    }

                }
        }
        logger.info("Unstack mpsUpdate done. Number of errors : " + this.updateErrors);

        // in case of errors, display the id of resources
        if (this.updateErrors > 0) {
            logger.info("Ressources update failed :" + updateErrorId.toString());
        }
    }

    public void companyUpdateError(Company companyToUpdate, Map<Long, String> updateErrorId,
                                   MpsUpdateJob resourceToUnstack) {
        this.updateErrors += 1;
        updateErrorId.put(companyToUpdate.getId(), ResourceType.ESTABLISHMENT.getLabel());
        Date date = new Date();
        companyToUpdate.setLastUpdate(date);
        AdministrativeState administrativeState = new AdministrativeState();

        administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);

        administrativeState.setAdminStateLastUpdated(date);

        companyToUpdate.setAdministrativeState(administrativeState);
        this.mpsUpdateJobService.deleteById(resourceToUnstack.getId());
        try {
            this.companyService.update(companyToUpdate);
        } catch (Exception e) {
            logger.error("Erreur update company error", e);
        }
        logger.debug("mpsUpdateJobService deleted !");
    }

    public void establishmentUpdateError(Establishment establishmentToUpdate, Map<Long, String> updateErrorId,
                                         MpsUpdateJob resourceToUnstack) {
        this.updateErrors += 1;
        updateErrorId.put(establishmentToUpdate.getId(), ResourceType.ESTABLISHMENT.getLabel());
        Date date = new Date();
        establishmentToUpdate.setLastUpdate(date);
        AdministrativeState administrativeState = new AdministrativeState();

        administrativeState.setAdminStateValue(AdministrativeStateValue.STATUS_UNKNOWN);

        administrativeState.setAdminStateLastUpdated(date);

        establishmentToUpdate.setAdministrativeState(administrativeState);
        this.mpsUpdateJobService.deleteById(resourceToUnstack.getId());
        this.establishmentService.update(establishmentToUpdate);
        logger.debug("Update Establishment " + establishmentToUpdate.getId() + " completed.");

        logger.debug("mpsUpdateJobService deleted !");
    }
}
