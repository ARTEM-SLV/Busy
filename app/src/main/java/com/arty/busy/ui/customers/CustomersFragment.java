package com.arty.busy.ui.customers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.Constants;
import com.arty.busy.databinding.FragmentCustomersBinding;
import com.arty.busy.models.Customer;
import com.arty.busy.ui.customers.adapters.CustomersAdapter;
import com.arty.busy.ui.services.adapters.ServicesAdapter;

public class CustomersFragment extends Fragment {

    private FragmentCustomersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CustomersViewModel customersViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(CustomersViewModel.class);

        binding = FragmentCustomersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Context context = container.getContext();

        Bundle arguments = getArguments();
        int uid = -1;
        if (arguments != null){
            uid = arguments.getInt(Constants.ID_CUSTOMER, -1);
        }

        CustomersAdapter customersAdapter = new CustomersAdapter(context, uid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        final RecyclerView listOfCustomers = binding.customersListC;
        listOfCustomers.setLayoutManager(linearLayoutManager);
        listOfCustomers.setAdapter(customersAdapter);

        customersViewModel.getListOfCustomers().observe(getViewLifecycleOwner(), customersAdapter::updateListOfCustomers);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}