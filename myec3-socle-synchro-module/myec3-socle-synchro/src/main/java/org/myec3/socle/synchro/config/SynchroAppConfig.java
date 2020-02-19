package org.myec3.socle.synchro.config;


import org.myec3.socle.config.HealthCheckConfig;
import org.springframework.context.annotation.*;

@Configuration
@Import({HealthCheckConfig.class})
public class SynchroAppConfig {
}
