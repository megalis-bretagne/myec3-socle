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
package org.myec3.socle.ws.server.validator;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.MethodType;

/**
 * 
 * Generic interface providing commons and abstract methods used by all type of
 * {@link Resource}. All validate must be inherit this interface.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface ResourceValidatorManager<T> {

	/**
	 * This method allows to check if a Resource exists into the database with the given UID.
	 * 
	 * @param resourceId
	 *            : Resource UID to validate
	 */
	Boolean validateResourceExists(Long resourceId);
	
	/**
	 * This method allows to validate object content through annotations of
	 * hibernate validator placed into the model of bean
	 * 
	 * @param resource
	 *            : the resource to validate
	 * @param methodType
	 *            : the method called (GET, POST, PUT, DELETE)
	 * @see org.myec3.socle.core.sync.api.MethodType
	 */
	void validateBeanContent(T resource, MethodType methodType);

	/**
	 * Method called on GET, it allows to validate the specific rules of the
	 * resource to get from the database
	 * 
	 * @param resourceId
	 *            : the resource id
	 */
	void validateGet(Long resourceId);

	/**
	 * Method called on POST, it allows to validate the specific rules of the
	 * resource to persist into the database
	 * 
	 * @param resource
	 *            : the resource to validate
	 */
	void validateCreate(T resource);

	/**
	 * Method called on PUT, it allows to validate the specific rules of the
	 * resource to persist into the database if the resource don't exists.
	 * 
	 * This method used during a PUT witch is considered like a post if the
	 * Resource doesn't exists
	 * 
	 * @param resource
	 *            : the resource to validate
	 */
	void validateUpdateCreation(T resource);

	/**
	 * Method called on PUT, it allows to validate the specific rules of the
	 * resource to update from the database
	 * 
	 * @param resource
	 *            : the resource to validate
	 * @param resourceId
	 *            : the resource's id
	 * @param resourceFound
	 *            : the resource found
	 */
	void validateUpdate(T resource, Long resourceId, T resourceFound);

	/**
	 * Method called on DELETE, it allows to validate the specific rules of the
	 * resource before delete it from the database
	 * 
	 * @param resourceId
	 *            : the resource id
	 */
	void validateDelete(Long resourceId);

	/**
	 * This method allows to prepare the resource before make an action.
	 * 
	 * @param resource
	 *            : the resource to prepare
	 * @param methodType
	 *            : the method type (GET, PUT, POST, DELETE)
	 */
	void prepareResource(T resource, MethodType methodType);

	/**
	 * This method allows to filter the resource before returning it at the
	 * application responsible for the operation.
	 * 
	 * @param resource
	 *            : the resource to filter
	 * @param methodType
	 *            : the method type (GET, PUT, POST, DELETE)
	 */
	void filterResource(T resource, MethodType methodType);

	/**
	 * Populates all collections of a given {@link Resource}
	 * 
	 * @param resource
	 *            : the {@link Resource} to populate
	 */
	void populateResourceCollections(T resource);
}
