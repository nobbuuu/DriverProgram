package com.haylion.android.data.model;

public enum CarpoolStatus {

    UNKNOWN(-1), // 状态未知
    NOT_CARPOOL(0), // 不拼单
    PROGRESSING(1), // 拼单中
    SUCCESS(2), // 拼单成功
    FAILURE(3); // 拼单失败

    int status;

    CarpoolStatus(int status) {
        this.status = status;
    }

    public static CarpoolStatus forStatus(int status) {
        switch (status) {
            case 0:
                return NOT_CARPOOL;
            case 1:
                return PROGRESSING;
            case 2:
                return SUCCESS;
            case 3:
                return FAILURE;
        }
        return UNKNOWN;
    }

    public static boolean isCarpool(int status){
        return forStatus(status) == SUCCESS;
    }

}
