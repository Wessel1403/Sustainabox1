package com.example.sustainabox.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> qrButtonText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        qrButtonText = new MutableLiveData<>();
        qrButtonText.setValue("Open QR scanner");
    }

    public LiveData<String> getFragmentText() {
        return mText;
    }

    public LiveData<String> getQRButtonText() {
        return qrButtonText;
    }
}