package com.arty.busy.ui.customers.adapters;

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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.OnFragmentCloseListener;
import com.arty.busy.ui.customers.activity.CustomerActivity;
import com.arty.busy.R;
import com.arty.busy.models.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomersAdapter extends ListAdapter<Customer, CustomersAdapter.CustomersViewHolder> {
    private final OnFragmentCloseListener closeListener;
    private final Context context;
    private final List<Customer> listOfCustomers;
    private final int uid;
    private final boolean isChoice;
    private LinearLayout mainLayoutBefore;
    private String lastQuery = "";

    public CustomersAdapter(Context context, int uid, boolean isChoice, OnFragmentCloseListener closeListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.uid = uid;
        this.isChoice = isChoice;
        this.closeListener = closeListener;
        this.listOfCustomers = new ArrayList<>();
    }

    @NonNull
    @Override
    public CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_customers, parent, false);
        return new CustomersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersViewHolder holder, int position) {
        Customer customer = getItem(position);
        holder.setData(customer);

        holder.itemView.setOnClickListener(v -> {
            if (isChoice) {
                if (mainLayoutBefore != null) {
                    mainLayoutBefore.setForeground(null);
                }
                v.setForeground(ContextCompat.getDrawable(context, R.drawable.style_radial_green_transparent));
                if (closeListener != null) {
                    Bundle result = new Bundle();
                    result.putParcelable("customer", customer);
                    closeListener.closeFragment(result);
                }
            } else {
                Intent intent = new Intent(context, CustomerActivity.class);
                intent.putExtra("customer", customer);
                context.startActivity(intent);
            }
        });
    }

    static final DiffUtil.ItemCallback<Customer> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Customer oldItem, @NonNull Customer newItem) {
            return oldItem.uid == newItem.uid;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Customer oldItem, @NonNull Customer newItem) {
            return oldItem.equals(newItem);
        }
    };

    class CustomersViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName = itemView.findViewById(R.id.tvName_LOC);
        private final TextView tvPhone = itemView.findViewById(R.id.tvPhone_LOC);
        private final TextView tvPicture = itemView.findViewById(R.id.tvPicture_LOC);
        private final LinearLayout mainLayout = itemView.findViewById(R.id.mainLayout_LOC);

        public CustomersViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(@NonNull Customer customer) {
            tvName.setText(customer.toString());
            tvPhone.setText(customer.phone);
            if (customer.picture == null) {
                tvPicture.setText(customer.shortTitle());
            }
            if (customer.uid == uid) {
                mainLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.style_radial_green_transparent));
                mainLayoutBefore = mainLayout;
            } else {
                mainLayout.setForeground(null);
            }
        }
    }

    public void updateListOfCustomers(List<Customer> newListOfCustomers) {
        listOfCustomers.clear();
        listOfCustomers.addAll(newListOfCustomers);
        filter(lastQuery);
    }

    public void filter(String query) {
        lastQuery = query;
        List<Customer> newFilteredList = new ArrayList<>();

        if (query.isEmpty()) {
            newFilteredList.addAll(listOfCustomers);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Customer customer : listOfCustomers) {
                if ((customer.first_name != null && customer.first_name.toLowerCase().contains(lowerCaseQuery)) ||
                        (customer.last_name != null && customer.last_name.toLowerCase().contains(lowerCaseQuery)) ||
                        (customer.phone != null && customer.phone.toLowerCase().contains(lowerCaseQuery))) {
                    newFilteredList.add(customer);
                }
            }
        }

        submitList(newFilteredList);
    }
}
