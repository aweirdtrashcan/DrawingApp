package com.stimply.drawingapp.presentation.component;

import static com.stimply.drawingapp.presentation.util.AppUtil.showSnackbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.stimply.drawingapp.data.local.SaveImageThread;

import java.util.ArrayList;

public class DrawingView extends View {
    public DrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupDrawing();
    }

    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }

    private CustomPath drawPath;
    private ArrayList<CustomPath> drawPaths = new ArrayList<>();
    private Bitmap canvasBitmap;
    private Paint drawPaint;
    private Paint canvasPaint;
    private Float brushSize = 20.0f;
    private Integer color = Color.BLACK;
    private Canvas canvas;

    private void setupDrawing() {
        drawPaint = new Paint();
        drawPaint.setColor(color);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        drawPath = new CustomPath(color, brushSize);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0.0f, 0.0f, canvasPaint);
        for (int i = 0; i < drawPaths.size(); i++) {
            CustomPath thisPath = drawPaths.get(i);
            drawPaint.setColor(thisPath.getColor());
            drawPaint.setStrokeWidth(thisPath.getBrushThickness());
            canvas.drawPath(thisPath, drawPaint);
        }
        if (!drawPath.isEmpty()) {
            drawPaint.setColor(drawPath.getColor());
            drawPaint.setStrokeWidth(drawPath.getBrushThickness());
            canvas.drawPath(drawPath, drawPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                drawPath.moveTo(touchX, touchY);
                break;
            }
            case MotionEvent.ACTION_UP: {
                drawPaths.add(drawPath);
                drawPath = new CustomPath(color, brushSize);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                drawPath.lineTo(touchX, touchY);
                break;
            }
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setSizeForBrush(Float newSize) {
        brushSize = getResources().getDisplayMetrics().density * newSize;
        drawPath.setBrushThickness(brushSize);
    }

    public void setColor(String colorName) {
        Integer color = Color.parseColor(colorName);
        drawPath.setColor(color);
        this.color = color;
    }

    public void undo() {
        try {
            drawPaths.remove(drawPaths.size() - 1);
            invalidate();
        } catch (IndexOutOfBoundsException e) {
            showSnackbar(this, "Nothing to be undoned");
        }
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        Rect destRect = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(bitmap, null, destRect, canvasPaint);
    }

    public Bitmap getViewAsBitmap() {
        Bitmap returnBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(returnBitmap);
        draw(targetCanvas);
        return returnBitmap;
    }

    static private class CustomPath extends Path {
        public CustomPath(Integer color, Float brushThickness) {
            super();
            this.color = color;
            this.brushThickness = brushThickness;
        }

        @ColorInt
        private Integer color;
        private Float brushThickness;

        @ColorInt
        public Integer getColor() {
            return color;
        }

        public void setColor(@ColorInt Integer color) {
            this.color = color;
        }

        public Float getBrushThickness() {
            return brushThickness;
        }

        public void setBrushThickness(Float brushThickness) {
            this.brushThickness = brushThickness;
        }
    }
}
