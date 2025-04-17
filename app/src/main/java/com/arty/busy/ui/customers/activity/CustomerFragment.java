package com.arty.busy.ui.customers.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.OnFragmentCloseListener;
import com.arty.busy.R;
import com.arty.busy.Utility;
import com.arty.busy.consts.Constants;
import com.arty.busy.databinding.FragmentCustomerBinding;
import com.arty.busy.models.Customer;
import com.arty.busy.ui.customers.viewmodels.CustomerViewModel;

public class CustomerFragment extends DialogFragment {
    private CustomerViewModel customerViewModel;
    private FragmentActivity activity;
    private FragmentCustomerBinding binding;
    private Customer customer, modifiedCustomer;
    private boolean isNew = false;
    private boolean isCreating = true;
    private OnFragmentCloseListener closeListener;

    // Фабричный метод для создания фрагмента с параметрами
    public static CustomerFragment newInstance(Customer customer) {
        CustomerFragment fragment = new CustomerFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.KEY_CUSTOMER, customer);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (FragmentActivity) context;
        } else {
            activity = requireActivity();
        }
        activity.getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        beforeFinishActivity(true);
                    }
                }
        );

        try {
            closeListener = (OnFragmentCloseListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnFragmentCloseListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customerViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CustomerViewModel.class);

        binding = FragmentCustomerBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();

        setWindowParam();

        setData();
        setView();
        setOnClickListeners();

        return binding.getRoot();
    }

    private void setWindowParam(){
        // Получаем параметры окна
        Window window = activity.getWindow();
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
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isCreating){
            isCreating = false;
        }
    }

    private void setData(){
        if (isCreating){
            Bundle args = getArguments();
            if (args != null) {
                customer = args.getParcelable("customer");
                if (customer == null) {
                    customer = new Customer();
                    modifiedCustomer = new Customer();

                    isNew = true;
                } else {
                    modifiedCustomer = new Customer(customer);
                }
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
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListenerForRoot() {
        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Utility.hideKeyboardAndClearFocus(activity);

                v.performClick();
            }
            return false;
        } );
    }

    private void fillCustomerValues(){
        modifiedCustomer.first_name = binding.etFirstNameC.getText().toString();
        modifiedCustomer.last_name = binding.etLastNameC.getText().toString();
        modifiedCustomer.phone = binding.etPhoneC.getText().toString();
    }

    private void beforeFinishActivity(boolean isClosing){
        fillCustomerValues();

        if (isClosing){
            handlerBtnClose();
        } else {
            handlerBtnDone();
        }
    }

    private void handlerBtnClose(){
        if (!modifiedCustomer.equals(customer)){
            showDialogCloseActivity();
        } else {
            closeWithResult(false);
        }
    }

    private void handlerBtnDone(){
        if (!checkFilling()) {
            return;
        }

        boolean update = true;
        if (isNew){
            customerViewModel.insertCustomer(modifiedCustomer);
        } else if (!modifiedCustomer.equals(customer)) {
            customerViewModel.updateCustomer(modifiedCustomer);
        } else {
            update = false;
        }

        closeWithResult(update);
    }

    private boolean checkFilling(){
        boolean result = true;

        if (modifiedCustomer.first_name.isEmpty()){
            String msg = getString(R.string.w_first_name_not_filled);
            Utility.showWarning(msg, binding.etFirstNameC, activity);

            return false;
        }

        return result;
    }

    private void showDialogCloseActivity(){
        new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> closeWithResult(false))
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDialogDeleteCustomer(){
        new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> DeleteCustomer())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void DeleteCustomer(){
        customerViewModel.deleteCustomer(customer);
        closeWithResult(true);
    }

    private void closeWithResult(boolean update){
        Bundle result = new Bundle();
        result.putBoolean(Constants.KEY_UPDATE, update);
        if (closeListener != null) {
            closeListener.closeFragment(result);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        closeListener = null;
    }
}