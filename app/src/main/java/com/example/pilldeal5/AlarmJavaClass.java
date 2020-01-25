package com.example.pilldeal5;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

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

public class AlarmJavaClass {

    public static void AlarmServiceMethod(final Context context) {
        final String[] time = new String[1];
        final String[][] spliter = new String[1][1];
        final Integer[] alarmHour = new Integer[1];
        final Integer[] alarmMinute = new Integer[1];
        final Ringtone[] ringtone = new Ringtone[1];
        final Timer t = new Timer();

        Log.i("ALARM123", "called123");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String useruid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //check user's alarm time
        final DatabaseReference myRef = database.getReference().child("users").child(useruid).child("Alarm time");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    time[0] = dataSnapshot.getValue(String.class);
                    spliter[0] = time[0].split(":");
                    alarmHour[0] = Integer.parseInt(spliter[0][0]);
                    alarmMinute[0] = Integer.parseInt(spliter[0][1]);
                    ringtone[0] = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                    t.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (Calendar.getInstance().getTime().getHours() == alarmHour[0] &&
                                    Calendar.getInstance().getTime().getMinutes() == alarmMinute[0]) {
                                Log.i("ALARM123", "play123");
                                ringtone[0].play();
                            } else {
                                ringtone[0].stop();
                                //aqui talvez dar uma time frame para tocar e dar opção de desligar?
                            }
                        }
                    }, 0, 2000);

                }catch (Exception e){
                    Log.i("No alarm","Update time of the alarm");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
            // Failed to read value
        });
    }
}
