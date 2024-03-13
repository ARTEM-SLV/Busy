package com.arty.busy.ui.customers.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.R;
import com.arty.busy.models.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomersViewHolder> {
    private Context context;
    private List<Customer> listOfCustomers;
    protected int uid;

    public CustomersAdapter(Context context, int uid) {
        this.context = context;
        this.uid = uid;
        this.listOfCustomers = new ArrayList<>();
    }

    @NonNull
    @Override
    public CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_customers, parent, false);

        return new CustomersAdapter.CustomersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersViewHolder holder, int position) {
        holder.setData(listOfCustomers.get(position));
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
