package com.haylion.android;

import com.haylion.android.data.util.DateUtils;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        //assertEquals(4, 2 + 2);
/*
        System.out.println(BusinessUtils.moneySpec(12d));
        System.out.println(BusinessUtils.moneySpec(12.0d));
        System.out.println(BusinessUtils.moneySpec(12.01d));
        System.out.println(BusinessUtils.moneySpec(12.255d));
        System.out.println(BusinessUtils.moneySpec(0.0d));
        System.out.println(BusinessUtils.moneySpec(123456789.55d));
        System.out.println(BusinessUtils.moneySpec(0.10d));*/

        long mWaitingTimeMillis = 0L;
        for (int i =0;i<60;i++){
            try {
                Thread.sleep(1000);
                mWaitingTimeMillis = mWaitingTimeMillis + 1000;
                System.out.println(DateUtils.getHHmmssOrmmssForLong(mWaitingTimeMillis));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}