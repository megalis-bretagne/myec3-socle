package org.myec3.socle.synchro.scheduler.config;

import static org.quartz.DateBuilder.futureDate;

import org.myec3.socle.synchro.scheduler.service.impl.DBListenerImpl;
import org.myec3.socle.synchro.scheduler.service.impl.JMSListenerImpl;
import org.myec3.socle.ws.config.SocleClientConfig;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
@PropertySource({ "classpath:socleCore.properties", "classpath:db.properties", "classpath:database.properties",
		"classpath:synchronization.properties", "classpath:quartzParallelScheduler.properties",
		"classpath:quartzScheduler.properties" })
@ComponentScan(basePackages = { "org.myec3.socle.synchro.scheduler" })
@Import({ SocleClientConfig.class })
public class SynchroSchedulerConfig {

	@Value("${refreshConnection.startDelay}")
	private int refreshConnectionStartDelay;

	@Value("${refreshConnection.repeatInterval}")
	private int refreshConnectionRepeatInterval;

	@Value("${retrieveSynchronizationTasks.startDelay}")
	private int retrieveSynchronizationTasksStartDelay;

	@Value("${retrieveSynchronizationTasks.repeatInterval}")
	private int retrieveSynchronizationTasksRepeatInterval;

	@Autowired
	private JMSListenerImpl jmsListener;

	@Autowired
	private DBListenerImpl dbListener;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean resfreshLitenerJobDetail() {
		MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
		methodInvokingJobDetailFactoryBean.setTargetObject(jmsListener);
		methodInvokingJobDetailFactoryBean.setTargetMethod("refreshListener");
		return methodInvokingJobDetailFactoryBean;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean retrieveSynchronizationTasksJobDetail() {
		MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
		methodInvokingJobDetailFactoryBean.setTargetObject(dbListener);
		methodInvokingJobDetailFactoryBean.setTargetMethod("retrieveSynchronizationTasks");
		return methodInvokingJobDetailFactoryBean;
	}

	@Bean
	public SimpleTrigger refreshListenerSimpleTrigger() {
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInMilliseconds(refreshConnectionRepeatInterval).repeatForever();

		TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.forJob(resfreshLitenerJobDetail().getObject())
				.withSchedule(simpleScheduleBuilder)
				.startAt(futureDate(refreshConnectionStartDelay, IntervalUnit.MILLISECOND));
		return triggerBuilder.build();
	}

	@Bean
	public SimpleTrigger retrieveSynchronizationTasksSimpleTrigger() {
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInMilliseconds(retrieveSynchronizationTasksRepeatInterval).repeatForever();

		TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.forJob(retrieveSynchronizationTasksJobDetail().getObject())
				.withSchedule(simpleScheduleBuilder)
				.startAt(futureDate(retrieveSynchronizationTasksStartDelay, IntervalUnit.MILLISECOND));
		return triggerBuilder.build();
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setTriggers(retrieveSynchronizationTasksSimpleTrigger(), refreshListenerSimpleTrigger());
		scheduler.setJobDetails(retrieveSynchronizationTasksJobDetail().getObject(),
				resfreshLitenerJobDetail().getObject());
		return scheduler;
	}

	@Bean
	public SchedulerFactoryBean quartzScheduler() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setJobFactory(new SpringBeanJobFactory());
		schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartzScheduler.properties"));
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
		return schedulerFactoryBean;
	}

	@Bean
	public SchedulerFactoryBean parallelScheduler() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setJobFactory(new SpringBeanJobFactory());
		schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartzParallelScheduler.properties"));
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
		return schedulerFactoryBean;
	}

}
