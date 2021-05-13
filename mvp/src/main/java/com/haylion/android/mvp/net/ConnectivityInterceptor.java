package com.haylion.android.mvp.net;

import android.content.Context;
import android.util.Log;


import com.haylion.android.mvp.exception.NoConnectivityException;
import com.haylion.android.util.NetworkUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class ConnectivityInterceptor implements Interceptor {

    private Context context;

    public ConnectivityInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (NetworkUtils.isConnected(context)) {

            return chain.proceed(chain.request());
        } else {
            throw new NoConnectivityException();
        }
    }

    /**
     * log拦截器
     */
    public HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {

        try {
            String text = URLDecoder.decode(message, "utf-8");
            Log.e("OKHttp-->", text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("OKHttp-->", message);
        }
    });

}
