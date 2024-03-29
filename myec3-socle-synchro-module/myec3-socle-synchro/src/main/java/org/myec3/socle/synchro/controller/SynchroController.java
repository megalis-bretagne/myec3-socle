package org.myec3.socle.synchro.controller;

import org.myec3.socle.core.constants.MyEc3ApplicationConstants;
import org.myec3.socle.core.domain.dto.OrganismLightDTO;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.myec3.socle.ws.client.impl.mps.MpsWsClient;
import org.myec3.socle.ws.client.impl.mps.response.ResponseEtablissement;
import org.myec3.socle.ws.client.impl.mps.response.ResponseUniteLegale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
public class SynchroController {

    private static final Logger logger = LoggerFactory.getLogger(SynchroController.class);
    private static final String ACTION_SOCIALE = "ACTION SOCIALE";
    private static final String CAISSE_ECOLE = "CAISSE DES ECOLES";

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

    private final MpsWsClient mpsWsClient = new MpsWsClient();

    @GetMapping(value = "/jcms/agent/")
    public String agentJcms(@RequestParam long id) {

        AgentProfile agent = agentProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.PORTAIL_MEGALIS_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());

        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        agentSynchronizer.synchronizeUpdate(agent, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);
        logger.info("synchro agent {}", agent.getUsername());

        return "synchro agent " + agent.getName();
    }


    @GetMapping(value = "/pastell/agent/")
    public String agentPastell(@RequestParam long id) {

        AgentProfile agent = agentProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.PASTELL_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());

        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        agentSynchronizer.synchronizeUpdate(agent, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);
        logger.info("synchro agent {}", agent.getUsername());

        return "synchro agent " + agent.getName();
    }

    @GetMapping(value = "/pastell/organism/")
    public String organismePastell(@RequestParam long id) {
        Organism organism = organismService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.PASTELL_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        organismSynchronizer.synchronizeUpdate(organism, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro organism " + organism.getName();
    }


    @GetMapping(value = "/pastell/organismDepartment/")
    public String organismDepartmentPastell(@RequestParam long id) {
        OrganismDepartment organismDepartment = organismDepartmentService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.PASTELL_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        organismDepartmentSynchronizer.synchronizeUpdate(organismDepartment, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro organismDepartment " + organismDepartment.getName();
    }


    @GetMapping(value = "/sdm/agent/")
    public String agent(@RequestParam long id) {

        AgentProfile agent = agentProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());

        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = MyEc3ApplicationConstants.GU;

        agentSynchronizer.synchronizeUpdate(agent, listApplicationIdToResynchronize, synchronizationType, sendingApplication);
        logger.info("synchro agent {}", agent.getUsername());

        return "synchro agent " + agent.getName();
    }

    @GetMapping(value = "/sdm/organism/")
    public String organisme(@RequestParam long id) {
        Organism organism = organismService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        organismSynchronizer.synchronizeUpdate(organism, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro organism " + organism.getName();
    }

    @GetMapping(value = "/sdm/organismDepartment/")
    public String organismDepartment(@RequestParam long id) {
        OrganismDepartment organismDepartment = organismDepartmentService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        organismDepartmentSynchronizer.synchronizeUpdate(organismDepartment, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro organismDepartment " + organismDepartment.getName();
    }


    @GetMapping(value = "/sdm/employee/")
    public String employee(@RequestParam long id) {
        EmployeeProfile employeeProfile = employeeProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        employeeSynchronizer.synchronizeUpdate(employeeProfile, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro employee " + employeeProfile.getName();
    }

    @GetMapping(value = "/sdm/company/")
    public String company(@RequestParam long id) {
        Company company = companyService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        companySynchronizer.synchronizeUpdate(company, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro company " + company.getName();
    }

    @GetMapping(value = "/sdm/establishment/")
    public String establishment(@RequestParam long id) {
        Establishment establishment = establishmentService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.SDM_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        establishmentSynchronizer.synchronizeUpdate(establishment, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro establishment " + establishment.getName();
    }

    @GetMapping(value = "/udata/organism/")
    public String udataOrganism(@RequestParam long id) {
        Organism organism = organismService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.UDATA_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        organismSynchronizer.synchronizeUpdate(organism, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro organism " + organism.getName();
    }

    @GetMapping(value = "/udata/agent/")
    public String udataAgent(@RequestParam long id) {
        AgentProfile agentProfile = agentProfileService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName(MyEc3ApplicationConstants.UDATA_APPLICATION);
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        agentSynchronizer.synchronizeUpdate(agentProfile, listApplicationIdToResynchronize, synchronizationType, MyEc3ApplicationConstants.GU);

        return "synchro agent " + agentProfile.getName();
    }

    /**
     * MEGALIS-152 - API to resync company label for SDM
     *
     * @param launchUpdate set true to update data in DB and send synchro to SDM.
     *                     If false, launch the process and return result as string.
     * @return
     */
    @GetMapping(value = "/organism/resync")
    public String resyncCompanySdm(@RequestParam boolean launchUpdate) {
        logger.info("[RESYNC] Call resync Organism");

        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;

        // Get all organism subscribe to SDM
        List<OrganismLightDTO> organisms = organismService.findOrganismLight()
                .stream().filter(organismLightDTO -> organismLightDTO.getSiren() != null).collect(Collectors.toList());

        logger.info("[RESYNC] call API siren for each organism : {}", organisms.size());

        // Mise dans un Thread pour éviter les pb de timeout
        CompletableFuture.supplyAsync(() -> {
            AtomicInteger index = new AtomicInteger(0);
            organisms.forEach(organismLightDTO -> {

                ResponseUniteLegale entreprises = null;
                ResponseEtablissement etablissement = null;
                try {
                    entreprises = mpsWsClient.getInfoEntreprises(organismLightDTO.getSiren());
                    etablissement = mpsWsClient.getInfoEtablissements(entreprises.getData().getSiretSiegeSocial());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (entreprises ==null || entreprises.getData() == null) {
                    logger.info("[RESYNC] [" + organismLightDTO.getId() + "] [" + organismLightDTO.getSiren() + "] Pas de reponse de API INSEE");
                } else {
                    String labelInsee = entreprises.getData().getPersonneMoraleAttributs().raisonSociale;
                    String city = etablissement.getData().getAdresse().getLibelleCommune();

                    // SI le libelle ACTION SOCIALE est présent et sans la présence de la commune, alors on ajoute la commune dans le label
                    if ((labelInsee.contains(ACTION_SOCIALE) || labelInsee.contains(CAISSE_ECOLE)) && !labelInsee.contains(city)) {
                        labelInsee = labelInsee + " DE " + city;
                    }
                    logger.info("[RESYNC] [" + organismLightDTO.getId() + "] [" + organismLightDTO.getSiren() + "] [" + organismLightDTO.getLabel() + "] ==> [" + labelInsee + "]");

                    if (launchUpdate) {
                        Organism organism = organismService.findOne(organismLightDTO.getId());
                        organism.setLabel(labelInsee);
                        organism = organismService.update(organism);
                        organismSynchronizer.synchronizeUpdate(organism, null, synchronizationType, MyEc3ApplicationConstants.GU);
                        logger.info("[RESYNC] Organism " + organismLightDTO.getId() + " Updated");
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
            return "END";
        });
        return "inprogress";
    }

}
