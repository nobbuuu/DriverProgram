package com.haylion.android.data.widgt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.jcodecraeer.xrecyclerview.CustomFooterViewCallBack;
import com.jcodecraeer.xrecyclerview.LoadingMoreFooter;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.SimpleViewSwitcher;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;

public class AchievementLoadingMoreFooter extends LoadingMoreFooter {

    private SimpleViewSwitcher progressCon;
    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;

    private TextView mText;
    private View leftLine,rightLine;
    private String loadingHint;
    private String noMoreHint;
    private String loadingDoneHint;

    private AVLoadingIndicatorView progressView;

    public AchievementLoadingMoreFooter(Context context) {
        super(context);
        initView();
    }

    /**
     * @param context
     * @param attrs
     */
    public AchievementLoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void destroy(){
        progressCon = null;
        if(progressView != null){
            progressView.destroy();
            progressView = null;
        }
    }

    public void setLoadingHint(String hint) {
        loadingHint = hint;
    }

    public void setNoMoreHint(String hint) {
        noMoreHint = hint;
    }

    public void setLoadingDoneHint(String hint) {
        loadingDoneHint = hint;
    }

    private ViewClickCallBack viewClickCallBack;
    public void initView(){
        setGravity(Gravity.CENTER);
        setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.mass_loading_more_footer, this);

        //加载圈
        progressCon = rootView.findViewById(R.id.progressCon);
        progressView = new  AVLoadingIndicatorView(this.getContext());
        progressView.setIndicatorColor(0xffB5B5B5);
        progressView.setIndicatorId(ProgressStyle.BallSpinFadeLoader);
        progressCon.setView(progressView);

        //提示文字
        leftLine = rootView.findViewById(R.id.leftLine);
        rightLine = rootView.findViewById(R.id.rightLine);
        mText = rootView.findViewById(R.id.tv_desc);
        mText.setText(getContext().getString(R.string.listview_loading));

        if(loadingHint == null || loadingHint.equals("")){
            loadingHint = (String)getContext().getText(R.string.listview_loading);
        }
        if(noMoreHint == null || noMoreHint.equals("")){
            noMoreHint = "到底了";
        }
        if(loadingDoneHint == null || loadingDoneHint.equals("")){
            loadingDoneHint = (String)getContext().getText(R.string.loading_done);
        }

        mText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewClickCallBack.addMoreViewClick();
            }
        });

    }

    public void setProgressStyle(int style) {
        if(style == ProgressStyle.SysProgress){
            progressCon.setView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyle));
        }else{
            progressView = new  AVLoadingIndicatorView(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            progressCon.setView(progressView);
        }
    }

    public void  setState(int state) {
        switch(state) {
            case STATE_LOADING:
                leftLine.setVisibility(GONE);
                rightLine.setVisibility(GONE);
                progressCon.setVisibility(VISIBLE);
                mText.setText(loadingHint);
                this.setVisibility(VISIBLE);
                break;
            case STATE_COMPLETE:
                leftLine.setVisibility(GONE);
                rightLine.setVisibility(GONE);
                mText.setText(loadingDoneHint);
//                this.setVisibility(View.GONE);
                progressCon.setVisibility(GONE);
                break;
            case STATE_NOMORE:
               /* leftLine.setVisibility(VISIBLE);
                rightLine.setVisibility(VISIBLE);*/

                leftLine.setVisibility(GONE);
                rightLine.setVisibility(GONE);
                mText.setText(noMoreHint);
                mText.setClickable(false); //到底了，不可点击。
                progressCon.setVisibility(View.GONE);
                this.setVisibility(VISIBLE);
                break;
        }
    }

    public CustomFooterViewCallBack callBack = new CustomFooterViewCallBack() {
        @Override
        public void onLoadingMore(View yourFooterView) {
            setState(STATE_LOADING);
        }

        @Override
        public void onLoadMoreComplete(View yourFooterView) {
            setState(STATE_COMPLETE);
        }

        @Override
        public void onSetNoMore(View yourFooterView, boolean noMore) {
            setState(STATE_NOMORE);
        }
    };

    public void setAddMoreViewCallBack(ViewClickCallBack callBack){
        this.viewClickCallBack = callBack;
    }

    public interface ViewClickCallBack{
        public void addMoreViewClick();
    }
}
