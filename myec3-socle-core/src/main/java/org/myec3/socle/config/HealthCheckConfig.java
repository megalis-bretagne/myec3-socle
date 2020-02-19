package org.myec3.socle.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.mvc.EndpointHandlerMapping;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collection;

@Configuration
@EnableWebMvc
@Import({ EndpointAutoConfiguration.class , HealthIndicatorAutoConfiguration.class})
public class HealthCheckConfig {

    //Ajout du healthCheck
    @Bean
    @Autowired
    public EndpointHandlerMapping endpointHandlerMapping(Collection<? extends MvcEndpoint> endpoints) {
        return new EndpointHandlerMapping(endpoints);
    }

    @Bean
    @Autowired
    public EndpointMvcAdapter healthActuatorEndPoint(HealthEndpoint delegate) {
        return new EndpointMvcAdapter(delegate);
    }
}
