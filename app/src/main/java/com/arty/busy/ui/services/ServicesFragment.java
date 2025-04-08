package com.arty.busy.ui.services;

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
import com.arty.busy.databinding.FragmentServicesBinding;
import com.arty.busy.ui.customers.activity.CustomerActivity;
import com.arty.busy.ui.customers.adapters.CustomersAdapter;
import com.arty.busy.ui.services.activity.ServiceActivity;
import com.arty.busy.ui.services.adapters.ServicesAdapter;
import com.arty.busy.ui.services.viewmodels.ServicesViewModel;

public class ServicesFragment extends Fragment implements OnFragmentCloseListener {
    private FragmentServicesBinding binding;
    private ServicesViewModel servicesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        servicesViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ServicesViewModel.class);

        binding = FragmentServicesBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//        Context context = container.getContext();
//
//        Bundle arguments = getArguments();
//        int uid = -1;
//        boolean isChoice = false;
//        if (arguments != null){
//            uid = arguments.getInt(Constants.ID_SERVICE, -1);
//            isChoice = arguments.getBoolean("isChoice");
//        }
//
//        ServicesAdapter servicesAdapter = new ServicesAdapter(context, uid, isChoice, this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
//        final RecyclerView listOfServices = binding.servicesListS;
//        listOfServices.setLayoutManager(linearLayoutManager);
//        listOfServices.setAdapter(servicesAdapter);
//
//        servicesViewModel.getListOfServices().observe(getViewLifecycleOwner(), servicesAdapter::updateListOfServices);

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
            uid = arguments.getInt(Constants.ID_SERVICE, -1);
            isChoice = arguments.getBoolean("isChoice");
        }

        ServicesAdapter servicesAdapter = new ServicesAdapter(context, uid, isChoice, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        final RecyclerView listOfServices = binding.servicesListS;
        listOfServices.setLayoutManager(linearLayoutManager);
        listOfServices.setAdapter(servicesAdapter);

        servicesViewModel.getListOfServices().observe(getViewLifecycleOwner(), servicesAdapter::updateListOfServices);

        if (isChoice) {
            binding.fabAddS.setImageResource(R.drawable.ic_back_24);
            binding.fabAddS.setOnClickListener(v -> {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            });
        } else {
            binding.fabAddS.setImageResource(R.drawable.ic_add_24);
            binding.fabAddS.setOnClickListener(v -> {
                Intent intent = new Intent(context, ServiceActivity.class);
                if (context != null) {
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public void closeFragment(Bundle result) {
        getParentFragmentManager().setFragmentResult("service", result);
        requireActivity().getSupportFragmentManager().popBackStack(); // Закрываем фрагмент
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}