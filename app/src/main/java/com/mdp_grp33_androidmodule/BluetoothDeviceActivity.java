package com.mdp_grp33_androidmodule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Objects;

@SuppressLint({"MissingPermission","SetTextI18n"})
public class BluetoothDeviceActivity extends DialogFragment {

    String connectedDevice = "";

    BluetoothService btService;
    BluetoothAdapter btAdapter;

    private TextView dialogTitle;

    private final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private ActivityResultLauncher<String[]> requestLocationPermissionLauncher;

    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> unknownDevices;
    private ArrayAdapter<BluetoothDevice> unknownArrayAdapter;
    private ListView unknownList;
    private ArrayList<BluetoothDevice> pairedDevices;
    private ArrayAdapter<BluetoothDevice> pairedArrayAdapter;
    private ListView pairedList;
    private ImageView close;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<>();
        unknownDevices = new ArrayList<>();

        requestLocationPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted ->{
                    if (isGranted.containsValue(false)){
                        new AlertDialog.Builder(getActivity()).setTitle("Location permission")
                                .setMessage("Allow location for Bluetooth scanning?")
                                .setNegativeButton("No", (dialogInterface, i) ->
                                        Toast.makeText(getActivity(),
                                                "Require location to find nearby devices", Toast.LENGTH_LONG).show())
                                .setPositiveButton("Yes", (dialogInterface, i) ->
                                        requestLocationPermissionLauncher.launch(PERMISSIONS)).create().show();
                    }else{
                        btAdapter.startDiscovery();
                    }
                });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        if (btAdapter == null){
            builder.setTitle("No Bluetooth");
            builder.setMessage("Device does not support Bluetooth");
            builder.setNegativeButton("Close", (dialog, which) -> dismiss());
        }else if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
            builder.setTitle("Bluetooth permission");
            builder.setMessage("This app requires Bluetooth");
            builder.setNegativeButton("Close", (dialog, which) -> dismiss());
        }else{
            return super.onCreateDialog(savedInstanceState);
        }
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        return alert;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_devices, container, false);
        dialogTitle = view.findViewById(R.id.dialogTitle);
        unknownList = view.findViewById(R.id.lv_unknown);
        pairedList = view.findViewById(R.id.lv_paired);
        close = view.findViewById(R.id.ivCloseButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutInflater = (LayoutInflater) requireActivity().getLayoutInflater();

        unknownArrayAdapter = new ArrayAdapter (requireActivity(), R.layout.bluetooth_scan, unknownDevices){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                convertView = layoutInflater.inflate(R.layout.bluetooth_scan, null);
                TextView name = (TextView) convertView.findViewById(R.id.scan_name);
                TextView address = (TextView) convertView.findViewById(R.id.scan_mac);
                BluetoothDevice device = unknownDevices.get(position);
                if (name != null)
                    name.setText(device.getName());
                if (address != null)
                    address.setText(device.getAddress());
                return convertView;
            }
        };
        unknownList.setOnItemClickListener((adapterView, v, i, l) -> {
            dialogTitle.setText("Connecting");
            btService.stop();
            refreshPairedDevices();
            BluetoothDevice selected = unknownDevices.get(i);
            selected.createBond();
        });
        unknownList.setAdapter(unknownArrayAdapter);

        pairedArrayAdapter = new ArrayAdapter(requireActivity(), R.layout.bluetooth_paired, pairedDevices){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                convertView = layoutInflater.inflate(R.layout.bluetooth_paired, null);
                TextView name = (TextView) convertView.findViewById(R.id.pair_name);
                TextView address = (TextView) convertView.findViewById(R.id.pair_mac);
                TextView connected = (TextView) convertView.findViewById(R.id.connected);
                BluetoothDevice device = pairedDevices.get(position);
                if (name != null)
                    name.setText(device.getName());
                if (address != null)
                    address.setText(device.getAddress());
                if (device.getAddress().equals(connectedDevice))
                    connected.setText("Connected");
                else
                    connected.setText("Not connected");
                return convertView;
            }
        };
        pairedList.setOnItemClickListener((adapterView, v, i, l) -> {
            dialogTitle.setText("Connecting");

            BluetoothDevice selected = pairedDevices.get(i);
            btService.stop();
            refreshPairedDevices();
            if(connectedDevice.equals(selected.getAddress())) {
                connectedDevice = "";
                btService.start();
                dialogTitle.setText("Not Connected");
                Toast.makeText(requireActivity(), "Disconnecting", Toast.LENGTH_LONG).show();
            }else{
                btService.connect(selected);
                connectedDevice = selected.getAddress();
            }
        });
        pairedList.setAdapter(pairedArrayAdapter);

        close.setOnClickListener(v -> dismiss());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        requireActivity().registerReceiver(broadcastReceiver, intentFilter);

        checkPermission();
    }

    private void checkPermission(){
        if(!btAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
        }
        int permissionCheck = ActivityCompat.checkSelfPermission(requireActivity(),"Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += ActivityCompat.checkSelfPermission(requireActivity(),"Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            requestLocationPermissionLauncher.launch(PERMISSIONS);
        }
        if(getArguments()!=null){
            String mac = getArguments().getString("connectedMAC");
            connectedDevice = mac;
            if(mac.equals("") && btAdapter.getState()==BluetoothAdapter.STATE_ON) {
                dialogTitle.setText("Not Connected");
                btService.start();
                btAdapter.startDiscovery();
            }else
                dialogTitle.setText("Connected");
            refreshPairedDevices();
        }else{
            Toast.makeText(requireActivity(), "This app requires Bluetooth", Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_ON) {
                        Log.d("Bluetooth Activity", "mBroadcastReceiver1: STATE ON");
                        btService.start();
                        if (!btAdapter.isDiscovering()) {
                            refreshPairedDevices();
                            btAdapter.startDiscovery();
                            Log.d("Bluetooth scan", String.valueOf(btAdapter.isDiscovering()));
                        }
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND: {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getAddress() != null && device.getAddress().length() > 0 && !unknownDevices.contains(device) && !pairedDevices.contains(device)) {
                        unknownDevices.add(device);
                    }
                    unknownArrayAdapter.notifyDataSetChanged();
                    break;
                }
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        btService.stop();
                        btService.connect(device);
                        Log.d("Bluetooth bonded", "Bounded " + device.getName());
                        if (unknownDevices.remove(device))
                            unknownArrayAdapter.notifyDataSetChanged();
                        connectedDevice = device.getAddress();
                        refreshPairedDevices();
                    }
                    break;
                }
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    refreshPairedDevices();
                    dialogTitle.setText("Connected");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    connectedDevice = "";
                    refreshPairedDevices();
                    dialogTitle.setText("Not Connected");
                    break;
            }
        }
    };

    private void refreshPairedDevices(){
        pairedDevices.clear();
        pairedDevices.addAll(btAdapter.getBondedDevices());
        pairedArrayAdapter.notifyDataSetChanged();
    }

    public void setBluetoothService(BluetoothService bluetoothService) {
        btService = bluetoothService;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(btAdapter != null && btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
        requireActivity().unregisterReceiver(broadcastReceiver);
    }
}