package com.haylion.android.data.model;


import java.util.List;

 /**
  * @class  AchievementList
  * @description 业绩列表
  * @date: 2019/12/17 10:19
  * @author: tandongdong
  */
public class AchievementList {

    private List<OrderAbstract> achievementList;
    public void setAchievementList(List<OrderAbstract> achievementList) {
        this.achievementList = achievementList;
    }
    public List<OrderAbstract> getAchievementList() {
        return achievementList;
    }

}