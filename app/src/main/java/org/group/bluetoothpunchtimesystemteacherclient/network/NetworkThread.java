package org.group.bluetoothpunchtimesystemteacherclient.network;

import android.util.Log;

import okhttp3.Response;

public class NetworkThread extends Thread {

    public static final int STATUS_CODE_NO_NETWORK = -1000;

    public static final int STATUS_CODE_REMOTE_SERVER_PROBLEM = -2000;

    public interface OnNetworkThreadReturnListener {

        void onNetworkThreadGetDataSuccessful(Response data);

        void onNetworkThreadGetDataFailed(int statusCode);

    }

    @Override
    public void run() {

    }
    
}
