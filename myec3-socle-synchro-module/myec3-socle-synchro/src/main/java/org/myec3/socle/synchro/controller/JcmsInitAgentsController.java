package org.myec3.socle.synchro.controller;

import com.mchange.v1.util.ListUtils;
import org.glassfish.jersey.internal.guava.Lists;
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
    private JcmsInitService jcmsInitService;

    @Autowired
    @Qualifier("agentProfileService")
    private AgentProfileService agentProfileService;


    @GetMapping
    public String jcmsInit() {

        List<AgentProfile> listeAgents =agentProfileService.findAll();

        List<List<AgentProfile>> parts = chopped(listeAgents, 1000);

        for (List<AgentProfile> pack: parts){
            jcmsInitService.insertAgentInParallelScheduler(pack);
        }
        return "import des "+ listeAgents.size()+" agents en cours dans JCMS ";
    }

    // chops a list into non-view sublists of length L
    static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }




}
