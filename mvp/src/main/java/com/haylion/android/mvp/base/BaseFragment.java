package com.haylion.android.mvp.base;

import android.os.Bundle;
import android.view.View;


import com.haylion.android.mvp.R;
import com.haylion.android.mvp.util.BusUtils;
import com.haylion.android.mvp.widget.TitleBar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<P extends AbstractPresenter> extends Fragment
        implements AbstractView, TitleBar.OnClickListener {

    private Unbinder unbinder;

    protected P presenter;
    private List<? extends AbstractPresenter> mMorePresenters;

    protected TitleBar mTitleBar;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        mTitleBar = view.findViewById(R.id.title_bar);
        if (mTitleBar != null) {
            mTitleBar.setOnClickListener(this);
        }
        didCreate();
        presenter = onCreatePresenter();
        mMorePresenters = addMorePresenter();
        if (presenter != null) {
            presenter.onViewCreated();
        }
        if (mMorePresenters != null) {
            for (int i = 0; i < mMorePresenters.size(); i++) {
                if (mMorePresenters.get(0) != null) {
                    mMorePresenters.get(i).onViewCreated();
                }
            }
        }
        if (useEventBus()) {
            BusUtils.register(this);
        }
    }

    @Override
    public void onLeftClick() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onTitleClick() {

    }

    @Override
    public void onRightClick() {

    }

    protected void didCreate() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected P onCreatePresenter() {
        return null;
    }

    /**
     * 添加更多Presenter（复用）
     */
    protected List<? extends AbstractPresenter> addMorePresenter() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        willDestroy();
        if (useEventBus()) {
            BusUtils.unregister(this);
        }
        if (presenter != null) {
            presenter.onViewDestroy();
        }
        if (mMorePresenters != null) {
            for (int i = 0; i < mMorePresenters.size(); i++) {
                if (mMorePresenters.get(0) != null) {
                    mMorePresenters.get(i).onViewDestroy();
                }
            }
        }
        unbinder.unbind();
    }

    protected void willDestroy() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void toast(@StringRes int msgRes) {
        this.toast(getString(msgRes));
    }

    @Override
    public void toast(CharSequence msg) {
        if (getActivity() != null && getActivity() instanceof MvpBaseActivity) {
            ((MvpBaseActivity) getActivity()).toast(msg);
        }
    }

    @Override
    public void showProgressDialog(@StringRes int stringResId) {
        this.showProgressDialog(getString(stringResId));
    }

    @Override
    public void showProgressDialog(CharSequence msg) {
        if (getActivity() != null && getActivity() instanceof MvpBaseActivity) {
            ((MvpBaseActivity) getActivity()).showProgressDialog(msg);
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (getActivity() != null && getActivity() instanceof MvpBaseActivity) {
            ((MvpBaseActivity) getActivity()).dismissProgressDialog();
        }
    }

    /**
     * 是否使用事件总线（默认不使用）
     */
    protected boolean useEventBus() {
        return false;
    }


}
