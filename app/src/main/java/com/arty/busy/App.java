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

    public static void fillWorkdayDefault(){
        Settings.TIME_BEGINNING = 8; // 8:00
        Settings.TIME_ENDING = 20; // 20:00

        saveSettings();
    }
}
