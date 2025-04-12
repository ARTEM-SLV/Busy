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
import com.arty.busy.models.Customer;
import com.arty.busy.models.Service;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class App extends Application {
    private BusyDao busyDao;
    private static SharedPreferences sharedPreferences;
    private static App instance;
    private char decimalSeparator;

    public static synchronized App getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        AppDatabase dataBase = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "busy_db")
                .allowMainThreadQueries()
                .build();

        busyDao = dataBase.busyDao();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalSeparator = symbols.getDecimalSeparator();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.this);
        updateSettings();
    }

    public BusyDao getBusyDao() {
        return busyDao;
    }

    public static void updateSettings(){
        Settings.TIME_BEGINNING = sharedPreferences.getInt(Constants.KEY_TIME_BEGINNING, -1);
        Settings.TIME_ENDING = sharedPreferences.getInt(Constants.KEY_TIME_ENDING, -1);
        Settings.TIME_BEGINNING_BREAK = sharedPreferences.getInt(Constants.KEY_TIME_BEGINNING_BREAK, -1);
        Settings.TIME_ENDING_BREAK = sharedPreferences.getInt(Constants.KEY_TIME_ENDING_BREAK, -1);

        Settings.TIME_24_HOURS_FORMAT_DATE = sharedPreferences.getBoolean(Constants.KEY_TIME_24_HOURS_FORMAT_DATE, true);
    }

    public static void saveSettings(){
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
        private String current = "";
        private int cursorPosition;
        private String currSymbol;
        private String prevSymbol;
        private boolean addedSymbol;

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
                int currentLength = current.length();

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

    public static int dpToPx(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public static int pxToDp(Context context, float px) {
        return Math.round(px / context.getResources().getDisplayMetrics().density);
    }

    public static void fillWorkdayDefault(){
        Settings.TIME_BEGINNING = 8; // 8:00
        Settings.TIME_ENDING = 20; // 20:00

        saveSettings();
    }
}
