package com.arty.busy.ui.home.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.consts.Constants;
import com.arty.busy.R;
import com.arty.busy.date.DateTime;
import com.arty.busy.ui.home.items.ItemListOfDays;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListOfDaysAdapter extends RecyclerView.Adapter<ListOfDaysAdapter.ViewHolderLOD> {
    @SuppressLint("StaticFieldLeak")
    protected static Activity parentActivity;
    @SuppressLint("StaticFieldLeak")
    protected static Context context;
    protected static List<ItemListOfDays> listOfDaysArr;
    protected static FragmentManager fragmentManager;
    private static Date dateSelectedElement;

    public ListOfDaysAdapter(Context context, FragmentManager fragmentManager, Activity activity) {
        parentActivity = activity;
        ListOfDaysAdapter.context = context;
        ListOfDaysAdapter.fragmentManager = fragmentManager;
        listOfDaysArr = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolderLOD onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_days, viewGroup, false);

        return new ViewHolderLOD(view);
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
        private final LinearLayout containerMain_LOD;
        private final LinearLayout containerRight_LOD;
        private final TextView tvDay;
        private final TextView tvCountTasks;
        private final ImageView icoThisDay;
        private final Date currentDate;

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("E. dd.MM");

        public ViewHolderLOD(@NonNull View view) {
            super(view);

            containerMain_LOD = view.findViewById(R.id.containerMain_LOD);
            containerRight_LOD = view.findViewById(R.id.containerRight_LOD);
            tvDay = view.findViewById(R.id.tvDay_LOD);
            tvCountTasks = view.findViewById(R.id.tvCountTasks_LOD);
            icoThisDay = view.findViewById(R.id.icoThisDay_LOD);
//            containerLeft_LOD = view.findViewById(R.id.containerLeft_LOD);

            currentDate = DateTime.getCurrentStartDate();

            view.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ItemListOfDays itemTaskList){
            tvDay.setText(df.format(itemTaskList.getDate()));
            Resources res = context.getResources();
            List<String> listTimeService = itemTaskList.getTimeService();
            List<String> listTitlesService = itemTaskList.getTitlesService();

            String totalS = res.getString(R.string.total);
            int totalI = listTitlesService.size();
            if (totalI == 0 ){
                @SuppressLint("UseCompatLoadingForDrawables")
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
                icoThisDay.setVisibility(View.VISIBLE);
            } else {
                icoThisDay.setVisibility(View.INVISIBLE);
            }


            int nom;
            for ( int i = 0; i < totalI; i++) {
                nom = i+1;

                if (nom>10){
                    break;
                }

                String time = listTimeService.get(i);
                String title = listTitlesService.get(i);

                @SuppressLint("DiscouragedApi") int idTime = res.getIdentifier("tvTask" + nom + "Time", "id", context.getPackageName());
                TextView tvTaskTime = itemView.findViewById(idTime);
                tvTaskTime.setText(time);

                @SuppressLint("DiscouragedApi") int idService = res.getIdentifier("tvTask" + nom, "id", context.getPackageName());
                TextView tvTask = itemView.findViewById(idService);
                tvTask.setText(title);
            }

            tvCountTasks.setText(totalS + " " + (totalI));
        }

        private void cleanTvTask(Resources res){
            for (int i = 1; i <= 10; i++) {
                @SuppressLint("DiscouragedApi") int idTime = res.getIdentifier("tvTask" + i + "Time", "id", context.getPackageName());
                TextView tvTaskTime = itemView.findViewById(idTime);
                tvTaskTime.setText("");

                @SuppressLint("DiscouragedApi") int idService = res.getIdentifier("tvTask" + i, "id", context.getPackageName());
                TextView tvTask = itemView.findViewById(idService);
                tvTask.setText("");
            }
        }

        @Override
        public void onClick(View v) {
            dateSelectedElement = listOfDaysArr.get(getAdapterPosition()).getDate();

            Bundle bundle = new Bundle();
            bundle.putLong(Constants.KEY_DATE, dateSelectedElement.getTime());

            NavController navController = Navigation.findNavController(parentActivity, R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_tasks_to_day, bundle);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadData(List<ItemListOfDays> listOfDays){
        listOfDaysArr.clear();
        listOfDaysArr.addAll(listOfDays);

        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addNewDataOnTop(List<ItemListOfDays> listOfDays) {
        listOfDaysArr.addAll(0, listOfDays);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addNewDataOnBot(List<ItemListOfDays> listOfDays) {
        listOfDaysArr.addAll(listOfDaysArr.size(), listOfDays);
        notifyDataSetChanged();
    }

    public void removeElement(int pos){
        listOfDaysArr.remove(pos);
        notifyItemRangeChanged(0, listOfDaysArr.size());
        notifyItemRemoved(pos);
    }

    public Date getSelectedDate(){
        return dateSelectedElement;
    }

    public Date getDate(int position) {
        return listOfDaysArr.get(position).getDate();
    }
}
