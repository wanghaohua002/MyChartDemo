package com.wanghaohua.mychartlinedemo.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by wanghaohua on 2020/9/16
 */
public class DensityUtil {
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getApplicationContext().getResources().getDisplayMetrics());
    }
}
