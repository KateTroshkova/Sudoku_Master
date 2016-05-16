package com.itschool.itprogect.sudoku_master;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class Grid extends View {

    enum STYLE{
        STYLE_BLACK,
        STYLE_WHITE
    }

    private STYLE style=STYLE.STYLE_BLACK;

    private int width=0;
    private int height=0;

    public Grid(Context context, STYLE style, int width, int height) {
        super(context);
        this.style=style;
        this.width=width;
        this.height=height;
    }

    public Grid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Grid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        width=(displaymetrics.widthPixels-90)/3;
        height=width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x=10;
        int y=15;
        Paint paint=new Paint();
        if(style==STYLE.STYLE_BLACK) paint.setColor(Color.BLACK);
        else {
            paint.setColor(Color.WHITE);
            x=0;
            y=0;
        }
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
