package com.arty.busy.ui.services.adapters;

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
import com.arty.busy.date.Time;
import com.arty.busy.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder> {
    @SuppressLint("StaticFieldLeak")
    protected Context context;
    protected List<Service> listOfServices;
    protected int uid;

    public ServicesAdapter(Context context, int uid) {
        this.context = context;
        this.uid = uid;
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
