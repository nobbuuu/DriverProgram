package com.haylion.android.user.vehicle;

import com.haylion.android.data.model.Vehicle;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

class MyVehicleContract {

    interface View extends AbstractView {

        void showVehicleList(List<Vehicle> vehicleList);

    }

    interface Presenter extends AbstractPresenter {

        void updateVehicle(VehicleOperation operation,Vehicle vehicle);

    }

}
