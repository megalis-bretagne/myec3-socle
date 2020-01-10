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

import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Structure;

/**
 * DAO interface for {@link Structure} objects. This interface defines global
 * methods that could be called on {@link Structure}
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface StructureDao extends ResourceDao<Structure> {

	/**
	 * Retrieve all structures {@link Structure} depending on the filters
	 * defined in parameter. If all parameters are null, return an empty list
	 * 
	 * @param value
	 *            : Structure label or siren to find. I
	 * 
	 * @return the list of all matching Structures. Return an empty list if
	 *         there's no result. Return null in case of errors.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<Structure> findAllByLabelOrSiren(String value);

	/**
	 * Find all structure {@link Structure} for a given {@link Application}
	 * 
	 * @param application
	 *            : {@link Application} to search on
	 * @return List of {@link Structure} associated to this application. return
	 *         null in case of errors
	 */
	List<Structure> findAllStructureByApplication(Application application);

	/**
	 * Find all child structures {@link Structure} of the current structure
	 * {@link Structure}
	 * 
	 * @param structure
	 *            : {@link Structure} to search on
	 * @return List of child {@link Structure} associated to this structure.
	 *         return null in case of errors
	 */
	List<Structure> findAllChildStructuresByStructure(Structure structure);

	/**
	 * Find all parent structures {@link Structure} of the current structure
	 * {@link Structure}
	 * 
	 * @param structure
	 *            : {@link Structure} to search on
	 * @return List of parent {@link Structure} associated to this structure.
	 *         return null in case of errors
	 */
	List<Structure> findAllParentStructuresByStructure(Structure structure);

	/**
	 * Find an organism {@link Organism} or company {@link Company} by its siren
	 * 
	 * @param siren
	 *            : exact siren to find.
	 * @return the organism {@link Organism} or company {@link Company} found.
	 *         Return null if there's no result.
	 * 
	 * @throws RuntimeException
	 *             in case of errors, specifically in case of multiple results
	 */
	Structure findBySiren(String siren);
}
