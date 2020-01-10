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
 * ENUMERATION declaring possible types of a synchronization job
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 */
public enum SynchronizationJobType {

	/**
	 * Creation job
	 */
	CREATE(0),

	/**
	 * modification job
	 */
	UPDATE(1),

	/**
	 * deletion job
	 */
	DELETE(2),

	/**
	 * collection creation job
	 */
	COLLECTION_CREATE(3),

	/**
	 * collection update job
	 */
	COLLECTION_UPDATE(4);

	private final int value;

	private SynchronizationJobType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
