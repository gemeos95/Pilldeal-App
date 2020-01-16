package com.example.pilldeal5.ui.medinfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MedInfoViewModel extends ViewModel {
    // MED INFO  MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO

    private MutableLiveData<String> mText;

    public MedInfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("In Development");
    }

    public LiveData<String> getText() {
        return mText;
    }
}