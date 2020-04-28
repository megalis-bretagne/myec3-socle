package org.myec3.socle.synchro.config;


import org.myec3.socle.config.AsyncConfiguration;
import org.myec3.socle.config.HealthCheckConfig;
import org.myec3.socle.synchro.controller.JcmsInitAgentsController;
import org.myec3.socle.synchro.controller.JcmsInitService;
import org.myec3.socle.synchro.controller.TestSynchroSDMController;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Import({HealthCheckConfig.class, AsyncConfiguration.class, JcmsInitAgentsController.class, JcmsInitService.class, TestSynchroSDMController.class})
@EnableWebMvc
public class SynchroAppConfig {
}
