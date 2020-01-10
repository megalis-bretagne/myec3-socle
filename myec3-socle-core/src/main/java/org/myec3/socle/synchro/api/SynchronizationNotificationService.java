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
package org.myec3.socle.synchro.api;

import java.util.List;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;

/**
 * Interface defining Business Services methods and providing synchronization
 * specific operations.
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface SynchronizationNotificationService {

	/**
	 * Notify the creation of a {@link Resource} to the external applications
	 * 
	 * @param resource
	 *            : the {@link Resource} created
	 */
	void notifyCreation(Resource resource);

	/**
	 * Notify the creation of a {@link Resource} to the external applications
	 * with a specific {@link SynchronizationConfiguration}
	 * 
	 * @param resource
	 *            : the {@link Resource} created
	 * @param configSync
	 *            : the {@link SynchronizationConfiguration} to use
	 */
	void notifyCreation(Resource resource,
			SynchronizationConfiguration configSync);

	/**
	 * Notify the update of a {@link Resource} to the external applications
	 * 
	 * @param resource
	 *            : the {@link Resource} updated
	 */
	void notifyUpdate(Resource resource);

	/**
	 * Notify the update of a {@link Resource} to the external applications with
	 * a specific {@link SynchronizationConfiguration}
	 * 
	 * @param resource
	 *            : the {@link Resource} updated
	 * @param configSync
	 *            : the {@link SynchronizationConfiguration} to use
	 */
	void notifyUpdate(Resource resource, SynchronizationConfiguration configSync);

	/**
	 * Notify the deletion of a {@link Resource} to the external applications
	 * 
	 * @param resource
	 *            : the {@link Resource} deleted
	 */
	void notifyDeletion(Resource resource);

	/**
	 * Notify the deletion of a {@link Resource} to the external applications
	 * with a specific {@link SynchronizationConfiguration}
	 * 
	 * @param resource
	 *            : the {@link Resource} deleted
	 * @param configSync
	 *            : the {@link SynchronizationConfiguration} to use
	 */
	void notifyDeletion(Resource resource,
			SynchronizationConfiguration configSync);

	/**
	 * Notify the update of a {@link Resource} collection to the external
	 * applications
	 * 
	 * @param resource
	 *            : the {@link Resource} deleted
	 * @param relationName
	 *            : name of the updated collection
	 * @param updatedResources
	 *            : list of updated resources of the collection
	 * @param addedResources
	 *            : list of added resources of the collection
	 * @param removedResources
	 *            : list of removed resources of the collection
	 */
	void notifyCollectionUpdate(Resource resource,
			SynchronizationRelationsName relationName,
			List<Resource> updatedResources, List<Resource> addedResources,
			List<Resource> removedResources);
}
