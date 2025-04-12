package com.arty.busy.ui.services.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.App;
import com.arty.busy.R;
import com.arty.busy.databinding.ActivityServiceBinding;
import com.arty.busy.date.Time;
import com.arty.busy.models.Service;
import com.arty.busy.ui.services.viewmodels.ServiceViewModel;

import java.util.Locale;

public class ServiceActivity extends AppCompatActivity {
    private ServiceViewModel serviceViewModel;
    private ActivityServiceBinding binding;
    private Service service, modifiedService;
    private boolean isNew = false;
    private boolean isCreating = true;
    private TextWatcher textWatcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        serviceViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(ServiceViewModel.class);

        binding = ActivityServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setWindowParam();

        init();
        setData();
        setView();
        setOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isCreating){
            isCreating = false;
        }
    }

    private void setWindowParam(){
        // Получаем параметры окна
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());

            // Устанавливаем ширину и высоту (50% ширины и 30% высоты экрана)
            layoutParams.width = (int) (getScreenWidth() * 0.9);
            layoutParams.height = (int) (getScreenHeight() * 0.9);

            window.setAttributes(layoutParams);
        }
    }

    private int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    private void init(){
        textWatcher = new App.MoneyTextWatcher(binding.etPriceS);

    }

    private void setData(){
        if (isCreating){
            service = getIntent().getParcelableExtra("service");
            if (service == null){
                service = new Service();
                modifiedService = new Service();

                isNew = true;
            } else {
                modifiedService= new Service(service);
            }
        }
    }

    private void setView(){
        binding.etShortTitleS.setText(service.short_title);
        binding.etTitleS.setText(service.title);
        binding.etDescriptionS.setText(service.description);
        binding.cbNotActiveS.setChecked(modifiedService.not_active);

        setDurationView(service.duration);
        setPriceView(modifiedService.price);

        if (isNew) {
            binding.btnDeleteTaskS.setVisibility(View.INVISIBLE);
        }
    }

    private void setOnClickListeners(){
        setOnTouchListenerForRoot();
        setOnClickListenerForETTitle();
        setOnClickListenerForETPrice();
        setOnClickListenerForETDuration();

        binding.btnOkS.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(this);
            beforeFinishActivity(false);
        });

        binding.btnCancelS.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(this);
            beforeFinishActivity(true);
        });

        binding.btnDeleteTaskS.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(this);
            showDialogDeleteService();
        });

        binding.cbNotActiveS.setOnCheckedChangeListener((buttonView, isChecked) -> modifiedService.not_active = isChecked);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListenerForRoot() {
        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                App.hideKeyboardAndClearFocus(this);

                v.performClick();
            }
            return false;
        } );
    }

    private void setOnClickListenerForETTitle(){
        binding.etTitleS.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setTitles();
                return true;
            }
            return false;
        });

        binding.etTitleS.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                setTitles();
            }
        });
    }

    private void setTitles(){
        String title = binding.etTitleS.getText().toString();
        modifiedService.title = title;
        if (binding.etShortTitleS.getText().toString().isEmpty()){
            binding.etShortTitleS.setText(modifiedService.getShortTitle());
        }
        if (binding.etDescriptionS.getText().toString().isEmpty()){
            binding.etDescriptionS.setText(title);
        }
    }

    private void setOnClickListenerForETPrice(){
        binding.etPriceS.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                App.hideKeyboardAndClearFocus(this);
                return true;
            }
            return false;
        });

        binding.etPriceS.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                String rawText = binding.etPriceS.getText().toString()
                        .replaceAll("[^\\d.,]", "");
                binding.etPriceS.setText(rawText);

                binding.etPriceS.post(() -> binding.etPriceS.selectAll());
            } else {
                binding.etPriceS.removeTextChangedListener(textWatcher);

                String price = binding.etPriceS.getText().toString().replace(',', '.');
                modifiedService.price = Double.parseDouble(price);
                App.formatToMoneyString(binding.etPriceS);

                binding.etPriceS.addTextChangedListener(textWatcher);
            }
        });

        binding.etPriceS.addTextChangedListener(textWatcher);
    }

    private void setOnClickListenerForETDuration() {
        binding.etDurationS.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(this);
            showTimePickerDialog();
        });
    }

    private void showTimePickerDialog() {
        Time time = new Time(binding.etDurationS.getText().toString());

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String newStartTime = String.format(Locale.getDefault(),"%02d:%02d", selectedHour, selectedMinute);
                    binding.etDurationS.setText(newStartTime);
                    Time newTime = new Time(newStartTime);
                    modifiedService.duration = newTime.toInt();
                },
                time.getHour(), time.getMinute(), true // true - 24-часовой формат
        );

        timePickerDialog.show();
    }

    private void setDurationView(int duration){
        Time time = new Time(duration);
        binding.etDurationS.setText(time.toString());
        modifiedService.duration = duration;
    }

    private void setPriceView(double price){
        binding.etPriceS.setText(App.getFormattedToMoney(price));
        modifiedService.price = price;
    }

    private void fillServiceValues(){
        modifiedService.short_title = binding.etShortTitleS.getText().toString();
        modifiedService.title = binding.etTitleS.getText().toString();
        modifiedService.description = binding.etDescriptionS.getText().toString();
    }

    private void beforeFinishActivity(boolean isClosing){
        fillServiceValues();

        if (isClosing){
            closeActivity();
        } else {
            doneActivity();
        }
    }

    private void closeActivity(){
        if (!modifiedService.equals(service)){
            showDialogCloseActivity();
        } else {
            finish();
        }
    }

    private void doneActivity(){
        if (!checkFilling()) {
            return;
        }

        if (isNew){
            serviceViewModel.insertService(modifiedService);
        } else if (!modifiedService.equals(service)) {
            serviceViewModel.updateService(modifiedService);
        }

        finish();
    }

    private boolean checkFilling(){
        boolean result = true;

        if (modifiedService.short_title.isEmpty()){
            String msg = getString(R.string.w_first_name_not_filled);
            App.showWarning(msg, binding.etShortTitleS, this);

            return false;
        }

        if (modifiedService.title.isEmpty()){
            String msg = getString(R.string.w_first_name_not_filled);
            App.showWarning(msg, binding.etTitleS, this);

            return false;
        }

        return result;
    }

    private void showDialogCloseActivity(){
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDialogDeleteService(){
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> DeleteCustomer())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void DeleteCustomer(){
        serviceViewModel.deleteService(service);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
