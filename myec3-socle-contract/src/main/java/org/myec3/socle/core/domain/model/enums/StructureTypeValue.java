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

import org.myec3.socle.core.domain.model.meta.StructureType;

/**
 * This enum provide the list of possible {@link StructureType} values allowed.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public enum StructureTypeValue {

	// the StructureType is an Organism (contains AgentProfile)
	ORGANISM,
	// the StructureType is a Company (contains EmployeeProfile)
	COMPANY;
}
