package com.arty.busy.date;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDate {
    @SuppressLint("SimpleDateFormat")
    public final static DateFormat timeFormat = new SimpleDateFormat("hh:mm");
    public final static long DAY = 86400000;
    private final static String TAG_PARSE_STRING_TO_TIME = "MyDateError.ParseStringToTime";

    @NonNull
    public static Date getStartDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    @NonNull
    public static Date getStartDay(@NonNull Calendar calendar){
        Calendar cloneCalendar = (Calendar) calendar.clone();

        cloneCalendar.set(Calendar.HOUR_OF_DAY, 0);
        cloneCalendar.set(Calendar.MINUTE, 0);
        cloneCalendar.set(Calendar.SECOND, 0);
        cloneCalendar.set(Calendar.MILLISECOND, 0);

        return cloneCalendar.getTime();
    }

    @NonNull
    public static Date getEndDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    @NonNull
    public static Date getEndDay(@NonNull Calendar calendar){
        Calendar cloneCalendar = (Calendar) calendar.clone();

        cloneCalendar.set(Calendar.HOUR_OF_DAY, 23);
        cloneCalendar.set(Calendar.MINUTE, 59);
        cloneCalendar.set(Calendar.SECOND, 59);
        cloneCalendar.set(Calendar.MILLISECOND, 999);

        return cloneCalendar.getTime();
    }

    public static void setStartDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        date = calendar.getTime();
    }

    public static void setStartDay(@NonNull Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static void setNextDay(@NonNull Calendar calendar, @NonNull Date d){
        calendar.roll(Calendar.DATE, true);
        if (d.compareTo(calendar.getTime())>0){
            calendar.roll(Calendar.MONTH, true);
        }
        if (d.compareTo(calendar.getTime())>0){
            calendar.roll(Calendar.YEAR, true);
        }
    }

    public static void setLastDay(@NonNull Calendar calendar, @NonNull Date d){
        calendar.roll(Calendar.DATE, false);
        if (d.compareTo(calendar.getTime())<0){
            calendar.roll(Calendar.MONTH, false);
        }
        if (d.compareTo(calendar.getTime())<0){
            calendar.roll(Calendar.YEAR, false);
        }
    }

    @NonNull
    public static Calendar getCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    @NonNull
    public static Calendar getCalendar(Date d){
        Calendar calendar = getCalendar();
        calendar.setTime(d);

        return calendar;
    }

    @NonNull
    public static Date getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static void addDay(Date currDate, int countDays){
        currDate = new Date(currDate.getTime() + 86400000*countDays);
    }

    public static Time parseStringToTime(String sTime){
        Time time = new Time();

        String[] arrTime = sTime.split(":");
        if (arrTime.length == 2) {
            try{
                byte hour = Byte.parseByte(arrTime[0]);
                time.setHour(hour);
            } catch (NumberFormatException exception){
                Log.e(TAG_PARSE_STRING_TO_TIME, "failed to parse hour due to", exception);
            }

            try{
                byte minute = Byte.parseByte(arrTime[1]);
                time.setMinute(minute);
            } catch (NumberFormatException exception){
                Log.e(TAG_PARSE_STRING_TO_TIME, "failed to parse minute due to", exception);
            }
        }

        return time;
    }
}
