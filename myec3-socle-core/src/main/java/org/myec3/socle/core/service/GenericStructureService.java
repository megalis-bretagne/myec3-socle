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
package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.Structure;

import java.util.List;

/**
 * This interface provide common method signatures for all resources
 * implementing {@link Structure}. WARNING : it is not an interface for
 * manipulation generic Structure type. All methods defined here should be
 * applied on a child of Profile. This interface exists in order to maximize
 * reuse between child implementations of Services manipulation children of
 * Structure.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T>
 *            : concrete Structure implementation
 */
public interface GenericStructureService<T extends Structure> extends
		ResourceService<T> {

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
	 * @return the list of all matching structures. Return an empty list if
	 *         there's no result. Return null in case of errors.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<T> findAllByCriteria(String label, String siren, String postalCode,
			String city);

	/**
	 * Find a structure by its siren
	 * 
	 * @param siren
	 *            : siren to search on
	 * @return the structure found, null if there is no result
	 * @throws IllegalArgumentException
	 *             if siren is null
	 * @throws RuntimeException
	 *             in case of errors
	 */
	T findBySiren(String siren);

	/**
	 * Check if siren is valid
	 * 
	 * @param siren
	 *            : siren to check
	 * @return TRUE if siren is valid, FALSE otherwise of if siren is null
	 */
	Boolean isSirenValid(String siren);

	/**
	 * Check if siren is valid depending on siren an nic
	 *
	 * @param siren : siren to check
	 * @param nic   : nic to check
	 * @return TRUE if siren is valid, FALSE otherwise or if siren or nic is null
	 */
	Boolean isSiretValid(String siren, String nic);

	/**
	 * Find a structure by acronym
	 * 
	 * @param acronym
	 *            : acronym to search on
	 * @return the found structure or null if there is no result
	 * @throws IllegalArgumentException
	 *             if acronym is null
	 * @throws RuntimeException
	 *             in cas of errors
	 */
	T findByAcronym(String acronym);

}
