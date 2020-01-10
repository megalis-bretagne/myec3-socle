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
package org.myec3.socle.core.domain.model.enums;

import org.myec3.socle.core.domain.model.meta.ProfileTypeRole;

/**
 * This enum provides usable values for {@link ProfileTypeRole}.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
public enum ProfileTypeRoleValue {

	ADMIN("admin"), BASIC("basic");

	/**
	 * value of the ProfileTypeRole
	 */
	private String value;

	/**
	 * Constructor
	 * 
	 * @param value
	 *            : value to set
	 */
	private ProfileTypeRoleValue(String value) {
		this.value = value;
	}

	/**
	 * @return the corresponding value
	 */
	public String getValue() {
		return value;
	}
}
