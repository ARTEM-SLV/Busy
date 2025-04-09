package com.arty.busy.ui.services;

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
import com.arty.busy.databinding.FragmentServicesBinding;
import com.arty.busy.ui.services.activity.ServiceActivity;
import com.arty.busy.ui.services.adapters.ServicesAdapter;
import com.arty.busy.ui.services.viewmodels.ServicesViewModel;

public class ServicesFragment extends Fragment implements OnFragmentCloseListener {
    private FragmentServicesBinding binding;
    private ServicesViewModel servicesViewModel;
    private ServicesAdapter servicesAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        servicesViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ServicesViewModel.class);

        binding = FragmentServicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Context context = root.getContext();

        Bundle arguments = getArguments();
        int uid = -1;
        boolean isChoice = false;
        if (arguments != null){
            uid = arguments.getInt(Constants.ID_SERVICE, -1);
            isChoice = arguments.getBoolean("isChoice");
        }

        servicesAdapter = new ServicesAdapter(context, uid, isChoice, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        final RecyclerView listOfServices = binding.servicesListS;
        listOfServices.setLayoutManager(linearLayoutManager);
        listOfServices.setAdapter(servicesAdapter);

        if (isChoice){
            binding.btnBackS.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
            binding.btnBackS.setVisibility(View.VISIBLE);
        } else {
            binding.btnBackS.setVisibility(View.GONE);
        }

        binding.fabAddS.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(requireActivity());
            Intent intent = new Intent(context, ServiceActivity.class);
            if (context != null) {
                context.startActivity(intent);
            }
        });

        setOnTouchListenerForCustomersList();
        setTextChangedListenerFotSearch(servicesAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        servicesViewModel.getListOfServices().observe(getViewLifecycleOwner(), servicesAdapter::updateListOfServices);
        servicesAdapter.filter("");

        if (servicesAdapter.getItemCount() == 0) {
            binding.tvEmptyS.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmptyS.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListenerForCustomersList() {
        binding.servicesListS.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                App.hideKeyboardAndClearFocus(requireActivity());

                v.performClick();
            }
            return false;
        } );
    }

    private void setTextChangedListenerFotSearch(ServicesAdapter servicesAdapter){
        binding.etSearchS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                servicesAdapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
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