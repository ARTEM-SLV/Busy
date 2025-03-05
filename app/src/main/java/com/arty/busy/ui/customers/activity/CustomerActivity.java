package com.arty.busy.ui.customers.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arty.busy.R;
import com.arty.busy.enums.Sex;
import com.arty.busy.models.Customer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CustomerActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ArrayAdapter<String> adapter;
    private Spinner sex;
    private ImageView ivPhoto;
    private byte[] imageData;

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

    private void setOnClickListeners(){
        ImageButton btnOk = findViewById(R.id.btnOk_C);
        btnOk.setOnClickListener(v -> {
            finish();
        });

        ImageButton btnCancel = findViewById(R.id.btnCancel_C);
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        ImageButton btnDeleteTask = findViewById(R.id.btnDeleteTask_C);
        btnDeleteTask.setOnClickListener(v -> {

        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        handleImageSelection(imageUri);
                    }
                }
        );

        ivPhoto = findViewById(R.id.ivPhoto_C);
        ivPhoto.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // Метод для обработки выбранного изображения
    private void handleImageSelection(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ivPhoto.setImageBitmap(bitmap);
            imageData = convertBitmapToByteArray(bitmap); // Конвертируем в byte[]
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для конвертации Bitmap в byte[]
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}