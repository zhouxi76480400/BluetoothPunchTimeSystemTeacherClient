package org.group.bluetoothpunchtimesystemteacherclient.timer;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.objects.CreateSessionPOJO;

import java.util.ArrayList;
import java.util.List;

public class ScanThread extends Thread {

    private static Application getApplication() {
        return MyApplication.getInstance();
    }

    public static void stopThread() {
        isRunning = false;
        scanThread = null;
    }

    public static void startThread(CreateSessionPOJO createSessionPOJO) {
        if(!isRunning) {
            if(scanThread == null) {
                scanThread = new ScanThread(createSessionPOJO);
            }
            scanThread.start();
        }
    }

    public static String ACTION_SCAN = "YOU_MUST_TO_CALL_SCAN_BLUETOOTH";

    public static String ACTION_SCAN_KEY_TIME = "time";

    private void sendScanBroadcast(int time) {
        Intent intent = new Intent();
        intent.setAction(ACTION_SCAN);
        intent.putExtra(ACTION_SCAN_KEY_TIME,time);
        getApplication().sendBroadcast(intent);
    }

    public static String ACTION_NOW = "NOW_NOW_NOW";

    public static String ACTION_NOW_KEY_REMAINING_TIME = "r";

    public static String ACTION_NOW_KEY_SCAN_INTERVAL = "s";

    public static String ACTION_NOW_KEY_SCAN_NEXT_TIME = "n";

    private void sendNowTimeBroadcast(int remaining_time,int next_scan_interval,int next_scan_time) {
        Intent intent = new Intent();
        intent.setAction(ACTION_NOW);
        intent.putExtra(ACTION_NOW_KEY_REMAINING_TIME,remaining_time);
        intent.putExtra(ACTION_NOW_KEY_SCAN_INTERVAL,next_scan_interval);
        intent.putExtra(ACTION_NOW_KEY_SCAN_NEXT_TIME,next_scan_time);
        getApplication().sendBroadcast(intent);
    }

    public static String ACTION_FINISHED = "SCAN_FINISHED";

    private void sendFinishBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_FINISHED);
        getApplication().sendBroadcast(intent);
    }

    private static ScanThread scanThread;

    private CreateSessionPOJO createSessionPOJO;

    private static boolean isRunning;

    private List<Integer> scanTimesTable;

    public ScanThread(CreateSessionPOJO createSessionPOJO) {
        super();
        ScanThread.this.createSessionPOJO = createSessionPOJO;
    }

    @Override
    public void run() {
        isRunning = true;
        int next_scan_times = 1;
        int now_time = 0;// sec.
        measureScanTimes();

        long last_time = 0;
        while (isRunning) {
            long nowTime = System.currentTimeMillis();
            if(nowTime - last_time > 1000) {
                now_time++;
//                Log.e("test","已經過了："+now_time+"秒");
                int remainingTimeSeconds =
                        getRemainingTimeSeconds(now_time,createSessionPOJO.time * 60);
//                Log.e("test","還剩："+remainingTimeSeconds+"秒");
                int next_scan_time_seconds_to_now_interval_seconds = -1;
                if(next_scan_times != -1) {
//                    Log.e("test","下一次掃描是第"+next_scan_times+"次");
                    int next_scan_time_seconds = scanTimesTable.get(next_scan_times-1);
//                    Log.e("test","下一次掃描的時間是:"+next_scan_time_seconds);
                    next_scan_time_seconds_to_now_interval_seconds =
                            next_scan_time_seconds - now_time;
//                    Log.e("test","下一次掃描距離現在時間秒:"+next_scan_time_seconds_to_now_interval_seconds);
                    if(next_scan_time_seconds_to_now_interval_seconds == 0) {
                        if(next_scan_times < scanTimesTable.size()) {
//                            Log.e("test","開始第"+(next_scan_times)+"次掃描");
                            sendScanBroadcast(next_scan_times);
                            next_scan_times++;
                        }else {
                            sendScanBroadcast(next_scan_times);
                            next_scan_times = -1;
//                            Log.e("test","沒有下一次掃描了");
                        }
                    }
                }
                sendNowTimeBroadcast(
                        remainingTimeSeconds,next_scan_time_seconds_to_now_interval_seconds,next_scan_times);
                if(now_time >= createSessionPOJO.time * 60) {
                    isRunning = false;
                    sendFinishBroadcast();
//                    Log.e("test","完成任務");
                }
                last_time = nowTime;
            }
        }
        scanThread = null;
    }

    private int getRemainingTimeSeconds(int pastTimeSec,int allTimeSec) {
        return allTimeSec - pastTimeSec;
    }

    private void measureScanTimes() {
        if(scanTimesTable == null) {
            scanTimesTable = new ArrayList<>();
        }
        scanTimesTable.clear();

        int first = 10;
        int last = createSessionPOJO.time * 60 - 20;
        int remainTimes = createSessionPOJO.frequency - 2;
        int interval = (last - first) / (1 + remainTimes);
//        Log.e("test","last:"+last);
//        Log.e("test","remainTimes:"+remainTimes);
//        Log.e("test","interval:"+interval);
        for (int i = 0 ; i< createSessionPOJO.frequency ; i ++) {
            if(i == 0) {
                scanTimesTable.add(first);
            }else if(i == createSessionPOJO.frequency - 1) {
                scanTimesTable.add(last);
            }else {
                int tmp = first+interval * i;
                scanTimesTable.add(tmp);
            }
        }
        Log.e("test","scan_time"+scanTimesTable.toString());
    }
}
