package com.arty.busy.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.ui.home.tasklist.ItemListOfDays;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<ItemListOfDays>> mListOfDays;

    public LiveData<List<ItemListOfDays>> getListOfDays() {
        List<ItemListOfDays> listOfDays = App.getInstance().getMainDao().getTasksForDay();
//
//        Calendar calendar = new GregorianCalendar();
//        int randomNum;
//        for (int i = 0; i < 30; i++) {
//            ItemListOfDays item = new ItemListOfDays();
//
//            Date d = calendar.getTime();
//            item.setDay(d);
//            calendar.roll(Calendar.DATE, true);
//
//            randomNum = (int) (Math.random()*9)+1;
//            item.setCountTasks(randomNum);
//
//            ArrayList <IconTasksValues> listTasks = new ArrayList<>();
//            for (int j = 0; j < randomNum; j++) {
//                int randomNum2 = (int) (Math.random()*10);
//
//                IconTasksValues dataTask = new IconTasksValues(arrNameForTest[randomNum2], arrDraw[randomNum2]);
//                listTasks.add(dataTask);
//
//                item.setListTasks(listTasks);
//            }
//
//            listOfDays.add(item);
//        }

        mListOfDays = new MutableLiveData<>();
        mListOfDays.setValue(listOfDays);

        return mListOfDays;
    }
}