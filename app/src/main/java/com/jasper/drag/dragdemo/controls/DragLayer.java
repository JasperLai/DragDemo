package com.jasper.drag.dragdemo.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;

import android.graphics.Rect;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

/**
 * A ViewGroup that coordinated dragging across its dscendants.
 */
public class DragLayer extends RelativeLayout implements DragController {

	private static final int VIBRATE_DURATION = 35;

	private static final int ANIMATION_SCALE_UP_DURATION = 110;

	private static final boolean PROFILE_DRAWING_DURING_DRAG = false;

	private boolean mDragging = false;

	private float mLastMotionX;

	private float mLastMotionY;

	/**
	 * The bitmap that is currently being dragged.
	 */
	private Bitmap mDragBitmap = null;

	private View mOriginator;

	private int mBitmapOffsetX;

	private int mBitmapOffsetY;

	/**
	 * X offset from where we touched on the cell to its upper-left corner.
	 */
	private float mTouchOffsetX;

	/**
	 * Y offset from where we touched on the cell to its upper-left corner.
	 */
	private float mTouchOffsetY;

	/**
	 * Utility rectangle.
	 */
	private Rect mDragRect = new Rect();

	/**
	 * Where the drag originated.
	 */
	private DragSource mDragSource;

	/**
	 * The data associated with the object being dragged.
	 */
	private Object mDragInfo;

	private float[] mDragCenter = new float[2];

	private final Rect mRect = new Rect();

	private final int[] mDropCoordinates = new int[2];

	private final Vibrator mVibrator;

	private DragListener mListener;

	// private DragScroller mDragScroller;

	private View mIgnoredDropTarget;

	private boolean mShouldDrop = true;

	private DropTarget mLastDropTarget;

	private Paint mDragPaint;

	private static final int ANIMATION_STATE_STARTING = 1;

	private static final int ANIMATION_STATE_RUNNING = 2;

	private static final int ANIMATION_STATE_DONE = 3;

	private static final int ANIMATION_TYPE_SCALE = 1;

	private float mAnimationFrom;

	private float mAnimationTo;

	private int mAnimationDuration;

	private long mAnimationStartTime;

	private int mAnimationType;

	private int mAnimationState = ANIMATION_STATE_DONE;

	private InputMethodManager mInputMethodManager;

    private  Matrix mScaleMatrix = new Matrix();

	private static final float DRAG_SCALE = 40;

	/**
	 * Used to create a new DragLayer from XML.
	 * 
	 * @param context
	 *            The application's context.
	 * @param attrs
	 *            The attribtues set containing the Workspace's customization
	 *            values.
	 */
	public DragLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		mVibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public void startDrag(View v, DragSource source, Object dragInfo,
			int dragAction) {
         //嵌套上层viewpager处理滑动手势冲突的时候使用
		//getParent().requestDisallowInterceptTouchEvent(true);

		if (PROFILE_DRAWING_DURING_DRAG) {
			android.os.Debug.startMethodTracing("Launcher");
		}

		// Hide soft keyboard, if visible
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);

		if (mListener != null) {
			mListener.onDragStart(v, source, dragInfo, dragAction);
		}

		Rect r = mDragRect;
		r.set(v.getScrollX(), v.getScrollY(), 0, 0);
		offsetDescendantRectToMyCoords(v, r);

		Bitmap mDragBitmapSmallCache = getViewBitmap(v);

		mTouchOffsetX = mLastMotionX - r.left;
		mTouchOffsetY = mLastMotionY - r.top;

		int width = mDragBitmapSmallCache.getWidth();
		int height = mDragBitmapSmallCache.getHeight();

		Matrix scale = new Matrix();
		float scaleFactor = v.getWidth();
		scaleFactor = (scaleFactor + DRAG_SCALE) / scaleFactor;
		scale.setScale(scaleFactor, scaleFactor);

