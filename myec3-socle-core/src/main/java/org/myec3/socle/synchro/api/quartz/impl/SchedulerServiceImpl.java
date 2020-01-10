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
package org.myec3.socle.synchro.api.quartz.impl;

import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.myec3.socle.core.constants.MyEc3EsbConstants;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.api.constants.SynchronizationParametersType;
import org.myec3.socle.synchro.api.constants.SynchronizationType;
import org.myec3.socle.synchro.api.quartz.SchedulerService;
import org.myec3.socle.synchro.api.quartz.job.PropagateCCSynchronizationJob;
import org.myec3.socle.synchro.api.quartz.job.PropagateCUDSynchronizationJob;
import org.myec3.socle.synchro.api.quartz.job.PropagateCUSynchronizationJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 * Concrete implementation of the Scheduler Service used during synchronization
 * process. This scheduler is used in case of an error occurs during the
 * transmission of the JMS to ESB IN queue.
 * 
 * This scheduler is defined into the application context and use a JobStoreTX
 * to save triggers and jobs into the database. The scheduler configuration is
 * definied into quartzJmsScheduler.properties
 * 
 * @see http://www.quartz-scheduler.org/docs/configuration/ConfigJobStoreTX.html
 * 
 * @author Matthieu Proboeuf <matthieu.proboeuf@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Service("synchroQuartzSchedulerService")
public class SchedulerServiceImpl implements SchedulerService {
	protected static Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

	private static final String TRIGGER_LABEL = "Trigger";
	private static final String QUARTZ_PROPAGATE_CUD_GROUP = "PROPAGATE_CUD";
	private static final String QUARTZ_PROPAGATE_CC_GROUP = "PROPAGATE_CC";
	private static final String QUARTZ_PROPAGATE_CU_GROUP = "PROPAGATE_CU";
	private static final int NAME_MAX_LENGHT = 200;
	private static final String PROPAGATE_CUD_JOB_NAME = "propagateCUDJob";
	private static final String PROPAGATE_CC_JOB_NAME = "propagateCCJob";
	private static final String PROPAGATE_CU_JOB_NAME = "propagateCUJob";

	/**
	 * Default constructor. Bean declared in jmsInMyec3Context.xml
	 */
	public SchedulerServiceImpl() {

	}

	/**
	 * Default scheduler used to send JMS to remote ESB queue (triggers are stored
	 * into ram)
	 */
	@Autowired
	@Qualifier("ramScheduler")
	private SchedulerFactoryBean ramScheduler;

	/**
	 * Scheduler used to manage triggers in case of error (triggers are stored into
	 * the database)
	 */
	@Autowired
	@Qualifier("jmsScheduler")
	private SchedulerFactoryBean jmsScheduler;

	/**
	 * Generate a unique name for the current job
	 * 
	 * @param jobName  : default job name
	 * @param resource : the {@link Resource} to synchronize
	 * @return a unique job name
	 */
	private String generateUniqueName(String jobName, Resource resource) {
		logger.debug("Enterring method generateUniqueName");
		double r = Math.random();
		double s = Math.random();
		String generatedName = jobName + resource.getId() + Calendar.getInstance().getTimeInMillis() + r + s;
		if (generatedName.length() >= NAME_MAX_LENGHT) {
			return generatedName.substring(0, (NAME_MAX_LENGHT - 1));
		}
		return generatedName;
	}

	/**
	 * Return the start time of trigger depending of number of attempts
	 * 
	 * @param nbAttempts : the given nb of attempts
	 * @return the start time to fire the trigger
	 */
	public Long getFireTriggerStartTime(int nbAttempts) {
		if (nbAttempts > 0) {
			return System.currentTimeMillis() + MyEc3EsbConstants.getQuartzResynchronizationDelay();
		}
		return System.currentTimeMillis() + MyEc3EsbConstants.getQuartzSynchronizationDelay();
	}

