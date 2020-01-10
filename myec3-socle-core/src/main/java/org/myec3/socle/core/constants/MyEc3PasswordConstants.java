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
	public static final int PASSWORD_LENGTH = 8;

	// Charset characters to use to generate password
	public static final String CHARSET_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

	// Charset numbers to use to generate password
	public static final String CHARSET_NUMBERS = "0123456789";

	/**
	 * Scrypt hashing Algorithm
	 */

	// true : using Scrypt hashing algorithm, false SHA1
	public static final boolean isScryptHashingAlgorithm = true;

	/**
	 * Configure Hashing algorithm "SCRYPT"
	 */

	// the CPU/Memory cost parameter (2^14 : 16 bits)
	public final static int SCRYPT_PARAMETER_N = 16384;

	// the block size parameter (8 bits)
	public final static int SCRYPT_PARAMETER_R = 8;

	// the parallelization parameter (8 bits)
	public final static int SCRYPT_PARAMETER_P = 1;

	/**
	 * Configure Hashing algorithm "SHA1"
	 */

	// Min length of hashed password
	public static final int SHA1_HASH_LENGTH = 40;

	// Hashing algorithm definition
	public static final String SHA1_HASH = "SHA1";

	// Radix used to generate hash for password
	public static final int SHA1_RADIX = 16;

}
