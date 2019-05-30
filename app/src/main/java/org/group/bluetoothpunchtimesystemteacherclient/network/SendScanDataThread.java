package org.group.bluetoothpunchtimesystemteacherclient.network;

import org.group.bluetoothpunchtimesystemteacherclient.objects.UpdateSessionDataPOJO;

import java.util.List;

public class SendScanDataThread extends NetworkThread {

    private int sid;
    private long time;
    private List<String> mac;

    public SendScanDataThread(int sid, long time, List<String> mac) {
        super();
        SendScanDataThread.this.sid = sid;
        SendScanDataThread.this.time = time;
        SendScanDataThread.this.mac = mac;
    }

    @Override
    public void run() {
        UpdateSessionDataPOJO updateSessionDataPOJO = new UpdateSessionDataPOJO();
        updateSessionDataPOJO.mac = mac;
        updateSessionDataPOJO.sid = sid;
        updateSessionDataPOJO.time = time;
        APIClass.updateSessionData(updateSessionDataPOJO);
    }

}
