package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.activities.adapters.StudentAdapter;
import org.group.bluetoothpunchtimesystemteacherclient.network.GetAllUsersThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.NetworkThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.RemoveUserThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.StatusCodeList;
import org.group.bluetoothpunchtimesystemteacherclient.objects.GetUserReturnPOJO;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class SetStudentsActivity extends AppCompatActivity implements
        MenuItem.OnActionExpandListener, ActionMode.Callback, SearchView.OnQueryTextListener,
        NetworkThread.OnNetworkThreadReturnListener, SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, StudentAdapter.StudentAdapterListener {

    public static final int TURN_ON_BLUETOOTH_REQUEST_CODE = 0x000001;

    private FrameLayout fl_main;

    private SwipeRefreshLayout refresh_layout;

    private RecyclerView recycler_view;

    private TextView tv_no_student;

    private StudentAdapter adapter;

    private ActionMode actionMode;

    private boolean isRequestServerNow;

    private boolean isFromFirstPage;

    private List<StudentInformationObject> dataSource;

    private Snackbar snackbarNoNetwork;

    private boolean isLastPageNow;

    private int lastRequestAddDataSize;

    private boolean isShowActionModeNow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_students);
        dataSource = new ArrayList<>();
        initView();
    }

    private MenuItem menu_item_add_user;

    private MenuItem menu_item_remove_user;

    private MenuItem menu_item_search_item;

    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_students, menu);
        menu_item_add_user = menu.findItem(R.id.menu_add_user);
        menu_item_remove_user = menu.findItem(R.id.menu_remove_user);
        menu_item_remove_user.setOnActionExpandListener(this);
        menu_item_search_item = menu.findItem(R.id.menu_search);
        menu_item_search_item.setOnActionExpandListener(this);

        searchView = (SearchView) menu_item_search_item.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//
