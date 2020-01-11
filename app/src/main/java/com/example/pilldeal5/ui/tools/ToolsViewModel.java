package com.example.pilldeal5.ui.tools;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ToolsViewModel extends ViewModel {
//  TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING TRACKING
    private MutableLiveData<String> mText;

    public ToolsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("In Development");
    }

    public LiveData<String> getText() {
        return mText;
    }
}