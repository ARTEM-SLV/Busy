package com.arty.busy.ui.services.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.database.BusyDao;
import com.arty.busy.models.Service;

import java.util.List;

public class ServicesViewModel extends ViewModel {
    private final BusyDao busyDao;

    public ServicesViewModel() {
        busyDao = App.getInstance().getBusyDao();
    }

    public LiveData<List<Service>> getListOfServices() {
        return busyDao.getAllServices();
    }
}