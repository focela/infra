package com.focela.platform.framework.quartz.config;

import com.focela.platform.framework.quartz.core.scheduler.SchedulerManager;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Optional;

/**
 * 定时任务 Configuration
 */
@AutoConfiguration
@EnableScheduling // 开启 Spring 自带的定时任务
@Slf4j
public class FocelaQuartzAutoConfiguration {

    @Bean
    public SchedulerManager schedulerManager(Optional<Scheduler> scheduler) {
        if (!scheduler.isPresent()) {
            log.info("[定when 任务 - is disabled][see https://www.example.com/job/ open]");
            return new SchedulerManager(null);
        }
        return new SchedulerManager(scheduler.get());
    }

}
