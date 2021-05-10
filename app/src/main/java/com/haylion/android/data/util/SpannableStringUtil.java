package com.haylion.android.data.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import androidx.core.content.ContextCompat;

import com.haylion.android.Constants;
import com.haylion.android.MyApplication;
import com.haylion.android.R;

/**
 * @author dengzh
 * @date 2019/10/24
 * Description:
 */
public class SpannableStringUtil {

    /**
     * @param str    字符串
     * @param color  颜色
     * @param start  开始位置
     * @param end    结束位置
     * @return
     */
/*    public static SpannableString setSpan(String str,int color,int start, int end){
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new ForegroundColorSpan(color),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }*/

    /**
     * 设置一段字体变色
     * @param str    字符串
     * @param colorId  颜色resId
     * @param start  开始位置
     * @param end    结束位置
     * @return
     */
    public static SpannableString setSpan(String str, int colorId, int start, int end){
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(MyApplication.getContext(),colorId)),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 订单详情 地址信息
     * @param str  文字
     * @param startIndex  起始位置
     * @param endIndex    结束位置
     * @return
     */
    public static SpannableString getAddrSpanString(String str, int startIndex, int endIndex){
        return getSpanString(str,startIndex,endIndex,R.color.maas_text_primary,Typeface.BOLD,1.15f);
    }

    /**
     * 导航页 地址信息
     * @param str  文字
     * @param startIndex  起始位置
     * @param endIndex    结束位置
     * @return
     */
    public static SpannableString getMapNaviAddrSpanString(String str, int startIndex, int endIndex){
        return getSpanString(str,startIndex,endIndex,R.color.maas_text_primary,Typeface.BOLD,1.125f);
    }

    /**
     * 设置一段文字的样式
     * @param str         文字
     * @param startIndex  起始位置
     * @param endIndex    结束位置
     * @param colorId     颜色id    R.color.maas_text_primary
     * @param style       样式     Typeface.BOLD
     * @param proportion  字体大小   如16sp -> 18sp,就是1.125
     * @return
     */
    public static SpannableString getSpanString(String str, int startIndex, int endIndex,int colorId,int style,float proportion){
        SpannableString spannableString = new SpannableString(str);
        //颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(MyApplication.getContext(), colorId));
        spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //加粗
        StyleSpan styleSpan = new StyleSpan(style);
        spannableString.setSpan(styleSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //字体大小，在TextView原有的文字大小的基础上，相对设置文字大小
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(proportion);
        spannableString.setSpan(sizeSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //改变字体
        TypefaceSpan typefaceSpan = new TypefaceSpan(Constants.TEXT_BOLD_TYPEFACE);
        spannableString.setSpan(typefaceSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return spannableString;
    }


}
