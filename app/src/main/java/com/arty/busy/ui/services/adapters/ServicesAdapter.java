package com.arty.busy.ui.services.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.OnFragmentCloseListener;
import com.arty.busy.R;
import com.arty.busy.date.Time;
import com.arty.busy.models.Service;
import com.arty.busy.ui.customers.activity.CustomerActivity;
import com.arty.busy.ui.services.activity.ServiceActivity;

import java.util.ArrayList;
import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder> {
    private OnFragmentCloseListener closeListener;
    protected Context context;
    protected List<Service> listOfServices;
    protected int uid;
    private boolean isChoice;
    private LinearLayout mainLayoutBefore;

    public ServicesAdapter(Context context, int uid, boolean isChoice, OnFragmentCloseListener closeListener) {
        this.context = context;
        this.uid = uid;
        this.listOfServices = new ArrayList<>();
        this.isChoice = isChoice;
        this.closeListener = closeListener;
    }

    @NonNull
    @Override
    public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_services, parent, false);

        return new ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesViewHolder holder, int position) {
        Service service = listOfServices.get(position);
        holder.setData(service);

        // Открываем DialogActivity при клике на элемент списка
        holder.itemView.setOnClickListener(v -> {
            if (isChoice){
                if (mainLayoutBefore != null){
                    mainLayoutBefore.setForeground(null);
                }
                v.setForeground(ContextCompat.getDrawable(context, R.drawable.style_radial_green_transparent));
                if (closeListener != null) {
                    Bundle result = new Bundle();
                    result.putParcelable("service", service);
                    closeListener.closeFragment(result); // Закрываем фрагмент
                }
            } else {
                Intent intent = new Intent(context, ServiceActivity.class);
                intent.putExtra("service", service);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfServices.size();
    }

    class ServicesViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvShortTitle = itemView.findViewById(R.id.tvShortTitle_LOS);
        private final TextView tvTitle = itemView.findViewById(R.id.tvTitle_LOS);
        private final TextView tvPrice = itemView.findViewById(R.id.tvPrice_LOS);
        private final TextView tvDuration = itemView.findViewById(R.id.tvDuration_LOS);
        private final LinearLayout mainLayout = itemView.findViewById(R.id.mainLayout_LOS);

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(@NonNull Service service){
            tvShortTitle.setText(service.short_title);
            tvTitle.setText(service.title);
            tvPrice.setText(Double.toString(service.price));
            tvDuration.setText(getTimeDuration(service.duration));
            if (service.uid == uid){
                mainLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.style_radial_green_transparent));
                mainLayoutBefore = mainLayout;
            } else mainLayout.setForeground(null);
        }
    }

    private String getTimeDuration(int duration){
        Time time = new Time(duration);

        return time.toString();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateListOfServices(List<Service> newListOfServices){
        listOfServices.clear();
        listOfServices.addAll(newListOfServices);

        notifyDataSetChanged();
    }
}
