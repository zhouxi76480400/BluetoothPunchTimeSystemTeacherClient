package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.network.CreateSessionThread;
import org.group.bluetoothpunchtimesystemteacherclient.objects.CreateSessionPOJO;
import org.group.bluetoothpunchtimesystemteacherclient.views.RadarView;

public class NewSessionActivity extends MyActivity {

    private static final String IS_NEW_SESSION_TAG = "IS_NEW_SESSION_TAG";

    public static void openNewSessionActivity(Activity aty, boolean isNewSession) {
        Intent intent = new Intent(aty,NewSessionActivity.class);
        intent.putExtra(IS_NEW_SESSION_TAG,isNewSession);
        aty.startActivity(intent);
    }

    private boolean isNewSession;

    private ProgressDialog waitingForSessionCreateDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsession);
        getData();
        initView();
        checkPermission();
        checkBluetooth();
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

    private boolean isRecordingNow() {
        return false;
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
            if(waitingForSessionCreateDialog == null) {
                waitingForSessionCreateDialog = new ProgressDialog(this);
                waitingForSessionCreateDialog.setCancelable(false);
                waitingForSessionCreateDialog.setMessage(getString(R.string.create_new_session_now));
            }
//            waitingForSessionCreateDialog.show();
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

}
