package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.network.CreateSessionThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.NetworkThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.SendScanDataThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.StatusCodeList;
import org.group.bluetoothpunchtimesystemteacherclient.objects.CreateSessionPOJO;
import org.group.bluetoothpunchtimesystemteacherclient.timer.ScanThread;
import org.group.bluetoothpunchtimesystemteacherclient.views.RadarView;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Response;

public class NewSessionActivity extends MyActivity implements
        NetworkThread.OnNetworkThreadReturnListener, View.OnClickListener {

    private static final String IS_NEW_SESSION_TAG = "IS_NEW_SESSION_TAG";

    public static void openNewSessionActivity(Activity aty, boolean isNewSession) {
        Intent intent = new Intent(aty,NewSessionActivity.class);
        intent.putExtra(IS_NEW_SESSION_TAG,isNewSession);
        aty.startActivity(intent);
    }

    private boolean isNewSession;

    private ProgressDialog waitingForSessionCreateDialog;

    private boolean isSendDataNow;

    private CreateSessionPOJO createSessionPOJO;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsession);
        getData();
        list = new ArrayList<>();
        initView();
        checkPermission();
        checkBluetooth();
        initAllBroadcastReceiver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == TURN_ON_BLUETOOTH_REQUEST_CODE) {
            // catch the turn on bt req
            if(resultCode == RESULT_OK) {
                // go to next phase
                initTask();
            } else {
                checkBluetooth();
            }
        }else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isRecordingNow;

    private boolean isRecordingNow() {
        return isRecordingNow;
    }

    @Override
    public void onBackPressed() {
        if(isRecordingNow()) {
            DialogInterface.OnClickListener onClickListener =
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            NewSessionActivity.super.onBackPressed();
                            break;
                    }
                }
            };
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.is_recording_now_title))
                    .setMessage(getString(R.string.is_recording_now_hint))
                    .setPositiveButton(getString(R.string.cancel),onClickListener)
                    .setNegativeButton(getString(R.string.exit),onClickListener)
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        }else
            super.onBackPressed();
    }

    private void getData() {
        Intent intent = getIntent();
        isNewSession = intent.getBooleanExtra(IS_NEW_SESSION_TAG,true);
    }

    private LinearLayout ll_main;

    private TextView tv_info;

    private RadarView radarView;

    private Button btn_stop;

    private TextView tv_now_time;

    private TextView tv_next_time_scan_interval;

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.new_session));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        ll_main = findViewById(R.id.ll_main);
        tv_info = findViewById(R.id.tv_info);
        radarView = findViewById(R.id.radar_view);
        btn_stop = findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        tv_now_time = findViewById(R.id.tv_now_time);
        tv_next_time_scan_interval = findViewById(R.id.tv_next_time_scan_interval);





