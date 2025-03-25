package com.arty.busy.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.home.items.ItemTaskInfo;
import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.List;

@Dao
public interface BusyDao {

    @Query("SELECT " +
                "tasks.day as date, " +
                "tasks.time as time," +
                "services.short_title as servicesShort " +
            "FROM Task as tasks " +
                "LEFT OUTER JOIN Service as services ON tasks.id_service = services.uid " +
            "WHERE tasks.day BETWEEN :dateBeginning AND :dateEnding " +
            "ORDER BY tasks.day, tasks.time")
    List<ItemTaskList> getTaskList(long dateBeginning, long dateEnding);

    @Query("SELECT " +
                "tasks.uid as id_task, " +
                "tasks.time as time, " +
                "tasks.done as done, " +
                "services.title as services, " +
                "tasks.duration as duration, " +
                "customers.first_name || ' ' || customers.last_name  as client " +
            "FROM Task as tasks " +
                "LEFT OUTER JOIN Service as services ON tasks.id_service = services.uid " +
                "LEFT OUTER JOIN Customer as customers ON tasks.id_customer = customers.uid " +
            "WHERE tasks.day = :day " +
            "ORDER BY tasks.day, tasks.time")
    List<ItemTaskInfo> getTasksInfoByDay(long day);

    // Tasks
    @Query("SELECT * FROM Task WHERE uid =:uid")
    Task getTaskByID(int uid);

    @Query("SELECT * FROM Task GROUP BY day")
    List<Task> getTasksByDay();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaskList(List<Task> taskList);

    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTaskList(Task task);

    // Services
    @Query("SELECT * FROM Service")
    List<Service> getAllServices();

    @Query("SELECT * FROM Service WHERE uid =:uid")
    Service getServiceByID(int uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertServiceList(List<Service> serviceList);

    @Update
    void updateServiceList(Service service);

    @Delete
    void deleteServiceList(Service service);

    // Customers
    @Query("SELECT * FROM Customer")
    List<Customer> getAllCustomers();

    @Query("SELECT * FROM Customer WHERE uid =:uid")
    Customer getCustomerByID(int uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCustomerList(List<Customer> customerList);

    @Update
    void updateCustomerList(Customer customer);

    @Insert
    void insertCustomer(Customer customer);

    @Update
    void updateCustomer(Customer customer);

    @Delete
    void deleteCustomerList(Customer customer);
}

