package com.arty.busy.ui.home.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.arty.busy.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TasksToDayActivity extends Activity {
    private Object date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_to_day);

        init();
    }

    private void init(){
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEEE dd. MMM");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = extras.get("Date");
            TextView tvDate = findViewById(R.id.tvTestDate_TTD);
            tvDate.setText(df.format(date));
        }
    }
}