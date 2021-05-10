package com.haylion.android.user.vehicle;

public enum VehicleOperation {

    ADD(1),
    DELETE(2),
    MODIFY(3);

    int operation;

    VehicleOperation(int operation) {
        this.operation = operation;
    }

    public int getOperation() {
        return operation;
    }

}
