package com.jasper.drag.dragdemo.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


/**
 * SquareLayout to make sure gridview item in square.
 *
 * @author jasper
 */
public class SquareLayout extends RelativeLayout {


    /**
     * constructor.
     *
     * @param context context
     * @param attrs   attrs
     */
    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * constructor.
     *
     * @param context context
     */
    public SquareLayout(Context context) {
        super(context);
    }

    @SuppressWarnings("unused")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // For simple implementation, or internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view. We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
//        
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
//        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY));
    }
}


