package com.arty.busy.date;

import android.util.Log;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTime {
    public final static DateFormat timeFormat24 = new SimpleDateFormat("HH:mm",  Locale.getDefault());
    public final static DateFormat timeFormat = new SimpleDateFormat("h:mm a",  Locale.getDefault());
    public final static long DAY = 86400000;
    private final static String TAG_PARSE_STRING_TO_TIME = "MyDateError.ParseStringToTime";
    public static Date getDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    @NonNull
    public static Date getStartDay(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

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


    public static Calendar getStartDay(long dateMillis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMillis);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    @NonNull
    public static Date getEndDay(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
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

    public static int getDayOfWeek(Date d){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        return  calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static boolean itsSunday(Date d){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    @NonNull
    public static Date getCurrentStartDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getCurrentTime(){
        return new Date();
    }

    public static void addDay(Date currDate, int countDays){
        currDate = new Date(currDate.getTime() + 86400000*countDays);
    }

//    public static Time parseIntToTime(){
//
//    }

    public static Time parseStringToTime(String timeS){
        Time time = new Time();

        String[] arrTime = timeS.split(":");
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

    public static String parseTimeToString(Time t){
        String sHour = t.getHour() < 10 ? "0"+t.getHour() : String.valueOf(t.getHour());
        String sMinute = t.getMinute() < 10 ? "0"+t.getMinute() : String.valueOf(t.getMinute());

        return sHour + ":" + sMinute;
    }

    public static Time getTime(Date d){
        String sTime = timeFormat24.format(d);

        return parseStringToTime(sTime);
    }
}
