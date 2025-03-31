package com.arty.busy.ui.home.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.arty.busy.consts.Constants;
import com.arty.busy.R;
import com.arty.busy.databinding.ItemListOfDaysBinding;
import com.arty.busy.date.DateTime;
import com.arty.busy.ui.home.items.ItemListOfDays;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListOfDaysAdapter extends RecyclerView.Adapter<ListOfDaysAdapter.ViewHolderLOD> {
    protected Activity activity;
    protected Context context;
    protected List<ItemListOfDays> listOfDaysArr;
    protected FragmentManager fragmentManager;
    private static Date dateSelectedElement;

    public ListOfDaysAdapter(Context context, FragmentManager fragmentManager, Activity activity) {
        this.activity = activity;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.listOfDaysArr = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolderLOD onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        ItemListOfDaysBinding binding = ItemListOfDaysBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolderLOD(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderLOD viewHolderLOD, int position) {
        ItemListOfDays item = listOfDaysArr.get(position);
        viewHolderLOD.bind(item, context);

        viewHolderLOD.itemView.setOnClickListener(v -> {
            // Используем контекст для навигации
            dateSelectedElement = listOfDaysArr.get(viewHolderLOD.getAdapterPosition()).getDate();

            Bundle bundle = new Bundle();
            bundle.putLong(Constants.KEY_DATE, dateSelectedElement.getTime());

            // Используем контекст для получения NavController
            NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_tasks_to_day, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return listOfDaysArr.size();
    }

    static class ViewHolderLOD extends RecyclerView.ViewHolder {
        private final ItemListOfDaysBinding binding;
        private final Date currentDate;
        DateFormat df = new SimpleDateFormat("E. dd.MM",  Locale.getDefault());

        public ViewHolderLOD(ItemListOfDaysBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            currentDate = DateTime.getCurrentStartDate();
        }

        public void bind(ItemListOfDays itemTaskList, Context context){
            binding.tvDayLOD.setText(df.format(itemTaskList.getDate()));
            List<String> listTimeService = itemTaskList.getTimeService();
            List<String> listTitlesService = itemTaskList.getTitlesService();

            int total = listTitlesService.size();
            if (total == 0 ){
                Drawable drawableFree = AppCompatResources.getDrawable(context, R.drawable.free);
                binding.containerRightLOD.setForeground(drawableFree);
            } else binding.containerRightLOD.setForeground(null);

            cleanTvTask();

            if (total == 10){
                binding.containerMainLOD.setBackgroundResource(R.drawable.style_radial_yellow);
            } else if (total > 10){
                binding.containerMainLOD.setBackgroundResource(R.drawable.style_radial_red);
            } else binding.containerMainLOD.setBackgroundResource(R.drawable.style_radial_green);

            if (currentDate.equals(itemTaskList.getDate())){
                binding.icoThisDayLOD.setVisibility(View.VISIBLE);
            } else {
                binding.icoThisDayLOD.setVisibility(View.INVISIBLE);
            }

            int nom;
            for (int i = 0; i < total; i++) {
                nom = i+1;

                if (nom>10){
                    break;
                }

                String time = listTimeService.get(i);
                String title = listTitlesService.get(i);

                TextView tvTask = getTaskTextView(nom);
                if (tvTask != null){
                    tvTask.setText(title);
                }

                TextView tvTaskTime = getTaskTimeTextView(nom);
                if (tvTaskTime != null){
                    tvTaskTime.setText(time);
                }
            }

            binding.tvCountTasksLOD.setText(context.getString(R.string.total_with_count, total));
        }

        private void cleanTvTask(){
            for (int i = 1; i <= 10; i++) {
                TextView tvTask = getTaskTextView(i); //binding.getRoot().findViewById(2000 + i);
                if (tvTask != null){
                    tvTask.setText("");
                }

                TextView tvTaskTime = getTaskTimeTextView(i);
                if (tvTaskTime != null){
                    tvTaskTime.setText("");
                }
            }
        }

        private TextView getTaskTextView(int position) {
            switch (position) {
                case 1: return binding.tvTask1;
                case 2: return binding.tvTask2;
                case 3: return binding.tvTask3;
                case 4: return binding.tvTask4;
                case 5: return binding.tvTask5;
                case 6: return binding.tvTask6;
                case 7: return binding.tvTask7;
                case 8: return binding.tvTask8;
                case 9: return binding.tvTask9;
                case 10: return binding.tvTask10;
                default: return null;
            }
        }
        private TextView getTaskTimeTextView(int position) {
            switch (position) {
                case 1: return binding.tvTask1Time;
                case 2: return binding.tvTask2Time;
                case 3: return binding.tvTask3Time;
                case 4: return binding.tvTask4Time;
                case 5: return binding.tvTask5Time;
                case 6: return binding.tvTask6Time;
                case 7: return binding.tvTask7Time;
                case 8: return binding.tvTask8Time;
                case 9: return binding.tvTask9Time;
                case 10: return binding.tvTask10Time;
                default:return null;
            }
        }

    }

    public void loadData(List<ItemListOfDays> listOfDays){
        // Запоминаем старую длину списка для использования в методах notify
        int oldSize = listOfDaysArr.size();

        // Очищаем текущий список и добавляем новые данные
        listOfDaysArr.clear();
        listOfDaysArr.addAll(listOfDays);

        // Уведомляем адаптер о том, что данные изменились
        if (oldSize == 0) {
            // Если список был пуст, добавляем все новые элементы
            notifyItemRangeInserted(0, listOfDaysArr.size());
        } else {
            // Если данные обновились, просто обновляем весь диапазон
            notifyItemRangeChanged(0, listOfDaysArr.size());
        }
    }

    public void addNewDataOnTop(List<ItemListOfDays> listOfDays) {
        listOfDaysArr.addAll(0, listOfDays);
        notifyItemRangeInserted(0, listOfDays.size()); //notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addNewDataOnBot(List<ItemListOfDays> listOfDays) {
        int startPos = listOfDaysArr.size();
        listOfDaysArr.addAll(listOfDaysArr.size(), listOfDays);
        notifyItemRangeInserted(startPos, listOfDays.size()); //notifyDataSetChanged();
    }

    public void removeElement(int pos){
        listOfDaysArr.remove(pos);
        notifyItemRangeChanged(0, listOfDaysArr.size());
        notifyItemRemoved(pos);
    }

    public Date getSelectedDate(){
        return dateSelectedElement;
    }
}
