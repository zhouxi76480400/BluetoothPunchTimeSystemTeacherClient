package org.group.bluetoothpunchtimesystemteacherclient.network;

import android.util.Log;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import okhttp3.Response;

public class GetAllUsersThread extends NetworkThread {

    private long lastNumber;

    public GetAllUsersThread(int lastNumber) {
        super();
        if(lastNumber <0) {
            this.lastNumber = 0;
        }
    }

    public GetAllUsersThread(int lastNumber, OnNetworkThreadReturnListener newListener) {
        super();
        Log.e("test","lastNumber:"+lastNumber);
        if(lastNumber < 0) {
            this.lastNumber = 0;
        }else {
            this.lastNumber = lastNumber;
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
                listener.onNetworkThreadGetDataSuccessful(getClass(),response);
            }else {
                int code = STATUS_CODE_NO_NETWORK;
                if(MyApplication.getInstance().isNetworkOn())
                    code = STATUS_CODE_REMOTE_SERVER_PROBLEM;
                listener.onNetworkThreadGetDataFailed(getClass(),code);
            }
        }
    }

}
