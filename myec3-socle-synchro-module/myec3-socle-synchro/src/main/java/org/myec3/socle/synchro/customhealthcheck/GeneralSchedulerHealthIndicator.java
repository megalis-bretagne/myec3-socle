package org.myec3.socle.synchro.customhealthcheck;

import org.myec3.socle.synchro.scheduler.service.SchedulerService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GeneralSchedulerHealthIndicator implements HealthIndicator {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private Environment env;

    @Override
    public Health health() {
        try {
            SchedulerService simpleSchedulerService = (SchedulerService) appContext.getBean("simpleSchedulerService");
            if ( simpleSchedulerService.getScheduler().isShutdown()){
                return Health.down().withDetail("general-Scheduler", "Not Available").build();
            }else{
                return Health.up().withDetail("general-Scheduler", "Available").build();
            }
        } catch (SchedulerException e) {
            return Health.down().withDetail("general-Scheduler", "Not Available"+e.getMessage() ).build();
        }
    }
}