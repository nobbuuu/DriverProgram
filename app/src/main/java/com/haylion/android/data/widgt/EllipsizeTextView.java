package com.haylion.android.data.widgt;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author dengzh
 * @date 2019/11/13
 * Description: 在指定位置省略字符
 * 参考：[Android TextView 在指定位置自动省略字符](https://juejin.im/post/5b2c6be3e51d4558bd51849d)
 */
public class EllipsizeTextView extends AppCompatTextView {

    public EllipsizeTextView(Context context) {
        super(context);
    }

    /**
     *
     * 指定 文字过长时，保留最后几位进行省略
     * 例如：originText 文字过长时，保留最后4位进行省略，那么就有  1234569...abcd
     * 局限性: 宽度不能设为wrap_content；lines必须设置为1。
     * @param originText
     * @param lastNum
     */
    public void setEllipsisText(String originText,int lastNum){
        //1.获取原文字在控件上占满的长度
        //获取原文字长度
        float originTextWidth = getPaint().measureText(originText);
        //获取控件长度
        float textViewWidth = getWidth();

        //2.判断控件是否可以装满文字
        if (textViewWidth >= originTextWidth) {
            setText(originText);
            return;
        }

        //3.切割，区分可省略的和必须保留的
        String prefixText = originText.substring(0, originText.length()-lastNum);
        String suffixText = "..." + originText.substring(originText.length()-lastNum);


        //5.不断递减指定位置前的字符串，以此来获取满足条件的前缀字符串。
        float prefixWidth = getPaint().measureText(prefixText);
        float suffixWidth = getPaint().measureText(suffixText);
        //后缀太长 不处理
        if (suffixWidth > textViewWidth){
            setText(originText);
        }else {
            //每减少前缀一个字符都去判断是否能塞满控件
            while (textViewWidth - prefixWidth < suffixWidth) {
                prefixText = prefixText.substring(0, prefixText.length() - 1);
                //关键
                prefixWidth = getPaint().measureText(prefixText);
            }
            //能塞满
            setText(prefixText + suffixText);
        }
    }
}
