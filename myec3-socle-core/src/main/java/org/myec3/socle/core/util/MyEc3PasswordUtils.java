package org.myec3.socle.core.util;

import org.myec3.socle.core.constants.MyEc3PasswordConstants;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class MyEc3PasswordUtils {
    private MyEc3PasswordUtils() {
        // no instanciation
    }

    /**
     * Generate a simple password;
     */
    public static String generatePassword() {
        Random random = new Random(System.currentTimeMillis());

        int nbSpecialCharacters = 1;
        int nbNumbers = 2;
        int nbLowercaseCharacters = 7;
        int nbUppercaseCharacters = 2;

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= nbSpecialCharacters; i++) {
            int pos = random.nextInt(MyEc3PasswordConstants.CHARSET_SPECIAL_CHARACTERS.length());
            sb.append(MyEc3PasswordConstants.CHARSET_SPECIAL_CHARACTERS.charAt(pos));
        }
        for (int i = 1; i <= nbLowercaseCharacters; i++) {
            int pos = random.nextInt(MyEc3PasswordConstants.CHARSET_LOWERCASE_CHARACTERS.length());
            sb.append(MyEc3PasswordConstants.CHARSET_LOWERCASE_CHARACTERS.charAt(pos));
        }
        for (int i = 1; i <= nbUppercaseCharacters; i++) {
            int pos = random.nextInt(MyEc3PasswordConstants.CHARSET_LOWERCASE_CHARACTERS.length());
            sb.append(String.valueOf(MyEc3PasswordConstants.CHARSET_LOWERCASE_CHARACTERS.charAt(pos)).toUpperCase());
        }
        for (int i = 1; i <= nbNumbers; i++) {
            int pos = random.nextInt(MyEc3PasswordConstants.CHARSET_NUMBERS.length());
            sb.append(MyEc3PasswordConstants.CHARSET_NUMBERS.charAt(pos));
        }

        String sbString = sb.toString();
        List<CharSequence> sbList = sbString.chars()
                .mapToObj(i -> String.valueOf((char) i)).collect(Collectors.toList());
        Collections.shuffle(sbList);

        return String.join("", sbList);
    }
}
