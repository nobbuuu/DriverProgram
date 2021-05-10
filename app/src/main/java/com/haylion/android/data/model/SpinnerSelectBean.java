package com.haylion.android.data.model;

/**
 * @author dengzh
 * @date 2019/10/21
 * Description:
 */
public class SpinnerSelectBean {

    private String value;
    private boolean isSelect;

    public SpinnerSelectBean(String value, boolean isSelect) {
        this.value = value;
        this.isSelect = isSelect;
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
