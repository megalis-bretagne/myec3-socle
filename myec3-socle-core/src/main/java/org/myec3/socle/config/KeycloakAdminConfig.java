package org.myec3.socle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:keycloak.properties"})
public class KeycloakAdminConfig {

    @Value("${socle.keycloak.admin.realm}")
    private String realm;

    @Value("${socle.keycloak.admin.baseUrl}")
    private String baseUrl;

    @Value("${socle.keycloak.admin.clientId}")
    private String clientId;

    @Value("${socle.keycloak.admin.clientSecret}")
    private String clientSecret;

    public String getRealm() {
        return realm;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
