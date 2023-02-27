package com.arty.busy.ui.home;

import android.content.Context;
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
import com.arty.busy.Constants;
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
//import com.arty.busy.ui.home.items.ItemTaskInfo;

public class TaskFragment extends Fragment {
    private FragmentTaskBinding binding;
    private Context context;
    private HomeViewModel homeViewModel;
    private View root;

    private boolean modified;
//    private ItemTaskInfo itemTaskInfo;
    private Task currentTask;
    private Customer customer;
    private Service service;

//    private BusyDao busyDao;

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
        context = getContext();

        init();
        setVisibilityView();
        setOnClickListeners();

        return root;
    }

    private void setOnClickListeners(){
        // setOnClickListener for btn Ok
        ImageButton btnOk = root.findViewById(R.id.btnOk_T);
        btnOk.setOnClickListener(v -> {
            if (modified){
                updateDataTask();
            }

            backToLastFragment();
        });

        // setOnClickListener for btn Cansel
        ImageButton btnCansel = root.findViewById(R.id.btnCancel_T);
        btnCansel.setOnClickListener(v -> {
            if (modified){

            }else
                backToLastFragment();
        });

        // setOnClickListener for text view Time
        TextView tvTime = root.findViewById(R.id.tvTime_T);
        tvTime.setOnClickListener(v -> {
            setModified(true);
        });

        // setOnClickListener for text view Customer
        TextView txCustomer = root.findViewById(R.id.txCustomer_T);
        txCustomer.setOnClickListener(v -> {
            setModified(true);

            Bundle bundle = new Bundle();
            if (customer != null)
                bundle.putInt(Constants.ID_CUSTOMER, customer.uid);
            else bundle.putInt(Constants.ID_CUSTOMER, -1);

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.nav_host_fragment_activity_main, CustomersFragment.class, bundle)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });

        // setOnClickListener for text view Customer
        TextView txService = root.findViewById(R.id.txService_T);
        txService.setOnClickListener(v -> {
            setModified(true);

            Bundle bundle = new Bundle();
            if (service != null)
                bundle.putInt(Constants.ID_SERVICE, service.uid);
            else bundle.putInt(Constants.ID_SERVICE, -1);

            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.nav_host_fragment_activity_main, ServicesFragment.class, bundle)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });
    }

    private void init(){
        setModified(false);

        initCheckBoxDone();
        initCheckBoxPaid();

        int idTask = requireArguments().getInt(Constants.ID_TASK);
        if (idTask != -1){
            currentTask = homeViewModel.getTask(idTask);
            setData();
        }
    }

    private void setVisibilityView(){
        ImageButton btnPostpone = root.findViewById(R.id.btnPostpone_T);
        ImageButton btnCancelTask = root.findViewById(R.id.btnCancelTask_T);
        if (currentTask == null){
            btnPostpone.setVisibility(View.GONE);
            btnCancelTask.setVisibility(View.INVISIBLE);
        } else {
            btnPostpone.setVisibility(View.VISIBLE);
            btnCancelTask.setVisibility(View.VISIBLE);
        }
    }

    private void initCheckBoxDone(){
        cbDone = root.findViewById(R.id.cbDone_T);
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setModified(true);
            }
        });
    }

    private void initCheckBoxPaid(){
        cbPaid = root.findViewById(R.id.cbPaid_T);
        cbPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setModified(true);
            }
        });
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

    private void updateDataTask(){
//        App.getInstance().getBusyDao().updateTaskList(task);
    }

    private Service getService(int id){
        return homeViewModel.getService(id);//busyDao.getServiceByID(uid);
    }

    private void setServiceForView(int uid){
        service = getService(uid);

        TextView etService = root.findViewById(R.id.txService_T);
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

        TextView etCustomer = root.findViewById(R.id.txCustomer_T);
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

//    public void onOkClick(View v){
//        if (modified){
//            updateDataTask();
//        }
//
//        finishFragment();
////        finish();
//    }
//
//    public void onCancelClick(View v){
//        if (modified){
//
//        } else finishFragment();//finish();
//    }

    private void backToLastFragment(){
        getParentFragmentManager().popBackStack();
    }

    public void onCancelTaskClick(View v){

    }

    public void onTimeClick(View v){

    }

    public void onPostponeClock(View view) {

    }

//    public void onCustomerClick(View v){
//        setModified(true);
//
//        ConstraintLayout containerElements = root.findViewById(R.id.containerElements_T);
//        containerElements.setVisibility(View.INVISIBLE);
//
//        Bundle bundle = new Bundle();
//        bundle.putInt("some_int", 0); // for example
//
//        getParentFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .add(R.id.nav_host_fragment_activity_main, CustomersFragment.class, bundle)
//                .addToBackStack(null)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .commit();
//    }

//    public void onServiceClick(View v){
//        setModified(true);
//    }

//    public void onDoneClick(Void v){
//        setModified(true);
//    }
//
//    public void onPaidClick(Void v){
//        setModified(true);
//    }

}