package com.arty.busy.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.consts.Constants;
import com.arty.busy.database.BusyDao;
import com.arty.busy.date.DateTime;
import com.arty.busy.date.Time;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.home.items.ItemListOfDays;
import com.arty.busy.ui.home.items.ItemTaskByHours;
import com.arty.busy.ui.home.items.ItemTaskInfo;
import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private long startDate, endDate;
    private BusyDao busyDao;

    public HomeViewModel() {
        this.busyDao = App.getInstance().getBusyDao();
    }

    public LiveData<List<ItemListOfDays>> getListOfDays(int direction) {
        List<ItemListOfDays> listOfDays = new ArrayList<>();

        switch (direction) {
            case Constants.DIRECTION_BACK:
                loadDataInListOfDays(listOfDays, startDate, -14, -1);
                startDate = listOfDays.get(0).getDate().getTime();

                break;
            case Constants.DIRECTION_FORWARD:
                loadDataInListOfDays(listOfDays, endDate, 1, 14);
                endDate = listOfDays.get(listOfDays.size()-1).getDate().getTime();

                break;
            default:
                Date d = new Date();
                loadDataInListOfDays(listOfDays, DateTime.getStartDay(d).getTime(), -14, 14);
                startDate = listOfDays.get(0).getDate().getTime();
                endDate = listOfDays.get(listOfDays.size()-1).getDate().getTime();
        }


        MutableLiveData<List<ItemListOfDays>> mListOfDays = new MutableLiveData<>();
        mListOfDays.setValue(listOfDays);

        return mListOfDays;
    }

    private void loadDataInListOfDays(List<ItemListOfDays> listOfDays, long referenceDate, int beforeDays, int afterDays){
        long dateBeginning = referenceDate + DateTime.DAY*beforeDays;
        long dateEnding = referenceDate + DateTime.DAY*afterDays;

        List<ItemTaskList> taskList = busyDao.getTaskList(dateBeginning, dateEnding);

        for (long day = dateBeginning; day <= dateEnding; day+= DateTime.DAY) {
            ItemListOfDays itemListOfDays = new ItemListOfDays();
            itemListOfDays.setDate(new Date(day));

            List<String> timeService = new ArrayList<>();
            List<String> titlesService = new ArrayList<>();
            int position = 0;
            for (int i = position; i < taskList.size(); i++) {
                ItemTaskList task = taskList.get(i);

                if (task.getDate() > day)
                    break;

                if (task.getDate() == day) {
                    timeService.add(task.getTime());
                    titlesService.add(task.getServicesShort());

                    position = i + 1;
                }
            }
            itemListOfDays.setTimeService(timeService);
            itemListOfDays.setTitlesService(titlesService);
            listOfDays.add(itemListOfDays);
        }
    }

    public List<ItemTaskInfo> getListTasksToDay(long date){
        List<ItemTaskInfo> taskInfoList = busyDao.getTasksInfoByDay(date);

        return taskInfoList;
    }

    public LiveData<List<ItemTaskByHours>> getLiveListTasksToDay(long date){
        List<ItemTaskInfo> taskInfoList = busyDao.getTasksInfoByDay(date);
        List<ItemTaskByHours> taskByHoursList = new ArrayList<>();
        ItemTaskByHours taskByHours;
        Time currentTime;
        Time timeStart;
        Time timeEnd;
        int duration;


        for (byte i = 0; i < 24; i++) {
            currentTime = new Time(i, (byte) 0);
            taskByHours = new ItemTaskByHours();
            taskByHours.setCurrentTime(currentTime.toString());

            for (ItemTaskInfo itemTaskInfo: taskInfoList) {
                String sTimeStart = itemTaskInfo.getTime();
                timeStart = DateTime.parseStringToTime(sTimeStart);

                if (timeStart.getHour() == i) {
                    duration = itemTaskInfo.getDuration();
                    timeEnd = DateTime.parseStringToTime(sTimeStart);
                    timeEnd.addTime(duration);
                    String sTimeEnd = DateTime.parseTimeToString(timeEnd);

                    taskByHours.setId_task(itemTaskInfo.getId_task());
                    taskByHours.setTaskTime(sTimeStart + " - " + sTimeEnd);
                    taskByHours.setServices(itemTaskInfo.getServices());
                    taskByHours.setClient(itemTaskInfo.getClient());
                    taskByHours.setHour(timeStart.getHour());
                    taskByHours.setMinutes(timeStart.getMinute());
                    taskByHours.setDuration(duration);
                    taskByHours.setTask(true);
                }
            }

            taskByHoursList.add(taskByHours);
        }

        MutableLiveData<List<ItemTaskByHours>> mListTaskInfo = new MutableLiveData<>();
        mListTaskInfo.setValue(taskByHoursList);

        return mListTaskInfo;
    }

    public int getPosStartListTasks(long date){
        int result = 0;
        Time timeStart;
        List<ItemTaskInfo> taskInfoList = busyDao.getTasksInfoByDay(date);
        Date currentDateStart = DateTime.getCurrentStartDate();
        Time currentTame = DateTime.getCurrentTime();

        for (ItemTaskInfo itemTaskInfo: taskInfoList) {
            String sTimeStart = itemTaskInfo.getTime();
            timeStart = DateTime.parseStringToTime(sTimeStart);
            if (date == currentDateStart.getTime()){
                if(timeStart.getHour() >= currentTame.getHour()){
                    result = timeStart.getHour();
                    break;
                }
            } else {
                result = timeStart.getHour();
                break;
            }
        }

        return result;
    }

    public int getPosStartTasks(int time){
        return time*360 + time*12;
    }

    public Time getCurrentTime(){
        return DateTime.getTime(new Date());
    }

    public Task getTask(int uid){
        return busyDao.getTaskByID(uid);
    }

    public Service getService(int uid){
        return busyDao.getServiceByID(uid);
    }

    public Customer getCustomer(int uid){
        return busyDao.getCustomerByID(uid);
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
            new Thread(() -> busyDao.deleteTaskList(task)).start();
            Log.d("Database", "Task delete: " + task.uid);
            Log.d("Task", task.toString());
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to delete task", e);
        }
    }
}