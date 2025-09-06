package com.skysoftsolution.basictoadavance.documentViewer.helperClasses;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {
    private Matrix matrix = new Matrix();
    private float[] mValues = new float[9];
    private float minScale = 1f, maxScale = 5f;
    private float saveScale = 1f;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private PointF last = new PointF();
    private boolean isDragging = false;

    public ZoomableImageView(Context c) { super(c); init(c); }
    public ZoomableImageView(Context c, AttributeSet a) { super(c, a); init(c); }
    public ZoomableImageView(Context c, AttributeSet a, int s) { super(c, a, s); init(c); }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        matrix.reset();
        setImageMatrix(matrix);

        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                float newScale = saveScale * scaleFactor;
                if (newScale < minScale) scaleFactor = minScale / saveScale;
                if (newScale > maxScale) scaleFactor = maxScale / saveScale;
                matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                saveScale *= scaleFactor;
                setImageMatrix(matrix);
                return true;
            }
        });

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onDoubleTap(MotionEvent e) {
                // toggle zoom
                float target = (saveScale < 2f) ? 2f : 1f;
                float factor = target / saveScale;
                matrix.postScale(factor, factor, e.getX(), e.getY());
                saveScale = target;
                centerImage();
                setImageMatrix(matrix);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                last.set(event.getX(), event.getY());
                isDragging = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging && event.getPointerCount() == 1) {
                    float dx = event.getX() - last.x;
                    float dy = event.getY() - last.y;
                    matrix.postTranslate(dx, dy);
                    last.set(event.getX(), event.getY());
                    fixTranslation();
                    setImageMatrix(matrix);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                centerImage();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerImage();
    }

    public void resetToFit() {
        // Fit the image to view bounds (called after setting bitmap)
        saveScale = 1f;
        matrix.reset();
        setImageMatrix(matrix);
        post(this::centerImage);
    }

    private void fixTranslation() {
        getImageMatrix().getValues(mValues);
        float transX = mValues[Matrix.MTRANS_X];
        float transY = mValues[Matrix.MTRANS_Y];

        float[] bounds = getImageBounds();
        float fixTransX = getFixTranslation(transX, bounds[0], bounds[2], getWidth());
        float fixTransY = getFixTranslation(transY, bounds[1], bounds[3], getHeight());
        matrix.postTranslate(fixTransX, fixTransY);
    }

    private float[] getImageBounds() {
        float[] vals = new float[9];
        matrix.getValues(vals);
        float scaleX = vals[Matrix.MSCALE_X];
        float scaleY = vals[Matrix.MSCALE_Y];
        float transX = vals[Matrix.MTRANS_X];
        float transY = vals[Matrix.MTRANS_Y];

        float width = getDrawable() != null ? getDrawable().getIntrinsicWidth() * scaleX : 0;
        float height = getDrawable() != null ? getDrawable().getIntrinsicHeight() * scaleY : 0;
        return new float[]{transX, transY, width, height};
    }

    private float getFixTranslation(float trans, float contentPos, float contentSize, float viewSize) {
        float minTrans, maxTrans;
        if (contentSize <= viewSize) {
            minTrans = (viewSize - contentSize) / 2f - contentPos;
            maxTrans = minTrans;
        } else {
            minTrans = viewSize - contentSize - contentPos;
            maxTrans = -contentPos;
        }
        if (trans < minTrans) return minTrans - trans;
        if (trans > maxTrans) return maxTrans - trans;
        return 0;
    }

    private void centerImage() {
        if (getDrawable() == null) return;
        getImageMatrix().getValues(mValues);
        fixTranslation();
        setImageMatrix(matrix);
    }
}
