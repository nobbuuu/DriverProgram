package com.haylion.android.data.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.haylion.android.BuildConfig;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.net.RetrofitHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
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
            //获取request
            Request request = chain.request();
            //从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldHttpUrl = request.url();
            String requestUrl = oldHttpUrl.toString();
            //匹配获得新的BaseUrl
            int port = 6663;
            if (requestUrl.contains("account")) {
                port = 6660;
            }
            //重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme("http")//更换网络协议
                    .host(oldHttpUrl.host())//更换主机名
//                    .port(port)//更换端口
//                    .removePathSegment(0)//移除第一个参数
                    .build();
            // 然后返回一个response至此结束修改
            Log.e("hostUrl", "hostUrl = " + newFullUrl.toString());
            return chain.proceed(builder.url(newFullUrl).build());
        }
    }

    public static RequestBody getRequestBody(File mFile) {
        if (mFile != null) {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);//ParamKey.TOKEN 自定义参数key常量类，即参数名
            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile);
            builder.addFormDataPart("file", mFile.getName(), imageBody);//element 后台接收图片流的参数名
            RequestBody part = builder.build();
            return part;
        } else {
            return null;
        }
    }
}
