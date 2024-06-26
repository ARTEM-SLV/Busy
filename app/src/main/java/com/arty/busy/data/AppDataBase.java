package com.arty.busy.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.arty.busy.models.Customer;
import com.arty.busy.models.LineOfBusiness;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;

@Database(entities = {Task.class, Service.class, Customer.class, LineOfBusiness.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract BusyDao busyDao();
}
