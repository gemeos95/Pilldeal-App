package com.example.pilldeal5;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private String time;
    private String[] spliter;
    private Integer alarmHour;
    private Integer alarmMinute;
    private Ringtone ringtone;
    private Timer t = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get user and alarm time
        Log.i("ALARM123", "called123");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String useruid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //check user's alarm time
        final DatabaseReference myRef = database.getReference().child("users").child(useruid).child("Alarm time");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                time = dataSnapshot.getValue(String.class);
                spliter = time.split(":");
                alarmHour = Integer.parseInt(spliter[0]);
                alarmMinute = Integer.parseInt(spliter[1]);
                Context context;
                Uri ringtoneUri;
                ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                t.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (Calendar.getInstance().getTime().getHours() == alarmHour &&
                        Calendar.getInstance().getTime().getMinutes() == alarmMinute){
                            Log.i("ALARM123", "play123");
                            ringtone.play();
                        }else {
                            ringtone.stop();
                            //aqui talvez dar uma time frame para tocar e dar opção de desligar?
                        }
                    }
                },0, 2000);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
            // Failed to read value
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        ringtone.stop();
        t.cancel();
        super.onDestroy();
    }
}
