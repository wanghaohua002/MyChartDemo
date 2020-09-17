package com.wanghaohua.mychartlinedemo.timetrend;

import android.graphics.DashPathEffect;
import android.graphics.Paint;

/**
 * Created by wanghaohua on 2020/9/16
 */
public class TimeTrendPaint {
    //分时图主线画笔颜色
    public final static int COLOR_TREND_LINE = 0xff2c5c9b;
    //外围边线画笔颜色
    public final static int COLOR_OUT_LINE = 0xff1b1e28;
    //昨日收盘价参考线颜色
    public final static int COLOR_PREPRICE_LINE = 0xffe7b8c0;
    //分时图下面的渐变色
    public final static int C_PRICE_BG = 0x504486CB; //阴影背景渐变起始色
    public final static int C_PRICE_ED = 0x0051A7FF; //阴影背景渐变高位色
    //红涨绿跌,平灰
    public final static int COLOR_RED = 0xfffec1bb;
    public final static int COLOR_GREEN = 0xffa6e1b9;
    public final static int COLOR_DARK = 0xffd4d6da;

    public Paint mOutLinePaint;
    public Paint mTextPaint;
    public Paint mTrendLinePaint;
    public Paint mPrePriceLinePaint;
    public Paint mGradientPaint;
    public Paint mRedPaint;
    public Paint mGreenPaint;
    public Paint mDarkPaint;

    public TimeTrendPaint() {
        mOutLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutLinePaint.setColor(COLOR_OUT_LINE);
        mOutLinePaint.setStyle(Paint.Style.STROKE);
        mOutLinePaint.setStrokeWidth(1f);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(COLOR_OUT_LINE);
        mTextPaint.setStyle(Paint.Style.FILL);

        mTrendLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrendLinePaint.setColor(COLOR_TREND_LINE);
        mTrendLinePaint.setStyle(Paint.Style.STROKE);
        mTrendLinePaint.setStrokeWidth(2f);

        mPrePriceLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPrePriceLinePaint.setColor(COLOR_PREPRICE_LINE);
        mPrePriceLinePaint.setStyle(Paint.Style.STROKE);
        mPrePriceLinePaint.setStrokeWidth(2f);
        mPrePriceLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGradientPaint.setStyle(Paint.Style.FILL);

        mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRedPaint.setColor(COLOR_RED);
        mRedPaint.setStyle(Paint.Style.STROKE);
        mRedPaint.setStrokeWidth(5f);

        mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGreenPaint.setColor(COLOR_GREEN);
        mGreenPaint.setStyle(Paint.Style.STROKE);
        mGreenPaint.setStrokeWidth(5f);

        mDarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDarkPaint.setColor(COLOR_DARK);
        mDarkPaint.setStyle(Paint.Style.STROKE);
        mDarkPaint.setStrokeWidth(5f);
    }


    public static TimeTrendPaint get() {
        return new TimeTrendPaint();
    }
}
