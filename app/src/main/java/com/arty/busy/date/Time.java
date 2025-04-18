package com.arty.busy.date;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.arty.busy.models.Customer;

import java.text.DecimalFormat;

public class Time {
    private byte hour;
    private byte minute;

    private final static String TAG_PARSE_STRING_TO_TIME = "Time.ParseStringToTime";

    public Time() {
        this.hour = 0;
        this.minute = 0;
    }

    public Time(Time time) {
        this.hour = time.hour;
        this.minute = time.minute;
    }

    public Time(int minutes){
        this.hour = 0;
        this.minute = 0;

        this.addTime(minutes);
    }

    public Time(byte hour, byte minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time(String timeS) {
        String[] arrTime = timeS.split(":");
        if (arrTime.length == 2) {
            try{
                this.hour = Byte.parseByte(arrTime[0]);
            } catch (NumberFormatException exception){
                Log.e(TAG_PARSE_STRING_TO_TIME, "failed to parse hour due to", exception);
            }

            try{
                this.minute = Byte.parseByte(arrTime[1]);
            } catch (NumberFormatException exception){
                Log.e(TAG_PARSE_STRING_TO_TIME, "failed to parse minute due to", exception);
            }
        }
    }

    public byte getHour() {
        return this.hour;
    }

    public String getHourS() {
        return new DecimalFormat( "00" ).format(this.hour);
    }

    public void setHour(byte hour) {
        if(hour < 0){
            this.hour = 0;
        } else if(hour > 23){
            this.hour = 23;
        } else
            this.hour = hour;
    }

    public void setTime(byte hour, byte minute){
        this.hour = hour;
        this.minute = minute;
    }

    public String getTime(){
        return getHourS() + ":" + getMinuteS();
    }

    public byte getMinute() {
        return this.minute;
    }

    public String getMinuteS() {
        return new DecimalFormat( "00" ).format(this.minute);
    }

    public void setMinute(byte minute) {
        if(minute < 0){
            this.minute = 0;
        } else if(minute > 59){
            this.minute = 59;
        } else
            this.minute = minute;
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    public String toString(){
        return String.format("%02d", this.hour) + ":" + String.format("%02d", this.minute);
    }

    public int toInt(){
        return hour * 60 + minute;
    }

    public int compareTo(Time t){
        int res = 0;

        if (this.hour > t.getHour()){
            res = 1;
        } else if (this.hour < t.getHour()){
            res = -1;
        } else{
            if (this.minute > t.getMinute()){
                res = 1;
            } else if (this.minute < t.getMinute()){
                res = -1;
            }
        }

        return res;
    }

    public void addHours(byte hours){
        byte res = (byte) (hours + this.hour);
        if (res >= 24){
            res -= 24;
        }

        this.hour = res;
    }

    public void addMinutes(byte minutes){
        byte res = (byte) (minutes + this.minute);
        if (res >= 60){
            this.addTime(res);
            res -= 60;
        }

        this.minute = res;
    }

    public void addTime(Time addedDate){
        byte hour = addedDate.getHour();
        byte minute = addedDate.getMinute();
        int countMinutes = minute + (hour * 60);

        addTime(countMinutes);
    }

    public void addTime(int count){
        byte hours = (byte) (count / 60);
        this.addHours(hours);

        byte minutes = (byte) (count - hours*60);
        if (minutes<0 & count>0)
            minutes = 0;
        this.addMinutes(minutes);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time that = (Time) o;
        return hour == that.hour && minute == that.minute;
    }
}
