package org.group.bluetoothpunchtimesystemteacherclient.activitys;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;

public class SetStudentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_students);
        initView();

    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.set_students));
        ActionBar actionBar = null;
        if((actionBar = getSupportActionBar()) != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetStudentsActivity.this.finish();
            }
        });



        checkBluetooth();
    }



    private void checkBluetooth() {
        int permission =
                PermissionChecker.checkCallingOrSelfPermission(this, Manifest.permission.BLUETOOTH);
        DialogInterface.OnClickListener exit_listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                MyApplication.getInstance().exit();
            }
        };
        if(PackageManager.PERMISSION_GRANTED != permission) {

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.system_permission))
                    .setMessage(getString(R.string.no_bt_permission))
                    .setPositiveButton(getString(R.string.ok),exit_listener)
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        }else {
            //
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null) {
                //no adapter
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.system_hardware))
                        .setMessage(getString(R.string.no_bluetooth_hardware))
                        .setPositiveButton(getString(R.string.ok),exit_listener)
                        .setCancelable(false)
                        .create();
                alertDialog.show();
            }else {

            }


        }
    }

}
