package com.example.asus.mystep;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Asus on 2016/6/3.
 *
 */

public class RoundProgressBar extends View {
    private Paint paint;
    private int roundColor;
    private int roundProgress;
    private int textColor;
    private float textSize;
    private float roundWidth;
    private int max;
    private int progress;
    private boolean textIsDisplay;
    private int style;
    private static final int STROKE=0;
    private static final int FILL=1;
    public RoundProgressBar(Context context) {
        super(context);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context context, AttributeSet attrs){
        paint=new Paint();
        TypedArray mTypeArray=context.obtainStyledAttributes(attrs,R.styleable.RoundProgressBar);
        roundColor=mTypeArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        roundProgress=mTypeArray.getColor(R.styleable.RoundProgressBar_roundProgressColor,Color.RED);
        textColor=mTypeArray.getColor(R.styleable.RoundProgressBar_textColor,Color.BLUE);
        textSize=mTypeArray.getDimension(R.styleable.RoundProgressBar_textSize,100);
        roundWidth=mTypeArray.getDimension(R.styleable.RoundProgressBar_roundWidth,5);
        max=mTypeArray.getInteger(R.styleable.RoundProgressBar_max,2000);
        style=mTypeArray.getInt(R.styleable.RoundProgressBar_style,0);
        textIsDisplay=mTypeArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable,true);
        mTypeArray.recycle();
    }
    public synchronized int getProgress() {
        return progress;
    }

    public synchronized void setProgress(int progress) {
        if(progress<0){
            throw new IllegalArgumentException("progress not less than 0");
        }
        if(progress>max){
            progress=max;
        }
        if (progress<=max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center=getWidth()/2;
        int radius= (int) (center-roundWidth/2);
        paint.setColor(roundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(center,center,radius,paint);
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent= (int) (((float)progress/(float)max)*100);
        float textWidth=paint.measureText(String.valueOf(progress));
        if (textIsDisplay && percent!=0 && style==STROKE) {
            canvas.drawText(String.valueOf(progress),center-textWidth/2,center+textWidth/2,paint);

        }

        paint.setStrokeWidth(roundWidth);
        paint.setColor(roundProgress);
        RectF oval=new RectF(center-radius,center-radius,center+radius,center+radius);
        switch (style){
            case STROKE:{
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval,-90,360*progress/max,false,paint);
                break;
            }
            case  FILL:{
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(progress!=0){
                    canvas.drawArc(oval,-90,360*progress/max,true,paint);
                    break;
                }
            }
        }
    }



    public synchronized int getMax() {
        return max;
    }

    /***
     * 设置进度的最大值
     * @param max
     */

    public synchronized void setMax(int max) {
        if(max<0){
            throw new IllegalArgumentException("progress not less than 0");
        }
        this.max = max;
    }

}
