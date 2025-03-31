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
import com.arty.busy.ui.home.viewmodels.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        DateFormat df = new SimpleDateFormat("EEEE dd MMM.", Locale.getDefault());
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
//        setIdForBtnTask();
        setGoneAllBtnTask();
        setCurrTime();
        setListTasksToDay();
    }

    private void setPosStart(int time){
        posStart = time*360 + time*12;
    }

    private void setCurrTime(){
        currTime = DateTime.getTime(new Date());
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

            Button btnTask = getButtonHour(hour); // root.findViewById(1000 + hour);
            if (btnTask == null){
                continue;
            }

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

    private void setGoneAllBtnTask(){
        for (int hour = 0; hour < 24; hour++) {
            Button btnTask = getButtonHour(hour); //root.findViewById(1000 + hour);
            if (btnTask != null){
                btnTask.setVisibility(View.GONE);
            }
        }
    }

    private Button getButtonHour(int hour){
        switch (hour) {
            case 0: return binding.btnHour0TTD;
            case 1: return binding.btnHour1TTD;
            case 2: return binding.btnHour2TTD;
            case 3: return binding.btnHour3TTD;
            case 4: return binding.btnHour4TTD;
            case 5: return binding.btnHour5TTD;
            case 6: return binding.btnHour6TTD;
            case 7: return binding.btnHour7TTD;
            case 8: return binding.btnHour8TTD;
            case 9: return binding.btnHour9TTD;
            case 10: return binding.btnHour10TTD;
            case 11: return binding.btnHour11TTD;
            case 12: return binding.btnHour12TTD;
            case 13: return binding.btnHour13TTD;
            case 14: return binding.btnHour14TTD;
            case 15: return binding.btnHour15TTD;
            case 16: return binding.btnHour16TTD;
            case 17: return binding.btnHour17TTD;
            case 18: return binding.btnHour18TTD;
            case 19: return binding.btnHour19TTD;
            case 20: return binding.btnHour20TTD;
            case 21: return binding.btnHour21TTD;
            case 22: return binding.btnHour22TTD;
            case 23: return binding.btnHour23TTD;
            default: return null;
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