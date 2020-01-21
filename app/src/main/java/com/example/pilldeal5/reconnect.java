package com.example.pilldeal5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class reconnect extends AppCompatActivity {
    ListView listView;
    TextView statusTextView;
    Button searchButton;
    Button disconnect;
    TextView status;
    TextView received;
    int mSelectedItem;
    int nummessages = 0;
    String finalstring;



    //Bluethooth connection
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> bluetoothDevices = new ArrayList<>();



    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_MESSAGE_RECIEVED = 5;
    private static final int STATE_FINISHED = 6;
    private static final int STATE_NOTHING=7;
    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable

    FirebaseAuth mAuth = null;


    //To make the device a server
    private static final String App_Name = "PillDeal"; // This is the code we use for BT Enable
    private static final UUID My_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ArrayList<String> adresses = new ArrayList<>(); //they have to have an adress but not a name
    ArrayList<BluetoothDevice> btArray = new ArrayList<>(); //they have to have an adress but not a name
    ArrayAdapter arrayAdapter;

    SendReceive sendReceive;
    ArrayList<String> Clicks = new ArrayList<>(); //they have to have an adress but not a name


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Clicks.add("second Click");
        Clicks.add("3º Click");
        Clicks.add("4º Click");
        Clicks.add("5ºClick");


        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(mAuth.getCurrentUser().getUid()).child("Clicks").push().setValue("Funcionou1");



// Attach a listener to read the data at our posts reference
        database.child("users").child(mAuth.getCurrentUser().getUid()).child("Clicks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,String> map;

                map = (HashMap<String, String>) dataSnapshot.getValue();
                Log.i("data Added map", String.valueOf(map));
                Log.i("data Added map", String.valueOf(map.size()));
                Log.i("data Added map", String.valueOf(map.get(map.size()-1)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });




        if (ContextCompat.checkSelfPermission(reconnect.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(reconnect.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},0);
        }

        listView = findViewById(R.id.listView);
        statusTextView = findViewById(R.id.statusTextView);
        searchButton = findViewById(R.id.searchButton);
        disconnect = findViewById(R.id.disconnect);
        status = findViewById(R.id.status);
        received = findViewById(R.id.received);

        //State of button
        disconnect.setEnabled(false);
        Message message = Message.obtain();
        message.what=STATE_NOTHING;
        handler.sendMessage(message);

        //State of handler

        //create an adapter, pass the layout and then pass the array created
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,bluetoothDevices);
        //set that the the list view
        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //Search paired Devices----------------------------

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        String[] strings = new String[pairedDevices.size()];
        int index=0;

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                    btArray.add(device);
                    Log.i("btArray22", String.valueOf(btArray));
                bluetoothDevices.add(device.getName() + "\n" + device.getAddress()); //Add to the array the devices
                arrayAdapter.notifyDataSetChanged();//notify list about the change
            }
        } else {
            btArray.add(null);
            Log.i("btArray22", "No Devices");

            bluetoothDevices.add("No Paired Devices.");
            arrayAdapter.notifyDataSetChanged();//notify list about the change
        }


        /*SPECIFIC TO TRIGER THE BROADCAST FUNCTION*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);

        //Search NEW Devices--------------------------------------------------------------------
        broadcastReceiver.onReceive(this, getIntent());

        //Bluethooth NOT SUPPORTED--------------------------------------------------------------------
        if (bluetoothAdapter == null) { // Device doesn't support Bluetooth
            Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
        }

        //Turn On Bluethooth--------------------------------------------------------------------
        if(!bluetoothAdapter.isEnabled()) { //Not turned on
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_ENABLE_REQUEST);
        }

        //Initialize server Bluethooth.
        ServerClass t = new ServerClass();
        t.start();



        //A FAMOSA LISTVIEW A SER ESMIOÇADA QUANDO ESTAMOS A TENTAR CONECTAR COM O CLIENTE EM ESPECIFICO ENCONTRADO
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
                //Initialize the client ---- IMPORTANT TO UNDERSTAND
                mSelectedItem = i;

                ClientClass clientClass=new ClientClass(btArray.get(i));
                clientClass.start();

                status.setText("Connecting");
            }
        });



    }

    /*SEACHING NEW DEVICES IT IS USED TO TAKE THE ACTIONS OF THE BLUETHOOTH AND FIND THE CLIENTS AROUND THE PHONE(SERVER)*/
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null) {
                Log.i("Action",action);
            }

            Message message = Message.obtain();
            message.what=STATE_LISTENING;

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                message.what=STATE_FINISHED;
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
                    btArray.add(device);
                    bluetoothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();//notify list about the change

                }
            }
            handler.sendMessage(message);//Send Message to the Handler

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(broadcastReceiver);
    }

    //WHEN SEARCH IS CLICKED WE START BY POPPING UP NEW bluetooth ACTIONS IN THE PHONE
    public void searchClicked(View view) {

        statusTextView.setText("Searching...");
        //Toast.makeText(reconnect.this, "onclick", Toast.LENGTH_SHORT).show();

        searchButton.setEnabled(false);

        bluetoothDevices.clear();
        btArray.clear();
        adresses.clear();

        bluetoothAdapter.startDiscovery();


    }

    public void Disconnect(View view) {

        /*
        * statusTextView.setText("Sending...");
        //Toast.makeText(reconnect.this, "onclick", Toast.LENGTH_SHORT).show();
        Log.i("sending", "asdsa");

        String string= "12345"; //string to send

        sendReceive.write(string.getBytes());
        * */
        sendReceive.cancel();

    }

