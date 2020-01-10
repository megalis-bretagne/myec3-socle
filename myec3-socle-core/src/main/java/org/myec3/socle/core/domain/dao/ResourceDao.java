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

import org.myec3.socle.core.domain.model.Resource;

/**
 * Generic DAO interface for {@link Resource} objects. This interface defines
 * global methods that could be called on any class derived from
 * {@link Resource}
 * 
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 * @param <T> Concrete Resource class
 * 
 */
public interface ResourceDao<T extends Resource> extends IGenericDao<T> {

	/**
	 * Get a T instance from its externalId.
	 * 
	 * @param externalId : externalId of the resource to find
	 * 
	 * @return the resource if found, null otherwise.
	 * 
	 * @throws RuntimeException in case of errors
	 */
	T findByExternalId(Long externalId);

	/**
	 * Get T instances include in an interval (any class derived from T)
	 * 
	 * @param minId : interval min id
	 * @param maxId : interval max id
	 * 
	 * @return the list of T objects (T extends Resource). Returns an empty list if
	 *         no result.
	 * 
	 * @throws RuntimeException in case of errors
	 */
	List<T> findByIntervalId(Long minId, Long maxId);

	/**
	 * Get a T instance from its name.
	 * 
	 * @param name : name of the resource to find
	 * 
	 * @return the resource if found, null otherwise.
	 * 
	 * @throws RuntimeException in case of errors
	 */
	T findByName(String name);

	/**
	 * Get all T instances holding exatcly the given label.
	 * 
	 * @param label : label of the resource to find
	 * 
	 * @return the complete list of all found resources. Returns an empty list if no
	 *         result.
	 * 
	 * @throws RuntimeException in case of errors
	 */
	List<T> findAllByLabel(String label);

	/**
	 * Get all T instances containing the given label.
	 * 
	 * @param label : label of the resource to find
	 * 
	 * @return the complete list of all found resources. Returns an empty list if no
	 *         result.
	 * 
	 * @throws RuntimeException in case of errors
	 */
	List<T> findAllByPartialLabel(String label);

}
