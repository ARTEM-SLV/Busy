package com.arty.busy.ui.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.models.Service;

import java.util.List;

public class ServicesViewModel extends ViewModel {

    private MutableLiveData<List<Service>> mListOfServices;

    public ServicesViewModel() {
        List<Service> listOfServices = App.getInstance().getBusyDao().getAllServices();

        mListOfServices = new MutableLiveData<>();
        mListOfServices.setValue(listOfServices);
    }

    public LiveData<List<Service>> getListOfServices() {
        return mListOfServices;
    }
}