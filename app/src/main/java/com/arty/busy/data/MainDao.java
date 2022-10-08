package com.arty.busy.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.arty.busy.model.Service;
import com.arty.busy.model.Task;
import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.List;

@Dao
public interface MainDao {

    @Query("SELECT tasks.day as date, services.short_title titleServices " +
            "FROM Task as tasks " +
            "LEFT OUTER JOIN Service as services " +
            "ON tasks.id_service = services.uid")
    List<ItemTaskList> getTasksAndServices();

//
//    @Query("SELECT * FROM Task WHERE date = :date")
//    List<Task> findByDate(long date);

    //tasks
    @Query("SELECT * FROM Task")
    List<Task> loadAll();

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
}

