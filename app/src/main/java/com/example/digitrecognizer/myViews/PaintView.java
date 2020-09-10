package com.example.digitrecognizer.myViews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.example.digitrecognizer.R;

public class PaintView extends androidx.appcompat.widget.AppCompatImageView {

    private Paint brush;
    private Path drawing;

    public PaintView(Context context) {
        super(context);
        init(null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(@Nullable AttributeSet set) {
        drawing = new Path();

        brush = new Paint(Paint.ANTI_ALIAS_FLAG);
        brush.setStrokeWidth(10f);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStyle(Paint.Style.STROKE);
        brush.setColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float X = event.getX();
        float Y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawing.moveTo(X, Y);
                return true;
            case MotionEvent.ACTION_MOVE:
                ( (Activity) getContext()).findViewById(R.id.button_convert).setEnabled(true);
                drawing.lineTo(X, Y);
                break;
            default:
                return false;
        }

        postInvalidate();
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(drawing, brush);
    }

    public int retWidth() {
        return getWidth();
    }

    public int retHeight() {
        return getHeight();
    }

    public void clear() {
        drawing.reset();
        postInvalidate();
    }
}
