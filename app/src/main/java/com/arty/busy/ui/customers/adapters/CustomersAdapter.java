package com.arty.busy.ui.customers.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.OnFragmentCloseListener;
import com.arty.busy.ui.customers.activity.CustomerActivity;
import com.arty.busy.R;
import com.arty.busy.models.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomersViewHolder> {
//    @SuppressLint("StaticFieldLeak")
//    protected static Activity parentActivity;
    private OnFragmentCloseListener closeListener;
    private Context context;
    private List<Customer> listOfCustomers;
    private int uid;
    private boolean isChoice;

    public CustomersAdapter(Context context, int uid, boolean isChoice, OnFragmentCloseListener closeListener) {
//        parentActivity = activity;
        this.context = context;
        this.uid = uid;
        this.listOfCustomers = new ArrayList<>();
        this.isChoice = isChoice;
        this.closeListener = closeListener;
    }

    @NonNull
    @Override
    public CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_customers, parent, false);

        return new CustomersAdapter.CustomersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersViewHolder holder, int position) {
        Customer customer = listOfCustomers.get(position);
        holder.setData(customer);

        // Открываем DialogActivity при клике на элемент списка
        holder.itemView.setOnClickListener(v -> {
            if (isChoice){
                if (closeListener != null) {
                    Bundle result = new Bundle();
                    result.putInt("uid", customer.uid);
                    result.putString("name", customer.toString());
                    closeListener.closeFragment(result); // Закрываем фрагмент
                }
            } else {
                Intent intent = new Intent(context, CustomerActivity.class);
                intent.putExtra("customer", customer);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfCustomers.size();
    }

    class CustomersViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName = itemView.findViewById(R.id.tvName_LOC);
        private final TextView tvPhone = itemView.findViewById(R.id.tvPhone_LOC);
        private final TextView tvPicture = itemView.findViewById(R.id.tvPicture_LOC);
        private final LinearLayout mainLayout = itemView.findViewById(R.id.mainLayout_LOC);

        public CustomersViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(@NonNull Customer customer){
            tvName.setText(customer.first_name + " " + customer.last_name);
            tvPhone.setText(customer.phone);
            if (customer.picture == null){
                tvPicture.setText(customer.first_name.charAt(0) + "" + customer.last_name.charAt(0));
            }
            if (customer.uid == uid){
                mainLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.style_radial_green_transparent));
            } else mainLayout.setForeground(null);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateListOfCustomers(List<Customer> newListOfCustomers){
        listOfCustomers.clear();
        listOfCustomers.addAll(newListOfCustomers);

        notifyDataSetChanged();
    }
}
