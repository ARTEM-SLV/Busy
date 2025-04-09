package com.arty.busy;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.room.Room;

import com.arty.busy.consts.Constants;
import com.arty.busy.consts.Settings;
import com.arty.busy.database.AppDatabase;
import com.arty.busy.database.BusyDao;
import com.arty.busy.date.DateTime;
import com.arty.busy.enums.Sex;
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.arty.busy.models.Task;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class App extends Application {
    private AppDatabase dataBase;
    private BusyDao busyDao;
    private SharedPreferences sharedPreferences;
    private static App instance;
    private char decimalSeparator;

    public static synchronized App getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        dataBase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "busy_db")
                .allowMainThreadQueries()
                .build();

        busyDao = dataBase.busyDao();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalSeparator = symbols.getDecimalSeparator();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.this);
//        fillWorkday();    //for test
        updateSettings();

//        fillDataBaseForTest();    //for test
//        fillCustomers();      //for test
    }

    public AppDatabase getDataBase() {
        return dataBase;
    }

    public void setDataBase(AppDatabase dataBase) {
        this.dataBase = dataBase;
    }

    public BusyDao getBusyDao() {
        return busyDao;
    }

    public void setMainDao(BusyDao busyDao) {
        this.busyDao = busyDao;
    }

    public void updateSettings(){
        Settings.TIME_BEGINNING = sharedPreferences.getInt(Constants.KEY_TIME_BEGINNING, -1);
        Settings.TIME_ENDING = sharedPreferences.getInt(Constants.KEY_TIME_ENDING, -1);
        Settings.TIME_BEGINNING_BREAK = sharedPreferences.getInt(Constants.KEY_TIME_BEGINNING_BREAK, -1);
        Settings.TIME_ENDING_BREAK = sharedPreferences.getInt(Constants.KEY_TIME_ENDING_BREAK, -1);

        Settings.TIME_24_HOURS_FORMAT_DATE = sharedPreferences.getBoolean(Constants.KEY_TIME_24_HOURS_FORMAT_DATE, true);
    }

    public void saveSettings(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Constants.KEY_TIME_BEGINNING, Settings.TIME_BEGINNING);
        editor.putInt(Constants.KEY_TIME_ENDING, Settings.TIME_ENDING);
        editor.putInt(Constants.KEY_TIME_BEGINNING_BREAK, Settings.TIME_BEGINNING_BREAK);
        editor.putInt(Constants.KEY_TIME_ENDING_BREAK, Settings.TIME_ENDING_BREAK);

        editor.putBoolean(Constants.KEY_TIME_24_HOURS_FORMAT_DATE, Settings.TIME_24_HOURS_FORMAT_DATE);

        editor.apply();
    }

    public static void showWarning(String msg, View view, Context context){
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(Color.TRANSPARENT);

        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.snackbar_custom, null);

        // Находим `TextView` и задаем текст программно
        TextView textView = customView.findViewById(R.id.snackbar_text);
        textView.setText(msg);

        // Очищаем стандартный текст и добавляем кастомный Layout
        layout.removeAllViews();
        layout.addView(customView);

        snackbar.setAnchorView(view);
        snackbar.show();
    }

    public static class MoneyTextWatcher implements TextWatcher {

        private final EditText editText;

        String current = "";
        int currentLength;
        int cursorPosition;
        String currSymbol;
        String prevSymbol;
        boolean addedSymbol;

        // Конструктор, который позволяет привязать слушатель к EditText
        public MoneyTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            prevSymbol = String.valueOf(charSequence.charAt(start));
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            cursorPosition = this.editText.getSelectionStart();
            addedSymbol = count > 0;

            int pos = start - before;
            if (pos < 0) {
                pos = start;
            }
            currSymbol = String.valueOf(charSequence.charAt(pos));
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!editable.toString().equals(current)) {
                this.editText.removeTextChangedListener(this);

                if (!addedSymbol && (prevSymbol.equals(".") || prevSymbol.equals(","))) {
                    this.editText.setText(current);
                    this.editText.setSelection(cursorPosition);

                    this.editText.addTextChangedListener(this);
                    return;
                }

                current = App.getFormattedString(editable.toString());
                if (current.isEmpty()) {
                    this.editText.setText("");
                    this.editText.setSelection(0);
                    this.editText.addTextChangedListener(this);
                    return;
                }
                currentLength = current.length();

                this.editText.setText(current);
                if (currSymbol.equals(".") || currSymbol.equals(",")) {
                    this.editText.setSelection(currentLength - 2);
                } else if (cursorPosition > currentLength - 2) {
                    if (cursorPosition > currentLength) {
                        cursorPosition = currentLength;
                    }
                    this.editText.setSelection(cursorPosition);
                } else {
                    this.editText.setSelection(cursorPosition);
                }

                this.editText.addTextChangedListener(this);
            }
        }
    }

    public static void hideKeyboardAndClearFocus(Activity activity) {
        EditText focusedEditText;
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus instanceof EditText) {
            focusedEditText = (EditText) currentFocus;
        } else return;

        // 1. Закрываем клавиатуру
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedEditText.getWindowToken(), 0);
        }, 100);

        // 2. Снимаем фокус
        focusedEditText.clearFocus();
    }

    public static void formatToMoneyString(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) return;

        try {
            // Преобразуем строку в число
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            currencyFormat.setMaximumFractionDigits(2);
            currencyFormat.setMinimumFractionDigits(2);

            double value = Double.parseDouble(text.replace(String.valueOf(instance.decimalSeparator), "."));
            editText.setText(currencyFormat.format(value)); // Отображаем в денежном формате
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFormattedToMoney(Double value) {
        if (value == 0d) return "0" + instance.decimalSeparator + "00";

        try {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            currencyFormat.setMaximumFractionDigits(2);
            currencyFormat.setMinimumFractionDigits(2);

            return currencyFormat.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0" + instance.decimalSeparator + "00";
    }

    public static String getFormattedString(String s){
        String cleanString = s.replaceAll("[^\\d" + instance.decimalSeparator + "]", ""); // Оставляем только цифры
        if (cleanString.isEmpty()) {
            return "0" + instance.decimalSeparator + "00";
        }

        // Разбиваем число на целую и дробную часть
        String[] parts = cleanString.split("[" + instance.decimalSeparator + "]");
        String integerPart = parts[0]; // Целая часть числа
        String decimalPart = (parts.length > 1) ? parts[1] : ""; // Дробная часть
        if (decimalPart.isEmpty() || decimalPart.equals("0")){
            decimalPart = "00";
        } else if (decimalPart.length() == 1){
            decimalPart += "0";
        }

        return integerPart + instance.decimalSeparator +  decimalPart.substring(0, 2);
    }

    public static Double getPriceDouble(EditText editText){
        double res = 0d;

        String input = editText.getText().toString();
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        try {
            Number number = format.parse(input);
            if (number != null) {
                res = number.doubleValue();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    //++ For test
    private void fillDataBaseForTest(){
        // filling of tasks
        fillTasks();

        // filling of services
        fillServices();

        //filling of customers
        fillCustomers();
    }

    private void fillTasks(){
        List<Task> taskList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        DateTime.setStartDay(calendar);
        int uid = 0;
        for (int i = 0; i < 100; i++) {
            Date dt; // = calendar.getTime();
            Date dd = DateTime.getStartDay(calendar);

            byte hour = 7;
            int randomJ = (int) Math.round(Math.random()*5)+1;
            for (int j = 0; j < randomJ; j++) {
                int randomNum = (int) Math.round(Math.random()*4)+1;
                int randomHour = (int) Math.round(Math.random()*2)+1;
                int randomMinute = (int) Math.round(Math.random()*1);

                hour += randomHour;

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, randomMinute==1 ? 30 : 0);
                dt = calendar.getTime();

                Task task = new Task();
                task.uid = uid;
                task.day = dd.getTime();
                task.time = DateTime.timeFormat24.format(dt);
                task.price = 500.00;
                task.id_customer = randomNum;
                task.id_service = randomNum;
                task.duration = 60;

                taskList.add(task);
                uid++;
            }

            calendar.add(Calendar.DATE, 1);
        }

        this.getBusyDao().insertTaskList(taskList);
    }

    private void fillServices(){
        List<Service> serviceList = new ArrayList<>();

        Service service1 = new Service();
        service1.uid = 1;
        service1.title = "Ресницы классика";
        service1.short_title = "РК";
        service1.description = "Ресницы классика";
        service1.price = 500.00;
        service1.duration = 60;
        service1.id_line_of_business = 1;
        serviceList.add(service1);

        Service service2 = new Service();
        service2.uid = 2;
        service2.title = "Ресницы 2D";
        service2.short_title = "Р2D";
        service2.description = "Ресницы 2D";
        service2.price = 500.00;
        service2.duration = 60;
        service2.id_line_of_business = 1;
        serviceList.add(service2);

        Service service3 = new Service();
        service3.uid = 3;
        service3.title = "Ресницы 3D";
        service3.short_title = "Р3D";
        service3.description = "Ресницы классика";
        service3.price = 500.00;
        service3.duration = 90;
        service3.id_line_of_business = 1;
        serviceList.add(service3);

        Service service4 = new Service();
        service4.uid = 4;
        service4.title = "Ресницы 5D";
        service4.short_title = "Р5D";
        service4.description = "Ресницы 5D";
        service4.price = 500.00;
        service4.duration = 120;
        service4.id_line_of_business = 1;
        serviceList.add(service4);

        Service service5 = new Service();
        service5.uid = 5;
        service5.title = "Массаж спины";
        service5.short_title = "МП";
        service5.description = "Массаж спины";
        service5.price = 500.00;
        service5.duration = 60;
        service5.id_line_of_business = 2;
        serviceList.add(service5);

        this.getBusyDao().insertServiceList(serviceList);
    }

    private void fillCustomers(){
        List<Customer> customerList = new ArrayList<>();

        Customer customer1 = new Customer();
        customer1.uid = 1;
        customer1.first_name = "Женя";
        customer1.last_name = "Иванова";
        customer1.phone = "+7(925) 123-45-67";
        customer1.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customerList.add(customer1);

        Customer customer2 = new Customer();
        customer2.uid = 2;
        customer2.first_name = "Максим";
        customer2.last_name = "Петров";
        customer2.phone = "+7(925)987-65-43";
        customer2.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customerList.add(customer2);

        Customer customer3 = new Customer();
        customer3.uid = 3;
        customer3.first_name = "Наташа";
        customer3.last_name = "Романова";
        customer3.phone = "8(925)077-25-70";
        customer3.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customerList.add(customer3);

        Customer customer4 = new Customer();
        customer4.uid = 4;
        customer4.first_name = "Ванда";
        customer4.last_name = "Максимова";
        customer4.phone = "89151234567";
        customer4.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customerList.add(customer4);

        Customer customer5 = new Customer();
        customer5.uid = 5;
        customer5.first_name = "Полина";
        customer5.last_name = "Гага";
        customer5.phone = "+7(910)777-66-55";
        customer5.sex = String.valueOf(Sex.female); //getString(R.string.female);
        customerList.add(customer5);

        this.getBusyDao().insertCustomerList(customerList);
    }

    private void fillWorkday(){
        Settings.TIME_BEGINNING = 8; // 8:00
        Settings.TIME_ENDING = 20; // 20:00

        saveSettings();
    }
    //-- For test
}
