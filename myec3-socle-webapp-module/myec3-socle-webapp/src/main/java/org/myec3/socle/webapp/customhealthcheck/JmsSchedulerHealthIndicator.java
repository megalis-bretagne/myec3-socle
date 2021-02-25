package org.myec3.socle.webapp.customhealthcheck;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

//@Component
public class JmsSchedulerHealthIndicator implements HealthIndicator {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private Environment env;

    @Override
    public Health health() {
        try {

            SchedulerFactoryBean jmsScheduler = (SchedulerFactoryBean) appContext.getBean("jmsScheduler");
            if ( jmsScheduler.getScheduler().isShutdown()){
                return Health.down().withDetail("jms-Scheduler", "Not Available").build();
            }else{
                return Health.up().withDetail("jms-Scheduler", "Available").build();
            }
        } catch (SchedulerException e) {
            return Health.down().withDetail("jms-Scheduler", "Not Available"+e.getMessage() ).build();
        }
    }
}
