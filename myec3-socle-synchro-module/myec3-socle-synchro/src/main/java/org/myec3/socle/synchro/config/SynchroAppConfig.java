package org.myec3.socle.synchro.config;


import org.myec3.socle.config.AsyncConfiguration;
import org.myec3.socle.config.HealthCheckConfig;
import org.myec3.socle.synchro.controller.AuthEventsController;
import org.myec3.socle.synchro.controller.JcmsInitAgentsController;
import org.myec3.socle.synchro.service.JcmsInitService;
import org.myec3.socle.synchro.controller.SynchroController;
import org.myec3.socle.synchro.customhealthcheck.GeneralSchedulerHealthIndicator;
import org.myec3.socle.synchro.customhealthcheck.ParallelSchedulerHealthIndicator;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@PropertySource({  "classpath:mpsUpdate.properties" })
@Import({HealthCheckConfig.class, AsyncConfiguration.class, JcmsInitAgentsController.class, AuthEventsController.class, JcmsInitService.class, SynchroController.class, ParallelSchedulerHealthIndicator.class, GeneralSchedulerHealthIndicator.class})
@ImportResource({"classpath:wsServerMyec3Context.xml"})
@EnableWebMvc
public class SynchroAppConfig {
}
