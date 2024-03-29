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
package org.myec3.socle.synchro.scheduler.service.impl;

import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationError;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.scheduler.constants.MyEc3SynchroConstants;
import org.myec3.socle.synchro.scheduler.job.*;
import org.myec3.socle.synchro.scheduler.job.resources.*;
import org.myec3.socle.synchro.scheduler.service.SchedulerService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Concrete implementation of the Scheduler Service with quartz framework. It's
 * in this class which we define triggers used during the synchronization
 * process.
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
public abstract class SchedulerServiceImpl implements SchedulerService {

	private static final String QUARTZ_DELAYED_GROUP = "DELAYED";
	private static final String QUARTZ_CREATION_GROUP = "CREATION";
	private static final String QUARTZ_UPDATE_GROUP = "UPDATE";
	private static final String QUARTZ_DELETION_GROUP = "DELETION";
	private static final String QUARTZ_COLLECTION_CREATE_GROUP = "COLLECTION CREATE";
	private static final String QUARTZ_COLLECTION_UPDATE_GROUP = "COLLECTION UPDATE";
	private static final String QUARTZ_COLLECTION_REMOVE_GROUP = "COLLECTION REMOVE";
	private static final String QUARTZ_SUBSCRIPTION_PROFILE_GROUP = "SUBSCRIPTION PROFILE";
	private static final String QUARTZ_SUBSCRIPTION_RESOURCE_GROUP = "SUBSCRIPTION RESOURCE";
	private static final int NAME_MAX_LENGHT = 200;
	private static final String TRIGGER_LABEL = "Trigger";
	private static final String DELAYED_JOB_NAME = "delayedJob";

	private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

	private Scheduler scheduler;

	/**
	 * Set the correct scheduler
	 * 
	 * @see ParallelSchedulerServiceImpl
	 * @see SimpleSchedulerServiceImpl
	 * 
	 * @param scheduler : the schudeler to use
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * get the correct scheduler
	 * 
	 * @see ParallelSchedulerServiceImpl
	 * @see SimpleSchedulerServiceImpl
	 * 
	 * @return the correct scheduler to use depending on job to schedule.
	 */
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	/**
	 * Generate a unique name for the job.
	 * 
	 * @param jobName  : the actual job name
	 * @param resource : the resource synchronized
	 * @return a unique job name
	 */
	private String generateUniqueName(String jobName, Resource resource) {
		logger.debug("Enterring method generateUniqueName");
		String randomUuid = UUID.randomUUID().toString();
		String generatedName = jobName + resource.getId() + Calendar.getInstance().getTimeInMillis() + randomUuid;
		if (generatedName.length() >= NAME_MAX_LENGHT) {
			return generatedName.substring(0, (NAME_MAX_LENGHT - 1));
		}
		return generatedName;
	}

