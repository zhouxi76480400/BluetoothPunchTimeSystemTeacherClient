package org.group.bluetoothpunchtimesystemteacherclient.network;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.objects.CreateSessionPOJO;

import okhttp3.Response;

public class CreateSessionThread extends NetworkThread {

    private CreateSessionPOJO createSessionPOJO;

    public CreateSessionThread(CreateSessionPOJO createSessionPOJO) {
        super();
        this.createSessionPOJO = createSessionPOJO;
    }

    @Override
    public void run() {
        Response response = APIClass.createNewSession(createSessionPOJO);
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
