package com.arty.busy.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.databinding.FragmentHomeBinding;
import com.arty.busy.ui.home.adapters.ListOfDaysAdapter;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ListOfDaysAdapter listOfDaysAdapter;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = container.getContext();
        listOfDaysAdapter = new ListOfDaysAdapter(context);

        final RecyclerView listOfDays = binding.homeListOfDays;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        listOfDays.setLayoutManager(linearLayoutManager);
        getItemTouchHelper().attachToRecyclerView(listOfDays);
        listOfDays.setAdapter(listOfDaysAdapter);

        homeViewModel.getListOfDays().observe(getViewLifecycleOwner(), listOfDaysAdapter::updateAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private ItemTouchHelper getItemTouchHelper(){
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.ANIMATION_TYPE_SWIPE_CANCEL) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
    }
}