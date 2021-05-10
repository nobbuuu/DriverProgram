package com.haylion.android.data.model;

/**
 * @author dengzh
 * @date 2019/10/21
 * Description: 简单的选择Bean
 */
public class SimpleSelectBean {

    private String value;     //真正的值
    private String desc;      //描述，显示的值
    private boolean isSelect;  //选中状态

    public SimpleSelectBean(String value, String desc, boolean isSelect) {
        this.value = value;
        this.desc = desc;
        this.isSelect = isSelect;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
