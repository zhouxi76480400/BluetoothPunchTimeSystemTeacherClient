package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import org.group.bluetoothpunchtimesystemteacherclient.R;

public class AddAStudentActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout til_mac_addr;

    private Button btn_scan;


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

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        til_mac_addr = findViewById(R.id.til_mac_addr);
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                openBluetoothScanDialog();
                break;


        }
    }

    private void openBluetoothScanDialog() {
        Intent intent = new Intent(this,BluetoothScanActivity.class);
        startActivityForResult(intent,BluetoothScanActivity.BLE_SCAN_REQ_CODE);
    }
}
