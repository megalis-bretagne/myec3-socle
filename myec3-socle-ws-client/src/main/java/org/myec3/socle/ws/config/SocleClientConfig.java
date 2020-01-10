package org.myec3.socle.ws.config;

import org.myec3.socle.config.CoreConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = { "org.myec3.socle.ws.batch", "org.myec3.socle.ws.client", "org.myec3.socle.ws.https" })
@PropertySource({ "classpath:socleCore.properties", "classpath:mpsUpdate.properties" })
@Import({ CoreConfig.class })
public class SocleClientConfig {
	// Empty ok
}
