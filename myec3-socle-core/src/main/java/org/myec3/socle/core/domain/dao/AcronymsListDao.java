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

import org.myec3.socle.core.domain.model.AcronymsList;

/**
 * This interface define methods to perform specific queries on
 * {@link AcronymsList} objects). It only defines new specific methods and
 * inherits methods from {@link ResourceDao}.
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
public interface AcronymsListDao extends NoResourceGenericDao<AcronymsList> {

	/**
	 * Find one acronym available to return for an organism. The organism's
	 * acronym must be unique
	 * 
	 * @return an acronym available for the organism. Return null in case of
	 *         error.
	 */
	AcronymsList findOneAvailableAcronym();

	/**
	 * Retrieve an acronym by the given value
	 * 
	 * @return found {@link AcronymsList} object
	 */
	AcronymsList findByValue(String value);

}