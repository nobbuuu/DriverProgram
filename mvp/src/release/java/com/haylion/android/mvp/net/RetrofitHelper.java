package com.haylion.android.mvp.net;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.haylion.android.mvp.BuildConfig;
import com.haylion.android.mvp.util.LogUtils;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.Logger;
import com.ihsanbal.logging.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public final class RetrofitHelper {
    private static final String TAG = "RetrofitHelper";

    private static Retrofit SINGLETON;

    private RetrofitHelper() {
    }

    public static void init(InitParam param) {
        Context context = param.context.getApplicationContext();

        Retrofit.Builder rfAPIBuilder = new Retrofit.Builder();
        rfAPIBuilder.baseUrl(param.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool())
                .retryOnConnectionFailure(true);

        okClientBuilder.addInterceptor(new ConnectivityInterceptor(context));
        if (param.interceptors != null) {
            for (Interceptor interceptor : param.interceptors) {
                okClientBuilder.addInterceptor(interceptor);
            }
        }

        okClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        okClientBuilder.addInterceptor(getLogInterceptor());

        rfAPIBuilder.client(okClientBuilder.build());

        SINGLETON = rfAPIBuilder.build();
    }

    private static LoggingInterceptor getLogInterceptor() {
        LoggingInterceptor.Builder builder = new LoggingInterceptor.Builder();
        builder.loggable(true).setLevel(Level.BASIC).log(Platform.INFO);
        builder.request("Request").response("Response");
        if (BuildConfig.DEBUG) {
            //保存到文件
            builder.logger(new Logger() {
                @Override
                public void log(int level, String tag, String msg) {
                    if (level == Log.ERROR) {
                        LogUtils.e("net", msg);
                    } else if (level == Log.WARN) {
                        LogUtils.w("net",  msg);
                    } else {
                        LogUtils.d("net", msg);
                    }
                }
            });
        } else {
            //default log
        }
        return builder.build();
    }


    public static class InitParam {

        private Context context;
        private String baseUrl;
        private Interceptor[] interceptors;

        public InitParam(Context context, String baseUrl) {
            this.context = context;
            this.baseUrl = baseUrl;
        }

        public void interceptor(Interceptor... interceptors) {
            this.interceptors = interceptors;
        }
    }


    public static <T> T getApi(Class<T> clazz) {
        if (SINGLETON == null) {
            throw new NullPointerException("Please invoke init() first");
        }
        return SINGLETON.create(clazz);
    }


}
