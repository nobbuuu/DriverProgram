package com.haylion.android.mvp.rx;

import com.haylion.android.mvp.base.BaseResponse;
import com.haylion.android.mvp.exception.ApiException;
import com.haylion.android.mvp.exception.NullDataException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

public class ApiTransformer<T> implements ObservableTransformer<BaseResponse<T>, T> {

    @Override
    public Observable<T> apply(Observable<BaseResponse<T>> upstream) {
        return upstream.compose(new ApiScheduler<>()).flatMap((Function<BaseResponse<T>, ObservableSource<T>>) r -> {
            if (r.getCode() == 200) {
                if (r.getData() == null) {
                    return Observable.error(new NullDataException());
                }
                return Observable.just(r.getData());
            } else {
                return Observable.error(new ApiException(r.getCode(), r.getMsg()));
            }
        });
    }

}