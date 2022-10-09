package com.arty.busy.ui.home.activity;

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
        setContentView(R.layout.activity_ltasks_to_day);

        init();
    }

    private void init(){
        DateFormat df = new SimpleDateFormat("EEEE dd. MMM");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = extras.get("Date");
            TextView tvDate = findViewById(R.id.tvTestDate);
            tvDate.setText(df.format(date));
        }
    }
}