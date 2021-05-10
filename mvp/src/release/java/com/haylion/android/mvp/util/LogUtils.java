package com.haylion.android.mvp.util;


import android.util.Log;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.DefaultFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class LogUtils {

    private static final String TAG = "LogUtils";

    /**
     * 保留最近7天的日志
     */
    private static final long LOG_RETAIN_MILLS = TimeUnit.DAYS.toMillis(7);

    private LogUtils() {
        throw new UnsupportedOperationException();
    }


    /**
     * 初始化日志工具库
     *
     * @param logDir 日志目录
     * @param logLevel 日志打印级别
     */
    public static void init(String logDir, int logLevel) {
        LogConfiguration.Builder lcb = new LogConfiguration.Builder()
                .tag(TAG)
                .t()
                .st(2)
                .b();

        if(logLevel >= LogLevel.VERBOSE && logLevel <= LogLevel.ERROR){
            lcb.logLevel(logLevel);
        } else {
            lcb.logLevel(LogLevel.WARN);
        }

        makeLogDir(logDir);

        Printer filePrinter = new FilePrinter
                .Builder(logDir)
                .fileNameGenerator(new DateFileNameGenerator())
                .flattener(new LogFlattener())
                .backupStrategy(new NeverBackupStrategy())
                .cleanStrategy(new FileLastModifiedCleanStrategy(LOG_RETAIN_MILLS))
                .build();

        XLog.init(lcb.build(), filePrinter);

    }

    /**
     * 初始化日志工具库
     *
     * @param logDir 日志目录
     */
    public static void init(String logDir) {
        LogConfiguration.Builder lcb = new LogConfiguration.Builder()
                .tag(TAG)
                .t()
                .st(2)
                .b();
        lcb.logLevel(LogLevel.ALL);

        makeLogDir(logDir);

        Printer androidPrinter = new AndroidPrinter();             // 通过 android.util.Log 打印日志的打印器

        Printer filePrinter = new FilePrinter  //保存到文件
                .Builder(logDir)
                .fileNameGenerator(new DateFileNameGenerator())
                .flattener(new LogFlattener())
                .backupStrategy(new NeverBackupStrategy())
                .cleanStrategy(new FileLastModifiedCleanStrategy(LOG_RETAIN_MILLS))
                .build();

        XLog.init(lcb.build(), androidPrinter, filePrinter);
    }

    /**
     * 创建日志文件夹
     *
     * @param logDir 日志目录
     */
    private static void makeLogDir(String logDir) {
        File file = new File(logDir);
        if (!file.exists()) {
            if (file.mkdirs()) {
                Log.d(TAG, "日志文件夹创建成功");
            } else {
                Log.e(TAG, "日志文件夹创建失败");
            }
        } else {
            Log.d(TAG, "日志文件夹已存在");
        }
    }

    /**
     * 日志格式化
     */
    private static class LogFlattener extends DefaultFlattener {

        @Override
        public CharSequence flatten(long timeMillis, int logLevel, String tag, String message) {
            return String.format("%tc", timeMillis)
                    + '|' + LogLevel.getShortLevelName(logLevel)
                    + '|' + tag
                    + '\n' + message + '\n';
        }

    }

    public static void e(String msg) {
        XLog.e(msg);
    }

    public static void e(Throwable throwable) {
        XLog.e(throwable);
    }

    public static void w(String msg) {
        XLog.w(msg);
    }

    public static void i(String msg) {
//        XLog.i(msg);
    }

    public static void d(String msg) {
//        XLog.d(msg);
    }

    public static void v(String msg) {
//        XLog.v(msg);
    }

    public static void e(String tag, Throwable throwable) {
        XLog.tag(tag).e(throwable);
    }

    public static void e(String tag, String throwable) {
        XLog.tag(tag).e(throwable);
    }

    public static void w(String tag, String msg) {
        XLog.tag(tag).w(msg);
    }

    public static void i(String tag, String msg) {
//        XLog.tag(tag).i(msg);
    }

    public static void d(String tag, String msg) {
//        XLog.tag(tag).d(msg);
    }

    public static void v(String tag, String msg) {
//        XLog.tag(tag).v(msg);
    }


    public static void json(String json) {
        XLog.json(json);
    }

    public static void xml(String xml) {
        XLog.xml(xml);
    }


}

