package com.haylion.android.user.shift;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.event.AddressChangedEvent;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.mvp.util.BusUtils;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class ShiftAddressActivity extends BaseActivity {

    @OnClick({R.id.btn_back, R.id.modify_address})
    void onViewClick(View view) {
        if (view.getId() == R.id.modify_address) {
            Intent intent = new Intent();
            intent.setClass(getContext(), PickAddressActivity.class);
            int addressType = parseAddressType();
            if (addressType == ADDRESS_TYPE_TURNOVER_DAY) {
                startActivityForResult(intent, REQUEST_PICK_TURNOVER_ADDRESS_DAY);

            } else if (addressType == ADDRESS_TYPE_TURNOVER_NIGHT) {
                startActivityForResult(intent, REQUEST_PICK_TURNOVER_ADDRESS_NIGHT);

            } else if (addressType == ADDRESS_TYPE_MAILING) {
                startActivityForResult(intent, REQUEST_PICK_MAILING_ADDRESS);
            }

        } else if (view.getId() == R.id.btn_back) {
            finish();
        }
    }

    @BindView(R.id.page_title)
    TextView mPageTitle;

    @BindView(R.id.address_title)
    TextView mAddressTitle;
    @BindView(R.id.address_desc)
    TextView mAddressDesc;

    @BindView(R.id.modify_address)
    TextView mModifyAddress;

    public static final int ADDRESS_TYPE_TURNOVER_DAY = 1;
    public static final int ADDRESS_TYPE_TURNOVER_NIGHT = 2;
    public static final int ADDRESS_TYPE_MAILING = 3;

    public static void start(Context context, int addressType, AddressInfo address) {
        Intent intent = new Intent();
        intent.setClass(context, ShiftAddressActivity.class);
        intent.putExtra(EXTRA_ADDRESS_TYPE, addressType);
        intent.putExtra(EXTRA_ADDRESS_INFO, address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_address);
        int addressType = parseAddressType();
        if (addressType == ADDRESS_TYPE_TURNOVER_DAY) {
            mPageTitle.setText(R.string.label_shift_turnover_address_day);
            mModifyAddress.setText(R.string.action_modify_turnover_address_day);

        } else if (addressType == ADDRESS_TYPE_TURNOVER_NIGHT) {
            mPageTitle.setText(R.string.label_shift_turnover_address_night);
            mModifyAddress.setText(R.string.action_modify_turnover_address_night);

        } else if (addressType == ADDRESS_TYPE_MAILING) {
            mPageTitle.setText(R.string.label_mailing_address);
            mModifyAddress.setText(R.string.action_modify_mailing_address);

        } else {
            throw new IllegalArgumentException("Invalid address type!");
        }
        AddressInfo addressInfo = parseAddressInfo();
        if (addressInfo == null) {
            if (addressType == ADDRESS_TYPE_TURNOVER_DAY) {
                mAddressTitle.setText("您还没有添加早交接班地址");
            } else if (addressType == ADDRESS_TYPE_TURNOVER_NIGHT) {
                mAddressTitle.setText("您还没有添加晚交接班地址");
            } else if (addressType == ADDRESS_TYPE_MAILING) {
                mAddressTitle.setText("您还没有添加在深居住地址");
            }
            mAddressDesc.setVisibility(View.GONE);
        } else {
            mAddressTitle.setText(addressInfo.getName());
            mAddressDesc.setText(addressInfo.getAddressDetail());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            AddressInfo addressInfo = PickAddressActivity.parseAddress(data);
            if (addressInfo != null) {
                mAddressTitle.setText(addressInfo.getName());
                mAddressDesc.setText(addressInfo.getAddressDetail());
                mAddressDesc.setVisibility(View.VISIBLE);
                AddressChangedEvent event = new AddressChangedEvent(
                        parseAddressType(), addressInfo
                );
                BusUtils.post(event);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private int parseAddressType() {
        return getIntent().getIntExtra(EXTRA_ADDRESS_TYPE, -1);
    }

    private AddressInfo parseAddressInfo() {
        return getIntent().getParcelableExtra(EXTRA_ADDRESS_INFO);
    }

    private static final String EXTRA_ADDRESS_TYPE = "address_type";
    private static final String EXTRA_ADDRESS_INFO = "address_info";

    private static final int REQUEST_PICK_TURNOVER_ADDRESS_DAY = 0x245;
    private static final int REQUEST_PICK_TURNOVER_ADDRESS_NIGHT = 0x246;
    private static final int REQUEST_PICK_MAILING_ADDRESS = 0x567;


}
