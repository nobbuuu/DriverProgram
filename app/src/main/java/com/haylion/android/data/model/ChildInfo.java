package com.haylion.android.data.model;

 /**
  * @class  ChildInfo
  * @description 小孩信息
  * @date: 2019/12/17 10:21
  * @author: tandongdong
  */
public class ChildInfo {
    private String name;
    private String mobile;  //对于小孩来说，这个就是虚拟号
    private String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
