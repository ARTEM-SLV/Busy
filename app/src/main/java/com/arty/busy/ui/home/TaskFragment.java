package com.arty.busy.ui.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

//import com.arty.busy.App;
import com.arty.busy.consts.Constants;
import com.arty.busy.R;
//import com.arty.busy.data.BusyDao;
import com.arty.busy.databinding.FragmentTaskBinding;
import com.arty.busy.date.MyDate;
import com.arty.busy.date.Time;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.arty.busy.ui.customers.CustomersFragment;
import com.arty.busy.ui.services.ServicesFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
//import com.arty.busy.ui.home.items.ItemTaskInfo;

public class TaskFragment extends Fragment {
    private FragmentTaskBinding binding;
//    private Context context;
    private HomeViewModel homeViewModel;
    private View root;

    private boolean modified;
//    private ItemTaskInfo itemTaskInfo;
    private Task currentTask;
    private Customer customer;
    private Service service;

//    private BusyDao busyDao;

    private ImageButton btnOk;
    private ImageButton btnCansel;
    private TextView tvCustomer;
    private  TextView tvService;
    private TextView tvDate;
    private TextView tvTime;
    private EditText etDuration;
    private CheckBox cbDone;
    private CheckBox cbPaid;

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
        root = binding.getRoot();
//        context = getContext();

        init();
        setVisibilityView();
        setOnClickListeners();

