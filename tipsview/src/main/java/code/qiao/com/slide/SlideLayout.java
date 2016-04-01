package code.qiao.com.slide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SlideLayout extends LinearLayout {
	private static final String TAG = "SlideLayout";

	protected final static int TOUCH_STATE_REST = 0;
	protected final static int TOUCH_STATE_SCROLLING = 1;
	protected boolean firstLayout;
	protected int curIndex = 0;
	protected OnSlideListener listener;
	protected int touchState = TOUCH_STATE_REST;
	protected Scroller scroller;
	protected boolean canDrag = true;
	protected int touchSlop;
	protected int minimumVelocity;
	protected int maximumVelocity;
	protected VelocityTracker velocityTracker;

	protected static final Interpolator interpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	public SlideLayout(Context context) {
		super(context);
		initialize();
	}

	public SlideLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	protected void initialize() {
		scroller = new Scroller(getContext(), interpolator);
		ViewConfiguration configuration = ViewConfiguration.get(getContext());
		touchSlop = configuration.getScaledTouchSlop() / 2;
		minimumVelocity = configuration.getScaledMinimumFlingVelocity();
		maximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		firstLayout = true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int count = getChildCount();
		int maxHeight = 0;
		int maxWidth = 0;

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				measureChild(child, widthMeasureSpec, heightMeasureSpec);
				maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
				maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
			}
		}

		if (heightMeasureSpec == 0) {
			for (int i = 0; i < count; i++) {
				final View child = getChildAt(i);
				ViewGroup.LayoutParams lp = child.getLayoutParams();
				if (child.getVisibility() != GONE && lp.height == LayoutParams.MATCH_PARENT) {
					measureChild(child, widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY));
				}
			}
		}

		maxWidth += getPaddingLeft() + getPaddingRight();
		maxHeight += getPaddingTop() + getPaddingBottom();

		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

		setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;

		int i = 0;
		for (int  len = getChildCount(); i < len; ++i) {
			View child = getChildAt(i);
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			child.layout(childLeft, 0, childLeft + childWidth, childHeight);
			childLeft += childWidth;
		}
		if (firstLayout) {
			setCurrentItem(curIndex);
			firstLayout = false;
		}
	}

	float lastMotionX, lastMotionY;
	float downMotionX, downMotionY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(!isDisallowIntercept) {
			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					getParent().requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_MOVE:
					if (touchState == TOUCH_STATE_REST) {
						boolean isInside = canSide(ev.getX() - downMotionX);
//						Log.e("OutSide.....currX:" + getScrollX(), "resut: " + isInside);
						getParent().requestDisallowInterceptTouchEvent(isInside);
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					getParent().requestDisallowInterceptTouchEvent(false);
					break;
			}
		}
		boolean handled = super.dispatchTouchEvent(ev);
