package com.arty.busy.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.Constants;
import com.arty.busy.databinding.FragmentHomeBinding;
import com.arty.busy.ui.home.adapters.ListOfDaysAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ListOfDaysAdapter listOfDaysAdapter;
    private Context context;
    private HomeViewModel homeViewModel;
    public boolean isLoadingUP = true, isLoadingDown = true;

    @SuppressLint("SimpleDateFormat")
    DateFormat df = new SimpleDateFormat("MMMM yyyy");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = container.getContext();

        listOfDaysAdapter = new ListOfDaysAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        final RecyclerView listOfDays = binding.homeListOfDays;
        listOfDays.setLayoutManager(linearLayoutManager);
        listOfDays.setAdapter(listOfDaysAdapter);
        getItemTouchHelper().attachToRecyclerView(listOfDays);

        homeViewModel.getListOfDays(0).observe(getViewLifecycleOwner(), listOfDaysAdapter::loadData);
        linearLayoutManager.scrollToPosition(14);

        listOfDays.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearLayoutManager.getChildCount();//смотрим сколько элементов на экране
                int totalItemCount = linearLayoutManager.getItemCount();//сколько всего элементов
                int firstVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();//какая позиция первого элемента

                if (dy<0 && firstVisibleItems == 0) {
                    if (!isLoadingUP && listOfDaysAdapter != null) {
                        homeViewModel.getListOfDays(Constants.DIRECTION_BACK).observe(getViewLifecycleOwner(), listOfDaysAdapter::addNewDataOnTop);
                        linearLayoutManager.scrollToPosition(12+visibleItemCount);
                        isLoadingUP = true;
                    }
                } else isLoadingUP = false;

                if (dy>0 && visibleItemCount + firstVisibleItems >= totalItemCount) {
                    if (!isLoadingDown && listOfDaysAdapter != null) {
                        homeViewModel.getListOfDays(Constants.DIRECTION_FORWARD).observe(getViewLifecycleOwner(), listOfDaysAdapter::addNewDataOnBot);
                        isLoadingDown = true;
                    }
                } else isLoadingDown = false;

//                Date d = listOfDaysAdapter.getDateFormListOfDaysArr(firstVisibleItems);
//                String formattedDate = df.format(d);
//
//                Toast toast = Toast.makeText(binding.homeListOfDays.getContext(), formattedDate, Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 0, 0);
//                toast.show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private ItemTouchHelper getItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                listOfDaysAdapter.removeElement(viewHolder.getAdapterPosition());
            }
        });
    }
}