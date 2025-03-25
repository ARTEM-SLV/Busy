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
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.arty.busy.ActivityForFragments;
import com.arty.busy.consts.Constants;
import com.arty.busy.R;
import com.arty.busy.consts.Settings;
import com.arty.busy.databinding.FragmentTasksToDayBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.date.Time;
import com.arty.busy.ui.home.HomeViewModel;
import com.arty.busy.ui.home.items.ItemTaskInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class TasksToDayFragment extends Fragment {
    private FragmentTasksToDayBinding binding;
    private Context context;
    private HomeViewModel homeViewModel;
    private View root;
    List<ItemTaskInfo> taskInfoList;
    private long currDate;
    private ScrollView scrollView;

    private int posStart;
    private int posY;
    private Time currTime;

    private ActivityResultLauncher<Intent> taskLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @SuppressLint("DiscouragedApi")
    private void init(){
        int lineID;
        LinearLayout linerHours;
        TextView tvDate = (TextView) binding.tvDateTTD; //findViewById(R.id.tvTestDate_TTD);
        scrollView = (ScrollView) binding.scrollTTD; //findViewById(R.id.scroll_TTD);

        scrollView.post(() -> scrollView.scrollTo(0, posStart));
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> posY = scrollView.getScrollY());

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEEE dd MMM.");
        Bundle arguments = getArguments();
        if (arguments != null) {
            currDate = arguments.getLong("Date");
            if (currDate != 0) {
                tvDate.setText(df.format(currDate));
            }
        }

        tvDate.setOnClickListener(v -> scrollView.smoothScrollBy(0, posStart - posY));

        FloatingActionButton btnAdd = (FloatingActionButton) binding.fabAddTTD;
        btnAdd.setOnClickListener(v -> openTask(null));

        if (Settings.TIME_BEGINNING != -1 || Settings.TIME_ENDING != -1) {
            for (int h = 0; h < 24; h++) {
                lineID = this.getResources().getIdentifier("hour" + (h) + "_TTD", "id", context.getPackageName());
                linerHours = root.findViewById(lineID);

                if (h == Settings.TIME_BEGINNING)
                    linerHours.setBackgroundResource(R.drawable.style_topline_salmon);

                if (h == Settings.TIME_ENDING)
                    linerHours.setBackgroundResource(R.drawable.style_bottomline_salmon);
            }
        }

        taskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> fillTasksData()
        );
    }

    private void setPosStart(int time){
//        posStart = time*360 + time*12;
        posStart = homeViewModel.getPosStartTasks(time);
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

        setGoneAllBtnTask();
        setCurrTime();
        setListTasksToDay();

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

            btnTask.setOnClickListener(v -> openTask(itemTaskInfo));
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

    private void openTask(ItemTaskInfo itemTaskInfo){
        Intent intent = new Intent(context, ActivityForFragments.class);
        if (itemTaskInfo != null){
            intent.putExtra(Constants.ID_TASK, itemTaskInfo.getId_task());
        } else {
            intent.putExtra(Constants.ID_TASK, -1);
        }
        intent.putExtra(Constants.KEY_DATE, currDate);
        taskLauncher.launch(intent);

//        Bundle bundle = new Bundle();
//        if (itemTaskInfo != null)
//            bundle.putInt(Constants.ID_TASK, itemTaskInfo.getId_task());
//        else bundle.putInt(Constants.ID_TASK, -1);
//        bundle.putLong(Constants.KEY_DATE, currDate);
//        getParentFragmentManager().setFragmentResult("bundle", bundle);
//        taskLauncher.launch(intent);

//
//        NavController navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment_activity_main);
//        navController.navigate(R.id.navigation_task, bundle);

//        getParentFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .add(R.id.container_for_fragments, TaskFragment.class, bundle)
//                .addToBackStack(null)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}