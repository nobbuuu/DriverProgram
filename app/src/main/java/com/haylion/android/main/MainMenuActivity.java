package com.haylion.android.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.notification.NotificationListActivity;
import com.haylion.android.orderlist.OrderListActivity;
import com.haylion.android.orderlist.achievement.AchievementListActivity;
import com.haylion.android.user.money.IncomeActivity;
import com.haylion.android.user.setting.SettingActivity;
import com.haylion.android.user.shift.ShiftInfoActivity;
import com.haylion.android.user.vehicle.MyVehicleActivity;
import com.haylion.android.user.wallet.MyWalletActivity;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenuActivity extends BaseActivity {

    @BindView(R.id.title_layout)
    View mTitleLayout;

    @BindView(R.id.iv_user_photo)
    CircleImageView mUserAvatar;
    @BindView(R.id.work_number)
    TextView mWorkNumber;
    @BindView(R.id.driver_real_name)
    TextView mDriverName;

    @OnClick({R.id.btn_back, R.id.ll_my_vehicle, R.id.ll_money,
            R.id.ll_income_list, R.id.ll_history_order, R.id.ll_history_achieve,
            R.id.ll_notification_list, R.id.ll_shift_ino, R.id.ll_setting
    })
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.ll_my_vehicle:
                MyVehicleActivity.start(getContext());
                break;
            case R.id.ll_money:
                startActivity(new Intent(this, MyWalletActivity.class));
                break;
            case R.id.ll_income_list:
                Intent intent = new Intent(this, IncomeActivity.class);
                intent.putExtra("header", "收益明细");
                startActivity(intent);
                break;
            case R.id.ll_history_order:
                OrderListActivity.start(this, OrderTimeType.HISTORY);
                break;
            case R.id.ll_history_achieve:
                intent = new Intent(this, AchievementListActivity.class);
                intent.putExtra("header", "历史成绩");
                startActivity(intent);
                break;
            case R.id.ll_notification_list:
                intent = new Intent(this, NotificationListActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_shift_ino:
                ShiftInfoActivity.start(getContext());
                break;
            case R.id.ll_setting:
                intent = new Intent(this, SettingActivity.class);
                intent.putExtra("header", "设置");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        transparentStatusBar();
        showDriverInfo(PrefserHelper.getDriverInfo());
    }

    @Override
    protected boolean lightStatusBar() {
        return false;
    }

    private void transparentStatusBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(false) //设置为true，图片无法置顶状态栏
                .transparentStatusBar()
                .statusBarDarkFont(false, 0.2f)
                .titleBarMarginTop(mTitleLayout)
                .init();
    }

    private void showDriverInfo(Driver driver) {
        if (driver == null) {
            return;
        }
        mWorkNumber.setText(driver.getCode());
        String driverName = driver.getLastName() + driver.getFirstName();
        mDriverName.setText(driverName);
        if (!TextUtils.isEmpty(driver.getPhotoUrl())) {
            Glide.with(this).load(driver.getPhotoUrl()).into(mUserAvatar);
        }
    }


}
