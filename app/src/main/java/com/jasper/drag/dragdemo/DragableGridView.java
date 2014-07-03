package com.jasper.drag.dragdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;

import com.jasper.drag.dragdemo.controls.DeleteZone;
import com.jasper.drag.dragdemo.controls.DragController;
import com.jasper.drag.dragdemo.controls.DragListListener;
import com.jasper.drag.dragdemo.controls.DragSource;
import com.jasper.drag.dragdemo.controls.DropTarget;


/**
 * Dragable gridview.
 *
 * @author jasper
 */
public class DragableGridView extends GridView implements DragSource, DropTarget,
        View.OnLongClickListener, View.OnClickListener {

    private DragController mDragController = null;
    private DragListListener mListener = null;
    boolean mDragging = false;

    private View mDragView = null;

    private static final int ANIMATION_DURATION = 200;

    /**
     * constructor.
     *
     * @param context context
     */
    public DragableGridView(Context context) {
        super(context);
    }

    /**
     * constructor.
     *
     * @param context context
     * @param attrs   attrs
     */
    public DragableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setDragController(DragController dragController) {
        if (dragController == null) {
            return;
        }
        mDragController = dragController;
    }


    /**
     * Sets the listener which will be notified for drag events and clicks on
     * items in the gridview.
     *
     * @param l DragableGridViewListener
     */
    public void setViewListener(DragListListener l) {
        mListener = l;
    }


    /**
     */
    // The following are the implementation for item events
    // ---------------------------------------------------------------------------
    // //

    /**
     * Long click on gridview item.
     *
     * @param v view to click
     * @return true if ok
     */
    @Override
    public boolean onLongClick(View v) {
        if (mDragController != null) {
            if (!v.isInTouchMode()) {
                return false;
            }

            mDragView = v;

            mDragController.startDrag(mDragView, this, v,
                    DragController.DRAG_ACTION_MOVE);
            return true;
        }

        // If we get here, return false to indicate that we have not taken care
        // of the event.
        return false;
    }

    /**
     * click on gridview item.
     *
     * @param v view to click
     */
    @Override
    public void onClick(View v) {
        if (mListener != null && v != null) {
            mListener.onItemClick(v, getPositionForView(v), v.getId());
        }
    }

    @Override
    public void onDrop(DragSource source, int x, int y, int xOffset,
                       int yOffset, Object dragInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDragEnter(DragSource source, int x, int y, int xOffset,
                            int yOffset, Object dragInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDragOver(DragSource source, int x, int y, int xOffset,
                           int yOffset, Object dragInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDragExit(DragSource source, int x, int y, int xOffset,
                           int yOffset, Object dragInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
                              int yOffset, Object dragInfo) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void onDropCompleted(View target, boolean success) {

        if (mDragView != null) {
            if (target instanceof DeleteZone) {
                if (mListener != null) {
                    mListener.onItemDragToDelete(mDragView, DragListListener.LIST_TYPE_GRIDVIEW);
                }
            } else {
                int positionOne = getPositionForView(mDragView);
                int positionTwo = getPositionForView(target);

            }
        }

    }
    /**
     * delete a item.
     * @param pos position
     */
//	public void deleteItem(int pos) {
//		prepareAnimation(getViewInPosition(pos));
//		Message msg = Message.obtain(mHandler, MSG_REMOVE_ITEM, pos, 0);
//		mHandler.sendMessageDelayed(msg, ANIMATION_DURATION);
//	}

    /**
     * start animation.
     *
     * @param v the view from which we start animation
     */
    public void prepareAnimation(View v) {
        // TODO Auto-generated method stub
        if (v == null) {
            return;
        }
        int index = getPositionForView(v);
        View currentView = v;
        index++;
        View nextView = getViewInPosition(index);
        while (nextView != null) {

            int[] move = new int[2];
            currentView.getLocationInWindow(move);
            int[] des = new int[2];
            nextView.getLocationInWindow(des);

            TranslateAnimation aTrans = null;
            aTrans = new TranslateAnimation(0, move[0] - des[0], 0, move[1] - des[1]);
            aTrans.setDuration(ANIMATION_DURATION);
            aTrans.setFillAfter(true);
            if (nextView.getVisibility() != View.INVISIBLE) {
                nextView.clearAnimation();
                nextView.startAnimation(aTrans);
            }
            currentView = getViewInPosition(index);
            index++;
            nextView = getViewInPosition(index);
        }


    }

    /**
     * start animation.
     *
     * @param pos the position
     */
    public void prepareAnimation(int pos) {
        // TODO Auto-generated method stub
        View v = getViewInPosition(pos);
        prepareAnimation(v);
    }

    /**
     * clear animation.
     *
     * @param pos the position where start the animation
     */
    public void clearAllAnimation(int pos) {
        int dragViewPos = pos;
        //clear animation
        for (int i = dragViewPos + 1; i <= getLastVisiblePosition(); i++) {
            if (getViewInPosition(i) != null) {
                getViewInPosition(i).clearAnimation();
            }
        }
//		ada
    }

    /**
     * gridview inner handler.
     */
//	public static final class DragableGridViewHandler extends Handler {
//		private final WeakReference<DragableGridView> mView;
//
//		/**
//		 * constructor.
//		 * @param v gridview
//		 */
//		public DragableGridViewHandler(DragableGridView v) {
//			mView = new WeakReference<DragableGridView>(v);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			if (mView == null) {
//				return;
//			}
//			final DragableGridView view = mView.get();
//			final MyYueduGridAdapter adapter = (HeaderGridViewAdatper) view.getAdapter();
//			switch (msg.what) {
//			case MSG_REMOVE_ITEM:
//				int dragViewPos = msg.arg1;
//				//clear animation
//				for (int i = dragViewPos + 1; i <= view.getLastVisiblePosition(); i++) {
//					if (view.getViewInPosition(i) != null) {
//						view.getViewInPosition(i).clearAnimation();
//					}
//				}			
//				adapter.remove(dragViewPos);
//				adapter.notifyDataSetChanged();
//				break;
//			case MSG_CHANGE_ITEM_STATE:
//				
//				break;
//			default:
//				break;
//			}
//		}
//	};
    private View getViewInPosition(int position) {
        int firstPosition = this.getFirstVisiblePosition();
        int lastPosition = this.getLastVisiblePosition();

        if ((position < firstPosition) || (position > lastPosition)) {
            return null;
        }

        return this.getChildAt(position - firstPosition);
    }


}
