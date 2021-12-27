package com.haylion.android;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.mobstat.StatService;
import com.dianping.logan.Logan;
import com.dianping.logan.LoganConfig;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.MessagePushUtils;
import com.haylion.android.data.util.NetWorkUtils;
import com.haylion.android.data.util.StorageUtils;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.mvp.BaseConfig;
import com.haylion.android.mvp.base.BaseApplication;
import com.haylion.android.mvp.rx.ReloginEvent;
import com.haylion.android.mvp.util.BusUtils;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.user.account.LoginActivity;
import com.haylion.android.user.account.MineActivity;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshInitializer;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 此页面使用BuildConfig 慎用，可能会不同环境串了。
 */
public class MyApplication extends BaseApplication {

    private static final String TAG = "MyApplication";
    private static Context context;
    private static int orderIdRunning = -1; //正在执行的订单ID

    public static int getOrderIdRunning() {
        return orderIdRunning;
    }

    public static void setOrderIdRunning(int orderIdRunning) {
        MyApplication.orderIdRunning = orderIdRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        StatService.autoTrace(this);//初始化百度统计
        BaseConfig.init(this);

        closeAndroidPDialog();

        // 创建应用主目录
        StorageUtils.makeHomeDirectory();

        //日志组件初始化
        LogUtils.init(StorageUtils.getLogPath());

        //本地信息存储初始化
        PrefserHelper.initPrefser(context);

        //网络请求参数
        NetWorkUtils.initRetrofit(context);

        //接入腾讯bugly功能
        Bugly.init(this, BuildConfig.BUGLY_APP_ID, false);
        Beta.canShowUpgradeActs.add(MineActivity.class);

        BusUtils.register(this);

       /* registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

            @Override
            public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setCurrentActivity(activity);
            }
        });*/

        //集成语音功能
        TTSUtil.init(this);
        TTSUtil.createSpeechSynthesizer(this);

        //接入美团日志上报系统
        LoganConfig config = new LoganConfig.Builder()
                .setCachePath(getApplicationContext().getFilesDir().getAbsolutePath())
                .setPath(getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                        + File.separator + "logan_v1")
                .setEncryptKey16("0123456789012345".getBytes())
                .setEncryptIV16("0123456789012345".getBytes())
                .build();
        Logan.init(config);

        Log.d(TAG, "log path: " + getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + File.separator + "logan_v1");

        //消息推送
        MessagePushUtils.umengPushInit(this);
        initSmartRefresh();
    }


    private void initSmartRefresh() {
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setEnableOverScrollDrag(true);
                layout.setEnableHeaderTranslationContent(true);
            }
        });

        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsHeader(context);
            }
        });

        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new ClassicsFooter(context);
            }
        });
    }
    public static Context getContext() {
        return context;
    }

    @Subscribe
    public void onRelogin(ReloginEvent event) {
        Logan.w("re login:", 1);
        LogUtils.d(TAG, "onRelogin");
        LoginActivity.start(this, true);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        BusUtils.unregister(this);
    }

    /**
     * 解决androidP 第一次打开程序出现莫名弹窗
     * 弹窗内容“detected problems with api ”
     * 原因：Android P 后谷歌限制了开发者调用非官方公开API 方法或接口，用反射直接调用源码会有这样的提示弹窗出现。
     */
    private void closeAndroidPDialog() {
        if (Build.VERSION.SDK_INT < 28) return;
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
