package com.arty.busy.ui.home.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arty.busy.R;
import com.arty.busy.Settings;

import java.text.Collator;
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
        LinearLayout line;
        int lineID;

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEEE dd. MMM");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = extras.get("Date");
            TextView tvDate = findViewById(R.id.tvTestDate_TTD);
            tvDate.setText(df.format(date));
        }

        for (int h = 0; h < 24; h++) {
            lineID = this.getResources().getIdentifier("line" + (h+1) + "_TOD", "id", getPackageName());
            line = findViewById(lineID);

            if(h < Settings.TIME_BEGINNING || h > Settings.TIME_ENDING)
                line.setBackgroundResource(R.color.Gray_30);

            if (h == Settings.TIME_BEGINNING)
                line.setBackgroundResource(R.drawable.style_topline_salmon);

            if (h == Settings.TIME_ENDING)
                line.setBackgroundResource(R.drawable.style_bottomline_salmon);
        }

//        Log.e("My settings", "time beginning: " + Settings.TIME_BEGINNING);
//        Log.e("My settings", "time ending: " + Settings.TIME_ENDING);
    }
}