package org.myec3.socle.core.constants;


/**
 * Constants used to managed configuration of the hash algorithm
 * 
 * @author Arnaud Ozeel <arnaud.ozeel@atosorigin.com>
 */
public class MyEc3PasswordConstants {

	/**
	 * Configure generate passwords
	 */

	// Min length expected for password
	public static final int PASSWORD_LENGTH = 12;

	// Charset lowercase characters to use to generate password
	public static final String CHARSET_LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

	// Charset special characters to use to generate password
	public static final String CHARSET_SPECIAL_CHARACTERS = "()[]{}\\/*-+=_$&%!?.;:,@";

	// Charset numbers to use to generate password
	public static final String CHARSET_NUMBERS = "0123456789";

}
