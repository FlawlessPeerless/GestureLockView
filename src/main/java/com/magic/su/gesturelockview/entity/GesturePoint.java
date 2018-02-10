package com.magic.su.gesturelockview.entity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * project: MagicAssistant
 * package: com.magicsu.android.magicassistant.view
 * file: GestureLockView
 * author: admin
 * date: 2018/2/8
 * description: 手势圆点
 */

public class GesturePoint {
    public enum STATE {
        POINT_CHECKED,
        POINT_UNCHECKED
    }

    private STATE state;
    private int x;
    private int y;
    private int diameter;

    public GesturePoint() {
        state = STATE.POINT_UNCHECKED;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return this.state;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (state == STATE.POINT_CHECKED) {
            drawChecked(canvas, paint);
        } else {
            drawUnChecked(canvas, paint);
        }
    }

    protected void drawChecked(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, diameter / 2, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, (float) (diameter / 2 * 0.6), paint);
    }

    protected void drawUnChecked(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        canvas.drawCircle(x, y, diameter / 2, paint);
    }
}
