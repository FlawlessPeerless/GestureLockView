package com.magic.su.gesturelockview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.magic.su.gesturelockview.entity.GesturePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * project: MagicAssistant
 * package: com.magicsu.android.magicassistant.view
 * file: GestureLockView
 * author: admin
 * date: 2018/2/8
 * description: 手势解锁控件
 */

public class GestureLockView extends View {
    private final int POINT_COUNT = 9;
    private Context mContext;
    private int mGridSize;  // 每个格子的长宽
    private List<GesturePoint> mPointList;
    private Paint mGesturePointDrawer;
    private Paint mLineDrawer;
    private Path mLinePath;
    private List<Integer> mCheckedPositionList; // 被选中的point位置集合
    private Point mFingerPoint; // 手指位置

    public GestureLockView(Context context) {
        super(context);
        initView(context);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, w, height, h;
        width = w = MeasureSpec.getSize(widthMeasureSpec);
        height = h = MeasureSpec.getSize(heightMeasureSpec);
        if (h < w) {
            width = h;
        } else {
            height = w;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mGridSize = w / 3;
        resetGesturePoints();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pos = getTouchedGrid(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (inspectPointState(event, mPointList.get(pos))) updateGestureState(pos);
                break;
            case MotionEvent.ACTION_MOVE:
                if (pos != -1) {
                    if (inspectPointState(event, mPointList.get(pos))) updateGestureState(pos);
                }
                break;
            case MotionEvent.ACTION_UP:
                initState();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        for (GesturePoint point : mPointList) {
            point.draw(canvas, mGesturePointDrawer);
        }
    }

    private void initView(Context context) {
        mContext = context;
        mPointList = new ArrayList<>();
        mCheckedPositionList = new ArrayList<>();
        mFingerPoint = new Point(0, 0);

        mGesturePointDrawer = new Paint();
        mLineDrawer = new Paint();
        mLinePath = new Path();
        mGesturePointDrawer.setAntiAlias(true);
        mLineDrawer.setAntiAlias(true);
        setLineStyle(mLineDrawer);
    }

    /**
     * 恢复默认状态
     */
    private void initState() {
        mCheckedPositionList.clear();
        for (GesturePoint point : mPointList) {
            point.setState(GesturePoint.STATE.POINT_UNCHECKED);
        }
    }

    /**
     * 重置point集合的位置
     */
    private void resetGesturePoints() {
        for (int i = 0; i < POINT_COUNT; i++) {
            GesturePoint point;
            int centerX = (i % 3) * mGridSize + (mGridSize / 2);
            int centerY = (i / 3) * mGridSize + (mGridSize / 2);
            if (mPointList.size() < POINT_COUNT) {
                point = new GesturePoint();
                mPointList.add(point);
            } else {
                point = mPointList.get(i);
            }
            point.setDiameter((int) (mGridSize * 0.6));
            point.setX(centerX);
            point.setY(centerY);
        }
    }

    private void updateGestureState(int pos) {
        GesturePoint point = mPointList.get(pos);
        if (point.getState() == GesturePoint.STATE.POINT_CHECKED) return;
        point.setState(GesturePoint.STATE.POINT_CHECKED);
        mCheckedPositionList.add(pos);
    }

    /**
     * 获取当前手指所在view的位置
     * @param event 事件对象
     */
    private int getTouchedGrid(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        mFingerPoint.set(x, y);
        int row = y / mGridSize;
        int col = x / mGridSize;
        int pos = row * 3 + col;
        if (pos < 0 || pos >= POINT_COUNT) return -1;
        return pos;
    }

    /**
     * 检查当前手指位置是否在点内
     * @param event 事件对象
     * @param point 手势点对象
     * @return boolean
     */
    private boolean inspectPointState(MotionEvent event, GesturePoint point) {
        int x = (int) Math.abs(event.getX() - point.getX());
        int y = (int) Math.abs(event.getY() - point.getY());
        double distance = Math.sqrt(x * x + y * y);
        return distance <= point.getDiameter() / 2;
    }


    /**
     * 绘制线条
     * @param canvas 画布对象
     */
    private void drawLine(Canvas canvas) {
        if (mCheckedPositionList.isEmpty()) return;
        mLinePath.reset();
        for (int i = 0; i < mCheckedPositionList.size(); i++) {

            GesturePoint point = mPointList.get(mCheckedPositionList.get(i));
            if (i == 0) {
                mLinePath.moveTo(point.getX(), point.getY());
            } else {
                mLinePath.lineTo(point.getX(), point.getY());
            }
        }
        mLinePath.lineTo(mFingerPoint.x, mFingerPoint.y);
        canvas.drawPath(mLinePath, mLineDrawer);
    }

    /**
     * 设置线条样式
     * @param lineDrawer 线条画笔
     */
    protected void setLineStyle(Paint lineDrawer) {
        lineDrawer.setColor(Color.RED);
        lineDrawer.setStyle(Paint.Style.STROKE);
        lineDrawer.setStrokeWidth(8);
    }
}
