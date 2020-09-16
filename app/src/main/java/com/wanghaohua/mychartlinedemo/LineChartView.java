package com.wanghaohua.mychartlinedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.wanghaohua.mychartlinedemo.util.DensityUtil.dp2px;

/**
 * Created by wanghaohua on 2020/9/1
 */
public class LineChartView extends View {

    private int mHeight;
    private int mWidth;
    //纵轴最大值
    private int mMaxYValue;
    //纵轴最小值
    private int mMinYValue;
    //Y分段数
    private int mYPhases = 10;
    //文字margin
    private int mTextMargin;
    //x轴游标高度
    private int mCursorHeight;
    //横轴每段长度
    private int mXPhaseWidth;
    //横轴
    private String[] mXAxisTexts = {"7月", "8月", "9月", "10月", "11月", "12月"};
    //点数据
    private int[] mValues = {0, 200, 481, 350, 600, 253};

    private int mPadding;
    //画字和线
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //画虚线
    private Paint mDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //画连接线
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //画点
    private Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath;

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

    private List<Point> mPoints = new ArrayList<>();
    private boolean mShowReticle = false;
    private float mPressedX = -1;
    private Runnable mHideReticleRunnable;

    public LineChartView(Context context) {
        super(context);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        int max = getMax(mValues);
        int min = getMin(mValues);
        mMaxYValue = max - min + (max - min) / 10;
        mMinYValue = min;

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

        PathEffect pathEffect = new DashPathEffect(new float[]{20, 20}, 0);
        mDashPaint.setColor(Color.LTGRAY);
        mDashPaint.setPathEffect(pathEffect);
        mDashPaint.setStrokeWidth(3);
        mDashPaint.setStyle(Paint.Style.STROKE);

        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.BLUE);
        mLinePaint.setStrokeWidth(1);

        mPointPaint.setColor(Color.GREEN);

        mPath = new Path();

        mHideReticleRunnable = new Runnable() {
            @Override
            public void run() {
                mShowReticle = false;
                invalidate();
            }
        };
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        mXPhaseWidth = (int) ((mWidth - 2 * mPadding - mMaxYTextWidth) / mXAxisTexts.length);

        mGraphStartY = mHeight - mPadding - mXTextHeight - mMaxYTextHeight / 2;
        mGraphEndY = mPadding - mMaxYTextHeight / 2;
        mGraphStartX = mPadding + mMaxYTextWidth + mTextMargin;
        mGraphEndX = mWidth - mPadding;

        for (int i = 0; i < mXAxisTexts.length; i++) {
            int currentX = (int) (mGraphStartX + i * mXPhaseWidth + (float) mXPhaseWidth / 2);
            int currentY = (int) (mGraphStartY - (mGraphStartY - mGraphEndY) * ((float) mValues[i]) / (mMaxYValue - mMinYValue));
            mPoints.add(new Point(currentX, currentY));
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mShowReticle = true;
                mPressedX = x;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mPressedX = x;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                postDelayed(mHideReticleRunnable, 1000);
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawYAxis(canvas);
        drawXAxis(canvas);
        drawPoints(canvas);
        drawLines(canvas);
        drawReticle(canvas);
    }

    private void drawYAxis(Canvas canvas) {
        int phaseValueUnit = (mMaxYValue - mMinYValue) / mYPhases;

        //画Y轴
        canvas.drawLine(mGraphStartX, mGraphStartY,
                mGraphStartX, mGraphEndY, mPaint);

        //画Y轴的每一段
        for (int i = 0; i <= mYPhases; i++) {
            float currentY = mGraphStartY - (mGraphStartY - mGraphEndY) * i / mYPhases;
            String currentText = String.valueOf(mMinYValue + i * phaseValueUnit);

            mPaint.getTextBounds(currentText, 0, currentText.length(), mBounds);
            //画标尺
            canvas.drawText(currentText,
                    mGraphStartX - mTextMargin - mBounds.width(), currentY + ((float) mBounds.height()) / 2, mPaint);

            //画横向虚线
            if (i != 0) {
                mPath.moveTo(mGraphStartX, currentY);
                mPath.lineTo(mGraphEndX, currentY);
                canvas.drawPath(mPath, mDashPaint);
                mPath.reset();
            }
        }
    }

    private void drawXAxis(Canvas canvas) {
        //画X轴
        canvas.drawLine(mGraphStartX, mGraphStartY, mGraphEndX, mGraphStartY, mPaint);
        for (int i = 0; i < mPoints.size(); i++) {
            Point point = mPoints.get(i);
            //画cursor
            canvas.drawLine(point.x, mGraphStartY, point.x, mGraphStartY + mCursorHeight,
                    mPaint);

            mPaint.getTextBounds(mXAxisTexts[i], 0, mXAxisTexts[i].length(), mBounds);
            //画X轴的字
            canvas.drawText(mXAxisTexts[i],
                    point.x - ((float) mBounds.width()) / 2, mGraphStartY + mCursorHeight + mTextMargin + mBounds.height(), mPaint);
        }
    }

    private void drawPoints(Canvas canvas) {
        for (int i = 0; i < mPoints.size(); i++) {
            Point point = mPoints.get(i);
            //画点
            mPointPaint.setStyle(Paint.Style.STROKE);
            mPointPaint.setColor(Color.GREEN);
            canvas.drawCircle(point.x, point.y, dp2px(getContext(), 5), mPointPaint);
            mPointPaint.setStyle(Paint.Style.FILL);
            mPointPaint.setColor(Color.GREEN);
            canvas.drawCircle(point.x, point.y, dp2px(getContext(), 2), mPointPaint);
        }
    }

    private void drawLines(Canvas canvas) {
        for (int i = 0; i < mPoints.size(); i++) {
            Point point = mPoints.get(i);
            //用path保存每个点的连线
            if (i != 0) {
                mPath.lineTo(point.x, point.y);
            } else {
                mPath.moveTo(point.x, point.y);
            }
        }
        canvas.drawPath(mPath, mLinePaint);
        mPath.reset();
    }

    private void drawReticle(Canvas canvas) {
        if (!mShowReticle || mPressedX < 0) {
            return;
        }

        Point nearestPoint = null;
        for (Point point : mPoints) {
            if (nearestPoint == null && Math.abs(point.x - mPressedX) < mXPhaseWidth) {
                nearestPoint = point;
            } else if (nearestPoint != null && Math.abs(point.x - mPressedX) < Math.abs(nearestPoint.x - mPressedX)) {
                nearestPoint = point;
            }
        }
        if (nearestPoint != null) {
            canvas.drawLine(mGraphStartX, nearestPoint.y, mGraphEndX, nearestPoint.y, mPaint);
            canvas.drawLine(nearestPoint.x, mGraphStartY, nearestPoint.x, mGraphEndY, mPaint);
            mPointPaint.setStyle(Paint.Style.STROKE);
            mPointPaint.setColor(Color.BLACK);
            canvas.drawCircle(nearestPoint.x, nearestPoint.y, dp2px(getContext(), 5), mPointPaint);
            mPointPaint.setStyle(Paint.Style.FILL);
            mPointPaint.setColor(Color.WHITE);
            canvas.drawCircle(nearestPoint.x, nearestPoint.y, dp2px(getContext(), 5), mPointPaint);

            String text = String.valueOf(mValues[mPoints.indexOf(nearestPoint)]);
            canvas.drawText(text, nearestPoint.x + mTextMargin, nearestPoint.y, mPaint);
        }
    }
}
