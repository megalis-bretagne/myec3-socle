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
import org.myec3.socle.synchro.core.domain.model.SynchronizationQueue;

/**
 * Interface defining Business Services methods and providing
 * {@link SynchronizationQueue} specific operations. This interface extends the
 * common {@link GenericService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface SynchronizationQueueService extends IGenericService<SynchronizationQueue> {

	/**
	 * Retrieve choosen number of instances existing {@link SynchronizationQueue}
	 * 
	 * @param limit : maximum number of instances to return
	 * @return the list of found {@link SynchronizationQueue}, an empty list if
	 *         there is no result
	 * @throws RuntimeException in case of errors
	 */
	List<SynchronizationQueue> findAll(int limit);
}
