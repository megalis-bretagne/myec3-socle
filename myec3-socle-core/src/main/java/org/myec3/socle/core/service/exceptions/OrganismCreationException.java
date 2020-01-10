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

import org.myec3.socle.core.domain.model.Organism;

/**
 * An exception returned when fail to create an {@link Organism}
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
@SuppressWarnings("serial")
public class OrganismCreationException extends StructureCreationException {

	/**
	 * Default Constructor
	 */
	public OrganismCreationException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : the error message
	 * @param cause
	 *            : the cause of the exception
	 */
	public OrganismCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            : the error message
	 */
	public OrganismCreationException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            : the cause of the exception
	 */
	public OrganismCreationException(Throwable cause) {
		super(cause);
	}

}
