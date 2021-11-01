package com.haylion.android;

public final class Constants {

    public static final String APP_NAME = "Haylion/MaasTaxiDriver";
    public static final String LOG_DIR = "log";
    public static final String DOWNLOAD_DIR = "download";
    public static final String LATEST_NAME = "app-latest.apk";


    public static final class Gps {
        //深圳市 114.06098127； 纬度：22.54155423
        public static final String SHENZHEN = "深圳市";
        public static final double SHENZHEN_LAT = 22.54155423;
        public static final double SHENZHEN_LNG = 114.06098127;

        //南方科技大学图书馆 113.999871,22.602506
        public static final String NANKEDA = "南方科技大学";
        public static final double NANKEDA_LAT = 22.602506;
        public static final double NANKEDA_LNG = 113.999871;

        //益田地铁站 114.050896,22.516112
        public static final String YITIAN = "益田地铁站";
        public static final double YITIAN_LAT = 22.516112;
        public static final double YITIAN_LNG = 114.050896;

        //深圳湾公园 113.993132,22.521682
        public static final String SHENZHENWAN = "深圳湾公园地铁站";
        public static final double SHENZHENWAN_LAT = 22.521682;
        public static final double SHENZHENWAN_LNG = 113.993132;

        public static final double DEFAULT_LAT = SHENZHEN_LAT;
        public static final double DEFAULT_LNG = SHENZHEN_LNG;

    }

    public static final String URL = "http://192.168.100.34:30378/";

    public static final String URL_TEST = "https://mt-test.haylion.cn/";  //测试环境

    public static String SERVICE_PHONE = "";

    /**
     * 文字粗体时的字体
     */
    public static final String TEXT_BOLD_TYPEFACE = "sans-serif-condensed-light";

    /**
     * EventBean 传递消息的Code
     */
    public static class EventCode {
        public final static int UPLOAD_CHILD_ON_PHOTO_SUCCESS = 100; //孩子上车照片 - 上传成功
        public final static int UPLOAD_CHILD_OFF_PHOTO_SUCCESS = 101; //孩子下车照片 - 上传成功

        public final static int VOICE_PROMPT_WAITING_PASSENGER_GET_ON = 110; //语音播报 - 到达上车点，等待乘客上车
        public final static int VOICE_PROMPT_PASSENGER_GET_ON = 111; //语音播报 - 乘客已上车
        public final static int VOICE_PROMPT_ARRIVE_AT_DESTINATION = 112; //语音播报 - 到达目的地

        //小孩单
        public final static int VOICE_PROMPT_WAITING_DRIVER_TAKE_PHOTO_FOR_GET_ON = 113; //语音播报 - 您已到达上车地点，请等待乘客上车并拍摄照片
        public final static int VOICE_PROMPT_GO_TO_DESTINATION = 114; //语音播报 - 家长已确认上车照片，请安全驾驶并前往目的地
        public final static int VOICE_PROMPT_WATTING_PASSENGER_CONFIRM_GET_ON_PHOTO = 115; //语音播报 - 您已完成拍照，请等待家长确认上车照片
        public final static int VOICE_PROMPT_WATTING_PASSENGER_CONFIRM_GET_OFF_PHOTO = 116; //语音播报 - 您已完成拍照，请等待家长确认下车照片


    }

    public static class ErrorCode {

        public final static int VEHICLE_NOT_FOUND = 400110; // 车辆未找到
        public final static int VEHICLE_ALREADY_ATTACHED = 400315; //已添加该车辆
        public final static int VEHICLE_IS_DISABLED = 400112; // 车辆已停用

        public final static int PHONE_NUMBER_EXISTS = 400320; // 已有其他司机使用该手机号
        public final static int PHONE_NUMBER_NO_CHANGE = 400319; // 新手机号和自己已有的手机号相同

        public final static int ACCESSIBILITY_SERVICE_IS_DISABLED = 400324; // 未开通女性专车订单服务

    }


}
