package com.arty.busy;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.arty.busy.data.AppDataBase;
import com.arty.busy.data.BusyDao;
import com.arty.busy.date.MyDate;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class App extends Application {
    private AppDataBase dataBase;
    private BusyDao busyDao;
    private SharedPreferences sharedPreferences;

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

        busyDao = dataBase.busyDao();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.this);
//        fillWorkday();    //for test
        updateSettings();

//        fillDataBaseForTest();    //for test
    }

    public AppDataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(AppDataBase dataBase) {
        this.dataBase = dataBase;
    }

    public BusyDao getBusyDao() {
        return busyDao;
    }

    public void setMainDao(BusyDao busyDao) {
        this.busyDao = busyDao;
    }

    public void updateSettings(){
        Settings.TIME_BEGINNING = sharedPreferences.getInt(Constants.KEY_TIME_BEGINNING, -1);
        Settings.TIME_ENDING = sharedPreferences.getInt(Constants.KEY_TIME_ENDING, -1);
        Settings.TIME_BEGINNING_BREAK = sharedPreferences.getInt(Constants.KEY_TIME_BEGINNING_BREAK, -1);
        Settings.TIME_ENDING_BREAK = sharedPreferences.getInt(Constants.KEY_TIME_ENDING_BREAK, -1);
    }

    public void saveSettings(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Constants.KEY_TIME_BEGINNING, Settings.TIME_BEGINNING);
        editor.putInt(Constants.KEY_TIME_ENDING, Settings.TIME_ENDING);
        editor.putInt(Constants.KEY_TIME_BEGINNING_BREAK, Settings.TIME_BEGINNING_BREAK);
        editor.putInt(Constants.KEY_TIME_ENDING_BREAK, Settings.TIME_ENDING_BREAK);

        editor.commit();
    }

    private void fillDataBaseForTest(){
        List<Task> taskList = new ArrayList<>();
        List<Service> serviceList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int uid = 0;
        for (int i = 0; i < 3000; i++) {
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            Date dt = calendar.getTime();
            Date dd = MyDate.getStartDay(calendar);

            int randomJ = (int) Math.round(Math.random()*10)+1;
            for (int j = 0; j < randomJ; j++) {
                int randomNum = (int) Math.round(Math.random()*4)+1;
                int randomHour = (int) Math.round(Math.random()*3);

                Task task = new Task();
                task.uid = uid;
                task.day = dd.getTime();
                task.time = MyDate.timeFormat.format(dt);
                task.id_customer = randomNum;
                task.id_service = randomNum;

                calendar.add(Calendar.HOUR_OF_DAY, randomHour);
                dt = calendar.getTime();

                taskList.add(task);
                uid++;
            }

            calendar.add(Calendar.DATE, 1);
        }
        App.getInstance().getBusyDao().insertTaskList(taskList);

        Service service1 = new Service();
        service1.uid = 1;
        service1.title = "Ресницы классика";
        service1.short_title = "РК";
        service1.description = "Ресницы классика";
        service1.price = 500.00;
        service1.execution_time = 60;
        service1.preparation_time = 15;
        service1.id_line_of_business = 1;
        serviceList.add(service1);

        Service service2 = new Service();
        service2.uid = 2;
        service2.title = "Ресницы 2D";
        service2.short_title = "Р2D";
        service2.description = "Ресницы 2D";
        service2.price = 500.00;
        service2.execution_time = 60;
        service2.preparation_time = 15;
        service2.id_line_of_business = 1;
        serviceList.add(service2);

        Service service3 = new Service();
        service3.uid = 3;
        service3.title = "Ресницы 3D";
        service3.short_title = "Р3D";
        service3.description = "Ресницы классика";
        service3.price = 500.00;
        service3.execution_time = 60;
        service3.preparation_time = 15;
        service3.id_line_of_business = 1;
        serviceList.add(service3);

        Service service4 = new Service();
        service4.uid = 4;
        service4.title = "Ресницы 5D";
        service4.short_title = "Р5D";
        service4.description = "Ресницы 5D";
        service4.price = 500.00;
        service4.execution_time = 60;
        service4.preparation_time = 15;
        service4.id_line_of_business = 1;
        serviceList.add(service4);

        Service service5 = new Service();
        service5.uid = 5;
        service5.title = "Массаж спины";
        service5.short_title = "МП";
        service5.description = "Массаж спины";
        service5.price = 500.00;
        service5.execution_time = 60;
        service5.preparation_time = 15;
        service5.id_line_of_business = 2;
        serviceList.add(service5);

        App.getInstance().getBusyDao().insertServiceList(serviceList);
    }

    private void fillWorkday(){
        Settings.TIME_BEGINNING = 8; // 8:00
        Settings.TIME_ENDING = 19; // 20:00

        saveSettings();
    }
}
