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
package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.enums.StructureTypeValue;
import org.myec3.socle.core.domain.model.meta.StructureType;

/**
 * DAO interface for {@link StructureType} objects. This interface defines
 * global methods that could be called on {@link StructureType}
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public interface StructureTypeDao extends NoResourceGenericDao<StructureType> {

	/**
	 * Find StructureType by its value. @see {@link StructureTypeValue}
	 * 
	 * @param value
	 *            : structure type value to search on
	 * @return the {@link StructureType} object with this value. return null in
	 *         case of error, if there is several {@link StructureType} with
	 *         this value or if there is no result.
	 */
	StructureType findByValue(StructureTypeValue value);

}