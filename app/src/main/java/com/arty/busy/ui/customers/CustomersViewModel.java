package com.arty.busy.ui.customers;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.database.BusyDao;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Task;

import java.util.List;

public class CustomersViewModel extends ViewModel {
    private BusyDao busyDao;
    private final MutableLiveData<List<Customer>> mListOfCustomers;
    private final MutableLiveData<Customer> mCustomer = new MutableLiveData<>();

    public CustomersViewModel() {
        busyDao = App.getInstance().getBusyDao();
        List<Customer> listOfCustomers = busyDao.getAllCustomers();

        mListOfCustomers = new MutableLiveData<>();
        mListOfCustomers.setValue(listOfCustomers);
    }

    public LiveData<List<Customer>> getListOfCustomers() {
        return mListOfCustomers;
    }

    public void setCustomer(Customer customer) {
        mCustomer.setValue(customer);
    }

    public LiveData<Customer> getCustomer() {
        return mCustomer;
    }

    public void insertCustomer(Customer customer) {
        try {
            new Thread(() -> busyDao.insertCustomer(customer)).start();
            Log.d("Database", "Task insert: " + customer.uid);
            Log.d("Task", customer.toString());
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to insert task", e);
        }
    }

    public void updateCustomer(Customer customer){
        try {
            new Thread(() -> busyDao.updateCustomer(customer)).start();
            Log.d("Database", "Task updated: " + customer.uid);
            Log.d("Task", customer.toString());
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to update task", e);
        }
    }

    public void deleteCustomer(Customer customer){
        try {
            new Thread(() -> busyDao.deleteCustomerList(customer)).start();
            Log.d("Database", "Task delete: " + customer.uid);
            Log.d("Task", customer.toString());
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to delete task", e);
        }
    }
}