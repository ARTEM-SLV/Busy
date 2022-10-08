package com.arty.busy.ui.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.R;
import com.arty.busy.ui.home.items.ItemListOfDays;
import com.arty.busy.ui.home.activity.TasksByDayActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ListOfDaysAdapter extends RecyclerView.Adapter<ListOfDaysAdapter.ViewHolderLOD> {
    private Context context;
    private List<ItemListOfDays> listOfDaysArr;

    public ListOfDaysAdapter(Context context) {
        this.context = context;
        listOfDaysArr = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolderLOD onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_days_lite, viewGroup, false);

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
        private TextView tvDay;
        private TextView tvCountTasks;
        private Context context;
        private List<ItemListOfDays> listOfDaysArr;

        private LinearLayout mainContainerLOD;

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("E. dd.MM");

        public ViewHolderLOD(@NonNull View itemView, Context context, List<ItemListOfDays> listOfDaysArr) {
            super(itemView);

            this.context = context;
            this.listOfDaysArr = listOfDaysArr;

            tvDay = itemView.findViewById(R.id.tvDay_LOD);
            tvCountTasks = itemView.findViewById(R.id.tvCountTasks_LOD);
            mainContainerLOD = itemView.findViewById(R.id.mainContainer_LOD);

            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ItemListOfDays itemTaskList){
            tvDay.setText(df.format(itemTaskList.getDate()));

            Resources res = context.getResources();
            List<String> titlesService = itemTaskList.getTitlesService();
            int i = 1;
            for (String title : titlesService) {
                int id = res.getIdentifier("tvTask" + i, "id", context.getPackageName());
                TextView tvTask = itemView.findViewById(id);
                tvTask.setText(title);

                i++;
            }

            String total = res.getString(R.string.total);
            tvCountTasks.setText(total + " " + titlesService.size());
        }

        @Override
        public void onClick(View v) {
//            Log.d("TestOnClick", "Pressed: " + getAdapterPosition());
            Intent intent = new Intent(context, TasksByDayActivity.class);
            intent.putExtra("Date", listOfDaysArr.get(getAdapterPosition()).getDate());
            context.startActivity(intent);
        }
    }

    public void updateAdapter(List<ItemListOfDays> updatedList){
        listOfDaysArr.clear();
        listOfDaysArr.addAll(updatedList);

        notifyDataSetChanged();
    }
}