		mAnimationTo = 1.0f;
		mAnimationFrom = 1.0f / scaleFactor;
		mAnimationDuration = ANIMATION_SCALE_UP_DURATION;
		mAnimationState = ANIMATION_STATE_STARTING;
		mAnimationType = ANIMATION_TYPE_SCALE;

		mDragBitmap = Bitmap.createBitmap(mDragBitmapSmallCache, 0, 0, width,
				height, scale, true);
		final Bitmap dragBitmap = mDragBitmap;
		mBitmapOffsetX = (dragBitmap.getWidth() - width) / 2;
		mBitmapOffsetY = (dragBitmap.getHeight() - height) / 2;

		if (dragAction == DRAG_ACTION_MOVE) {
			v.setVisibility(INVISIBLE);
		}

		mDragPaint = null;
		mDragging = true;
		mOriginator = v;
		mDragSource = source;
		mDragInfo = dragInfo;
		mShouldDrop = true;
		if (mVibrator != null) {
			mVibrator.vibrate(VIBRATE_DURATION);
		}

		invalidate();
	}

	// /**
	// * Draw the view into a bitmap.
	// */
	private Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);

		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);

		// Reset the drawing cache background color to fully transparent
		// for the duration of this operation
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			Log.e("", "failed getViewBitmap(" + v + ")", new RuntimeException());
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);

		return bitmap;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return mDragging || super.dispatchKeyEvent(event);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			SystemClock.uptimeMillis();
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

			super.dispatchDraw(canvas);

			if (mDragging && mDragBitmap != null) {

				if (mAnimationState == ANIMATION_STATE_STARTING) {

					mAnimationStartTime = SystemClock.uptimeMillis();
					mAnimationState = ANIMATION_STATE_RUNNING;
				}
				if (!mIsInDeleteZone) {

					if (mAnimationState == ANIMATION_STATE_RUNNING) {
						float normalized = (float) (SystemClock.uptimeMillis() - mAnimationStartTime)
								/ mAnimationDuration;
						if (normalized >= 1.0f) {
							mAnimationState = ANIMATION_STATE_DONE;
						}
						normalized = Math.min(normalized, 1.0f);
						final float value = mAnimationFrom
								+ (mAnimationTo - mAnimationFrom) * normalized;
						switch (mAnimationType) {
						case ANIMATION_TYPE_SCALE:
							final Bitmap dragBitmap = mDragBitmap;
							canvas.save();
							canvas.translate(this.getScrollX() + mLastMotionX
									- mDragBitmap.getWidth() / 2,
									this.getScrollY() + mLastMotionY
											- mDragBitmap.getHeight() / 2);
							canvas.translate(
									(dragBitmap.getWidth() * (1.0f - value)) / 2,
									(dragBitmap.getHeight() * (1.0f - value)) / 2);
							canvas.scale(value, value);
							canvas.drawBitmap(dragBitmap, 0.0f, 0.0f,
									mDragPaint);
							canvas.restore();
							break;
						default:
							break;
						}
					} else {
						canvas.drawBitmap(
								mDragBitmap,
								this.getScrollX() + mLastMotionX
										- mDragBitmap.getWidth() / 2,
								this.getScrollY() + mLastMotionY
										- mDragBitmap.getHeight() / 2,
								mDragPaint);
					}
				} else {
					if (mAnimationState == ANIMATION_STATE_RUNNING) {
						float normalized = (float) (SystemClock.uptimeMillis() - mAnimationStartTime)
								/ mAnimationDuration;
						if (normalized >= 1.0f) {
							mAnimationState = ANIMATION_STATE_DONE;
						}
						normalized = Math.min(normalized, 1.0f);
						final float value = mAnimationTo
								+ (mAnimationFrom - mAnimationTo) * normalized;
						switch (mAnimationType) {
						case ANIMATION_TYPE_SCALE:
							final Bitmap dragBitmap = mDragBitmap;
							canvas.save();
							canvas.translate(this.getScrollX() + mLastMotionX
									- mDragBitmap.getWidth() / 2,
									this.getScrollY() + mLastMotionY
											- mDragBitmap.getHeight() / 2);
							canvas.translate(
									(dragBitmap.getWidth() * (1.0f - value)) / 2,
									(dragBitmap.getHeight() * (1.0f - value)) / 2);
							canvas.scale(value, value);
							canvas.drawBitmap(dragBitmap, 0.0f, 0.0f,
									mDragPaint);
							canvas.restore();
							break;
						default:
							break;
						}
					} else {
						mScaleMatrix.setScale(0.5f, 0.5f,
								mDragBitmap.getWidth() / 2,
								mDragBitmap.getHeight() / 2);
						mScaleMatrix.postTranslate(
								this.getScrollX() + mLastMotionX
										- mDragBitmap.getWidth() / 2,
								this.getScrollY() + mLastMotionY
										- mDragBitmap.getHeight() / 2);
						canvas.drawBitmap(mDragBitmap, mScaleMatrix, mDragPaint);
					}
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	private void endDrag() {
        //配合拦截viewpager事件
	//	getParent().requestDisallowInterceptTouchEvent(false);
		if (mDragging) {
			mDragging = false;
			if (mDragBitmap != null) {
				mDragBitmap.recycle();
			}
            //判断是否隐藏在删除区域
//			if (!mIsInDeleteZone) {
//				revealDragOriginator();
//			}
            revealDragOriginator();
			if (mListener != null) {
				mListener.onDragEnd();
			}
			mIsInDeleteZone = false;
		}

		invalidate();
	}

	public void revealDragOriginator(){
		if (mOriginator != null) {
			mOriginator.setVisibility(View.VISIBLE);
		}
	}
	/**
	 * cancel.
	 */
	public void cancelDrag() {
		if (mDragging) {
			if (mLastDropTarget != null) {
				mLastDropTarget.onDragExit(mDragSource, 0, 0,
						(int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo);
			}
			endDrag();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_DOWN:
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;
			mLastDropTarget = null;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			if (mShouldDrop && drop(x, y)) {
				mShouldDrop = false;
			}
			endDrag();
			break;
		default:
			break;
		}

		return mDragging;
	}

	private boolean mIsInDeleteZone = false;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mDragging) {
			return false;
		}
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;

			break;
		case MotionEvent.ACTION_MOVE:

			final int scrollX = this.getScrollX();
			final int scrollY = this.getScrollY();

			final float touchX = mTouchOffsetX;
			final float touchY = mTouchOffsetY;

			final int offsetX = mBitmapOffsetX;
			final int offsetY = mBitmapOffsetY;
			int left = (int) (scrollX + mLastMotionX - mDragBitmap.getWidth() / 2);
			int top = (int) (scrollY + mLastMotionY - mDragBitmap.getHeight() / 2);

			final Bitmap dragBitmap = mDragBitmap;
			final int width = dragBitmap.getWidth();
			final int height = dragBitmap.getHeight();
			final Rect rect = mRect;
			rect.set(left - 1, top - 1, left + width + 1, top + height + 1);

			mLastMotionX = x;
			mLastMotionY = y;

			left = (int) (scrollX + x - touchX - offsetX);
			top = (int) (scrollY + y - touchY - offsetY);

			rect.union(left - 1, top - 1, left + width + 1, top + height + 1);

			mDragCenter[0] = rect.centerX();
			mDragCenter[1] = rect.centerY();

			invalidate(rect);

			final int[] coordinates = mDropCoordinates;

			DropTarget dropTarget = findDropTarget((int) x, (int) y,
					coordinates);

			if (dropTarget != null) {
				if (mLastDropTarget == dropTarget) {
					dropTarget.onDragOver(mDragSource, coordinates[0],
							coordinates[1], (int) mTouchOffsetX,
							(int) mTouchOffsetY, mDragInfo);
				} else {
					if (mLastDropTarget != null) {
						mLastDropTarget.onDragExit(mDragSource, coordinates[0],
								coordinates[1], (int) mTouchOffsetX,
								(int) mTouchOffsetY, mDragInfo);
					}
					if (dropTarget instanceof DeleteZone) {
						Log.d("dragLayer", "go into deletezone");
						mAnimationState = ANIMATION_STATE_STARTING;
						mIsInDeleteZone = true;

					}
					dropTarget.onDragEnter(mDragSource, coordinates[0],
							coordinates[1], (int) mTouchOffsetX,
							(int) mTouchOffsetY, mDragInfo);
				}
			} else {
				if (mLastDropTarget != null) {
					if (mLastDropTarget instanceof DeleteZone) {
						// scale up
						Log.d("dragLayer", "go out deletezone");
						mIsInDeleteZone = false;
					}
					mLastDropTarget.onDragExit(mDragSource, coordinates[0],
							coordinates[1], (int) mTouchOffsetX,
							(int) mTouchOffsetY, mDragInfo);
				}
			}

			mLastDropTarget = dropTarget;

			break;
		case MotionEvent.ACTION_UP:

			if (mShouldDrop) {
				drop(x, y);
				mShouldDrop = false;
			}
			endDrag();
			break;
		case MotionEvent.ACTION_CANCEL:
			endDrag();
		default:
			break;
		}

		return true;
	}

	private boolean drop(float x, float y) {
		invalidate();
		final int[] coordinates = mDropCoordinates;
		DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);

		if (dropTarget != null) {
			dropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1],
					(int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo);
			if (dropTarget.acceptDrop(mDragSource, coordinates[0],
					coordinates[1], (int) mTouchOffsetX, (int) mTouchOffsetY,
					mDragInfo)) {
				dropTarget.onDrop(mDragSource, coordinates[0], coordinates[1],
						(int) mTouchOffsetX, (int) mTouchOffsetY, mDragInfo);
				mDragSource.onDropCompleted((View) dropTarget, true);
				return true;
			} else {
				mDragSource.onDropCompleted((View) dropTarget, false);
				return true;
			}
		}
		return false;
	}

	DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
		return findDropTarget(this, x, y, dropCoordinates);
	}

	private DropTarget findDropTarget(ViewGroup container, int x, int y,
			int[] dropCoordinates) {
		final Rect r = mDragRect;
		final int count = container.getChildCount();
		final int scrolledX = x + container.getScrollX();
		final int scrolledY = y + container.getScrollY();
		final View ignoredDropTarget = mIgnoredDropTarget;

		for (int i = count - 1; i >= 0; i--) {
			final View child = container.getChildAt(i);
			if (child.getVisibility() == VISIBLE && child != ignoredDropTarget) {
				child.getHitRect(r);
				if (r.contains(scrolledX, scrolledY)) {
					DropTarget target = null;
					if (child instanceof ViewGroup) {
						// x = scrolledX - child.getLeft();
						// y = scrolledY - child.getTop();
						target = findDropTarget((ViewGroup) child, scrolledX
								- child.getLeft(), scrolledY - child.getTop(),
								dropCoordinates);
					}
					if (target == null) {
						if (child instanceof DropTarget) {
							// Only consider this child if they will accept
							DropTarget childTarget = (DropTarget) child;
							if (childTarget.acceptDrop(mDragSource, x, y, 0, 0,
									mDragInfo)) {
								dropCoordinates[0] = x;
								dropCoordinates[1] = y;
								return (DropTarget) child;
							} else {
								return null;
							}
						}
					} else {
						return target;
					}
				}
			}
		}

		return null;
	}

	@Override
	public void setDragListener(DragListener l) {
		mListener = l;
	}

	@Override
	public void removeDragListener(DragListener l) {
		mListener = null;
	}

	/**
	 * Specifies the view that must be ignored when looking for a drop target.
	 * 
	 * @param view
	 *            The view that will not be taken into account while looking for
	 *            a drop target.
	 */
	void setIgnoredDropTarget(View view) {
		mIgnoredDropTarget = view;
	}

}
