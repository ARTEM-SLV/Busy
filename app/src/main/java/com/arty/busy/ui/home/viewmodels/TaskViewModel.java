package com.arty.busy.ui.home.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.R;
import com.arty.busy.database.BusyDao;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;

public class TaskViewModel extends ViewModel {
    private final BusyDao busyDao;
    private Task currentTask, modifiedTask;

    public TaskViewModel() {
        this.busyDao = App.getInstance().getBusyDao();
    }

    public void getTasks(int idTask, long currDate, String time){
        if (currentTask == null){
            if (idTask == -1){
                currentTask = new Task();
                currentTask.day = currDate;
                currentTask.time = time;
            } else {
                currentTask = getTask(idTask);
            }
            modifiedTask = new Task(currentTask);
        }
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public Task getModifiedTask() {
        return modifiedTask;
    }

    public Task getTask(int uid){
        return busyDao.getTaskByID(uid);
    }

    public Service getService(int uid){
        Service service = busyDao.getServiceByID(uid);
        if (service == null) {
            service = new Service();
            service.title = "<" + uid + ">" + App.getInstance().getString(R.string.w_object_not_found_in_database);
        }

        return service;
    }

    public Customer getCustomer(int uid){
        Customer customer = busyDao.getCustomerByID(uid);
        if (customer == null){
            customer = new Customer();
            customer.first_name = "<" + uid + ">" + App.getInstance().getString(R.string.w_object_not_found_in_database);
        }

        return customer;
    }

    public void insertTask(Task task){
        try {
            new Thread(() -> busyDao.insertTask(task)).start();
            Log.d("Database", "Task insert: " + task.uid);
            Log.d("Task", task.toString());
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to insert task", e);
        }
    }

    public void updateTask(Task task){
        try {
            new Thread(() -> busyDao.updateTask(task)).start();
            Log.d("Database", "Task updated: " + task.uid);
            Log.d("Task", task.toString());
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to update task", e);
        }
    }

    public void deleteTask(Task task){
        try {
            new Thread(() -> busyDao.deleteTask(task)).start();
            Log.d("Database", "Task delete: " + task.uid);
            Log.d("Task", task.toString());
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to delete task", e);
        }
    }
}