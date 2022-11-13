package com.arty.busy.ui.home.items;

public class ItemTasksToDay {
    private long date;
    private String services;
    private String servicesShort;
    private String client;
    private double price;
    private boolean done;
    private boolean paid;

    public ItemTasksToDay() {
        this.date = 0;
        this.services = "";
        this.servicesShort = "";
        this.client = "";
        this.price = 0.0d;
        this.done = false;
        this.paid = false;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getServicesShort() {
        return servicesShort;
    }

    public void setServicesShort(String servicesShort) {
        this.servicesShort = servicesShort;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}