package org.myec3.socle.webapp.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterneDelta;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneDeltaService;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NonUniqueResultException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Component("sdmSynchroService")
public class SdmSynchroService {

    private static final Logger logger = LoggerFactory.getLogger(SdmSynchroService.class);

    @Autowired
    @Qualifier("synchroIdentifiantExterneDeltaService")
    private SynchroIdentifiantExterneDeltaService synchroIdentifiantExterneDeltaService;

    @Autowired
    @Qualifier("synchroIdentifiantExterneService")
    private SynchroIdentifiantExterneService synchroIdentifiantExterneService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("organismService")
    private OrganismService organismService;

    @Autowired
    @Qualifier("organismDepartmentService")
    private OrganismDepartmentService organismDepartmentService;

    @Autowired
    @Qualifier("companyService")
    private CompanyService companyService;

    @Autowired
    @Qualifier("applicationService")
    private ApplicationService applicationService;

    @Transactional
    public void traiterListeAgentsSdm(Application sdmApplication, List<LinkedHashMap<String, Object>> agentsListe) {
        for (LinkedHashMap<String, Object> sdmJsonAgent : agentsListe) {

            Integer idSdm = (Integer) sdmJsonAgent.get("id");
            String identifiant = (String) sdmJsonAgent.get("identifiant");

            try {
                User userSocle = userService.findByUsername(identifiant);

                if (userSocle != null) {
                    SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                    synchro.setApplication(sdmApplication);
                    synchro.setTypeRessource(ResourceType.AGENT_PROFILE);
                    synchro.setIdSocle(userSocle.getId());
                    synchro.setIdAppliExterne(Long.valueOf(idSdm));

                    synchroIdentifiantExterneService.create(synchro);

                } else {

                    //cas je ne trouve pas le user dans le socle, on alimente la table synchro delta
                    SynchroIdentifiantExterneDelta delta = new SynchroIdentifiantExterneDelta();

                    delta.setApplication(sdmApplication);
                    delta.setTypeRessource(ResourceType.AGENT_PROFILE);
                    delta.setIdSocle(null);
                    delta.setIdAppliExterne(Long.valueOf(idSdm));
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = new HashMap<>();
                    String json = null;
                    try {
                        json = mapper.writeValueAsString(sdmJsonAgent);
                        delta.setJson(json);
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de l'agent :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void traiterListeOrganismesListeSdm(Application sdmApplication, List<LinkedHashMap<String, Object>> organismesListe) {


        for (LinkedHashMap<String, Object> sdmJsonOrganisme : organismesListe) {

            Integer idSdm = (Integer) sdmJsonOrganisme.get("id");
            String acronyme = (String) sdmJsonOrganisme.get("acronyme");

            try {
                Organism organismSocle = organismService.findByAcronym(acronyme);

                if (organismSocle != null) {
                    SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                    synchro.setApplication(sdmApplication);
                    synchro.setTypeRessource(ResourceType.ORGANISM);
                    synchro.setIdSocle(organismSocle.getId());
                    synchro.setIdAppliExterne(Long.valueOf(idSdm));

                    synchroIdentifiantExterneService.create(synchro);

                } else {

                    //cas je ne trouve pas le user dans le socle, on alimente la table synchro delta
                    SynchroIdentifiantExterneDelta delta = new SynchroIdentifiantExterneDelta();

                    delta.setApplication(sdmApplication);
                    delta.setTypeRessource(ResourceType.ORGANISM);
                    delta.setIdSocle(null);
                    delta.setIdAppliExterne(Long.valueOf(idSdm));
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = new HashMap<>();
                    String json = null;
                    try {
                        json = mapper.writeValueAsString(sdmJsonOrganisme);
                        delta.setJson(json);
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de l'agent :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Transactional
    public void traiterListeServicesListeSdm(Application sdmApplication, List<LinkedHashMap<String, Object>> servicesListe) {

        for (LinkedHashMap<String, Object> sdmJsonService : servicesListe) {

            Integer idSdm = (Integer) sdmJsonService.get("id");
            String idExterne = (String) sdmJsonService.get("idExterne");

            try {
                OrganismDepartment organismDepartmentSocle = organismDepartmentService.findByExternalId(Long.valueOf(idExterne));

                if (organismDepartmentSocle != null) {
                    SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                    synchro.setApplication(sdmApplication);
                    synchro.setTypeRessource(ResourceType.ORGANISM_DEPARTMENT);
                    synchro.setIdSocle(organismDepartmentSocle.getId());
                    synchro.setIdAppliExterne(Long.valueOf(idSdm));

                    synchroIdentifiantExterneService.create(synchro);

                } else {

                    //cas je ne trouve pas le user dans le socle, on alimente la table synchro delta
                    SynchroIdentifiantExterneDelta delta = new SynchroIdentifiantExterneDelta();

                    delta.setApplication(sdmApplication);
                    delta.setTypeRessource(ResourceType.ORGANISM_DEPARTMENT);
                    delta.setIdSocle(null);
                    delta.setIdAppliExterne(Long.valueOf(idSdm));
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = new HashMap<>();
                    String json = null;
                    try {
                        json = mapper.writeValueAsString(sdmJsonService);
                        delta.setJson(json);
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de l'agent :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    @Async
    public Future<Boolean> traiterListeEntreprisesListeSdm(List<LinkedHashMap<String, Object>> entreprisesListe,int numPage) {

        Application sdmApplication = applicationService.findByName("SDM");

        for (LinkedHashMap<String, Object> sdmJsonEntreprise : entreprisesListe) {

            Integer idSdm = (Integer) sdmJsonEntreprise.get("id");
            String siren = (String) sdmJsonEntreprise.get("siren");

            try {
                Company company=null;
                try {
                 company = companyService.findBySiren(siren);
                } catch (IncorrectResultSizeDataAccessException e) {
                    logger.error("PAGE {} - le siren: {} n'est pas unique dans la base du socle",numPage,siren);
                    company=null;
                }

                if (company != null) {
                    SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                    synchro.setApplication(sdmApplication);
                    synchro.setTypeRessource(ResourceType.COMPANY);
                    synchro.setIdSocle(company.getId());
                    synchro.setIdAppliExterne(Long.valueOf(idSdm));

                    synchroIdentifiantExterneService.create(synchro);

                } else {

                    //cas je ne trouve pas le user dans le socle, on alimente la table synchro delta
                    SynchroIdentifiantExterneDelta delta = new SynchroIdentifiantExterneDelta();

                    delta.setApplication(sdmApplication);
                    delta.setTypeRessource(ResourceType.COMPANY);
                    delta.setIdSocle(null);
                    delta.setIdAppliExterne(Long.valueOf(idSdm));
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = new HashMap<>();
                    String json = null;
                    try {
                        json = mapper.writeValueAsString(sdmJsonEntreprise);
                        delta.setJson(json);
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de la company :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                logger.error("PAGE {} - Exception.message : {}",numPage,e.getMessage());
            }
        }
        return new AsyncResult<>(Boolean.TRUE);
    }

    public Future<Boolean> traiterListeEtablissementsListeSdm(List<LinkedHashMap<String, Object>> etablissementsListe, int numPage) {
        Application sdmApplication = applicationService.findByName("SDM");

        for (LinkedHashMap<String, Object> sdmJsonEtablissement : etablissementsListe) {

            Integer idSdm = (Integer) sdmJsonEtablissement.get("id");
            Integer idEntreprise = (Integer) sdmJsonEtablissement.get("idEntreprise");

            try {
                Company company=null;
                Establishment establishment= null;
                try {
                    //TODO solution temporaire
                    //je récupère la compagny et si il y a que 1 establishment alors je l'utilise dans mon mapping
                    company =companyService.findCompanyByIdSdm(Long.valueOf(idEntreprise));
                    //company = companyService.findByAcronym()
                    if ( company !=null && company.getEstablishments() !=null && company.getEstablishments().size() ==1) {
                        establishment = company.getEstablishments().get(0);
                    }

                } catch (IncorrectResultSizeDataAccessException e) {
                    logger.error("PAGE {} - l'idEntreprise SDM: {} n'est pas unique dans la base du socle",numPage,idEntreprise);
                    company=null;
                }

                if (establishment != null) {
                    SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                    synchro.setApplication(sdmApplication);
                    synchro.setTypeRessource(ResourceType.ESTABLISHMENT);
                    synchro.setIdSocle(establishment.getId());
                    synchro.setIdAppliExterne(Long.valueOf(idSdm));

                    synchroIdentifiantExterneService.create(synchro);

                } else {

                    //cas je ne trouve pas le user dans le socle, on alimente la table synchro delta
                    SynchroIdentifiantExterneDelta delta = new SynchroIdentifiantExterneDelta();

                    delta.setApplication(sdmApplication);
                    delta.setTypeRessource(ResourceType.ESTABLISHMENT);
                    delta.setIdSocle(null);
                    delta.setIdAppliExterne(Long.valueOf(idSdm));
                    delta.setIdAppliExterne(Long.valueOf(idSdm));
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = new HashMap<>();
                    String json = null;
                    try {
                        json = mapper.writeValueAsString(sdmJsonEtablissement);
                        delta.setJson(json);
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de la company :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                logger.error("PAGE {} - Exception.message : {}",numPage,e.getMessage());
                e.printStackTrace();
            }
        }
        return new AsyncResult<>(Boolean.TRUE);


    }
}