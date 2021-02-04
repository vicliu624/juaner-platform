package indi.vicliu.juaner.upms.config;

import indi.vicliu.juaner.common.scheduling.annotation.SchedulingConfigurer;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * @ClassName ScheduleConfig
 * @Description TODO
 * @Author Song XingPing
 * @Date 2019/8/3 14:15
 * @Version 1.0
 */

@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        int count = taskRegistrar.getCronTaskList().size()
                + taskRegistrar.getFixedDelayTaskList().size()
                + taskRegistrar.getFixedRateTaskList().size()
                + taskRegistrar.getTriggerTaskList().size();
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(count));
    }
}
