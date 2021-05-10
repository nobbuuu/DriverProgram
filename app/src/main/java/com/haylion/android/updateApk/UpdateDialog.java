package com.haylion.android.updateApk;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateDialog extends BaseDialog {

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @OnClick({R.id.stop_download, R.id.dismiss_dialog})
    void onButtonClick(View view) {
        dismiss();
        if (view.getId() == R.id.stop_download) {
            if (mCallback != null) {
                mCallback.onCanceled();
            }
        }
    }

    private Callback mCallback;

    public UpdateDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_dlg);
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public interface Callback {

        void onCanceled();

    }

    public UpdateDialog setCallback(Callback callback) {
        this.mCallback = callback;
        return this;
    }


}
