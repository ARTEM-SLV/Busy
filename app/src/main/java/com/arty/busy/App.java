package com.arty.busy;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.arty.busy.data.AppDataBase;
import com.arty.busy.data.BusyDao;
import com.arty.busy.date.MyDate;
import com.arty.busy.enums.Sex;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;

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
//        fillCustomers();
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

        Settings.TIME_24_HOURS_FORMAT_DATE = sharedPreferences.getBoolean(Constants.KEY_TIME_24_HOURS_FORMAT_DATE, true);
    }

    public void saveSettings(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Constants.KEY_TIME_BEGINNING, Settings.TIME_BEGINNING);
        editor.putInt(Constants.KEY_TIME_ENDING, Settings.TIME_ENDING);
        editor.putInt(Constants.KEY_TIME_BEGINNING_BREAK, Settings.TIME_BEGINNING_BREAK);
        editor.putInt(Constants.KEY_TIME_ENDING_BREAK, Settings.TIME_ENDING_BREAK);

        editor.putBoolean(Constants.KEY_TIME_24_HOURS_FORMAT_DATE, Settings.TIME_24_HOURS_FORMAT_DATE);

        editor.commit();
    }

    //++ For test
    private void fillDataBaseForTest(){
        // filling of tasks
        fillTasks();

        // filling of services
        fillServices();

        //filling of customers
        fillCustomers();
    }

    private void fillTasks(){
        List<Task> taskList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        MyDate.setStartDay(calendar);
        int uid = 0;
        for (int i = 0; i < 3000; i++) {
            Date dt; // = calendar.getTime();
            Date dd = MyDate.getStartDay(calendar);

            byte hour = 7;
            int randomJ = (int) Math.round(Math.random()*5)+1;
            for (int j = 0; j < randomJ; j++) {
                int randomNum = (int) Math.round(Math.random()*4)+1;
                int randomHour = (int) Math.round(Math.random()*2)+1;
                int randomMinute = (int) Math.round(Math.random()*1);

                hour += randomHour;

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, randomMinute==1 ? 30 : 0);
                dt = calendar.getTime();

                Task task = new Task();
                task.uid = uid;
                task.day = dd.getTime();
                task.time = MyDate.timeFormat24.format(dt);
                task.id_customer = randomNum;
                task.id_service = randomNum;

                taskList.add(task);
                uid++;
            }

            calendar.add(Calendar.DATE, 1);
        }

        this.getBusyDao().insertTaskList(taskList);
    }

    private void fillServices(){
        List<Service> serviceList = new ArrayList<>();

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
        service3.execution_time = 90;
        service3.preparation_time = 15;
        service3.id_line_of_business = 1;
        serviceList.add(service3);

        Service service4 = new Service();
        service4.uid = 4;
        service4.title = "Ресницы 5D";
        service4.short_title = "Р5D";
        service4.description = "Ресницы 5D";
        service4.price = 500.00;
        service4.execution_time = 120;
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

        this.getBusyDao().insertServiceList(serviceList);
    }

    private void fillCustomers(){
        List<Customer> customerList = new ArrayList<>();

        Customer customer1 = new Customer();
        customer1.uid = 1;
        customer1.first_name = "Женя";
        customer1.last_name = "Иванова";
        customer1.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customer1.age = 25;
        customerList.add(customer1);

        Customer customer2 = new Customer();
        customer2.uid = 2;
        customer2.first_name = "Максим";
        customer2.last_name = "Петров";
        customer2.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customer2.age = 25;
        customerList.add(customer2);

        Customer customer3 = new Customer();
        customer3.uid = 3;
        customer3.first_name = "Наташа";
        customer3.last_name = "Романова";
        customer3.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customer3.age = 25;
        customerList.add(customer3);

        Customer customer4 = new Customer();
        customer4.uid = 4;
        customer4.first_name = "Ванда";
        customer4.last_name = "Максимова";
        customer4.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customer4.age = 25;
        customerList.add(customer4);

        Customer customer5 = new Customer();
        customer5.uid = 5;
        customer5.first_name = "Полина";
        customer5.last_name = "Гага";
        customer5.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customer5.age = 25;
        customerList.add(customer5);

        this.getBusyDao().insertCustomerList(customerList);
    }

    private void fillWorkday(){
        Settings.TIME_BEGINNING = 8; // 8:00
        Settings.TIME_ENDING = 19; // 20:00

        saveSettings();
    }
    //-- For test
}
