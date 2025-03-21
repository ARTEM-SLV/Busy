package com.arty.busy.ui.home;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.consts.Constants;
import com.arty.busy.databinding.FragmentHomeBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.ui.home.adapters.ListOfDaysAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private RecyclerView listOfDays;
    private FragmentHomeBinding binding;
    private ListOfDaysAdapter listOfDaysAdapter;
    private HomeViewModel homeViewModel;
    public boolean isLoadingUP = true, isLoadingDown = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listOfDaysAdapter = new ListOfDaysAdapter(getContext(), getParentFragmentManager(), getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        listOfDays = binding.homeListOfDays;
        listOfDays.setLayoutManager(linearLayoutManager);
        listOfDays.setAdapter(listOfDaysAdapter);
        getItemTouchHelper().attachToRecyclerView(listOfDays);

        Date d = listOfDaysAdapter.getSelectedDate();
        homeViewModel.getListOfDays(d).observe(getViewLifecycleOwner(), listOfDaysAdapter::loadData);

        addOnScrollListenerRecyclerView(linearLayoutManager);

        return root;
    }

    private void addOnScrollListenerRecyclerView(LinearLayoutManager linearLayoutManager){
        listOfDays.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int positionFirstElement = linearLayoutManager.findFirstVisibleItemPosition(); // позиция первого элемента
                int visibleItemCount = linearLayoutManager.getChildCount(); //  сколько элементов на экране
                int totalItemCount = linearLayoutManager.getItemCount(); // сколько всего элементов

                if (dy<0 && positionFirstElement == 0) {
                    if (!isLoadingUP && listOfDaysAdapter != null) {
                        homeViewModel.getListOfDays(Constants.DIRECTION_BACK).observe(getViewLifecycleOwner(), listOfDaysAdapter::addNewDataOnTop);
                        linearLayoutManager.scrollToPosition(12+visibleItemCount);
                        isLoadingUP = true;
                    }
                } else isLoadingUP = false;

                if (dy>0 && visibleItemCount + positionFirstElement >= totalItemCount) {
                    if (!isLoadingDown && listOfDaysAdapter != null) {
                        homeViewModel.getListOfDays(Constants.DIRECTION_FORWARD).observe(getViewLifecycleOwner(), listOfDaysAdapter::addNewDataOnBot);
                        isLoadingDown = true;
                    }
                } else isLoadingDown = false;

//                Date d = listOfDaysAdapter.getDate(positionFirstElement);
//                Calendar calendar = DateTime.getCalendar(d);
//                if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
//                    String monthName = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(d);
//                    Toast toast = Toast.makeText(recyclerView.getContext(), monthName, Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.TOP, 0, 200);
//                    toast.show();
//                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        listOfDays.getLayoutManager().scrollToPosition(11);
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