package com.arty.busy.ui.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.MyDate;
import com.arty.busy.R;
import com.arty.busy.ui.home.items.ItemListOfDays;
import com.arty.busy.ui.home.activity.TasksToDayActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public Date getDateFormListOfDaysArr(int pos) {
        return listOfDaysArr.get(pos).getDate();
    }

    static class ViewHolderLOD extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private List<ItemListOfDays> listOfDaysArr;

        private LinearLayout containerMain_LOD;
        private LinearLayout containerRight_LOD;
        private TextView tvDay;
        private TextView tvCountTasks;
        private ImageView icoThisDay;
//        private LinearLayout containerLeft_LOD;

        private Date currentDate;

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("E. dd.MM");

        public ViewHolderLOD(@NonNull View view, Context context, List<ItemListOfDays> listOfDaysArr) {
            super(view);
            this.context = context;
            this.listOfDaysArr = listOfDaysArr;

            containerMain_LOD = view.findViewById(R.id.containerMain_LOD);
            containerRight_LOD = view.findViewById(R.id.containerRight_LOD);
            tvDay = view.findViewById(R.id.tvDay_LOD);
            tvCountTasks = view.findViewById(R.id.tvCountTasks_LOD);
            icoThisDay = view.findViewById(R.id.icoThisDay_LOD);
//            containerLeft_LOD = view.findViewById(R.id.containerLeft_LOD);

            currentDate = MyDate.getCurrentDate();

            view.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ItemListOfDays itemTaskList){
            tvDay.setText(df.format(itemTaskList.getDate()));
            Resources res = context.getResources();
            List<String> titlesService = itemTaskList.getTitlesService();

            String totalS = res.getString(R.string.total);
            int totalI = titlesService.size();
            if (totalI == 0 ){
                Drawable drawableFree = context.getDrawable(R.drawable.free);
                containerRight_LOD.setForeground(drawableFree);
            } else containerRight_LOD.setForeground(null);

            cleanTvTask(res);

            if (totalI == 10){
                containerMain_LOD.setBackgroundResource(R.drawable.style_radial_yellow);
            } else if (totalI > 10){
                containerMain_LOD.setBackgroundResource(R.drawable.style_radial_red);
            } else containerMain_LOD.setBackgroundResource(R.drawable.style_radial_green);

            if (currentDate.equals(itemTaskList.getDate())){
//                Drawable drawableStroke = context.getDrawable(R.drawable.style_stroke_darkblue);
//                containerMain_LOD.setForeground(drawableStroke);
                icoThisDay.setVisibility(View.VISIBLE);
            } else {
//                containerMain_LOD.setForeground(null);
                icoThisDay.setVisibility(View.INVISIBLE);
            }


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
            Intent intent = new Intent(context, TasksToDayActivity.class);
            intent.putExtra("Date", listOfDaysArr.get(getAdapterPosition()).getDate());
            context.startActivity(intent);
        }
    }

    public void loadData(List<ItemListOfDays> listOfDays){
        listOfDaysArr.clear();
        listOfDaysArr.addAll(listOfDays);

        notifyDataSetChanged();
    }

    public void addNewDataOnTop(List<ItemListOfDays> listOfDays) {
        listOfDaysArr.addAll(0, listOfDays);
        notifyDataSetChanged();
    }

    public void addNewDataOnBot(List<ItemListOfDays> listOfDays) {
        listOfDaysArr.addAll(this.listOfDaysArr.size(), listOfDays);
        notifyDataSetChanged();
    }

    public void removeElement(int pos){
        listOfDaysArr.remove(pos);
        notifyItemRangeChanged(0, listOfDaysArr.size());
        notifyItemRemoved(pos);
    }
}
