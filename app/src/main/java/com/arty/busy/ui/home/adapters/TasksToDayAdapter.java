package com.arty.busy.ui.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.ActivityForFragments;
import com.arty.busy.consts.Constants;
import com.arty.busy.R;
import com.arty.busy.ui.home.items.ItemTaskByHours;

import java.util.ArrayList;
import java.util.List;

public class TasksToDayAdapter extends RecyclerView.Adapter<TasksToDayAdapter.ViewHolderTTD>{
    @SuppressLint("StaticFieldLeak")
    protected static Context context;
    protected static List<ItemTaskByHours> tasksByHours;

    public TasksToDayAdapter(Context context, FragmentManager fragmentManager) {
        TasksToDayAdapter.context = context;
        TasksToDayAdapter.tasksByHours = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolderTTD onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_to_day, parent, false);
        return new ViewHolderTTD(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTTD holder, int position) {
        holder.setData(tasksByHours.get(position));
    }

    @Override
    public int getItemCount() {
        return tasksByHours.size();
    }

    static class ViewHolderTTD extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvDate;
        public Button btnTask;

        public ViewHolderTTD(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvHourITT);
            btnTask = itemView.findViewById(R.id.btnHour_ITT);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ItemTaskByHours itemTaskByHours){
            tvDate.setText(itemTaskByHours.getCurrentTime());

            if (itemTaskByHours.isTask()){
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) btnTask.getLayoutParams();
                params.topMargin = itemTaskByHours.getMinutes() * 6;
                params.height = itemTaskByHours.getDuration() * 6;

                btnTask.setLayoutParams(params);
                btnTask.setText(itemTaskByHours.getTaskTime() + "\n"
                        + itemTaskByHours.getClient() + "\n"
                        + itemTaskByHours.getServices());

                btnTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTask(itemTaskByHours.getId_task());
                    }
                });

                btnTask.setVisibility(View.VISIBLE);
            } else {
                btnTask.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolderTTD holder) {
        super.onViewRecycled(holder);

        // Операция по очистке представления, которое будет переиспользовано
        holder.btnTask.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadData(List<ItemTaskByHours> tasksToDay){
        tasksByHours.clear();
        tasksByHours.addAll(tasksToDay);

        notifyDataSetChanged();
    }

    private static void openTask(int id_task){
        Intent intent = new Intent(TasksToDayAdapter.context, ActivityForFragments.class);
        intent.putExtra(Constants.ID_TASK, id_task);

        TasksToDayAdapter.context.startActivity(intent);
    }
}
