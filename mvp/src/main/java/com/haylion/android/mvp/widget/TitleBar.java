package com.haylion.android.mvp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.haylion.android.mvp.R;

public class TitleBar extends FrameLayout {

    private Context mContext;

    FrameLayout mTitleBar;

    TextView mLeftText;
    ImageView mLeftIcon;

    TextView mTitle;
    TextView mSubtitle;

    TextView mRightText;
    ImageView mRightIcon;

    private OnClickListener mListener;

    public TitleBar(@NonNull Context context) {
        this(context, null);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews(attrs);
    }

    private void initViews(@Nullable AttributeSet attrs) {
        View.inflate(mContext, R.layout.title_bar, this);
        findViewsById();
        setViewsStyle(attrs);
    }

    private void findViewsById() {
        mTitleBar = findViewById(R.id.title_layout);

        mLeftText = findViewById(R.id.left_text);
        mLeftText.setOnClickListener(mInternalListener);
        mLeftIcon = findViewById(R.id.left_icon);
        mLeftIcon.setOnClickListener(mInternalListener);

        mTitle = findViewById(R.id.title);
        mTitle.setOnClickListener(mInternalListener);
        mSubtitle = findViewById(R.id.subtitle);
        mSubtitle.setOnClickListener(mInternalListener);

        mRightText = findViewById(R.id.right_text);
        mRightText.setOnClickListener(mInternalListener);
        mRightIcon = findViewById(R.id.right_icon);
        mRightIcon.setOnClickListener(mInternalListener);
    }

    private void setViewsStyle(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.TitleBar, 0, 0);
        try {
            int bgResId = ta.getResourceId(R.styleable.TitleBar_tb_background, 0);
            if (bgResId == 0) {
                bgResId = android.R.color.black;
            }
            mTitleBar.setBackgroundResource(bgResId);

            boolean leftTextVisible = ta.getBoolean(R.styleable.TitleBar_tb_left_text_visible, false);
            if (leftTextVisible) {
                String leftText = ta.getString(R.styleable.TitleBar_tb_left_text);
                if (TextUtils.isEmpty(leftText)) {
                    leftTextVisible = false;
                } else {
                    mLeftText.setText(leftText);
                }

            } else {
                mLeftText.setText(null);
            }

            boolean leftIconVisible = ta.getBoolean(R.styleable.TitleBar_tb_left_icon_visible, true);
            if (leftIconVisible) {
                int leftIconResId = ta.getResourceId(R.styleable.TitleBar_tb_left_icon, R.drawable.ic_arrow_back);
                setLeftIcon(leftIconResId);
            } else {
                mLeftIcon.setVisibility(GONE);
                if (!leftTextVisible) {
                    mLeftText.setVisibility(INVISIBLE);
                }
            }

            boolean titleVisible = ta.getBoolean(R.styleable.TitleBar_tb_title_visible, true);
            if (titleVisible) {
                String title = ta.getString(R.styleable.TitleBar_tb_title);
                mTitle.setText(title);
                int titleColor = ta.getColor(R.styleable.TitleBar_tb_title_color, Color.WHITE);
                mTitle.setTextColor(titleColor);
                boolean subtitleVisible = ta.getBoolean(R.styleable.TitleBar_tb_subtitle_visible, false);
                if (subtitleVisible) {
                    String subtitle = ta.getString(R.styleable.TitleBar_tb_subtitle);
                    mSubtitle.setText(subtitle);
                    int subtitleColor = ta.getColor(R.styleable.TitleBar_tb_subtitle_color, Color.WHITE);
                    mSubtitle.setTextColor(subtitleColor);
                } else {
                    mSubtitle.setVisibility(GONE);
                }

            } else {
                mTitle.setVisibility(GONE);
                mSubtitle.setVisibility(GONE);
            }

            boolean rightTextVisible = ta.getBoolean(R.styleable.TitleBar_tb_right_text_visible, true);
            if (rightTextVisible) {
                String rightText = ta.getString(R.styleable.TitleBar_tb_right_text);
                if (TextUtils.isEmpty(rightText)) {
                    rightTextVisible = false;
                } else {
                    mRightText.setText(rightText);
                }

            } else {
                mRightText.setText(null);
            }

            boolean rightIconVisible = ta.getBoolean(R.styleable.TitleBar_tb_right_icon_visible, false);
            if (rightIconVisible) {
                int rightIconResId = ta.getResourceId(R.styleable.TitleBar_tb_right_icon, 0);
                setRightIcon(rightIconResId);
            } else {
                mRightIcon.setVisibility(GONE);
                if (!rightTextVisible) {
                    mRightText.setVisibility(INVISIBLE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ta.recycle();
        }
    }

    private View.OnClickListener mInternalListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.left_icon || id == R.id.left_text) {
                if (mListener != null) {
                    mListener.onLeftClick();
                }

            } else if (id == R.id.right_icon || id == R.id.right_text) {
                if (mListener != null) {
                    mListener.onRightClick();
                }

            } else if (id == R.id.title || id == R.id.subtitle) {
                if (mListener != null) {
                    mListener.onTitleClick();
                }

            }
        }
    };

    public void setLeftIcon(@DrawableRes int drawableResId) {
        if (drawableResId == 0) {
            mLeftIcon.setVisibility(GONE);
            return;
        }
        mLeftIcon.setImageResource(drawableResId);
    }

    public void setLeftIcon(Drawable leftIcon) {
        mLeftIcon.setImageDrawable(leftIcon);
    }

    public void setRightIcon(@DrawableRes int drawableResId) {
        if (drawableResId == 0) {
            mRightIcon.setVisibility(GONE);
            return;
        }
        mRightIcon.setImageResource(drawableResId);
    }

    public void setRightIcon(Drawable leftIcon) {
        mRightIcon.setImageDrawable(leftIcon);
    }

    public interface OnClickListener {
        void onLeftClick();

        void onRightClick();

        void onTitleClick();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    public void setTitle(CharSequence t) {
        if (TextUtils.isEmpty(t)) {
            mTitle.setVisibility(GONE);
        } else {
            if (mTitle.getVisibility() != VISIBLE) {
                mTitle.setVisibility(VISIBLE);
            }
            mTitle.setText(t);
        }
    }

    public void setTitle(@StringRes int strResId) {
        setTitle(getString(strResId));
    }

    public void setSubtitle(CharSequence sub) {
        if (TextUtils.isEmpty(sub)) {
            mSubtitle.setVisibility(GONE);
        } else {
            if (mSubtitle.getVisibility() != VISIBLE) {
                mSubtitle.setVisibility(VISIBLE);
            }
            mSubtitle.setText(sub);
        }
    }

    public void setSubtitle(@StringRes int strResId) {
        setSubtitle(getString(strResId));
    }

    private String getString(@StringRes int strResId) {
        return getContext().getString(strResId);
    }

    public boolean isLeftIconVisible() {
        return mLeftIcon.getVisibility() == View.VISIBLE;
    }

    public void setLeftText(CharSequence t) {
        mLeftText.setText(t);
    }


}
