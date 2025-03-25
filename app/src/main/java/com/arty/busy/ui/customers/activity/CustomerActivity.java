package com.arty.busy.ui.customers.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.arty.busy.R;
import com.arty.busy.databinding.ActivityCustomerBinding;
import com.arty.busy.enums.Sex;
import com.arty.busy.models.Customer;
import com.arty.busy.ui.customers.CustomersViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CustomerActivity extends AppCompatActivity {
    private CustomersViewModel customersViewModel;
    private @NonNull ActivityCustomerBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ArrayAdapter<String> adapter;
    private Spinner sex;
    private ImageView ivPhoto;
    private byte[] imageData;
    private Customer customer, modifiedCustomer;
    private boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_customer);

        customersViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CustomersViewModel.class);

        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setWindowParam();

//        setSexValues();
//        Customer customer = getIntent().getParcelableExtra("customer");

//        setData();
//        setOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initSexValues();
        setData();
        setView();

        setOnClickListeners();
        setOnEditors();

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

    private void initSexValues(){
        String[] items = {Sex.male.name(), Sex.female.name()};

//        sex = findViewById(R.id.etSex_C);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.etSexC.setAdapter(adapter);
    }

    private void setData(){
        if (customer == null){
            customer = getIntent().getParcelableExtra("customer");
            if (customer == null){
                modifiedCustomer = new Customer();
            } else {
                modifiedCustomer = new Customer(customer);
            }
        }
        isNew = customer == null;
    }

    @SuppressLint("SetTextI18n")
    private void setView(){
//        TextView tvPhoto = findViewById(R.id.tvPhoto_C);
        binding.tvPhotoC.setText(modifiedCustomer.shortTitle());

//        TextView firsName = findViewById(R.id.etFirstName_C);
        binding.etFirstNameC.setText(modifiedCustomer.first_name);

//        TextView lastName = findViewById(R.id.etLastName_C);
        binding.etFirstNameC.setText(modifiedCustomer.last_name);

//        TextView phone = findViewById(R.id.etPhone_C);
        binding.etPhoneC.setText(modifiedCustomer.phone);

//        TextView age = findViewById(R.id.etAge_C);
        binding.etAgeC.setText(Integer.toString(modifiedCustomer.age));

        int position = adapter.getPosition(modifiedCustomer.sex);
        binding.etSexC.setSelection(position);

        if (isNew) {
            binding.layoutPhotoC.setVisibility(View.INVISIBLE);
        }
    }

    private void setOnClickListeners(){
//        ImageButton btnOk = findViewById(R.id.btnOk_C);
        binding.btnOkC.setOnClickListener(v -> {
            beforeFinishActivity(false);
        });

//        ImageButton btnCancel = findViewById(R.id.btnCancel_C);
        binding.btnCancelC.setOnClickListener(v -> {
            beforeFinishActivity(true);
        });

//        ImageButton btnDeleteTask = findViewById(R.id.btnDeleteTask_C);
        binding.btnDeleteTaskC.setOnClickListener(v -> {
            showDialogDeleteCustomer();
        });

        binding.etSexC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                int position = adapter.getPosition(position);
//                binding.etSexC.getItemAtPosition(position);
                String selectedItem = parent.getItemAtPosition(position).toString();
                modifiedCustomer.sex = selectedItem;
                binding.etSexC.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        imagePickerLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri imageUri = result.getData().getData();
//                        handleImageSelection(imageUri);
//                    }
//                }
//        );
//
//        ivPhoto = findViewById(R.id.ivPhoto_C);
//        ivPhoto.setOnClickListener(v -> openGallery());
    }

    private void setOnEditors(){
        setOnEditorsForFirstName();
        setOnEditorsForLastName();
        setOnEditorsForPhone();
        setOnEditorsForAge();
    }

    private void setOnEditorsForFirstName(){
        binding.etFirstNameC.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                modifiedCustomer.first_name = binding.etFirstNameC.getText().toString();
                return true;
            }
            return false;
        });
        binding.etFirstNameC.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                modifiedCustomer.first_name = binding.etFirstNameC.getText().toString();
            }
        });
    }

    private void setOnEditorsForLastName(){
        binding.etLastNameC.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                modifiedCustomer.last_name = binding.etLastNameC.getText().toString();
                return true;
            }
            return false;
        });
        binding.etLastNameC.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                modifiedCustomer.last_name = binding.etLastNameC.getText().toString();
            }
        });
    }

    private void setOnEditorsForPhone(){
        binding.etPhoneC.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                modifiedCustomer.phone = binding.etPhoneC.getText().toString();
                return true;
            }
            return false;
        });
        binding.etPhoneC.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                modifiedCustomer.phone = binding.etPhoneC.getText().toString();
            }
        });
    }

    private void setOnEditorsForAge(){
        binding.etAgeC.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String text = binding.etAgeC.getText().toString();
                if (!text.isEmpty()){
                    modifiedCustomer.age = Integer.parseInt(text);
                };
                return true;
            }
            return false;
        });
        binding.etAgeC.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = binding.etAgeC.getText().toString();
                if (!text.isEmpty()){
                    modifiedCustomer.age = Integer.parseInt(text);
                };
            }
        });
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

    private void beforeFinishActivity(boolean isClosing){
        if (!modifiedCustomer.equals(customer)) {
            if (isClosing) {
                showDialogCloseActivity();
            } else {
                if (!checkFilling()) {
                    return;
                }
                if (isNew) {
                    customersViewModel.insertCustomer(modifiedCustomer);
                } else {
                    customersViewModel.updateCustomer(modifiedCustomer);
                }

                finish();
            }
        } else {
            finish();
        }

//        if (isNew) {
//            if (isClosing) {
//                showDialogCloseActivity();
//                finish();
//            } else {
//                customersViewModel.insertCustomer(modifiedCustomer);
//            }
//        } else {
//            if (!modifiedCustomer.equals(customer)) {
//                if (isClosing) {
//                    showDialogCloseActivity();
//                } else {
//                    if (!checkFilling()) {
//                        return;
//                    }
//                    if (isNew) {
//                        customersViewModel.insertCustomer(modifiedCustomer);
//                    } else {
//                        customersViewModel.updateCustomer(modifiedCustomer);
//                    }
//
//                    finish();
//                }
//            } else {
//                finish();
//            }
//        }
    }

    private boolean checkFilling(){
        boolean result = true;

        if (modifiedCustomer.first_name.isEmpty()){
            showMessage("Не указан день");
            return false;
        }

        return result;
    }

    private void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showDialogCloseActivity(){
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDialogDeleteCustomer(){
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(R.string.q_cancel)
                .setPositiveButton(R.string.cd_yes, (dialog, which) -> DeleteCustomer())
                .setNegativeButton(R.string.cd_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void DeleteCustomer(){
        customersViewModel.deleteCustomer(customer);
        finish();
    }
}