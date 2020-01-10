/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.core.tools;

import java.text.Normalizer;

/**
 * This class extend J2SDK behaviours to handle diacritic characters String.
 **/
public abstract class UnaccentLetter {

	/**
	 * private constructor in order to forbid instanciation
	 */
	private UnaccentLetter() {
	}

	/**
	 * Convert an accentuated characters String to a non diacritic characters
	 * String. This allows to better perform sorts an alphabetical ordering.
	 * 
	 * @param stringAccented
	 *            : initial String
	 * 
	 * @return normalized String without accents
	 */
	public static String unaccentString(String stringAccented) {
		// Normalize String by "splitting" special accentuated charcters in two
		// characters :
		// on firt character enconding the accent, one other encoding the letter
		String temp = Normalizer.normalize(stringAccented, Normalizer.Form.NFD);

		// Cleanup the string by removing non ASCII characters.
		// i.e. in this case, by removing first character enconding the accent
		// to keeep
		// only the "normal" character.
		return temp.replaceAll("[^\\p{ASCII}]", "");
	}

}
