package com.haylion.android.mvp.base;

import java.util.HashMap;
import java.util.Map;

public class ListRequest {

    private int size; // 每页显示条数默认为10
    private int page; // 当前页，默认为1

    protected Map<String, Object> params;

    public ListRequest() {
        this(1);
    }

    public ListRequest(int page) {
        this(page, 10);
    }

    public ListRequest(int page, int size) {
        this.size = size;
        this.page = page;
        this.params = new HashMap<String, Object>();
        this.params.put("size", size);
        this.params.put("page", page);
/*        this.params = new HashMap<String, Object>() {{
            put("size", size);
            put("page", page);
        }};*/
    }

    public Map<String, Object> getQueryMap() {
        return params;
    }

}
