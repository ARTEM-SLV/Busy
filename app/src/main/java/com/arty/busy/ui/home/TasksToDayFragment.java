package com.arty.busy.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.ActivityForFragments;
import com.arty.busy.Constants;
import com.arty.busy.R;
import com.arty.busy.Settings;
import com.arty.busy.databinding.FragmentTasksToDayBinding;
import com.arty.busy.date.MyDate;
import com.arty.busy.date.Time;
import com.arty.busy.ui.home.items.ItemTaskInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TasksToDayFragment extends Fragment {
    private FragmentTasksToDayBinding binding;
    private Context context;
    private HomeViewModel homeViewModel;
    private View root;

    List<ItemTaskInfo> taskInfoList;

    private long currDateL;
    private TextView tvDate;
    private ScrollView scrollView;
    private ConstraintLayout constraintLayout;

    private int posStart;
    private int posY;

    private Time currTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tasks_to_day);

//        setPosStart(Settings.TIME_BEGINNING);
//        init();
//        fillTasksData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentTasksToDayBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getContext();

        setPosStart(Settings.TIME_BEGINNING);
        init();
        fillTasksData();

        return root;
    }

    private void init(){
        int lineID;
        LinearLayout linerHours;
        tvDate = (TextView) binding.tvDateTTD; //findViewById(R.id.tvTestDate_TTD);
        constraintLayout = (ConstraintLayout) binding.constraintTTD; //findViewById(R.id.constraint_TTD);
        scrollView = (ScrollView) binding.scrollTTD; //findViewById(R.id.scroll_TTD);
//        taskInfoList = new ArrayList<>();

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
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            currDate = extras.getLong("Date");
//            tvDate.setText(df.format(currDate));
//        }
        //        currDateL = requireArguments().getLong("Date");
        Bundle args = getArguments();
        if (args != null) {
            currDateL = args.getLong("Date");
            if (currDateL != 0) {
                tvDate.setText(df.format(currDateL));
            }
        }

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollBy(0, posStart - posY);
            }
        });

        FloatingActionButton btnAdd = (FloatingActionButton) binding.fabAddTTD;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTask(null);
            }
        });

        if (Settings.TIME_BEGINNING != -1 || Settings.TIME_ENDING != -1) {
            for (int h = 0; h < 24; h++) {
                lineID = this.getResources().getIdentifier("hour" + (h) + "_TTD", "id", context.getPackageName());
                linerHours = root.findViewById(lineID);

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
//        posStart = time*360 + time*12;
        posStart = homeViewModel.getPosStartTasks(time);
    }

    private void setCurrTime(){
//        currTime = MyDate.getTime(new Date());
        currTime = homeViewModel.getCurrentTime();
    }

    private void setListTasksToDay(){
//        taskInfoList = App.getInstance().getBusyDao().getTasksInfoByDay(currDateL);
        taskInfoList = homeViewModel.getListTasksToDay(currDateL);
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

            int currResColor = context.getColor(R.color.Black);
            if (itemTaskInfo.isDone()){
                currResColor = context.getColor(R.color.Green);
            } else if (isNextTask(timeStart)){
                currResColor = context.getColor(R.color.Navy);
                setPosStart(hour);
            }

            if (crossedTimesOfTasks(timeStart, timeEnd, i)) {
                currResColor = context.getColor(R.color.Brown);
            }

            @SuppressLint("DiscouragedApi")
            int btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", context.getPackageName());
            Button btnTask = root.findViewById(btnID);

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
            btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", context.getPackageName());
            Button btnTask = root.findViewById(btnID);
            btnTask.setVisibility(View.GONE);
        }
    }

    private boolean isNextTask(Time time){
        boolean res = false;

        if (currDateL == MyDate.getCurrentStartDate().getTime()) {
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

    private void openTask(ItemTaskInfo itemTaskInfo){
        Intent intent = new Intent(context, ActivityForFragments.class);
        if (itemTaskInfo != null){
            intent.putExtra(Constants.ID_TASK, itemTaskInfo.getId_task());
        }
        startActivity(intent);

//        Bundle bundle = new Bundle();
//        if (itemTaskInfo != null)
//            bundle.putInt(Constants.ID_TASK, itemTaskInfo.getId_task());
//         else bundle.putInt(Constants.ID_TASK, -1);
//
//        getParentFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .add(R.id.nav_host_fragment_activity_main, TaskFragment.class, bundle)
//                .addToBackStack(null)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .commit();
    }
}