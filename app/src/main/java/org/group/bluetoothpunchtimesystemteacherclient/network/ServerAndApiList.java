package org.group.bluetoothpunchtimesystemteacherclient.network;

import java.io.Serializable;

public class ServerAndApiList implements Serializable {

    /**
     * WebServer Address
     */
    public static final String SERVER_ADDRESS = "http://192.168.50.61:8080/s/";

    /**
     * Get All Users
     */
    public static final String API_GET_ALL_USER = "getusr";

    /**
     * Add A User
     */
    public static final String API_ADD_A_USER = "addusr";

    /**
     * Use This Method To Get Api Address
     * @param apiName
     * @return
     */
    public static final String getFullAPIAddress(String apiName) {
        return String.format("%s%s",SERVER_ADDRESS,apiName);
    }





}
