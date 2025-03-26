package com.arty.busy.ui.customers.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.database.BusyDao;
import com.arty.busy.models.Customer;

import java.util.List;

public class CustomersViewModel extends ViewModel {
    private final BusyDao busyDao;
//    private final MutableLiveData<Customer> mCustomer = new MutableLiveData<>();

    public CustomersViewModel() {
        busyDao = App.getInstance().getBusyDao();
    }

    public LiveData<List<Customer>> getListOfCustomers() {
        List<Customer> listOfCustomers = busyDao.getAllCustomers();

        MutableLiveData<List<Customer>> mListOfCustomers = new MutableLiveData<>();
        mListOfCustomers.setValue(listOfCustomers);

        return mListOfCustomers;
    }

//    public void setCustomer(Customer customer) {
//        mCustomer.setValue(customer);
//    }
//
//    public LiveData<Customer> getCustomer() {
//        return mCustomer;
//    }
}