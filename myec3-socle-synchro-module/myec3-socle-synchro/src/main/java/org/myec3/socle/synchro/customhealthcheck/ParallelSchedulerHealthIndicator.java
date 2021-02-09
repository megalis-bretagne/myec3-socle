package org.myec3.socle.synchro.customhealthcheck;

import org.myec3.socle.core.util.SpringApplicationContext;
import org.myec3.socle.synchro.scheduler.service.SchedulerService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ParallelSchedulerHealthIndicator implements HealthIndicator {

    @Autowired
    private ApplicationContext appContext;

    @Override
    public Health health() {
        try {
            SchedulerService parallelSchedulerService = (SchedulerService) appContext.getBean("parallelSchedulerService");
            if ( parallelSchedulerService.getScheduler().isShutdown()){
                return Health.down().withDetail("parallel-Scheduler", "Not Available").build();
            }else{
                return Health.up().withDetail("parallel-Scheduler", "Available").build();
            }
        } catch (SchedulerException e) {
            return Health.down().withDetail("parallel-Scheduler", "Not Available"+e.getMessage() ).build();
        }
    }
}