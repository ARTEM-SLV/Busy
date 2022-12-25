package com.arty.busy.date;

import java.text.DecimalFormat;

public class Time {
    private byte hour;
    private byte minute;

    public Time() {
        this.hour = 0;
        this.minute = 0;
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

    public String getTimeS(){
        return this.getHourS() + ":" + getMinuteS();
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

    public void addTime(int count){
        byte hours = (byte) (count / 60);
        this.addHours(hours);

        byte minutes = (byte) (count - hours*60);
        if (minutes<0 & count>0)
            minutes = 0;
        this.addMinutes(minutes);
    }
}
