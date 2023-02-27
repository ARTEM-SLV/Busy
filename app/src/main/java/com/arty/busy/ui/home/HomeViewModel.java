package com.arty.busy.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.Constants;
import com.arty.busy.Settings;
import com.arty.busy.data.BusyDao;
import com.arty.busy.date.MyDate;
import com.arty.busy.date.Time;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.home.items.ItemListOfDays;
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
                loadDataInListOfDays(listOfDays, MyDate.getStartDay(d).getTime(), -14, 14);
                startDate = listOfDays.get(0).getDate().getTime();
                endDate = listOfDays.get(listOfDays.size()-1).getDate().getTime();
        }


        MutableLiveData<List<ItemListOfDays>> mListOfDays = new MutableLiveData<>();
        mListOfDays.setValue(listOfDays);

        return mListOfDays;
    }

    private void loadDataInListOfDays(List<ItemListOfDays> listOfDays, long referenceDate, int beforeDays, int afterDays){
        long dateBeginning = referenceDate + MyDate.DAY*beforeDays;
        long dateEnding = referenceDate + MyDate.DAY*afterDays;

        List<ItemTaskList> taskList = busyDao.getTaskList(dateBeginning, dateEnding);

        for (long day = dateBeginning; day <= dateEnding; day+=MyDate.DAY) {
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

    public int getPosStartTasks(int time){
        return time*360 + time*12;
    }

    public Time getCurrentTime(){
        return MyDate.getTime(new Date());
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

    public String getCustomerName(Customer customer){
        StringBuilder nameCustomer = new StringBuilder("");
        nameCustomer.append(customer.first_name);
        nameCustomer.append(" ");
        nameCustomer.append(customer.last_name);

        return nameCustomer.toString();
    }

    public String getViewOfTime(String timeStart, String timeEnd){
        StringBuilder sbTime = new StringBuilder("");
        sbTime.append(timeStart);
        sbTime.append(" - ");
        sbTime.append(timeEnd);

        return sbTime.toString();
    }
}