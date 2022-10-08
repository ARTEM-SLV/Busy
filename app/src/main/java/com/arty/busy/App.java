package com.arty.busy;

import android.app.Application;

import androidx.room.Room;

import com.arty.busy.data.AppDataBase;
import com.arty.busy.data.MainDao;
import com.arty.busy.model.Service;
import com.arty.busy.model.Task;

import java.util.ArrayList;
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

        fillDataBaseForTest();
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
        List<Task> taskList = new ArrayList<>();
        List<Service> serviceList = new ArrayList<>();

        Calendar calendar = new GregorianCalendar();
        int uid = 0;
        for (int i = 0; i < 30; i++) {
            Date dt = calendar.getTime();
            Date dd = MyDate.getStartDay(calendar);

            int randomJ = (int) Math.round(Math.random()*11)+1;

            for (int j = 0; j < randomJ; j++) {
                int randomNum = (int) Math.round(Math.random()*4)+1;

                Task task = new Task();
                task.uid = uid;
                task.day = dd.getTime();
                task.date_time = dt.getTime();
                task.id_customer = randomNum;
                task.id_service = randomNum;

                taskList.add(task);
                uid++;
            }

            calendar.roll(Calendar.DATE, true);
        }
        App.getInstance().getMainDao().insertTaskList(taskList);

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

        App.getInstance().getMainDao().insertServiceList(serviceList);
    }


}
