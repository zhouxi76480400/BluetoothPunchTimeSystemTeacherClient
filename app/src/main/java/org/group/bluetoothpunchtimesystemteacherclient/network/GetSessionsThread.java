package org.group.bluetoothpunchtimesystemteacherclient.network;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;

import okhttp3.Response;

public class GetSessionsThread extends NetworkThread {

    private int last_sid;

    public GetSessionsThread(int last_sid) {
        super();
        this.last_sid = last_sid;
    }

    @Override
    public void run() {
        Response response = APIClass.getSessions(last_sid);
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
