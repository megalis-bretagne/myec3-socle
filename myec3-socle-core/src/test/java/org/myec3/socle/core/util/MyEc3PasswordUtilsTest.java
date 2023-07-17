package org.myec3.socle.core.util;

import org.junit.Test;
import org.myec3.socle.core.constants.MyEc3PasswordConstants;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class MyEc3PasswordUtilsTest {

    /**
     * Vérifie que les mots de passe temporaires générés par le socle sont en accord avec la politique de mot de passe définie sur Keycloak.
     * <p>
     * ATTENTION : le code est à faire évoluer en fonction des modifications de configuration Keycloak.
     */
    @Test
    public void testKeycloakPasswordPolicy() {
        String password = MyEc3PasswordUtils.generatePassword();

        assertThat(password)
                .as("les mots de passe générées doivent respecter la longueur minimale")
                .hasSizeGreaterThanOrEqualTo(MyEc3PasswordConstants.PASSWORD_LENGTH);
        assertThat(password)
                .as("les mots de passe générées doivent contenir au moins une majuscule")
                .containsPattern(Pattern.compile("[A-Z]"));
        assertThat(password)
                .as("les mots de passe générées doivent contenir au moins une minuscule")
                .containsPattern(Pattern.compile("[a-z]"));
        assertThat(password)
                .as("les mots de passe générées doivent contenir au moins un chiffre")
                .containsPattern(Pattern.compile("[0-9]"));
        assertThat(password)
                .as("les mots de passe générées doivent contenir au moins un caractère spécial (= autre que chiffre ou lettre)")
                .containsPattern(Pattern.compile("[^A-Za-z0-9]"));
    }
}
