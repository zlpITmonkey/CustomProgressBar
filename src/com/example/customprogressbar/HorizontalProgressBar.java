package com.example.customprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class HorizontalProgressBar extends ProgressBar 
{
    private static final int DEFAULT_TEXT_SIZE = 10;   
    private static final int DEFAULT_TEXT_COLOR = 0xffFC00D1;
    private static final int DEFAULT_Color_UNREACH = 0xFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 3;    //文字右侧进度条高度
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 3;   //文字左侧进度条高度
    private static final int DEFAULT_TEXT_OFFSET = 10;   //文字两边的间距的总和

    //设置自定义属性的默认值
    protected int mTextSize = spToPx(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnreachHeight = dpToPx(DEFAULT_HEIGHT_UNREACH);
    protected int mUnreachColor = DEFAULT_Color_UNREACH;
    protected int mReachHeight = dpToPx(DEFAULT_HEIGHT_REACH);
    protected int mReachColor = DEFAULT_COLOR_REACH;
    protected int mTextOffset = dpToPx(DEFAULT_TEXT_OFFSET);

    protected Paint mPaint = new Paint();

    private int mRealWidth;

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //第一步：获取自定义参数
    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttrs(attrs);
        mPaint.setTextSize(mTextSize);
    }

    /**获得自定义参数*/
    private void obtainStyleAttrs(AttributeSet attrs) 
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        mTextColor = ta.getColor(R.styleable.HorizontalProgressBar_progress_text_color, mTextColor);
        mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressBar_progress_text_size, mTextSize);
        mReachColor = ta.getColor(R.styleable.HorizontalProgressBar_progress_reach_color, mReachColor);
        mReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBar_progress_reach_height, mReachHeight);
        mUnreachColor = ta.getColor(R.styleable.HorizontalProgressBar_progress_unreach_color, mUnreachColor);
        mUnreachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBar_progress_unreach_height, mUnreachHeight);
        ta.recycle();
    }

    /**第二步：测量控件
    1参数.widthMeasureSpec;2参数.heightMeasureSpec。
            从这两个参数中，我们就可以获得父控件（即为我们自定义控件外层的布局）的模式以及它的大小*/
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        //告诉父控件，控件的宽高为我们设定的宽高
        setMeasuredDimension(widthSize, height);

        //进度条真正的宽度为父控件的宽度减去左右的内边距
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    //第三步：绘制进度条
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //移动开始画的坐标为x为左边距的结束位置，y为进度条高的一半，即中心位置
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        boolean noNeedUnreach = false;
        //绘制文字左边进度
        //计算所占的百分比
        float radio = getProgress() * 1.0f / getMax();
        //文字左边进度的宽度（包含了左边文字的间距）
        float progressX = radio * mRealWidth;
        //设置显示文字的样式
        String text = getProgress() + "%";
        //获取文字的宽度（画笔工具的measureText方法可以获取文字的宽度）
        float textWidth = mPaint.measureText(text);
        //当文字的宽度和文字左边进度的宽度大于等于控件的真实宽度时，右边文字的宽度就不需要绘制了
        if (textWidth + progressX >= mRealWidth) 
        {
        	//同时左边文字进度等于控件真实宽度减去文字的宽度（实际上还包括了左边间距）
            progressX = mRealWidth - textWidth;
            noNeedUnreach = true;
        }
        //文字左边进度结束位置（即为文字左边进度的真实宽度，就是减去左边文字边距的宽度）
        float endX = progressX - mTextOffset / 2;
       //当这个值大于0时才需要绘制
        if (endX > 0) 
        {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }
        //绘制文字
        mPaint.setColor(mTextColor);
        //设置画笔的粗细
        mPaint.setStrokeWidth(mTextSize);
        mPaint.setAntiAlias(true);
        //绘制文字结束的位置
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2) ;
        canvas.drawText(text, progressX, y, mPaint);

        //绘制右边文字进度
        if (!noNeedUnreach) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(Math.min(start, mRealWidth), 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();

    }

    //2.1步测量真实高度
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        //获取父控件的模式和大小
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        
        //EXACTLY(完全)，父元素决定子元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
        // 当父控件的长度和宽度是确定值或者MATCH_PARENT时就是这个模式
        if (mode == MeasureSpec.EXACTLY) 
     /**如果父控件的宽高是准确值，那么我们控件的大小即为父控件的值，即便子控件的大小很大，也是为父控件的大小
     *（父控件为match_parent的话，自定义控件的大小可以自由设定的，因为父控件已经是最大了，这种设置，跟第二种模式类似）*/        {
            result = size;
        } 
        //如果不是第一种模式，那就得自己定义大小（这种情况下还有一种模式）
        else {
			int textSize = (int) (mPaint.descent() - mPaint.ascent());
			//Math.max（）取里面数之间的最大值（只有最大才满足真实高度，如果取小，那么大的值便显示不出）
			result = getPaddingTop()
					+ getPaddingBottom()
					+ Math.max(Math.max(mUnreachHeight, mReachHeight),
							Math.abs(textSize));
			
	   //AT_MOST(最大值测量模式)：在限制范围内（一般指父控件大小，也就是屏幕大小），可以是任意大小。自适应大小
        //父控件的长度和宽度是xml中的 wrap_content时就是这个模式
			if (mode == MeasureSpec.AT_MOST) 
			{
		      /**Math.min（）方法就是，取两者之间的最小值，如果子控件设置的值超过父控件的值，则取我们父控件的值
			   *但是 wrap_content是自适应的，子控件多大，父控件就有多大，所以子控件往往是那个小的值
			   *所以这个方法的意思就是子控件的大小由自己定，但要控制下面这种情况下子控件的值
			   *如果父控件已经是最大了，即便子控件再大最后的值还是父控件的值（因为这时父控件变成小值了，这便是限制范围）*/
				result = Math.min(size, result);
            }
        }
        return result;
    }

    //因为系统识别的是px,所以要把把sp和dp，都转换为px
    protected int spToPx(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

    protected int dpToPx(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }
}
