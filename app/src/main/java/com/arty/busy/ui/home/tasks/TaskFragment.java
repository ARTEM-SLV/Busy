package com.arty.busy.ui.home.tasks;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.arty.busy.consts.Constants;
import com.arty.busy.R;
import com.arty.busy.databinding.FragmentTaskBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.date.Time;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.customers.CustomersFragment;
import com.arty.busy.ui.home.HomeViewModel;
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
    private HomeViewModel homeViewModel;
    private Task currentTask, modifiedTask;
    private Customer customer;
    private Service service;
    private ImageButton btnOk;
    private ImageButton btnCansel;
    private TextView tvCustomer;
    private TextView tvService;
    private TextView tvDate;
    private TextView tvTime;
    private EditText etDuration;
    private EditText etPrice;
    private CheckBox cbDone;
    private CheckBox cbPaid;
    private SimpleDateFormat dateFormat;
    private Date currDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = root.getContext();

        init();
        setData();
        setVisibilityView();
        setOnClickListeners();

        return root;
    }

    private void init(){
        dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        initViews();
        initCheckBoxDone();
        initCheckBoxPaid();
    }

    private void initViews(){
        cbDone = binding.cbDoneT;
        cbPaid = binding.cbPaidT;

        btnOk = binding.btnOkT;
        btnCansel = binding.btnCancelT;

        tvCustomer = binding.tvCustomerT;
        tvService = binding.tvServiceT;
        tvDate = binding.tvDateT;
        tvTime = binding.tvTimeT;

        etDuration = binding.etDurationT;
        etPrice = binding.etPriceT;
    }

    private void initCheckBoxDone(){
        cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            modifiedTask.done = isChecked;
        });
    }

    private void initCheckBoxPaid(){
        cbPaid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            modifiedTask.paid = isChecked;
        });
    }

    private void setData(){
        int idTask = requireArguments().getInt(Constants.ID_TASK);
        if (idTask == -1){
            return;
        }

        currentTask = homeViewModel.getTask(idTask);
        modifiedTask = new Task(currentTask);

        currDay = new Date(currentTask.day);
        String formattedDate = dateFormat.format(DateTime.getCalendar(currDay).getTime());
        tvDate.setText(formattedDate);

        Time timeEnd = DateTime.parseStringToTime(currentTask.time);
        timeEnd.addTime(currentTask.duration);
        String sTimeEnd = DateTime.parseTimeToString(timeEnd);
        setTimeView(currentTask.time, sTimeEnd);
        setDurationView(currentTask.duration);

        setServiceView(currentTask.id_service);
        setCustomerView(currentTask.id_customer);
        setPriceView(currentTask.price);

        cbDone.setChecked(currentTask.done);
        cbPaid.setChecked(currentTask.paid);
    }

    private void setOnClickListeners(){
        setOnClickListenerBtnOk();
        setOnClickListenerBtnCansel();
        setOnClickListenerTVCustomer();
        setOnClickListenerTVService();
        setOnClickListenerForTVDate();
        setOnClickListenerForTVTime();
        setOnClickListenerForETDuration();
        setOnClickListenerForETPrice();
    }

    private void setOnClickListenerBtnOk(){
        btnOk.setOnClickListener(v -> beforeFinishActivity(false));
    }
    
    private void setOnClickListenerBtnCansel(){
        btnCansel.setOnClickListener(v -> beforeFinishActivity(true));

        TextView tvTime = binding.tvTimeT;
        tvTime.setOnClickListener(v -> {
        });
    }

    private void setOnClickListenerTVCustomer(){
        tvCustomer.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            if (customer != null)
                bundle.putInt(Constants.ID_CUSTOMER, customer.uid);
            else bundle.putInt(Constants.ID_CUSTOMER, -1);

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.container_for_fragments, CustomersFragment.class, bundle)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });
    }

    private void setOnClickListenerTVService(){
        tvService.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            if (service != null)
                bundle.putInt(Constants.ID_SERVICE, service.uid);
            else bundle.putInt(Constants.ID_SERVICE, -1);

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.container_for_fragments, ServicesFragment.class, bundle)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });
    }

    private void setOnClickListenerForTVDate(){
        tvDate.setOnClickListener(v -> showDatePicker());
    }

    private void setOnClickListenerForTVTime(){
        tvTime.setOnClickListener(v -> showTimePickerDialog(tvTime, true));
    }

    private void setOnClickListenerForETDuration() {
        etDuration.setOnClickListener(v -> showTimePickerDialog(etDuration, false));
    }

    private void setOnClickListenerForETPrice(){
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                modifiedTask.price = Double.parseDouble(etPrice.getText().toString());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                if (!text.isEmpty()) {
//                    etPrice.removeTextChangedListener(this); // Убираем слушатель, чтобы не было зацикливания
//                    etPrice.setText(text + ".0"); // Добавляем точку в конец
//                    etPrice.setSelection(etPrice.getText().length()); // Устанавливаем курсор в конец
//                    etPrice.addTextChangedListener(this); // Возвращаем слушатель

                    modifiedTask.price = Double.parseDouble(text);
                }
            }
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
            tvDate.setText(formattedDate);
            currDay = DateTime.getStartDay(year, month, dayOfMonth);
            modifiedTask.day = currDay.getTime();
        }, selectedYear.get(), selectedMonth.get(), selectedDay.get());

        datePickerDialog.show();
    }

    private void showTimePickerDialog(TextView textView, boolean setTimeNow) {
        int hour = 0, minute = 0;
        String currentTime = textView.getText().toString();

        if (!currentTime.isEmpty()) {
            String[] timeParts = currentTime.split(" - ");
            if (timeParts.length > 0) {
                String[] startTime = timeParts[0].split(":");
                hour = Integer.parseInt(startTime[0]);
                minute = Integer.parseInt(startTime[1]);
            }
        } else if (setTimeNow) {
            // Если поле пустое, берем текущее время
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, selectedHour, selectedMinute) -> {
                    @SuppressLint("DefaultLocale") String newStartTime
                            = String.format("%02d:%02d", selectedHour, selectedMinute);
                    textView.setText(newStartTime);
                    setPerformanceTvTime();
                },
                hour, minute, true // true - 24-часовой формат
        );

        timePickerDialog.show();
    }

    private void setVisibilityView(){
        ImageButton btnCancelTask = binding.btnCancelTaskT;
        if (currentTask == null){
            btnCancelTask.setVisibility(View.INVISIBLE);
        } else {
            btnCancelTask.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setPerformanceTvTime(){
        String currentTime = tvTime.getText().toString();
        String duration = etDuration.getText().toString();
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

        tvTime.setText(getPerformanceOfTime(timeStart, timeEnd.toString()));
        modifiedTask.time = timeStart;
        modifiedTask.duration = timeDuration.toInt();
    }

    private void setServiceView(int id){
        service = homeViewModel.getService(id);
        tvService.setText(service.title);
    }

    private void setCustomerView(int id){
        customer = homeViewModel.getCustomer(id);
        tvCustomer.setText(homeViewModel.getCustomerName(customer));
    }

    private void setPriceView(double price){
        etPrice.setText(String.valueOf(price));
    }

    @SuppressLint("SetTextI18n")
    private void setTimeView(String timeStart, String timeEnd){
        tvTime.setText(getPerformanceOfTime(timeStart, timeEnd));
    }

    private void setDurationView(int duration){
        Time time = new Time(duration);

        etDuration.setText(time.toString());
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
        if (isClosing) {
            if (!currentTask.equals(modifiedTask)) {
                showDialogCloseFragment();
            } else {
                finishThisActivity();
            }
        } else {
            if (!currentTask.equals(modifiedTask)){
                homeViewModel.updateTask(modifiedTask);
            }
            finishThisActivity();
        }
    }

    private void showDialogCloseFragment(){
        new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> finishThisActivity())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
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