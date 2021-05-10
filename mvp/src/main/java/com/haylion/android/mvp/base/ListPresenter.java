package com.haylion.android.mvp.base;

public abstract class ListPresenter<V extends ListContract.View, R extends BaseRepository>
        extends BasePresenter<V, R> {

    public ListPresenter(V view) {
        super(view);
    }


}
