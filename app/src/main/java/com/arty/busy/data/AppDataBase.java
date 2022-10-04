package com.arty.busy.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.arty.busy.model.Customer;
import com.arty.busy.model.LineOfBusiness;
import com.arty.busy.model.Service;
import com.arty.busy.model.Task;
import com.arty.busy.model.Workday;

@Database(entities = {Task.class, Service.class, Customer.class, LineOfBusiness.class, Workday.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract MainDao mainDao();
}
