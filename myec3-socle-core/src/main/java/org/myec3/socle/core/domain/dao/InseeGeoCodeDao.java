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

import org.myec3.socle.core.domain.model.InseeGeoCode;

/**
 * This interface define methods to perform specific queries on
 * {@link InseeGeoCode} objects. It only defines new specific methods and
 * inherits methods from {@link ResourceDao}.
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * 
 */
public interface InseeGeoCodeDao extends ResourceDao<InseeGeoCode> {

	/**
	 * Find a list of InseeGeoCode by its postalCode
	 * 
	 * @param postalCode
	 *            : exact postalCode to find.
	 * @return the INSEEGeoCode found. Return null if there's no result.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	List<InseeGeoCode> findByPostalCode(String postalCode);

	/**
	 * Find InseeGeoCode by its inseeCode
	 * 
	 * @param inseeCode
	 *            : exact inseeCode to find.
	 * @return the INSEEGeoCode found. Return null if there's no result.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	InseeGeoCode findByInseeCode(String inseeCode);

	/**
	 * Find InseeGeoCode by its postalCode and town
	 * 
	 * @param postalCode
	 *            : exact postalCode to find.
	 * @param city
	 *            : exact city to find.
	 * @return the INSEEGeoCode found. Return null if there's no result.
	 * @throws RuntimeException
	 *             in case of errors
	 */
	InseeGeoCode findByPostalCodeAndCity(String postalCode, String city);

}
