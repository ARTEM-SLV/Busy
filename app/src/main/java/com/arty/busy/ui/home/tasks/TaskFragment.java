package com.arty.busy.ui.home.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.App;
import com.arty.busy.OnFragmentCloseListener;
import com.arty.busy.consts.Constants;
import com.arty.busy.R;
import com.arty.busy.databinding.FragmentTaskBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.date.Time;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.customers.CustomersFragment;
import com.arty.busy.ui.home.viewmodels.TaskViewModel;
import com.arty.busy.ui.services.ServicesFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskFragment extends Fragment {
    private FragmentTaskBinding binding;
    private Activity activity;
    private TaskViewModel taskViewModel;
    private Customer customer;
    private Service service;
    private SimpleDateFormat dateFormat;
    private Date currDay;
    private Task currentTask, modifiedTask;
    private boolean isNew = false;
    private TextWatcher textWatcher;
    private OnFragmentCloseListener closeListener;

    // Фабричный метод для создания фрагмента с параметрами
    public static TaskFragment newInstance(long date, int id, String time) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.KEY_DATE, date);
        args.putInt(Constants.ID_TASK, id);
        args.putString(Constants.KEY_TIME, time);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        taskViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(TaskViewModel.class);

        binding = FragmentTaskBinding.inflate(inflater, container, false);

        init();
        setData();
        setView();
        setOnClickListeners();

        setDataFromFragment();

        return binding.getRoot();
    }

    private void init(){
        Context context = binding.getRoot().getContext();
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = requireActivity();
        }

        dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        textWatcher = new App.MoneyTextWatcher(binding.etPriceT);
    }

    private void setData(){
        long currDate = 0;
        int idTask = -1;
        String time = "00:00";

        Bundle args = getArguments();
        if (args != null) {
            currDate = args.getLong(Constants.KEY_DATE);
            idTask = args.getInt(Constants.ID_TASK);
            time = args.getString(Constants.KEY_TIME);
        }

        currDay = new Date(currDate);
        String formattedDate = dateFormat.format(DateTime.getCalendar(currDay).getTime());
        binding.tvDateT.setText(formattedDate);

        taskViewModel.getTasks(idTask, currDate, time);
        currentTask = taskViewModel.getCurrentTask();
        modifiedTask = taskViewModel.getModifiedTask();

        isNew = idTask == -1;
    }

    private void setView(){
        Time timeEnd = DateTime.parseStringToTime(modifiedTask.time);
        timeEnd.addTime(modifiedTask.duration);
        String sTimeEnd = "";
        if (!modifiedTask.time.equals("")){
            sTimeEnd = DateTime.parseTimeToString(timeEnd);
        }
        setTimeView(modifiedTask.time, sTimeEnd);
        setDurationView(modifiedTask.duration);

        setServiceView(modifiedTask.id_service);
        setCustomerView(modifiedTask.id_customer);
        setPriceView(modifiedTask.price);

        binding.cbDoneT.setChecked(modifiedTask.done);
        binding.cbPaidT.setChecked(modifiedTask.paid);

        if (isNew){
            binding.btnCancelTaskT.setVisibility(View.INVISIBLE);
            binding.btnReschedule.setVisibility(View.GONE);
        }
    }

    private void setOnClickListeners(){
        setOnTouchListenerForRoot();
        setOnClickListenerTVCustomer();
        setOnClickListenerTVService();
        setOnClickListenerForETPrice();

        binding.btnOkT.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(activity);
            beforeFinishActivity(false);
        });

        binding.btnCloseT.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(activity);
            beforeFinishActivity(true);
        });

        binding.btnReschedule.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(activity);
            showDatePicker();
        });

        binding.tvTimeT.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(activity);
            showTimePickerDialog(binding.tvTimeT, modifiedTask.time);
        });

        binding.etDurationT.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(activity);
            showTimePickerDialog(binding.etDurationT, binding.etDurationT.getText().toString());
        });

        binding.btnCancelTaskT.setOnClickListener(v -> showDialogDeleteTask());

        binding.cbDoneT.setOnCheckedChangeListener((buttonView, isChecked) -> modifiedTask.done = isChecked);
        binding.cbPaidT.setOnCheckedChangeListener((buttonView, isChecked) -> modifiedTask.paid = isChecked);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListenerForRoot() {
        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                App.hideKeyboardAndClearFocus(activity);

                v.performClick();
            }
            return false;
        } );
    }

    private void setOnClickListenerTVCustomer(){
        binding.tvCustomerT.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(activity);

            Bundle bundle = new Bundle();
            if (customer != null)
                bundle.putInt(Constants.ID_CUSTOMER, customer.uid);
            else bundle.putInt(Constants.ID_CUSTOMER, -1);
            bundle.putBoolean("isChoice", true);

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(
                            android.R.anim.fade_in,  // Анимация при входе
                            android.R.anim.fade_out,  // Анимация при выходе
                            android.R.anim.fade_in,  // Анимация при возврате назад
                            android.R.anim.fade_out  // Анимация при закрытии
                    )
                    .replace(R.id.mainContainer_TTD, CustomersFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setOnClickListenerTVService(){
        binding.tvServiceT.setOnClickListener(v -> {
            App.hideKeyboardAndClearFocus(activity);

            Bundle bundle = new Bundle();
            if (service != null)
                bundle.putInt(Constants.ID_SERVICE, service.uid);
            else bundle.putInt(Constants.ID_SERVICE, -1);
            bundle.putBoolean("isChoice", true);

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(
                            android.R.anim.fade_in,  // Анимация при входе
                            android.R.anim.fade_out,  // Анимация при выходе
                            android.R.anim.fade_in,  // Анимация при возврате назад
                            android.R.anim.fade_out  // Анимация при закрытии
                    )
                    .replace(R.id.mainContainer_TTD, ServicesFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setOnClickListenerForETPrice(){
        binding.etPriceT.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                App.hideKeyboardAndClearFocus(activity);
                return true;
            }
            return false;
        });

        binding.etPriceT.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                String rawText = binding.etPriceT.getText().toString()
                        .replaceAll("[^\\d.,]", "");
                binding.etPriceT.setText(rawText);

                binding.etPriceT.post(() -> binding.etPriceT.selectAll());
            } else {
                binding.etPriceT.removeTextChangedListener(textWatcher);

                String price = binding.etPriceT.getText().toString().replace(',', '.');
                modifiedTask.price = Double.parseDouble(price);
                App.formatToMoneyString(binding.etPriceT);

                binding.etPriceT.addTextChangedListener(textWatcher);
            }
        });

        binding.etPriceT.addTextChangedListener(textWatcher);
    }

    private void setDataFromFragment(){
        getParentFragmentManager().setFragmentResultListener("customer", this, (requestKey, result) -> {
            customer = result.getParcelable(requestKey);
            modifiedTask.id_customer = customer.uid;
            binding.tvCustomerT.setText(customer.toString());
        });

        getParentFragmentManager().setFragmentResultListener("service", this, (requestKey, result) -> {
            service = result.getParcelable(requestKey);
            modifiedTask.id_service = service.uid;
            binding.tvServiceT.setText(service.title);

            setDurationView(service.duration);
            setPriceView(service.price);
        });
    }

    private void showDatePicker() {
        Calendar calendar;
        AtomicInteger selectedYear = new AtomicInteger();
        AtomicInteger selectedMonth = new AtomicInteger();
        AtomicInteger selectedDay = new AtomicInteger();

        // Если уже есть дата, парсим её, иначе используем текущую
        if (currDay == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = DateTime.getCalendar(currDay);
        }
        selectedYear.set(calendar.get(Calendar.YEAR));
        selectedMonth.set(calendar.get(Calendar.MONTH));
        selectedDay.set(calendar.get(Calendar.DAY_OF_MONTH));

        // Открываем DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            activity, (view, year, month, dayOfMonth) -> {

            String formattedDate = dateFormat.format(DateTime.getDate(year, month, dayOfMonth));
            binding.tvDateT.setText(formattedDate);
            currDay = DateTime.getStartDay(year, month, dayOfMonth);
            modifiedTask.day = currDay.getTime();
        }, selectedYear.get(), selectedMonth.get(), selectedDay.get());

        datePickerDialog.show();
    }

    private void showTimePickerDialog(TextView textView, String sTime) {
        Time time = new Time(sTime);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                activity,
                (view, selectedHour, selectedMinute) -> {
                    String newStartTime = String.format(Locale.getDefault(),"%02d:%02d", selectedHour, selectedMinute);
                    textView.setText(newStartTime);
                    setPerformanceTvTime();
                },
                time.getHour(), time.getMinute(), true // true - 24-часовой формат
        );

        timePickerDialog.show();
    }

    private void setPerformanceTvTime(){
        String currentTime = binding.tvTimeT.getText().toString();
        String duration = binding.etDurationT.getText().toString();
        String timeStart;

        String[] timeParts = currentTime.split(" - ");
        if (timeParts.length > 0) {
            timeStart = timeParts[0];
        } else {
            timeStart = currentTime;
        }

        Time timeEnd = new Time(timeStart);
        Time timeDuration = new Time(duration);
        timeEnd.addTime(timeDuration);

        binding.tvTimeT.setText(getPerformanceOfTime(timeStart, timeEnd.toString()));
        modifiedTask.time = timeStart;
        modifiedTask.duration = timeDuration.toInt();
    }

    private void setServiceView(int id){
        if (id == 0 ){
            return;
        }
        if (service == null){
            service = taskViewModel.getService(id);
        }
        binding.tvServiceT.setText(service.title);
    }

    private void setCustomerView(int id){
        if (id == 0 ){
            return;
        }
        if (customer == null){
            customer = taskViewModel.getCustomer(id);
        }
        binding.tvCustomerT.setText(customer.toString());
    }

    private void setPriceView(double price){
        binding.etPriceT.setText(App.getFormattedToMoney(price));
        modifiedTask.price = price;
    }

    private void setTimeView(String timeStart, String timeEnd){
        binding.tvTimeT.setText(getPerformanceOfTime(timeStart, timeEnd));
    }

    private void setDurationView(int duration){
        Time time = new Time(duration);
        binding.etDurationT.setText(time.toString());
        modifiedTask.duration = duration;
    }

    private String getPerformanceOfTime(String timeStart, String timeEnd){
        String res;

        if (timeEnd.equals(timeStart)){
            res = timeStart;
        } else {
            res = timeStart + " - " + timeEnd;
        }

        return res;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        beforeFinishActivity(true);
                    }
                }
        );

        try {
            closeListener = (OnFragmentCloseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnFragmentCloseListener");
        }
    }

    private void beforeFinishActivity(boolean isClosing){
        if (isClosing){
            closeActivity();
        } else {
            doneActivity();
        }
    }

    private void closeActivity(){
        if (!modifiedTask.equals(currentTask)){
            showDialogCloseFragment();
        } else {
            closeWithResult(false);
        }
    }

    private void doneActivity(){
        if (!checkFilling()) {
            return;
        }

        boolean update = true;
        if (isNew){
            taskViewModel.insertTask(modifiedTask);
        } else if (!modifiedTask.equals(currentTask)) {
            taskViewModel.updateTask(modifiedTask);
        } else {
            update = false;
        }

        closeWithResult(update);
    }

    private boolean checkFilling(){
        boolean result = true;

        if (modifiedTask.day == 0){
            String msg = getString(R.string.w_day_not_specified);
            App.showWarning(msg, binding.tvDateT, activity);
            return false;
        }

        if (Objects.equals(modifiedTask.time, "00:00")){
            String msg = getString(R.string.w_time_not_filled);
            App.showWarning(msg, binding.tvTimeT, activity);
            return false;
        }

        if (modifiedTask.id_customer <= 0){
            String msg = getString(R.string.w_client_is_not_filled);
            App.showWarning(msg, binding.tvCustomerT, activity);
            return false;
        }

        if (modifiedTask.id_service <= 0){
            String msg = getString(R.string.w_service_is_not_filled);
            App.showWarning(msg, binding.tvServiceT, activity);
            return false;
        }

        return result;
    }

    private void showDialogCloseFragment(){
        new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> closeWithResult(false))
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDialogDeleteTask(){
        new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> DeleteTask())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void DeleteTask(){
        taskViewModel.deleteTask(currentTask);
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