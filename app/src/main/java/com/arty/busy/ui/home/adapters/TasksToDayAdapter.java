package com.arty.busy.ui.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.R;
import com.arty.busy.ui.home.items.ItemListOfDays;
import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.ArrayList;
import java.util.List;

public class TasksToDayAdapter extends RecyclerView.Adapter<TasksToDayAdapter.ViewHolderLOD> {
    private Context context;
    private List<ItemListOfDays> listOfDaysArr;

    public TasksToDayAdapter(Context context) {
        this.context = context;
        listOfDaysArr = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolderLOD onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_days, viewGroup, false);

        return new ViewHolderLOD(view, context, listOfDaysArr);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderLOD viewHolderLOD, int i) {
        viewHolderLOD.setData(listOfDaysArr.get(i));
    }

    @Override
    public int getItemCount() {
        return listOfDaysArr.size();
    }

    static class ViewHolderLOD extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private List<ItemTaskList> taskLists;

        public ViewHolderLOD(@NonNull View itemView, Context context, List<ItemListOfDays> listOfDaysArr) {
            super(itemView);
            this.context = context;
        }

        @SuppressLint("SetTextI18n")
        public void setData(ItemListOfDays itemTaskList){

        }

        private void cleanTvTask(Resources res){
            for (int i = 1; i <= 10; i++) {
                int id = res.getIdentifier("tvTask" + i, "id", context.getPackageName());
                TextView tvTask = itemView.findViewById(id);
                tvTask.setText("");
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void updateAdapter(List<ItemListOfDays> updatedList){
        listOfDaysArr.clear();
        listOfDaysArr.addAll(updatedList);

        notifyDataSetChanged();
    }
}
