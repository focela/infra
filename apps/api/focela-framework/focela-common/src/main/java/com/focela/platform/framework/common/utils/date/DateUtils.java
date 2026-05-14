package com.focela.platform.framework.common.utils.date;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Date/time utilities.
 */
public class DateUtils {

    /**
     * Default time zone.
     */
    public static final String TIME_ZONE_DEFAULT = "GMT+8";

    /**
     * Milliseconds per second.
     */
    public static final long SECOND_MILLIS = 1000;

    public static final String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";

    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";

    /**
     * Convert LocalDateTime to Date.
     *
     * @param date LocalDateTime
     * @return Date
     */
    public static Date of(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        // Combine the date-time with the system time zone to create a ZonedDateTime.
        ZonedDateTime zonedDateTime = date.atZone(ZoneId.systemDefault());
        // Convert local date-time to an Instant timestamp.
        Instant instant = zonedDateTime.toInstant();
        // Convert UTC to the system default time zone.
        return Date.from(instant);
    }

    /**
     * Convert Date to LocalDateTime.
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime of(Date date) {
        if (date == null) {
            return null;
        }
        // Convert to timestamp.
        Instant instant = date.toInstant();
        // Convert UTC to the system default time zone.
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Date addTime(Duration duration) {
        return new Date(System.currentTimeMillis() + duration.toMillis());
    }

    public static boolean isExpired(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(time);
    }

    /**
     * Build a Date with the given year/month/day.
     *
     * @param year  year
     * @param month month
     * @param day   day
     * @return the date
     */
    public static Date buildTime(int year, int month, int day) {
        return buildTime(year, month, day, 0, 0, 0);
    }

    /**
     * Build a Date with the given year/month/day/hour/minute/second.
     *
     * @param year   year
     * @param month  month
     * @param day    day
     * @param hour   hour
     * @param minute minute
     * @param second second
     * @return the date
     */
    public static Date buildTime(int year, int month, int day,
                                 int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0); // Typically 0 ms.
        return calendar.getTime();
    }

    public static Date max(Date a, Date b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.compareTo(b) > 0 ? a : b;
    }

    public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.isAfter(b) ? a : b;
    }

    /**
     * Whether the given date is today.
     *
     * @param date date
     * @return whether it is today
     */
    public static boolean isToday(LocalDateTime date) {
        return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now());
    }

    /**
     * Whether the given date is yesterday.
     *
     * @param date date
     * @return whether it is yesterday
     */
    public static boolean isYesterday(LocalDateTime date) {
        return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now().minusDays(1));
    }

}
