package org.cubewhy.lunarcn.utils;

import okhttp3.Call;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

public class HttpUtils {
    public static final OkHttpClient okHttpClient = new OkHttpClient();
    private HttpUtils() {

    }

    public static Call get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return okHttpClient.newCall(request);
    }
}
