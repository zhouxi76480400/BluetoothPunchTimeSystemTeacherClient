package org.group.bluetoothpunchtimesystemteacherclient.network;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;

import java.util.List;

import okhttp3.Response;

public class RemoveUserThread extends NetworkThread {

    private List<Integer> list;

    public RemoveUserThread(List<Integer> removeList) {
        super();
        this.list = removeList;
    }


    @Override
    public void run() {
        super.run();
        if(list != null) {
            Response response = APIClass.removeUsers(list);
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
}
