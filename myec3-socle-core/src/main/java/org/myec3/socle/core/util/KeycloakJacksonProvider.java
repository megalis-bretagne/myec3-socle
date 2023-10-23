package org.myec3.socle.core.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

/**
 * Provider Jackson pour Reasteasy permettant de faire fonctionner le client d'admin Keycloak.
 * <p>
 * Note EGE : sans ce provider, une configuration incompatible est utilisée. Je ne sais pas d'où elle provient (on n'a
 * pas le même comportement sur le projet Reg-Law qui utilise également le client d'admin).
 *
 * @see <a href="https://stackoverflow.com/a/75584693">https://stackoverflow.com/a/75584693</a>
 */
public class KeycloakJacksonProvider extends ResteasyJackson2Provider {
    private static class CustomNaming extends PropertyNamingStrategies.NamingBase {

        public CustomNaming() {
            super();
        }

        @Override
        public String translate(String s) {
            switch (s) {
                case "token":
                    return "access_token";
                case "expiresIn":
                    return "expires_in";
                case "refreshExpiresIn":
                    return "refresh_expires_in";
                case "refreshToken":
                    return "refreshToken";
                case "tokenType":
                    return "token_type";
                case "idToken":
                    return "id_token";
                case "notBeforePolicy":
                    return "not-before-policy";
                case "sessionState":
                    return "session_state";
                case "errorDescription":
                    return "error_description";
                case "errorUri":
                    return "error_uri";
                default:
                    return s;
            }
        }
    }

    public KeycloakJacksonProvider() {
        super();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(new CustomNaming());
        setMapper(mapper);
    }
}
