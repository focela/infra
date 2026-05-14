package com.focela.platform.framework.quartz.core.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for Quartz Cron expressions.
 */
public class CronUtils {

    /**
     * Validate whether a CRON expression is valid.
     *
     * @param cronExpression CRON expression
     * @return whether it is valid
     */
    public static boolean isValid(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * Compute the next n execution times for the given CRON expression.
     *
     * @param cronExpression CRON expression
     * @param n number of times
     * @return matching execution times
     */
    public static List<LocalDateTime> getNextTimes(String cronExpression, int n) {
        // 1. Build the CronExpression object
        CronExpression cron;
        try {
            cron = new CronExpression(cronExpression);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        // 2. Starting from now, compute n matching times
        Date now = new Date();
        List<LocalDateTime> nextTimes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Date nextTime = cron.getNextValidTimeAfter(now);
            // 2.1 If nextTime is null, no more valid times - exit the loop
            if (nextTime == null) {
                break;
            }
            nextTimes.add(LocalDateTimeUtil.of(nextTime));
            // 2.2 Advance "now" to the next trigger time
            now = nextTime;
        }
        return nextTimes;
    }

}
