package com.haylion.android.data.qualifier;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({
        ActionType.CLICK_TYPE_SHORT_CLICKED,
        ActionType.CLICK_TYPE_LONG_CLICKED
})
public @interface ActionType {
    int CLICK_TYPE_SHORT_CLICKED = 1000;

    int CLICK_TYPE_LONG_CLICKED = 1001;

}