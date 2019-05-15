package com.vvsemir.kindaimageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class CircleDrawable extends Drawable {
    protected float radius;

    protected final RectF mRect = new RectF();
    protected final RectF mBitmapRect;
    protected final BitmapShader bitmapShader;
    protected final Paint paint;
    protected final Paint strokePaint;
    protected final float strokeWidth;
    protected float strokeRadius;

    public CircleDrawable(Bitmap bitmap, Integer strokeColor, float strokeWidth) {
        radius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2;

        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        if (strokeColor == null) {
            strokePaint = null;
        } else {
            strokePaint = new Paint();
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(strokeColor);
            strokePaint.setStrokeWidth(strokeWidth);
            strokePaint.setAntiAlias(true);
        }
        this.strokeWidth = strokeWidth;
        strokeRadius = radius - strokeWidth / 2;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRect.set(0, 0, bounds.width(), bounds.height());
        radius = Math.min(bounds.width(), bounds.height()) / 2;
        strokeRadius = radius - strokeWidth / 2;

        // Resize the original bitmap to fit the new bound
        Matrix shaderMatrix = new Matrix();
        shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
        bitmapShader.setLocalMatrix(shaderMatrix);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(radius, radius, radius, paint);
        if (strokePaint != null) {
            canvas.drawCircle(radius, radius, strokeRadius, strokePaint);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }
}
