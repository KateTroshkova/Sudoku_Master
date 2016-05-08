package com.itschool.itprogect.sudoku_master;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class Grid extends View {
    public Grid(Context context) {
        super(context);
    }

    public Grid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Grid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int x=10;
        int y=0;
        int width=(displaymetrics.widthPixels-90)/3;
        int height=width;
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        canvas.drawLine(x, y, x+width*3, y, paint);
        canvas.drawLine(x, y+height, x+width*3, y+height, paint);
        canvas.drawLine(x, y+height*2, x+width*3, y+height*2, paint);
        canvas.drawLine(x, y+height*3, x+width*3, y+height*3, paint);
        canvas.drawLine(x, y, x, y+height*3, paint);
        canvas.drawLine(x+width, y, x+width, y+height*3, paint);
        canvas.drawLine(x+width*2, y, x+width*2, y+height*3, paint);
        canvas.drawLine(x+width*3, y, x+width*3, y+height*3, paint);
    }
}
