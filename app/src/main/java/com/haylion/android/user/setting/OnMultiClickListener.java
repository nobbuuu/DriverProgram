package com.haylion.android.user.setting;

import android.os.SystemClock;
import android.view.View;

import java.util.concurrent.TimeUnit;

public abstract class OnMultiClickListener implements View.OnClickListener {

    private final long DURATION;
    private final long[] mHits;

    public OnMultiClickListener(int counts) {
        this(counts, TimeUnit.SECONDS.toMillis(3)); // 默认3秒内
    }

    public OnMultiClickListener(int counts, long duration) {
        this.mHits = new long[counts];
        this.DURATION = duration;
    }

    @Override
    public final void onClick(View v) {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            onMultiClick(v);
        }
    }

    public abstract void onMultiClick(View view);


}
