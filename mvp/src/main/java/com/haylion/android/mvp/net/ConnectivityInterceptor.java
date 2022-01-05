package com.haylion.android.mvp.net;

import android.content.Context;
import android.util.Log;


import com.haylion.android.mvp.BuildConfig;
import com.haylion.android.mvp.exception.NoConnectivityException;
import com.haylion.android.util.NetworkUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import androidx.annotation.NonNull;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
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
            Request request = chain.request();
            //测试环境登录接口需要切端口
            HttpUrl url = request.url();
            if (BuildConfig.BUILD_TYPE.equals("debug") && url.toString().contains("account")) {
                Request.Builder newBuilder = request.newBuilder();
                HttpUrl.Builder builder = url.newBuilder();
                builder.port(6660);
                return chain.proceed(newBuilder.url(builder.build()).build());
            }
            return chain.proceed(request);
        } else {
            throw new NoConnectivityException();
        }
    }

    /**
     * log拦截器
     */
    public HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
        Log.d("OKHttp-->", message);
    });

}
