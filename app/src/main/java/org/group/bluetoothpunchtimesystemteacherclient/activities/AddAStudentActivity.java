package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ReplacementTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.google.gson.Gson;

import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;

import java.util.ArrayList;
import java.util.List;

public class AddAStudentActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * check this flag, if this value is true, should submit to update API.
     */
    private boolean is_edit;

    private LinearLayout ll_main;

    private TextInputLayout til_mac_addr;

    private TextInputLayout til_student_number;

    private TextInputLayout til_last_name;

    private TextInputLayout til_first_name;

    private Button btn_scan;

    private ProgressBar progress;

    private ScrollView scroll_view;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_student);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_a_student,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ok:
                checkInput();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(is_edit) {
            toolbar.setTitle(getString(R.string.edit_student_information));
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        ll_main = findViewById(R.id.ll_main);
        scroll_view = findViewById(R.id.scroll_view);
        progress = findViewById(R.id.progress);
        til_mac_addr = findViewById(R.id.til_mac_addr);
        til_mac_addr.getEditText().setTransformationMethod(new ReplacementTransformationMethod() {

            private char[] lower = {'a','b','c','d','e','f'};

            private char[] upper = {'A','B','C','D','E','F'};

            @Override
            protected char[] getOriginal() {
                return lower;
            }

            @Override
            protected char[] getReplacement() {
                return upper;
            }
        });
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
        til_student_number = findViewById(R.id.til_student_number);
        til_last_name = findViewById(R.id.til_last_name);
        til_first_name = findViewById(R.id.til_first_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                openBluetoothScanActivity();
                break;


        }
    }

    private void openBluetoothScanActivity() {
        Intent intent = new Intent(this,BluetoothScanActivity.class);
        startActivityForResult(intent,BluetoothScanActivity.BLE_SCAN_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == BluetoothScanActivity.BLE_SCAN_REQ_CODE) {
            if(resultCode == RESULT_OK && data != null) {
                Bundle bundle = data.getExtras();
                BluetoothDevice bluetoothDevice =
                        bundle.getParcelable(BluetoothScanActivity.BT_DEVICE_KEY);
                if(bluetoothDevice != null) {
                    til_mac_addr.getEditText().setText(
                            removeColonFromMACAddress(bluetoothDevice.getAddress()));
                    til_student_number.getEditText().requestFocus();
                }
            }
        }
    }

    private String removeColonFromMACAddress(String mac) {
        if(mac != null) {
            String[] array = mac.split(":");
            StringBuilder stringBuilder = new StringBuilder();
            for(String s : array) {
                stringBuilder.append(s);
            }
            return stringBuilder.toString();
        }
        return null;
    }

    private void checkInput() {
        String mac_address = til_mac_addr.getEditText().getText().toString();
        String student_number = til_student_number.getEditText().getText().toString();
        String last_name = til_last_name.getEditText().getText().toString();
        String first_name = til_first_name.getEditText().getText().toString();
        boolean not_filled = false;
        List<String> which_not_filled_list = new ArrayList<>();
        List<View> which_not_filled_view_list = new ArrayList<>();
        if(mac_address.length() != getResources().getInteger(R.integer.mac_address_length)) {
            not_filled = true;
            which_not_filled_list.add(getString(R.string.mac_address));
            which_not_filled_view_list.add(til_mac_addr.getEditText());
        }
        if(student_number.length() == 0) {
            not_filled = true;
            which_not_filled_list.add(getString(R.string.student_number));
            which_not_filled_view_list.add(til_student_number.getEditText());
        }
        if(last_name.length() == 0) {
            not_filled = true;
            which_not_filled_list.add(getString(R.string.last_name));
            which_not_filled_view_list.add(til_last_name.getEditText());
        }
        if(first_name.length() == 0) {
            not_filled = true;
            which_not_filled_list.add(getString(R.string.first_name));
            which_not_filled_view_list.add(til_first_name.getEditText());
        }
        if(not_filled) {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0 ; i < which_not_filled_list.size() ; i++) {
                String txt = which_not_filled_list.get(i);
                stringBuilder.append(txt);
                if(i != which_not_filled_list.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            which_not_filled_view_list.get(0).requestFocus();
            String hint_text = String.format(getString(R.string.student_data_not_filled_hint),
                    stringBuilder.toString());
            Snackbar.make(ll_main,hint_text,Snackbar.LENGTH_SHORT).show();
        }else {
            StudentInformationObject studentInformationObject = new StudentInformationObject();
            studentInformationObject.mac_address = mac_address;
            studentInformationObject.student_number = student_number;
            studentInformationObject.last_name = last_name;
            studentInformationObject.first_name = first_name;
            Gson gson = new Gson();
            String json = gson.toJson(studentInformationObject);
            Log.e("test",json);
            scroll_view.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);

        }
    }
}
