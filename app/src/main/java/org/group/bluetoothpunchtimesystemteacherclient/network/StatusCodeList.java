package org.group.bluetoothpunchtimesystemteacherclient.network;

import java.io.Serializable;

public class StatusCodeList implements Serializable {

    public static final int STATUS_CODE_OK = 0;

    public static final int STATUS_CODE_PARAMETER_NOT_EQUALS = 1;

    public static final int STATUS_CODE_JSON_CONVERT_FAILED = 2;

    public static final int STATUS_CODE_JSON_PARAMETER_NOT_EQUALS = 3;

    public static final int STATUS_CODE_USER_DATA_NOT_WRITE_SUCCESS = 4;

    public static final int STATUS_CODE_MAC_ADDRESS_EXIST = 5;

    public static final int STATUS_CODE_STUDENT_NUMBER_EXIST = 6;

}
