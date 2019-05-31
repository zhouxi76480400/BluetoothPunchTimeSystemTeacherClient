package org.group.bluetoothpunchtimesystemteacherclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.activities.adapters.SessionInformationAdapter;
import org.group.bluetoothpunchtimesystemteacherclient.network.GetSessionInformationThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.NetworkThread;
import org.group.bluetoothpunchtimesystemteacherclient.network.StatusCodeList;
import org.group.bluetoothpunchtimesystemteacherclient.objects.CreateSessionPOJO;
import org.group.bluetoothpunchtimesystemteacherclient.objects.GetSessionsReturnPOJO;
import org.group.bluetoothpunchtimesystemteacherclient.objects.GetSessionsReturnPOJOList;
import org.group.bluetoothpunchtimesystemteacherclient.objects.InformationsClass;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Response;

public class SessionInformationActivity extends AppCompatActivity implements
        NetworkThread.OnNetworkThreadReturnListener {

    public static final String POJO_KEY = "CreateSessionPOJO";

    public static void gotoSessionInformationActivity(Activity activity,
                                                      CreateSessionPOJO createSessionPOJO) {
        Intent intent = new Intent(activity,SessionInformationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(POJO_KEY,createSessionPOJO);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    private CreateSessionPOJO createSessionPOJO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_information);
        informationsClasses = new ArrayList<>();
        dataSource = new ArrayList<>();
        getData();
        initView();
        getDataFromServer();
    }

    private void getData() {
        try {
            Bundle bundle = getIntent().getExtras();
            createSessionPOJO = (CreateSessionPOJO) bundle.getSerializable(POJO_KEY);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(createSessionPOJO == null) {
            finish();
        }
    }

    private RecyclerView recycler_view;

    private SessionInformationAdapter adapter;

    private ProgressBar progress;

    private List<GetSessionsReturnPOJO> dataSource;

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(createSessionPOJO.name);
        setSupportActionBar(toolbar);
        ActionBar actionBar = null;
        if((actionBar = getSupportActionBar()) != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SessionInformationAdapter(informationsClasses);
        recycler_view.setAdapter(adapter);
        progress = findViewById(R.id.progress);
    }

    private boolean isRequestNetwork;

    @Override
    public void onBackPressed() {
        if(isRequestNetwork) {
            Toast.makeText(this,getString(R.string.waiting),Toast.LENGTH_SHORT).show();
        }else
            super.onBackPressed();
    }

    private void getDataFromServer() {
        isRequestNetwork = true;
        GetSessionInformationThread getSessionInformationThread =
                new GetSessionInformationThread(createSessionPOJO.sort_id);
        getSessionInformationThread.setOnNetworkThreadReturnListener(this);
        getSessionInformationThread.start();
    }

    @Override
    public void onNetworkThreadGetDataSuccessful(Class clazz, Response data) {
        isRequestNetwork = false;
        int code = data.code();
        if(code == HttpURLConnection.HTTP_OK) {
            try {
                String str = data.body().string();
                String json = URLDecoder.decode(str,"UTF-8");
                JSONObject jsonObject = new JSONObject(json);
                String s = jsonObject.getString("s");
                if(s.equals(String.valueOf(StatusCodeList.STATUS_CODE_OK))) {
                    GetSessionsReturnPOJOList list =
                            new Gson().fromJson(json,GetSessionsReturnPOJOList.class);
                    List<GetSessionsReturnPOJO> pojoList = list.l;
                    dataSource.addAll(pojoList);
                    processData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }else {
                    onNetworkThreadGetDataFailed(clazz,StatusCodeList.
                            STATUS_CODE_JSON_PARAMETER_NOT_EQUALS);
                }
            } catch (Exception e) {
                onNetworkThreadGetDataFailed(clazz,StatusCodeList.
                        STATUS_CODE_JSON_PARAMETER_NOT_EQUALS);
                e.printStackTrace();
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
        isRequestNetwork = false;




    }

    private Map<Long,StudentInformationObject> id_info_map;
    private Map<Long,Integer> times_map;
    private int count;

    private void processData() {
        if(dataSource != null) {
            // check
            Map<Long,StudentInformationObject> map = new HashMap<>();
            Map<Long,Integer> times = new HashMap<>();
            count = dataSource.size();
            for(int i = 0 ; i < dataSource.size() ; i ++ ) {
                GetSessionsReturnPOJO getSessionsReturnPOJO = dataSource.get(i);
                List<StudentInformationObject> studentInformationObjects = getSessionsReturnPOJO.data;
                for(int j = 0 ; j < studentInformationObjects.size() ; j ++) {
                    StudentInformationObject studentInformationObject =
                            studentInformationObjects.get(j);
                    map.put(studentInformationObject.id,studentInformationObject);
                    //
                    Integer integer = times.get(studentInformationObject.id);
                    if(integer == null) {
                        integer = 0;
                    }
                    integer ++;
                    times.put(studentInformationObject.id,integer);

                }
            }
            times_map = times;
            id_info_map = map;
            handleNext();
        }
    }

    private List<InformationsClass> informationsClasses;

    private void handleNext() {
        for (Long key : times_map.keySet()) {
            int value = times_map.get(key);
            StudentInformationObject studentInformationObject = id_info_map.get(key);
            int mid = count / 2;
            boolean ok = false;
            if(value > mid) {
                ok = true;
            }
            Log.e("test",studentInformationObject.last_name+","+ok);
            InformationsClass informationsClass = new InformationsClass();
            informationsClass.isOK = ok;
            informationsClass.studentInformationObject = studentInformationObject;
            informationsClasses.add(informationsClass);
        }
    }

}
