package com.arty.busy.ui.customers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.arty.busy.R;
import com.arty.busy.enums.Sex;
import com.arty.busy.models.Customer;

public class CustomerActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    Spinner sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_customer);

        setWindowParam();

        setSexValues();

        Customer customer = getIntent().getParcelableExtra("customer");
        if (customer != null){
            setData(customer);
        }
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

    private void setData(Customer customer){
        TextView firsName = findViewById(R.id.etFirstName_C);
        firsName.setText(customer.first_name);

        TextView lastName = findViewById(R.id.etLastName_C);
        lastName.setText(customer.last_name);

        TextView phone = findViewById(R.id.etPhone_C);
        phone.setText(customer.phone);

        TextView age = findViewById(R.id.etAge_C);
        age.setText(Integer.toString(customer.age));

        int position = adapter.getPosition(customer.sex);
        sex.setSelection(position);
    }

    private void setSexValues(){
        String[] items = {Sex.male.name(), Sex.female.name()};

        sex = findViewById(R.id.etSex_C);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(adapter);
    }
}