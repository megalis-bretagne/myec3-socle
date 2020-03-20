package org.myec3.socle.synchro.controller;

import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestSycnhroSDMController {

    private static final Logger logger = LoggerFactory.getLogger(TestSycnhroSDMController.class);

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
    @Qualifier("agentSynchronizer")
    private ResourceSynchronizationManager<EmployeeProfile> employeeSynchronizer;

    @Autowired
    @Qualifier("organismSynchronizer")
    private ResourceSynchronizationManager<Organism> organismSynchronizer;

    @Autowired
    @Qualifier("organismDepartmentSynchronizer")
    private ResourceSynchronizationManager<OrganismDepartment> organismDepartmentSynchronizer;

    @Autowired
    @Qualifier("organismSynchronizer")
    private ResourceSynchronizationManager<Company> companySynchronizer;

    @Autowired
    @Qualifier("establishmentSynchronizer")
    private ResourceSynchronizationManager<Establishment> establishmentSynchronizer;

    @Autowired
    @Qualifier("applicationService")
    private ApplicationService applicationService;


    @RequestMapping(value = "/sdm/agent/", method = {RequestMethod.GET})
    @Transactional
    public String agent(@RequestParam long idAgent) {

        AgentProfile agent =agentProfileService.findOne(idAgent);

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
    @Transactional
    public String organisme(@RequestParam long idorganism) {
        Organism organism =organismService.findOne(idorganism);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        organismSynchronizer.synchronizeUpdate(organism, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro emplyoee "+ organism.getName();
    }

    @RequestMapping(value = "/sdm/organismDepartment/", method = {RequestMethod.GET})
    @Transactional
    public String organismDepartment(@RequestParam long idOrganismDepartment) {
        OrganismDepartment organismDepartment =organismDepartmentService.findOne(idOrganismDepartment);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        organismDepartmentSynchronizer.synchronizeUpdate(organismDepartment, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro emplyoee "+ organismDepartment.getName();
    }


    @RequestMapping(value = "/sdm/employee/", method = {RequestMethod.GET})
    @Transactional
    public String employee(@RequestParam long idEmployee) {
        EmployeeProfile employeeProfile =employeeProfileService.findOne(idEmployee);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        employeeSynchronizer.synchronizeUpdate(employeeProfile, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro emplyoee "+ employeeProfile.getName();
    }

    @RequestMapping(value = "/sdm/company/", method = {RequestMethod.GET})
    @Transactional
    public String company(@RequestParam long idCompagny) {
        Company company =companyService.findOne(idCompagny);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        companySynchronizer.synchronizeUpdate(company, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro emplyoee "+ company.getName();
    }


    @RequestMapping(value = "/sdm/establishment/", method = {RequestMethod.GET})
    @Transactional
    public String establishment(@RequestParam long idEstablishment) {
        Establishment establishment =establishmentService.findOne(idEstablishment);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        establishmentSynchronizer.synchronizeUpdate(establishment, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro emplyoee "+ establishment.getName();
    }


}
