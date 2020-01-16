package com.example.pilldeal5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class reconnect extends AppCompatActivity {

    //Bluethooth connection
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private BluetoothSocket mBTSocket;

    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
    private ProgressDialog progressDialog;
    ListView listView;
    TextView statusTextView;
    Button searchButton;
    Button connect;
    ArrayList<String> bluetoothDevices = new ArrayList<>();
    ArrayList<String> adresses = new ArrayList<>(); //they have to have an adress but not a name

    ArrayAdapter arrayAdapter;

    BluetoothAdapter bluetoothAdapter;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null) {
                Log.i("Action",action);
            }
            Log.i("Entrou","It worked");

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                statusTextView.setText("Finished");
                searchButton.setEnabled(true);
            }else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//alows us to grab information to the device
                String name = device.getName();//get name
                String adress = device.getAddress();//get name
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                Log.i("DEVice found", "Name:" + name+ "Adress:" + adress+ "rssi:" + rssi);

                //FILTER RSSI

                if(!adresses.contains(adress)){
                    adresses.add(adress);

                    //UPDATE DATA ON LISTVIEW
                    String deviceString;
                    if(name == null || name.equals("")){
                        deviceString = "Adress: " + adress + "- RSSI: "+ rssi + "dBm";
                    }else{
                        deviceString = "Name: " + name + "- RSSI: "+ rssi + "dBm";
                    }
                    bluetoothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();//notify list about the change
                }
            }
        }
    };

    public void searchClicked(View view) {
        statusTextView.setText("Searching...");
        //Toast.makeText(reconnect.this, "onclick", Toast.LENGTH_SHORT).show();
        Log.i("onclick", "asdsa");

        searchButton.setEnabled(false);

        bluetoothDevices.clear();
        adresses.clear();

        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);



        if (ContextCompat.checkSelfPermission(reconnect.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(reconnect.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},0);
        }

        listView = findViewById(R.id.listView);
        statusTextView = findViewById(R.id.statusTextView);
        searchButton = findViewById(R.id.searchButton);
        connect = findViewById(R.id.searchButton);

        //create an adapter, pass the layout and then pass the array created
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,bluetoothDevices);
        //set that the the list view
        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //Search paired Devices----------------------------

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                bluetoothDevices.add(device.getName() + "\n" + device.getAddress());
                arrayAdapter.notifyDataSetChanged();//notify list about the change
            }
        } else {
            bluetoothDevices.add("No Paired Devices.");
            arrayAdapter.notifyDataSetChanged();//notify list about the change
        }



        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);

        //Search NEW Devices--------------------------------------------------------------------
        broadcastReceiver.onReceive(this, getIntent());

        //Turn On Bluethooth--------------------------------------------------------------------
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_ENABLE_REQUEST);
        }

        //Connect to device

        /*
        * 1. Chose possition, save in array, name etc..(see examples)
        * 2. Chose possition with function
        * 3. Code to connect
        * */

        //connect to the specific device!

    }




}


