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

import com.arty.busy.OnFragmentCloseListener;
import com.arty.busy.consts.Constants;
import com.arty.busy.databinding.FragmentServicesBinding;
import com.arty.busy.ui.services.adapters.ServicesAdapter;

public class ServicesFragment extends Fragment implements OnFragmentCloseListener {

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

        return root;
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