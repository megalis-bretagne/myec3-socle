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

import org.myec3.socle.core.domain.model.Resource;

/**
 * This interface defines generic methods providing business behaviours for all
 * entities inherited from {@link Resource}
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> : concrete entity type, derived from {@link Resource} to work on
 */
public interface ResourceService<T extends Resource> extends IGenericService<T> {

	/**
	 * Retrieve a T entity from its externalId
	 * 
	 * @param externalId : externalId to find
	 * 
	 * @return the T entity if found, null if there is no result
	 * 
	 * @throws IllegalArgumentException if externalId is null
	 * 
	 * @throws RuntimeException         in case of errors
	 */
	T findByExternalId(Long externalId);

	/**
	 * Retrieve existing T entities which id are included in an interval
	 * 
	 * @param minId : interval min id
	 * @param maxId : interval max id
	 * 
	 * @return the list of found T entities, an empty list if there is no result,
	 *         null otherwise
	 * 
	 * @throws IllegalArgumentException if nimId or maxId are null and if minId >
	 *                                  maxId
	 * 
	 * @throws RuntimeException         in case of errors
	 */
	List<T> findByIntervalId(Long minId, Long maxId);

	/**
	 * Retrieve a T entity from its name
	 * 
	 * @param name : name to find
	 * 
	 * @return the T entity if found, null if there is no result or error occured
	 * 
	 * @throws IllegalArgumentException if name is null
	 * 
	 * @throws RuntimeException         in case of errors
	 */
	T findByName(String name);

	/**
	 * Retrieve all existing T entities with an exact given label
	 * 
	 * @param label : label to find
	 * 
	 * @return the list of found T entities, an empty list if there is no result,
	 *         null otherwise
	 * 
	 * @throws RuntimeException in case of errors
	 */
	List<T> findAllByLabel(String label);

	/**
	 * Retrieve all existing T entities with a partial given label : ie all entities
	 * which label contains the given label
	 * 
	 * @param label : label to find
	 * 
	 * @return the list of found T entities, an empty list if there is no result,
	 *         null otherwise
	 * 
	 * @throws RuntimeException in case of errors
	 */
	List<T> findAllByPartialLabel(String label);
}
