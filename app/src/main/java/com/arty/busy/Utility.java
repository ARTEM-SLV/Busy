package com.arty.busy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Utility {
    public static DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    public static char decimalSeparator = symbols.getDecimalSeparator();

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

    public static class PhoneMaskTextWatcher implements TextWatcher {
        private static final String MASK = "+7(XXX) XXX-XX-XX";
        private static final char MASK_CHAR = 'X';
        private final EditText editText;
        private boolean isFormatting = false;
        private boolean deletingHyphen = false;
        private int hyphenStart = 0;

        public PhoneMaskTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (isFormatting) return;

            deletingHyphen = count == 1 && after == 0
                    && !Character.isDigit(s.charAt(start));
            if (deletingHyphen) {
                hyphenStart = start;
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isFormatting) return;

            isFormatting = true;

            // Удаляем все нецифровые символы
            String digits = s.toString().replaceAll("[^0-9]", "");

            // Применяем маску
            StringBuilder formatted = new StringBuilder();
            int digitIndex = 0;

            for (int i = 0; i < MASK.length() && digitIndex < digits.length(); i++) {
                char maskChar = MASK.charAt(i);
                if (maskChar == MASK_CHAR) {
                    formatted.append(digits.charAt(digitIndex));
                    digitIndex++;
                } else {
                    formatted.append(maskChar);
                }
            }

            // Если удаляли дефис, корректируем позицию
            if (deletingHyphen) {
                s.delete(hyphenStart, hyphenStart + 1); // Теперь работает, т.к. s - Editable
                deletingHyphen = false;
            }

            // Обновляем текст
            if (!formatted.toString().equals(s.toString())) {
                s.replace(0, s.length(), formatted.toString());
            }

            isFormatting = false;
        }
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

                current = getFormattedString(editable.toString());
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

            double value = Double.parseDouble(text.replace(String.valueOf(decimalSeparator), "."));
            editText.setText(currencyFormat.format(value)); // Отображаем в денежном формате
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFormattedToMoney(Double value) {
        if (value == 0d) return "0" + decimalSeparator + "00";

        try {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            currencyFormat.setMaximumFractionDigits(2);
            currencyFormat.setMinimumFractionDigits(2);

            return currencyFormat.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0" + decimalSeparator + "00";
    }

    public static String getFormattedString(String s){
        String cleanString = s.replaceAll("[^\\d" + decimalSeparator + "]", ""); // Оставляем только цифры
        if (cleanString.isEmpty()) {
            return "0" + decimalSeparator + "00";
        }

        // Разбиваем число на целую и дробную часть
        String[] parts = cleanString.split("[" + decimalSeparator + "]");
        String integerPart = parts[0]; // Целая часть числа
        String decimalPart = (parts.length > 1) ? parts[1] : ""; // Дробная часть
        if (decimalPart.isEmpty() || decimalPart.equals("0")){
            decimalPart = "00";
        } else if (decimalPart.length() == 1){
            decimalPart += "0";
        }

        return integerPart + decimalSeparator +  decimalPart.substring(0, 2);
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
}
