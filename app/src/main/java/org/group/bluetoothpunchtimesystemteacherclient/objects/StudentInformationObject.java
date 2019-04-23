package org.group.bluetoothpunchtimesystemteacherclient.objects;

import java.io.Serializable;

public class StudentInformationObject implements Serializable {

    public String mac_address;

    public String student_number;

    public String last_name;

    public String first_name;

    /**
     * maybe available from API
     */
    public long id;

}
