package com.haylion.android.data.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.haylion.android.data.repo.PrefserHelper;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;


/**
 * 语音工具类，集成科大讯飞语音合成的方案
 * 免费的账号，有次数限制。
 * 参考http://mscdoc.xfyun.cn/android/api/
 * alec
 * 2018-10-31
 */
public class TTSUtil {
    private static final String TAG = "TTSUtil";
    private static SpeechSynthesizer speechSynthesizer;

    public static void init(Context context) {
//        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5b2239f6");
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=5d6f196c");

//        initParameter();
    }

    public static void createSpeechSynthesizer(Context context) {
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(context, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.d(TAG,"onInit " + i);
            }
        });
    }

    public static void destroySpeechSynthesizer() {
        if (speechSynthesizer == null) {
            return;
        }
        speechSynthesizer.stopSpeaking();
        speechSynthesizer.destroy();
    }

    private static void initParameter() {
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "100"); //合成时的音量，100为最大值
//        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "vixm"); //粤语
//        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "vixl"); //台湾普通话
//        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //普通话，科大讯飞默认的
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "vixy"); //普通话，科大讯飞默认的

    }

    public interface TTSUtilCallBack {
        void onSpeakBegin();

        void onCompleted();
    }

    /**
     * 听单播报(根据语音播报的开关，决定是否需要播报语音)
     * @param str
     */
    public static void play(String str) {
        Log.d(TAG, "voice content:" + str);

        String voiceStatus = PrefserHelper.getCache(PrefserHelper.KEY_ORDER_VOICE_ENABLE);
        if(!"disable".equals(voiceStatus)){
            play(str, null);
        }
    }

    /**
     * 地图导航语音（根据开关决定师傅播报）
     * @param str
     */
    public static void playAmapNavi(String str) {
        Log.d(TAG, "voice content:" + str);
        String voiceStatus = PrefserHelper.getCache(PrefserHelper.KEY_AMAP_NAVI_VOICE_ENABLE);
        Log.d(TAG, "voiceStatus:" + voiceStatus);
        if(!"disable".equals(voiceStatus)){
            play(str, null);
        }
    }

    /**
     * 不做限制的 播报
     * @param str
     */
    public static void playVoice(String str) {
        Log.d(TAG, "voice content:" + str);
        play(str, null);
    }

     /**
      * @method
      * @description 播报语音
      * @date: 2020/2/27 9:55
      * @author: tandongdong
      * @param
      * @return
      */
    private static void play(String str, final TTSUtilCallBack callback) {
        Log.d(TAG, "str: " + str);
        initParameter();
        speechSynthesizer.startSpeaking(str, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                Log.d(TAG,"onSpeakBegin");
                if (callback != null) {
                    callback.onSpeakBegin();
                }
            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

                Log.d(TAG,"onBufferProgress");
            }

            @Override
            public void onSpeakPaused() {

                Log.d(TAG,"onSpeakPaused");
            }

            @Override
            public void onSpeakResumed() {

                Log.d(TAG,"onSpeakResumed");
            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

                Log.d(TAG,"onSpeakProgress");
            }

            @Override
            public void onCompleted(SpeechError speechError) {
                Log.d(TAG,"onCompleted");

                if (callback != null) {
                    callback.onCompleted();
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

                Log.d(TAG,"onEvent");
            }
        });
    }

    public static void stop() {
        speechSynthesizer.stopSpeaking();
    }

}
