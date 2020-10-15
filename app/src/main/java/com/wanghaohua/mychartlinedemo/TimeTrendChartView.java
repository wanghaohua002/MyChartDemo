package com.wanghaohua.mychartlinedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.wanghaohua.mychartlinedemo.timetrend.TimeTrendDataSource;
import com.wanghaohua.mychartlinedemo.timetrend.TimeTrendModel;
import com.wanghaohua.mychartlinedemo.timetrend.TimeTrendPaint;
import com.wanghaohua.mychartlinedemo.timetrend.TimeTrendTag;

import java.util.List;

import static com.wanghaohua.mychartlinedemo.util.DensityUtil.dp2px;

/**
 * Created by wanghaohua on 2020/9/16
 */
public class TimeTrendChartView extends View {

    private static final int COLUMN_EACH_DAY = 314;
    //分时图区域
    private Rect mTrendFrame = new Rect();
    //文字区域
    private Rect mTextFrame = new Rect();
    //成交量区域
    private Rect mVolumeFrame = new Rect();
    //文字大小
    private Rect mRect = new Rect();

    //每段宽度
    private float mXPhaseWidth;

    private int mPadding;
    private TimeTrendPaint mPaints;

    private List<TimeTrendModel> mData;
    //昨天收盘价
    private float mPrePrice;
    //每日的开盘收盘时间点
    private TimeTrendTag mTimeTag;

    private Path mPath = new Path();
    private Path mBgPath = new Path();
    private float mMaxPrice = Float.MIN_VALUE;
    private float mMinPrice = Float.MAX_VALUE;
    private float mMaxVolume = Float.MIN_VALUE;


    public TimeTrendChartView(Context context) {
        super(context);
    }

    public TimeTrendChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeTrendChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        mPadding = dp2px(getContext(), 20);
        mPaints = TimeTrendPaint.get();

        TimeTrendDataSource dataSource = new TimeTrendDataSource();
        mData = dataSource.getData(getContext());
        mPrePrice = dataSource.getPrePrice();
        mTimeTag = dataSource.getTimeTag();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int trendHeight = (h - 2 * mPadding) * 3 / 5;
        int volumeHeight = (h - 2 * mPadding) * 3 / 10;
        int textHeight = (h - 2 * mPadding) / 10;

        mTrendFrame.set(mPadding, mPadding, w - mPadding, mPadding + trendHeight);
        mTextFrame.set(mPadding, mPadding + trendHeight, w - mPadding,
                mPadding + trendHeight + textHeight);
        mVolumeFrame.set(mPadding, mPadding + trendHeight + textHeight, w - mPadding, h - mPadding);

        mXPhaseWidth = ((float) (w - 2 * mPadding - TimeTrendPaint.WIDTH_VOLUME)) / (COLUMN_EACH_DAY - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initDrawData();
        drawTimeTrend(canvas);
        drawText(canvas);
        drawVolume(canvas);
    }

    private void initDrawData() {
        float lastPrice = 0f;
        for (int i = 0; i < mData.size(); i++) {
            TimeTrendModel model = mData.get(i);
            float price = Float.parseFloat(model.getPrice());
            if (price > mMaxPrice) {
                mMaxPrice = price;
            }
            if (price < mMinPrice) {
                mMinPrice = price;
            }

            float volume = Float.parseFloat(model.getVolume());
            if (volume > mMaxVolume) {
                mMaxVolume = volume;
            }

            //上一分时的价格
            if (i == 0) {
                lastPrice = mPrePrice;
            }
            if (lastPrice > price) {
                model.state = TimeTrendModel.STATE_FALL;
            } else if (lastPrice < price) {
                model.state = TimeTrendModel.STATE_RISE;
            } else {
                model.state = TimeTrendModel.STATE_FLAT;
            }

            lastPrice = price;
        }

        float delta = countDelta(mMaxPrice, mMinPrice);
        mMaxPrice = mMaxPrice + delta;
        mMinPrice = Math.max(mMinPrice - delta, 0);

    }

