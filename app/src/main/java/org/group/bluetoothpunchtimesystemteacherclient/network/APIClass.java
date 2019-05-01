package org.group.bluetoothpunchtimesystemteacherclient.network;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIClass {

    private static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    /**
     * get All User
     * @param lastNumber
     * @return
     */
    public static Response getAllUsers(long lastNumber) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("last",String.valueOf(lastNumber))
                .build();
        Request request = new Request.Builder()
                .url(ServerAndApiList.getFullAPIAddress(ServerAndApiList.API_GET_ALL_USER))
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param isEdit
     * @param json
     * @return
     */
    public static Response addAUser(boolean isEdit,String json) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Map<String,String> kv = new HashMap<>();
        kv.put("e",String.valueOf(isEdit));
        kv.put("d",json);
        RequestBody requestBody = getRequestBody(kv);
        Request request = new Request.Builder()
                .post(requestBody)
                .url(ServerAndApiList.getFullAPIAddress(ServerAndApiList.API_ADD_A_USER))
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static RequestBody getRequestBody(Map<String,String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        int map_size = map.size();
        Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator();
        int now_size = 0;
        while (iterator.hasNext()) {
            now_size++;
            Map.Entry<String,String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                value = URLEncoder.encode(value,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            stringBuilder.append(String.format("%s=%s",key,value));
            if(now_size < map_size) {
                stringBuilder.append("&");
            }
        }
        return RequestBody.create(FORM_CONTENT_TYPE, stringBuilder.toString());
    }

}
