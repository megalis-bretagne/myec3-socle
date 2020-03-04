package org.myec3.socle.synchro.config;


import org.myec3.socle.config.HealthCheckConfig;
import org.myec3.socle.synchro.controller.JcmsInitAgentsController;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Import({HealthCheckConfig.class, JcmsInitAgentsController.class})
@EnableWebMvc
public class SynchroAppConfig {
}
