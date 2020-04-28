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
public class TestSynchroSDMController {

    private static final Logger logger = LoggerFactory.getLogger(TestSynchroSDMController.class);

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

    @RequestMapping(value = "/jcms/agent/", method = {RequestMethod.GET})
    @Transactional
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


    @RequestMapping(value = "/sdm/agent/", method = {RequestMethod.GET})
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public String establishment(@RequestParam long id) {
        Establishment establishment =establishmentService.findOne(id);

        Application applicationASynchroniser = applicationService.findByName("SDM");
        List<Long> listApplicationIdToResynchronize = new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());
        SynchronizationType synchronizationType = SynchronizationType.SYNCHRONIZATION;
        String sendingApplication = "GU";

        establishmentSynchronizer.synchronizeUpdate(establishment, listApplicationIdToResynchronize, synchronizationType, sendingApplication);

        return "synchro emplyoee "+ establishment.getName();
    }


}
