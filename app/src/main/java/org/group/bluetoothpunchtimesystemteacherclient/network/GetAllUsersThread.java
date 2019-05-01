package org.group.bluetoothpunchtimesystemteacherclient.network;

import android.util.Log;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetAllUsersThread extends NetworkThread {

    private long lastNumber;

    private OnNetworkThreadReturnListener listener;

    public void setOnNetworkThreadReturnListener(OnNetworkThreadReturnListener newListener) {
        this.listener = newListener;
    }

    public OnNetworkThreadReturnListener getListener() {
        return this.listener;
    }

    public GetAllUsersThread(int lastNumber) {
        super();
        if(lastNumber <0) {
            lastNumber = 0;
        }
    }

    public GetAllUsersThread(int lastNumber, OnNetworkThreadReturnListener newListener) {
        super();
        if(lastNumber <0) {
            lastNumber = 0;
        }
        this.listener = newListener;
    }

    public GetAllUsersThread(OnNetworkThreadReturnListener newListener) {
        super();
        lastNumber = 0;
        this.listener = newListener;
    }

    @Override
    public void run() {
        super.run();
        Response response = APIClass.getAllUsers(lastNumber);
        if(listener != null) {
            if(response != null) {
                Log.e("aaaa","bbbbbb");
                listener.onNetworkThreadGetDataSuccessful(response);
            }else {
                int code = STATUS_CODE_NO_NETWORK;
                if(MyApplication.getInstance().isNetworkOn())
                    code = STATUS_CODE_REMOTE_SERVER_PROBLEM;
                listener.onNetworkThreadGetDataFailed(code);
            }
        }
    }

}
