package com.arty.busy;

import android.app.Application;

import androidx.room.Room;

import com.arty.busy.data.AppDataBase;
import com.arty.busy.data.MainDao;
import com.arty.busy.model.Service;
import com.arty.busy.model.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class App extends Application {
    private AppDataBase dataBase;
    private MainDao mainDao;

    private static App instance;

    public static App getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        dataBase = Room.databaseBuilder(getApplicationContext(),
                AppDataBase.class, "busy_db")
                .allowMainThreadQueries()
                .build();

        mainDao = dataBase.mainDao();
    }

    public AppDataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(AppDataBase dataBase) {
        this.dataBase = dataBase;
    }

    public MainDao getMainDao() {
        return mainDao;
    }

    public void setMainDao(MainDao mainDao) {
        this.mainDao = mainDao;
    }

    private void fillDataBaseForTest(){
        List<Task> taskList = null;
        List<Service> serviceList = null;

        Calendar calendar = new GregorianCalendar();
        for (int i = 0; i < 30; i++) {
            Task task = new Task();

            Date dd = calendar.getTime();
            Date dt = calendar.getTime();
            calendar.roll(Calendar.DATE, true);

            task.uid = i;
            task.date = dd.getTime();
            task.time = dt.getTime();
            task.id_customer = i;
            task.id_service = i;

            taskList.add(task);
        }

        for (int i = 0; i < 5; i++) {
            Service service = new Service();

            service.uid = i;
//            service.description = ;
//            service.time = dt.getTime();
//            service.id_customer = i;
//            service.id_service = i;
        }
    }


}