// TO CALCULATE THE TIME AND SEND ONLY THE PROPER THINGS TO THE DATABASE
@RequiresApi(api = Build.VERSION_CODES.O)
public void givenTwoDateTimesInJava8_whenDifferentiatingInSeconds_thenWeGetTen() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tenSecondsLater = now.plusSeconds(10);

    long diff = ChronoUnit.SECONDS.between(now, tenSecondsLater);
    Log.i("TimeDifference",String.valueOf(diff));
}
    //This handler will be recieving messages from the treadS above that says in wich stage the project is!
    Handler handler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean handleMessage (Message message) {
            Log.i("Handler",String.valueOf(message.arg1));
            //statusTextView.setText();

            switch (message.what)
            {
                case STATE_NOTHING:
                    status.setText("Not Connected");
                    break;
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_FINISHED:
                    status.setText("Finished");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    disconnect.setEnabled(true);
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Conection Failed");
                    break;
                case STATE_MESSAGE_RECIEVED:
                    byte[] readBuff= (byte[]) message.obj;
                    String tempMsg = new String(readBuff,0,message.arg1);

                    /*
                    * 1. See if there is any last click time by the user (if not)
                    * mAuth.getCurrentUser().getUid()
                    * 1.1 Detect what is the time now
                    * 1.2 Save it in the database
                    *
                    * 2. If yes
                    * 2.1 Take the last time and make the diference with now.
                    * 2.2 if greater than x save it in the database, otherwise, show a toast
                    * */

                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime tenSecondsLater = now.plusSeconds(10);

                    long diff = ChronoUnit.SECONDS.between(now, tenSecondsLater);
                    Log.i("TimeDifference",String.valueOf(now));

                    if(tempMsg != null){
                        nummessages += 1;
                    }
                    Log.i("increment",Integer.toString(nummessages));
                    Log.i("Messageved",tempMsg);
                    finalstring = tempMsg;
                    received.setText(finalstring);
                    break;
            }
            return true;
        }
    });



    //The app will be a server bluethooth device
    /*
     * O objetivo do soquete do servidor é ouvir solicitações de conexão de entrada e,
     * quando uma for aceita, fornecer um BluetoothSocket conectado
     * . Quando o BluetoothSocket é adquirido do BluetoothServerSocket,
     * o BluetoothServerSocket pode — e precisa — ser descartado, a menos que você queria aceitar mais conexões.
     * https://developer.android.com/guide/topics/connectivity/bluetooth.html#ConnectingDevices
     * */

    private  class  ServerClass extends Thread{
        // to listen for incoming connection requests and provide a connected BluetoothSocket after a request is accepted.
        private final  BluetoothServerSocket serverSocket;

        public ServerClass(){
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;

            try{//Get a BluetoothServerSocket by calling listenUsingRfcommWithServiceRecord()
                tmp =bluetoothAdapter.listenUsingRfcommWithServiceRecord(App_Name, My_UUID); // establish APP as server socket
                Log.i("Entrou server client","oioi");
            }catch (IOException ex){
                ex.printStackTrace();
            }
            serverSocket = tmp;
        }

        public void run(){
            BluetoothSocket socket =null;

            while(socket == null){ //Start listening for connection requests by calling accept()
                try{
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);//Send Message to the Handler, this way we know it :) (and can display it)
                    socket =serverSocket.accept(); //will accept the connection from the client -- then the method will return something to the socket making it be no longer null --> passing to the if() below
                }catch (IOException e){
                    Log.i("Catch","run - server side");
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);//Send Message to the Handler, this way we know it :) (and can display it)
                }

                if(socket!=null){ //have established the connection in the serverSocket.accept();
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);//Send Message to the Handler

                    //Write code for send /receive
                    //initialize the send receive for the server device
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();


                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break; //Beack the while loop
                }
            }



        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e("", "Could not close the connect socket", e);
            }
        }

    }

    //Although the app is a server bluethooth device, it needs to create methods on wich the clients can connect into.
    /*
     * Para iniciar uma conexão com um dispositivo remoto que aceite conexões com um soquete de servidor aberto,
     *  é necessário antes ter um objeto BluetoothDevice que representa o dispositivo remoto.
     * Para saber como criar um BluetoothDevice, consulte Encontrar dispositivos.
     * Em seguida, use BluetoothDevice para adquirir um BluetoothSocket e iniciar a conexão.
     * https://developer.android.com/guide/topics/connectivity/bluetooth.html#ConnectingDevices
     * */


    private  class  ClientClass extends Thread{

        private final BluetoothDevice device;//BluetoothDevice object that represents the remote device.
        private final BluetoothSocket socket;//You must then use the BluetoothDevice to acquire a BluetoothSocket and initiate the connection.

        public ClientClass(BluetoothDevice device1){
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            device = device1; //Take the device as client!!!


            try{
                //The UUID passed here must match the UUID used by the server device
                tmp =device.createRfcommSocketToServiceRecord(My_UUID); //Using the BluetoothDevice, get a BluetoothSocket
            }catch (IOException ex){
                ex.printStackTrace();
            }
            socket=tmp;
        }
        public void run(){
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();


            //Because connect() is a blocking call, you should always perform this connection procedure in a thread that is separate from the main activity (UI) thread.
            try{ //Initiate the connection by calling connect(). Note that this method is a blocking call.
                socket.connect();
                Message message = Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                //initialize the send/receive for client device
                sendReceive=new SendReceive(socket);
                sendReceive.start();

            }catch (IOException e){
                e.printStackTrace();
                Message message = Message.obtain();
                message.what=STATE_CONNECTION_FAILED; //when you are not connecting to the decive you need to cancel de the device -- we do it in the handler
                handler.sendMessage(message);

                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.e("Server not connected", "Could not close the client socket", closeException);
                }
            }

            /*
            *
            *
            *
            *
            * */

        }


        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("OnCancel", "Could not close the client socket", e);
            }
        }
    }

    //TO SEND AND RECEIVE
    /*
     * https://developer.android.com/guide/topics/connectivity/bluetooth.html#ConnectingDevices
     *
     *
     * 1.Get the InputStream and OutputStream that handle transmissions through the socket
     * 2.Read and write data to the streams using read(byte[]) and write(byte[]).
     *
     * both the read(byte[]) and write(byte[]) methods are blocking calls.
     *
     * The read(byte[]) method blocks until there is something to read from the stream.
     * */

