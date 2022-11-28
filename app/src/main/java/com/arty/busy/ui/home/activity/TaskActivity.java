package com.arty.busy.ui.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.arty.busy.App;
import com.arty.busy.Constants;
import com.arty.busy.R;
import com.arty.busy.models.Task;
import com.arty.busy.ui.home.items.ItemTaskToDay;

public class TaskActivity extends Activity {
    private boolean modified;
    private ItemTaskToDay itemTaskToDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_task);

        setModified(false);

        Intent intent = getIntent();
        if (intent != null){
            itemTaskToDay = (ItemTaskToDay) intent.getSerializableExtra(Constants.ITEM_TASK_TO_DAY);
            setData();
        }
    }

    private void setModified(boolean value){
        modified = value;
    }

    private void setData(){
        if (itemTaskToDay == null)
            return;

        TextView tvTime = findViewById(R.id.tvTime_T);
        tvTime.setText(itemTaskToDay.getTime());

        TextView etService = findViewById(R.id.etService_T);
        etService.setText(itemTaskToDay.getServices());

        TextView etCustomer = findViewById(R.id.etCustomer_T);
        etCustomer.setText(itemTaskToDay.getClient());

        EditText etPrice = findViewById(R.id.etPrice_T);
        etPrice.setText(String.valueOf(itemTaskToDay.getPrice()));

        CheckBox cbDone = findViewById(R.id.cbDone_T);
        cbDone.setChecked(itemTaskToDay.isDone());

        CheckBox cbPaid = findViewById(R.id.cbPaid_T);
        cbPaid.setChecked(itemTaskToDay.isPaid());
    }

    public void onServiceClick(View v){
        setModified(true);
    }

    public void onCustomerClick(View v){
        setModified(true);
    }

    public void onOkClick(View v){
        if (modified){
            updateDataTask();
        }

        finish();
    }

    private void updateDataTask(){
        Task task = App.getInstance().getBusyDao().getTaskByID(itemTaskToDay.getId_task());

//        task.

        App.getInstance().getBusyDao().updateTaskList(task);
    }

    public void onCancelClick(View v){
        if (modified){

        } else finish();
    }
}