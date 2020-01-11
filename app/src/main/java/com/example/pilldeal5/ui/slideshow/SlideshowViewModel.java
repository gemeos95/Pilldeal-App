package com.example.pilldeal5.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {
    // MED INFO  MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO MED INFO

    private MutableLiveData<String> mText;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("In Development");
    }

    public LiveData<String> getText() {
        return mText;
    }
}