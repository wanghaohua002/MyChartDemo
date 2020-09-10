package com.wanghaohua.mychartlinedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

/**
 * Created by wanghaohua on 2020/9/8
 */
public class HistogramView extends View {

    //纵轴最大值
    private int mMaxYValue;
    //纵轴最小值
    private int mMinYValue;
    //Y分段数
    private int mYPhases = 6;
    //文字margin
    private int mTextMargin;
    //x轴游标高度
    private int mCursorHeight;
    //横轴每段长度
    private int mXPhaseWidth;
    //横轴
    private String[] mXAxisTexts = {"7月", "8月", "9月", "10月", "11月", "12月"};
    //点数据
    private int[] mValues = {30, 200, 481, 350, 600, 253};
    //柱宽度
    private int mBarWidth;

    private int mPadding;

    //画字和线
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //画柱
    private Paint mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //Y轴字体宽高
    private float mMaxYTextWidth;
    private float mMaxYTextHeight;
    //X轴字体高度
    private float mXTextHeight;

    //框图边缘坐标
    private float mGraphStartY;
    private float mGraphEndY;
    private float mGraphStartX;
    private float mGraphEndX;

    private Rect mBounds = new Rect();
    private Path mPath = new Path();
    private Random random = new Random();

    public HistogramView(Context context) {
        super(context);
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        int max = getMax(mValues);
        int min = getMin(mValues);
        mMinYValue = Math.max(0, min - (max - min) / 10);
        mMaxYValue = max - mMinYValue + (max - mMinYValue) / 10;

        mPadding = dp2px(getContext(), 20);
        mTextMargin = dp2px(getContext(), 5);
        mCursorHeight = dp2px(getContext(), 5);

        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(dp2px(getContext(), 14));

        mPaint.getTextBounds(String.valueOf(mMaxYValue), 0, String.valueOf(mMaxYValue).length(),
                mBounds);
        mMaxYTextWidth = mBounds.width();
        mMaxYTextHeight = mBounds.height();

        mPaint.getTextBounds(mXAxisTexts[0], 0, mXAxisTexts[0].length(), mBounds);
        mXTextHeight = mBounds.height();
    }

    private int getMax(int[] nums) {
        int max = nums[0];
        for (int i = 1; i < nums.length; i++) {
            max = Math.max(max, nums[i]);
        }
        return max;
    }

    private int getMin(int[] nums) {
        int min = nums[0];
        for (int i = 1; i < nums.length; i++) {
            min = Math.min(min, nums[i]);
        }
        return min;
    }

    public int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getApplicationContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXPhaseWidth = (int) ((w - dp2px(getContext(), 30) - 2 * mPadding - mMaxYTextWidth) / mXAxisTexts.length);
        mBarWidth = mXPhaseWidth / 2;
        mBarPaint.setStrokeWidth(mBarWidth);

        mGraphStartY = h - mPadding - mXTextHeight - mMaxYTextHeight / 2;
        mGraphEndY = mPadding - mMaxYTextHeight / 2;
        mGraphStartX = mPadding + mMaxYTextWidth + mTextMargin;
        mGraphEndX = w - mPadding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawYAxis(canvas);
        drawXAxis(canvas);
        drawBars(canvas);
    }

    private void drawYAxis(Canvas canvas) {
        int phaseValueUnit = (mMaxYValue - mMinYValue) / mYPhases;

        //画Y轴
        canvas.drawLine(mGraphStartX, mGraphStartY,
                mGraphStartX, mGraphEndY, mPaint);

        //画箭头
        canvas.save();
        canvas.translate(mGraphStartX, mGraphEndY);
        canvas.rotate(-30);
        canvas.drawLine(0, 0, 0, dp2px(getContext(), 20), mPaint);
        canvas.rotate(60);
        canvas.drawLine(0, 0, 0, dp2px(getContext(), 20), mPaint);
        canvas.restore();

        //画Y轴的每一段
        for (int i = 0; i <= mYPhases; i++) {
            float currentY = mGraphStartY - (mGraphStartY - mGraphEndY -  dp2px(getContext(), 30)) * i / mYPhases;
            String currentText = String.valueOf(mMinYValue + i * phaseValueUnit);

            mPaint.getTextBounds(currentText, 0, currentText.length(), mBounds);
            //画标尺
            canvas.drawText(currentText,
                    mGraphStartX - mTextMargin - mBounds.width(), currentY + ((float) mBounds.height()) / 2, mPaint);
        }
    }

    private void drawXAxis(Canvas canvas) {
        //画X轴
        canvas.drawLine(mGraphStartX, mGraphStartY, mGraphEndX, mGraphStartY, mPaint);
        //画箭头
        canvas.save();
        canvas.translate(mGraphEndX, mGraphStartY);
        canvas.rotate(60);
        canvas.drawLine(0, 0, 0, dp2px(getContext(), 20), mPaint);
        canvas.rotate(60);
        canvas.drawLine(0, 0, 0, dp2px(getContext(), 20), mPaint);
        canvas.restore();

        for (int i = 0; i < mXAxisTexts.length; i++) {
            int currentX = (int) (mGraphStartX + i * mXPhaseWidth + (float) mXPhaseWidth / 2);
            int currentY = (int) (mGraphStartY - (mGraphStartY - mGraphEndY - dp2px(getContext(), 30)) * ((float) mValues[i]) / (mMaxYValue - mMinYValue));
            //画cursor
            canvas.drawLine(currentX, mGraphStartY, currentX, mGraphStartY + mCursorHeight,
                    mPaint);

            mPaint.getTextBounds(mXAxisTexts[i], 0, mXAxisTexts[i].length(), mBounds);
            //画X轴的字
            canvas.drawText(mXAxisTexts[i],
                    currentX - ((float) mBounds.width()) / 2, mGraphStartY + mCursorHeight + mTextMargin + mBounds.height(), mPaint);
        }
    }

    private void drawBars(Canvas canvas) {
        for (int i = 0; i < mXAxisTexts.length; i++) {
            int currentX = (int) (mGraphStartX + i * mXPhaseWidth + (float) mXPhaseWidth / 2);
            int currentY = (int) (mGraphStartY - (mGraphStartY - mGraphEndY - dp2px(getContext(), 30)) * ((float) mValues[i]) / (mMaxYValue - mMinYValue));

            //生成随机颜色
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            mBarPaint.setColor(Color.rgb(r,g,b));
            canvas.drawLine(currentX, mGraphStartY, currentX, currentY, mBarPaint);
        }
    }
}
