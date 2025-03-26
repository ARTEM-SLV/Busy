package com.arty.busy.ui.customers.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.database.BusyDao;
import com.arty.busy.models.Customer;

public class CustomerViewModel extends ViewModel {
    private final BusyDao busyDao;
//    private final MutableLiveData<Customer> mCustomer = new MutableLiveData<>();

    public CustomerViewModel() {
        busyDao = App.getInstance().getBusyDao();
    }

    public void insertCustomer(Customer customer) {
        try {
            new Thread(() -> busyDao.insertCustomer(customer)).start();
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to insert customer", e);
        }
    }

    public void updateCustomer(Customer customer){
        try {
            new Thread(() -> busyDao.updateCustomer(customer)).start();
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to update customer", e);
        }
    }

    public void deleteCustomer(Customer customer){
        try {
            new Thread(() -> busyDao.deleteCustomerList(customer)).start();
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to delete customer", e);
        }
    }
}
