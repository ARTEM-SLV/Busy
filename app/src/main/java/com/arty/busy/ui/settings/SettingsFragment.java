package com.arty.busy.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.databinding.FragmentCustomersBinding;
import com.arty.busy.databinding.FragmentSettingsBinding;
import com.arty.busy.models.Task;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        setOnClickListenerTestBtn();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setOnClickListenerTestBtn(){
        Button testBtn = binding.testBtn;
        testBtn.setOnClickListener(v -> {
            EditText testEditText = binding.etTestTaskID;
            String text = testEditText.getText().toString();
            text = text.replace("", "0");
            int uid = Integer.parseInt(text);

            Task task = settingsViewModel.testGetTaskByID(uid);
            if (task != null){
                Log.d("Task", task.toString());
            }
        });
    }
}