package com.arty.busy.ui.customers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arty.busy.App;
import com.arty.busy.R;
import com.arty.busy.databinding.ActivityCustomerBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.enums.Sex;
import com.arty.busy.models.Customer;
import com.arty.busy.ui.customers.viewmodels.CustomerViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerActivity extends AppCompatActivity {
    private CustomerViewModel customerViewModel;
    private ActivityCustomerBinding binding;
    private ArrayAdapter<String> adapter;
    private Customer customer, modifiedCustomer;
    private boolean isNew = false;
    private boolean isCreating = true;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        customerViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CustomerViewModel.class);

        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setWindowParam();

        initSexValues();
        setOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setData();
        setView();

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

    private void initSexValues(){
        String[] items = {Sex.sex.name(), Sex.male.name(), Sex.female.name()};

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                int color = ContextCompat.getColor(getContext(), R.color.DarkSlateGray);
                ((TextView) view).setTextColor(color); // Цвет текста в Spinner (выбранный элемент)
                return view;
            }
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                int color = ContextCompat.getColor(getContext(), R.color.DarkSlateGray);
                ((TextView) view).setTextColor(color); // Цвет текста в выпадающем списке
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.etSexC.setAdapter(adapter);
    }

    private void setData(){
        if (isCreating){
            customer = getIntent().getParcelableExtra("customer");
            if (customer == null){
                customer = new Customer();
                modifiedCustomer = new Customer();

                isNew = true;
            } else {
                modifiedCustomer = new Customer(customer);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setView(){
        binding.tvPhotoC.setText(modifiedCustomer.shortTitle());
        binding.etFirstNameC.setText(modifiedCustomer.first_name);
        binding.etLastNameC.setText(modifiedCustomer.last_name);
        binding.etPhoneC.setText(modifiedCustomer.phone);
        if (modifiedCustomer.birthDate != 0) {
            binding.tvBirthDateC.setText(dateFormat.format(new Date(modifiedCustomer.birthDate)));
        }

        int position = adapter.getPosition(modifiedCustomer.sex);
        binding.etSexC.setSelection(position);

        if (isNew) {
            binding.layoutPhotoC.setVisibility(View.INVISIBLE);
            binding.btnDeleteTaskC.setVisibility(View.INVISIBLE);
        }
    }

    private void setOnClickListeners(){
        binding.btnOkC.setOnClickListener(v -> beforeFinishActivity(false));
        binding.btnCancelC.setOnClickListener(v -> beforeFinishActivity(true));
        binding.btnDeleteTaskC.setOnClickListener(v -> showDialogDeleteCustomer());

        setOnTouchListenerForRoot();
        setOnClickListenerFotETAge();

        binding.etSexC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modifiedCustomer.sex = parent.getItemAtPosition(position).toString();
                binding.etSexC.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void setOnClickListenerFotETAge(){
        binding.tvBirthDateC.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(this);
            showDatePicker();
        });
    }

    private void showDatePicker() {
        Calendar calendar;
        AtomicInteger selectedYear = new AtomicInteger();
        AtomicInteger selectedMonth = new AtomicInteger();
        AtomicInteger selectedDay = new AtomicInteger();

        // Если уже есть дата, парсим её, иначе используем текущую
        if (modifiedCustomer.birthDate == 0) {
            calendar = Calendar.getInstance();
        } else {
            Date birthDate = new Date(modifiedCustomer.birthDate);
            calendar = DateTime.getCalendar(birthDate);
        }
        selectedYear.set(calendar.get(Calendar.YEAR));
        selectedMonth.set(calendar.get(Calendar.MONTH));
        selectedDay.set(calendar.get(Calendar.DAY_OF_MONTH));

        // Открываем DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, year, month, dayOfMonth) -> {

            String formattedDate = dateFormat.format(DateTime.getDate(year, month, dayOfMonth));
            binding.tvBirthDateC.setText(formattedDate);
            Date currDay = DateTime.getStartDay(year, month, dayOfMonth);
            modifiedCustomer.birthDate = currDay.getTime();
        }, selectedYear.get(), selectedMonth.get(), selectedDay.get());

        datePickerDialog.show();
    }

    private void fillCustomerValues(){
        modifiedCustomer.first_name = binding.etFirstNameC.getText().toString();
        modifiedCustomer.last_name = binding.etLastNameC.getText().toString();
        modifiedCustomer.phone = binding.etPhoneC.getText().toString();
    }

    private void beforeFinishActivity(boolean isClosing){
        fillCustomerValues();

        if (isClosing){
            closeActivity();
        } else {
            doneActivity();
        }
    }

    private void closeActivity(){
        if (!modifiedCustomer.equals(customer)){
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
            customerViewModel.insertCustomer(modifiedCustomer);
        } else if (!modifiedCustomer.equals(customer)) {
            customerViewModel.updateCustomer(modifiedCustomer);
        }

        finish();
    }

    private boolean checkFilling(){
        boolean result = true;

        if (modifiedCustomer.first_name.isEmpty()){
            String msg = getString(R.string.w_first_name_not_filled);
            App.showWarning(msg, binding.etFirstNameC, this);

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

    private void showDialogDeleteCustomer(){
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> DeleteCustomer())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void DeleteCustomer(){
        customerViewModel.deleteCustomer(customer);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}