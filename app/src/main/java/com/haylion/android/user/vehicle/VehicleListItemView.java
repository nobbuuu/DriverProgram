package com.haylion.android.user.vehicle;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.Vehicle;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class VehicleListItemView extends BaseItemView<Vehicle> {

    @BindView(R.id.vehicle_number)
    TextView vehicleNumber;

    @OnClick(R.id.edit_vehicle)
    void onEditVehicle() {
        notifyItemAction(ACTION_EDIT_VEHICLE);
    }

    public VehicleListItemView(Context context) {
        super(context);
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.vehicle_list_item;
    }

    @Override
    public void bind(Vehicle vehicle) {
        vehicleNumber.setText(vehicle.getNumber());
    }

    public static final int ACTION_EDIT_VEHICLE = 0x2432;


}
