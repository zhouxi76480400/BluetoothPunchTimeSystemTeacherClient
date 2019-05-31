package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.activities.adapters.SessionListAdapter;
import org.group.bluetoothpunchtimesystemteacherclient.network.GetSessionsThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.NetworkThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.StatusCodeList;
import org.group.bluetoothpunchtimesystemteacherclient.objects.AllSessionsGetObject;
import org.group.bluetoothpunchtimesystemteacherclient.objects.CreateSessionPOJO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class AllSessionsListActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener, NetworkThread.OnNetworkThreadReturnListener,
        SessionListAdapter.SessionListAdapterListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allsessionslist);
        initView();
        requestFromNetworkDelay();
    }

    private SwipeRefreshLayout refresh_layout;

    private RecyclerView recycler_view;

    private SessionListAdapter adapter;

    private TextView tv_no_record;

    private List<CreateSessionPOJO> createSessionPOJOList;

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.all_records));
        ActionBar actionBar = null;
        if((actionBar = getSupportActionBar()) != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllSessionsListActivity.this.finish();
            }
        });
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeColors(getColor(R.color.colorAccent));
        refresh_layout.setOnRefreshListener(this);
        recycler_view = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        createSessionPOJOList = new ArrayList<>();
        adapter = new SessionListAdapter(recycler_view,createSessionPOJOList);
        adapter.setSessionListAdapterListener(this);
        recycler_view.setAdapter(adapter);
        tv_no_record = findViewById(R.id.tv_no_record);
        tv_no_record.setVisibility(View.GONE);

    }

    @Override
    public void onRefresh() {
        doRequest(0);
    }

    private void requestFromNetworkDelay() {
        recycler_view.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh_layout.setRefreshing(true);
                doRequest(0);
            }
        },100);
    }

    private boolean isRequestDataNow;

    private boolean isEndPage;

    private boolean isRequestFirstPage;

    private void doRequest(int lastSid) {
        if(!isRequestDataNow) {
            GetSessionsThread getSessionsThread = new GetSessionsThread(lastSid);
            if(lastSid > 0) {
                isRequestFirstPage = false;
            }else {
                isRequestFirstPage = true;
            }
            getSessionsThread.setOnNetworkThreadReturnListener(this);
            getSessionsThread.start();
            isRequestDataNow = true;
        }
    }

    @Override
    public void onNetworkThreadGetDataSuccessful(Class clazz, Response data) {
        isRequestDataNow = false;
        int code = data.code();
        if(code == HttpURLConnection.HTTP_OK) {
            String json = null;
            try {
                json = data.body().string();
            } catch (IOException e) {
                onNetworkThreadGetDataFailed(clazz, StatusCodeList.STATUS_CODE_JSON_PARAMETER_NOT_EQUALS);
                e.printStackTrace();
            }
            if(clazz == GetSessionsThread.class) {
                AllSessionsGetObject object = null;
                try {
                    object = new Gson().fromJson(json,AllSessionsGetObject.class);
                }catch (Exception e) {
                    onNetworkThreadGetDataFailed(clazz, StatusCodeList.STATUS_CODE_JSON_PARAMETER_NOT_EQUALS);
                    e.printStackTrace();
                }
                isEndPage = object.end_page;
                int old_size = 0;
                if(isRequestFirstPage) {
                    createSessionPOJOList.clear();
                }else {
                    old_size = createSessionPOJOList.size();
                }
                int add_size = object.data.size();
                createSessionPOJOList.addAll(object.data);
                int finalOld_size = old_size;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeRefreshUI();
                        if(isRequestFirstPage) {
                            adapter.notifyDataSetChanged();
                        }else {
                            adapter.notifyItemRangeChanged(finalOld_size,add_size);
                        }
                    }
                });
            }
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onNetworkThreadGetDataFailed(clazz,code);
                }
            });
        }
    }

    @Override
    public void onNetworkThreadGetDataFailed(Class clazz, int statusCode) {
        isRequestDataNow = false;
        if(clazz == GetSessionsThread.class) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeRefreshUI();
                }
            });
        }

        Log.e("test","onNetworkThreadGetDataFailed");

    }

    private void changeRefreshUI() {
        if(!isRequestDataNow) {
            if(refresh_layout.isRefreshing()) {
               refresh_layout.setRefreshing(false);
            }
        }else {
            if(!refresh_layout.isRefreshing()) {
                refresh_layout.setRefreshing(true);
            }
        }
    }

    @Override
    public boolean isLastPage() {
        return isEndPage;
    }

    @Override
    public void onPressed(int position, SessionListAdapter which) {
        CreateSessionPOJO createSessionPOJO = createSessionPOJOList.get(position);
        SessionInformationActivity.gotoSessionInformationActivity(this,createSessionPOJO);
    }

    @Override

    public void onLoad() {
        if(!isEndPage) {
            int last_sid = createSessionPOJOList.size();
            Log.e("test","test:"+last_sid);
            doRequest(last_sid);
        }
    }
}
