package org.myec3.socle.synchro.core.config;

import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.service.KeycloakUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {
    @Bean
    public KeycloakUserService keycloakUserService() {
        // Stub pour les tests : on ne communique pas avec Keycloak.
        return new KeycloakUserService() {
            @Override
            public void saveProfileToKeycloak(Profile profile) {
                // do nothing
            }
        };
    }
}
