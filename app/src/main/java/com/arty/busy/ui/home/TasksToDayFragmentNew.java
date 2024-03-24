package com.arty.busy.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.Constants;
import com.arty.busy.databinding.FragmentTasksToDayNewBinding;
import com.arty.busy.ui.home.adapters.TasksToDayAdapter;

public class TasksToDayFragmentNew extends Fragment {
    private FragmentTasksToDayNewBinding binding;
    private TasksToDayAdapter tasksToDayAdapter;
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

        tasksToDayAdapter = new TasksToDayAdapter(containerContext, getParentFragmentManager());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(containerContext, RecyclerView.VERTICAL, false);
        RecyclerView listTasksToDay = binding.listTasksToDay;
        listTasksToDay.setLayoutManager(linearLayoutManager);
        listTasksToDay.setAdapter(tasksToDayAdapter);

        Long date = 0l;
        Bundle args = getArguments();
        if (args != null){
            date = args.getLong(Constants.KEY_DATE);
        }
        listTasksToDay.scrollToPosition(homeViewModel.getPosStartListTasks(date));
        homeViewModel.getLiveListTasksToDay(date).observe(getViewLifecycleOwner(), tasksToDayAdapter::loadData);

        return root;
    }


}
