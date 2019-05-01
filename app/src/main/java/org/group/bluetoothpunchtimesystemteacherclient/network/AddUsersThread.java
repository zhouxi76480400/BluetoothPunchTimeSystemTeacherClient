package org.group.bluetoothpunchtimesystemteacherclient.network;

import android.util.Log;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;

import java.io.IOException;

import okhttp3.Response;

public class AddUsersThread extends NetworkThread {

    private boolean isEdit;

    private String json;

    private OnNetworkThreadReturnListener listener;

    public void setOnNetworkThreadReturnListener(OnNetworkThreadReturnListener newListener) {
        this.listener = newListener;
    }

    public AddUsersThread(boolean isEdit,String json) {
        super();
        this.isEdit = isEdit;
        this.json = json;
    }

    @Override
    public void run() {
        Response response = APIClass.addAUser(isEdit,json);
        if(listener != null) {
            if (response != null) {
                listener.onNetworkThreadGetDataSuccessful(response);
            }else {
                if(MyApplication.getInstance().isNetworkOn()) {
                    listener.onNetworkThreadGetDataFailed(NetworkThread.STATUS_CODE_NO_NETWORK);
                }else {
                    listener.onNetworkThreadGetDataFailed(
                            NetworkThread.STATUS_CODE_REMOTE_SERVER_PROBLEM);
                }
            }
        }
    }
}
