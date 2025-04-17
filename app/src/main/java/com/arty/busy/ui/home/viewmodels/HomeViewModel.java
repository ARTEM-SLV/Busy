package com.arty.busy.ui.home.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.consts.Constants;
import com.arty.busy.database.BusyDao;
import com.arty.busy.date.DateTime;
import com.arty.busy.ui.home.items.ItemListOfDays;
import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private long startDate, endDate;
    private final BusyDao busyDao;
    private ArrayList<Long> currListOfDate;
    private ArrayList<Long> addedListOfDate;
//    private final MutableLiveData<Date> triggerDate = new MutableLiveData<>();

    public HomeViewModel() {
        this.busyDao = App.getInstance().getBusyDao();
        currListOfDate = new ArrayList<>();
    }

    public LiveData<List<ItemListOfDays>> getListOfDays(Date d) {
        List<ItemListOfDays> listOfDays = new ArrayList<>();

        if (d == null){
            d = new Date();
        }

        loadDataInListOfDays(listOfDays, DateTime.getStartDay(d).getTime(), -14, 14);
        currListOfDate.addAll(addedListOfDate);
        startDate = listOfDays.get(0).getDate().getTime();
        endDate = listOfDays.get(listOfDays.size()-1).getDate().getTime();

        MutableLiveData<List<ItemListOfDays>> mListOfDays = new MutableLiveData<>();
        mListOfDays.setValue(listOfDays);

        return mListOfDays;
    }

    public LiveData<List<ItemListOfDays>> getListOfDays(int direction) {
        List<ItemListOfDays> listOfDays = new ArrayList<>();

        switch (direction) {
            case Constants.DIRECTION_BACK:
                loadDataInListOfDays(listOfDays, startDate, -14, -1);
                currListOfDate.addAll(0, addedListOfDate);
                startDate = listOfDays.get(0).getDate().getTime();
                break;
            case Constants.DIRECTION_FORWARD:
                loadDataInListOfDays(listOfDays, endDate, 1, 14);
                currListOfDate.addAll(addedListOfDate);
                endDate = listOfDays.get(listOfDays.size()-1).getDate().getTime();
        }


        MutableLiveData<List<ItemListOfDays>> mListOfDays = new MutableLiveData<>();
        mListOfDays.setValue(listOfDays);

        return mListOfDays;
    }

    private void loadDataInListOfDays(List<ItemListOfDays> listOfDays, long currentDate, int beforeDays, int afterDays){
        long dateBeginning = currentDate + DateTime.DAY*beforeDays;
        long dateEnding = currentDate + DateTime.DAY*afterDays;

        List<ItemTaskList> taskList = busyDao.getTaskList(dateBeginning, dateEnding);
        addedListOfDate = new ArrayList<>();

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

            addedListOfDate.add(day);
        }
    }

    public void createListOfDate(){
        currListOfDate = new ArrayList<>();
    }

    public ArrayList<Long> getListOfDate(){
        return currListOfDate;
    }
}