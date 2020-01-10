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
package org.myec3.socle.synchro.scheduler.service;

import java.util.List;

import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.CompanyDepartment;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationError;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.quartz.Scheduler;

/**
 * This service provides an API to store trigger jobs "on the fly", based on
 * declared configuration. It allows to store creation, update and deletion
 * triggers in order to perform synchronization tasks.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis cucchietti <denis.cucchietti@atosorigin.com>
 */
public interface SchedulerService {

	/**
	 * @return the correct scheduler depending on the jobDetailBean to perform
	 */
	Scheduler getScheduler();

	/**
	 * Schedules a trigger to perform synchronization tasks related to Resource
	 * creation. The trigger will be launched immediately and one time.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            : resource created
	 * @param listApplicationIdToResynchronize
	 *            : the list of applications to synchronize (if null, all
	 *            applications are synchronized)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateCreationTrigger(String jobName, Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to Resource
	 * deletion. The trigger will be launched immediately and one time.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            : resource deleted
	 * @param listApplicationIdToResynchronize
	 *            : the list of applications to synchronize (if null, all
	 *            applications are synchronized)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateDeletionTrigger(String jobName, Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to Resource
	 * update. The trigger will be launched immediately and one time.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            : resource updated
	 * @param listApplicationIdToResynchronize
	 *            : the list of applications to synchronize (if null, all
	 *            applications are synchronized)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateUpdateTrigger(String jobName, Resource resource,
			List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks when an error
	 * occured. The trigger will be launched with a delay and one or several
	 * times depending on the synchronization success. The trigger can be
	 * launched in @see ResourcesSynchronizationJob.
	 * 
	 * @param initialSynchronizationId
	 *            : id of the first synchronization (@see
	 *            SynchronizationInitial) triggered for the current resource
	 * @param synchronizationError
	 *            : the error occured during the synchronization process (@see
	 *            SynchronizationError)
	 * @param resource
	 *            :the resource to synchronize again
	 * @param listRole
	 *            : list of roles of {@link Profile}. This list can be null if
	 *            the resource to synchronize is not a Profile
	 * @param subscription
	 *            : the {@link SynchronizationSubscription} concerned by the
	 *            error
	 * @param synchronizationJobType
	 *            : the job's type used during the synchronization process (@see
	 *            SynchronizationJobType.class)
	 * @param delay
	 *            : the delay before the job is lunched again
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addDelayedResourceTrigger(Long initialSynchronizationId,
			SynchronizationError synchronizationError, Resource resource,
			List<Role> listRole, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType, Long delay,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to a
	 * {@link CompanyDepartment}. The trigger will be launched in
	 * CompanyDepartment Manager (@see
	 * CompanyDerpatmentSynchronizationManagerImpl) with a delay and one or
	 * several times depending on the synchronization success.
	 * 
	 * The trigger scheduled call a webservice's method depending on the
	 * {@link SynchronizationJobType} value.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is a
	 *            {@link CompanyDepartment}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionCompanyDepartmentTrigger(
			String jobName, Resource resource,
			SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);
	
	/**
	 * Schedules a trigger to perform synchronization tasks related to a
	 * {@link Establishment}. The trigger will be launched in
	 * Establishment Manager (@see
	 * EstablishmentSynchronizationManagerImpl) with a delay and one or
	 * several times depending on the synchronization success.
	 * 
	 * The trigger scheduled call a webservice's method depending on the
	 * {@link SynchronizationJobType} value.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is a
	 *            {@link Establishment}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionEstablishmentTrigger(
			String jobName, Resource resource,
			SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to an
	 * {@link OrganismDepartment}. The trigger will be launched in
	 * OrganismDepartment Manager (@see
	 * OrganismDerpatmentSynchronizationManagerImpl.class) with a delay and one
	 * or several times depending on the synchronization success.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is an
	 *            {@link OrganismDepartment}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionOrganismDepartmentTrigger(
			String jobName, Resource resource,
			SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to a
	 * {@link Company}. The trigger will be launched in Company Manager (@see
	 * CompanySynchronizationManagerImpl.class) with a delay and one or several
	 * times depending on the synchronization success.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is a {@link Company}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionCompanyTrigger(String jobName,
			Resource resource, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to a
	 * {@link Customer}. The trigger will be launched in Customer Manager (@see
	 * CustomerSynchronizationManagerImpl) with a delay and one or several times
	 * depending on the synchronization success.
	 * 
	 * The trigger scheduled call a webservice's method depending on the
	 * {@link SynchronizationJobType} value.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is a {@link Customer}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionCustomerTrigger(String jobName,
			Resource resource, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to an
	 * {@link Organism}. The trigger will be launched in Organism Manager (@see
	 * OrganismSynchronizationManagerImpl.class) with a delay and one or several
	 * times depending on the synchronization success.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is an {@link Organism}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionOrganismTrigger(String jobName,
			Resource resource, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to an
	 * {@link AdminProfile}. The trigger will be launched in Agent Profile
	 * Manager (@see AdminProfileSynchronizationManagerImpl) with a delay and
	 * one or several times depending on the synchronization success.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is an
	 *            {@link AdminProfile}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionAdminProfileTrigger(
			String jobName, AdminProfile resource, List<Role> listRole,
			SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to an
	 * {@link AgentProfile}. The trigger will be launched in Agent Profile
	 * Manager (@see AgentProfileSynchronizationManagerImpl) with a delay and
	 * one or several times depending on the synchronization success.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is an
	 *            {@link AgentProfile}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionAgentProfileTrigger(
			String jobName, AgentProfile resource, List<Role> listRole,
			SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Schedules a trigger to perform synchronization tasks related to an
	 * {@link EmployeeProfile}. The trigger will be launched in Employee Profile
	 * Manager (@see EmployeeProfileSynchronizationManagerImpl.class) with a
	 * delay and one or several times depending on the synchronization success.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized. In this case it is an
	 *            {@link EmployeeProfile}
	 * @param subscription
	 *            linked subscription
	 * @param synchronizationJobType
	 *            type of job synchronization (@see
	 *            SynchronizationJobType.class)
	 * @param synchronizationType
	 *            : the type of synchronization (@see SynchronizationType.class)
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateSynchronizationSubscriptionEmployeeProfileTrigger(
			String jobName, EmployeeProfile resource, List<Role> listRoles,
			SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication);

	/**
	 * Collections create trigger. Trigger fired when a collection is created.
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized.
	 * @param relationName
	 *            : the name of the collection created.
	 * @param createdResources
	 *            : the list of created resources contained in the new
	 *            collection
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateCollectionCreateTrigger(String jobName, Resource resource,
			String relationName, List<Resource> createdResources,
			String sendingApplication);

	/**
	 * Collections update trigger. Trigger fired when a collection is updated.
	 * 
	 * * @param jobName : technical job name
	 * 
	 * @param resource
	 *            resource synchronized.
	 * @param relationName
	 *            : the name of the collection updated.
	 * @param updatedResources
	 *            : the list of resources updated into the collection
	 * @param addedResources
	 *            : the list of new resources added into the collection
	 * @param removedResources
	 *            : the list of old resources removed from the collection
	 * 
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateCollectionUpdateTrigger(String jobName, Resource resource,
			String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources,
			String sendingApplication);

	/**
	 * Collections remove trigger. Trigger fired when a collection is deleted
	 * (never used).
	 * 
	 * @param jobName
	 *            : technical job name
	 * @param resource
	 *            resource synchronized.
	 * @param relationName
	 *            : the name of the collection created.
	 * @param removedResources
	 *            : the list of old resources removed from the collection
	 * @param sendingApplication
	 *            : the name of the application sending JMS
	 */
	void addImmediateCollectionRemoveTrigger(String jobName, Resource resource,
			String relationName, List<Resource> removedResources,
			String sendingApplication);
}
