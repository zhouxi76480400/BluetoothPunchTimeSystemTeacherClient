package org.group.bluetoothpunchtimesystemteacherclient.network;

import com.google.gson.Gson;

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



        }
    }
}
