package com.haylion.android.data.util;

import java.math.BigDecimal;

/**
 * @author dengzh
 * @date 2019/10/25
 * Description:
 */
public class NumberUtil {

    /**
     * 保留几位小数
     * @param res
     * @param newScale  几位小数
     * @return
     */
    public static double roundHalfUp(Double res,int newScale){
        return new BigDecimal(res).setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
