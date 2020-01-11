package com.example.pilldeal5.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {
    // SETTINGS  SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS
    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("In Development");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

//Setting