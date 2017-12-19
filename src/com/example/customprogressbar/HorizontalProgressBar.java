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
    private static final int DEFAULT_HEIGHT_UNREACH = 3;    //�����Ҳ�������߶�
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 3;   //�������������߶�
    private static final int DEFAULT_TEXT_OFFSET = 10;   //�������ߵļ����ܺ�

    //�����Զ������Ե�Ĭ��ֵ
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

    //��һ������ȡ�Զ������
    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttrs(attrs);
        mPaint.setTextSize(mTextSize);
    }

    /**����Զ������*/
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

    /**�ڶ����������ؼ�
    1����.widthMeasureSpec;2����.heightMeasureSpec��
            �������������У����ǾͿ��Ի�ø��ؼ�����Ϊ�����Զ���ؼ����Ĳ��֣���ģʽ�Լ����Ĵ�С*/
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        //���߸��ؼ����ؼ��Ŀ��Ϊ�����趨�Ŀ��
        setMeasuredDimension(widthSize, height);

        //�����������Ŀ��Ϊ���ؼ��Ŀ�ȼ�ȥ���ҵ��ڱ߾�
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    //�����������ƽ�����
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //�ƶ���ʼ��������ΪxΪ��߾�Ľ���λ�ã�yΪ�������ߵ�һ�룬������λ��
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        boolean noNeedUnreach = false;
        //����������߽���
        //������ռ�İٷֱ�
        float radio = getProgress() * 1.0f / getMax();
        //������߽��ȵĿ�ȣ�������������ֵļ�ࣩ
        float progressX = radio * mRealWidth;
        //������ʾ���ֵ���ʽ
        String text = getProgress() + "%";
        //��ȡ���ֵĿ�ȣ����ʹ��ߵ�measureText�������Ի�ȡ���ֵĿ�ȣ�
        float textWidth = mPaint.measureText(text);
        //�����ֵĿ�Ⱥ�������߽��ȵĿ�ȴ��ڵ��ڿؼ�����ʵ���ʱ���ұ����ֵĿ�ȾͲ���Ҫ������
        if (textWidth + progressX >= mRealWidth) 
        {
        	//ͬʱ������ֽ��ȵ��ڿؼ���ʵ��ȼ�ȥ���ֵĿ�ȣ�ʵ���ϻ���������߼�ࣩ
            progressX = mRealWidth - textWidth;
            noNeedUnreach = true;
        }
        //������߽��Ƚ���λ�ã���Ϊ������߽��ȵ���ʵ��ȣ����Ǽ�ȥ������ֱ߾�Ŀ�ȣ�
        float endX = progressX - mTextOffset / 2;
       //�����ֵ����0ʱ����Ҫ����
        if (endX > 0) 
        {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }
        //��������
        mPaint.setColor(mTextColor);
        //���û��ʵĴ�ϸ
        mPaint.setStrokeWidth(mTextSize);
        mPaint.setAntiAlias(true);
        //�������ֽ�����λ��
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2) ;
        canvas.drawText(text, progressX, y, mPaint);

        //�����ұ����ֽ���
        if (!noNeedUnreach) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(Math.min(start, mRealWidth), 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();

    }

    //2.1��������ʵ�߶�
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        //��ȡ���ؼ���ģʽ�ʹ�С
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        
        //EXACTLY(��ȫ)����Ԫ�ؾ�����Ԫ�ص�ȷ�д�С����Ԫ�ؽ����޶��ڸ����ı߽���������������С��
        // �����ؼ��ĳ��ȺͿ����ȷ��ֵ����MATCH_PARENTʱ�������ģʽ
        if (mode == MeasureSpec.EXACTLY) 
     /**������ؼ��Ŀ����׼ȷֵ����ô���ǿؼ��Ĵ�С��Ϊ���ؼ���ֵ�������ӿؼ��Ĵ�С�ܴ�Ҳ��Ϊ���ؼ��Ĵ�С
     *�����ؼ�Ϊmatch_parent�Ļ����Զ���ؼ��Ĵ�С���������趨�ģ���Ϊ���ؼ��Ѿ�������ˣ��������ã����ڶ���ģʽ���ƣ�*/        {
            result = size;
        } 
        //������ǵ�һ��ģʽ���Ǿ͵��Լ������С����������»���һ��ģʽ��
        else {
			int textSize = (int) (mPaint.descent() - mPaint.ascent());
			//Math.max����ȡ������֮������ֵ��ֻ������������ʵ�߶ȣ����ȡС����ô���ֵ����ʾ������
			result = getPaddingTop()
					+ getPaddingBottom()
					+ Math.max(Math.max(mUnreachHeight, mReachHeight),
							Math.abs(textSize));
			
	   //AT_MOST(���ֵ����ģʽ)�������Ʒ�Χ�ڣ�һ��ָ���ؼ���С��Ҳ������Ļ��С���������������С������Ӧ��С
        //���ؼ��ĳ��ȺͿ����xml�е� wrap_contentʱ�������ģʽ
			if (mode == MeasureSpec.AT_MOST) 
			{
		      /**Math.min�����������ǣ�ȡ����֮�����Сֵ������ӿؼ����õ�ֵ�������ؼ���ֵ����ȡ���Ǹ��ؼ���ֵ
			   *���� wrap_content������Ӧ�ģ��ӿؼ���󣬸��ؼ����ж�������ӿؼ��������Ǹ�С��ֵ
			   *���������������˼�����ӿؼ��Ĵ�С���Լ�������Ҫ������������������ӿؼ���ֵ
			   *������ؼ��Ѿ�������ˣ������ӿؼ��ٴ�����ֵ���Ǹ��ؼ���ֵ����Ϊ��ʱ���ؼ����Сֵ�ˣ���������Ʒ�Χ��*/
				result = Math.min(size, result);
            }
        }
        return result;
    }

    //��Ϊϵͳʶ�����px,����Ҫ�Ѱ�sp��dp����ת��Ϊpx
    protected int spToPx(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

    protected int dpToPx(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }
}
