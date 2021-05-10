package com.haylion.android.uploadPhoto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.haylion.android.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by dengzh on 2019/9/25
 */
public class LargeImageDialog extends DialogFragment {


    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private List<String> list = new ArrayList<>();
    private int totalNum; //总页数
    private boolean isShowNum = true; //是否显示数字
    private Unbinder unbinder;

    public static LargeImageDialog newInstance() {
        return new LargeImageDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullScreen); //dialog全屏
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_large_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {
        totalNum = list.size();
        tvNum.setText("1/" + totalNum);
        tvNum.setVisibility(isShowNum? View.VISIBLE:View.INVISIBLE);
        viewPager.setAdapter(new LargeImageAdapter(getContext(), list));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            /**
             * 页面滑动结束时回调
             * @param i
             */
            @Override
            public void onPageSelected(int i) {
                tvNum.setText((i + 1) + "/" + totalNum);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setCurrentItem(0);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isAdded()) {
            return;
        }
        super.show(manager, tag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public LargeImageDialog setData(List<String> list) {
        this.list = list;
        return this;
    }

    public LargeImageDialog setData(String url) {
        list.add(url);
        return this;
    }

    public LargeImageDialog isShowNum(boolean flag){
        isShowNum = flag;
        return this;
    }


    @OnClick({R.id.iv_close, R.id.ll_main})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
            case R.id.ll_main:
                dismiss();
                break;
            default:
                break;
        }
    }
}
