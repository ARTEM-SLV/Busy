package com.arty.busy.ui.customers.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.database.BusyDao;
import com.arty.busy.models.Customer;

import java.util.List;

public class CustomersViewModel extends ViewModel {
    private final BusyDao busyDao;

    public CustomersViewModel() {
        busyDao = App.getInstance().getBusyDao();
    }

    public LiveData<List<Customer>> getListOfCustomers() {
        return busyDao.getAllCustomers();
    }
}