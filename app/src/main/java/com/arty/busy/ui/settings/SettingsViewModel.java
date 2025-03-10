package com.arty.busy.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.database.BusyDao;
import com.arty.busy.models.Task;
import com.arty.busy.ui.home.items.ItemListOfDays;

public class SettingsViewModel extends ViewModel {
    private BusyDao busyDao;

    private final MutableLiveData<String> mText;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Settings fragment");

        this.busyDao = App.getInstance().getBusyDao();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public Task testGetTaskByID(int uid){
        return busyDao.getTaskByID(uid);
    }
}