package org.myec3.socle.synchro.controller;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.scheduler.manager.ResourceSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/jcmsInit")
public class JcmsInitAgentsController {

    private static final Logger logger = LoggerFactory.getLogger(JcmsInitAgentsController.class);

    @Autowired
    @Qualifier("agentSynchronizer")
    private ResourceSynchronizationManager<AgentProfile> agentSynchronizer;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("agentProfileService")
    private AgentProfileService agentProfileService;

    @Autowired
    @Qualifier("applicationService")
    private ApplicationService applicationService;

/*    @Autowired
    @Qualifier("userService")
    private UserService userService;*/

    @GetMapping
    @Transactional
    public String jcmsInit() {

        List<AgentProfile> listeAgents =agentProfileService.findAll();
        Application applicationASynchroniser = applicationService.findByName("portail megalisbretagne");

        List<Long> listApplicationIdToResynchronize= new ArrayList<>();
        listApplicationIdToResynchronize.add(applicationASynchroniser.getId());

        SynchronizationType synchronizationType=SynchronizationType.SYNCHRONIZATION;
        String sendingApplication ="GU";

        for (AgentProfile resource : listeAgents){
            agentSynchronizer.synchronizeCreation(resource,listApplicationIdToResynchronize,synchronizationType,sendingApplication);
            logger.info("synchro agent {}",resource.getUsername());
        }

        return "import des "+ listeAgents.size()+" agents en cours dans JCMS ";

    }

}
