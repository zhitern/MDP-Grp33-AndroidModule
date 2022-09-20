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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private static MainActivity instance;

    GridManager gridManager;
    Button btnBT;
    String connectedDevice = "";
    TextView bluetoothStatus;
    TextView robotStatus;


    BluetoothAdapter btAdapter;
    BluetoothService btService;
    BluetoothDeviceActivity btPopup;
    BluetoothDevice lastConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetupBotControls();
        SetupBluetoothTestTools();

        gridManager = GridManager.GetInstance();
        gridManager.Init(20, 20, 0, this);

        RobotCar robotCar = (RobotCar) findViewById(R.id.robot_car);
        robotCar.Init(4, new Vec2D(1, 2));

        robotStatus = findViewById(R.id.text_robotStatus);

        Activity mainActivity = this;
        Button sendBtn = (Button)findViewById(R.id.btn_sendInfo);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "";

                for (GridView obs : GridView.gridViewList) {
                    msg += "," + obs.obstacle.localId +
                            "," + obs.grid.x +
                            "," + obs.grid.y +
                            "," + obs.obstacle.GetDirectionString();
                }


                Toast.makeText(mainActivity, msg, Toast.LENGTH_LONG).show();
                sendCommmand(msg);
            }
        });

        bluetoothStatus = findViewById(R.id.text_bluetoothStatus);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        btPermission();
        if(btAdapter.isEnabled())
            bluetoothStatus.setText("Bluetooth on");
        else
            bluetoothStatus.setText("Bluetooth off");

        btnBT.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                if(!btAdapter.isEnabled()){
                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBTIntent);
                }else{
                    btPermission();
                    if (lastConnected!=null) {
                        reconnectHandler.removeCallbacks(reconnectRunnable);
                        lastConnected = null;
                        connectedDevice = "";
                    }
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

    protected void SetupBluetoothTestTools() {
        TextInputEditText input = (TextInputEditText) findViewById(R.id.textInput_btTest);
        Button btn = (Button) findViewById(R.id.button_btTest);

        Activity mainActivity = this;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mainActivity, input.getText().toString(), Toast.LENGTH_SHORT).show();
                sendCommmand(input.getText().toString());
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
                sendCommmand("w");
                Toast.makeText(mainContext, "UpBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommmand("s");
                Toast.makeText(mainContext, "downBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommmand("a");
                Toast.makeText(mainContext, "leftBtn Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommmand("d");
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
                            bluetoothStatus.setText("Ready to Start");
                            if (lastConnected!=null) {
                                reconnectHandler.removeCallbacks(reconnectRunnable);
                                lastConnected = null;
                            }
                            break;
                        case Constants.STATE_CONNECTING:
                            bluetoothStatus.setText("Connecting");
                            connectedDevice = "";
                            break;
                        case Constants.STATE_LISTEN:
                            break;
                        case Constants.STATE_NONE:
                            if(!btPopup.isRemoving()&&btPopup.isResumed()&&
                                    btPopup.getDialog()!=null&&btPopup.getDialog().isShowing()){
                            }else if(connectedDevice!="") {
                                lastConnected = btAdapter.getRemoteDevice(connectedDevice);
                                promptReconnect();
                            }
                            bluetoothStatus.setText("Not Connected");
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
                    String[] readList = readMessage.split(",");
                    String checkCmd = readList[0];
                    switch (checkCmd){
                        case "ROBOT":
                            RobotCar.GetInstance().UpdateFromMsg(readList);
                            //sendCommmand("ack"); send back acknowlegdment?
                            break;
                        case "IMG":
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), readMessage,
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
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

    private void sendCommmand(String command){
        if(btService!=null&&btService.getState()==Constants.STATE_CONNECTED)
            btService.write(command.getBytes(StandardCharsets.UTF_8));
    }
    public static void SendObstacles(int id, int x, int y, int dir) {
        instance.sendCommmand(
                "ADDOBS" + "," +
                        Integer.toString(id) + "," +
                        Integer.toString(x) + "," +
                        Integer.toString(y) + "," +
                        Integer.toString(dir)
        );
    }
    public static void HideObstacles(int id) {
        instance.sendCommmand(
                "HIDEOBS" + "," +
                        Integer.toString(id)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        btPermission();
    }

    private void btPermission(){
        if(btAdapter==null){
            new AlertDialog.Builder(this).setTitle("Bluetooth Not Supported")
                    .setNegativeButton("Exit", (dialogInterface, i) -> {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }).show();
        }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            if(btService==null&&btAdapter.isEnabled()){
                btnBT = findViewById(R.id.btn_bluetooth);
                btService = new BluetoothService(this,mHandler);
                btService.start();
                btPopup = new BluetoothDeviceActivity();
                btPopup.setBluetoothService(btService);
            }
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
                bluetoothStatus.setText("Reconnecting");
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
        if(btService!=null)
            btService.stop();
    }
}