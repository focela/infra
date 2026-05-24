package com.focela.platform.common.utils.date;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.DateIntervalEnum;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import static cn.hutool.core.date.DatePattern.*;

/**
 * Time utility class for {@link LocalDateTime}
 */
public class LocalDateTimeUtils {

    /**
     * Empty LocalDateTime, mainly used as the default value for DB unique indexes
     */
    public static final LocalDateTime EMPTY = buildTime(1970, 1, 1);

    public static final DateTimeFormatter UTC_MS_WITH_XXX_OFFSET_FORMATTER = createFormatter(UTC_MS_WITH_XXX_OFFSET_PATTERN);

    /**
     * Parse a time string
     *
     * Compared with {@link LocalDateTimeUtil#parse(CharSequence)}, this tries multiple patterns until one succeeds
     *
     * @param time time string
     * @return parsed time
     */
    public static LocalDateTime parse(String time) {
        try {
            return LocalDateTimeUtil.parse(time, DatePattern.NORM_DATE_PATTERN);
        } catch (DateTimeParseException e) {
            return LocalDateTimeUtil.parse(time);
        }
    }

    public static LocalDateTime addTime(Duration duration) {
        return LocalDateTime.now().plus(duration);
    }

    public static LocalDateTime minusTime(Duration duration) {
        return LocalDateTime.now().minus(duration);
    }

    public static boolean beforeNow(LocalDateTime date) {
        return date.isBefore(LocalDateTime.now());
    }

    public static boolean afterNow(LocalDateTime date) {
        return date.isAfter(LocalDateTime.now());
    }

