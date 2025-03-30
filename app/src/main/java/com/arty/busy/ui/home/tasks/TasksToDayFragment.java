package com.arty.busy.ui.home.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.ActivityForFragments;
import com.arty.busy.consts.Constants;
import com.arty.busy.R;
import com.arty.busy.consts.Settings;
import com.arty.busy.databinding.FragmentTasksToDayBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.date.Time;
import com.arty.busy.ui.home.items.ItemTaskInfo;
import com.arty.busy.ui.home.viewmodel.HomeViewModel;
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
    private long currDate;
    private int scrollY;

    private int posStart;
    private int posY;
    private Time currTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentTasksToDayBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getContext();

        binding.btnBackTTD.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        init();
        setData();
        fillTasksData();
    }

    @SuppressLint("DiscouragedApi")
    private void init(){
        int lineID;
        LinearLayout linerHours;
        ScrollView scrollView = binding.scrollTTD;

        if (scrollY > 0){
            scrollView.post(() -> scrollView.scrollTo(0, scrollY));
        } else {
            scrollView.post(() -> scrollView.scrollTo(0, posStart));
        }
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> posY = scrollView.getScrollY());

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEEE dd MMM.");
        Bundle arguments = getArguments();
        if (arguments != null) {
            currDate = arguments.getLong(Constants.KEY_DATE);
            if (currDate != 0) {
                binding.tvDateTTD.setText(df.format(currDate));
            }
        }
        binding.tvDateTTD.setOnClickListener(v -> scrollView.smoothScrollBy(0, posStart - posY));

        FloatingActionButton btnAdd = binding.fabAddTTD;
        btnAdd.setOnClickListener(v -> openTask(-1, "00:00"));

        for (byte h = 0; h < 24; h++) {
            lineID = this.getResources().getIdentifier("hour" + (h) + "_TTD", "id", context.getPackageName());
            linerHours = root.findViewById(lineID);

            if (h == Settings.TIME_BEGINNING)
                linerHours.setBackgroundResource(R.drawable.style_topline_salmon);

            if (h == Settings.TIME_ENDING)
                linerHours.setBackgroundResource(R.drawable.style_bottomline_salmon);
        }
    }

    private void setData(){
        setPosStart(Settings.TIME_BEGINNING);
        setIdForBtnTask();
        setGoneAllBtnTask();
        setCurrTime();
        setListTasksToDay();
    }

    private void setPosStart(int time){
        posStart = time*360 + time*12;
    }

    private void setCurrTime(){
        currTime = homeViewModel.getCurrentTime();
    }

    private void setListTasksToDay(){
        taskInfoList = homeViewModel.getListTasksToDay(currDate);
    }

    @SuppressLint("SetTextI18n")
    private void fillTasksData(){
        Time    timeStart;
        Time    timeEnd;
        int     duration;
        byte    hour;
        byte    minute;

        for (int i = 0; i < taskInfoList.size(); i++) {
            ItemTaskInfo itemTaskInfo = taskInfoList.get(i);

            duration = itemTaskInfo.getDuration();

            String sTimeStart = itemTaskInfo.getTime();
            timeStart = DateTime.parseStringToTime(sTimeStart);

            timeEnd = DateTime.parseStringToTime(sTimeStart);
            timeEnd.addTime(duration);
            String sTimeEnd = DateTime.parseTimeToString(timeEnd);

            hour = timeStart.getHour();
            minute = timeStart.getMinute();

            int currResColor = context.getColor(R.color.Black);
            if (itemTaskInfo.isDone()){
                currResColor = context.getColor(R.color.DarkGreen);
            } else if (isNextTask(timeStart)){
                currResColor = context.getColor(R.color.Navy);
                setPosStart(hour);
            }

            if (crossedTimesOfTasks(timeStart, timeEnd, i)) {
                currResColor = context.getColor(R.color.Brown);
            }

            Button btnTask = root.findViewById(hour);

            btnTask.setOnClickListener(v -> {
                openTask(itemTaskInfo.getId_task(), itemTaskInfo.getTime());
                scrollY = binding.scrollTTD.getScrollY(); // Текущая прокрутка ScrollView
            });
            btnTask.setText(sTimeStart + " - " + sTimeEnd + "\n" + itemTaskInfo.getClient() + "\n" + itemTaskInfo.getServices());
            btnTask.setTextColor(currResColor);

            ConstraintLayout.LayoutParams btnParams = (ConstraintLayout.LayoutParams) btnTask.getLayoutParams();
            btnParams.setMargins(0, minute*6, 0, 0);
            btnTask.setLayoutParams(btnParams);
            btnTask.setHeight(duration*6);

            btnTask.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("ResourceType")
    private void setIdForBtnTask(){
        binding.btnHour0TTD.setId(0);
        binding.btnHour1TTD.setId(1);
        binding.btnHour2TTD.setId(2);
        binding.btnHour3TTD.setId(3);
        binding.btnHour4TTD.setId(4);
        binding.btnHour5TTD.setId(5);
        binding.btnHour6TTD.setId(6);
        binding.btnHour7TTD.setId(7);
        binding.btnHour8TTD.setId(8);
        binding.btnHour9TTD.setId(9);
        binding.btnHour10TTD.setId(10);
        binding.btnHour11TTD.setId(11);
        binding.btnHour12TTD.setId(12);
        binding.btnHour13TTD.setId(13);
        binding.btnHour14TTD.setId(14);
        binding.btnHour15TTD.setId(15);
        binding.btnHour16TTD.setId(16);
        binding.btnHour17TTD.setId(17);
        binding.btnHour18TTD.setId(18);
        binding.btnHour19TTD.setId(19);
        binding.btnHour20TTD.setId(20);
        binding.btnHour21TTD.setId(21);
        binding.btnHour22TTD.setId(22);
        binding.btnHour23TTD.setId(23);
    }

    private void setGoneAllBtnTask(){
        for (int hour = 0; hour < 24; hour++) {
            Button btnTask = root.findViewById(hour);
            btnTask.setVisibility(View.GONE);
        }
    }

    private boolean isNextTask(Time time){
        boolean res = false;

        if (currDate == DateTime.getCurrentStartDate().getTime()) {
            for (ItemTaskInfo itemTaskInfo : taskInfoList) {
                String sTime = itemTaskInfo.getTime();
                Time t = DateTime.parseStringToTime(sTime);

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
            Time timeEndLastTask = DateTime.parseStringToTime(sTime);
            timeEndLastTask.addTime(itemTaskInfo.getDuration());

            res = timeStart.compareTo(timeEndLastTask) == -1;
        }
        if (index+1 < taskInfoList.size()){
            ItemTaskInfo itemTaskInfo = taskInfoList.get(index+1);
            String sTime = itemTaskInfo.getTime();
            Time timeStartNextTask = DateTime.parseStringToTime(sTime);

            res = res || timeEnd.compareTo(timeStartNextTask) == 1;
        }

        return res;
    }

    private void openTask(int id_task, String time){
        Intent intent = new Intent(context, ActivityForFragments.class);
        intent.putExtra(Constants.KEY_DATE, currDate);
        intent.putExtra(Constants.ID_TASK, id_task);
        intent.putExtra(Constants.KEY_TIME, time);

        startActivity(intent);

//        taskLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}