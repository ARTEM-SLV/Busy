package com.arty.busy.ui.home.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.consts.Constants;
import com.arty.busy.database.BusyDao;
import com.arty.busy.date.DateTime;
import com.arty.busy.ui.home.items.ItemListOfDays;
import com.arty.busy.ui.home.items.ItemTaskInfo;
import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TasksViewModel extends ViewModel {
    private final BusyDao busyDao;

    public TasksViewModel() {
        this.busyDao = App.getInstance().getBusyDao();
    }

    public List<ItemTaskInfo> getListTasksToDay(long date){
        return busyDao.getTasksInfoByDay(date);
    }
}