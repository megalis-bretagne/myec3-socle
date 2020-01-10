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

import java.util.List;

import org.myec3.socle.core.domain.model.InseeGeoCode;

/**
 * Interface defining Business Services methods and providing
 * {@link InseeGeoCode} specific operations. This interface extends the common
 * {@link ResourceService} interface by adding new specific methods
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 */
public interface InseeGeoCodeService extends ResourceService<InseeGeoCode> {

	/**
	 * Find {@link InseeGeoCode} instance by postalCode
	 * 
	 * @param postalCode
	 *            : postalCode to search on
	 * @return found {@link InseeGeoCode} object or null if no result.
	 * @throws IllegalArgumentException
	 *             if postal code is null
	 * @throws RuntimeException
	 *             in case of technical errors
	 */
	List<InseeGeoCode> findByPostalCode(String postalCode);

	/**
	 * Find {@link InseeGeoCode} instance by inseeCode
	 * 
	 * @param inseeCode
	 *            : insee code to search on
	 * @return found {@link InseeGeoCode} object or null if no result.
	 * @throws IllegalArgumentException
	 *             if inseeCode is null
	 * @throws RuntimeException
	 *             in case of technical errors
	 */
	InseeGeoCode findByInseeCode(String inseeCode);

	/**
	 * Find {@link InseeGeoCode} instance by postalCode and city label
	 * 
	 * @param postalCode
	 *            : postal code to search on
	 * @param city
	 *            : city label to search on
	 * @return found {@link InseeGeoCode} object or null if no result.
	 * @throws IllegalArgumentException
	 *             if postalCode or city are null
	 * @throws RuntimeException
	 *             in case of technical errors
	 */
	InseeGeoCode findByPostalCodeAndCity(String postalCode, String city);
}
