package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.ProgressBar;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.activities.adapters.StudentAdapter;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;

import java.util.List;

public class SetStudentsActivity extends AppCompatActivity implements
        MenuItem.OnActionExpandListener, ActionMode.Callback, SearchView.OnQueryTextListener {

    public static final int TURN_ON_BLUETOOTH_REQUEST_CODE = 0x000001;

    private SwipeRefreshLayout refresh_layout;

    private RecyclerView recycler_view;

    private StudentAdapter adapter;

    private ProgressBar progress;

    private ActionMode actionMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_students);
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
            actionMode.finish();
            removeUsersFromServer();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.actionMode = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_user:
                gotoAddAStudentActivity();
                return true;
            case R.id.menu_remove_user:
                showActionMode();
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
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(this,recycler_view);
        recycler_view.setAdapter(adapter);
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

    private void gotoAddAStudentActivity() {
        Intent intent = new Intent(this,AddAStudentActivity.class);
        startActivity(intent);
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
    private void readAllUsersDataFromServer() {


    }

    /**
     * must push to server
     */
    private void removeUsersFromServer() {

    }
}
