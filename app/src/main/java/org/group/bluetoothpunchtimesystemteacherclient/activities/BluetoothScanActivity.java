package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.activities.adapters.BluetoothListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BluetoothScanActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        BluetoothListAdapter.OnBluetoothDeviceSelectedListener {

    public static final int BLE_SCAN_REQ_CODE = 0x00001;

    public static final String BT_DEVICE_KEY = "BT_DEVICE_KEY";

    private BroadcastReceiver receiver;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    private BluetoothListAdapter bluetoothListAdapter;

    private List<BluetoothDevice> list;

    private boolean isNotFirstRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);
        initView();
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            finish();
            return;
        }
        createReceiver();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.scan_bt_device));
        setSupportActionBar(toolbar);
        ActionBar actionBar = null;
        if((actionBar = getSupportActionBar()) != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        refreshLayout = findViewById(R.id.srl);
        refreshLayout.setColorSchemeColors(getColor(R.color.colorAccent));
        refreshLayout.setOnRefreshListener(this);
        recyclerView = findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        bluetoothListAdapter = new BluetoothListAdapter(this,list);
        bluetoothListAdapter.setBluetoothDeviceSelectedListener(this);
        recyclerView.setAdapter(bluetoothListAdapter);
    }

    @Override
    protected void onDestroy() {
        stopScan();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
        if(!isNotFirstRefresh) {
            isNotFirstRefresh = true;
            scan();
        }
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
                    list.clear();
                    bluetoothListAdapter.notifyDataSetChanged();
                    if(!refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(true);
                    }
                }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    if(refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                }else if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    list.add(device);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.e("test",deviceName+","+deviceHardwareAddress);
                    bluetoothListAdapter.notifyItemInserted(list.size()-1);
                }else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

                }
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
        stopScan();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.startDiscovery();
    }

    private void stopScan() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
    }

    @Override
    public void onRefresh() {
        scan();
    }

    @Override
    public void onBluetoothDeviceSelected(
            BluetoothListAdapter which, BluetoothDevice bluetoothDevice, int position) {
        if(bluetoothDevice != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable(BT_DEVICE_KEY,bluetoothDevice);
            intent.putExtras(bundle);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
