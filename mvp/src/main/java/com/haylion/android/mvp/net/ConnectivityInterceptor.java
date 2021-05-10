package com.haylion.android.mvp.net;

import android.content.Context;


import com.haylion.android.mvp.exception.NoConnectivityException;
import com.haylion.android.util.NetworkUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Response;

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

}
