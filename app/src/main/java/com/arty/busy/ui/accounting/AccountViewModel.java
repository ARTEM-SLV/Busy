package com.arty.busy.ui.accounting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AccountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Count fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}