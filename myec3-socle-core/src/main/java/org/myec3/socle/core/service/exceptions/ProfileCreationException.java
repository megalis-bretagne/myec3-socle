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
package org.myec3.socle.core.service.exceptions;

import org.myec3.socle.core.domain.model.Profile;

/**
 * An exception returned when fail to create a {@link Profile}
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
public class ProfileCreationException extends RuntimeException {

	private static final long serialVersionUID = 5964832359571741619L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : the error message
	 * @param cause
	 *            : the cause of the exception
	 */
	public ProfileCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : the error message
	 */
	public ProfileCreationException(String message) {
		super(message);
	}

}
