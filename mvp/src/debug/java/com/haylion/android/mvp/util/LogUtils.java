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

/**
 * @class LogUtils
 * @description 日志打印工具类，可以将日志打印到文件，
 * @date: 2019/12/2 15:08
 * @author: tandongdong
 */
public class LogUtils {

    private static final String TAG = "LogUtils";

    /**
     * 保留最近7天的日志
     */
    private static final long LOG_RETAIN_MILLS = TimeUnit.DAYS.toMillis(7);

    private LogUtils() {
        throw new UnsupportedOperationException();
    }

/*    LogConfiguration config = new LogConfiguration.Builder()
            .tag("MY_TAG")                                         // 指定 TAG，默认为 "X-LOG"
            .t()                                                   // 允许打印线程信息，默认禁止
            .st(2)                                                 // 允许打印深度为2的调用栈信息，默认禁止
            .b()                                                   // 允许打印日志边框，默认禁止
            .jsonFormatter(new MyJsonFormatter())                  // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
            .xmlFormatter(new MyXmlFormatter())                    // 指定 XML 格式化器，默认为 DefaultXmlFormatter
            .throwableFormatter(new MyThrowableFormatter())        // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
            .threadFormatter(new MyThreadFormatter())              // 指定线程信息格式化器，默认为 DefaultThreadFormatter
            .stackTraceFormatter(new MyStackTraceFormatter())      // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
            .borderFormatter(new MyBoardFormatter())               // 指定边框格式化器，默认为 DefaultBorderFormatter
            .addObjectFormatter(AnyClass.class,                    // 为指定类添加格式化器
                    new AnyClassObjectFormatter())                 // 默认使用 Object.toString()
            .build();

    Printer androidPrinter = new AndroidPrinter();             // 通过 android.util.Log 打印日志的打印器
    Printer SystemPrinter = new SystemPrinter();               // 通过 System.out.println 打印日志的打印器
    Printer filePrinter = new FilePrinter                      // 打印日志到文件的打印器
            .Builder("/sdcard/xlog/")                              // 指定保存日志文件的路径
            .fileNameGenerator(new DateFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
            .backupStrategy(new MyBackupStrategy())                // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
            .logFlattener(new MyLogFlattener())                    // 指定日志平铺器，默认为 DefaultLogFlattener
            .build();

XLog.init(LogLevel.ALL,                                    // 指定日志级别，低于该级别的日志将不会被打印
    config,                                                // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
    androidPrinter,                                        // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter
    systemPrinter,
    filePrinter);    */

/*    Printer filePrinter = new FilePrinter                      // 打印日志到文件的打印器
            .Builder("/sdcard/haylion/taxi/xlog/")                              // 指定保存日志文件的路径
            .fileNameGenerator(new DateFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
            .backupStrategy(new MyBackupStrategy())                // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
            .logFlattener(new MyLogFlattener())                    // 指定日志平铺器，默认为 DefaultLogFlattener
            .build();*/


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
                .nt()
                .nst()
                .nb();
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
                    + ' ' + LogLevel.getShortLevelName(logLevel)
                    + ' ' + tag
                    + " , " + message + ' ';
        }

    }

    public static void e(String msg) {
        XLog.e(msg);
    }

    public static void e(Throwable throwable) {
        XLog.e(throwable);
    }

    public static void e(String tag, Throwable throwable) {
        XLog.tag(tag).e(throwable);
    }

    public static void e(String tag, String msg) {
        XLog.tag(tag).e(msg);
    }

    public static void w(String msg) {
        XLog.w(msg);
    }

    public static void w(String tag, String msg) {
        XLog.tag(tag).w(msg);
    }

    public static void i(String msg) {
        XLog.i(msg);
    }

    public static void i(String tag, String msg) {
        XLog.tag(tag).i(msg);
    }

    public static void d(String msg) {
        XLog.d(msg);
    }

    public static void d(String tag, String msg) {
        XLog.tag(tag).d(msg);
    }

    public static void v(String msg) {
        XLog.v(msg);
    }

    public static void v(String tag, String msg) {
        XLog.tag(tag).v(msg);
    }

    public static void json(String json) {
        XLog.json(json);
    }

    public static void xml(String xml) {
        XLog.xml(xml);
    }


}
