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

import org.myec3.socle.core.domain.model.Structure;

/**
 * This interface defines methods to perform queries on {@link Structure}
 * objects or objects extending structures.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T>
 *            instance, child of Structure to query on
 */
public interface GenericStructureDao<T extends Structure> extends
		ResourceDao<T> {

	/**
	 * Retrieve all structures depending on the filters defined in parameters.
	 * If all parameters are null, retrieve all organisms.
	 * 
	 * @param label
	 *            : Structure label to find. If this parameter is not null,
	 *            filters on all structures with the same label. Case no
	 *            matters.
	 * @param siren
	 *            : Structure siren to find. If this parameter is not null,
	 *            filters on all structures with exactly the same siren.
	 * @param postalCode
	 *            : Structure postalCode to find. If this parameter is not null,
	 *            filters on all structures with exactly the same postalCode.
	 * @param city
	 *            : Structure city to find. If this parameter is not null,
	 *            filters on all structures with the same city. Case no matters
	 * @return the list of all matching Structures. Return an empty list if
	 *         there's no result. Return null in case of errors.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<T> findAllByCriteria(String label, String siren, String postalCode,
			String city);

	/**
	 * Find an organism by its siren
	 * 
	 * @param siren
	 *            : exact siren to find.
	 * @return the organism found. Return null if there's no result.
	 * 
	 * @throws RuntimeException
	 *             in case of errors, specifically in case of multiple results
	 */
	T findBySiren(String siren);

	/**
	 * Find an organism by its acronym
	 * 
	 * @param acronym
	 *            : exact acronym to find.
	 * @return the organism found. Return null if there's no result.
	 * 
	 * @throws RuntimeException
	 *             in case of errors, specifically in case of multiple results
	 */
	T findByAcronym(String acronym);

}