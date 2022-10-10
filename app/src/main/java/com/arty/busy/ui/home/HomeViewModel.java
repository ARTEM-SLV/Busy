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
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int position = 0;
        for (int i = 1; i < 101; i++) {
            Date d = calendar.getTime();
            ItemListOfDays itemListOfDays = new ItemListOfDays();
            itemListOfDays.setDate(d);

//            if (position == taskList.size())
//                position = 0;

            List<String> titlesService = new ArrayList<>();
            for (int j = position; j < taskList.size(); j++) {
                ItemTaskList task = taskList.get(j);

                if (task.getDate()>d.getTime())
                    break;

                if (task.getDate()==d.getTime()) {
                    titlesService.add(task.getTitleServices());

                    position = j+1;
                }
            }
            itemListOfDays.setTitlesService(titlesService);
            listOfDays.add(itemListOfDays);

            MyDate.setNextDay(calendar, d);
        }

        mListOfDays = new MutableLiveData<>();
        mListOfDays.setValue(listOfDays);

        return mListOfDays;
    }
}