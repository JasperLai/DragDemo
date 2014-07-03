package com.jasper.drag.dragdemo.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This class describes an area within a DragLayer where a dragged item can be
 * dropped in order to remove it from the screen. It is a subclass of ImageView
 * so it is easy to make the area appear as a trash icon or whatever you like.
 * <p/>
 * <p/>
 * The default implementation assumes that the ImageView supports image levels.
 * Image level 1 is the normal view. Level 2 is for use when the DeleteZone has
 * a dragged object over it. To change that behavior, override methods
 * onDragEnter and onDragExit.
 */

public class DeleteZone extends ImageView implements DropTarget {
    int mWidth = -1;
    int mHeight = -1;

    private static final int ANIMATION_DURATION = 500;

    /**
     * @param context a context
     */
    public DeleteZone(Context context) {
        super(context);
    }

    /**
     * @param context a context
     * @param w       width
     * @param h       height
     */
    public DeleteZone(Context context, int w, int h) {
        super(context);
        mWidth = w;
        mHeight = h;
    }

    /**
     * @param context a context
     * @param attrs   attrs
     */
    public DeleteZone(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     */
    // Instance Variables

    private DragController mDragController;
    private boolean mEnabled = true;

    /**
     */
    // Properties

    /**
     * Get the value of the DragController property.
     *
     * @return DragController
     */

    public DragController getDragController() {
        return mDragController;
    } // end getDragController

    /**
     * Set the value of the DragController property.
     *
     * @param newValue DragController
     */

    public void setDragController(DragController newValue) {
        mDragController = newValue;
    } // end setDragController

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mWidth != -1 && mHeight != -1) {
            this.setMeasuredDimension(mWidth, mHeight);
        }
    }

    /**
     */
    // DropTarget interface implementation

    /**
     * Handle an object being dropped on the DropTarget. For a DeleteZone, we
     * don't really do anything because we want the view being dragged to
     * vanish.
     *
     * @param source   DragSource where the drag started
     * @param x        X coordinate of the drop location
     * @param y        Y coordinate of the drop location
     * @param xOffset  Horizontal offset with the object being dragged where the
     *                 original touch happened
     * @param yOffset  Vertical offset with the object being dragged where the
     *                 original touch happened
     * @param dragInfo Data associated with the object being dragged
     */
    public void onDrop(DragSource source, int x, int y, int xOffset,
                       int yOffset, Object dragInfo) {
        // Bitmap bitgetViewBitmap(this);
        clearAnimation();

    }

    /**
     * React to a dragged object entering the area of this DeleteZone. Provide
     * the user with some visual feedback.
     *
     * @param source
     * where this drag action originated
     * @param x
     * current x position of drag item
     * @param y
     * current y position of drag item
     * @param xOffset
     * the x distance from where this drag action originated
     * @param yOffset
     * the y distance from where this drag action originated
     * @param dragInfo
     * the drag item
     */
    private static final int DEFAULT_DURATION = 200;

    public void onDragEnter(DragSource source, int x, int y, int xOffset,
                            int yOffset, Object dragInfo) {
        // TODO add deletezone animation here
//		 final ScaleAnimation animation = new ScaleAnimation(1.0f, 2.0f, 1.0f,
//		 2.0f, Animation.RELATIVE_TO_SELF, 0.5f,
//		 Animation.RELATIVE_TO_SELF, 0.5f);
//		 animation.setDuration(ANIMATION_DURATION);
//		 animation.setFillAfter(true);
//		 startAnimation(animation);
    }

    @Override
    /**
     * React to something being dragged over the drop target.
     */
    public void onDragOver(DragSource source, int x, int y, int xOffset,
                           int yOffset, Object dragInfo) {

    }

    @Override
    /**
     * React to a dragged object leaving the area of this DeleteZone. Provide
     * the user with some visual feedback.
     */
    public void onDragExit(DragSource source, int x, int y, int xOffset,
                           int yOffset, Object dragInfo) {
        // Scale down when exit
//		 final ScaleAnimation animation = new ScaleAnimation(2.0f, 1.0f, 2.0f,
//		 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
//		 Animation.RELATIVE_TO_SELF, 0.5f);
//		 animation.setDuration(ANIMATION_DURATION);
//		 startAnimation(animation);
    }

    /**
     * Check if a drop action can occur at, or near, the requested location.
     * This may be called repeatedly during a drag, so any calls should return
     * quickly.
     *
     * @param source   DragSource where the drag started
     * @param x        X coordinate of the drop location
     * @param y        Y coordinate of the drop location
     * @param xOffset  Horizontal offset with the object being dragged where the
     *                 original touch happened
     * @param yOffset  Vertical offset with the object being dragged where the
     *                 original touch happened
     * @param dragInfo Data associated with the object being dragged
     * @return True if the drop will be accepted, false otherwise.
     */
    public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
                              int yOffset, Object dragInfo) {
        return isEnabled();
    }

    /**
     */
    // Methods

    /**
     * Return true if this DeleteZone is enabled. If it is, it means that it
     * will accept dropped views.
     *
     * @return boolean
     */

    public boolean isEnabled() {
        return mEnabled && (getVisibility() == View.VISIBLE);
    }

    /**
     * Show a string on the screen via Toast.
     *
     * @param msg String
     */

    public void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    } // end toast

} // end DeleteZone
