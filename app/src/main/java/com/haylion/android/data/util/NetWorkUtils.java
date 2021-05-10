package com.haylion.android.data.util;

import android.content.Context;
import android.text.TextUtils;

import com.haylion.android.BuildConfig;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.net.RetrofitHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetWorkUtils {
    public static void initRetrofit(Context context) {
        RetrofitHelper.InitParam userParam = new RetrofitHelper.InitParam(context, BuildConfig.API_URL);
        userParam.interceptor(new TokenInterceptor());
        RetrofitHelper.init(userParam);
    }

    private static class TokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();

            if (!TextUtils.isEmpty(PrefserHelper.getToken())) {
                builder.addHeader("token", PrefserHelper.getToken());
            }
            return chain.proceed(builder.build());
        }

    }
}
