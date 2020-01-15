package com.example.pilldeal5.ui.tracking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrackingViewModel extends ViewModel {
//  TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING
    private MutableLiveData<String> mText;

    public TrackingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("In Development");
    }

    public LiveData<String> getText() {
        return mText;
    }
}