package com.arty.busy.ui.customers.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.arty.busy.R;
import com.arty.busy.Utility;
import com.arty.busy.databinding.ActivityCustomerBinding;
import com.arty.busy.models.Customer;
import com.arty.busy.ui.customers.viewmodels.CustomerViewModel;

public class CustomerActivity extends AppCompatActivity {
    private CustomerViewModel customerViewModel;
    private ActivityCustomerBinding binding;
    private Customer customer, modifiedCustomer;
    private boolean isNew = false;
    private boolean isCreating = true;
    private TextWatcher phoneTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        customerViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CustomerViewModel.class);

        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

    private void init(){
        phoneTextWatcher = new Utility.PhoneMaskTextWatcher(binding.etPhoneC);
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

    private void setView(){
        binding.tvPhotoC.setText(modifiedCustomer.shortTitle());
        binding.etFirstNameC.setText(modifiedCustomer.first_name);
        binding.etLastNameC.setText(modifiedCustomer.last_name);
        binding.etPhoneC.setText(modifiedCustomer.phone);
        binding.cbNotActiveC.setChecked(modifiedCustomer.not_active);

        if (isNew) {
            binding.layoutPhotoC.setVisibility(View.INVISIBLE);
            binding.btnDeleteTaskC.setVisibility(View.INVISIBLE);
        }
    }

    private void setOnClickListeners(){
        binding.btnOkC.setOnClickListener(v -> beforeFinishActivity(false));
        binding.btnCancelC.setOnClickListener(v -> beforeFinishActivity(true));
        binding.btnDeleteTaskC.setOnClickListener(v -> showDialogDeleteCustomer());

        binding.cbNotActiveC.setOnCheckedChangeListener((buttonView, isChecked) -> modifiedCustomer.not_active = isChecked);

        setOnTouchListenerForRoot();
        setOnClickListenerForETPhone();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListenerForRoot() {
        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Utility.hideKeyboardAndClearFocus(this);

                v.performClick();
            }
            return false;
        } );
    }

    private void setOnClickListenerForETPhone(){
        binding.etPhoneC.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
//                String rawText = binding.etPhoneC.getText().toString()
//                        .replaceAll("[^\\d.,]", "");
//                binding.etPhoneC.setText(rawText);

                binding.etPhoneC.post(() -> binding.etPhoneC.selectAll());
            } else {
                binding.etPhoneC.removeTextChangedListener(phoneTextWatcher);

                String phone = binding.etPhoneC.getText().toString().replace(',', '.');
//                modifiedCustomer.phone = phone;
                Utility.formatToMoneyString(binding.etPhoneC);

                binding.etPhoneC.addTextChangedListener(phoneTextWatcher);
            }
        });

        binding.etPhoneC.addTextChangedListener(phoneTextWatcher);
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
            Utility.showWarning(msg, binding.etFirstNameC, this);

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