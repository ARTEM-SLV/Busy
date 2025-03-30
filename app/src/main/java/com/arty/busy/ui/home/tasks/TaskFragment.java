package com.arty.busy.ui.home.tasks;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.arty.busy.consts.Constants;
import com.arty.busy.R;
import com.arty.busy.databinding.FragmentTaskBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.date.Time;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.customers.CustomersFragment;
import com.arty.busy.ui.home.viewmodel.TaskViewModel;
import com.arty.busy.ui.services.ServicesFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskFragment extends Fragment {
    private FragmentTaskBinding binding;
    private Context context;
    private TaskViewModel taskViewModel;
    private Customer customer;
    private Service service;
    private SimpleDateFormat dateFormat;
    private Date currDay;
    private Task currentTask, modifiedTask;
    private boolean isNew = false;

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
        View root = binding.getRoot();
        context = root.getContext();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        init();
        setData();
        setView();
        setOnClickListeners();

        setDataFromFragment();
    }

    private void init(){
        dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        initCheckBoxDone();
        initCheckBoxPaid();
    }

    private void initCheckBoxDone(){
        binding.cbDoneT.setOnCheckedChangeListener((buttonView, isChecked) -> modifiedTask.done = isChecked);
    }

    private void initCheckBoxPaid(){
        binding.cbPaidT.setOnCheckedChangeListener((buttonView, isChecked) -> modifiedTask.paid = isChecked);
    }

    private void setData(){
        long currDate = 0;
        String time = "00:00";
        int idTask = -1;

        Bundle arguments = Objects.requireNonNull(getActivity()).getIntent().getExtras();
        if (arguments != null){
            currDate = arguments.getLong(Constants.KEY_DATE);
            idTask = arguments.getInt(Constants.ID_TASK);
            time = arguments.getString(Constants.KEY_TIME);
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
        setOnClickListenerBtnOk();
        setOnClickListenerBtnCansel();
        setOnClickListenerTVCustomer();
        setOnClickListenerTVService();
        setOnClickListenerForTVDate();
        setOnClickListenerForBtnReschedule();
        setOnClickListenerForTVTime();
        setOnClickListenerForETDuration();
        setOnClickListenerForETPrice();
        setOnClickListenerForBtnCancelTask();
    }

    private void setOnClickListenerBtnOk(){
        binding.btnOkT.setOnClickListener(v -> beforeFinishActivity(false));
    }
    
    private void setOnClickListenerBtnCansel(){
        binding.btnCancelT.setOnClickListener(v -> beforeFinishActivity(true));
    }

    private void setOnClickListenerTVCustomer(){
        binding.tvCustomerT.setOnClickListener(v -> {
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
                    .replace(R.id.container_for_fragments, CustomersFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setOnClickListenerTVService(){
        binding.tvServiceT.setOnClickListener(v -> {
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
                    .replace(R.id.container_for_fragments, ServicesFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setOnClickListenerForTVDate(){
        binding.tvDateT.setOnClickListener(v -> showDatePicker());
    }

    private void setOnClickListenerForBtnReschedule(){
        binding.btnReschedule.setOnClickListener(v -> showDatePicker());
    }

    private void setOnClickListenerForTVTime(){
        binding.tvTimeT.setOnClickListener(v -> showTimePickerDialog(binding.tvTimeT, modifiedTask.time));
    }

    private void setOnClickListenerForETDuration() {
        binding.etDurationT.setOnClickListener(v -> showTimePickerDialog(binding.etDurationT, binding.etDurationT.getText().toString()));
    }

    private void setOnClickListenerForETPrice(){
        binding.etPriceT.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String text = binding.etPriceT.getText().toString();
                if (!text.isEmpty()){
                    modifiedTask.price = Double.parseDouble(text);
                }
                return true;
            }
            return false;
        });
        binding.etPriceT.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = binding.etPriceT.getText().toString();
                if (!text.isEmpty()){
                    modifiedTask.price = Double.parseDouble(text);
                }
            }
        });
    }

    private void setOnClickListenerForBtnCancelTask(){
        binding.btnCancelTaskT.setOnClickListener(v -> showDialogDeleteTask());
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
            context, (view, year, month, dayOfMonth) -> {

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
                context,
                (view, selectedHour, selectedMinute) -> {
                    @SuppressLint("DefaultLocale") String newStartTime
                            = String.format("%02d:%02d", selectedHour, selectedMinute);
                    textView.setText(newStartTime);
                    setPerformanceTvTime();
                },
                time.getHour(), time.getMinute(), true // true - 24-часовой формат
        );

        timePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
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
        binding.etPriceT.setText(String.valueOf(price));
        modifiedTask.price = price;
    }

    @SuppressLint("SetTextI18n")
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
                        setEnabled(false); // Отключаем callback
                        beforeFinishActivity(true);
                    }
                }
        );
    }

    private void beforeFinishActivity(boolean isClosing){
        modifiedTask.price = Double.parseDouble(binding.etPriceT.getText().toString());

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
            finishThisActivity();
        }
    }

    private void doneActivity(){
        if (!checkFilling()) {
            return;
        }

        if (isNew){
            taskViewModel.insertTask(modifiedTask);
        } else if (!modifiedTask.equals(currentTask)) {
            taskViewModel.updateTask(modifiedTask);
        }

        finishThisActivity();
    }

    private boolean checkFilling(){
        boolean result = true;

        if (modifiedTask.day == 0){
            String msg = getString(R.string.w_day_not_specified);
            App.showWarning(msg, binding.tvDateT, context);
            return false;
        }

        if (Objects.equals(modifiedTask.time, "00:00")){
            String msg = getString(R.string.w_time_not_filled);
            App.showWarning(msg, binding.tvTimeT, context);
            return false;
        }

        if (modifiedTask.id_customer <= 0){
            String msg = getString(R.string.w_client_is_not_filled);
            App.showWarning(msg, binding.tvCustomerT, context);
            return false;
        }

        if (modifiedTask.id_service <= 0){
            String msg = getString(R.string.w_service_is_not_filled);
            App.showWarning(msg, binding.tvServiceT, context);
            return false;
        }

        return result;
    }

    private void showDialogCloseFragment(){
        new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> finishThisActivity())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDialogDeleteTask(){
        new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> DeleteTask())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void DeleteTask(){
        taskViewModel.deleteTask(currentTask);
        finishThisActivity();
    }

    private void finishThisActivity(){
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}