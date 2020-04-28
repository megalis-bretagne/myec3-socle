package org.myec3.socle.webapp.controller;

import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneDeltaService;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class SdmInitController {

    private static final Logger logger = LoggerFactory.getLogger(SdmInitController.class);

    @Autowired
    private SdmInitResourceClientimpl clientMps;

    @Autowired
    @Qualifier("synchroIdentifiantExterneDeltaService")
    private SynchroIdentifiantExterneDeltaService synchroIdentifiantExterneDeltaService;

    @Autowired
    @Qualifier("synchroIdentifiantExterneService")
    private SynchroIdentifiantExterneService synchroIdentifiantExterneService;

    @Autowired
    @Qualifier("sdmSynchroService")
    private SdmSynchroService sdmSynchroService;

    @RequestMapping(value = "/sdmInit/truncate/all", method = {RequestMethod.GET})
    @Transactional
    public String truncateAll() {
        synchroIdentifiantExterneDeltaService.truncate();
        logger.info("tuncate synchroIdentifiantExterneDelta ok");
        synchroIdentifiantExterneService.truncate();
        logger.info("tuncate synchroIdentifiantExterneService ok");

        return "truncate synchroIdentifiantExterneDelta et synchroIdentifiantExterneService ok";

    }

    @RequestMapping(value = "/sdmInit/truncate/agents", method = {RequestMethod.GET})
    @Transactional
    public String truncateAgents() {
        return deleteResource(ResourceType.AGENT_PROFILE);
    }

    @RequestMapping(value = "/sdmInit/truncate/organismes", method = {RequestMethod.GET})
    @Transactional
    public String truncateOrganismes() {
        return deleteResource(ResourceType.ORGANISM);
    }

    @RequestMapping(value = "/sdmInit/truncate/services", method = {RequestMethod.GET})
    @Transactional
    public String truncateServices() {
        return deleteResource(ResourceType.ORGANISM_DEPARTMENT);
    }


    @RequestMapping(value = "/sdmInit/truncate/etablissements", method = {RequestMethod.GET})
    @Transactional
    public String truncateetEablissements() {
        return deleteResource(ResourceType.ESTABLISHMENT);
    }

    @RequestMapping(value = "/sdmInit/truncate/entreprises", method = {RequestMethod.GET})
    @Transactional
    public String truncateEntreprises() {
        return deleteResource(ResourceType.COMPANY);
    }

    @RequestMapping(value = "/sdmInit/truncate/inscrit", method = {RequestMethod.GET})
    @Transactional
    public String truncateInscrit() {
        return deleteResource(ResourceType.EMPLOYEE_PROFILE);
    }

    public String deleteResource(ResourceType type) {
        return "truncate "+type.getLabel() +" dans synchroIdentifiantExterneDelta et synchroIdentifiantExterneService ok";

    }
    @RequestMapping(value = "/sdmInit/all", method = {RequestMethod.GET})
    public String sdmInitAll() {

        //Etape 1 : on vide les tables
        //String result = this.truncateAll();

        String result= this.sdmInitOrgannismes();
        result += this.sdmInitServices();
        result+= this.sdmInitAgents();
        result+= this.sdmInitEntreprises();
        result+= this.sdmInitEtablissements();

        //retour+= this.sdmInitInscrit();

        return result;

    }

    @RequestMapping(value = "/sdmInit/agents", method = {RequestMethod.GET})
    public String sdmInitAgents() {

        String retour = "<h1> Import des Agents</h1></br></br>";

        int page = 1;
        int nb_probleme=0;

        boolean resteDesPages = true;
        HashMap<Integer,Future<Boolean>> resultFuture = new HashMap();

        while (resteDesPages) {

            List<LinkedHashMap<String, Object>> agentsListe = null;
            try {
                Object response = clientMps.get("https://marches.megalis.bretagne.bzh/app.php/api/v1/agents.json", page);
                LinkedHashMap<String, Object> services = (LinkedHashMap<String, Object>) response;
                agentsListe = (List<LinkedHashMap<String, Object>>) services.get("agents");

            } catch (Exception e) {
                retour += "taitement page:" + page + "en erreur</br>";
                logger.error("probleme sur la page {}", page);
            }

            if (agentsListe == null) {
                page++;
                nb_probleme++;
                if(nb_probleme > 5){
                    //si on rencntre plus de 5 page en erreurs on sort
                    resteDesPages=false;
                }
            } else {
                if (agentsListe.size() > 0) {
                    resultFuture.put(page,sdmSynchroService.traiterListeAgentsSdm(agentsListe,page));
                    page++;

                } else {
                    resteDesPages = false;
                }
            }

        }

        for(Integer pageKey : resultFuture.keySet()){
            try{
                resultFuture.get(pageKey).get();
                retour += "taitement page:" + pageKey + "ok</br>";
                logger.info("traitement agents ok  page:{}", pageKey);
            }

            catch (InterruptedException | ExecutionException e){
                retour += "taitement async page:" + pageKey + " en erreur</br>";
                logger.error("Erreur traitement async pour la page {}",pageKey);
            }
        }
        return retour;
    }


    @RequestMapping(value = "/sdmInit/organismes", method = {RequestMethod.GET})
    public String sdmInitOrgannismes() {

        String retour = "<h1> Import des Organismes</h1></br></br>";

        int page = 1;
        int nb_probleme=0;

        boolean resteDesPages = true;
        HashMap<Integer,Future<Boolean>> resultFuture = new HashMap();

        while (resteDesPages) {

            List<LinkedHashMap<String, Object>> organismesListe = null;

            try {
                Object response = clientMps.get("https://marches.megalis.bretagne.bzh/app.php/api/v1/organismes.json", page);
                LinkedHashMap<String, Object> organismes = (LinkedHashMap<String, Object>) response;
                organismesListe = (List<LinkedHashMap<String, Object>>) organismes.get("organismes");

            } catch (Exception e) {
                retour += "taitement page:" + page + "en erreur</br>";
                logger.error("probleme sur la page {}", page);
            }

            if (organismesListe == null) {
                page++;
                nb_probleme++;
                if(nb_probleme > 5){
                    //si on rencntre plus de 5 page en erreurs on sort
                    resteDesPages=false;
                }
            } else {
                if (organismesListe.size() > 0) {
                    resultFuture.put(page,sdmSynchroService.traiterListeOrganismesListeSdm(organismesListe,page));
                    page++;

                } else {
                    resteDesPages = false;
                }
            }
        }

        for(Integer pageKey : resultFuture.keySet()){
            try{
                resultFuture.get(pageKey).get();
                retour += "taitement page:" + pageKey + "ok</br>";
                logger.info("traitement organismes ok  page:{}", pageKey);
            }

            catch (InterruptedException | ExecutionException e){
                retour += "taitement async page:" + pageKey + " en erreur</br>";
                logger.error("Erreur traitement async pour la page {}",pageKey);

            }
        }
        return retour;
    }


    @RequestMapping(value = "/sdmInit/services", method = {RequestMethod.GET})
    public String sdmInitServices() {

        String retour = "<h2> Import des services</h2></br>";
        int page = 1;
        int nb_probleme=0;

        boolean resteDesPages = true;
        HashMap<Integer,Future<Boolean>> resultFuture = new HashMap();

        while (resteDesPages) {

            List<LinkedHashMap<String, Object>> servicesListe = null;
            try {
                Object response = clientMps.get("https://marches.megalis.bretagne.bzh/app.php/api/v1/services.json", page);
                LinkedHashMap<String, Object> services = (LinkedHashMap<String, Object>) response;
                servicesListe = (List<LinkedHashMap<String, Object>>) services.get("services");

            } catch (Exception e) {
                retour += "taitement page:" + page + "en erreur</br>";
                logger.error("probleme sur la page {}", page);
            }

            if (servicesListe == null) {
                page++;
                nb_probleme++;
                if(nb_probleme > 5){
                    //si on rencntre plus de 5 page en erreurs on sort
                    resteDesPages=false;
                }
            } else {
                if (servicesListe.size() > 0) {
                    resultFuture.put(page,sdmSynchroService.traiterListeServicesListeSdm(servicesListe,page));
                    page++;

                } else {
                    resteDesPages = false;
                }
            }
        }

        for(Integer pageKey : resultFuture.keySet()){
            try{
                resultFuture.get(pageKey).get();
                retour += "taitement page:" + pageKey + "ok</br>";
                logger.info("traitement services ok  page:{}", pageKey);
            }

            catch (InterruptedException | ExecutionException e){
                retour += "taitement async page:" + pageKey + " en erreur</br>";
                logger.error("Erreur traitement async pour la page {}",pageKey);

            }
        }
        return retour;
    }

    @RequestMapping(value = "/sdmInit/etablissements", method = {RequestMethod.GET})
    public String sdmInitEtablissements() {

        String retour = "<h2> Import des etablissements</h2></br>";

        int page = 1;
        int nb_probleme=0;

        boolean resteDesPages = true;
        HashMap<Integer,Future<Boolean>> resultFuture = new HashMap();

        while (resteDesPages) {

            List<LinkedHashMap<String, Object>> etablissementsListe = null;

            try {
                Object response = clientMps.get("https://marches.megalis.bretagne.bzh/app.php/api/v1/etablissements.json", page);
                LinkedHashMap<String, Object> services = (LinkedHashMap<String, Object>) response;
                etablissementsListe = (List<LinkedHashMap<String, Object>>) services.get("etablissements");
            } catch (Exception e) {
                retour += "taitement page:" + page + "en erreur</br>";
                logger.error("probleme sur la page {}", page);
            }

            if (etablissementsListe == null) {
                page++;
                nb_probleme++;
                if(nb_probleme > 5){
                    //si on rencntre plus de 5 page en erreurs on sort
                    resteDesPages=false;
                }
            } else {
                if (etablissementsListe.size() > 0) {
                    resultFuture.put(page,sdmSynchroService.traiterListeEtablissementsListeSdm(etablissementsListe,page));
                    page++;

                } else {
                    resteDesPages = false;
                }
            }
        }

        for(Integer pageKey : resultFuture.keySet()){
            try{
                resultFuture.get(pageKey).get();
                retour += "taitement page:" + pageKey + "ok</br>";
                logger.info("traitement etablissements ok  page:{}", pageKey);
            }

            catch (InterruptedException | ExecutionException e){
                retour += "taitement async page:" + pageKey + " en erreur</br>";
                logger.error("Erreur traitement async pour la page {}",pageKey);

            }
        }
        return retour;
    }




    @RequestMapping(value = "/sdmInit/entreprises", method = {RequestMethod.GET})
    public String sdmInitEntreprises() {

        String retour = "<h2> Import des entreprises</h2></br>";

        int page = 1;
        int nb_probleme=0;

        boolean resteDesPages = true;
        HashMap<Integer,Future<Boolean>> resultFuture = new HashMap();
        while (resteDesPages) {

            List<LinkedHashMap<String, Object>> entreprisesListe = null;
            try {
                Object response = clientMps.get("https://marches.megalis.bretagne.bzh/app.php/api/v1/entreprises.json", page);
                LinkedHashMap<String, Object> services = (LinkedHashMap<String, Object>) response;
                entreprisesListe = (List<LinkedHashMap<String, Object>>) services.get("entreprises");

            } catch (Exception e) {
                retour += "taitement page:" + page + "en erreur</br>";
                logger.error("probleme sur la page {}", page);
            }

            if (entreprisesListe == null) {
                page++;
                nb_probleme++;
                if(nb_probleme > 5){
                    //si on rencntre plus de 5 page en erreurs on sort
                    resteDesPages=false;
                }
            } else {
                if (entreprisesListe.size() > 0) {
                    resultFuture.put(page,sdmSynchroService.traiterListeEntreprisesListeSdm(entreprisesListe,page));
                    page++;

                } else {
                    resteDesPages = false;
                }
            }

        }

        for(Integer pageKey : resultFuture.keySet()){
            try{
                resultFuture.get(pageKey).get();
                retour += "taitement page:" + pageKey + "ok</br>";
                logger.info("traitement entreprises ok  page:{}", pageKey);
            }

            catch (InterruptedException | ExecutionException e){
                retour += "taitement async page:" + pageKey + " en erreur</br>";
                logger.error("Erreur traitement async pour la page {}",pageKey);

            }

        }


        return retour;
    }


    @RequestMapping(value = "/sdmInit/inscrits", method = {RequestMethod.GET})
    public String sdmInitInscrits() {

        String retour = "<h2> Import des inscrits</h2></br>";

        int page = 1;
        int nb_probleme=0;

        boolean resteDesPages = true;
        HashMap<Integer,Future<Boolean>> resultFuture = new HashMap();

        while (resteDesPages) {

            List<LinkedHashMap<String, Object>> inscritsListe = null;
            try {
                Object response = clientMps.get("https://marches.megalis.bretagne.bzh/app.php/api/v1/inscrits.json", page);
                LinkedHashMap<String, Object> services = (LinkedHashMap<String, Object>) response;
                inscritsListe = (List<LinkedHashMap<String, Object>>) services.get("inscrits");

            } catch (Exception e) {
                retour += "taitement page:" + page + "en erreur</br>";
                logger.error("probleme sur la page {}", page);
            }

            if (inscritsListe == null) {
                page++;
                nb_probleme++;
                if(nb_probleme > 5){
                    //si on rencntre plus de 5 page en erreurs on sort
                    resteDesPages=false;
                }
            } else {
                if (inscritsListe.size() > 0) {
                    resultFuture.put(page,sdmSynchroService.traiterListeInscritsListeSdm(inscritsListe,page));
                    page++;

                } else {
                    resteDesPages = false;
                }
            }
        }

        for(Integer pageKey : resultFuture.keySet()){
            try{
                resultFuture.get(pageKey).get();
                retour += "taitement page:" + pageKey + "ok</br>";
                logger.info("traitement inscrits ok  page:{}", pageKey);
            }

            catch (InterruptedException | ExecutionException e){
                retour += "taitement async page:" + pageKey + " en erreur</br>";
                logger.error("Erreur traitement async pour la page {}",pageKey);

            }
        }
        return retour;
    }



}
