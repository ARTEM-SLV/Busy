package com.arty.busy.ui.home.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.consts.Constants;
import com.arty.busy.databinding.FragmentTasksToDayNewBinding;
import com.arty.busy.ui.home.HomeViewModel;
import com.arty.busy.ui.home.adapters.EventsTasksToDayAdapter;
import com.arty.busy.ui.home.adapters.TimeLineTasksToDayAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TasksToDayFragmentNew extends Fragment {
    private FragmentTasksToDayNewBinding binding;
//    private FrameLayout buttonContainer;
//    private final List<Button> buttons = new ArrayList<>(); // Список кнопок
//    private RecyclerView timeLineTasksToDay;
    private TimeLineTasksToDayAdapter timeLineTasksToDayAdapter;
    private EventsTasksToDayAdapter eventsTasksToDayAdapter;
    private Context containerContext;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentTasksToDayNewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        containerContext = container.getContext();

//        buttonContainer = binding.buttonContainer;
        timeLineTasksToDayAdapter = new TimeLineTasksToDayAdapter(containerContext);
        eventsTasksToDayAdapter = new EventsTasksToDayAdapter(containerContext);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(containerContext, RecyclerView.VERTICAL, false);
        LinearLayoutManager linearLayoutManagerEvents = new LinearLayoutManager(containerContext, RecyclerView.VERTICAL, false);

        RecyclerView timeLineTasksToDay = binding.timeLineTasksToDay;
        RecyclerView eventsTasksToDay = binding.eventsTasksToDay;

        timeLineTasksToDay.setLayoutManager(linearLayoutManager);
        timeLineTasksToDay.setAdapter(timeLineTasksToDayAdapter);

        eventsTasksToDay.setLayoutManager(linearLayoutManagerEvents);
        eventsTasksToDay.setAdapter(eventsTasksToDayAdapter);

//        // Связываем прокрутку
//        syncScroll(timeLineTasksToDay, eventsTasksToDay);

        Long date = 0l;
        Bundle args = getArguments();
        if (args != null){
            date = args.getLong(Constants.KEY_DATE);
        }
        timeLineTasksToDay.scrollToPosition(homeViewModel.getPosStartListTasks(date));
        homeViewModel.getLiveListTasksToDay(date).observe(getViewLifecycleOwner(), timeLineTasksToDayAdapter::loadData);
        eventsTasksToDay.scrollToPosition(homeViewModel.getPosStartListTasks(date));
        homeViewModel.getLiveListTasksToDay(date).observe(getViewLifecycleOwner(), eventsTasksToDayAdapter::loadData);

        syncScroll(timeLineTasksToDay, eventsTasksToDay);

//        // Связываем прокрутку списков
//        timeLineTasksToDay.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                eventsTasksToDay.scrollBy(dx, dy);
//            }
//        });
//        eventsTasksToDay.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                timeLineTasksToDay.scrollBy(dx, dy);
//            }
//        });

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("EEEE dd MMM.");
        TextView tvDate = (TextView) binding.tvDateTTD;
        tvDate.setText(df.format(date));

        return root;
    }

    // Создаем слушатель для синхронизации прокрутки
    private void syncScroll(RecyclerView primary, RecyclerView secondary) {
        primary.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                secondary.removeOnScrollListener(this); // Убираем слушатель, чтобы избежать зацикливания
                secondary.scrollBy(dx, dy);
                secondary.addOnScrollListener(this); // Возвращаем слушатель
            }
        });

        secondary.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                primary.removeOnScrollListener(this);
                primary.scrollBy(dx, dy);
                primary.addOnScrollListener(this);
            }
        });
    }
}
