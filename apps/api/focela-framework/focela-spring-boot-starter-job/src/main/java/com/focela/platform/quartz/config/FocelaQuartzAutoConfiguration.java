package com.focela.platform.quartz.config;

import com.focela.platform.quartz.core.scheduler.SchedulerManager;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Optional;

/**
 * Scheduled task configuration.
 */
@AutoConfiguration
@EnableScheduling // Enable Spring's built-in scheduling
@Slf4j
public class FocelaQuartzAutoConfiguration {

    @Bean
    public SchedulerManager schedulerManager(Optional<Scheduler> scheduler) {
        if (!scheduler.isPresent()) {
            log.info("[scheduled task - disabled][see https://platform.focela.com/job/ to enable]");
            return new SchedulerManager(null);
        }
        return new SchedulerManager(scheduler.get());
    }

}
