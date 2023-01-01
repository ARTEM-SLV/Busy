package com.arty.busy.ui.services.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.R;
import com.arty.busy.date.Time;
import com.arty.busy.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder> {
    private Context context;
    private List<Service> listOfServices;

    public ServicesAdapter(Context context) {
        this.context = context;
        listOfServices = new ArrayList<>();
    }

    @NonNull
    @Override
    public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_services, parent, false);

        return new ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesViewHolder holder, int position) {
        holder.setData(listOfServices.get(position));
    }

    @Override
    public int getItemCount() {
        return listOfServices.size();
    }

    class ServicesViewHolder extends RecyclerView.ViewHolder {
        TextView tvShortTitle = itemView.findViewById(R.id.tvShortTitle_LOS);
        TextView tvTitle = itemView.findViewById(R.id.tvTitle_LOS);
        TextView tvPrice = itemView.findViewById(R.id.tvPrice_LOS);
        TextView tvDuration = itemView.findViewById(R.id.tvDuration_LOS);

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Service service){
            tvShortTitle.setText(service.short_title);
            tvTitle.setText(service.title);
            tvPrice.setText(Double.toString(service.price));
            tvDuration.setText(getTimeDuration(service.duration));
        }
    }

    private String getTimeDuration(int duration){
        Time time = new Time(duration);

        return time.getTimeS();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateListOfServices(List<Service> newListOfServices){
        listOfServices.clear();
        listOfServices.addAll(newListOfServices);

        notifyDataSetChanged();
    }
}
