package com.haylion.android.user.account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ChangePasswordActivity extends BaseActivity<ChangePasswordContract.Presenter> implements
        ChangePasswordContract.View {

    @BindView(R.id.old_password)
    EditText mOldPassword;
    @BindView(R.id.new_password)
    EditText mNewPassword;
    @BindView(R.id.password_again)
    EditText mPasswordAgain;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    @BindView(R.id.view_old_pwd_line)
    View viewOldPwdLine;
    @BindView(R.id.view_new_pwd_line)
    View viewNewPwdLine;
    @BindView(R.id.view_pwd_again_line)
    View viewPwdAgainLine;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    public static void start(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                viewOldPwdLine.setPressed(hasFocus);
            }
        });
        mNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                viewNewPwdLine.setPressed(hasFocus);
            }
        });
        mPasswordAgain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                viewPwdAgainLine.setPressed(hasFocus);
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.btn_confirm})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_confirm:
                String oldPassword = mOldPassword.getText().toString().trim();
                String newPassword = mNewPassword.getText().toString().trim();
                String passwordAgain = mPasswordAgain.getText().toString().trim();
                presenter.changePassword(oldPassword, newPassword, passwordAgain);
                break;
            default:
                break;
        }
    }

    @OnTextChanged(value = {R.id.old_password, R.id.new_password, R.id.password_again})
    public void onTextChanged() {
        String oldPwd = mOldPassword.getText().toString().trim();
        String newPwd = mNewPassword.getText().toString().trim();
        String pwdAgain = mPasswordAgain.getText().toString().trim();
        tvTips.setText("");
        tvTips.setVisibility(View.GONE);
        btnConfirm.setEnabled(!TextUtils.isEmpty(oldPwd) && !TextUtils.isEmpty(newPwd) && !TextUtils.isEmpty(pwdAgain));
    }


    @Override
    protected ChangePasswordContract.Presenter onCreatePresenter() {
        return new ChangePasswordPresenter(this);
    }


    @Override
    public void onChangePasswordSuccess() {
        toast(getString(R.string.toast_modify_pwd_success_to_login));
        LoginActivity.start(getContext(), true);
    }

   /* private void showSuccessDialog(){
        BaseDialog dialog = new BaseDialog(this,R.layout.dialog_change_pwd_success, Gravity.CENTER,true);
        dialog.getView(R.id.ll_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.toggleDialog();
            }
        });
        dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        dialog.toggleDialog();
    }*/


    @Override
    public void onChangePwdFail(String errorMsg) {
        tvTips.setText("* " + errorMsg);
        tvTips.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRightClick() {
        String oldPassword = mOldPassword.getText().toString().trim();
        String newPassword = mNewPassword.getText().toString().trim();
        String passwordAgain = mPasswordAgain.getText().toString().trim();
        presenter.changePassword(oldPassword, newPassword, passwordAgain);
    }


    @OnClick(R.id.btn_confirm)
    public void changePwd() {
        String oldPassword = mOldPassword.getText().toString().trim();
        String newPassword = mNewPassword.getText().toString().trim();
        String passwordAgain = mPasswordAgain.getText().toString().trim();
        presenter.changePassword(oldPassword, newPassword, passwordAgain);
    }


}
