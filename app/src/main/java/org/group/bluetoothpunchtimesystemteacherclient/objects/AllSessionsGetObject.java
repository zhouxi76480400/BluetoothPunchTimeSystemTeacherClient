package org.group.bluetoothpunchtimesystemteacherclient.objects;

import java.io.Serializable;
import java.util.List;

public class AllSessionsGetObject implements Serializable {

    public String s;

    public boolean end_page;

    public List<CreateSessionPOJO> data;

}