//                Log.e("test","aaaaaaa");
//                menu_item_add_user.setVisible(true);
//                return true;
//            }
//        });


        return true;
    }

    private void showActionMode() {
        if(actionMode == null) {
            actionMode = startSupportActionMode(this);
            isShowActionModeNow = true;
            if(refresh_layout.isEnabled()) {
                refresh_layout.setEnabled(false);
            }
            showAndHideCheckbox(true);
        }

    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.menu_action_mode_remove_users,menu);
        actionMode.setTitle(getString(R.string.select_users_to_remove));
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.menu_remove_users_ok) {
//            actionMode.finish();
            removeUsersFromServer();
//            onActionItemHideChangeUI();
        }
        return true;
    }

    private void onActionItemHideChangeUI() {
        isShowActionModeNow = false;
        if(!refresh_layout.isEnabled()) {
            refresh_layout.setEnabled(true);
        }
        showAndHideCheckbox(false);
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
        onActionItemHideChangeUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_user:
                gotoAddAStudentActivity();
                return true;
            case R.id.menu_remove_user:
                if(!isRequestServerNow) {
                    showActionMode();
                }
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
        fl_main = findViewById(R.id.fl_main);
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeColors(getColor(R.color.colorAccent));
        refresh_layout.setOnRefreshListener(this);
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        tv_no_student = findViewById(R.id.tv_no_student);
        tv_no_student.setVisibility(View.GONE);
        adapter = new StudentAdapter(this,recycler_view,dataSource);
        adapter.setStudentAdapterListener(this);
        recycler_view.setAdapter(adapter);
        snackbarNoNetwork = Snackbar.make(fl_main, getString(R.string.no_network_hint),
                Snackbar.LENGTH_INDEFINITE);
        snackbarNoNetwork.setAction(getString(R.string.retry),
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(!MyApplication.getInstance().isNetworkOn()) {
                            fl_main.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    snackbarNoNetwork.show();
                                }
                            },300);
                            return;
                        }
                        readAllUsersDataFromServer(getLastId());
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
                    readAllUsersDataFromServer(0);
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
        if(requestCode == TURN_ON_BLUETOOTH_REQUEST_CODE) {
            // catch the turn on bt req
            if(resultCode == RESULT_OK) {
                // go to next phase
//                readAllUsersDataFromServer();
                Log.e("test","go back");
            } else {
                checkBluetooth();
            }
        }else if(requestCode == AddAStudentActivity.REQUEST_CODE_ADD_A_STUDENT) {
            if(resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if(bundle != null) {
                    Serializable serializable =
                            bundle.getSerializable(AddAStudentActivity.INTENT_OBJECT_KEY);
                    if(serializable != null && serializable instanceof StudentInformationObject) {
                        StudentInformationObject studentInformationObject =
                                (StudentInformationObject) serializable;
                        dataSource.add(0,studentInformationObject);
                        adapter.notifyItemInserted(0);
                    }
                }
            }
        }else if(requestCode == AddAStudentActivity.REQUEST_CODE_EDIT_A_STUDENT) {
            if(resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Serializable serializable =
                        bundle.getSerializable(AddAStudentActivity.INTENT_OBJECT_KEY);
                if(serializable != null && serializable instanceof StudentInformationObject) {
                    StudentInformationObject studentInformationObject =
                            (StudentInformationObject) serializable;
                    int pos = -1;
                    for(int i = 0 ; i < dataSource.size() ; i ++) {
                        StudentInformationObject tmp = dataSource.get(i);
                        if(studentInformationObject.id == tmp.id) {
                            pos = i;
                            break;
                        }
                    }
                    if(pos != -1) {
                        dataSource.remove(pos);
                        dataSource.add(pos,studentInformationObject);
                        adapter.notifyItemChanged(pos);
                    }
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void gotoAddAStudentActivity() {
        AddAStudentActivity.gotoAddAStudentActivityForNewStudent(this);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        if(item.getItemId() == R.id.menu_search) {
            menu_item_add_user.setVisible(false);
            menu_item_remove_user.setVisible(false);
        }else if(item.getItemId() == R.id.menu_remove_user) {
            menu_item_add_user.setVisible(false);
            menu_item_search_item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        if(item.getItemId() == R.id.menu_search) {
            menu_item_add_user.setVisible(true);
            menu_item_remove_user.setVisible(true);
        }else if(item.getItemId() == R.id.menu_remove_user) {
            menu_item_add_user.setVisible(true);
            menu_item_search_item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Log.e("test",s);

        if(searchView != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            }
            searchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        Log.e("test",s);
        return true;
    }

    /**
     * need request network to download all student data
     */
    private void readAllUsersDataFromServer(int last_number) {
        if(!isRequestServerNow && !isShowActionModeNow) {
            if(!MyApplication.getInstance().isNetworkOn()) {
                if(refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);
                }
                if(!snackbarNoNetwork.isShown()) {
                    snackbarNoNetwork.show();
                }
                return;
            }
            boolean isFromFirst = false;
            if(last_number == 0) {
                isFromFirst = true;
                dataSource.clear();
            }
            isFromFirstPage = isFromFirst;
//            changeUI(isFromFirstPage,true);
            isRequestServerNow = true;
            GetAllUsersThread thread = new GetAllUsersThread(last_number,this);
            thread.start();
        }
    }

    private void changeUI(boolean isFromFirstPage,boolean isRequestServerNow) {
        if(isRequestServerNow) {
            if(isFromFirstPage) {
                if(!refresh_layout.isRefreshing())
                    refresh_layout.setRefreshing(true);
            }else {

            }
        }else {
            if(isFromFirstPage) {
                if(refresh_layout.isRefreshing())
                    refresh_layout.setRefreshing(false);
            }else {

            }
        }
        this.isFromFirstPage = isFromFirstPage;
        this.isRequestServerNow = isRequestServerNow;

        if(isFromFirstPage) {
//            Log.e("test","isFromFirstPage");
            adapter.notifyDataSetChanged();
        }else {
            int last = dataSource.size() - lastRequestAddDataSize;
            adapter.notifyItemRangeInserted(last,lastRequestAddDataSize);
            adapter.notifyItemChanged(dataSource.size());
        }


        Log.e("test",isLastPageNow+"isLastPage");
        if(dataSource.size() == 0) {
            if(tv_no_student.getVisibility() != View.VISIBLE) {
                tv_no_student.setVisibility(View.VISIBLE);
            }
        }else {
            if(tv_no_student.getVisibility() != View.GONE) {
                tv_no_student.setVisibility(View.GONE);
            }
        }
    }

    /**
     * must push to server
     */
    private void removeUsersFromServer() {
        List<Integer> removeList = new ArrayList<>();
        // copy a new one
        Map<Integer,Boolean> tmp = adapter.getSelectedMap();
        Iterator iterator = tmp.keySet().iterator();
        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            Boolean aBoolean = tmp.get(integer);
            if(aBoolean) {
                removeList.add((int) dataSource.get(integer).id);
            }
        }
        // send to server
        isRequestServerNow = true;
        RemoveUserThread removeUserThread = new RemoveUserThread(removeList);
        removeUserThread.start();




//        adapter.cleanMap();
    }

    @Override
    public void onNetworkThreadGetDataSuccessful(Response data) {
        int code = data.code();
        if(code == HttpURLConnection.HTTP_OK) {
            String json = null;
            try {
                json = data.body().string();
            } catch (IOException e) {
                onNetworkThreadGetDataFailed(StatusCodeList.STATUS_CODE_JSON_PARAMETER_NOT_EQUALS);
                e.printStackTrace();
            }
            decodeJSON(json);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeNetworkFlag();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onNetworkThreadGetDataFailed(code);
                }
            });
        }
    }

    @Override
    public void onNetworkThreadGetDataFailed(int statusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onNetworkThreadGetDataFailedMainThread(statusCode);
            }
        });
    }

    private void onNetworkThreadGetDataFailedMainThread(int statusCode) {
        changeNetworkFlag();
        if(statusCode == NetworkThread.STATUS_CODE_NO_NETWORK) {
            if(!snackbarNoNetwork.isShown())
                snackbarNoNetwork.show();
        }
    }

    private void changeNetworkFlag() {
        if(isRequestServerNow) {
            if(refresh_layout.isRefreshing()) {
                refresh_layout.setRefreshing(false);
            }
            if(snackbarNoNetwork != null) {
                if(MyApplication.getInstance().isNetworkOn()) {
                    if(snackbarNoNetwork.isShown())
                        snackbarNoNetwork.dismiss();
                }
            }
            changeUI(isFromFirstPage,!isRequestServerNow);
        }
    }

    @Override
    public void onRefresh() {
        readAllUsersDataFromServer(0);
    }

    @Override
    public void onClick(View v) {

    }

    private boolean isRequestFromFirstPage() {
        if(dataSource.size() > 0)
            return false;
        else
            return true;
    }

    private int getLastId() {
        if(dataSource.size() > 0) {
            StudentInformationObject studentInformationObject
                    = dataSource.get(dataSource.size() - 1);
            return (int) studentInformationObject.id;
        }
        return 0;
    }

    private void decodeJSON(String data) {
        if(isFromFirstPage) {
            Log.e("test","isFromFirstPage");
            dataSource.clear();
        }
        Gson gson = new Gson();
        GetUserReturnPOJO getUserReturnPOJO = gson.fromJson(data, GetUserReturnPOJO.class);
        isLastPageNow = getUserReturnPOJO.end_page;
        lastRequestAddDataSize = getUserReturnPOJO.data.size();
        dataSource.addAll(getUserReturnPOJO.data);
        Log.e("test",data);
    }

    private void showAndHideCheckbox(boolean isShowCheckbox) {
        adapter.isShowCheckbox = isShowCheckbox;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoad() {
        if(!isRequestServerNow) {
            Log.e("test","aaaaaaa");
            int id = (int) dataSource.get(dataSource.size() - 1).id;
            Log.e("test","aaaa:"+id);

            readAllUsersDataFromServer(id);
        }
    }

    @Override
    public boolean isLastPage() {
        return isLastPageNow;
    }

    @Override
    public void onItemPress(int position) {
        StudentInformationObject studentInformationObject = dataSource.get(position);
        AddAStudentActivity.gotoAddAnStudentActivityForExistStudent(
                this,studentInformationObject);
    }
}
