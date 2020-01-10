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
package org.myec3.socle.synchro.core.service;

import java.util.List;

import org.myec3.socle.core.service.IGenericService;
import org.myec3.socle.synchro.core.domain.model.SynchronizationError;

/**
 * Interface defining Business Services methods and providing
 * {@link SynchronizationError} specific operations. This interface extends the
 * common {@link GenericService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface SynchronizationErrorService extends IGenericService<SynchronizationError> {

	/**
	 * Find all SynchronizationError associated directly to an resource.
	 * 
	 * @param resourceId : the ID of a resource
	 * 
	 * @return List of SynchronizationError associated to this resource. return null
	 *         if no result or in case of error.
	 * 
	 */
	List<SynchronizationError> findAllByResourceId(Long resourceId);
}
