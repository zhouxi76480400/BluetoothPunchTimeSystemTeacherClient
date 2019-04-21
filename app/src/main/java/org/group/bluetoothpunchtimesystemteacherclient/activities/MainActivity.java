package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.group.bluetoothpunchtimesystemteacherclient.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_PERMISSION_CODE = 0x0000011;

    private Toolbar toolbar;

    private LinearLayout ll_show_all_session, ll_start_new_session, ll_cv_set_students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkPermission();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ll_show_all_session = findViewById(R.id.ll_show_all_session);
        ll_show_all_session.setOnClickListener(this);
        ll_start_new_session = findViewById(R.id.ll_start_new_session);
        ll_start_new_session.setOnClickListener(this);
        ll_cv_set_students = findViewById(R.id.ll_cv_set_students);
        ll_cv_set_students.setOnClickListener(this);



    }

    private void checkPermission() {
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    showNoPermissionsDialog();
                }
                return;
        }
    }

    private void showNoPermissionsDialog() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_cv_set_students:
                gotoSetStudentsActivity();
                break;
        }
    }

    private void gotoSetStudentsActivity() {
        Intent intent = new Intent(this, SetStudentsActivity.class);
        startActivity(intent);
    }
}
