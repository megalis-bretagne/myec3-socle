package org.myec3.socle.ws.server.config;

import org.myec3.socle.config.CoreConfig;
import org.myec3.socle.ws.batch.job.GenerateUpdateList;
import org.myec3.socle.ws.batch.job.UnstackUpdateList;
import org.myec3.socle.ws.config.SocleClientConfig;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@PropertySource({ "classpath:database.properties", "classpath:mpsUpdate.properties" })
@ComponentScan(basePackages = { "org.myec3.socle.ws.server" })
@Import({ CoreConfig.class, SocleClientConfig.class })
@EnableWebMvc
public class ServerCoreConfig {

	@Autowired
	private Environment env;

	@Autowired
	private GenerateUpdateList generateUpdateList;

	@Autowired
	private UnstackUpdateList unstackUpdateList;

	// Begin CRON Configuration for the creation task

	@Bean
	public MethodInvokingJobDetailFactoryBean populateJob() {
		MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
		methodInvokingJobDetailFactoryBean.setTargetObject(generateUpdateList);
		methodInvokingJobDetailFactoryBean.setTargetMethod("populateUpdateResource");
		return methodInvokingJobDetailFactoryBean;
	}

	@Bean
	public CronTrigger populateTrigger() {
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
				.cronSchedule(env.getProperty("mpsUpdate.populateCronTrigger"));
		TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger().forJob(populateJob().getObject())
				.withSchedule(cronScheduleBuilder);
		return triggerBuilder.build();
	}

	@Bean
	public SchedulerFactoryBean populateScheduler() {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setTriggers(populateTrigger());
		scheduler.setJobDetails(populateJob().getObject());
		return scheduler;
	}

	// Begin CRON Configuration for the unstack task

	@Bean
	public MethodInvokingJobDetailFactoryBean unstackJob() {
		MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
		methodInvokingJobDetailFactoryBean.setTargetObject(unstackUpdateList);
		methodInvokingJobDetailFactoryBean.setTargetMethod("unstackUpdateResource");
		return methodInvokingJobDetailFactoryBean;
	}

	@Bean
	public CronTrigger unstackTrigger() {
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
				.cronSchedule(env.getProperty("mpsUpdate.unstackCronTrigger"));
		TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger().forJob(unstackJob().getObject())
				.withSchedule(cronScheduleBuilder);
		return triggerBuilder.build();
	}

	@Bean
	public SchedulerFactoryBean unstackScheduler() {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setTriggers(unstackTrigger());
		scheduler.setJobDetails(unstackJob().getObject());
		return scheduler;
	}
}
