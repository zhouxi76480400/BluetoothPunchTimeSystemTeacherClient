package org.group.bluetoothpunchtimesystemteacherclient.network;

import android.util.Log;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;

import okhttp3.Response;

public class GetSessionInformationThread extends NetworkThread {

    private int sid;

    public GetSessionInformationThread(int sid) {
        super();
        this.sid = sid;
    }

    @Override
    public void run() {
        Response response = APIClass.getSessionInfo(sid);
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
