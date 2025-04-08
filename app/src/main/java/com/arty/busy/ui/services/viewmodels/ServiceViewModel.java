package com.arty.busy.ui.services.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.arty.busy.App;
import com.arty.busy.database.BusyDao;
import com.arty.busy.models.Service;

public class ServiceViewModel extends ViewModel {
    private final BusyDao busyDao;

    public ServiceViewModel() {
        busyDao = App.getInstance().getBusyDao();
    }

    public void insertService(Service service) {
        try {
            new Thread(() -> busyDao.insertService(service)).start();
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to insert customer", e);
        }
    }

    public void updateService(Service service){
        try {
            new Thread(() -> busyDao.updateService(service)).start();
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to update customer", e);
        }
    }

    public void deleteService(Service service){
        try {
            new Thread(() -> busyDao.deleteService(service)).start();
        } catch (Exception e) {
            Log.e("DatabaseError", "Failed to delete customer", e);
        }
    }
}