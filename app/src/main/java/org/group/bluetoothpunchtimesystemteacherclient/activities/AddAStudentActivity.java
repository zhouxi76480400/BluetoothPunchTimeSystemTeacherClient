package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.network.AddUsersThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.NetworkThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.StatusCodeList;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class AddAStudentActivity extends AppCompatActivity implements View.OnClickListener,
        NetworkThread.OnNetworkThreadReturnListener {

    public static void gotoAddAStudentActivityForNewStudent(Activity context) {
        Intent intent = new Intent(context,AddAStudentActivity.class);
        context.startActivityForResult(intent,AddAStudentActivity.REQUEST_CODE_ADD_A_STUDENT);
    }

    public static void gotoAddAnStudentActivityForExistStudent(
            Activity context, StudentInformationObject studentInformationObject) {
        Intent intent = new Intent(context,AddAStudentActivity.class);
        intent.setAction(REQUEST_CODE_EDIT);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_OBJECT_KEY, studentInformationObject);
        intent.putExtras(bundle);
        context.startActivityForResult(intent,AddAStudentActivity.REQUEST_CODE_EDIT_A_STUDENT);
    }

    public static String REQUEST_CODE_EDIT = "REQUEST_CODE_EDIT";

    public static int REQUEST_CODE_ADD_A_STUDENT = 12;

    public static int REQUEST_CODE_EDIT_A_STUDENT = 14;

    public static String INTENT_OBJECT_KEY = "new_user_obj";

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

    private boolean isRequestNow;

    private String waitingUpdateJSON;

    private StudentInformationObject waitingUpdateObject;

    private Snackbar snackbarNoNetwork;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_student);
        getData();
        initView();
        modifyData();
    }

    private void modifyData() {
        if(is_edit) {
            til_mac_addr.getEditText().setText(waitingUpdateObject.mac_address);
            til_student_number.getEditText().setText(waitingUpdateObject.student_number);
            til_first_name.getEditText().setText(waitingUpdateObject.first_name);
            til_last_name.getEditText().setText(waitingUpdateObject.last_name);
            til_mac_addr.getEditText().requestFocus();
            showIME();
        }
    }

    private void getData() {
        Intent intent = getIntent();
        if(intent != null) {
            if(intent.getAction() != null && intent.getAction().equals(REQUEST_CODE_EDIT)) {
                Bundle bundle = intent.getExtras();
                if(bundle != null) {
                    Serializable serializable = bundle.getSerializable(INTENT_OBJECT_KEY);
                    if(serializable != null && serializable instanceof StudentInformationObject) {
                        StudentInformationObject studentInformationObject =
                                (StudentInformationObject) serializable;
                        waitingUpdateObject = studentInformationObject;
                        is_edit = true;
                    }
                }
            }
        }
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
                if(!isRequestNow) {
                    checkInput();
                }
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
                    onBackPressed();
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
        snackbarNoNetwork =
                Snackbar.make(ll_main,getString(R.string.no_network_hint),
                        Snackbar.LENGTH_INDEFINITE);
        snackbarNoNetwork.setAction(getString(R.string.retry),new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!MyApplication.getInstance().isNetworkOn()) {
                    ll_main.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!snackbarNoNetwork.isShown()) {
                                snackbarNoNetwork.show();
                            }
                        }
                    },300);
                    return;
                }
                checkInput();
            }
        });

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
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.
                hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
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
            StudentInformationObject studentInformationObject = null;
            if(waitingUpdateObject != null) {
                studentInformationObject = waitingUpdateObject;
            }else {
                studentInformationObject = new StudentInformationObject();
            }
            studentInformationObject.mac_address = mac_address;
            studentInformationObject.student_number = student_number;
            studentInformationObject.last_name = last_name;
            studentInformationObject.first_name = first_name;
            Gson gson = new Gson();
            waitingUpdateJSON = gson.toJson(studentInformationObject);
            waitingUpdateObject = studentInformationObject;
            sendNewDataToServer();
        }
    }

    private void sendNewDataToServer() {
        if(!isRequestNow) {
            if(!MyApplication.getInstance().isNetworkOn()) {
                if(!snackbarNoNetwork.isShown()) {
                    snackbarNoNetwork.show();
                }
                return;
            }
            if(snackbarNoNetwork.isShown()) {
                snackbarNoNetwork.dismiss();
            }
            AddUsersThread addUsersThread = new AddUsersThread(is_edit,waitingUpdateJSON);
            addUsersThread.setOnNetworkThreadReturnListener(this);
            setIsRequestNow(true);
            addUsersThread.start();
        }
    }

    @Override
    public void onBackPressed() {
        if(isRequestNow) {
            Snackbar.make(ll_main,getString(R.string.waiting_for_update_hint),
                    BaseTransientBottomBar.LENGTH_SHORT).show();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNetworkThreadGetDataSuccessful(Class clazz, Response response) {
        String data = null;
        try {
            data = response.body().string();
        } catch (IOException e) {
            onNetworkThreadGetDataFailed(clazz, NetworkThread.STATUS_CODE_REMOTE_SERVER_PROBLEM);
            e.printStackTrace();
            return;
        }
        int status_code = StatusCodeList.STATUS_CODE_USER_DATA_NOT_WRITE_SUCCESS;
        int id = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            status_code = Integer.valueOf(jsonObject.getString("s"));
            if(status_code == StatusCodeList.STATUS_CODE_OK && !is_edit) {
                id = Integer.valueOf(jsonObject.getString("id"));
            }
        } catch (JSONException e) {
            onNetworkThreadGetDataFailed(clazz, NetworkThread.STATUS_CODE_REMOTE_SERVER_PROBLEM);
            e.printStackTrace();
            return;
        }
        if(status_code != StatusCodeList.STATUS_CODE_OK) {
            onNetworkThreadGetDataFailed(clazz, status_code);
            return;
        }
        if(!is_edit)
            waitingUpdateObject.id = id;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_OBJECT_KEY,waitingUpdateObject);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String hint = null;
                if(is_edit) {
                    hint = getString(R.string.edit_a_user_success_hint);
                }else {
                    hint = getString(R.string.add_a_user_success_hint);
                }
                Toast.makeText(AddAStudentActivity.this,hint,Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onNetworkThreadGetDataFailed(Class clazz, int statusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onNetworkThreadGetDataFailedRunOnUiThread(statusCode);
            }
        });
    }

    private void onNetworkThreadGetDataFailedRunOnUiThread(int statusCode) {
        Log.e("error_code", String.valueOf(statusCode));
        setIsRequestNow(false);
        String hint = null;
        EditText needFocus = null;
        if(statusCode == StatusCodeList.STATUS_CODE_MAC_ADDRESS_EXIST) {
            hint = getString(R.string.mac_address_exists);
            needFocus = til_mac_addr.getEditText();
        }else if(statusCode == StatusCodeList.STATUS_CODE_STUDENT_NUMBER_EXIST) {
            hint = getString(R.string.student_number_exists);
            needFocus = til_student_number.getEditText();
        }else{
            hint = getString(R.string.unknown_error);
        }
        Snackbar.make(ll_main,hint,Snackbar.LENGTH_SHORT).show();
        if(needFocus != null) {
            needFocus.requestFocus();
            needFocus.setSelection(needFocus.getText().length());
            showIME();
        }
    }

    private void showIME() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setIsRequestNow(boolean isRequestNow) {
        if(isRequestNow) {
            if(progress.getVisibility() != View.VISIBLE) {
                progress.setVisibility(View.VISIBLE);
            }
            if(scroll_view.getVisibility() != View.GONE) {
                scroll_view.setVisibility(View.GONE);
            }
        }else {
            if(progress.getVisibility() != View.GONE) {
                progress.setVisibility(View.GONE);
            }
            if(scroll_view.getVisibility() != View.VISIBLE) {
                scroll_view.setVisibility(View.VISIBLE);
            }
        }
        this.isRequestNow = isRequestNow;
    }

}
