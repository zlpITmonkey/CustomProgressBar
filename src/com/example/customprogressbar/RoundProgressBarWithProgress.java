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

    //��һ������ȡ�Զ������
    public RoundProgressBarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) 
    {
        super(context, attrs, defStyleAttr);
        obtainAttributeValue(attrs);
        //��úÿ�һЩ
        mReachHeight = mUnreachHeight * 2;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    //�ڶ����������ؼ��Ĵ�С
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxPaintWidth = Math.max(mReachHeight, mUnreachHeight);
//  �ؼ��Ŀ��(��Բ��ֱ��+�Ѽ��ؽ������Ŀ��
//  ���Ѽ��ؽ�������һ������Բ���棬һ������Բ���棬�����������߶���һ�룬����Ϊ������+�����ڱ߾�)
        int expect = mRadius * 2 + mMaxPaintWidth + getPaddingRight() + getPaddingLeft();
        
   //resolveSize�����Ǹ��ݸ��ؼ�������ģʽ�µ�������ó��ӿؼ��ĸ߶ȺͿ��,�������Լ�����Ĳ�����һ����
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);
        //��������ѡ��һ��С�ģ���Ϊ�ؼ���ȣ���Ϊ������ֵ���ܲ�ͬ���������ǻ��Ƶ���Բ����Ҫ�����ͬ��
        int realWidth = Math.min(width, height);
  //������Բ�������������ڵ��Ǹ�Բ����ֱ�����ؼ��Ŀ�-�����ڱ߾�-�Ѽ��ؽ�������
  //���Ѽ��ؽ�������һ������Բ���棬һ������Բ���棬�����������߶���һ�룬����Ϊ��������
        mRadius = (realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;

        setMeasuredDimension(realWidth, realWidth);
    }

    private void obtainAttributeValue(AttributeSet attrs) 
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RoundProgressBarWithProgress);
        mRadius = (int) ta.getDimension(R.styleable.RoundProgressBarWithProgress_progress_radius, mRadius);
        ta.recycle();
    }
    
    
    //������������Բ�ν�����
    
    //����������Σ������Ͻǿ�ʼ���ԣ�ֻ��Ҫ�ұߺ��±ߵĳ��ȣ���ΪԲ��ֱ����
    private RectF mRectF = new RectF(0, 0, mRadius * 2, mRadius * 2);

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text = getProgress() + "%";
        //�������ָ߶��Լ����
        int textWidth = (int) mPaint.measureText(text);
        int textHeight = (int) ((mPaint.descent() + mPaint.ascent()));
        canvas.save();
        //�ƶ����굽��������ε����Ͻ�λ�ã���һ��ԲҪ�Ȼ�����������Σ�
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop() + mMaxPaintWidth / 2);
        //��unReachBar��Բ��
        mPaint.setColor(mUnreachColor);
        mPaint.setStrokeWidth(mUnreachHeight);
        mPaint.setStyle(Paint.Style.STROKE);
      //��Բ  ��һ��������Բ����������Σ��ڶ����������ֱ�Ϊ��ʵ�����Լ�Ҫ���Ķ��������ĸ�������ʾ�Ƿ��Բ��
        //��Ϊ��ʼ����λ��Ϊ���Ͻǣ�����Բ�ĵ�����xy��ΪԲ�뾶
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        
        //��reachBar������
        //����Ҫ�����Ļ��ȣ���������ռ�İٷֱȳ���360���Ϳ������ÿ���ٷֱ�����ĽǶ�
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mReachHeight);
        //��Բ  ��һ��������Բ����������Σ��ڶ����������ֱ�Ϊ��ʵ�����Լ�Ҫ���Ķ��������ĸ�������ʾ�Ƿ��Բ��
        canvas.drawArc(mRectF, 0, sweepAngle, false, mPaint);

        //��������
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        //�������֣���ʼ����Ϊ���ֵ������������
        canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight / 2, mPaint);
        canvas.restore();
    }
}
