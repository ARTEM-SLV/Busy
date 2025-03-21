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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private boolean isNew = false;
    private FragmentTaskBinding binding;
    private Context context;
    private HomeViewModel homeViewModel;
    private Task currentTask, modifiedTask;
    private Customer customer;
    private Service service;
//    private ImageButton btnOk;
//    private ImageButton btnCansel;
//    private TextView tvCustomer;
//    private TextView tvService;
//    private TextView tvDate;
//    private TextView tvTime;
//    private EditText etDuration;
//    private EditText etPrice;
//    private CheckBox cbDone;
//    private CheckBox cbPaid;
    private SimpleDateFormat dateFormat;
    private Date currDay;

    private long currDate = 0;
    private int idTask = -1;

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
        setView();
        setOnClickListeners();

        setDataFromFragment();

        return root;
    }

    private void init(){
        dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

//        initViews();
        initCheckBoxDone();
        initCheckBoxPaid();
    }

//    private void initViews(){
//        cbDone = binding.cbDoneT;
//        cbPaid = binding.cbPaidT;
//
//        btnOk = binding.btnOkT;
//        btnCansel = binding.btnCancelT;
//
//        tvCustomer = binding.tvCustomerT;
//        tvService = binding.tvServiceT;
//        tvDate = binding.tvDateT;
//        tvTime = binding.tvTimeT;
//
//        etDuration = binding.etDurationT;
//        etPrice = binding.etPriceT;
//    }

    private void initCheckBoxDone(){
        binding.cbDoneT.setOnCheckedChangeListener((buttonView, isChecked) -> {
            modifiedTask.done = isChecked;
        });
    }

    private void initCheckBoxPaid(){
        binding.cbPaidT.setOnCheckedChangeListener((buttonView, isChecked) -> {
            modifiedTask.paid = isChecked;
        });
    }

    private void setData(){
        Bundle arguments = Objects.requireNonNull(getActivity()).getIntent().getExtras();
        if (arguments != null){
            currDate = arguments.getLong(Constants.KEY_DATE);
            idTask = arguments.getInt(Constants.ID_TASK);
        }

        currDay = new Date(currDate);
        String formattedDate = dateFormat.format(DateTime.getCalendar(currDay).getTime());
        binding.tvDateT.setText(formattedDate);

        if (currentTask == null){
            if (idTask == -1){
                currentTask = new Task();
                currentTask.day = currDate;
            } else {
                currentTask = homeViewModel.getTask(idTask);
            }
            modifiedTask = new Task(currentTask);
        }
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
        }
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
        setOnClickListenerForИtnCancelTask();
    }

    private void setOnClickListenerBtnOk(){
        binding.btnOkT.setOnClickListener(v -> beforeFinishActivity(false));
    }
    
    private void setOnClickListenerBtnCansel(){
        binding.btnCancelT.setOnClickListener(v -> beforeFinishActivity(true));

//        TextView tvTime = binding.tvTimeT;
//        tvTime.setOnClickListener(v -> {
//        });
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

    private void setOnClickListenerForTVTime(){
        binding.tvTimeT.setOnClickListener(v -> showTimePickerDialog(binding.tvTimeT, true));
    }

    private void setOnClickListenerForETDuration() {
        binding.etDurationT.setOnClickListener(v -> showTimePickerDialog(binding.etDurationT, false));
    }

    private void setOnClickListenerForETPrice(){
        binding.etPriceT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                if (!text.isEmpty()) {
                    modifiedTask.price = Double.parseDouble(text);
                }
            }
        });
    }

    private void setOnClickListenerForИtnCancelTask(){
        binding.btnCancelTaskT.setOnClickListener(v -> {
            showDialogCancelTask();
        });
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

    private void showTimePickerDialog(TextView textView, boolean setTimeNow) {
        int hour = 0, minute = 0;
        String currentTime = modifiedTask.time; //textView.getText().toString();

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
            service = homeViewModel.getService(id);
        }
        binding.tvServiceT.setText(service.title);
    }

    private void setCustomerView(int id){
        if (id == 0 ){
            return;
        }
        if (customer == null){
            customer = homeViewModel.getCustomer(id);
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
        if (!currentTask.equals(modifiedTask)){
            if (isClosing){
                showDialogCloseFragment();
            } else {
                if (!checkFilling()){
                    return;
                }
                if (isNew){
                    homeViewModel.insertTask(modifiedTask);
                } else {
                    homeViewModel.updateTask(modifiedTask);
                }

                finishThisActivity();
            }
        } else {
            finishThisActivity();
        }
    }

    private boolean checkFilling(){
        boolean result = true;

        if (Objects.equals(modifiedTask.time, "")){
            showMessage("Не заполнено время");
            return false;
        }

        return result;
    }

    private void showMessage(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void showDialogCloseFragment(){
        new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> finishThisActivity())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDialogCancelTask(){
        new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> DeleteTask())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void DeleteTask(){
        homeViewModel.deleteTask(currentTask);
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