//        ll_main.setVisibility(View.GONE);
//        radarView.setSearching(true);
//        radarView.addPoint();
//        radarView.addPoint();

    }

    @Override
    public void isBluetoothOpen() {
        initTask();
    }


    private void initTask() {
        if(isNewSession) {
            showSelectProgramDialog();
        }else {


        }
    }


    private void showSelectProgramDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_select_program,null);
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getDataFromDialog(view);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        onBackPressed();
                        break;
                }
            }
        };
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.set_program))
                .setView(view)
                .setPositiveButton(getString(R.string.create),onClickListener)
                .setNegativeButton(getString(R.string.quit),onClickListener)
                .create();
        alertDialog.setCancelable(false);
        //
        NumberPicker number_picker_set_time = view.findViewById(R.id.number_picker_set_time);
        String[] displayedValuesTime = getResources().getStringArray(R.array.array_time);
        number_picker_set_time.setDisplayedValues(displayedValuesTime);
        number_picker_set_time.setWrapSelectorWheel(true);
        number_picker_set_time.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        number_picker_set_time.setMinValue(0);
        number_picker_set_time.setMaxValue(displayedValuesTime.length - 1);
        //
        NumberPicker number_picker_set_frequency =
                view.findViewById(R.id.number_picker_set_frequency);
        String[] displayedValuesFrequency = getResources().getStringArray(R.array.array_scan_times);
        number_picker_set_frequency.setDisplayedValues(displayedValuesFrequency);
        number_picker_set_frequency.setWrapSelectorWheel(true);
        number_picker_set_frequency.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        number_picker_set_frequency.setMinValue(0);
        number_picker_set_frequency.setMaxValue(displayedValuesFrequency.length - 1);
        //
        EditText et_lesson_name = view.findViewById(R.id.et_lesson_name);
        //


        alertDialog.show();
        et_lesson_name.requestFocus();
        et_lesson_name.postDelayed(new Runnable() {
            @Override
            public void run() {
                showIME();
            }
        },200);
    }

    private void getDataFromDialog(View view) {
        NumberPicker number_picker_set_time = view.findViewById(R.id.number_picker_set_time);
        NumberPicker number_picker_set_frequency =
                view.findViewById(R.id.number_picker_set_frequency);
        EditText et_lesson_name = view.findViewById(R.id.et_lesson_name);
        //
        String name = et_lesson_name.getText().toString();
        boolean isNameOK = !name.isEmpty();
        int[] array_time = getResources().getIntArray(R.array.array_time_integer);
        int time = array_time[number_picker_set_time.getValue()];
        int[] array_scan_times = getResources().getIntArray(R.array.array_scan_times_integer);
        int frequency = array_scan_times[number_picker_set_frequency.getValue()];
        if(isNameOK) {
            CreateSessionPOJO createSessionPOJO = new CreateSessionPOJO();
            createSessionPOJO.name = name;
            createSessionPOJO.time = time;
            createSessionPOJO.frequency = frequency;
            CreateSessionThread createSessionThread = new CreateSessionThread(createSessionPOJO);
            createSessionThread.setOnNetworkThreadReturnListener(this);
            if(waitingForSessionCreateDialog == null) {
                waitingForSessionCreateDialog = new ProgressDialog(this);
                waitingForSessionCreateDialog.setCancelable(false);
                waitingForSessionCreateDialog.setMessage(getString(R.string.create_new_session_now));
            }
            waitingForSessionCreateDialog.show();
            isSendDataNow = true;
            createSessionThread.start();
        }else {
            Toast.makeText(this,
                    getString(R.string.please_fill_the_name),Toast.LENGTH_SHORT).show();
            showSelectProgramDialog();
        }
    }

    protected void showIME() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onNetworkThreadGetDataSuccessful(Class clazz, Response data) {
        isSendDataNow = false;
        int code = data.code();
        if(code == HttpURLConnection.HTTP_OK) {
            try {
                String str = data.body().string();
                String json = URLDecoder.decode(str,"UTF-8");
                JSONObject jsonObject = new JSONObject(json);
                String s = jsonObject.getString("s");
                if(s.equals(String.valueOf(StatusCodeList.STATUS_CODE_OK))) {
                    String d = jsonObject.getString("d");
                    CreateSessionPOJO createSessionPOJO =
                            new Gson().fromJson(d,CreateSessionPOJO.class);
                    NewSessionActivity.this.createSessionPOJO = createSessionPOJO;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waitingForSessionCreateDialog.dismiss();
                            loadData();
                        }
                    });
                }else {
                    onNetworkThreadGetDataFailed(clazz,StatusCodeList.
                            STATUS_CODE_JSON_PARAMETER_NOT_EQUALS);
                }
            } catch (Exception e) {
                onNetworkThreadGetDataFailed(clazz,StatusCodeList.
                        STATUS_CODE_JSON_PARAMETER_NOT_EQUALS);
                e.printStackTrace();
            }
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onNetworkThreadGetDataFailed(clazz,code);
                }
            });
        }
    }

    @Override
    public void onNetworkThreadGetDataFailed(Class clazz, int statusCode) {
        isSendDataNow = false;
        if(clazz == CreateSessionThread.class) {
            String hint = null;
            if(MyApplication.getInstance().isNetworkOn()) {
                hint = getString(R.string.unknown_error);
            }else {
                hint = getString(R.string.no_network_hint);
            }
            String finalHint = hint;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waitingForSessionCreateDialog.dismiss();
                    Toast.makeText(NewSessionActivity.this, finalHint,Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void loadData() {
        isRecordingNow = true;
        radarView.setSearching(true);
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z", Locale.getDefault());
        String str = String.format(getString(R.string.session_info),createSessionPOJO.name,
                simpleDateFormat.format(new Date(createSessionPOJO.create_time)),
                createSessionPOJO.time,createSessionPOJO.frequency);
        tv_info.setText(str);
        ScanThread.startThread(createSessionPOJO);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stop:
                onBackPressed();
                break;


        }
    }

    @Override
    protected void onDestroy() {
        ScanThread.stopThread();
        unregisterAllBroadcastReceiver();
        super.onDestroy();
    }

    private List<String> list;

    private void initAllBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(ScanThread.ACTION_FINISHED)) {
                    notifyFinish();
                }else if(action.equals(ScanThread.ACTION_NOW)) {
                    int remaining_time =
                            intent.getIntExtra(ScanThread.ACTION_NOW_KEY_REMAINING_TIME,-1);
                    int scan_interval =
                            intent.getIntExtra(ScanThread.ACTION_NOW_KEY_SCAN_INTERVAL,-1);
                    int next_time =
                            intent.getIntExtra(ScanThread.ACTION_NOW_KEY_SCAN_NEXT_TIME,-1);
                    updateUI(remaining_time,scan_interval,next_time);
                }else if(action.equals(ScanThread.ACTION_SCAN)) {
                    int time = intent.getIntExtra(ScanThread.ACTION_SCAN_KEY_TIME, -1);
                    scan(time);
                }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    list.clear();
                }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    onScanFinish();
                    radarView.cleanPoint();
                }else if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    list.add(AddAStudentActivity.removeColonFromMACAddress(deviceHardwareAddress));
                    radarView.addPoint();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScanThread.ACTION_NOW);
        intentFilter.addAction(ScanThread.ACTION_FINISHED);
        intentFilter.addAction(ScanThread.ACTION_SCAN);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    private void notifyFinish() {
        Log.e("test","notifyFinish");
        tv_now_time.setText("");
    }

    private int now_scan_time;

    private long now_millis;

    private void scan(int time) {
        Log.e("test","scan:"+time);
        now_scan_time = time;
        now_millis = System.currentTimeMillis();
        scan();
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

    private void updateUI(int remaining_time, int scan_interval, int next_time) {
//        Log.e("test","updateUI:"+remaining_time+","+scan_interval+","+next_time);
        tv_now_time.setText(String.format(getString(R.string.now_time_hint),
                remaining_time,createSessionPOJO.time * 60));
        if(scan_interval != -1 && next_time != -1) {
            tv_next_time_scan_interval.setText(
                    String.format(getString(R.string.scan_times_hint),
                            scan_interval,next_time,createSessionPOJO.frequency));
        }else {
            tv_next_time_scan_interval.setText("");
        }
    }

    private void unregisterAllBroadcastReceiver() {
        unregisterReceiver(broadcastReceiver);
    }

    private void onScanFinish() {
        if(now_scan_time > 0) {
//            Log.e("test","fin:" +now_scan_time);
//            Log.e("test",list.toString());
            SendScanDataThread sendScanDataThread = new SendScanDataThread(createSessionPOJO.sort_id,
                    now_millis,list);
            sendScanDataThread.start();
        }
    }
}
