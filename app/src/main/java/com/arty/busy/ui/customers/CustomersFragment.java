package com.arty.busy.ui.customers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.App;
import com.arty.busy.OnFragmentCloseListener;
import com.arty.busy.consts.Constants;
import com.arty.busy.databinding.FragmentCustomersBinding;
import com.arty.busy.ui.customers.activity.CustomerActivity;
import com.arty.busy.ui.customers.adapters.CustomersAdapter;
import com.arty.busy.ui.customers.viewmodels.CustomersViewModel;

public class CustomersFragment extends Fragment implements OnFragmentCloseListener {
    private FragmentCustomersBinding binding;
    private CustomersViewModel customersViewModel;
    private CustomersAdapter customersAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        customersViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CustomersViewModel.class);

        binding = FragmentCustomersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context context = root.getContext();

        Bundle arguments = getArguments();
        int uid = -1;
        boolean isChoice = false;
        if (arguments != null){
            uid = arguments.getInt(Constants.ID_CUSTOMER, -1);
            isChoice = arguments.getBoolean("isChoice");
        }

        customersAdapter = new CustomersAdapter(context, uid, isChoice, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        final RecyclerView listOfCustomers = binding.customersListC;
        listOfCustomers.setLayoutManager(linearLayoutManager);
        listOfCustomers.setAdapter(customersAdapter);

        if (isChoice){
            binding.btnBackC.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
            binding.btnBackC.setVisibility(View.VISIBLE);
        } else {
            binding.btnBackC.setVisibility(View.INVISIBLE);
        }

        binding.btnAddC.setOnClickListener(v -> {
            // TODO: 11.04.2025
        });

        binding.fabAddC.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(requireActivity());
            Intent intent = new Intent(context, CustomerActivity.class);
            if (context != null) {
                context.startActivity(intent);
            }
        });

        setOnTouchListenerForCustomersList();
        setTextChangedListenerFotSearch(customersAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        customersViewModel.getListOfCustomers().observe(getViewLifecycleOwner(), customersAdapter::updateListOfCustomers);
        customersAdapter.filter("");

        if (customersAdapter.getItemCount() == 0) {
            binding.tvEmptyC.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmptyC.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListenerForCustomersList() {
        binding.customersListC.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                App.hideKeyboardAndClearFocus(requireActivity());

                v.performClick();
            }
            return false;
        } );
    }

    private void setTextChangedListenerFotSearch(CustomersAdapter customersAdapter){
        binding.etSearchC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customersAdapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
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