    /**
     * Build a specific time
     *
     * @param year  year
     * @param month month
     * @param day   day
     * @return the specified time
     */
    public static LocalDateTime buildTime(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0);
    }

    public static LocalDateTime[] buildBetweenTime(int year1, int month1, int day1,
                                                   int year2, int month2, int day2) {
        return new LocalDateTime[]{buildTime(year1, month1, day1), buildTime(year2, month2, day2)};
    }

    /**
     * Determine whether the given time falls within the time range
     *
     * @param startTime start time
     * @param endTime end time
     * @param time given time
     * @return whether it is within the range
     */
    public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime, Timestamp time) {
        if (startTime == null || endTime == null || time == null) {
            return false;
        }
        return LocalDateTimeUtil.isIn(LocalDateTimeUtil.of(time), startTime, endTime);
    }

    /**
     * Determine whether the given time falls within the time range
     *
     * @param startTime start time
     * @param endTime end time
     * @param time given time
     * @return whether it is within the range
     */
    public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime, String time) {
        if (startTime == null || endTime == null || time == null) {
            return false;
        }
        return LocalDateTimeUtil.isIn(parse(time), startTime, endTime);
    }

    /**
     * Determine whether the current time falls within the time range
     *
     * @param startTime start time
     * @param endTime   end time
     * @return whether it is within the range
     */
    public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }
        return LocalDateTimeUtil.isIn(LocalDateTime.now(), startTime, endTime);
    }

    /**
     * Determine whether the current time falls within the time range
     *
     * @param startTime start time
     * @param endTime   end time
     * @return whether it is within the range
     */
    public static boolean isBetween(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }
        LocalDate nowDate = LocalDate.now();
        return LocalDateTimeUtil.isIn(LocalDateTime.now(),
                LocalDateTime.of(nowDate, LocalTime.parse(startTime)),
                LocalDateTime.of(nowDate, LocalTime.parse(endTime)));
    }

    /**
     * Determine whether two time ranges overlap
     *
     * @param startTime1 start of time1
     * @param endTime1   end of time1
     * @param startTime2 start of time2
     * @param endTime2   end of time2
     * @return true if overlapping, false otherwise
     */
    public static boolean isOverlap(LocalTime startTime1, LocalTime endTime1, LocalTime startTime2, LocalTime endTime2) {
        LocalDate nowDate = LocalDate.now();
        return LocalDateTimeUtil.isOverlap(LocalDateTime.of(nowDate, startTime1), LocalDateTime.of(nowDate, endTime1),
                LocalDateTime.of(nowDate, startTime2), LocalDateTime.of(nowDate, endTime2));
    }

    /**
     * Get the start of the month that contains the given date
     * For example: 2023-09-30 00:00:00,000
     *
     * @param date date
     * @return start of the month
     */
    public static LocalDateTime beginOfMonth(LocalDateTime date) {
        return date.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
    }

    /**
     * Get the end of the month that contains the given date
     * For example: 2023-09-30 23:59:59,999
     *
     * @param date date
     * @return end of the month
     */
    public static LocalDateTime endOfMonth(LocalDateTime date) {
        return date.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
    }

    /**
     * Get the quarter of the year for the given date
     *
     * @param date date
     * @return the quarter (1-4)
     */
    public static int getQuarterOfYear(LocalDateTime date) {
        return (date.getMonthValue() - 1) / 3 + 1;
    }

    /**
     * Get the number of days between the given date and now. If the given date is after now, the result is negative.
     *
     * @param dateTime date
     * @return days between
     */
    public static Long between(LocalDateTime dateTime) {
        return LocalDateTimeUtil.between(dateTime, LocalDateTime.now(), ChronoUnit.DAYS);
    }

    /**
     * Get the start time of today
     *
     * @return today
     */
    public static LocalDateTime getToday() {
        return LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
    }

    /**
     * Get the start time of yesterday
     *
     * @return yesterday
     */
    public static LocalDateTime getYesterday() {
        return LocalDateTimeUtil.beginOfDay(LocalDateTime.now().minusDays(1));
    }

    /**
     * Get the start time of this month
     *
     * @return this month
     */
    public static LocalDateTime getMonth() {
        return beginOfMonth(LocalDateTime.now());
    }

    /**
     * Get the start time of this year
     *
     * @return this year
     */
    public static LocalDateTime getYear() {
        return LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
    }

    public static List<LocalDateTime[]> getDateRangeList(LocalDateTime startTime,
                                                         LocalDateTime endTime,
                                                         Integer interval) {
        // 1.1 find the enum
        DateIntervalEnum intervalEnum = DateIntervalEnum.valueOf(interval);
        Assert.notNull(intervalEnum, "interval({}} no matching enum found", interval);
        // 1.2 align times
        startTime = LocalDateTimeUtil.beginOfDay(startTime);
        endTime = LocalDateTimeUtil.endOfDay(endTime);

        // 2. loop and build time ranges
        List<LocalDateTime[]> timeRanges = new ArrayList<>();
        switch (intervalEnum) {
            case HOUR:
                while (startTime.isBefore(endTime)) {
                    timeRanges.add(new LocalDateTime[]{startTime, startTime.plusHours(1).minusNanos(1)});
                    startTime = startTime.plusHours(1);
                }
            case DAY:
                while (startTime.isBefore(endTime)) {
                    timeRanges.add(new LocalDateTime[]{startTime, startTime.plusDays(1).minusNanos(1)});
                    startTime = startTime.plusDays(1);
                }
                break;
            case WEEK:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfWeek = startTime.with(DayOfWeek.SUNDAY).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfWeek});
                    startTime = endOfWeek.plusNanos(1);
                }
                break;
            case MONTH:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfMonth = startTime.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfMonth});
                    startTime = endOfMonth.plusNanos(1);
                }
                break;
            case QUARTER:
                while (startTime.isBefore(endTime)) {
                    int quarterOfYear = getQuarterOfYear(startTime);
                    LocalDateTime quarterEnd = quarterOfYear == 4
                            ? startTime.with(TemporalAdjusters.lastDayOfYear()).plusDays(1).minusNanos(1)
                            : startTime.withMonth(quarterOfYear * 3 + 1).withDayOfMonth(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, quarterEnd});
                    startTime = quarterEnd.plusNanos(1);
                }
                break;
            case YEAR:
                while (startTime.isBefore(endTime)) {
                    LocalDateTime endOfYear = startTime.with(TemporalAdjusters.lastDayOfYear()).plusDays(1).minusNanos(1);
                    timeRanges.add(new LocalDateTime[]{startTime, endOfYear});
                    startTime = endOfYear.plusNanos(1);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid interval: " + interval);
        }
        // 3. fallback: keep the last entry capped at endTime
        LocalDateTime[] lastTimeRange = CollUtil.getLast(timeRanges);
        if (lastTimeRange != null) {
            lastTimeRange[1] = endTime;
        }
        return timeRanges;
    }

    /**
     * Format a date range
     *
     * @param startTime start time
     * @param endTime   end time
     * @param interval  time interval
     * @return formatted time range
     */
    public static String formatDateRange(LocalDateTime startTime, LocalDateTime endTime, Integer interval) {
        // 1. find the enum
        DateIntervalEnum intervalEnum = DateIntervalEnum.valueOf(interval);
        Assert.notNull(intervalEnum, "interval({}} no matching enum found", interval);

        // 2. loop and build the time range
        switch (intervalEnum) {
            case HOUR:
                return LocalDateTimeUtil.format(startTime, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
            case DAY:
                return LocalDateTimeUtil.format(startTime, DatePattern.NORM_DATE_PATTERN);
            case WEEK:
                return LocalDateTimeUtil.format(startTime, DatePattern.NORM_DATE_PATTERN)
                        + StrUtil.format("(Week {})", LocalDateTimeUtil.weekOfYear(startTime));
            case MONTH:
                return LocalDateTimeUtil.format(startTime, DatePattern.NORM_MONTH_PATTERN);
            case QUARTER:
                return StrUtil.format("{}-Q{}", startTime.getYear(), getQuarterOfYear(startTime));
            case YEAR:
                return LocalDateTimeUtil.format(startTime, DatePattern.NORM_YEAR_PATTERN);
            default:
                throw new IllegalArgumentException("Invalid interval: " + interval);
        }
    }

    /**
     * Get the first day of the quarter that contains the given date
     *
     * @param date date
     * @return first day of the quarter
     */
    public static LocalDate getQuarterStart(LocalDate date) {
        Month firstMonthOfQuarter = date.getMonth().firstMonthOfQuarter();
        return LocalDate.of(date.getYear(), firstMonthOfQuarter, 1);
    }

    /**
     * Get the first day of the week (Monday) that contains the given date
     *
     * @param date date
     * @return Monday of the week
     */
    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    /**
     * Convert the given {@link LocalDateTime} to seconds since the Unix epoch (1970-01-01T00:00:00Z).
     *
     * @param sourceDateTime local date time to convert, must not be null
     * @return seconds since 1970-01-01T00:00:00Z (epoch second)
     * @throws NullPointerException if {@code sourceDateTime} is {@code null}
     * @throws DateTimeException if the conversion results in an out-of-range value or other date-time error
     */
    public static Long toEpochSecond(LocalDateTime sourceDateTime) {
        return TemporalAccessorUtil.toInstant(sourceDateTime).getEpochSecond();
    }

}
