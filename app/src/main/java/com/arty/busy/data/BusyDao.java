package com.arty.busy.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.home.items.ItemTaskList;
import com.arty.busy.ui.home.items.ItemTasksToDay;

import java.util.List;

@Dao
public interface BusyDao {

    @Query("SELECT " +
                "tasks.day as date, " +
                "services.short_title as servicesShort " +
            "FROM Task as tasks " +
                "LEFT OUTER JOIN Service as services ON tasks.id_service = services.uid " +
            "WHERE tasks.day BETWEEN :dateBeginning AND :dateEnding " +
            "ORDER BY tasks.day")
    List<ItemTaskList> getTaskList(long dateBeginning, long dateEnding);

    @Query("SELECT " +
                "tasks.time as time, " +
                "services.title as services, " +
                "services.short_title as servicesShort, " +
                "customers.first_name as client, " +
                "services.price as price, " +
                "tasks.done as done, " +
                "tasks.paid as paid " +
            "FROM Task as tasks " +
                "LEFT OUTER JOIN Service as services ON tasks.id_service = services.uid " +
                "LEFT OUTER JOIN Customer as customers ON tasks.id_customer = customers.uid " +
            "WHERE tasks.day = :day " +
            "ORDER BY tasks.day")
    List<ItemTasksToDay> getTasksToDay(long day);

//
//    @Query("SELECT * FROM Task WHERE date = :date")
//    List<Task> findByDate(long date);

    //tasks
    @Query("SELECT day FROM Task GROUP BY day")
    List<Long> getTaskDates();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaskList(List<Task> taskList);

    @Update
    void updateTaskList(Task task);

    @Delete
    void deleteTaskList(Task task);

    //services
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertServiceList(List<Service> serviceList);

    @Update
    void updateServiceList(Service service);

    @Delete
    void deleteServiceList(Service service);

    //workday
    @Query("SELECT day FROM Task GROUP BY day")
    List<Long> getWorkdayTime();
}

