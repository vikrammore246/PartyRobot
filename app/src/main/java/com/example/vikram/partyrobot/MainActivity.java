package com.example.vikram.partyrobot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ImageView up, down, left, right, connect, stop, btnCenter, btnInfo;
    private TextView txtConStatus, waiting;
    private LinearLayout playLayout;
    private ConstraintLayout startLayout;


    private final String DEVICE_ADDRESS="00:18:E4:34:C9:82";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    Button startButton, sendButton,clearButton,stopButton;
    //TextView connStatus;
    EditText editText;
    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playLayout = findViewById(R.id.playLayout);
        playLayout.setVisibility(View.GONE);
        startLayout = findViewById(R.id.startLayout);

        up = findViewById(R.id.btnUp);
        down = findViewById(R.id.btnDown);
        left = findViewById(R.id.btnLeft);
        right = findViewById(R.id.btnRight);
        connect = findViewById(R.id.btnConnect);
        connect.setVisibility(View.GONE);
        stop = findViewById(R.id.btnStop);
        btnCenter = findViewById(R.id.txtCenter);
        //connStatus = findViewById(R.id.txtConnStatus);
        txtConStatus = findViewById(R.id.txtConnStatus);
        btnInfo = findViewById(R.id.btnAboutUs);
        waiting = findViewById(R.id.txtWaiting);
        waiting.setVisibility(View.GONE);

        btnInfo.setOnClickListener((View) -> {

            Toast.makeText(MainActivity.this, "About Us", Toast.LENGTH_LONG).show();

        });

        up.setOnClickListener((View) -> {

            String string = "u";
            try {
                outputStream.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //txtCenter.setText("up");

        });

        left.setOnClickListener((View) -> {

            String string = "l";
            try {
                outputStream.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //txtCenter.setText("left");

        });

        right.setOnClickListener((View) -> {

            String string = "r";
            try {
                outputStream.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //txtCenter.setText("right");

        });

        down.setOnClickListener((View) -> {

            String string = "d";
            try {
                outputStream.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //txtCenter.setText("down");

        });
    }



    //INITIALIZE BLUETOOTH

    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device doesn't Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }


    //CONNECT TO MODULE

    public boolean BTconnect()
    {

        waiting.setVisibility(View.VISIBLE);

        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(MainActivity.this, "Connection Time Out", Toast.LENGTH_LONG).show();
        }


        return connected;
    }


    //LISTEN FOR DATA

    void beginListenForData() {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread  = new Thread(new Runnable() {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");
                            handler.post(new Runnable() {
                                public void run()
                                {
                                    //txtCenter.setText(string);
                                }
                            });

                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }


    //CLOSE CONNECTION

    public void onClickStop(View view) throws IOException{

        onStopVisibility();

        stopThread = true;
        outputStream.close();
        inputStream.close();
        socket.close();

        deviceConnected=false;
        //connStatus.setText("Connection Closed!");
        txtConStatus.setText("Dis-\nconnected");

    }

    private void onStopVisibility(){

        stop.setVisibility(View.VISIBLE);
        connect.setVisibility(View.GONE);

        playLayout.setVisibility(View.GONE);
        startLayout.setVisibility(View.VISIBLE);

    }


    //START CONNECTION

    public void onClickStart(View view) {

        if(BTinit())
        {
            if(BTconnect())
            {
                waiting.setVisibility(View.GONE);
                connect.setVisibility(android.view.View.GONE);
                stop.setVisibility(android.view.View.VISIBLE);
                deviceConnected=true;
                beginListenForData();

                startLayout.setVisibility(View.GONE);
                playLayout.setVisibility(View.VISIBLE);

                stop.setVisibility(View.GONE);
                connect.setVisibility(View.VISIBLE);

                //connStatus.setText("Connection Opened!");
                txtConStatus.setText("Connected");
            }

        }

    }





}
