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

import com.arty.busy.App;
import com.arty.busy.R;
import com.arty.busy.Settings;
import com.arty.busy.date.MyDate;
import com.arty.busy.date.Time;
import com.arty.busy.ui.home.items.ItemTasksToDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TasksToDayActivity extends Activity {
    List<ItemTasksToDay> listTasksToDay;

    private long currDate;
    private TextView tvDate;
    private ScrollView scrollView;
    private ConstraintLayout constraintLayout;

    private int posStart = Settings.TIME_BEGINNING*360 + Settings.TIME_BEGINNING*12;
    private int posY;

    private Time currTime;

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

    private void setCurrTime(){
        Date currDate = new Date();
        currTime = MyDate.getTime(currDate);
    }

    private void setListTasksToDay(){
        listTasksToDay = App.getInstance().getBusyDao().getTasksToDay(currDate);
    }

    @SuppressLint("SetTextI18n")
    private void fillTasksData(){
        Time    timeStart;
        Time    timeEnd;
        int     duration;
        byte    hour;
        byte    minute;

        setCurrTime();
        setListTasksToDay();

        for (int i = 0; i < listTasksToDay.size(); i++) {
            ItemTasksToDay itemTaskToDay = listTasksToDay.get(i);

            duration = itemTaskToDay.getDuration();

            String sTimeStart = itemTaskToDay.getTime();
            timeStart = MyDate.parseStringToTime(sTimeStart);

            timeEnd = MyDate.parseStringToTime(sTimeStart);
            timeEnd.addTime(duration);
            String sTimeEnd = MyDate.parseTimeToString(timeEnd);

            hour = timeStart.getHour();
            minute = timeStart.getMinute();

            int currResColor = getColor(R.color.Black);
            if (itemTaskToDay.isDone()){
                currResColor = getColor(R.color.Green);
            } else if (crossedTimesOfTasks(timeStart, timeEnd, i)){
                currResColor = getColor(R.color.Brown);
            } else if (isNextTask(timeStart)){
                currResColor = getColor(R.color.Navy);
            }

            @SuppressLint("DiscouragedApi")
            int btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", getPackageName());
            Button btnTask = findViewById(btnID);

            ConstraintLayout.LayoutParams btnParams = (ConstraintLayout.LayoutParams) btnTask.getLayoutParams();
            btnParams.setMargins(0, minute*6, 0, 0);
            btnTask.setLayoutParams(btnParams);
            btnTask.setHeight(duration*6);

            btnTask.setText(sTimeStart + " - " + sTimeEnd + "\n" + itemTaskToDay.getClient() + "\n" + itemTaskToDay.getServices());
            btnTask.setTextColor(currResColor);
            btnTask.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("DiscouragedApi")
    private void setInvisibleAllBtnTask(){
        int btnID;

        for (int hour = 0; hour < 24; hour++) {
            btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", getPackageName());
            Button btnTask = findViewById(btnID);
            btnTask.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isNextTask(Time time){
        boolean res = false;

        if (currDate == MyDate.getCurrentStartDate().getTime()) {
            for (ItemTasksToDay itemTaskToDay : listTasksToDay) {
                String sTime = itemTaskToDay.getTime();
                Time t = MyDate.parseStringToTime(sTime);

                if (t.compareTo(currTime) == 1) {
                    res = t.compareTo(time) == 0;

                    break;
                }
            }
        }

        return res;
    }

    private boolean crossedTimesOfTasks(Time timeStart, Time timeEnd, int index){
        boolean res = false;

        if (index > 0){
            ItemTasksToDay itemTaskToDay = listTasksToDay.get(index-1);
            String sTime = itemTaskToDay.getTime();
            Time timeEndLastTask = MyDate.parseStringToTime(sTime);
            timeEndLastTask.addTime(itemTaskToDay.getDuration());

            res = res || timeStart.compareTo(timeEndLastTask) == -1;
        }
        if (index+1 < listTasksToDay.size()){
            ItemTasksToDay itemTaskToDay = listTasksToDay.get(index+1);
            String sTime = itemTaskToDay.getTime();
            Time timeStartNextTask = MyDate.parseStringToTime(sTime);

            res = res || timeEnd.compareTo(timeStartNextTask) == 1;
        }

        return res;
    }
}