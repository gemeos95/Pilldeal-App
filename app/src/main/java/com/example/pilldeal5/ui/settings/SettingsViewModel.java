package com.example.pilldeal5.ui.settings;

import android.app.TimePickerDialog;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    // SETTINGS  SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS SETTINGS
    private MutableLiveData<String> mText;


    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("In Development");

    }

    public LiveData<String> getText() {
        return mText;
    }
}

//Setting