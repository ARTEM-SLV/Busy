package com.arty.busy.ui.customers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.OnFragmentCloseListener;
import com.arty.busy.R;
import com.arty.busy.consts.Constants;
import com.arty.busy.databinding.FragmentCustomersBinding;
import com.arty.busy.ui.customers.activity.CustomerActivity;
import com.arty.busy.ui.customers.adapters.CustomersAdapter;
import com.arty.busy.ui.customers.viewmodels.CustomersViewModel;

public class CustomersFragment extends Fragment implements OnFragmentCloseListener {
    private FragmentCustomersBinding binding;
    private CustomersViewModel customersViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        customersViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CustomersViewModel.class);

        binding = FragmentCustomersBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getContext();

        Bundle arguments = getArguments();
        int uid = -1;
        boolean isChoice = false;
        if (arguments != null){
            uid = arguments.getInt(Constants.ID_CUSTOMER, -1);
            isChoice = arguments.getBoolean("isChoice");
        }

        CustomersAdapter customersAdapter = new CustomersAdapter(context, uid, isChoice, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        final RecyclerView listOfCustomers = binding.customersListC;
        listOfCustomers.setLayoutManager(linearLayoutManager);
        listOfCustomers.setAdapter(customersAdapter);

        customersViewModel.getListOfCustomers().observe(getViewLifecycleOwner(), customersAdapter::updateListOfCustomers);

        if (isChoice) {
            binding.fabAddC.setImageResource(R.drawable.ic_back_24);
            binding.fabAddC.setOnClickListener(v -> {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        } else {
            binding.fabAddC.setImageResource(R.drawable.ic_add_24);
            binding.fabAddC.setOnClickListener(v -> {
                Intent intent = new Intent(context, CustomerActivity.class);
                if (context != null) {
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void closeFragment(Bundle result) {
        getParentFragmentManager().setFragmentResult("customer", result);
        requireActivity().getSupportFragmentManager().popBackStack(); // Закрываем фрагмент
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}