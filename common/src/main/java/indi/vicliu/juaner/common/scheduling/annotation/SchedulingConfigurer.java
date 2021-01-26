package indi.vicliu.juaner.common.scheduling.annotation;

import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@FunctionalInterface
public interface SchedulingConfigurer {

    /**
     * Callback allowing a {@link org.springframework.scheduling.TaskScheduler
     * TaskScheduler} and specific {@link org.springframework.scheduling.config.Task Task}
     * instances to be registered against the given the {@link ScheduledTaskRegistrar}.
     * @param taskRegistrar the registrar to be configured.
     */
    void configureTasks(ScheduledTaskRegistrar taskRegistrar);

}
