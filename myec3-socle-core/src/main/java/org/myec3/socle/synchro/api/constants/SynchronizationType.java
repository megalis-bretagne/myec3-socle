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
package org.myec3.socle.synchro.api.constants;

/**
 * Enumeration used to know what service is responsible for the current
 * synchronization
 * 
 * @author Denis Cucchietti<denis.cucchietti@atosorigin.com>
 */
public enum SynchronizationType {

	/**
	 * Default synchronization case.
	 */
	SYNCHRONIZATION("Synchronisation"),

	/**
     * synchronization to lunch from database 
     */
    SYNCHRONIZATION_QUEUE("File d\'attente"), 
    
    /**
	 */
	ERROR_HANDLING("Gestion des erreurs"),

	/**
	 * manual synchronization
	 */
	RESYNCHRONIZATION("Exploitation");

	private final String value;

	private SynchronizationType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
