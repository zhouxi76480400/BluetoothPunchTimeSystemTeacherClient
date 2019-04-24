package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;

public class SetStudentsActivity extends AppCompatActivity {

    public static final int TURN_ON_BLUETOOTH_REQUEST_CODE = 0x000001;

    private SwipeRefreshLayout refresh_layout;

    private RecyclerView recycler_view;

    private ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_students);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_students, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_user:
                gotoAddAStudentActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeColors(getColor(R.color.colorAccent));
        recycler_view = findViewById(R.id.recycler_view);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);




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
                            .setNegativeButton(R.string.back,listener)
                            .setPositiveButton(R.string.retry,listener)
                            .setCancelable(false)
                            .create();
                    alertDialog.show();
                }else {
                    readAllUsersDataFromServer();
                }
            }
        }
    }

    private void gotoSystemBluetoothUI() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(enableBtIntent, TURN_ON_BLUETOOTH_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TURN_ON_BLUETOOTH_REQUEST_CODE) {
            // catch the turn on bt req
            if(resultCode == RESULT_OK) {
                // go to next phase
                readAllUsersDataFromServer();
            } else {
                checkBluetooth();
            }
        }
    }

    /**
     * need request network to download all student data
     */
    private void readAllUsersDataFromServer() {


    }

    private void gotoAddAStudentActivity() {
        Intent intent = new Intent(this,AddAStudentActivity.class);
        startActivity(intent);
    }

}
