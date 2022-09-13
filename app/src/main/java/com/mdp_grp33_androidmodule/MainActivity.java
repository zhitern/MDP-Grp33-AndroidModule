package com.mdp_grp33_androidmodule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity{

    GridManager gridManager;
    Button btnBT;
    String connectedDevice = "";
    TextView status;

    BluetoothAdapter btAdapter;
    BluetoothService btService;
    BluetoothDeviceActivity btPopup;
    BluetoothDevice lastConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetupBotControls();

        gridManager = GridManager.GetInstance();
        gridManager.Init(20, 20, 0, this);

        RobotCar robotCar = (RobotCar) findViewById(R.id.robot_car);
        robotCar.Init(4);

        status = findViewById(R.id.text_robotStatus);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btnBT = findViewById(R.id.btn_bluetooth);
        btService = new BluetoothService(this,mHandler);
        //btService.start();
        btPopup = new BluetoothDeviceActivity();
        btPopup.setBluetoothService(btService);

        btPermission();

        btnBT.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                if(!btAdapter.isEnabled()){
                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBTIntent);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString("connectedMAC", connectedDevice);
                    btPopup.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {ft.remove(prev);}
                    ft.addToBackStack(null);
                    btPopup.show(ft, "dialog");
                }
            }else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("This app requires Bluetooth")
                        .setTitle("Alert")
                        .show();
            }
        });
    }

    protected void SetupBotControls(){
        Button upBtn = (Button)findViewById(R.id.btn_up);
        Button downBtn = (Button)findViewById(R.id.btn_down);
        Button leftBtn = (Button)findViewById(R.id.btn_left);
        Button rightBtn = (Button)findViewById(R.id.btn_right);

        Activity mainContext = this;

        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainContext, "UpBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainContext, "downBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainContext, "leftBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainContext, "rightBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final Handler mHandler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    Log.d("Message", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                            testWrite();
                            status.setText("Connected");
                            break;
                        case Constants.STATE_CONNECTING:
                            status.setText("Connecting");
                            connectedDevice = "";
                            break;
                        case Constants.STATE_LISTEN:
                            break;
                        case Constants.STATE_NONE:
                            if(!btPopup.isRemoving()&&btPopup.isResumed()&&
                                    btPopup.getDialog()!=null&&btPopup.getDialog().isShowing()){
                            }else if(connectedDevice!="") {
                                btService.start();
                                lastConnected = btAdapter.getRemoteDevice(connectedDevice);
                                promptReconnect();
                            }
                            status.setText("Not Connected");
                            connectedDevice = "";
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d("MESSAGE_WRITE", writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("MESSAGE_READ", readMessage);
                    Toast.makeText(getApplicationContext(), readMessage,
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    connectedDevice = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + connectedDevice, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    String message = msg.getData().getString(Constants.TOAST);
                    if (message.equals("Device connection was lost")&&btPopup.isRemoving()&&!btPopup.isResumed()&&
                            btPopup.getDialog()!=null&&!btPopup.getDialog().isShowing()){

                    }else
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    // write function testing for now
    private void testWrite(){
        String test = "hi";
        btService.write(test.getBytes(StandardCharsets.UTF_8));
    }

    private void btPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            status.setText("Bluetooth on");
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("This app requires Bluetooth")
                    .setTitle("Alert")
                    .show();
        }
    }

    // handles the runnable for reconnecting to bluetooth device
    Handler reconnectHandler = new Handler();

    // runnable that runs code to reconnect to bluetooth device
    Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                status.setText("Reconnecting");
                if (btService.getState() == Constants.STATE_CONNECTED) {
                    Log.d("reconnected",lastConnected.getAddress());
                    connectedDevice = lastConnected.getAddress();
                    reconnectHandler.removeCallbacks(reconnectRunnable);
                    lastConnected = null;
                }
                btService.connect(lastConnected);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Failed to reconnect, trying in 5 second", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // prompt user whether to reconnect to bluetooth device
    private void promptReconnect() {
        new AlertDialog.Builder(this).setTitle("Reconnect to Bluetooth Device")
                .setPositiveButton("Yes", (dialogInterface, i) ->
                        // set timing
                        reconnectHandler.postDelayed(reconnectRunnable, 5000))
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btService.stop();
    }
}