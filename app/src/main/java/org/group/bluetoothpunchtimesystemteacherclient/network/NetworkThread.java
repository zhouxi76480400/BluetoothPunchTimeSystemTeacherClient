package org.group.bluetoothpunchtimesystemteacherclient.network;

import android.util.Log;

import okhttp3.Response;

public class NetworkThread extends Thread {

    public OnNetworkThreadReturnListener listener;

    public void setOnNetworkThreadReturnListener(OnNetworkThreadReturnListener newListener) {
        this.listener = newListener;
    }

    public OnNetworkThreadReturnListener getListener() {
        return this.listener;
    }

    public static final int STATUS_CODE_NO_NETWORK = -1000;

    public static final int STATUS_CODE_REMOTE_SERVER_PROBLEM = -2000;

    public interface OnNetworkThreadReturnListener {

        void onNetworkThreadGetDataSuccessful(Class clazz, Response data);

        void onNetworkThreadGetDataFailed(Class clazz, int statusCode);

    }

    @Override
    public void run() {

    }
    
}
