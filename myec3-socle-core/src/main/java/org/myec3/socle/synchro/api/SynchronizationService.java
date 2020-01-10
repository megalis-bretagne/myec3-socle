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
import org.myec3.socle.synchro.api.constants.SynchronizationType;

/**
 * Interface publishing methods to handle propagation of events related to
 * modification of objects (creation, modification, deletion).
 * 
 * It provides a Service to manage and launch synchronization tasks.
 * 
 * This service is mainly design to reduce coupling between core model objets
 * and synchronization tasks : service can be overridden later in order to
 * implement custom synchronization tasks.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
public interface SynchronizationService {

	/**
	 * Propagates creation of a resource
	 * 
	 * @param resource
	 *            : resource created
	 * @param listApplicationIdToResynchronize
	 *            : list of the ids of applications that have to be
	 *            resynchronized (in case of resynchronization). No
	 *            synchronyzation sent in case of an empty list.
	 * @param synchronizationType
	 *            : type of the synchronization process @see
	 *            {@link SynchronizationType}
	 * @param sendingApplication
	 *            : name of the application responsible for synchronization
	 */
	void propagateCreation(Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Propagates update of a resource
	 * 
	 * @param resource
	 *            : resource updated
	 * @param listApplicationIdToResynchronize
	 *            : list of the ids of applications that have to be
	 *            resynchronized (in case of resynchronization). No
	 *            synchronyzation sent in case of an empty list.
	 * @param synchronizationType
	 *            : type of the synchronization process @see
	 *            {@link SynchronizationType}
	 * @param sendingApplication
	 *            : name of the application responsible for synchronization
	 */
	void propagateUpdate(Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Propagates deletion of a resource
	 * 
	 * @param resource
	 *            : resource deleted
	 * @param listApplicationIdToResynchronize
	 *            : list of the ids of applications that have to be
	 *            resynchronized (in case of resynchronization). No
	 *            synchronyzation sent in case of an empty list.
	 * @param synchronizationType
	 *            : type of the synchronization process @see
	 *            {@link SynchronizationType}
	 * @param sendingApplication
	 *            : name of the application responsible for synchronization
	 */
	void propagateDeletion(Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Propagates update of a relation
	 * 
	 * @param resource
	 *            : resource owner of the relation
	 * @param relationName
	 *            : name of the updated relation
	 * @param updatedResources
	 *            : list of updated resources
	 * @param addedResources
	 *            : list of added resources
	 * @param removedResources
	 *            : list of removed resources
	 * @param sendingApplication
	 *            : name of the application responsible for synchronization
	 */
	void propagateCollectionUpdate(Resource resource, String relationName,
			List<Resource> updatedResources, List<Resource> addedResources,
			List<Resource> removedResources, String sendingApplication);

	/**
	 * Propagates create of a relation
	 * 
	 * @param resource
	 *            : resource owner of the relation
	 * @param relationName
	 *            : name of the created relation
	 * @param createdResources
	 *            : list of removed resources
	 * @param sendingApplication
	 *            : name of the application responsible for synchronization
	 */
	void propagateCollectionCreate(Resource resource, String relationName,
			List<Resource> createdResources, String sendingApplication);
}
