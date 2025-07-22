package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleOverlayView extends View {
    private Paint circlePaint;
    private Paint rayPaint;
    private int rayCount = 40;
    private float radius = 200f;

    public CircleOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(4);
        circlePaint.setColor(Color.WHITE);

        rayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rayPaint.setStyle(Paint.Style.STROKE);
        rayPaint.setStrokeWidth(4);
        rayPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Draw circle
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // Draw rays
        for (int i = 0; i < rayCount; i++) {
            double angle = 2 * Math.PI * i / rayCount;
            float startX = (float) (centerX + radius * Math.cos(angle));
            float startY = (float) (centerY + radius * Math.sin(angle));
            float endX = (float) (centerX + (radius + 20) * Math.cos(angle));
            float endY = (float) (centerY + (radius + 20) * Math.sin(angle));
            canvas.drawLine(startX, startY, endX, endY, rayPaint);
        }
    }
}

