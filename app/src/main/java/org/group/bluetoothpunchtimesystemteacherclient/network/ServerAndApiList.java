package org.group.bluetoothpunchtimesystemteacherclient.network;

import java.io.Serializable;

public class ServerAndApiList implements Serializable {

    /**
     * WebServer Address
     */
    public static final String SERVER_ADDRESS = "http://192.168.1.43:8080/s/";
//    public static final String SERVER_ADDRESS = "http://172.19.1.20:8080/s/";
//    public static final String SERVER_ADDRESS = "http://192.168.50.44:8080/s/";

    /**
     * Get All Users
     */
    public static final String API_GET_ALL_USER = "getusr";

    /**
     * Add A User
     */
    public static final String API_ADD_A_USER = "addusr";

    /**
     * Remove Users
     */
    public static final String API_REMOVE_USERS = "removeusrs";

    /**
     * create new session
     */
    public static final String API_CREATE_NEW_SESSION = "cs";

    /**
     * Use This Method To Get Api Address
     * @param apiName
     * @return
     */
    public static final String getFullAPIAddress(String apiName) {
        return String.format("%s%s",SERVER_ADDRESS,apiName);
    }





}
