package code.qiao.com.tabview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import code.qiao.com.tipsview.R;

public class ItemView extends View {
    private final int sp8 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, getResources().getDisplayMetrics());

    //当前View
    private Bitmap mBitmap;

    //底部图片
    private int mColor = 0xFF3F51B5;
    private float mAlpha = 0f;
    private Bitmap mIconBitmap;
    private Rect mIconRect;
    private int bitmapWidth;
    //底部Text
    private String mText = "TabItem";
    private int mTextSize = sp8;
    private Paint mTextPaint;
    private Rect mTextBound = new Rect();

    public ItemView(Context context, String mText, int resId) {
        super(context);
        this.mText = mText;
        this.mIconBitmap = BitmapFactory.decodeResource(getResources(), resId);
        initWithAttr(null);
    }

    /**
     * 初始化自定义属性值
     *
     * @param context
     * @param attrs
     */
    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithAttr(attrs);
    }

    private void initWithAttr(AttributeSet attrs) {
        if (attrs != null) {
            // 获取设置的图标
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.TabItemView);

            Drawable drawable = a.getDrawable(R.styleable.TabItemView_item_icon);
            if (drawable != null)
                mIconBitmap = ((BitmapDrawable) drawable).getBitmap();
            mColor = a.getColor(R.styleable.TabItemView_item_color, mColor);
            mText = a.getString(R.styleable.TabItemView_text);
            mTextSize = (int) a.getDimension(R.styleable.TabItemView_text_size, sp8);

            a.recycle();
        }

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColor);
        // 得到text绘制范围
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 得到绘制icon的宽
        bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom() - mTextBound.height() - sp8 / 2);

        int left = (getMeasuredWidth() - bitmapWidth) / 2;
        int top = (getMeasuredHeight() - mTextBound.height() - bitmapWidth - sp8 / 2) / 2;
        // 设置icon的绘制范围
        mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int alpha = (int) Math.ceil((255 * mAlpha));
        drawSouceBitmap(canvas, alpha);
        drawTargetBitmap(alpha);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        drawText(canvas, 0xffcccccc, 255 - alpha);
        drawText(canvas, mColor, alpha);
    }

    private void drawSouceBitmap(Canvas canvas, int alpha) {
        canvas.drawBitmap(mIconBitmap, null, mIconRect, getPaint(255 - alpha));
    }

    private Paint getPaint(int alpha) {
        Paint mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(false);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        return mPaint;
    }

    private void drawTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);
        Paint mPaint = getPaint(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }

    private void drawText(Canvas canvas, int color, int alpha) {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(color);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mText,
                mIconRect.left + mIconRect.width() / 2 - mTextBound.width() / 2,
                mIconRect.bottom + mTextBound.height() + sp8 / 4,
                mTextPaint);
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setColor(int color) {
        mColor = color;
        invalidate();
    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public Point getIconRightTop() {
        return new Point(mIconRect.right, mIconRect.top);
    }

    public void setIconAlpha(float alpha) {
        this.mAlpha = alpha;
        invalidateView();
    }


    public void setIconColor(int color) {
        mColor = color;
        invalidateView();
    }

    public void setIcon(int resId) {
        this.mIconBitmap = BitmapFactory.decodeResource(getResources(), resId);
        if (mIconRect != null)
            invalidateView();
    }

    public void setIcon(Bitmap iconBitmap) {
        this.mIconBitmap = iconBitmap;
        if (mIconRect != null)
            invalidateView();
    }

    /**
     * 状态保存
     */
    private static final String INSTANCE_STATE = "instance_state";
    private static final String STATE_ALPHA = "state_alpha";
    private static final String STATE_Color = "state_color";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mAlpha);
        bundle.putInt(STATE_Color, mColor);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATE_ALPHA);
            mColor = bundle.getInt(STATE_Color);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