//		Log.i(TAG, "dispatchTouchEvent: "+handled);
		return handled;
	}

	boolean isDisallowIntercept = false;
	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		this.isDisallowIntercept = disallowIntercept;
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}

	private boolean canSide(float distance) {
		final float currX = getScrollX();
		final int maxX = getChildAt(getChildCount() - 1).getRight() - getChildAt(0).getWidth();
		return  (distance<0 && currX <maxX)
				||  (distance>0 && currX>0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!canDrag)
			return false;
		float x = ev.getX();
		float y = ev.getY();
		switch ((ev.getAction())) {
			case MotionEvent.ACTION_DOWN:
				if (!scroller.isFinished()) {
					scroller.abortAnimation();
				}
				touchState = scroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				downMotionX = lastMotionX = x;
				downMotionY = lastMotionY = y;
				return false;
			case MotionEvent.ACTION_MOVE:
//				Log.i(TAG, "onInterceptTouchEvent: ....");
				if (touchState == TOUCH_STATE_REST) {

					int xDiff = (int) Math.abs(x - lastMotionX);
					if (xDiff > touchSlop ) {//&& isInSide()
						touchState = TOUCH_STATE_SCROLLING;
						return true;
					}
				}
			case MotionEvent.ACTION_UP:
				return false;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!canDrag)
			return false;
		float x = ev.getX();
		float y = ev.getY();
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(ev);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
//				Log.i(TAG, "onTouchEvent: .......");
				if (touchState == TOUCH_STATE_REST) {
					int xDiff = (int) Math.abs(x - lastMotionX);
					if (xDiff > touchSlop) {
						touchState = TOUCH_STATE_SCROLLING;
						return true;
					}
				}
				if (touchState == TOUCH_STATE_SCROLLING) {
					int delta = (int) (lastMotionX - x);
					lastMotionX = x;
					lastMotionY = y;

					final int scrollX = getScrollX();
					if (delta < 0) {
						if (scrollX > 0) {
							scrollBy(delta, 0);
						} else {
							scrollTo(0, 0);
							// scrollBy(delta / 3, 0);
						}
					} else if (delta > 0) {
						int maxX = getChildAt(getChildCount() - 1).getRight();
						if (maxX - scrollX - getWidth() > 0) {
							scrollBy(delta, 0);
						} else {
							scrollTo(maxX - getWidth(), 0);
							// scrollBy(delta / 3, 0);
						}
					}
					getParent().requestDisallowInterceptTouchEvent(true);
					getParent().getParent().requestDisallowInterceptTouchEvent(true);
					if (listener != null) {
						listener.onDragBegin();
					}
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (touchState == TOUCH_STATE_SCROLLING) {
					velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
					int velocityX = (int) velocityTracker.getXVelocity();
					int newIndex = curIndex;
					if (x < downMotionX) {
						int checkX = getScrollX() + getWidth();
						for (int i = 0, len = getChildCount(); i < len; i++) {
							View child = getChildAt(i);
							if (child.getLeft() <= checkX && child.getRight() >= checkX) {
								newIndex = checkX < (child.getLeft() + child.getRight()) / 2 ? i - 1 : i;
							}
						}
					} else {
						int checkX = getScrollX();
						for (int i = 0, len = getChildCount(); i < len; i++) {
							View child = getChildAt(i);
							if (child.getLeft() <= checkX && child.getRight() >= checkX) {
								newIndex = checkX < (child.getLeft() + child.getRight()) / 2 ? i : i + 1;
							}
						}
					}

					setCurrentItemInternal(newIndex, velocityX);

					if (velocityTracker != null) {
						velocityTracker.recycle();
						velocityTracker = null;
					}
					touchState = TOUCH_STATE_REST;
				}
				break;
		}
		return true;
	}



	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
	}

	public void setCanDrag(boolean canDrag) {
		this.canDrag = canDrag;
	}

	public int getCurrent() {
		return curIndex;
	}

	public void setCurrentItem(int index) {
		setCurrentItemInternal(index, null);
	}

	public void setCurrentItem(int index, Integer duration) {
		setCurrentItemInternal(index, duration);
	}

	protected void setCurrentItemInternal(int index, Integer duration) {
		int childCount = getChildCount();
		if (index < 0)
			index = 0;
		if (index >= childCount)
			index = childCount > 0 ? index - 1 : 0;

		if (listener != null)
			listener.onIndexChanged(index, curIndex);
		curIndex = index;

		View childView = getChildAt(index);
		int newX = childView.getLeft();
		int maxX = getChildAt(childCount - 1).getRight();
		if (newX + getWidth() > maxX)
			newX = maxX - getWidth();
		int delta = newX - getScrollX();
		Integer dura = duration;
		if (dura == null) {
			dura = Math.abs(delta) * 2;
			dura = dura > 700 ? 700 : dura;
		}
		scroller.startScroll(getScrollX(), getScrollY(), delta, getScrollY(), dura);
		invalidate();
	}

	public void setListener(OnSlideListener listener) {
		this.listener = listener;
	}

	public static class OnSlideListener {
		protected void onIndexChanged(int curIndex, int prevIndex) {
		}

		protected void onDragBegin() {
		}
	}
}
