package com.arty.busy.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.MyDate;
import com.arty.busy.ui.home.items.ItemListOfDays;
import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<ItemListOfDays>> mListOfDays;

    public LiveData<List<ItemListOfDays>> getListOfDays() {
        List<ItemTaskList> taskList = App.getInstance().getMainDao().getTasksAndServices();
        List<ItemListOfDays> listOfDays = new ArrayList<>();

        Calendar calendar = new GregorianCalendar();
        for (int i = 1; i < 101; i++) {
            Date d = MyDate.getStartDay(calendar);
            calendar.roll(Calendar.DATE, true);
            ItemListOfDays itemListOfDays = new ItemListOfDays();
            itemListOfDays.setDate(d);

            List<String> titlesService = new ArrayList<>();

            for (ItemTaskList task: taskList) {
                Date dateTask = new Date(task.getDate());
                if (d.compareTo(dateTask)==0) {
                    titlesService.add(task.getTitleServices());
                }
            }
            itemListOfDays.setTitlesService(titlesService);

            listOfDays.add(itemListOfDays);
        }

        mListOfDays = new MutableLiveData<>();
        mListOfDays.setValue(listOfDays);

        return mListOfDays;
    }
}