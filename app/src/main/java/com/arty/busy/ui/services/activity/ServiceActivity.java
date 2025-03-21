package com.arty.busy.ui.services.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arty.busy.R;
import com.arty.busy.models.Service;

public class ServiceActivity extends AppCompatActivity {
//    ArrayAdapter<String> adapter;
    private boolean modified;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_service);

        setWindowParam();

        Service service = getIntent().getParcelableExtra("service");
        if (service != null){
            setData(service);
        }

        setOnClickListeners();
    }

    private void setWindowParam(){
        // Получаем параметры окна
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());

            // Устанавливаем ширину и высоту (50% ширины и 30% высоты экрана)
            layoutParams.width = (int) (getScreenWidth() * 0.9);
            layoutParams.height = (int) (getScreenHeight() * 0.8);

            window.setAttributes(layoutParams);
        }
    }

    private int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    @SuppressLint("SetTextI18n")
    private void setData(Service service){
        TextView shortTitle = findViewById(R.id.etShortTitle_S);
        shortTitle.setText(service.short_title);

        TextView title = findViewById(R.id.etTitle_S);
        title.setText(service.title);

        TextView description = findViewById(R.id.tvDescription_S);
        description.setText(service.description);

        TextView duration = findViewById(R.id.etDuration_S);
        duration.setText(Integer.toString(service.duration));

        TextView price = findViewById(R.id.etPrice_S);
        price.setText(Double.toString(service.price));
    }

    private void setOnClickListeners(){
        ImageButton btnOk = findViewById(R.id.btnOk_S);
        btnOk.setOnClickListener(v -> {
            if (modified) {

            }

            finish();
        });

        ImageButton btnCancel = findViewById(R.id.btnCancel_S);
        btnCancel.setOnClickListener(v -> {
            if (modified) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.q_cancel)
                        .setPositiveButton(R.string.cd_yes, (dialog, which) -> {
                            finish();
                        })
                        .setNegativeButton(R.string.cd_no, (dialog, which) -> {

                        }).show();
            } else {
                finish();
            }
        });

        ImageButton btnDeleteTask = findViewById(R.id.btnDeleteTask_S);
        btnDeleteTask.setOnClickListener(v -> {

        });
    }
}
