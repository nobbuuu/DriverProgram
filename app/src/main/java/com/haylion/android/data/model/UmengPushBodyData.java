package com.haylion.android.data.model;

 /**
  * @class  UmengPushBodyData
  * @description 友盟推送的body对象
  * @date: 2019/12/9 11:15
  * @author: tandongdong
  */
public class UmengPushBodyData {
    private String activity;
    private String custom;
    private String after_open;
    private String play_vibrate;
    private String sound;

     public String getActivity() {
         return activity;
     }

     public void setActivity(String activity) {
         this.activity = activity;
     }

     public String getCustom() {
         return custom;
     }

     public void setCustom(String custom) {
         this.custom = custom;
     }

     public String getAfter_open() {
         return after_open;
     }

     public void setAfter_open(String after_open) {
         this.after_open = after_open;
     }

     public String getPlay_vibrate() {
         return play_vibrate;
     }

     public void setPlay_vibrate(String play_vibrate) {
         this.play_vibrate = play_vibrate;
     }

     public String getSound() {
         return sound;
     }

     public void setSound(String sound) {
         this.sound = sound;
     }
 }
