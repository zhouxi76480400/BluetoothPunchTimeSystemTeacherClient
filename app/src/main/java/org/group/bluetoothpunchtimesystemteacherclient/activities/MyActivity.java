package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class MyActivity extends AppCompatActivity {


    public static final int TURN_ON_BLUETOOTH_REQUEST_CODE = 0x000001;

    public static final int REQUEST_PERMISSION_CODE = 0x0000011;

    protected void checkPermission() {
        String [] permissions = null;
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            if(info.requestedPermissions != null) {
                permissions = info.requestedPermissions;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AtomicBoolean needRequestPermission = new AtomicBoolean(false);
        for (String permission : permissions) {
            if(!needRequestPermission.get()) {
                int havePermission = PermissionChecker.
                        checkSelfPermission(this, permission);
                if(havePermission != PermissionChecker.PERMISSION_GRANTED) {
                    needRequestPermission.set(true);
                }
            }
        }
        if(needRequestPermission.get()) {
            //request permission
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean isGranted = true;
                    for(int grantResult : grantResults) {
                        if(isGranted) {
                            if(grantResult != PermissionChecker.PERMISSION_GRANTED) {
                                isGranted = false;
                            }
                        }
                    }
                    if(!isGranted) {
                        showNoPermissionsDialog();
                    }
                }
                return;
        }
    }

    private void showNoPermissionsDialog() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE) {
                    checkPermission();
                }else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    MyApplication.getInstance().exit();
                }
            }
        };
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.system_permission)
                .setMessage(R.string.no_permissions_hint)
                .setCancelable(false)
                .setPositiveButton(R.string.retry,onClickListener)
                .setNegativeButton(R.string.exit,onClickListener)
                .create();
        alertDialog.show();
    }

    protected void checkBluetooth() {
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
                // check bluetooth is open ?
                if(!bluetoothAdapter.isEnabled()) {
                    AlertDialog.OnClickListener listener = new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_NEUTRAL:
                                    gotoSystemBluetoothUI();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    finish();
                                    break;
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkBluetooth();
                                    break;
                            }
                        }
                    };
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.system_hardware)
                            .setMessage(R.string.not_turn_on_the_bt_notification)
                            .setNeutralButton(R.string.to_turn_on_bt,listener)
                            .setNegativeButton(R.string.txt_back,listener)
                            .setPositiveButton(R.string.retry,listener)
                            .setCancelable(false)
                            .create();
                    alertDialog.show();
                }else {
                    isBluetoothOpen();
                }
            }
        }
    }

    public void isBluetoothOpen() {

    }

    private void gotoSystemBluetoothUI() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(enableBtIntent, TURN_ON_BLUETOOTH_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
