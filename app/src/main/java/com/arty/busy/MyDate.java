package com.arty.busy;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyDate {
    public static Date getStartDay(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getStartDay(Calendar calendar){
        Calendar cloneCalendar = (Calendar) calendar.clone();

        cloneCalendar.set(Calendar.HOUR, 0);
        cloneCalendar.set(Calendar.MINUTE, 0);
        cloneCalendar.set(Calendar.SECOND, 0);
        cloneCalendar.set(Calendar.MILLISECOND, 0);

        return cloneCalendar.getTime();
    }

    public static Date getEndDay(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public static Date getEndDay(Calendar calendar){
        Calendar cloneCalendar = (Calendar) calendar.clone();

        cloneCalendar.set(Calendar.HOUR, 23);
        cloneCalendar.set(Calendar.MINUTE, 59);
        cloneCalendar.set(Calendar.SECOND, 59);
        cloneCalendar.set(Calendar.MILLISECOND, 999);

        return cloneCalendar.getTime();
    }

    public static void increaseByDay(Calendar calendar, Date d){
        calendar.roll(Calendar.DATE, true);
        if (d.compareTo(calendar.getTime())>0){
            calendar.roll(Calendar.MONTH, true);
        }
        if (d.compareTo(calendar.getTime())>0){
            calendar.roll(Calendar.YEAR, true);
        }
    }

    public static void decreaseByDay(Calendar calendar, Date d){
        calendar.roll(Calendar.DATE, false);
        if (d.compareTo(calendar.getTime())<0){
            calendar.roll(Calendar.MONTH, false);
        }
        if (d.compareTo(calendar.getTime())<0){
            calendar.roll(Calendar.YEAR, false);
        }
    }
}