	/**
	 * Return the scheduler to use to fire trigger depending on nbAttempts. For the
	 * first attempt we use a ram scheduler
	 * 
	 * @param nbAttempts
	 * @return the correct scheduler used to execute trigger
	 */
	public Scheduler getScheduler(int nbAttempts) {
		if (nbAttempts == 0) {
			logger.debug("ramScheduler used");
			return this.ramScheduler.getScheduler();
		}
		logger.debug("jmsScheduler used");
		return this.jmsScheduler.getScheduler();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediatePropagateCUDTrigger(Resource resource, List<Long> listApplicationIdToResynchronize,
			SynchronizationType synchronizationType, SynchronizationJobType synchronizationJobType, int nbAttempts) {
		logger.debug("Enterring method addImmediatePropagateCUDTrigger");

		// inject created resource object
		JobDetail job = JobBuilder.newJob(PropagateCUDSynchronizationJob.class)
				.withIdentity(generateUniqueName(PROPAGATE_CUD_JOB_NAME, resource), QUARTZ_PROPAGATE_CUD_GROUP)
				.storeDurably(false)
				.build();
		// trigger launched immediately an only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + PROPAGATE_CUD_JOB_NAME, resource),
						QUARTZ_PROPAGATE_CUD_GROUP)
				.startAt(new Date(this.getFireTriggerStartTime(nbAttempts))).build();
		trig.getJobDataMap().put(SynchronizationParametersType.RESOURCE.getValue(), resource);
		trig.getJobDataMap().put(SynchronizationParametersType.LIST_APPLICATION_ID.getValue(),
				listApplicationIdToResynchronize);
		trig.getJobDataMap().put(SynchronizationParametersType.SYNCHRONIZATION_TYPE.getValue(), synchronizationType);
		trig.getJobDataMap().put(SynchronizationParametersType.SYNCHRONIZATION_JOB_TYPE.getValue(),
				synchronizationJobType);
		trig.getJobDataMap().put(SynchronizationParametersType.NB_ATTEMPTS.getValue(), nbAttempts);

		try {
			this.getScheduler(nbAttempts).scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error("[addImmediatePropagateCUDTrigger] An error has occured during lauch propagateCUD job: "
					+ e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediatePropagateCCTrigger(Resource resource, String relationName, List<Resource> createdResources,
			String sendingApplication, int nbAttempts) {
		logger.debug("Enterring method addImmediatePropagateCCTrigger");

		// inject created resource object
		JobDetail job = JobBuilder.newJob(PropagateCCSynchronizationJob.class)
				.withIdentity(generateUniqueName(PROPAGATE_CC_JOB_NAME, resource), QUARTZ_PROPAGATE_CC_GROUP)
				.storeDurably(false)
				.build();

		// trigger launched immediately an only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + PROPAGATE_CC_JOB_NAME, resource),
						QUARTZ_PROPAGATE_CC_GROUP)
				.startAt(new Date(this.getFireTriggerStartTime(nbAttempts) + new Long(1000))).build();
		trig.getJobDataMap().put(SynchronizationParametersType.RESOURCE.getValue(), resource);
		trig.getJobDataMap().put(SynchronizationParametersType.RELATION_NAME.getValue(), relationName);
		trig.getJobDataMap().put(SynchronizationParametersType.CREATED_RESOURCES.getValue(), createdResources);
		trig.getJobDataMap().put(SynchronizationParametersType.SENDING_APPLICATION.getValue(), sendingApplication);
		trig.getJobDataMap().put(SynchronizationParametersType.NB_ATTEMPTS.getValue(), nbAttempts);

		try {
			this.getScheduler(nbAttempts).scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error("[addImmediatePropagateCCTrigger] An error has occured during lauch propagateCC job: "
					+ e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addImmediatePropagateCUTrigger(Resource resource, String relationName, List<Resource> updatedResources,
			List<Resource> addedResources, List<Resource> removedResources, String sendingApplication, int nbAttempts) {
		logger.debug("Enterring method addImmediatePropagateCUTrigger");

		// inject created resource object
		JobDetail job = JobBuilder.newJob(PropagateCUSynchronizationJob.class)
				.withIdentity(generateUniqueName(PROPAGATE_CU_JOB_NAME, resource), QUARTZ_PROPAGATE_CU_GROUP)
				.storeDurably(false)
				.build();

		// trigger launched immediately an only one time
		SimpleTrigger trig = (SimpleTrigger) newTrigger()
				.withIdentity(generateUniqueName(TRIGGER_LABEL + PROPAGATE_CU_JOB_NAME, resource),
						QUARTZ_PROPAGATE_CU_GROUP)
				.startAt(new Date(this.getFireTriggerStartTime(nbAttempts) + new Long(1000))).build();
		trig.getJobDataMap().put(SynchronizationParametersType.RESOURCE.getValue(), resource);
		trig.getJobDataMap().put(SynchronizationParametersType.RELATION_NAME.getValue(), relationName);
		trig.getJobDataMap().put(SynchronizationParametersType.UPDATED_RESOURCES.getValue(), updatedResources);
		trig.getJobDataMap().put(SynchronizationParametersType.ADDED_RESOURCES.getValue(), addedResources);
		trig.getJobDataMap().put(SynchronizationParametersType.REMOVED_RESOURCES.getValue(), removedResources);
		trig.getJobDataMap().put(SynchronizationParametersType.SENDING_APPLICATION.getValue(), sendingApplication);
		trig.getJobDataMap().put(SynchronizationParametersType.NB_ATTEMPTS.getValue(), nbAttempts);

		try {
			this.getScheduler(nbAttempts).scheduleJob(job, trig);
		} catch (SchedulerException e) {
			logger.error("[addImmediatePropagateCUTrigger] An error has occured during lauch propagateCU job: "
					+ e.getMessage());
		}
	}
}
