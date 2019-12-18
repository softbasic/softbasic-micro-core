package com.github.softbasic.micro.config.scheduling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableScheduling
@Configuration
@ConditionalOnProperty(prefix="spring.task.scheduling",name = "enable", havingValue = "true")
public class ScheduledConfig implements SchedulingConfigurer {

    @Value("${spring.task.scheduling.pool.size}")
    private int corePoolSize;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(setTaskExecutors());
    }

    @Bean(destroyMethod="shutdown")
    Executor setTaskExecutors(){
        return Executors.newScheduledThreadPool(corePoolSize);
    }
}
