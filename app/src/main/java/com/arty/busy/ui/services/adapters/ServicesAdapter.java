package com.arty.busy.ui.services.adapters;

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
import com.arty.busy.R;
import com.arty.busy.date.Time;
import com.arty.busy.models.Service;
import com.arty.busy.ui.services.activity.ServiceActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServicesAdapter extends ListAdapter<Service, ServicesAdapter.ServicesViewHolder> {

    private final OnFragmentCloseListener closeListener;
    private final Context context;
    private final int uid;
    private final boolean isChoice;
    private LinearLayout mainLayoutBefore;
    private String lastQuery = "";
    private final List<Service> fullList = new ArrayList<>();

    public ServicesAdapter(Context context, int uid, boolean isChoice, OnFragmentCloseListener closeListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.uid = uid;
        this.isChoice = isChoice;
        this.closeListener = closeListener;
    }

    private static final DiffUtil.ItemCallback<Service> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Service oldItem, @NonNull Service newItem) {
            return oldItem.uid == newItem.uid;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Service oldItem, @NonNull Service newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_services, parent, false);
        return new ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesViewHolder holder, int position) {
        Service service = getItem(position);
        holder.setData(service);

        holder.itemView.setOnClickListener(v -> {
            if (isChoice) {
                if (mainLayoutBefore != null) {
                    mainLayoutBefore.setForeground(null);
                }
                v.setForeground(ContextCompat.getDrawable(context, R.drawable.style_radial_green_transparent));
                if (closeListener != null) {
                    Bundle result = new Bundle();
                    result.putParcelable("service", service);
                    closeListener.closeFragment(result);
                }
            } else {
                Intent intent = new Intent(context, ServiceActivity.class);
                intent.putExtra("service", service);
                context.startActivity(intent);
            }
        });
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

        public void setData(@NonNull Service service) {
            tvShortTitle.setText(service.short_title);
            tvTitle.setText(service.title);
            tvPrice.setText(String.format(Locale.getDefault(), "%.2f", service.price));
            tvDuration.setText(getTimeDuration(service.duration));
            if (service.uid == uid) {
                mainLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.style_radial_green_transparent));
                mainLayoutBefore = mainLayout;
            } else mainLayout.setForeground(null);
        }
    }

    private String getTimeDuration(int duration) {
        Time time = new Time(duration);
        return time.toString();
    }

    public void updateListOfServices(List<Service> newList) {
        fullList.clear();
        fullList.addAll(newList);
        filter(lastQuery);
    }

    public void filter(String query) {
        lastQuery = query;

        if (query.isEmpty()) {
            submitList(new ArrayList<>(fullList));
        } else {
            String lower = query.toLowerCase();
            List<Service> filtered = new ArrayList<>();
            for (Service s : fullList) {
                if (s.title != null && s.title.toLowerCase().contains(lower)) {
                    filtered.add(s);
                }
            }
            submitList(filtered);
        }
    }
}
