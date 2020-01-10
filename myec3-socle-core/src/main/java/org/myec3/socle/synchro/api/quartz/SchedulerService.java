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
package org.myec3.socle.synchro.api.quartz;

import java.util.List;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;

/**
 * Interface which defines methods used by the scheduler to send the correct JMS
 * message to remote ESB queue.
 * 
 * @see JMSSynchronizationServiceImpl.class
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
public interface SchedulerService {

	/**
	 * Add immediate trigger for propagate create/update/delete of a
	 * {@link Resource}
	 * 
	 * @param resource
	 *            : the {@link Resource} to synchronize
	 * @param listApplicationIdToResynchronize
	 *            : the list of {@link Application} to synchronize. If null, all
	 *            applications are synchronized
	 * @param synchronizationType
	 *            : the type of the synchronization
	 * @param synchronizationJobType
	 *            : the action to perform
	 * @param nbAttempts
	 *            : the number of attempts to perform this synchronization task
	 */
	public void addImmediatePropagateCUDTrigger(Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType,
			SynchronizationJobType synchronizationJobType, int nbAttempts);

	/**
	 * Add immediate trigger for propagate collection creation. Trigger used
	 * when a collection is created.
	 * 
	 * @param resource
	 *            : the {@link Resource} owner of the collection
	 * @param relationName
	 *            : the name of the collection created
	 * @param createdResources
	 *            : the list of {@link Resource} created
	 * @param sendingApplication
	 *            : the name of the {@link Application} responsible for
	 *            synchronization
	 * @param nbAttempts
	 *            : the number of attempts to synchronize the {@link Resource}
	 */
	public void addImmediatePropagateCCTrigger(Resource resource,
			String relationName, List<Resource> createdResources,
			String sendingApplication, int nbAttempts);

	/**
	 * Add immediate trigger for propagate collection update. Trigger used when
	 * a collection is updated.
	 * 
	 * @param resource
	 *            : the {@link Resource} owner of the collection
	 * @param relationName
	 *            : the name of the collection updated
	 * @param updatedResources
	 *            : the list of {@link Resource} updated
	 * @param addedResources
	 *            : the list of {@link Resource} added to the collection
	 * @param removedResources
	 *            : the list of {@link Resource} removed from the collection
	 * @param sendingApplication
	 *            : the name of the {@link Application} responsible for
	 *            synchronization
	 * @param nbAttempts
	 *            : the number of attempts to synchronize the {@link Resource}
	 */
	public void addImmediatePropagateCUTrigger(Resource resource,
			String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources,
			String sendingApplication, int nbAttempts);

}
