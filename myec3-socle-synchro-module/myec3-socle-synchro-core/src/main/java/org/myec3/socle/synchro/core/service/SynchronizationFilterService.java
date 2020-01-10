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

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.IGenericService;
import org.myec3.socle.synchro.core.domain.model.SynchronizationFilter;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;

/**
 * Interface defining Business Services methods and providing
 * {@link SynchronizationFilter} specific operations. This interface extends the
 * common {@link GenericService} interface by adding new specific methods
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface SynchronizationFilterService extends IGenericService<SynchronizationFilter> {

	/**
	 * Business method used to filter a specific resource. We use this method to
	 * hide some parameters into the XML sent at the distant application which has
	 * subscribed at the type of resource.
	 * 
	 * @param resource     : the resource to filter
	 * @param subscription : the subscription concerned by the filter
	 */
	void filter(Resource resource, SynchronizationSubscription subscription);
}