	/**
	 * Return the JobBuilder corresponding at the concrete type of the resource
	 * object
	 * 
	 * @param resource : the resource synchronized
	 * @return the correct JobBuilder corresponding at the concrete type of the
	 *         resource object
	 */
	public JobBuilder getResourceJobBuilder(Resource resource) {
		if (AdminProfile.class.equals(resource.getClass())) {
			return JobBuilder.newJob(AdminSynchronizationJob.class);
		}
		if (AgentProfile.class.equals(resource.getClass())) {
			return JobBuilder.newJob(AgentSynchronizationJob.class);
		}
		if (EmployeeProfile.class.equals(resource.getClass())) {
			return JobBuilder.newJob(EmployeeSynchronizationJob.class);
		}
		if (Customer.class.equals(resource.getClass())) {
			return JobBuilder.newJob(CustomerSynchronizationJob.class);
		}
		if (Organism.class.equals(resource.getClass())) {
			return JobBuilder.newJob(OrganismSynchronizationJob.class);
		}
		if (OrganismDepartment.class.equals(resource.getClass())) {
			return JobBuilder.newJob(OrganismDepartmentSynchronizationJob.class);
		}
		if (Company.class.equals(resource.getClass())) {
			return JobBuilder.newJob(CompanySynchronizationJob.class);
		}
		if (CompanyDepartment.class.equals(resource.getClass())) {
			return JobBuilder.newJob(CompanyDepartmentSynchronizationJob.class);
		}
		if (Establishment.class.equals(resource.getClass())) {
			return JobBuilder.newJob(EstablishmentSynchronizationJob.class);
		}

		// If the resource concrete type is not one of those described behind,
		// the resource is not a synchronizable object
		// we throw an Exception
		StringBuilder builder = new StringBuilder();
		builder.append("No matching Resource type found for class '").append(resource.getClass())
				.append("' !! JobDetailBean cannot be resolved. Please consider adding a corresponding Resource type.");

		throw new NoClassDefFoundError(builder.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateCreationTrigger(String jobName, Resource resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {
		long startTime = System.currentTimeMillis() + MyEc3SynchroConstants.DELAY;
		logger.debug("Enterring method addImmediateCreationTrigger");

		// inject created resource object
		JobDetail job = JobBuilder.newJob(CreationSynchronizationJob.class)
				.withIdentity(generateUniqueName(jobName, resource), QUARTZ_CREATION_GROUP)
				.storeDurably(false)
				.build();

		// trigger launched immediately an only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + jobName, resource), QUARTZ_CREATION_GROUP)
				.startAt(new Date(startTime)).build();
		trig.getJobDataMap().put("resource", resource);
		trig.getJobDataMap().put("listApplicationIdToResynchronize", listApplicationIdToResynchronize);
		trig.getJobDataMap().put("synchronizationType", synchronizationType);
		trig.getJobDataMap().put("sendingApplication", sendingApplication);
		try {
			scheduler.scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error(
					"[addImmediateCreationTrigger] An error has occured during lauch creation job: " + e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateDeletionTrigger(String jobName, Resource resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {
		long startTime = System.currentTimeMillis() + MyEc3SynchroConstants.DELAY;
		logger.debug("Enterring method addImmediateDeletionTrigger");

		JobDetail job = JobBuilder.newJob(DeletionSynchronizationJob.class)
				.withIdentity(generateUniqueName(jobName, resource), QUARTZ_DELETION_GROUP)
				.storeDurably(false)
				.build();

		// inject deleted resource object
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + jobName, resource), QUARTZ_DELETION_GROUP)
				.startAt(new Date(startTime)).build();
		trig.getJobDataMap().put("resource", resource);
		trig.getJobDataMap().put("listApplicationIdToResynchronize", listApplicationIdToResynchronize);
		trig.getJobDataMap().put("synchronizationType", synchronizationType);
		trig.getJobDataMap().put("sendingApplication", sendingApplication);

		try {
			scheduler.scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error(
					"[addImmediateDeletionTrigger] An error has occured during lauch deletion job: " + e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateUpdateTrigger(String jobName, Resource resource,
			List<Long> listApplicationIdToResynchronize, SynchronizationType synchronizationType,
			String sendingApplication) {
		long startTime = System.currentTimeMillis() + MyEc3SynchroConstants.DELAY;
		logger.debug("Enterring method addImmediateUpdateTrigger");

		JobDetail job = JobBuilder.newJob(UpdateSynchronizationJob.class)
				.withIdentity(generateUniqueName(jobName, resource), QUARTZ_UPDATE_GROUP)
				.storeDurably(false)
				.build();

		// inject update resource object
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + jobName, resource), QUARTZ_UPDATE_GROUP)
				.startAt(new Date(startTime)).build();
		trig.getJobDataMap().put("resource", resource);
		trig.getJobDataMap().put("listApplicationIdToResynchronize", listApplicationIdToResynchronize);
		trig.getJobDataMap().put("synchronizationType", synchronizationType);
		trig.getJobDataMap().put("sendingApplication", sendingApplication);

		try {
			scheduler.scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error("[addImmediateUpdateTrigger] An error has occured during lauch update job: " + e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionCompanyDepartmentTrigger(String jobName, Resource resource,
			SynchronizationSubscription subscription, SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionCompanyDepartmentTrigger");
		this.addImmediateSynchronizationSubscriptionResourceTrigger(getResourceJobBuilder(resource), jobName, resource,
				subscription, synchronizationJobType, synchronizationType, sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionEstablishmentTrigger(String jobName, Resource resource,
			SynchronizationSubscription subscription, SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionEstablishmentTrigger");
		this.addImmediateSynchronizationSubscriptionResourceTrigger(getResourceJobBuilder(resource), jobName, resource,
				subscription,
				synchronizationJobType, synchronizationType, sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionCompanyTrigger(String jobName, Resource resource,
			SynchronizationSubscription subscription, SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionCompanyTrigger");
		this.addImmediateSynchronizationSubscriptionResourceTrigger(getResourceJobBuilder(resource), jobName, resource,
				subscription,
				synchronizationJobType, synchronizationType, sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionAdminProfileTrigger(String jobName, AdminProfile resource,
			List<Role> listRoles, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType, SynchronizationType synchronizationType,
			String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionAdminProfileTrigger");
		this.addImmediateSynchronizationSubscriptionProfileTrigger(getResourceJobBuilder(resource), jobName, resource,
				listRoles, subscription,
				synchronizationJobType, synchronizationType, sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionAgentProfileTrigger(String jobName, AgentProfile resource,
			List<Role> listRoles, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType, SynchronizationType synchronizationType,
			String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionAgentProfileTrigger");
		//Pour slow pas de synchro d'agent si il n'y pas pas de certificat
		if (subscription.getId() == 22 && resource.getUser() != null && StringUtils.isEmpty(resource.getUser().getCertificate())) {
			logger.info(
					"Pas de synchro à faire pour slow dans le cas d'un agent sans certificat "
							+ subscription.getApplication().getUrl());
		} else {
			this.addImmediateSynchronizationSubscriptionProfileTrigger(getResourceJobBuilder(resource), jobName, resource,
					listRoles, subscription,
					synchronizationJobType, synchronizationType, sendingApplication);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionEmployeeProfileTrigger(String jobName, EmployeeProfile resource,
			List<Role> listRoles, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType, SynchronizationType synchronizationType,
			String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionEmployeeProfileTrigger");
		this.addImmediateSynchronizationSubscriptionProfileTrigger(getResourceJobBuilder(resource), jobName, resource,
				listRoles,
				subscription, synchronizationJobType, synchronizationType, sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionCustomerTrigger(String jobName, Resource resource,
			SynchronizationSubscription subscription, SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionCustomerTrigger");
		this.addImmediateSynchronizationSubscriptionResourceTrigger(getResourceJobBuilder(resource), jobName, resource,
				subscription,
				synchronizationJobType, synchronizationType, sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionOrganismDepartmentTrigger(String jobName, Resource resource,
			SynchronizationSubscription subscription, SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionOrganismDepartmentTrigger");
		this.addImmediateSynchronizationSubscriptionResourceTrigger(getResourceJobBuilder(resource), jobName, resource,
				subscription, synchronizationJobType, synchronizationType, sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateSynchronizationSubscriptionOrganismTrigger(String jobName, Resource resource,
			SynchronizationSubscription subscription, SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionOrganismTrigger");
		this.addImmediateSynchronizationSubscriptionResourceTrigger(getResourceJobBuilder(resource), jobName, resource,
				subscription,
				synchronizationJobType, synchronizationType, sendingApplication);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addImmediateSynchronizationSubscriptionProfileTrigger(JobBuilder jobBuilder, String jobName,
			Profile resource, List<Role> listRoles, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType, SynchronizationType synchronizationType,
			String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionProfileTrigger");

		// Job properties
		JobDetail job = jobBuilder
				.withIdentity(generateUniqueName(jobName, resource), QUARTZ_SUBSCRIPTION_PROFILE_GROUP)
				.storeDurably(false)
				.build();

		// trigger launched immediately an only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + jobName, resource), QUARTZ_SUBSCRIPTION_PROFILE_GROUP)
				.startAt(new Date()).build();
		trig.getJobDataMap().put("resource", resource);
		trig.getJobDataMap().put("roles", listRoles);
		trig.getJobDataMap().put("subscription", subscription);
		trig.getJobDataMap().put("synchronizationJobType", synchronizationJobType);
		trig.getJobDataMap().put("synchronizationErrorId", null);
		trig.getJobDataMap().put("synchronizationType", synchronizationType);
		trig.getJobDataMap().put("sendingApplication", sendingApplication);
		try {
			scheduler.scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error("[addImmediateSynchronizationSubscriptionProfileTrigger] An error has occured during lauch "
					+ synchronizationJobType + " job: " + e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	private void addImmediateSynchronizationSubscriptionResourceTrigger(JobBuilder jobBuilder, String jobName,
			Resource resource, SynchronizationSubscription subscription, SynchronizationJobType synchronizationJobType,
			SynchronizationType synchronizationType, String sendingApplication) {
		logger.debug("Enterring method addImmediateSynchronizationSubscriptionResourceTrigger");

		// Job properties
		JobDetail job = jobBuilder
				.withIdentity(generateUniqueName(jobName, resource), QUARTZ_SUBSCRIPTION_RESOURCE_GROUP)
				.storeDurably(false)
				.build();

		// trigger launched immediately and only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + jobName, resource), QUARTZ_SUBSCRIPTION_RESOURCE_GROUP)
				.startAt(new Date()).build();
		trig.getJobDataMap().put("resource", resource);
		trig.getJobDataMap().put("subscription", subscription);
		trig.getJobDataMap().put("synchronizationJobType", synchronizationJobType);
		trig.getJobDataMap().put("synchronizationErrorId", null);
		trig.getJobDataMap().put("synchronizationType", synchronizationType);
		trig.getJobDataMap().put("sendingApplication", sendingApplication);

		try {
			scheduler.scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error("[addImmediateSynchronizationSubscriptionResourceTrigger] An error has occured during lauch "
					+ synchronizationJobType + " job: " + e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateCollectionCreateTrigger(String jobName, Resource resource, String relationName,
			List<Resource> createdResources, String sendingApplication) {
		long startTime = System.currentTimeMillis() + MyEc3SynchroConstants.DELAY;
		logger.debug("Enterring method addImmediateCollectionCreateTrigger");
		// trigger launched immediately an only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + jobName, resource), QUARTZ_COLLECTION_CREATE_GROUP)
				.startAt(new Date(startTime)).build();

		// Job properties
		JobDetail job = JobBuilder.newJob(CollectionCreateSynchronizationJob.class)
				.withIdentity(generateUniqueName(jobName, resource), QUARTZ_COLLECTION_CREATE_GROUP)
				.storeDurably(false)
				.build();
		// inject created resource object
		job.getJobDataMap().put("resource", resource);
		job.getJobDataMap().put("relationName", relationName);
		job.getJobDataMap().put("createdResources", createdResources);
		job.getJobDataMap().put("sendingApplication", sendingApplication);
		try {
			scheduler.scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error(
					"[addImmediateCollectionCreateTrigger] An error has occured during lauch collection create job: "
							+ e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateCollectionUpdateTrigger(String jobName, Resource resource, String relationName,
			List<Resource> updatedResources, List<Resource> addedResources, List<Resource> removedResources,
			String sendingApplication) {
		long startTime = System.currentTimeMillis() + MyEc3SynchroConstants.DELAY;
		logger.debug("Enterring method addImmediateCollectionUpdateTrigger");
		// trigger launched immediately an only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + jobName, resource), QUARTZ_COLLECTION_UPDATE_GROUP)
				.startAt(new Date(startTime)).build();

		// Job properties
		JobDetail job = JobBuilder.newJob(CollectionUpdateSynchronizationJob.class)
				.withIdentity(generateUniqueName(jobName, resource), QUARTZ_COLLECTION_UPDATE_GROUP)
				.storeDurably(false)
				.build();

		// inject update resource object
		job.getJobDataMap().put("resource", resource);
		job.getJobDataMap().put("relationName", relationName);
		job.getJobDataMap().put("updatedResources", updatedResources);
		job.getJobDataMap().put("addedResources", addedResources);
		job.getJobDataMap().put("removedResources", removedResources);
		job.getJobDataMap().put("sendingApplication", sendingApplication);
		try {
			scheduler.scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error("[addImmediateCollectionUpdateTrigger] An error has occured during lauch collection update job",e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediateCollectionRemoveTrigger(String jobName, Resource resource, String relationName,
			List<Resource> removedResources, String sendingApplication) {
		long startTime = System.currentTimeMillis() + MyEc3SynchroConstants.DELAY;
		logger.debug("Enterring method addImmediateCollectionRemoveTrigger");
		// trigger launched immediately an only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + jobName, resource), QUARTZ_COLLECTION_REMOVE_GROUP)
				.startAt(new Date(startTime)).build();

		// Job properties
		JobDetail job = JobBuilder.newJob(CollectionRemoveSynchronizationJob.class)
				.withIdentity(generateUniqueName(jobName, resource), QUARTZ_COLLECTION_REMOVE_GROUP)
				.storeDurably(false)
				.build();

		// inject update resource object
		job.getJobDataMap().put("resource", resource);
		job.getJobDataMap().put("relationName", relationName);
		job.getJobDataMap().put("oldResources", removedResources);
		job.getJobDataMap().put("sendingApplication", sendingApplication);
		try {
			scheduler.scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error("[addImmediateCollectionRemoveTrigger] An error has occured during lauch collection remove job",e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addDelayedResourceTrigger(Long initialSynchronizationId, SynchronizationError synchronizationError,
			Resource resource, List<Role> listRoles, SynchronizationSubscription subscription,
			SynchronizationJobType synchronizationJobType, Long delay, SynchronizationType synchronizationType,
			String sendingApplication) {
		logger.debug("Enterring method addDelayedResourceTrigger");
		// check if delay not = null
		if (delay == null) {
			logger.error("[addDelayedResourceTrigger] delay is null for handling errors : " + "MethodType : "
					+ synchronizationError.getMethodType() + " Resource : " + resource.getName() + ", id : "
					+ resource.getId());
			// We set a default delay to continue handling errors
			delay = 1000L;
		}
		logger.info("[addDelayedResourceTrigger] Initializing newDelayedResourceTrigger with a delay = {} ms" ,delay);

		// Create new trigger with a delay
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(TRIGGER_LABEL + generateUniqueDelayedJob(resource, subscription, delay),
						QUARTZ_DELAYED_GROUP)
				.startAt(Date.from(Instant.now().plusMillis(delay))).build();

		if (resource.getClass().getSuperclass().equals(Profile.class)) {
			trig.getJobDataMap().put("roles", listRoles);
		}

		// Get the jobDetailBean corresponding to the resource class
		JobDetail jobDetail = this.getResourceJobBuilder(resource)
				.withIdentity(generateUniqueDelayedJob(resource, subscription, delay),
						QUARTZ_DELAYED_GROUP)
				.storeDurably(false)
				.build();
		// inject resource object
		jobDetail.getJobDataMap().put("initialSynchronizationId", initialSynchronizationId);
		jobDetail.getJobDataMap().put("synchronizationErrorId", synchronizationError.getId());
		jobDetail.getJobDataMap().put("resource", resource);
		jobDetail.getJobDataMap().put("subscription", subscription);
		jobDetail.getJobDataMap().put("synchronizationJobType", synchronizationJobType);
		jobDetail.getJobDataMap().put("synchronizationType", synchronizationType);
		jobDetail.getJobDataMap().put("sendingApplication", sendingApplication);

		try {
			scheduler.scheduleJob(jobDetail, trig);
		} catch (SchedulerException e) {
			logger.error("[addDelayedResourceTrigger] An error has occured during lauch delayed job", e);
		}
	}

	@Override
	public void deleteDelayedAfterSucess(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		try {
			String identiferJobToFind = buildPrefixUniqueDelayedJob(resource, synchronizationSubscription);

			// First : getting all job key
			List<String> jobKeyInProgressNotToDelete = scheduler.getCurrentlyExecutingJobs().stream()
					.filter(jobExecutionContext -> jobExecutionContext.getJobDetail().getKey().getName().startsWith(identiferJobToFind))
					.map(jobExecutionContext -> jobExecutionContext.getJobDetail().getKey().getName())
					.collect(Collectors.toList());

			// Get job Identifier for DELAYED GROUP ONLY
			scheduler.getJobKeys(GroupMatcher.jobGroupEquals(QUARTZ_DELAYED_GROUP)).stream()
					.filter(jobKey -> jobKey.getName().startsWith(identiferJobToFind) && !jobKeyInProgressNotToDelete.contains(jobKey.getName()))
					.forEach(jobKey -> 	deleteJob(jobKey, resource, synchronizationSubscription));
		}
		catch (SchedulerException e) {
			logger.error("[DELETE JOB] Error when try delete Job for resource {}, subscription {}",
					resource.getId(), synchronizationSubscription.getId() ,e);
		}
	}

	/**
	 * Delete Job given a JobKey
	 * @param jobKey	jobkey identifier
	 * @param resource	resource associated
	 * @param synchronizationSubscriptio	subcription associated
	 */
	private void deleteJob(JobKey jobKey, Resource resource, SynchronizationSubscription synchronizationSubscriptio) {
		try {
			logger.info("[DELETE JOB] DELAYED JOB {} AFTER SUCCESS on ressource {} and subscription {} ",
					jobKey.getName(), resource.getId(), synchronizationSubscriptio.getId());
			scheduler.deleteJob(jobKey);
		}  catch (SchedulerException e) {
			logger.error("[DELETE JOB] Error when try delete Job {}", jobKey.getName() ,e);
		}
	}

	/**
	 * Generate unique Job Name for a delayed JOB
	 * Format Name :
	 * 	delayedJob[subscriptionId][resourceId][Delay][timestamp][UUID]
	 *
	 * @param resource	resource tfor job
	 * @param synchronizationSubscription	subscription job
	 * @param delay	the delay for the next Trigger
	 * @return	uniquement Name for Job
	 */
	private String generateUniqueDelayedJob(Resource resource, SynchronizationSubscription synchronizationSubscription, Long delay) {
		logger.debug("Enterring method generateUniqueDelayedJob");
		String randomUuid = UUID.randomUUID().toString();
		String generatedName = buildPrefixUniqueDelayedJob(resource, synchronizationSubscription) +
				delay +
				Calendar.getInstance().getTimeInMillis() +
				randomUuid;
		if (generatedName.length() >= NAME_MAX_LENGHT) {
			return generatedName.substring(0, (NAME_MAX_LENGHT - 1));
		}
		return generatedName;
	}

	/**
	 * Build prefixe for generateUniqueDelayedJob
	 * @param resource
	 * @param synchronizationSubscription
	 * @return prefix for Job
	 */
	private String buildPrefixUniqueDelayedJob(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		return DELAYED_JOB_NAME + synchronizationSubscription.getId() +
				resource.getId();
	}
}
