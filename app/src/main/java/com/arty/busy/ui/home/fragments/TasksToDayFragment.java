package com.arty.busy.ui.home.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.arty.busy.App;
import com.arty.busy.Constants;
import com.arty.busy.R;
import com.arty.busy.Settings;
import com.arty.busy.date.MyDate;
import com.arty.busy.date.Time;
import com.arty.busy.ui.home.items.ItemTaskInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TasksToDayFragment extends Activity {
    List<ItemTaskInfo> taskInfoList;

    private long currDate;
    private TextView tvDate;
    private ScrollView scrollView;
    private ConstraintLayout constraintLayout;

    private int posStart;
    private int posY;

    private Time currTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_to_day);

        setPosStart(Settings.TIME_BEGINNING);
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

        setGoneAllBtnTask();

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

    private void setPosStart(int time){
        posStart = time*360 + time*12;
    }

    private void setCurrTime(){
        Date currDate = new Date();
        currTime = MyDate.getTime(currDate);
    }

    private void setListTasksToDay(){
        taskInfoList = App.getInstance().getBusyDao().getTasksInfoByDay(currDate);
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

        for (int i = 0; i < taskInfoList.size(); i++) {
            ItemTaskInfo itemTaskInfo = taskInfoList.get(i);

            duration = itemTaskInfo.getDuration();

            String sTimeStart = itemTaskInfo.getTime();
            timeStart = MyDate.parseStringToTime(sTimeStart);

            timeEnd = MyDate.parseStringToTime(sTimeStart);
            timeEnd.addTime(duration);
            String sTimeEnd = MyDate.parseTimeToString(timeEnd);

            hour = timeStart.getHour();
            minute = timeStart.getMinute();

            int currResColor = getColor(R.color.Black);
            if (itemTaskInfo.isDone()){
                currResColor = getColor(R.color.Green);
            } else if (isNextTask(timeStart)){
                currResColor = getColor(R.color.Navy);
                setPosStart(hour);
            }

            if (crossedTimesOfTasks(timeStart, timeEnd, i)) {
                currResColor = getColor(R.color.Brown);
            }

            @SuppressLint("DiscouragedApi")
            int btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", getPackageName());
            Button btnTask = findViewById(btnID);

            ConstraintLayout.LayoutParams btnParams = (ConstraintLayout.LayoutParams) btnTask.getLayoutParams();
            btnParams.setMargins(0, minute*6, 0, 0);
            btnTask.setLayoutParams(btnParams);
            btnTask.setHeight(duration*6);

            btnTask.setText(sTimeStart + " - " + sTimeEnd + "\n" + itemTaskInfo.getClient() + "\n" + itemTaskInfo.getServices());
            btnTask.setTextColor(currResColor);
            btnTask.setVisibility(View.VISIBLE);

            btnTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTask(itemTaskInfo);
                }
            });
        }
    }

    @SuppressLint("DiscouragedApi")
    private void setGoneAllBtnTask(){
        int btnID;

        for (int hour = 0; hour < 24; hour++) {
            btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", getPackageName());
            Button btnTask = findViewById(btnID);
            btnTask.setVisibility(View.GONE);
        }
    }

    private boolean isNextTask(Time time){
        boolean res = false;

        if (currDate == MyDate.getCurrentStartDate().getTime()) {
            for (ItemTaskInfo itemTaskInfo : taskInfoList) {
                String sTime = itemTaskInfo.getTime();
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
            ItemTaskInfo itemTaskInfo = taskInfoList.get(index-1);
            String sTime = itemTaskInfo.getTime();
            Time timeEndLastTask = MyDate.parseStringToTime(sTime);
            timeEndLastTask.addTime(itemTaskInfo.getDuration());

            res = timeStart.compareTo(timeEndLastTask) == -1;
        }
        if (index+1 < taskInfoList.size()){
            ItemTaskInfo itemTaskInfo = taskInfoList.get(index+1);
            String sTime = itemTaskInfo.getTime();
            Time timeStartNextTask = MyDate.parseStringToTime(sTime);

            res = res || timeEnd.compareTo(timeStartNextTask) == 1;
        }

        return res;
    }

    public void onAddClick(View v){
        openTask(null);
    }

    private void openTask(ItemTaskInfo itemTaskInfo){
        Intent intent = new Intent(TasksToDayFragment.this, TaskActivity.class);
        if (itemTaskInfo != null){
            intent.putExtra(Constants.ITEM_TASK_TO_DAY, itemTaskInfo);
        }
        startActivity(intent);
    }
}