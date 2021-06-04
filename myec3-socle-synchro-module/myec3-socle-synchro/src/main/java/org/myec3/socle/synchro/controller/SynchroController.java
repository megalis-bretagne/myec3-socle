package org.myec3.socle.synchro.controller;

import org.myec3.socle.core.domain.dto.OrganismLightDTO;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.*;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;
import org.myec3.socle.ws.client.impl.mps.response.ResponseEntreprises;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class SynchroController {

    private static final Logger logger = LoggerFactory.getLogger(SynchroController.class);
    private static final String ACTION_SOCIALE = "ACTION SOCIALE";

    @Autowired
    @Qualifier("agentProfileService")
    private AgentProfileService agentProfileService;

    @Autowired
    @Qualifier("employeeProfileService")
    private EmployeeProfileService employeeProfileService;


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
    @Qualifier("agentSynchronizer")
    private ResourceSynchronizationManager<AgentProfile> agentSynchronizer;

    @Autowired
    @Qualifier("employeeSynchronizer")
    private ResourceSynchronizationManager<EmployeeProfile> employeeSynchronizer;

    @Autowired
    @Qualifier("organismSynchronizer")
    private ResourceSynchronizationManager<Organism> organismSynchronizer;

    @Autowired
    @Qualifier("organismDepartmentSynchronizer")
    private ResourceSynchronizationManager<OrganismDepartment> organismDepartmentSynchronizer;

    @Autowired
    @Qualifier("companySynchronizer")
    private ResourceSynchronizationManager<Company> companySynchronizer;

    @Autowired
    @Qualifier("establishmentSynchronizer")
    private ResourceSynchronizationManager<Establishment> establishmentSynchronizer;

    @Autowired
    @Qualifier("applicationService")
    private ApplicationService applicationService;

    private MpsWsClient mpsWsClient = new MpsWsClient();

    @RequestMapping(value = "/jcms/agent/", method = {RequestMethod.GET})
    public String agentJcms(@RequestParam long id) {

        AgentProfile agent =agentProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("portail megalisbretagne");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());

        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        agentSynchronizer.synchronizeUpdate(agent, listApplicationIdToResynchronize, synchronizationType, sendingApplication);
        logger.info("synchro agent {}", agent.getUsername());

        return "synchro agent "+ agent.getName();
    }


    @RequestMapping(value = "/pastell/agent/", method = {RequestMethod.GET})
    public String agentPastell(@RequestParam long id) {

        AgentProfile agent =agentProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("Pastell");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());

        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        agentSynchronizer.synchronizeUpdate(agent, listApplicationIdToResynchronize, synchronizationType, sendingApplication);
        logger.info("synchro agent {}", agent.getUsername());

        return "synchro agent "+ agent.getName();
    }

    @RequestMapping(value = "/pastell/organism/", method = {RequestMethod.GET})
    public String organismePastell(@RequestParam long id) {
        Organism organism =organismService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("Pastell");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        organismSynchronizer.synchronizeUpdate(organism, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro organisme "+ organism.getName();
    }

    @RequestMapping(value = "/pastell/organismDepartment/", method = {RequestMethod.GET})
    public String organismDepartmentPastell(@RequestParam long id) {
        OrganismDepartment organismDepartment =organismDepartmentService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("Pastell");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        organismDepartmentSynchronizer.synchronizeUpdate(organismDepartment, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro organismDepartment "+ organismDepartment.getName();
    }



    @RequestMapping(value = "/sdm/agent/", method = {RequestMethod.GET})
    public String agent(@RequestParam long id) {

        AgentProfile agent =agentProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());

        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        agentSynchronizer.synchronizeUpdate(agent, listApplicationIdToResynchronize, synchronizationType, sendingApplication);
        logger.info("synchro agent {}", agent.getUsername());

        return "synchro agent "+ agent.getName();
    }

    @RequestMapping(value = "/sdm/organism/", method = {RequestMethod.GET})
    public String organisme(@RequestParam long id) {
        Organism organism =organismService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        organismSynchronizer.synchronizeUpdate(organism, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro organisme "+ organism.getName();
    }

    @RequestMapping(value = "/sdm/organismDepartment/", method = {RequestMethod.GET})
    public String organismDepartment(@RequestParam long id) {
        OrganismDepartment organismDepartment =organismDepartmentService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        organismDepartmentSynchronizer.synchronizeUpdate(organismDepartment, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro organismDepartment "+ organismDepartment.getName();
    }


    @RequestMapping(value = "/sdm/employee/", method = {RequestMethod.GET})
    public String employee(@RequestParam long id) {
        EmployeeProfile employeeProfile =employeeProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        employeeSynchronizer.synchronizeUpdate(employeeProfile, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro employee "+ employeeProfile.getName();
    }

    @RequestMapping(value = "/sdm/company/", method = {RequestMethod.GET})
    public String company(@RequestParam long id) {
        Company company =companyService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        companySynchronizer.synchronizeUpdate(company, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro emplyoee "+ company.getName();
    }

    @RequestMapping(value = "/sdm/establishment/", method = {RequestMethod.GET})
    public String establishment(@RequestParam long id) {
        Establishment establishment = establishmentService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        establishmentSynchronizer.synchronizeUpdate(establishment, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro emplyoee "+ establishment.getName();
    }

    /**
     * MEGALIS-152 - API to resync company label for SDM
     * @param launchUpdate set true to update data in DB and send synchro to SDM.
     *                     If false, launch the process and return result as string.
     * @return
     */
    @RequestMapping(value = "/organism/resync", method = {RequestMethod.GET})
    public String resyncCompanySdm(@RequestParam boolean launchUpdate) {
        logger.info("[RESYNC] Call resync Organism");

        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        // Get all organism subscribe to SDM
        List<OrganismLightDTO> organisms = organismService.findOrganismLight()
                .stream().filter(organismLightDTO -> organismLightDTO.getSiren() != null ).collect(Collectors.toList());

        logger.info("[RESYNC] call API siren for each organism : {}", organisms.size() );

        // Mise dans un Thread pour éviter les pb de timeout
        CompletableFuture.supplyAsync(() -> {
            StringBuilder result = new StringBuilder("");
            AtomicInteger index = new AtomicInteger(0);
            organisms.forEach( organismLightDTO -> {
                ResponseEntreprises entreprises = mpsWsClient.getInfoEntreprises(organismLightDTO.getSiren());
                if (entreprises.getEntreprise() == null) {
                    logger.info("[RESYNC] ["+organismLightDTO.getId()+"] ["+organismLightDTO.getSiren()+"] Pas de reponse de API INSEE");
                    result.append("[RESYNC] ["+organismLightDTO.getId()+"] ["+organismLightDTO.getSiren()+"] Pas de reponse de API INSEE <br/>");
                } else {
                    // Mise en Majuscule et sans accent
                    String labelSocle = UnaccentLetter.unaccentString(organismLightDTO.getLabel())
                            .toUpperCase();

                    String labelInsee = entreprises.getEntreprise().getLabel();
                    String city = entreprises.getEtablissement_siege().getAddress().getCity();

                    if (!labelSocle.equals(labelInsee) || (labelInsee.contains(ACTION_SOCIALE) && !labelInsee.contains(city)) ) {
                        // SI le libelle ACTION SOCIALE est présent et sans la présence de la commune, alors on ajoute la commune dans le label
                        if (labelInsee.contains(ACTION_SOCIALE) && !labelInsee.contains(city)) {
                            labelInsee = labelInsee + " DE "+city;
                        }
                        logger.info("[RESYNC] ["+organismLightDTO.getId()+"] ["+organismLightDTO.getSiren()+"] ["+ organismLightDTO.getLabel()+"] ==> ["+ labelInsee +"]");
                        result.append("["+organismLightDTO.getId()+"] ["+ organismLightDTO.getLabel()+"] ==> ["+ labelInsee +"] <br>");

                        if (launchUpdate) {
                            Organism organism = organismService.findOne(organismLightDTO.getId());
                            organism.setLabel(labelInsee);
                            organism = organismService.update(organism);
                            organismSynchronizer.synchronizeUpdate(organism, null, synchronizationType, sendingApplication);
                            logger.info("[RESYNC] Organism "+organismLightDTO.getId()+" Updated");
                        }
                    } else {
                        logger.info("[RESYNC] ["+organismLightDTO.getId()+"] ["+organismLightDTO.getSiren()+"] ["+ organismLightDTO.getLabel()+"] ==> PAS DE CHANGEMENT");
                    }
                }

                // Sleep d'1 seconde toutes les 4 itérations pour ne pas atteindre les 2000 requêtes / 10 minutes sur l'API INSEE
                if (index.incrementAndGet() >= 4) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    index.set(0);
                }

            });
            logger.info("[RESYNC] END");
            return result.toString();
        });
        return "inprogress";
    }

}
