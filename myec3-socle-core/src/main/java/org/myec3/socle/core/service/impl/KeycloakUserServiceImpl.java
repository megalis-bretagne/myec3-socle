package org.myec3.socle.core.service.impl;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.myec3.socle.config.KeycloakAdminConfig;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.service.KeycloakUserService;
import org.myec3.socle.core.util.KeycloakJacksonProvider;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;
import java.util.*;

@Service("keycloakUserService")
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private static final String EXTERNAL_ID_ATTR_NAME = "externalId";
    private static final String UID_ATTR_NAME = "uid";
    private static final String USERTYPE_ATTR_NAME = "userType";
    private static final String ALFUSERNAME_MONOTENANT_ATTR_NAME = "alfUserNameMonoTenant";
    private static final String ALFUSERNAME_MULTITENANT_ATTR_NAME = "alfUserNameMultiTenant";
    private static final String SIREN = "siren";
    private static final String ROLE = "role";
    private static final String SUFFIX_MONOTENANT = "@monotenant.megalis";

    private final RealmResource realm;

    KeycloakUserServiceImpl(KeycloakAdminConfig keycloakConfig) {
        Client resteasyClient = ResteasyClientBuilder.newClient();
        resteasyClient.register(KeycloakJacksonProvider.class);

        this.realm = KeycloakBuilder
                .builder()
                .serverUrl(keycloakConfig.getBaseUrl())
                .realm(keycloakConfig.getRealm())
                .clientId(keycloakConfig.getClientId())
                .clientSecret(keycloakConfig.getClientSecret())
                .grantType("client_credentials")
                .resteasyClient(resteasyClient)
                .build()
                .realm(keycloakConfig.getRealm());

        // On fait tout de suite un appel à Keycloak, cela permet de valider que le compte utilisé pour le client d'admin
        // est bien configuré (pour avoir l'erreur maintenant plutôt que plus tard).
        this.realm.users().count();
    }

    @Override
    public void saveProfileToKeycloak(Profile profile) {
        Optional<UserRepresentation> existingUser = this.getKeycloakUserByUsername(profile.getUsername());
        UserResource userResource;
        if (existingUser.isPresent()) {
            // User account exists in Keycloak, we will update it
            userResource = updateKeycloakUserWithProfile(existingUser.get(), profile);
        } else {
            // User account does not exist in Keycloak, we will create it
            createKeycloakUserWithProfile(profile);
            userResource = null;
        }

        if (profile.getUser().getTemporaryPassword() != null) {
            if (userResource == null) {
                existingUser = this.getKeycloakUserByUsername(profile.getUsername());
                userResource = this.realm.users().get(existingUser.orElseThrow(() -> new IllegalStateException("Created account not found")).getId());
            }
            // A temporary password is defined for the user, we set it on the user account.
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setType("password");
            credentialRepresentation.setValue(profile.getUser().getTemporaryPassword());
            credentialRepresentation.setTemporary(true);
            userResource.resetPassword(credentialRepresentation);
        }
    }

    /**
     * Update an existing Keycloak account with data from a given profile.
     */
    private UserResource updateKeycloakUserWithProfile(UserRepresentation userToUpdate, Profile profile) {
        applyProfileToUserRepresentation(profile, userToUpdate);
        UserResource userResource = this.realm.users().get(userToUpdate.getId());
        userResource.update(userToUpdate);
        return userResource;
    }

    /**
     * Create a Keycloak account with data from a given profile.
     */
    private void createKeycloakUserWithProfile(Profile profile) {
        UserRepresentation userToCreate = new UserRepresentation();
        applyProfileToUserRepresentation(profile, userToCreate);
        Response response = this.realm.users().create(userToCreate);
        if (response.getStatus() < 200 || response.getStatus() >= 300) {
            throw new ResponseProcessingException(response, String.format("Erreur à la création du compte utilisateur (code de statut: %d)", response.getStatus()));
        }
    }

    /**
     * Apply values from a given profile to a user representation.
     * This method set attributes value but does not persist the user.
     */
    private void applyProfileToUserRepresentation(Profile profile, UserRepresentation userRepresentation) {
        String userName = profile.getUsername();
        String firstName = profile.getUser().getFirstname();
        String lastName = profile.getUser().getLastname();
        boolean enabled = profile.getUser().isEnabled();
        String socleId = profile.getId().toString();
        String socleExternalId = profile.getExternalId().toString();
        String email = profile.getEmail();
        String userType = profile.getProfileType().getValue().getLabel();

        String role = null;
        if (profile.getRoles() != null) {
            for (Role profileRole : profile.getRoles()) {
                if ("pub-open-data".equals(profileRole.getApplication().getName())) {
                    role = profileRole.getName();
                }
            }
        }

        String siren = null;
        if (ProfileTypeValue.AGENT.equals(profile.getProfileType().getValue())) {
            AgentProfile agentProfile = (AgentProfile) profile;
            if (agentProfile.getOrganismDepartment() != null && agentProfile.getOrganismDepartment().getOrganism() != null) {
                siren = agentProfile.getOrganismDepartment().getOrganism().getSiren();
            }
        }

        boolean isAbonneeParapheur = profile.getRoles().stream()
                .map(Role::getApplication)
                .anyMatch(Application::isMonotenant);
        String alfUserNameMonoTenant = isAbonneeParapheur
                ? profile.getId() + SUFFIX_MONOTENANT
                : profile.getAlfUserName();
        String alfUserNameMultiTenant = profile.getAlfUserName();

        userRepresentation.setUsername(userName);
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
        userRepresentation.setEmail(email);
        userRepresentation.setEnabled(enabled);

        Map<String, List<String>> userAttributes = userRepresentation.getAttributes() != null
                ? new HashMap<>(userRepresentation.getAttributes())
                : new HashMap<>();

        setUserAttributeValue(userAttributes, EXTERNAL_ID_ATTR_NAME, socleExternalId);
        setUserAttributeValue(userAttributes, UID_ATTR_NAME, socleId);
        setUserAttributeValue(userAttributes, USERTYPE_ATTR_NAME, userType);
        setUserAttributeValue(userAttributes, ALFUSERNAME_MONOTENANT_ATTR_NAME, alfUserNameMonoTenant);
        setUserAttributeValue(userAttributes, ALFUSERNAME_MULTITENANT_ATTR_NAME, alfUserNameMultiTenant);
        setUserAttributeValue(userAttributes, SIREN, siren);
        setUserAttributeValue(userAttributes, ROLE, role);

        userRepresentation.setAttributes(userAttributes);
    }

    /**
     * Update a singular user attribute, removing it if the value is null.
     */
    private void setUserAttributeValue(Map<String, List<String>> userAttributes, String key, String value) {
        if (value == null) {
            userAttributes.remove(key);
        } else {
            userAttributes.put(key, Collections.singletonList(value));
        }
    }

    /**
     * Retrieve a user account from Keycloak.
     */
    private Optional<UserRepresentation> getKeycloakUserByUsername(String username) {
        return this.realm.users().searchByUsername(username, true).stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
}
