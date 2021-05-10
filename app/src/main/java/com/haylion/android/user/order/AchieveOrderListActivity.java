package com.haylion.android.user.order;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.haylion.android.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AchieveOrderListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achieve_order_list);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}
