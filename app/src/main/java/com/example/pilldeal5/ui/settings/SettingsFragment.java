package com.example.pilldeal5.ui.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pilldeal5.MainActivity;
import com.example.pilldeal5.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.UUID;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    TimePickerDialog picker;
    Button btnGet;
    TextView tvw;
    String time;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        settingsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        //code from https://www.tutlane.com/tutorial/android/android-timepicker-with-examples
        tvw=(TextView)root.findViewById(R.id.textView1);
        tvw.setText("Selected Time: "+ "not yet selected");
        // get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String useruid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //check user's alarm time
        final DatabaseReference myRef = database.getReference().child("users").child(useruid).child("Alarm time");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                time = dataSnapshot.getValue(String.class);
                Log.i("TIME123", time);
                tvw.setText("Selected Time: "+ time);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
            // Failed to read value
        });
        tvw.setText("Selected Time: "+ time);
        btnGet=(Button)root.findViewById(R.id.button1);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                String time = sHour + ":" + sMinute;
                                tvw.setText("Selected Time: "+ time);
                                // get user uid from shared prefs
                                /*Context context = getActivity();
                                SharedPreferences sharedPref = context.getSharedPreferences(
                                        getString(R.string.preference_file_alarm_time), Context.MODE_PRIVATE);
                                String useruid = sharedPref.getString(getString(R.string.preference_file_alarm_time), "error");*/

                                //update firebase
                                //if(!useruid.equals("error")){ //fail safe caso dê erro a ler useruid
                                //}
                                //update Firebase
                                myRef.setValue(time);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        Switch switchButton = root.findViewById(R.id.switch1);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                }
            }
        });





        return root;
    }

}
/*      for embeded time picker, not as popup
        tvw=root.findViewById(R.id.textView1);
        picker=root.findViewById(R.id.timePicker1);
        picker.setIs24HourView(true);
        btnGet=root.findViewById(R.id.button1);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, minute;
                String am_pm;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour = picker.getHour();
                    minute = picker.getMinute();
                } else {
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }
                if (hour > 12) {
                    am_pm = "PM";
                    hour = hour - 12;
                } else {
                    am_pm = "AM";
                }
                tvw.setText("Selected Date: " + hour + ":" + minute + " " + am_pm);
            }
        });*/