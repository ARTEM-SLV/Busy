package com.arty.busy.ui.count;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Count fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}