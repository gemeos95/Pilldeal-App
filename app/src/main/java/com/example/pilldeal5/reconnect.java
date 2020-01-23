package com.example.pilldeal5;

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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class reconnect extends AppCompatActivity {
    ListView listView;
    TextView statusTextView;
    Button searchButton;
    Button connect;
    TextView status;
    TextView received;
    int mSelectedItem;


    //Bluethooth connection
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> bluetoothDevices = new ArrayList<>();



    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_MESSAGE_RECIEVED = 5;

    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable

    //To make the device a server
    private static final String App_Name = "PillDeal"; // This is the code we use for BT Enable
    private static final UUID My_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ArrayList<String> adresses = new ArrayList<>(); //they have to have an adress but not a name
    ArrayList<BluetoothDevice> btArray = new ArrayList<>(); //they have to have an adress but not a name
    ArrayAdapter arrayAdapter;

    SendReceive sendReceive;


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
        status = findViewById(R.id.status);
        received = findViewById(R.id.received);

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
                    btArray.add(device);
                    bluetoothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();//notify list about the change

                }
            }
        }
    };

    //WHEN SEARCH IS CLICKED WE START BY POPPING UP NEW bluetooth ACTIONS IN THE PHONE
    public void searchClicked(View view) {
        statusTextView.setText("Searching...");
        //Toast.makeText(reconnect.this, "onclick", Toast.LENGTH_SHORT).show();
        Log.i("onclick", "asdsa");

        searchButton.setEnabled(false);

        bluetoothDevices.clear();
        btArray.clear();
        adresses.clear();

        bluetoothAdapter.startDiscovery();
    }

    //WHEN SEARCH IS CLICKED WE START BY POPPING UP NEW bluetooth ACTIONS IN THE PHONE
    public void sendmessage(View view) {
        statusTextView.setText("Sending...");
        //Toast.makeText(reconnect.this, "onclick", Toast.LENGTH_SHORT).show();
        Log.i("sending", "asdsa");

        String string= "12345"; //string to send

        sendReceive.write(string.getBytes());
    }


    //This handler will be recieving messages from the treadS above that says in wich stage the project is!
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage (Message message) {
            Log.i("Handler",String.valueOf(message.arg1));
            //statusTextView.setText();

            switch (message.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:

                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Conection Failed");
                    break;
                case STATE_MESSAGE_RECIEVED:

                    byte[] readBuff= (byte[]) message.obj;
                    String tempMsg = new String(readBuff,0,message.arg1);

                    Log.i("Messageved",tempMsg);
                    received.setText(tempMsg);
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

        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try{
                serverSocket=bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(App_Name, My_UUID); // establish APP as server socket
                Log.i("Entrou server client","oioi");
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
        public void run(){
            BluetoothSocket socket =null;

            while(socket == null){
                try{
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);//Send Message to the Handler, this way we know it :) (and can display it)
                    socket =serverSocket.accept(); //will accept the connection from the client -- then the method will return something to the socket making it be no longer null --> passing to the if() below
                }catch (IOException e){
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);//Send Message to the Handler, this way we know it :) (and can display it)
                }

                if(socket!=null){ //have established the connection in the serverSocket.accept();
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);//Send Message to the Handler, this way we know it :) (and can display it)

                    //Write code for send /receive
                    //initialize the send receive for the server device
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();



                    break; //Beack the while loop
                }
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

        private final BluetoothDevice device;
        private final BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1){
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            device = device1; //Take the device as client!!!


            try{
                tmp =device.createInsecureRfcommSocketToServiceRecord(My_UUID); // establish APP as server socket
            }catch (IOException ex){
                ex.printStackTrace();
            }
            socket=tmp;
        }
        public void run(){
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                try{
                    socket.connect();
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    //initialize the send/receive for booth
                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                }catch (IOException e){
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what=STATE_CONNECTION_FAILED; //when you are not connecting to the decive you need to cancel de the device -- we do it in the handler
                    handler.sendMessage(message);
                }
        }
    }

    //TO SEND AND RECEIVE
    /*
     * https://developer.android.com/guide/topics/connectivity/bluetooth.html#ConnectingDevices
     * */

private class SendReceive extends Thread{

    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    //constuctor
    public SendReceive(BluetoothSocket socket)
    {
        bluetoothSocket=socket; //take the socket from the connection
        InputStream tempIn=null;
        OutputStream tempOut=null;

    try{
        tempIn= bluetoothSocket.getInputStream(); //geting the streams of data - Entrar
        tempOut=bluetoothSocket.getOutputStream();//geting the streams of data - Sair
    }catch(IOException e){
        e.printStackTrace();
    }

        inputStream= tempIn; //initializing final variables
        outputStream=tempOut;

    }

    public void run()
    {
        byte [] buffer = new byte[1024]; //what contans the message
        int bytes;//number of bytes

        while(true){ //while loop because we are always ready to receive new messages
            try{
                bytes= inputStream.read(buffer);
                handler.obtainMessage(STATE_MESSAGE_RECIEVED,bytes,-1,buffer).sendToTarget(); //Send this info to handler, just for input stream
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public void write(byte[] bytes){

        try{
            outputStream.write(bytes); //write what to send
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
}


