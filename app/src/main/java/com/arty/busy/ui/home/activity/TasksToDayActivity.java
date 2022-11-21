package com.arty.busy.ui.home.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.arty.busy.App;
import com.arty.busy.R;
import com.arty.busy.Settings;
import com.arty.busy.date.MyDate;
import com.arty.busy.date.Time;
import com.arty.busy.ui.home.items.ItemTasksToDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TasksToDayActivity extends Activity {
    private long currDate;
    private TextView tvDate;
    private ScrollView scrollView;
    private ConstraintLayout constraintLayout;

    private int posStart = Settings.TIME_BEGINNING*180 + Settings.TIME_BEGINNING*12;
    private int posY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_to_day);

        init();
        fillTasksData();
    }

    private void init(){
        int lineID;
        LinearLayout linerHours;
        tvDate = (TextView) findViewById(R.id.tvTestDate_TTD);
        constraintLayout = findViewById(R.id.constraint_TTD);
        scrollView = (ScrollView) findViewById(R.id.scroll_TTD);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, posStart);
            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                posY = scrollView.getScrollY();
            }
        });

        setInvisibleAllBtnTask();

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEEE dd MMM.");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currDate = extras.getLong("Date");

            tvDate.setText(df.format(currDate));
        }

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollBy(0, posStart - posY);
            }
        });

        if (Settings.TIME_BEGINNING != -1 || Settings.TIME_ENDING != -1) {
            for (int h = 0; h < 24; h++) {
                lineID = this.getResources().getIdentifier("hour" + (h) + "_TTD", "id", getPackageName());
                linerHours = findViewById(lineID);

//                if (Settings.TIME_BEGINNING != -1 && h < Settings.TIME_BEGINNING || Settings.TIME_ENDING != -1 && h > Settings.TIME_ENDING)
//                    linerHours.setBackgroundResource(R.color.Gray_20);

                if (h == Settings.TIME_BEGINNING)
                    linerHours.setBackgroundResource(R.drawable.style_topline_salmon);

                if (h == Settings.TIME_ENDING)
                    linerHours.setBackgroundResource(R.drawable.style_bottomline_salmon);
            }
        }
    }

    private void fillTasksData(){
        Time time;
        int btnID;
        byte hour;
        byte minute;

        List<ItemTasksToDay> listTasksToDay = App.getInstance().getBusyDao().getTasksToDay(currDate);

        for (ItemTasksToDay itemTaskToDay: listTasksToDay) {
            String sTime = itemTaskToDay.getTime();
            time = MyDate.parseStringToTime(sTime);
            hour = time.getHour();
            minute = time.getMinute();

            btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", getPackageName());
            Button btnTask = findViewById(btnID);

            ConstraintLayout.LayoutParams btnParams = (ConstraintLayout.LayoutParams) btnTask.getLayoutParams();
            btnParams.setMargins(0, minute*3, 0, 0);
            btnParams.setMarginStart(270);
            btnTask.setLayoutParams(btnParams);
            btnTask.setHeight(itemTaskToDay.getExecution_time()*3);

            btnTask.setText(sTime + "\n" + itemTaskToDay.getServices() + "\n" + itemTaskToDay.getClient());
            btnTask.setVisibility(View.VISIBLE);
        }
    }

    private void setInvisibleAllBtnTask(){
        int btnID;

        for (int hour = 0; hour < 24; hour++) {
            btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", getPackageName());
            Button btnTask = findViewById(btnID);
            btnTask.setVisibility(View.INVISIBLE);
        }
    }
}