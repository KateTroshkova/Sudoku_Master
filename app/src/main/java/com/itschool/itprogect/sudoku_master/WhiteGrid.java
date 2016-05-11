package com.itschool.itprogect.sudoku_master;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class WhiteGrid extends View {

    private int width;
    private int height;
    private int x=0;
    private int y=0;

    public WhiteGrid(Context context, Bitmap bitmap) {
        super(context);
    }

    public WhiteGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WhiteGrid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setWidth(int width){
        this.width=width;
    }

    public void setHeight(int height){
        this.height=height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
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
