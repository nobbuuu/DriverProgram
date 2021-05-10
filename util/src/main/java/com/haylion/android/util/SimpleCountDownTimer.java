package com.haylion.android.util;

import android.os.CountDownTimer;

public class SimpleCountDownTimer extends CountDownTimer {

    public SimpleCountDownTimer(long millisInFuture) {
        super(millisInFuture, millisInFuture);
    }

    public SimpleCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {

    }

    @Override
    public void onFinish() {

    }

}
