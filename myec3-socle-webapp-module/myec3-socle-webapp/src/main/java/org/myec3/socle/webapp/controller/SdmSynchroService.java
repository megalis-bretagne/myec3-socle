package org.myec3.socle.webapp.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
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
import org.springframework.util.StringUtils;

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
    @Qualifier("establishmentService")
    private EstablishmentService establishmentService;

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
    @Async
    public Future<Boolean> traiterListeAgentsSdm(List<LinkedHashMap<String, Object>> agentsListe, int page) {
        Application sdmApplication = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);;

        for (LinkedHashMap<String, Object> sdmJsonAgent : agentsListe) {

            Integer idSdm = (Integer) sdmJsonAgent.get("id");
            String identifiant = (String) sdmJsonAgent.get("identifiant");

            try {
                User userSocle=null;
                if (!StringUtils.isEmpty(identifiant)) {
                    userSocle = userService.findByUsername(identifiant);
                }
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
                        if (json.length() > 9999){
                            delta.setJson(json.substring(0,9998));
                        }else{
                            delta.setJson(json);
                        }
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de l'agent :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                logger.error("PAGE {} - Exception.message : {}",page,e.getMessage());
            }
        }

        return new AsyncResult<>(Boolean.TRUE);
    }

    @Transactional
    @Async
    public Future<Boolean> traiterListeOrganismesListeSdm(List<LinkedHashMap<String, Object>> organismesListe, int page) {
        Application sdmApplication = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);;

        for (LinkedHashMap<String, Object> sdmJsonOrganisme : organismesListe) {

            Integer idSdm = (Integer) sdmJsonOrganisme.get("id");
            String acronyme = (String) sdmJsonOrganisme.get("acronyme");

            try {
                Organism organismSocle =null;
                if (!StringUtils.isEmpty(acronyme)){
                    organismSocle = organismService.findByAcronym(acronyme);
                }
                if (organismSocle != null) {
                    SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                    synchro.setApplication(sdmApplication);
                    synchro.setTypeRessource(ResourceType.ORGANISM);
                    synchro.setIdSocle(organismSocle.getId());
                    synchro.setIdAppliExterne(Long.valueOf(idSdm));
                    synchro.setAcronyme(organismSocle.getAcronym());

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
                        if (json.length() > 9999){
                            delta.setJson(json.substring(0,9998));
                        }else{
                            delta.setJson(json);
                        }
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de l'organisme :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                logger.error("PAGE {} - Exception.message : {}",page,e.getMessage());
            }
        }
        return new AsyncResult<>(Boolean.TRUE);
    }

    @Transactional
    @Async
    public Future<Boolean> traiterListeServicesListeSdm(List<LinkedHashMap<String, Object>> servicesListe, int page) {
        Application sdmApplication = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);;

        for (LinkedHashMap<String, Object> sdmJsonService : servicesListe) {

            Integer idSdm = (Integer) sdmJsonService.get("id");
            String idExterne = (String) sdmJsonService.get("idExterne");

            try {
                OrganismDepartment organismDepartmentSocle =null;
                if (!StringUtils.isEmpty(idExterne)){
                    organismDepartmentSocle = organismDepartmentService.findByExternalId(Long.valueOf(idExterne));
                }

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
                        if (json.length() > 9999){
                            delta.setJson(json.substring(0,9998));
                        }else{
                            delta.setJson(json);
                        }
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json du service :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                logger.error("PAGE {} - Exception.message : {}",page,e.getMessage());
            }
        }
        return new AsyncResult<>(Boolean.TRUE);
    }

    @Transactional
    @Async
    public Future<Boolean> traiterListeEntreprisesListeSdm(List<LinkedHashMap<String, Object>> entreprisesListe,int numPage) {

        Application sdmApplication = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);;

        for (LinkedHashMap<String, Object> sdmJsonEntreprise : entreprisesListe) {

            Integer idSdm = (Integer) sdmJsonEntreprise.get("id");
            String siren = (String) sdmJsonEntreprise.get("siren");

            try {
                Company company=null;
                if (!StringUtils.isEmpty(siren)){
                    company = companyService.findBySiren(siren);
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
                        if (json.length() > 9999){
                            delta.setJson(json.substring(0,9998));
                        }else{
                            delta.setJson(json);
                        }
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

    @Transactional
    @Async
    public Future<Boolean> traiterListeEtablissementsListeSdm(List<LinkedHashMap<String, Object>> etablissementsListe, int numPage) {
        Application sdmApplication = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);;

        for (LinkedHashMap<String, Object> sdmJsonEtablissement : etablissementsListe) {

            Integer idSdm = (Integer) sdmJsonEtablissement.get("id");
            String siret = (String) sdmJsonEtablissement.get("siret");

            try {

                Establishment establishment=null;
                if (!StringUtils.isEmpty(siret) && siret.length() == 14  ){
                    establishment = establishmentService.findByNic(siret.substring(0,9),siret.substring(9,14));
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
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = new HashMap<>();
                    String json = null;
                    try {
                        json = mapper.writeValueAsString(sdmJsonEtablissement);
                        if (json.length() > 9999){
                            delta.setJson(json.substring(0,9998));
                        }else{
                            delta.setJson(json);
                        }
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de la establishment :{} - colonne JSON de la table delta =NULL", idSdm);
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

    @Transactional
    @Async
    public Future<Boolean> traiterListeInscritsListeSdm(List<LinkedHashMap<String, Object>> inscritsListe, int page) {

        Application sdmApplication = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);;

        for (LinkedHashMap<String, Object> sdmJsonInscrit : inscritsListe) {

            Integer idSdm = (Integer) sdmJsonInscrit.get("id");
            String login = (String) sdmJsonInscrit.get("login");

            try {

                User userSocle=null;
                if (!StringUtils.isEmpty(login)){
                    userSocle = userService.findByUsername(login);
                }

                if (userSocle != null) {
                    SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                    synchro.setApplication(sdmApplication);
                    synchro.setTypeRessource(ResourceType.EMPLOYEE_PROFILE);
                    synchro.setIdSocle(userSocle.getId());
                    synchro.setIdAppliExterne(Long.valueOf(idSdm));

                    synchroIdentifiantExterneService.create(synchro);

                } else {

                    //cas je ne trouve pas le user dans le socle, on alimente la table synchro delta
                    SynchroIdentifiantExterneDelta delta = new SynchroIdentifiantExterneDelta();

                    delta.setApplication(sdmApplication);
                    delta.setTypeRessource(ResourceType.EMPLOYEE_PROFILE);
                    delta.setIdSocle(null);
                    delta.setIdAppliExterne(Long.valueOf(idSdm));
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> map = new HashMap<>();
                    String json = null;
                    try {
                        json = mapper.writeValueAsString(sdmJsonInscrit);
                        if (json.length() > 9999){
                            delta.setJson(json.substring(0,9998));
                        }else{
                            delta.setJson(json);
                        }
                    } catch (JsonProcessingException ex) {
                        logger.error("convertion en json de la employee :{} - colonne JSON de la table delta =NULL", idSdm);
                    }

                    synchroIdentifiantExterneDeltaService.create(delta);
                }

            } catch (Exception e) {
                logger.error("PAGE {} - Exception.message : {}",page,e.getMessage());
            }
        }
        return new AsyncResult<>(Boolean.TRUE);
    }
}
