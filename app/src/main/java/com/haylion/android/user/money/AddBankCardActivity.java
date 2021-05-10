package com.haylion.android.user.money;


import android.app.Activity;
import android.os.Bundle;

import com.haylion.android.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBankCardActivity extends Activity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        ButterKnife.bind(this);

    }

/*    @OnClick({R.id.iv_back})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }*/

    @OnClick(R.id.iv_back)
    public void clickBackMenu() {
        finish();
    }

}
