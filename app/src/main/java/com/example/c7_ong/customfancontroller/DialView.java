package com.example.c7_ong.customfancontroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class DialView extends View{
    private static int SELECTION_COUNT = 4;
    private float mWidth;
    private float mHeight;
    private Paint mTextPaint;
    private Paint mDialPaint;
    private float mRadius;
    private int mActiveSelection;
    private int mFanOnColor;
    private int mFanOffColor;
    //string buffer for dial labels, float for computeXY result
    private final StringBuffer mTempLabel = new StringBuffer(0);
    private final float[] mTempResult = new float[2];

    //standard constructor for the class
    public DialView(Context context) {
        super(context);
        init(context, null);
    }

    //constructor is called when a view is built from an XML file
    public DialView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    //constructor is called to supply the default style
    public DialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //this function is used to initialize instance variables, it is called by constructors
    private void init(Context context, AttributeSet attrs)
    {

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextSize(40f);
        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFanOffColor = Color.CYAN;
        mFanOnColor = Color.GRAY;
        mDialPaint.setColor(mFanOffColor);
        //Initialize current selection
        mActiveSelection = 0;

        if (attrs != null)
        {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DialView, 0,0);
            mFanOnColor = typedArray.getColor(R.styleable.DialView_fanOnColor, mFanOnColor);
            mFanOffColor = typedArray.getColor(R.styleable.DialView_fanOffColor, mFanOffColor);
            typedArray.recycle();
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActiveSelection = (mActiveSelection + 1) % SELECTION_COUNT;
                if (mActiveSelection >= 1)
                {
                    mDialPaint.setColor(mFanOnColor);
                }
                else
                {
                    mDialPaint.setColor(mFanOffColor);
                }
                invalidate();
            }

        });
    }

    //called during layout when the size of the view has changed
    //w = current width
    //h = current height
    //oldw = old width
    //oldh = old height
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        mWidth = w;
        mHeight = h;
        mRadius = (float) (Math.min(mWidth, mHeight)/2*0.8);
    }

    //gets the X/Y coordinates for a label or indicator
    private float[] computeXYForPosition(final int pos, final float radius)
    {
        float[] result = mTempResult;
        Double startAngle = Math.PI * (9/8d);
        Double angle = startAngle + (pos * (Math.PI / 4));
        result[0] = (float) (radius * Math.cos(angle)) + (mWidth/2);
        result[1] = (float) (radius * Math.sin(angle)) + (mHeight/2);
        return result;
    }

    //render the view content
    @Override
    protected void onDraw (Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawCircle(mWidth/2, mHeight/2, mRadius, mDialPaint);
        final float labelRadius = mRadius + 20;
        StringBuffer label = mTempLabel;
        for (int i = 0; i < SELECTION_COUNT; i++)
        {
            float[] xyData = computeXYForPosition(i, labelRadius);
            float x = xyData[0];
            float y = xyData[1];
            label.setLength(0);
            label.append(i);
            canvas.drawText(label, 0, label.length(), x, y, mTextPaint);
        }

        final float markerRadius = mRadius - 35;
        float[] xyData = computeXYForPosition(mActiveSelection, markerRadius);
        float x = xyData[0];
        float y = xyData[1];
        canvas.drawCircle(x,y,20,mTextPaint);
    }

}
