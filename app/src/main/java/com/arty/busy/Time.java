package com.arty.busy;

public class Time {
    private byte hour;
    private byte minute;

    public byte getHour() {
        return hour;
    }

    public void setHour(byte hour) {
        if(hour < 0){
            this.hour = 0;
        } else if(hour > 23){
            this.hour = 23;
        } else this.hour = hour;
    }

    public byte getMinute() {
        return minute;
    }

    public void setMinute(byte minute) {
        if(minute < 0){
            this.minute = 0;
        } else if(minute > 60){
            this.minute = 60;
        } else this.minute = minute;
    }
}
