package org.group.bluetoothpunchtimesystemteacherclient;

import android.app.Application;
import android.content.Intent;

import org.group.bluetoothpunchtimesystemteacherclient.activities.ExitActivity;

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

}