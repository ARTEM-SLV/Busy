package com.arty.busy.ui.home.items;

public class ItemTaskByHours {
    private int id_task;
    private String currentTime;
    private String taskTime;
    private String services;
    private String client;
    private int hour;
    private int minutes;
    private int duration;
    private boolean isTask;

    public ItemTaskByHours() {
        this.taskTime = "";
        this.services = "";
        this.client = "";
        this.hour = 0;
        this.minutes = 0;
        this.duration = 0;
        this.isTask = false;
    }

    public int getId_task() {
        return id_task;
    }

    public void setId_task(int id_task) {
        if (id_task < 0){
            this.id_task = 0;
        } else
            this.id_task = id_task;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        if(hour < 0)
            this.hour = 0;
        else
            this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if(minutes < 0)
            this.minutes = 0;
        else
            this.minutes = minutes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if(duration < 0)
            this.duration = 0;
        else
            this.duration = duration;
    }

    public boolean isTask() {
        return isTask;
    }

    public void setTask(boolean task) {
        isTask = task;
    }
}
