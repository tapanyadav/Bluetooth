package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;



public class LedOnOffAcivity extends AppCompatActivity {

    Button btnOn, btnOff,btnSend,btnSendDT;

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    TextView textView,textDateTime;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    EditText ed;

    //Date and time variable
    public static final String DATE_FORMAT_2="dd-MM-yyyy";
    public static final String DATE_FORMAT_1="hh:mm:ss:a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_on_off_acivity);


        Intent intent = getIntent();
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);
        btnOn = findViewById(R.id.btnon);
        btnOff = findViewById(R.id.btnoff);
        textView=findViewById(R.id.textView);
        ed=findViewById(R.id.edittext);
        btnSend=findViewById(R.id.btnSend);
        textDateTime=findViewById(R.id.textViewDateTime);
        btnSendDT=findViewById(R.id.btnSenddt);

        new ConnectBT().execute();

        btnSendDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDateTime();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                turnOnLed();      //method to turn on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                turnOffLed();   //method to turn off
            }
        });




    }

    private void sendDateTime() {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(getCurrentDate().getBytes());
                btSocket.getOutputStream().write(getCurrentTime().getBytes());
                textDateTime.setText(getCurrentDate());

            }
            catch (IOException e)
            {
                msg("Error");
            }
        }

    }

    //date and time pick
    public static String getCurrentDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT_2);
       // dateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 05 : 30"));
        Date today= Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
    public static String getCurrentTime(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT_1);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 05 : 30"));
        Date today= Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    private void turnOnLed() {

        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("TO".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void sendData() {

        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(ed.getText().toString().getBytes());
                textView.setText(ed.getText().toString());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void turnOffLed() {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("TF".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(LedOnOffAcivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
