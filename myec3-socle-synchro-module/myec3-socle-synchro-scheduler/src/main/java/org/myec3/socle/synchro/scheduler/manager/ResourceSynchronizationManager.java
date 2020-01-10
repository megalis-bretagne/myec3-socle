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
package org.myec3.socle.synchro.scheduler.manager;

import java.util.List;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;

/**
 * Generic interface publishing methods to perform synchronization tasks on a
 * resource with type R. These methods are called after creation, update and
 * deletion events
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 * @param <R>
 *            : concrete oject type, extending {@link Resource}. Concrete type of the
 *            resource to synchronize.
 */
public interface ResourceSynchronizationManager<R extends Resource> {

	/**
	 * Performs synchronization tasks after creation of a resource.
	 * 
	 * @param resource
	 *            : the resource created to synchronize
	 * @param listApplicationIdToResynchronize
	 *            : the list of application to send the new resource (this list
	 *            can be null. In this case the resource created will be sent to
	 *            all applications)
	 * @param synchronizationType
	 *            : the type of synchronization (SYNCHRONIZATION, ERROR_HANDLING
	 *            or RESYNCHRONIZATION)
	 * @param sendingApplication
	 *            : the name of application that sent the JMS
	 */
	void synchronizeCreation(R resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Performs synchronization tasks after deletion of a resource.
	 * 
	 * @param resource
	 *            : the resource deleted to synchronize
	 * @param listApplicationIdToResynchronize
	 *            : the list of application to send the deleted resource (this
	 *            list can be null. In this case the resource deleted will be
	 *            send to all applications)
	 * @param synchronizationType
	 *            : the type of synchronization (SYNCHRONIZATION, ERROR_HANDLING
	 *            or RESYNCHRONIZATION)
	 * @param sendingApplication
	 *            : the name of application that sent the JMS
	 */
	void synchronizeDeletion(R resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Performs synchronization tasks after update of a resource.
	 * 
	 * @param resource
	 *            : the resource updated
	 * @param listApplicationIdToResynchronize
	 *            : the list of application to send the updated resource (this
	 *            list can be null. In this case the resource updated will be
	 *            send to all applications)
	 * @param synchronizationType
	 *            : the type of synchronization (SYNCHRONIZATION, ERROR_HANDLING
	 *            or RESYNCHRONIZATION)
	 * @param sendingApplication
	 *            : the name of application that sent the JMS
	 */
	void synchronizeUpdate(R resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Performs synchronization tasks after collection create of a resource.
	 * 
	 * @param resource
	 *            : the actual resource
	 * @param relationName
	 *            : the name of the resource created
	 * @param updatedResources
	 *            : the list of roles which has been updated
	 * @param addedResources
	 *            : the list of roles which has been added
	 * @param removedResources
	 *            : the list of roles which has been removed
	 * @param sendingApplication
	 *            : the name of application that sent the JMS
	 */
	void synchronizeCollectionCreate(R resource, String relationName,
			List<Resource> createdResources, String sendingApplication);

	/**
	 * Performs synchronization tasks after collection update of a resource.
	 * 
	 * @param resource
	 *            : the actual resource
	 * @param relationName
	 *            : the name of the resource updated
	 * @param updatedResources
	 *            : the list of roles which has been updated
	 * @param addedResources
	 *            : the list of roles which has been added
	 * @param removedResources
	 *            : the list of roles which has been removed
	 * @param sendingApplication
	 *            : the name of application that sent the JMS
	 */
	void synchronizeCollectionUpdate(R resource, String relationName,
			List<Resource> updatedResources, List<Resource> addedResources,
			List<Resource> removedResources, String sendingApplication);

	/**
	 * Performs synchronization tasks after collection remove of a resource.
	 * 
	 * @param resource
	 *            : the actual resource
	 * @param relationName
	 *            : the name of the resource deleted
	 * @param updatedResources
	 *            : the list of roles which has been updated
	 * @param addedResources
	 *            : the list of roles which has been added
	 * @param removedResources
	 *            : the list of roles which has been removed
	 * @param sendingApplication
	 *            : the name of application that sent the JMS
	 */
	void synchronizeCollectionRemove(R resource, String relationName,
			List<Resource> removedResources, String sendingApplication);

	/**
	 * Set Job used to perform synchronization tasks of a resource.
	 * 
	 * @param synchronizationJobType
	 *            : the type of synchronization to perform (CREATE, UPDATE,
	 *            DELETE)
	 * @param resource
	 *            : the resource to synchronize
	 * @param listApplicationIdToResynchronize
	 *            : the list of application to send the updated resource (this
	 *            list can be null. In this case the resource updated will be
	 *            send to all applications)
	 * @param synchronizationType
	 *            : the type of synchronization (SYNCHRONIZATION, ERROR_HANDLING
	 *            or RESYNCHRONIZATION)
	 * @param sendingApplication
	 *            : the name of application that sent the JMS
	 */
	void setCommonJob(SynchronizationJobType synchronizationJobType,
			R resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);
}
