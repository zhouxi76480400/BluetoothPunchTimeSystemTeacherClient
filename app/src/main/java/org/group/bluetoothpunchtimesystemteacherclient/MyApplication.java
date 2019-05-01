package org.group.bluetoothpunchtimesystemteacherclient;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.telecom.ConnectionService;

import org.group.bluetoothpunchtimesystemteacherclient.activities.ExitActivity;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static MyApplication getInstance() {
        return myApplication;
    }

    public void exit() {
        try{
            Intent intent = new Intent(MyApplication.getInstance(), ExitActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getInstance().startActivity(intent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<StudentInformationObject> list;

    /**
     * Get Data From Single Instance
     * @return
     */
    public List<StudentInformationObject> getStudentInformations() {
        if(list == null)
            list = new ArrayList<>();
        return list;
    }

    public boolean isNetworkOn() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if(network == null)
            return false;
        return true;
    }
}