        return root;
    }

    private void init(){
        setModified(false);

        initViews();
        initCheckBoxDone();
        initCheckBoxPaid();

        int idTask = requireArguments().getInt(Constants.ID_TASK);
        if (idTask != -1){
            currentTask = homeViewModel.getTask(idTask);
            setData();
        }
    }

    private void initViews(){
        cbDone = root.findViewById(R.id.cbDone_T);
        cbPaid = root.findViewById(R.id.cbPaid_T);

        btnOk = root.findViewById(R.id.btnOk_T);
        btnCansel = root.findViewById(R.id.btnCancel_T);

        tvCustomer = root.findViewById(R.id.tvCustomer_T);
        tvService = root.findViewById(R.id.tvService_T);
        tvDate = root.findViewById(R.id.tvDate_T);
        tvTime = root.findViewById(R.id.tvTime_T);

        etDuration = root.findViewById(R.id.etDuration_T);
    }

    private void initCheckBoxDone(){
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setModified(true);
            }
        });
    }

    private void initCheckBoxPaid(){
        cbPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setModified(true);
            }
        });
    }

    private void setOnClickListeners(){
        setOnClickListenerForBtnOk();
        setOnClickListenerForBtnCansel();
        setOnClickListenerForTVCustomer();
        setOnClickListenerForTVService();
        setOnClickListenerForTVDate();
        setOnClickListenerForTVTime();
        setOnClickListenerForETDuration();
    }

    private void setOnClickListenerForBtnOk(){
        btnOk.setOnClickListener(v -> {
            if (modified){
                updateDataTask();
            }

            finishActivityForFragment();
        });
    }
    
    private void setOnClickListenerForBtnCansel(){
        btnCansel.setOnClickListener(v -> {
            if (modified){

            }else
                finishActivityForFragment();
        });

        // setOnClickListener for text view Time
        TextView tvTime = root.findViewById(R.id.tvTime_T);
        tvTime.setOnClickListener(v -> {
            setModified(true);
        });
    }
    
    private void setOnClickListenerForTVService(){
        tvCustomer.setOnClickListener(v -> {
            setModified(true);

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
    
    private void setOnClickListenerForTVCustomer(){
        tvService.setOnClickListener(v -> {
            setModified(true);

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
        tvTime.setOnClickListener(v -> showTimePickerDialog(tvTime));
    }

    private void setOnClickListenerForETDuration() {
        etDuration.setOnClickListener(v -> showTimePickerDialog(etDuration));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        AtomicInteger selectedYear = new AtomicInteger();
        AtomicInteger selectedMonth = new AtomicInteger();
        AtomicInteger selectedDay = new AtomicInteger();
//        int selectedHour;
//        int selectedMinute;

        // Если уже есть дата, парсим её, иначе используем текущую
        if (!tvDate.getText().toString().isEmpty()) {
            String[] parts = tvDate.getText().toString().split(" ");
            String[] dateParts = parts[0].split("\\.");
//            String[] timeParts = parts[1].split(":");

            selectedYear.set(Integer.parseInt(dateParts[2]));
            selectedMonth.set(Integer.parseInt(dateParts[1]) - 1); // В Calendar январь = 0
            selectedDay.set(Integer.parseInt(dateParts[0]));
//            selectedHour = Integer.parseInt(timeParts[0]);
//            selectedMinute = Integer.parseInt(timeParts[1]);
        } else {
            selectedYear.set(calendar.get(Calendar.YEAR));
            selectedMonth.set(calendar.get(Calendar.MONTH));
            selectedDay.set(calendar.get(Calendar.DAY_OF_MONTH));
//            selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
//            selectedMinute = calendar.get(Calendar.MINUTE);
        }

        // Открываем DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            root.getContext(), (view, year, month, dayOfMonth) -> {
            selectedYear.set(year);
            selectedMonth.set(month);
            selectedDay.set(dayOfMonth);

//            // После выбора даты открываем TimePickerDialog
//            showTimePickerDialog();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String formattedDate
                    = dateFormat.format(MyDate.getDate(selectedYear.get(), selectedMonth.get(), selectedDay.get()));
//                    = String.format("%02d.%02d.%04d", selectedDay.get(), selectedMonth.get() + 1, selectedYear.get());
            tvDate.setText(formattedDate);
        }, selectedYear.get(), selectedMonth.get(), selectedDay.get());

        datePickerDialog.show();
    }

    private void showTimePickerDialog(TextView tvTime) {
        int hour = 0, minute = 0;
        String currentTime = tvTime.getText().toString();

        if (!currentTime.isEmpty()) {
//            String timeRange = currentTime;
            String[] timeParts = currentTime.split(" - ");
            if (timeParts.length > 0) {
                String[] startTime = timeParts[0].split(":");
                hour = Integer.parseInt(startTime[0]);
                minute = Integer.parseInt(startTime[1]);
            }
        } else {
            // Если поле пустое, берем текущее время
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                root.getContext(),
                (view, selectedHour, selectedMinute) -> {
                    String newStartTime  = String.format("%02d:%02d", selectedHour, selectedMinute);
                    tvTime.setText(newStartTime);
                    setPerformanceTvTime();
                },
                hour, minute, true // true - 24-часовой формат
        );

        timePickerDialog.show();
    }

    private void setVisibilityView(){
//        ImageButton btnPostpone = root.findViewById(R.id.btnPostpone_T);
        ImageButton btnCancelTask = root.findViewById(R.id.btnCancelTask_T);
        if (currentTask == null){
//            btnPostpone.setVisibility(View.GONE);
            btnCancelTask.setVisibility(View.INVISIBLE);
        } else {
//            btnPostpone.setVisibility(View.VISIBLE);
            btnCancelTask.setVisibility(View.VISIBLE);
        }
    }

    private void setModified(boolean isModified){
        modified = isModified;
    }

    private void setData(){
        setServiceForView(currentTask.id_service);
        setCustomerForView(currentTask.id_customer);
        setPriceForView(currentTask.price);

        Time timeEnd = MyDate.parseStringToTime(currentTask.time);
        timeEnd.addTime(service.duration);
        String sTimeEnd = MyDate.parseTimeToString(timeEnd);
        setTimeForView(currentTask.time, sTimeEnd);

        cbDone.setChecked(currentTask.done);
        cbPaid.setChecked(currentTask.paid);
    }

    private void setPerformanceTvTime(){
        String currentTime = tvTime.getText().toString();
        String durationTime = etDuration.getText().toString();
        String timeStart;

        String[] timeParts = currentTime.split(" - ");
        if (timeParts.length > 0) {
            timeStart = timeParts[0];
        } else {
            timeStart = currentTime;
        }

        Time timeEnd = new Time(timeStart);
        timeEnd.addTime(new Time(durationTime));

        String timeText = timeStart + " - " + timeEnd.toString();
        tvTime.setText(timeText);
    }

    private void updateDataTask(){
//        App.getInstance().getBusyDao().updateTaskList(task);
    }

    private Service getService(int id){
        return homeViewModel.getService(id);//busyDao.getServiceByID(uid);
    }

    private void setServiceForView(int uid){
        service = getService(uid);

        TextView etService = root.findViewById(R.id.tvService_T);
        etService.setText(service.title);
    }

    private Customer getCustomer(int id){
        return homeViewModel.getCustomer(id);//busyDao.getCustomerByID(uid);
    }

    private void setCustomerForView(int id){
        customer = getCustomer(id);

//        StringBuilder nameCustomer = new StringBuilder("");
//        nameCustomer.append(customer.first_name);
//        nameCustomer.append(" ");
//        nameCustomer.append(customer.last_name);

        TextView etCustomer = root.findViewById(R.id.tvCustomer_T);
        etCustomer.setText(homeViewModel.getCustomerName(customer));
    }

    private void setPriceForView(double price){
        EditText etPrice = root.findViewById(R.id.etPrice_T);
        etPrice.setText(String.valueOf(price));
    }

    private void setTimeForView(String timeStart, String timeEnd){
//        StringBuilder sbTime = new StringBuilder("");
//        sbTime.append(timeStart);
//        sbTime.append(" - ");
//        sbTime.append(timeEnd);

        TextView tvTime = root.findViewById(R.id.tvTime_T);
        tvTime.setText(homeViewModel.getViewOfTime(timeStart, timeEnd));
    }

    private void finishActivityForFragment(){
//        getParentFragmentManager().popBackStack();
//        getParentFragmentManager().beginTransaction().remove(this).commit();
        getActivity().finish();
    }

}