private class SendReceive extends Thread{

    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private byte[] buffer; // mmBuffer store for the stream


    //constuctor
    public SendReceive(BluetoothSocket socket)
    {
        bluetoothSocket=socket; //take the socket from the connection
        InputStream tempIn=null;
        OutputStream tempOut=null;

        try{
            tempIn= bluetoothSocket.getInputStream(); //geting the streams of data - Entrar
        }catch(IOException e){
            Log.i("Inputstream","Error get when tring to get input stream");
            e.printStackTrace();
        }

        try{
            tempOut=bluetoothSocket.getOutputStream();//geting the streams of data - Sair
        }catch(IOException e){
            Log.i("outputstream","Error get when tring to get output stream");
            e.printStackTrace();
        }


        inputStream = tempIn; //initializing final variables
        outputStream = tempOut;

    }

    public void run()
    {
        buffer = new byte[1024]; //what contans the message
        int bytes;//number of bytes // bytes returned from read()

        while(true){ // Keep listening to the InputStream until an exception occurs.
            try{
                // Read from the InputStream.
                bytes= inputStream.read(buffer);
                // Send the obtained bytes to the handler.
                handler.obtainMessage(STATE_MESSAGE_RECIEVED,bytes,-1,buffer).sendToTarget(); //Send this info to handler, just for input stream
            }catch(IOException e){
                Log.d("Error", "Input stream was disconnected", e);
                e.printStackTrace();
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes){

        try{
            outputStream.write(bytes); //write what to send
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            bluetoothSocket.close();
            Message message = Message.obtain();
            message.what=STATE_NOTHING;
            handler.sendMessage(message);
            disconnect.setEnabled(false);
        } catch (IOException e) {
            Log.e("Disconect", "Could not close the connect socket", e);
        }
    }

}

}


