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

import org.myec3.socle.core.domain.model.meta.ProfileType;

/**
 * This enum provide the list of possible {@link ProfileType} values allowed.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public enum ProfileTypeValue {

	// Profile is an agent
	AGENT("AGENT"),
	// Profile is a company employee
	EMPLOYEE("EMPLOYEE"),
	// Profile is a platform or service admin
	ADMIN("ADMIN");
	
	private final String label;

	private ProfileTypeValue(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