    private void drawTimeTrend(Canvas canvas) {
        canvas.drawRect(mTrendFrame, mPaints.mOutLinePaint);
        float diffPrice = mMaxPrice - mMinPrice;
        float lastX = mTrendFrame.left;
        for (int i = 0; i < mData.size(); i++) {
            TimeTrendModel model = mData.get(i);
            float currentX = mTrendFrame.left + i * mXPhaseWidth + TimeTrendPaint.WIDTH_VOLUME / 2;
            float currentY =
                    mTrendFrame.bottom - (Float.parseFloat(model.getPrice()) - mMinPrice) / diffPrice * mTrendFrame.height();
            if (i == 0) {
                mPath.moveTo(currentX, currentY);
                mBgPath.moveTo(currentX, mTrendFrame.bottom);
            } else {
                mPath.lineTo(currentX, currentY);
            }
            mBgPath.lineTo(currentX, currentY);
            lastX = currentX;
        }

        mBgPath.lineTo(lastX, mTrendFrame.bottom);
        mBgPath.close();

        canvas.drawPath(mPath, mPaints.mTrendLinePaint);
        mPaints.mGradientPaint.setShader(new LinearGradient(mTrendFrame.left, mTrendFrame.top,
                mTrendFrame.left, mTrendFrame.bottom, TimeTrendPaint.C_PRICE_BG,
                TimeTrendPaint.C_PRICE_ED, Shader.TileMode.CLAMP));
        canvas.drawPath(mBgPath, mPaints.mGradientPaint);
        mBgPath.reset();
        mPath.reset();
    }

    private void drawText(Canvas canvas) {
        canvas.drawRect(mTextFrame, mPaints.mOutLinePaint);
        mPaints.mTextPaint.setTextSize(dp2px(getContext(), 12));
        float textMargin = dp2px(getContext(), 3);

        mPaints.mTextPaint.getTextBounds(mTimeTag.startTime, 0, mTimeTag.startTime.length(), mRect);
        mPaints.mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(mTimeTag.startTime, mTextFrame.left + textMargin,
                mTextFrame.top + textMargin + mRect.height(), mPaints.mTextPaint);

        mPaints.mTextPaint.getTextBounds(mTimeTag.middleTime, 0, mTimeTag.middleTime.length(),
                mRect);
        mPaints.mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mTimeTag.middleTime, ((float) (mTextFrame.left + mTextFrame.right)) / 2,
                mTextFrame.top + textMargin + mRect.height(), mPaints.mTextPaint);

        mPaints.mTextPaint.getTextBounds(mTimeTag.endTime, 0, mTimeTag.endTime.length(), mRect);
        mPaints.mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(mTimeTag.endTime, mTextFrame.right - textMargin,
                mTextFrame.top + textMargin + mRect.height(), mPaints.mTextPaint);
    }

    private void drawVolume(Canvas canvas) {
        canvas.drawRect(mVolumeFrame, mPaints.mOutLinePaint);
        for (int i = 0; i < mData.size(); i++) {
            Paint paint = null;
            TimeTrendModel model = mData.get(i);
            float currentX = mVolumeFrame.left + i * mXPhaseWidth + TimeTrendPaint.WIDTH_VOLUME / 2;
            float currentY = mVolumeFrame.bottom - Float.parseFloat(model.getVolume()) / mMaxVolume * mVolumeFrame.height();
            switch (model.state) {
                case TimeTrendModel.STATE_FALL:
                    paint = mPaints.mGreenPaint;
                    break;
                case TimeTrendModel.STATE_RISE:
                    paint = mPaints.mRedPaint;
                    break;
                case TimeTrendModel.STATE_FLAT:
                default:
                    paint = mPaints.mDarkPaint;
                    break;
            }
            canvas.drawLine(currentX, mVolumeFrame.bottom, currentX, currentY, paint);
        }
    }


    /**
     * 处理最大值最小值差值，使其分布在昨日收盘价格上下部分
     *
     * @return
     */
    private float countDelta(float maxPrice, float minPrice) {
        float delta_max = Math.abs(maxPrice - mPrePrice);
        float delta_min = Math.abs(minPrice - mPrePrice);
        float delta = Math.max(delta_max, delta_min);
        delta *= 1.05;
        if (mPrePrice > 10 && delta < 0.02f) {
            delta = 0.02f;
        }
        if (mPrePrice < 10 && delta < 0.002f) {
            delta = 0.002f;
            if (mPrePrice > 0.01f) {
                delta = 0.01f; //避免在价格坐标轴上无法体现出来
            }
        }
        return delta;
    }
}
