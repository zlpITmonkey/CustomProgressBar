package com.example.customprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RoundProgressBarWithProgress extends HorizontalProgressBar {
    private int mRadius = dpToPx(30);

    private int mMaxPaintWidth;

    public RoundProgressBarWithProgress(Context context) {
        this(context, null);
    }

    public RoundProgressBarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //第一步：获取自定义参数
    public RoundProgressBarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) 
    {
        super(context, attrs, defStyleAttr);
        obtainAttributeValue(attrs);
        //变得好看一些
        mReachHeight = mUnreachHeight * 2;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    //第二步：测量控件的大小
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxPaintWidth = Math.max(mReachHeight, mUnreachHeight);
//  控件的宽度(内圆的直径+已加载进度条的宽度
//  （已加载进度条有一半在内圆里面，一半在内圆外面，但是左右两边都有一半，所以为整个宽）+左右内边距)
        int expect = mRadius * 2 + mMaxPaintWidth + getPaddingRight() + getPaddingLeft();
        
   //resolveSize方法是根据父控件的三种模式下的情况，得出子控件的高度和宽度,与我们自己定义的测量是一样的
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);
        //从两者中选出一个小的，作为控件宽度（因为测量的值可能不同，但是我们绘制的是圆，需要宽高相同）
        int realWidth = Math.min(width, height);
  //计算内圆（就是文字所在的那个圆）的直径（控件的宽-左右内边距-已加载进度条宽
  //（已加载进度条有一半在内圆里面，一半在内圆外面，但是左右两边都有一半，所以为整个宽））
        mRadius = (realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;

        setMeasuredDimension(realWidth, realWidth);
    }

    private void obtainAttributeValue(AttributeSet attrs) 
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RoundProgressBarWithProgress);
        mRadius = (int) ta.getDimension(R.styleable.RoundProgressBarWithProgress_progress_radius, mRadius);
        ta.recycle();
    }
    
    
    //第三步：绘制圆形进度条
    
    //画外接正方形，从左上角开始所以，只需要右边和下边的长度（即为圆的直径）
    private RectF mRectF = new RectF(0, 0, mRadius * 2, mRadius * 2);

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text = getProgress() + "%";
        //计算文字高度以及宽度
        int textWidth = (int) mPaint.measureText(text);
        int textHeight = (int) ((mPaint.descent() + mPaint.ascent()));
        canvas.save();
        //移动坐标到外接正方形的左上角位置（画一个圆要先画出外接正方形）
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop() + mMaxPaintWidth / 2);
        //画unReachBar，圆形
        mPaint.setColor(mUnreachColor);
        mPaint.setStrokeWidth(mUnreachHeight);
        mPaint.setStyle(Paint.Style.STROKE);
      //画圆  第一个参数是圆的外接正方形，第二三个参数分别为其实度数以及要画的度数，第四个参数表示是否过圆心
        //因为开始画的位置为左上角，所以圆心的坐标xy即为圆半径
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        
        //画reachBar，弧形
        //计算要画出的弧度，进度条所占的百分比乘以360，就可以算出每个百分比所需的角度
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        //画圆  第一个参数是圆的外接正方形，第二三个参数分别为其实度数以及要画的度数，第四个参数表示是否过圆心
        canvas.drawArc(mRectF, 0, sweepAngle, false, mPaint);

        //画出文字
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        //绘制文字，开始坐标为文字的左边中心坐标
        canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight / 2, mPaint);
        canvas.restore();
    }
}
