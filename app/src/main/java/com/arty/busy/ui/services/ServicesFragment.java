package com.arty.busy.ui.services;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.consts.Constants;
import com.arty.busy.databinding.FragmentServicesBinding;
import com.arty.busy.ui.services.adapters.ServicesAdapter;

public class ServicesFragment extends Fragment {

    private FragmentServicesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ServicesViewModel servicesViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ServicesViewModel.class);

        binding = FragmentServicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Context context = container.getContext();

        Bundle arguments = getArguments();
        int uid = -1;
        if (arguments != null){
            uid = arguments.getInt(Constants.ID_SERVICE, -1);
        }

        ServicesAdapter servicesAdapter = new ServicesAdapter(context, uid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        final RecyclerView listOfServices = binding.servicesListS;
        listOfServices.setLayoutManager(linearLayoutManager);
        listOfServices.setAdapter(servicesAdapter);

        servicesViewModel.getListOfServices().observe(getViewLifecycleOwner(), servicesAdapter::updateListOfServices);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}