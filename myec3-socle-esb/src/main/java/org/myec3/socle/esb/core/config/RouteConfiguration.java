package org.myec3.socle.esb.core.config;

import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "org.myec3.socle.esb.core" })
public class RouteConfiguration extends CamelConfiguration {
	// Empty Ok
}
