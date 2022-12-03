package com.arty.busy.ui.home.items;

import java.io.Serializable;

public class ItemTaskInfo implements Serializable {
    private int id_task;
    private String time;
    private String services;
    private String client;
//    private double price;
    private int duration;
    private boolean done;
//    private boolean paid;

    public ItemTaskInfo() {
        this.id_task = 0;
        this.time = "00:00";
        this.services = "";
        this.client = "";
//        this.price = 0.0d;
//        this.duration = 0;
        this.done = false;
//        this.paid = false;
    }

    public int getId_task() {
        return id_task;
    }

    public void setId_task(int id_task) {
        if (id_task < 0){
            id_task = 0;
        }

        this.id_task = id_task;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        if (price < 0)
//            this.price = 0;
//        else
//            this.price = price;
//    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if(duration < 0)
            this.duration = 0;
        else
            this.duration = duration;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

//    public boolean isPaid() {
//        return paid;
//    }
//
//    public void setPaid(boolean paid) {
//        this.paid = paid;
//    }
}
