package com.arty.busy.ui.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.PictureDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        private TextView tvDay;
        private TextView tvCountTasks;
        private Context context;
        private List<ItemListOfDays> listOfDaysArr;

        private LinearLayout mainContainerLOD;
        private LinearLayout containerRight_LOD;

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("E. dd.MM");

        public ViewHolderLOD(@NonNull View itemView, Context context, List<ItemListOfDays> listOfDaysArr) {
            super(itemView);

            this.context = context;
            this.listOfDaysArr = listOfDaysArr;

            tvDay = itemView.findViewById(R.id.tvDay_LOD);
            tvCountTasks = itemView.findViewById(R.id.tvCountTasks_LOD);
            mainContainerLOD = itemView.findViewById(R.id.mainContainer_LOD);
            containerRight_LOD = itemView.findViewById(R.id.containerRight_LOD);

            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ItemListOfDays itemTaskList){
            tvDay.setText(df.format(itemTaskList.getDate()));
            Resources res = context.getResources();
            List<String> titlesService = itemTaskList.getTitlesService();

            String totalS = res.getString(R.string.total);
            int totalI = titlesService.size();
            if (totalI == 0 ){
                Drawable drawable = context.getDrawable(R.drawable.free);
                containerRight_LOD.setForeground(drawable);
            } else containerRight_LOD.setForeground(null);

            cleanTvTask(res);

            if (totalI == 10){
                mainContainerLOD.setBackgroundResource(R.drawable.style_radial_yellow);
            } else if (totalI > 10){
                mainContainerLOD.setBackgroundResource(R.drawable.style_radial_red);
            } else mainContainerLOD.setBackgroundResource(R.drawable.style_radial_green);

            int i = 1;
            for (String title : titlesService) {
                if (i>10){
                    break;
                }

                int id = res.getIdentifier("tvTask" + i, "id", context.getPackageName());
                TextView tvTask = itemView.findViewById(id);
                tvTask.setText(title);

                i++;
            }

            tvCountTasks.setText(totalS + " " + (totalI));
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
