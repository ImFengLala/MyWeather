package com.jluandroid.myweather.util;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Fengl on 2018/3/20.
 */

public class HttpUtil {

    private static final String TAG = "HttpUtil";

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        Log.d(TAG, "sendOkHttpRequest: 发送网络请求啦" + address);
        client.newCall(request).enqueue(callback);
    }
}
