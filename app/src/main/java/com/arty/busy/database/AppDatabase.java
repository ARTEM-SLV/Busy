package com.arty.busy.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.arty.busy.models.Customer;
import com.arty.busy.models.LineOfBusiness;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;

@Database(entities = {Task.class, Service.class, Customer.class, LineOfBusiness.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract BusyDao busyDao();

//    public static synchronized AppDatabase getInstance(Context context) {
//        if (instance == null) {
//            instance = Room.databaseBuilder(context.getApplicationContext(),
//                            AppDatabase.class, "busy_db")
//                    .allowMainThreadQueries()
//                    .build();
//        }
//        return instance;
//    }
}
