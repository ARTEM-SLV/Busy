package com.arty.busy.ui.home.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.ActivityForFragments;
import com.arty.busy.R;
import com.arty.busy.consts.Constants;
import com.arty.busy.consts.Settings;
import com.arty.busy.databinding.FragmentTasksToDayNewBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.date.Time;
import com.arty.busy.ui.home.viewmodel.HomeViewModel;
import com.arty.busy.ui.home.items.ItemTaskInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TasksToDayFragmentNew extends Fragment {
    private FragmentTasksToDayNewBinding binding;
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
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentTasksToDayNewBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        context = getContext();

//        setPosStart(Settings.TIME_BEGINNING);
//        init();
//        fillTasksData();

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
//        int lineID;
//        LinearLayout linerHours;
        scrollView = binding.scrollTTD;

        scrollView.post(() -> scrollView.scrollTo(0, posStart));
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

        int previousViewId = ConstraintLayout.LayoutParams.PARENT_ID;
        Time time = new Time();
        byte m = 0;
        for (byte h = 0; h < 24; h++) {
//            lineID = this.getResources().getIdentifier("hour" + (h) + "_TTD", "id", context.getPackageName());
//            linerHours = root.findViewById(lineID);
//
//            if (h == Settings.TIME_BEGINNING)
//                linerHours.setBackgroundResource(R.drawable.style_topline_salmon);
//
//            if (h == Settings.TIME_ENDING)
//                linerHours.setBackgroundResource(R.drawable.style_bottomline_salmon);

            View itemView = LayoutInflater.from(context).inflate(R.layout.item_task_to_day, binding.constraintTTD, false);
            itemView.setId(h);

            if (h == Settings.TIME_BEGINNING)
                itemView.setBackgroundResource(R.drawable.style_topline_salmon);

            if (h == Settings.TIME_ENDING)
                itemView.setBackgroundResource(R.drawable.style_bottomline_salmon);

            time.setTime(h, m);
            String sTime = time.getTime();

            TextView textView = itemView.findViewById(R.id.tvHour_TTD);
            textView.setText(time.toString());

            LinearLayout layoutHour = itemView.findViewById(R.id.layoutHour_TTD);
            layoutHour.setOnClickListener(v -> openTask(-1, sTime));

            LinearLayout layoutHalfHour = itemView.findViewById(R.id.layoutHalfHour_TTD);
            layoutHalfHour.setOnClickListener(v -> {
                time.addTime(30);
                openTask(-1, time.toString());
            });

            // Добавляем элемент в ScrollView
            binding.constraintTTD.addView(itemView);

            // Настраиваем ConstraintSet для правильного расположения элементов
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(binding.constraintTTD);

            if (previousViewId == ConstraintLayout.LayoutParams.PARENT_ID) {
                // Первый элемент привязываем к верху родительского контейнера
                constraintSet.connect(itemView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16);
            } else {
                // Все последующие элементы привязываем к предыдущему
                constraintSet.connect(itemView.getId(), ConstraintSet.TOP, previousViewId, ConstraintSet.BOTTOM, 16);
            }

            constraintSet.connect(itemView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(itemView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.applyTo(binding.constraintTTD);

            // Сохраняем ID текущего элемента для привязки следующего
            previousViewId = itemView.getId();
        }


//        taskLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//                result -> fillTasksData()
//        );
    }

    private void setData() {
        setPosStart(Settings.TIME_BEGINNING);
        setCurrTime();
        setListTasksToDay();
    }

    private void setPosStart(int time){
        posStart = time*360 + time*12;
//        posStart = homeViewModel.getPosStartTasks(time);
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

//        setGoneAllBtnTask();
//        setCurrTime();
//        setListTasksToDay();

        int previousButtonId = -1; // Храним ID предыдущей кнопки
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

            LinearLayout layoutHour = binding.constraintTTD.findViewById(hour);
            if (layoutHour == null) {
                Log.d("fillTasksData", "LinearLayout not found by Id " + hour);
                continue;
            }

//            @SuppressLint("DiscouragedApi")
//            int btnID = this.getResources().getIdentifier("btnHour" + (hour) + "_TTD", "id", context.getPackageName());
            Button btnTask = new Button(context); //root.findViewById(btnID);
            int btnId = View.generateViewId();
            btnTask.setId(btnId);

            btnTask.setOnClickListener(v -> openTask(itemTaskInfo.getId_task(), itemTaskInfo.getTime()));
            btnTask.setText(sTimeStart + " - " + sTimeEnd + "\n" + itemTaskInfo.getClient() + "\n" + itemTaskInfo.getServices());
            btnTask.setTextColor(currResColor);
            btnTask.setBackgroundResource(R.drawable.style_frame_azure);

//            ConstraintLayout.LayoutParams btnParams = (ConstraintLayout.LayoutParams) btnTask.getLayoutParams();
//            btnParams.setMargins(0, minute*6, 0, 0);
//            btnTask.setLayoutParams(btnParams);
//            btnTask.setHeight(duration*6);

            binding.constraintTTD.addView(btnTask);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(binding.constraintTTD);
//            constraintSet.connect(btnTask.getId(), ConstraintSet.TOP, layoutHour.getId(), ConstraintSet.BOTTOM);
            if (previousButtonId == -1) {
                // Первая кнопка привязывается к layoutHour
                constraintSet.connect(btnTask.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 16);
            } else {
                // Следующие кнопки привязываются к предыдущей кнопке
                constraintSet.connect(btnTask.getId(), ConstraintSet.TOP, previousButtonId, ConstraintSet.BOTTOM, 16);
            }

            constraintSet.connect(btnTask.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(btnTask.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.applyTo(binding.constraintTTD);

            previousButtonId = btnId;
//            btnTask.setVisibility(View.VISIBLE);
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

    private void openTask(int id_task, String time){
        Intent intent = new Intent(context, ActivityForFragments.class);
        intent.putExtra(Constants.KEY_DATE, currDate);
        intent.putExtra(Constants.ID_TASK, id_task);
        intent.putExtra(Constants.KEY_TIME, time);

        taskLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}