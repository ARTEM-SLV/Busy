package com.arty.busy.ui.home.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.arty.busy.App;
import com.arty.busy.Constants;
import com.arty.busy.R;
import com.arty.busy.data.BusyDao;
import com.arty.busy.date.MyDate;
import com.arty.busy.date.Time;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.customers.CustomersFragment;
import com.arty.busy.ui.home.items.ItemTaskInfo;

public class TaskActivity extends AppCompatActivity {
    private boolean modified;
    private ItemTaskInfo itemTaskInfo;
    private Task currentTask;
    private Customer customer;
    private Service service;

    private BusyDao busyDao;

    private CheckBox cbDone;
    private CheckBox cbPaid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_task);

        init();
        setVisibilityView();
    }

    private void init(){
        setModified(false);

        initCheckBoxDone();
        initCheckBoxPaid();

        busyDao = App.getInstance().getBusyDao();

        Intent intent = getIntent();
        if (intent != null){
            itemTaskInfo = (ItemTaskInfo) intent.getSerializableExtra(Constants.ITEM_TASK_TO_DAY);
            setData();
        }
    }

    private void setVisibilityView(){
        ImageButton btnPostpone = findViewById(R.id.btnPostpone_T);
        ImageButton btnCancelTask = findViewById(R.id.btnCancelTask_T);
        if (itemTaskInfo == null){
            btnPostpone.setVisibility(View.GONE);
            btnCancelTask.setVisibility(View.INVISIBLE);
        } else {
            btnPostpone.setVisibility(View.VISIBLE);
            btnCancelTask.setVisibility(View.VISIBLE);
        }
    }

    private void initCheckBoxDone(){
        cbDone = findViewById(R.id.cbDone_T);
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setModified(true);
            }
        });
    }

    private void initCheckBoxPaid(){
        cbPaid = findViewById(R.id.cbPaid_T);
        cbPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setModified(true);
            }
        });
    }

    private void setModified(boolean isModified){
        modified = isModified;
    }

    private void setData(){
        if (itemTaskInfo == null)
            return;

        currentTask = busyDao.getTaskByID(itemTaskInfo.getId_task());

        setServiceForView(currentTask.id_service);
        setCustomerForView(currentTask.id_customer);
        setPriceForView(currentTask.price);

        Time timeEnd = MyDate.parseStringToTime(currentTask.time);
        timeEnd.addTime(service.duration);
        String sTimeEnd = MyDate.parseTimeToString(timeEnd);
        setTimeForView(currentTask.time, sTimeEnd);

        cbDone.setChecked(currentTask.done);
        cbPaid.setChecked(currentTask.paid);
    }

    private void updateDataTask(){
//        App.getInstance().getBusyDao().updateTaskList(task);
    }

    private Service getService(int uid){
        return busyDao.getServiceByID(uid);
    }

    private void setServiceForView(int uid){
        service = getService(uid);

        TextView etService = findViewById(R.id.etService_T);
        etService.setText(service.title);
    }

    private Customer getCustomer(int uid){
        return busyDao.getCustomerByID(uid);
    }

    private void setCustomerForView(int uid){
        customer = getCustomer(uid);

        StringBuilder nameCustomer = new StringBuilder("");
        nameCustomer.append(customer.first_name);
        nameCustomer.append(" ");
        nameCustomer.append(customer.last_name);

        TextView etCustomer = findViewById(R.id.etCustomer_T);
        etCustomer.setText(nameCustomer);
    }

    private void setPriceForView(double price){
        EditText etPrice = findViewById(R.id.etPrice_T);
        etPrice.setText(String.valueOf(price));
    }

    private void setTimeForView(String timeStart, String timeEnd){
        StringBuilder sbTime = new StringBuilder("");
        sbTime.append(timeStart);
        sbTime.append(" - ");
        sbTime.append(timeEnd);

        TextView tvTime = findViewById(R.id.tvTime_T);
        tvTime.setText(sbTime);
    }

    public void onOkClick(View v){
        if (modified){
            updateDataTask();
        }

        finish();
    }

    public void onCancelClick(View v){
        if (modified){

        } else finish();
    }

    public void onCancelTaskClick(View v){

    }

    public void onTimeClick(View v){

    }

    public void onPostponeClock(View view) {

    }

    public void onCustomerClick(View v){
        setModified(true);

        ConstraintLayout containerElements = findViewById(R.id.containerElements_T);
        containerElements.setVisibility(View.INVISIBLE);

        Bundle bundle = new Bundle();
        bundle.putInt("some_int", 0); // for example

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.mainContainer_T, CustomersFragment.class, bundle)
                .commit();
    }

    public void onServiceClick(View v){
        setModified(true);
    }

//    public void onDoneClick(Void v){
//        setModified(true);
//    }
//
//    public void onPaidClick(Void v){
//        setModified(true);
//    }

}