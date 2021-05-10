package com.haylion.android.data.model;

/**
 * @author dengzh
 * @date 2019/11/15
 * Description:
 */
public class PaymentResult {

    /**
     * 订单id
     */
    private long orderId;

    /**
     * true：自动付款成功（已完成）；  false：自动付款失败，或非自动付款订单（待支付）
     * */
    private boolean isAutoPaySucceed;

    public PaymentResult(boolean isAutoPaySucceed) {
        this.isAutoPaySucceed = isAutoPaySucceed;
    }

}
