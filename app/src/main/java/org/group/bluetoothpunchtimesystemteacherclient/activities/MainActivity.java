package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;


public class MainActivity extends MyActivity implements View.OnClickListener {

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                MyApplication.getInstance().exit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout ll_show_all_session = findViewById(R.id.ll_show_all_session);
        ll_show_all_session.setOnClickListener(this);
        LinearLayout ll_start_new_session = findViewById(R.id.ll_start_new_session);
        ll_start_new_session.setOnClickListener(this);
        LinearLayout ll_cv_set_students = findViewById(R.id.ll_cv_set_students);
        ll_cv_set_students.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_cv_set_students:
                gotoSetStudentsActivity();
                break;
            case R.id.ll_start_new_session:
                onNewSessionButtonPressed();
                break;
        }
    }

    private void gotoSetStudentsActivity() {
        Intent intent = new Intent(this, SetStudentsActivity.class);
        startActivity(intent);
    }

    private void onNewSessionButtonPressed() {
//         DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
//             @Override
//             public void onClick(DialogInterface dialog, int which) {
//                 gotoNewSessionActivity(which == 0);
//             }
//         };
//         String list [] = {
//                 getString(R.string.start_a_new_session),
//                 getString(R.string.continue_a_exist_session)
//         };
//         AlertDialog alertDialog = new AlertDialog.Builder(this)
//                 .setTitle(getString(R.string.select_your_next_step))
//                 .setItems(list,onClickListener)
//                 .create();
//         alertDialog.show();
        gotoNewSessionActivity(true);
    }

    private void gotoNewSessionActivity(boolean isNewSession) {
        NewSessionActivity.openNewSessionActivity(this,isNewSession);
    }

}
