package com.haylion.android.data.bean;

import java.util.List;

public class ClaimResult {

    /**
     * claimResult : false
     * unClaimDate : ["2021年05月10日","2021年05月09日"]
     */

    private boolean claimResult;
    private List<String> unClaimDate;

    public boolean isClaimResult() {
        return claimResult;
    }

    public void setClaimResult(boolean claimResult) {
        this.claimResult = claimResult;
    }

    public List<String> getUnClaimDate() {
        return unClaimDate;
    }

    public void setUnClaimDate(List<String> unClaimDate) {
        this.unClaimDate = unClaimDate;
    }
}
