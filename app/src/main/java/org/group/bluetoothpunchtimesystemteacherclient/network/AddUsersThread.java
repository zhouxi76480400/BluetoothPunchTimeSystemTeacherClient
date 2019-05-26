package org.group.bluetoothpunchtimesystemteacherclient.network;


import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;


import okhttp3.Response;

public class AddUsersThread extends NetworkThread {

    private boolean isEdit;

    private String json;

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
                listener.onNetworkThreadGetDataSuccessful(getClass(),response);
            }else {
                if(MyApplication.getInstance().isNetworkOn()) {
                    listener.onNetworkThreadGetDataFailed(getClass(),
                            NetworkThread.STATUS_CODE_NO_NETWORK);
                }else {
                    listener.onNetworkThreadGetDataFailed(getClass(),
                            NetworkThread.STATUS_CODE_REMOTE_SERVER_PROBLEM);
                }
            }
        }
    }
}
