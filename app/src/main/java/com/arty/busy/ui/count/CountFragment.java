package com.arty.busy.ui.count;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.databinding.FragmentCountBinding;
import com.arty.busy.databinding.FragmentCustomersBinding;

public class CountFragment extends Fragment {

    private FragmentCountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CountViewModel countViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(CountViewModel.class);

        binding = FragmentCountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCount;
        countViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}