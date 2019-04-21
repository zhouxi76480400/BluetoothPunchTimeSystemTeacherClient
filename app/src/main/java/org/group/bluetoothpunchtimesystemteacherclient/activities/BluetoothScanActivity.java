package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class BluetoothScanActivity extends AppCompatActivity {

    public static final int BLE_SCAN_REQ_CODE = 0x00001;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            finish();
            return;
        }
        createReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
        scan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    private void createReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {

                }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {

                }else if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.e("test",deviceName+","+deviceHardwareAddress);

                }else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

                }
                Log.e("test",action);
                Log.e("test", String.valueOf(intent.getExtras() == null));
            }
        };
    }

    private void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);
    }

    private void unregisterReceivers() {
        unregisterReceiver(receiver);
    }

    public void scan() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        adapter.startDiscovery();
    }

}
