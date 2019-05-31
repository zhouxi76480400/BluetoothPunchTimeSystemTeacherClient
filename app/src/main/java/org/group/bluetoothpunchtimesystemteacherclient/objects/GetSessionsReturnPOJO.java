package org.group.bluetoothpunchtimesystemteacherclient.objects;

import java.io.Serializable;
import java.util.List;

public class GetSessionsReturnPOJO implements Serializable {

    public long create_time;

    public List<StudentInformationObject> data;

}
