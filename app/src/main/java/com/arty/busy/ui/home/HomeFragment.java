package com.arty.busy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.consts.Constants;
import com.arty.busy.databinding.FragmentHomeBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.ui.home.adapters.ListOfDaysAdapter;
import com.arty.busy.ui.home.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    private RecyclerView rvListOfDays;
    private FragmentHomeBinding binding;
    private ListOfDaysAdapter listOfDaysAdapter;
    LinearLayoutManager linearLayoutManager;
    private HomeViewModel homeViewModel;
    public boolean isLoadingUP = true, isLoadingDown = true;
    private int visibleItemCount, totalItemCount;
    private int firstVisible, lastVisibleItem;
    private int currPos, pos;
    private Date currDate = DateTime.getCurrentStartDate();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listOfDaysAdapter = new ListOfDaysAdapter(getContext(), getParentFragmentManager(), getActivity());
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvListOfDays = binding.homeListOfDays;
        rvListOfDays.setLayoutManager(linearLayoutManager);
        rvListOfDays.setAdapter(listOfDaysAdapter);

        addOnScrollListenerRecyclerView(linearLayoutManager);

//        homeViewModel.getListOfDays(currDate).observe(getViewLifecycleOwner(), listOfDaysAdapter::loadData);
//        rvListOfDays.post(() -> scrollToPositionListOfDays(currDate,false));

        binding.fbGoToCurrDate.setOnClickListener(v -> scrollToPositionListOfDays(currDate,true));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        homeViewModel.createListOfDate();

//        int position = 11;
//        currPos = listOfDaysAdapter.getCurrPosition();

        Date d = listOfDaysAdapter.getSelectedDate();
        homeViewModel.getListOfDays(d).observe(getViewLifecycleOwner(), listOfDaysAdapter::loadData);

        rvListOfDays.post(() -> scrollToPositionListOfDays(d,false));
    }

    private void scrollToPositionListOfDays(Date d, boolean smooth){
        int firstVisible = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = linearLayoutManager.findLastVisibleItemPosition();
        int visibleItemCount = linearLayoutManager.getChildCount();
        pos = findPosForDate(d.getTime(), firstVisible, lastVisible, visibleItemCount);
        if (pos != -1){
            if (smooth){
                rvListOfDays.smoothScrollToPosition(pos);
            } else {
                rvListOfDays.scrollToPosition(pos);
            }
        }
    }

    private int findPosForDate(long targetDate, int firstVisible, int lastVisibleItem, int visibleItemCount) {
        int pos;

        ArrayList<Long> listOfDates = homeViewModel.getListOfDate();
        pos = listOfDates.indexOf(targetDate);

        if (pos == -1) {
            return pos;
        }

        int halfScreen = 3; //visibleItemCount / 2;
        if (firstVisible > pos){
            halfScreen *= -1;
//            halfScreen++;
            pos += halfScreen;
        } else if (lastVisibleItem < pos){
//            halfScreen--;
            pos += halfScreen;
        }

        return pos;
    }

    private void addOnScrollListenerRecyclerView(LinearLayoutManager linearLayoutManager){
        rvListOfDays.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisible = linearLayoutManager.findFirstVisibleItemPosition(); // позиция первого элемента
                int visibleItemCount = linearLayoutManager.getChildCount(); //  сколько элементов на экране
                int totalItemCount = linearLayoutManager.getItemCount(); // сколько всего элементов

                if (dy<0 && firstVisible == 0) {
                    if (!isLoadingUP && listOfDaysAdapter != null) {
                        homeViewModel.getListOfDays(Constants.DIRECTION_BACK).observe(getViewLifecycleOwner(), listOfDaysAdapter::addNewDataOnTop);
                        linearLayoutManager.scrollToPosition(12+ visibleItemCount);
                        isLoadingUP = true;
                    }
                } else isLoadingUP = false;

                if (dy>0 && visibleItemCount + firstVisible >= totalItemCount) {
                    if (!isLoadingDown && listOfDaysAdapter != null) {
                        homeViewModel.getListOfDays(Constants.DIRECTION_FORWARD).observe(getViewLifecycleOwner(), listOfDaysAdapter::addNewDataOnBot);
                        isLoadingDown = true;
                    }
                } else isLoadingDown = false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}