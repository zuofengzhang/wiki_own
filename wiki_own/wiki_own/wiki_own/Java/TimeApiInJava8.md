---
title: "Java8 Time API"
layout: post
date: 2016-04-12 11:28:00
category: Java
tags:
 - Java
 - Throwable

share: true
comments: true
---


```java
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class TimeApiInJava8 {
    public void dateToLocalDate() {
        final Date date = new Date();
        final Instant instant = date.toInstant();
        System.out.println(instant);
        final ZoneId defaultZoneId = ZoneId.systemDefault();
        ZonedDateTime atZone = instant.atZone(defaultZoneId);
        final LocalDate localDate = atZone.toLocalDate();
        System.out.println(localDate);
        LocalDateTime localDateTime = atZone.toLocalDateTime();
        System.out.println(localDateTime);
    }

    public void dateToLocalDateTime() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId systemDefault = ZoneId.systemDefault();
        LocalDateTime.ofInstant(instant, systemDefault);
    }

    public LocalDateTime longToLocalDateTime(Long time) {
        ZoneId systemDefault = ZoneId.systemDefault();
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime localDateTime = instant.atZone(systemDefault).toLocalDateTime();
        return localDateTime;
    }

    public void localDateNow() {
        LocalDate now = LocalDate.now();
        System.out.println("localDateNow: " + now);
        System.out.println("year: " + now.getYear());
        System.out.println("month: " + now.getMonthValue());
        System.out.println("day: " + now.getDayOfMonth());
    }

    public void localDateDiff() {
        LocalDate nowLocalDate = LocalDate.of(2019, 12, 15);
        LocalDate birthLocalDate = LocalDate.of(1986, 2, 14);
        long l = nowLocalDate.toEpochDay() - birthLocalDate.toEpochDay();
        System.out.println("date diff: " + l);
    }

    public void firstLastDay() {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfNextYear = now.with(TemporalAdjusters.firstDayOfNextYear());
        LocalDate lastDayOfYear = now.with(TemporalAdjusters.lastDayOfYear());
        LocalDate firstDayOfYear = now.with(TemporalAdjusters.firstDayOfYear());
        System.out.println("firstDayOfMonth: " + firstDayOfMonth);
        System.out.println("lastDayOfMonth: " + lastDayOfMonth);
        System.out.println("firstDayOfNextYear: " + firstDayOfNextYear);
        System.out.println("lastDayOfYear: " + lastDayOfYear);
        System.out.println("firstDayOfYear: " + firstDayOfYear);
    }

    public void getDayOfMonth() {
        LocalDate localDate = LocalDate.of(2019, 2, 14);
        LocalDate lastDay = localDate.with(TemporalAdjusters.lastDayOfMonth());
        int dayOfMonth = lastDay.getDayOfMonth();
        System.out.println("day of month: " + dayOfMonth);
    }

    public void localDateCompareTo() {
        LocalDate date1 = LocalDate.of(2018, 9, 20);
        LocalDate date2 = LocalDate.of(2018, 9, 21);
        System.out.println(date1 + ".compareTo(" + date2 + "): " + date1.compareTo(date2));
        System.out.println(date1 + ".compareTo(" + date1 + "): " + date1.compareTo(date1));
        System.out.println(date2 + ".compareTo(" + date1 + "): " + date2.compareTo(date1));

    }

    public void nextMonth() {

        LocalDate localDate = LocalDate.of(2018, 2, 12);

        // 下一周的该星期
        LocalDate nextWeeks1 = localDate.minusWeeks(-1);
        System.out.println(nextWeeks1);
        // 2018-02-19
        LocalDate nextWeeks2 = localDate.plusWeeks(1);
        System.out.println(nextWeeks2);

        // 获取下个月的这天
        LocalDate nextMonth1 = localDate.minusMonths(-1);
        System.out.println(nextMonth1);
        // 2018-03-12
        LocalDate nextMonth2 = localDate.plusMonths(1);
        System.out.println(nextMonth2);

        // 下个月的1号
        LocalDate localDate3 = LocalDate.of(localDate.getYear(), localDate.getMonthValue() + 1, 1);
        System.out.println(localDate3);
        // 2018-03-01
    }

    public void localDatePeriod() {
        LocalDate date1 = LocalDate.of(2018, 10, 9);
        LocalDate date2 = LocalDate.of(2019, 4, 1);
        Period period = Period.between(date1, date2);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        System.out.println("years:" + years + ", months:" + months + ", days:" + days);
        // years:0, months:5, days:23
    }

    public void ofEpochSecond() {
        Instant now = Instant.now();
        System.out.println(now);
        // 2019-03-13T06:41:32.865Z

        // 去除毫秒
        long l = now.toEpochMilli() / 1000;
        // 通过秒构建Instant对象
        Instant instant = Instant.ofEpochSecond(l);
        System.out.println(instant);
        // 2019-03-13T06:41:32Z
    }

    public void stringToLocalDate() {
        final String string = "2018-12-07";
        LocalDate parse = LocalDate.parse(string);
        System.out.println(parse.toString());
        // 结果是2018-12-07

    }

    public void localDateTimeToZonedDateTime() {
        final String string = "2018-12-07T09:33:38";
        LocalDateTime parse = LocalDateTime.parse(string);
        ZonedDateTime z1 = ZonedDateTime.of(parse, ZoneId.of("Asia/Shanghai"));
        System.out.println(z1.toString());
        // 2018-12-07T09:33:38+08:00[Asia/Shanghai]

        ZonedDateTime z2 = ZonedDateTime.of(parse, ZoneId.of("Z"));
        System.out.println(z2.toString());
        // 2018-12-07T09:33:38Z

        ZonedDateTime z3 = ZonedDateTime.of(parse, ZoneId.of("UTC"));
        System.out.println(z3.toString());
        // 2018-12-07T09:33:38Z[UTC]

        ZonedDateTime z4 = ZonedDateTime.of(parse, ZoneId.of("UTC+08:00"));
        System.out.println(z4.toString());
        // 2018-12-07T09:33:38+08:00[UTC+08:00]

        ZonedDateTime z5 = ZonedDateTime.of(parse, ZoneId.of("+08:00"));
        System.out.println(z5.toString());
        // 2018-12-07T09:33:38+08:00

        ZonedDateTime z6 = ZonedDateTime.of(parse, ZoneId.of("+00:00"));
        System.out.println(z6.toString());
        // 2018-12-07T09:33:38Z

    }

    public static void main(final String[] args) {
        TimeApiInJava8 timeApi = new TimeApiInJava8();
        timeApi.dateToLocalDate();
        timeApi.dateToLocalDateTime();
        System.out.println("long to localDateTime: " + timeApi.longToLocalDateTime(System.currentTimeMillis()));
        timeApi.localDateNow();
        timeApi.localDateDiff();
        timeApi.firstLastDay();
        timeApi.getDayOfMonth();
        timeApi.localDateCompareTo();
        timeApi.nextMonth();
        timeApi.localDatePeriod();
        timeApi.ofEpochSecond();
        timeApi.stringToLocalDate();
        timeApi.localDateTimeToZonedDateTime();
    }
}
```
