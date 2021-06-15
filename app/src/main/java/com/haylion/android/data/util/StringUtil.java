package com.haylion.android.data.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.haylion.android.MyApplication;

public class StringUtil {

    private StringUtil() {
    }

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }
        if (phoneNumber.length() == 11) {
            return phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return phoneNumber;
    }

    /**
     * 修改部分颜色
     *
     * @param first
     * @param content 给content这个内容设置颜色color,且字体大小设置为spValue
     * @param end
     * @param color
     * @return
     */
    public static SpannableString setTextPartSizeColor(String first, String content, String end, int color) {
        SpannableString spannableSale = new SpannableString(first + content + end);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(MyApplication.getContext().getResources().getColor(color));//设置颜色样式
        spannableSale.setSpan(colorSpan, first.length(), first.length() + content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableSale;
    }

}
