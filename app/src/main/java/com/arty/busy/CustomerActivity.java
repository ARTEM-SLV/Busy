package com.arty.busy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.arty.busy.enums.Sex;
import com.arty.busy.models.Customer;

public class CustomerActivity extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    Spinner sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        setSexValues();

        Customer customer = getIntent().getParcelableExtra("customer");
        if (customer != null){
            setData(customer);
        }
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