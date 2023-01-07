package com.arty.busy.ui.customers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;

import java.util.List;

public class CustomersViewModel extends ViewModel {

    private final MutableLiveData<List<Customer>> mListOfCustomers;

    public CustomersViewModel() {
        List<Customer> listOfCustomers = App.getInstance().getBusyDao().getAllCustomers();

        mListOfCustomers = new MutableLiveData<>();
        mListOfCustomers.setValue(listOfCustomers);
    }

    public LiveData<List<Customer>> getListOfCustomers() {
        return mListOfCustomers;
    }
}