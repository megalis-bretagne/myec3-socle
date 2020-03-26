package org.myec3.socle.synchro.core.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.myec3.socle.synchro.core.service.TraiterReponseSDMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service("traiterReponseSDMService")
public class TraiterReponseSDMServiceImpl implements TraiterReponseSDMService {

    private static final Logger logger = LoggerFactory.getLogger(TraiterReponseSDMServiceImpl.class);

    @Autowired
    @Qualifier("synchroIdentifiantExterneService")
    private SynchroIdentifiantExterneService synchroIdentifiantExterneService;

    @Autowired
    @Qualifier("applicationService")
    private ApplicationService applicationService;

    @Transactional
    public void traiterReponseOk(Resource resource, String responseToString) throws IOException {
        if ( !StringUtils.isEmpty(responseToString)) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(responseToString, Map.class);
            Object mpe = map.get("mpe");
            LinkedHashMap<String, Object> mpeHM = (LinkedHashMap<String, Object>) mpe;
            Object reponse = mpeHM.get("reponse");
            LinkedHashMap<String, Object> reponseHM = (LinkedHashMap<String, Object>) reponse;

            if (AgentProfile.class.equals(resource.getClass())) {
                if (reponseHM.get("Agent") instanceof LinkedHashMap) {
                    Object agent = reponseHM.get("Agent");
                    LinkedHashMap<String,Object> agentHM = (LinkedHashMap<String,Object>) agent;

                    SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.AGENT_PROFILE);
                    if (s==null){
                        Application sdmApplication = applicationService.findByName("SDM");
                        SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                        synchro.setApplication(sdmApplication);
                        synchro.setTypeRessource(ResourceType.AGENT_PROFILE);
                        synchro.setIdSocle(resource.getId());
                        if (agentHM.get("id") !=null){
                            synchro.setIdAppliExterne(Long.valueOf((Integer)agentHM.get("id")));
                        }
                        synchroIdentifiantExterneService.create(synchro);
                    }else {
                        s.setIdSocle(resource.getId());
                        if (agentHM.get("id") !=null){
                            s.setIdAppliExterne(Long.valueOf((Integer)agentHM.get("id")));
                        }
                        synchroIdentifiantExterneService.update(s);

                    }
                } else {
                    logger.error("Probleme de parsing de la reponse OK {}",responseToString);
                }
            }
            if (EmployeeProfile.class.equals(resource.getClass())) {
                if (reponseHM.get("Inscrit") instanceof LinkedHashMap) {
                    Object inscrit = reponseHM.get("Inscrit");
                    LinkedHashMap<String,Object> inscritHM = (LinkedHashMap<String,Object>) inscrit;

                    SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.EMPLOYEE_PROFILE);
                    if (s==null){
                        Application sdmApplication = applicationService.findByName("SDM");
                        SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                        synchro.setApplication(sdmApplication);
                        synchro.setTypeRessource(ResourceType.EMPLOYEE_PROFILE);
                        synchro.setIdSocle(resource.getId());
                        if (inscritHM.get("id") !=null){
                            synchro.setIdAppliExterne(Long.valueOf((Integer)inscritHM.get("id")));
                        }
                        synchroIdentifiantExterneService.create(synchro);
                    }else {
                        s.setIdSocle(resource.getId());
                        if (inscritHM.get("id") !=null){
                            s.setIdAppliExterne(Long.valueOf((Integer)inscritHM.get("id")));
                        }
                        synchroIdentifiantExterneService.update(s);

                    }
                } else {
                    logger.error("Probleme de parsing de la reponse OK {}",responseToString);
                }
            }
            if (Organism.class.equals(resource.getClass())) {
                //{"mpe":{"reponse":{"statutReponse":"OK","organisme":[{"id":null,"acronyme":"a1b","categorieInsee":null,"sigle":"Syndicat Mixte M\u00e9galis Bretagne","siren":"253514491","denomination":"","nic":"00047","adresse":{"rue":"","codePostal":"35510","ville":"CESSON SEVIGNE","pays":"France"},"description":null,"logo":[]}]}}}
                if (reponseHM.get("organisme") instanceof ArrayList) {
                    Object organisme = reponseHM.get("organisme");
                    ArrayList<LinkedHashMap<String,String>> organismeHM = (ArrayList<LinkedHashMap<String,String>>) organisme;
                    for (LinkedHashMap<String,String> orga: organismeHM) {
                        SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.ORGANISM);
                        if (s==null){
                            Application sdmApplication = applicationService.findByName("SDM");
                            SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                            synchro.setApplication(sdmApplication);
                            synchro.setTypeRessource(ResourceType.ORGANISM);
                            synchro.setIdSocle(resource.getId());
                            if (orga.get("id") !=null){
                                synchro.setIdAppliExterne(Long.valueOf(orga.get("id")));
                            }
                            if (orga.get("acronyme") !=null){
                                synchro.setAcronyme(String.valueOf(orga.get("acronyme")));
                            }
                            synchroIdentifiantExterneService.create(synchro);
                        }else {
                            s.setIdSocle(resource.getId());
                            s.setAcronyme(String.valueOf(orga.get("acronyme")));
                            synchroIdentifiantExterneService.update(s);
                        }
                    }
                } else {
                    logger.error("Probleme de parsing de la reponse OK {}",responseToString);
                }
            }
            if (Company.class.equals(resource.getClass())) {
                if (reponseHM.get("Entreprise") instanceof LinkedHashMap) {
                    Object entreprise = reponseHM.get("Entreprise");
                    LinkedHashMap<String,Object> ent = (LinkedHashMap<String,Object>) entreprise;

                    SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.COMPANY);
                    if (s==null){
                        Application sdmApplication = applicationService.findByName("SDM");
                        SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                        synchro.setApplication(sdmApplication);
                        synchro.setTypeRessource(ResourceType.COMPANY);
                        synchro.setIdSocle(resource.getId());
                        if (ent.get("id") !=null){
                            synchro.setIdAppliExterne(Long.valueOf((Integer)ent.get("id")));
                        }
                        synchroIdentifiantExterneService.create(synchro);
                    }else {
                        s.setIdSocle(resource.getId());
                        if (ent.get("id") !=null){
                            s.setIdAppliExterne(Long.valueOf((Integer)ent.get("id")));
                        }
                        synchroIdentifiantExterneService.update(s);

                    }
                } else {
                    logger.error("Probleme de parsing de la reponse OK {}",responseToString);
                }
            }
            if (OrganismDepartment.class.equals(resource.getClass())) {
                if (reponseHM.get("service") instanceof LinkedHashMap) {
                    Object service = reponseHM.get("service");
                    LinkedHashMap<String,Object> serv = (LinkedHashMap<String,Object>) service;

                    SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.ORGANISM_DEPARTMENT);
                    if (s==null){
                        Application sdmApplication = applicationService.findByName("SDM");
                        SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                        synchro.setApplication(sdmApplication);
                        synchro.setTypeRessource(ResourceType.ORGANISM_DEPARTMENT);
                        synchro.setIdSocle(resource.getId());
                        if (serv.get("id") !=null){
                            synchro.setIdAppliExterne(Long.valueOf((Integer)serv.get("id")));
                        }
                        synchroIdentifiantExterneService.create(synchro);
                    }else {
                        s.setIdSocle(resource.getId());
                        if (serv.get("id") !=null){
                            s.setIdAppliExterne(Long.valueOf((Integer)serv.get("id")));
                        }
                        synchroIdentifiantExterneService.update(s);

                    }
                } else {
                    logger.error("Probleme de parsing de la reponse OK {}",responseToString);
                }
            }
            if (Establishment.class.equals(resource.getClass())) {
                if (reponseHM.get("Etablissement") instanceof LinkedHashMap) {
                    Object etablissement = reponseHM.get("Etablissement");
                    LinkedHashMap<String,Object> etab = (LinkedHashMap<String,Object>) etablissement;

                    SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.ESTABLISHMENT);
                    if (s==null){
                        Application sdmApplication = applicationService.findByName("SDM");
                        SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
                        synchro.setApplication(sdmApplication);
                        synchro.setTypeRessource(ResourceType.ESTABLISHMENT);
                        synchro.setIdSocle(resource.getId());
                        if (etab.get("id") !=null){
                            synchro.setIdAppliExterne(Long.valueOf((Integer)etab.get("id")));
                        }
                        synchroIdentifiantExterneService.create(synchro);
                    }else {
                        s.setIdSocle(resource.getId());
                        if (etab.get("id") !=null){
                            s.setIdAppliExterne(Long.valueOf((Integer)etab.get("id")));
                        }
                        synchroIdentifiantExterneService.update(s);

                    }
                } else {
                    logger.error("Probleme de parsing de la reponse OK {}",responseToString);
                }
            }
        }
    }
}
