package org.group.bluetoothpunchtimesystemteacherclient.network;

import android.util.Log;

import com.google.gson.Gson;

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
        Log.e("test","aaaaa");
    }
}
