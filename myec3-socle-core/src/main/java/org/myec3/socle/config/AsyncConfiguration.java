package org.myec3.socle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        //nb de threads simultanés avant de mettre une tâche en file d'attente
        executor.setCorePoolSize(10);

        //taille de la file d'attente
        //si une tâche arrive et que le nb de threads > corPoolSize, la tâche est mise dans la queue
        executor.setQueueCapacity(100);

        //lorsque la file d'attente est pleine, création d'un nouveau thread tant que l'on ne dépasse pas le max
        executor.setMaxPoolSize(100);

        executor.setThreadNamePrefix("worker-exec-");

        //c'est le thread appelant qui exécutera la tâche si on a atteint le maxPool
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;

    }
}
