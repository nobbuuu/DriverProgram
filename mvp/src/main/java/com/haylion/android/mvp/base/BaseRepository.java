package com.haylion.android.mvp.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseRepository {

    private volatile CompositeDisposable mCompositeDisposable;

    public void onCreate() {

    }

    protected final void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            synchronized (this) {
                if (mCompositeDisposable == null) {
                    mCompositeDisposable = new CompositeDisposable();
                }
            }
        }
        mCompositeDisposable.add(disposable);
    }

    public void onDestroy() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }


}
